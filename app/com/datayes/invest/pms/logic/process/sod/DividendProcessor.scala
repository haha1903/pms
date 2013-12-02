package com.datayes.invest.pms.logic.process.sod

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.{ CashPositionDao, CashTransactionDao, PositionHistDao }
import com.datayes.invest.pms.dao.account.{ SecurityPositionDao, SecurityTransactionDao }
import com.datayes.invest.pms.dao.security.EquityDividendDao
import com.datayes.invest.pms.entity.security.EquityDividend
import com.datayes.invest.pms.util.DefaultValues
import org.joda.time.{LocalTime, LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.collection.JavaConversions._
import scala.math.BigDecimal._
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.account.CashTransaction
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.dbtype.CashTransactionType
import com.datayes.invest.pms.dbtype.CashTransactionMethod
import com.datayes.invest.pms.dbtype.CashTransactionReason
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.dbtype.TradeSide


class DividendProcessor extends Logging {

  @Inject
  private var cashPositionDao: CashPositionDao = null

  @Inject
  private var cashTransactionDao: CashTransactionDao = null

  @Inject
  private var equityDividendDao: EquityDividendDao = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var securityPositionDao: SecurityPositionDao = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    logger.debug("Start dividend process for account #{} on {} (before market open)", account.getId, asOfDate)
    dividend(account.getId, asOfDate)
    logger.info("Dividend done for account #{}", account.getId)
  }

  private def dividend(accountId: Long, asOfDate: LocalDate): Unit = {
    val positionList = securityPositionDao.findByAccountId(accountId)
    val dividendList = loadDividends(positionList, asOfDate)
    for (position <- positionList) {
      val dividend = dividendList.get(position.getSecurityId)
      dividend match {
        case Some(d) => 
          //logger.debug("dividend accountId = {}, positionId = {}, info = {}", accountId, position.getId, dividend)
          doDividend(accountId, asOfDate, d, position.getId)
        case None =>
      }
    }
  }
  
  private def loadDividends(positionList: Seq[SecurityPosition], asOfDate: LocalDate): Map[Long, EquityDividend] = {
    val ids = positionList.map(_.getSecurityId)
    if (ids.size == 0) {
      return null
    }
    val dividend = equityDividendDao.findBySecurityIdsExDiviDate(ids, asOfDate)
    val map = dividend.map { p => (p.getSecurityId.toLong -> p) } .toMap
    map
  }

  private def doDividend(accountId: Long, asOfDate: LocalDate, dividend: EquityDividend, positionId: Long): Unit = {
    if (dividend.getActualCashDivirmb != null && dividend.getActualCashDivirmb > 0) {
      updateCashAmount(accountId, asOfDate, dividend.getActualCashDivirmb, positionId)
    }

    updateEquityQuantity(accountId, dividend, asOfDate, positionId)
  }

  private def updateCashAmount(accountId: Long, asOfDate: LocalDate, cashDividends: BigDecimal, positionId: Long): Unit = {
    val cashPositionHist = getCashPositionHist(accountId, LedgerType.CASH, asOfDate)
    if (cashPositionHist != null) {
      var cashAmountAvailable = cashPositionHist.getQuantity
      val positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)
      if (positionHist == null) {
        logger.error("Failed to get position hist for position #{} on asOfDate", positionId)
        return
      }
      val quantity = positionHist.getQuantity
      val cashChange = cashDividends * quantity / 10
      cashAmountAvailable = (cashAmountAvailable + cashChange).bigDecimal
      cashPositionHist.setQuantity(cashAmountAvailable)
      positionHistDao.update(cashPositionHist)
      saveCashTransaction(accountId, asOfDate, cashChange)
    }
  }

  private def saveCashTransaction(accountId: Long, asOfDate: LocalDate, cashDividends: BigDecimal): Unit = {
    val cashTransaction = createCashTransaction(accountId, asOfDate)
    cashTransaction.setAmount(cashDividends.bigDecimal)
    cashTransactionDao.save(cashTransaction)
  }

  private def createCashTransaction(accountId: Long, asOfDate: LocalDate): CashTransaction = {
    val cashTransaction = new CashTransaction(accountId, TransactionSource.PMS.getDbValue,
      TransactionClass.CASH.getDbValue(), CashTransactionType.DEBIT.getDbValue(), CashTransactionMethod.TRANSFER.getDbValue(),
      CashTransactionReason.CAPMOVE.getDbValue())

    cashTransaction.setOrderId(null)
    cashTransaction.setSourceTransactionId("SETTLEMENT")    // TODO what is this source transaction id?
    cashTransaction.setSourceTransactionDate(new LocalDateTime(asOfDate.toDateTimeAtCurrentTime()))
    cashTransaction.setTransactionStatus(null)
    cashTransaction.setTransactionStatus(null)
    cashTransaction.setStatusChangeDate(null)
    cashTransaction.setPartyId(null)
    cashTransaction.setIntAcctId(accountId)
    cashTransaction.setExtAcctCode(null)
    cashTransaction.setCurrency2Code(DefaultValues.CURRENCY_CODE)
    cashTransaction.setFxRate1(null)

    cashTransaction
  }

  private def getCashPositionHist(accountId: Long, ledgerType: LedgerType, asOfDate: LocalDate): PositionHist = {
    val ledgerTypeId = ledgerType.getDbValue
    val position = cashPositionDao.findByAccountIdLedgerId(accountId, ledgerTypeId)
    if (position == null) {
      logger.error("account id = #{}, {} can not be null", accountId, ledgerType)
      return null
    }
    val positionHist = positionHistDao.findByPositionIdAsOfDate(position.getId, asOfDate)
    positionHist
  }

  private def updateEquityQuantity(accountId: Long, dividend: EquityDividend, asOfDate: LocalDate, positionId: Long): Unit = {
    val positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)
    if (positionHist == null) {
      logger.error("Failed to get position hist for position #{} on asOfDate", positionId)
      return
    }
    var quantity = positionHist.getQuantity
    val bonusShareRatio = if (dividend.getBonusShareRatio != null) {
      dividend.getBonusShareRatio
    } else {
      BigDecimal(0.0)
    }

    val tranAddShareRatio = if (dividend.getTranAddShareRatio != null) {
      dividend.getTranAddShareRatio
    } else {
      BigDecimal(0.0)
    }

    val quantityChange = quantity * (bonusShareRatio / 10 + tranAddShareRatio / 10)
    if (quantityChange == 0 ) {
      return
    }
    logger.debug("quantity = {}, bonusShareRatio = {}, tranAddShareRatio = {}", quantity, bonusShareRatio, tranAddShareRatio)
    quantity = (quantity + quantityChange).bigDecimal.setScale(0, java.math.RoundingMode.DOWN)
    positionHist.setQuantity(quantity)
    positionHistDao.update(positionHist)
    saveSecurityTransaction(accountId, asOfDate, dividend.getSecurityId, quantityChange)
  }

  private def saveSecurityTransaction(accountId: Long, asOfDate: LocalDate, securityId: Long, quantity: BigDecimal): Unit = {
    val securityTransaction = createSecurityTransaction(accountId, asOfDate, securityId, quantity)
    securityTransactionDao.save(securityTransaction)
  }

  private def createSecurityTransaction(accountId: Long, asOfDate: LocalDate, securityId: Long, quantity: BigDecimal): SecurityTransaction = {
    val securityTransaction = new SecurityTransaction(accountId, TransactionSource.PMS.getDbValue,
      TransactionClass.TRADE.getDbValue(), securityId, TradeSide.BUY.getDbValue())

    securityTransaction.setOrderId(null)
    securityTransaction.setSourceTransactionId(DefaultValues.PMS_SOURCE_TRANSACTION_ID)
    securityTransaction.setSourceTransactionDate(asOfDate.toLocalDateTime(LocalTime.MIDNIGHT))
    securityTransaction.setTransactionStatus(null)
    securityTransaction.setStatusChangeDate(null)
    securityTransaction.setAmount(quantity.bigDecimal)
    securityTransaction.setAvgPrice(BigDecimal(0).bigDecimal)
    securityTransaction.setSettleCurrCode(null)
    securityTransaction.setTraderId(null)
    securityTransaction.setBrokerId(null)
    securityTransaction.setFxRate2(null)
    securityTransaction.setCommissions(BigDecimal(0).bigDecimal)
    securityTransaction.setFees(BigDecimal(0).bigDecimal)
    securityTransaction.setExecutionDate(new LocalDateTime(asOfDate.toDateTimeAtCurrentTime))
    securityTransaction.setSettlementDate(asOfDate)
    securityTransaction.setTransactionReason(null)

    securityTransaction
  }
}