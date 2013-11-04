package com.datayes.invest.pms.logic.process.sod

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.{ CarryingValueHistDao, CashPositionDao }
import com.datayes.invest.pms.dao.account.{ PositionHistDao, SecurityPositionDao, CashTransactionDao }
import com.datayes.invest.pms.dao.security.{ FutureDao, SecurityDao, PriceVolumeDao, FuturePriceVolumeDao }
import com.datayes.invest.pms.entity.account.{Account, CashTransaction, SecurityPosition}
import com.datayes.invest.pms.util.DefaultValues
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import javax.inject.Inject
import scala.collection.JavaConversions._
import scala.math.BigDecimal._
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.dbtype.CashTransactionType
import com.datayes.invest.pms.dbtype.CashTransactionMethod
import com.datayes.invest.pms.dbtype.CashTransactionReason
import com.datayes.invest.pms.logic.process.Processor

class MarginProcessor extends Processor with Logging {

  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null

  @Inject
  private var cashPositionDao: CashPositionDao = null
  
  @Inject
  private var cashTransactionDao: CashTransactionDao = null
  
  @Inject
  private var futureDao: FutureDao = null

  @Inject
  private var futurePriceVolumeDao: FuturePriceVolumeDao = null

  @Inject
  private var marketDataService: MarketDataService = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var priceVolumeDao: PriceVolumeDao = null

  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityPostionDao: SecurityPositionDao = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    logger.debug("Start Margin process for account #{} on {} (before market open)", account.getId, asOfDate)
    refreshAccount(account.getId, asOfDate)
    logger.info("Margin refreshed for account #{}", account.getId)
  }

  private def refreshAccount(accountId: Long, asOfDate: LocalDate): Unit = {
    val securityPositionList = securityPostionDao.findByAccountId(accountId)
    val futurePositionList = securityPositionList.filter(p => filterFuture(p))
    futurePositionList.map(p => refreshMargin(accountId, p, asOfDate))
  }

  private def filterFuture(securityPosition: SecurityPosition): Boolean = {
    val security = securityDao.findById(securityPosition.getSecurityId)
    security match {
      case future: Future => true
      case _ => false
    }
  }

  private def refreshMargin(accountId: Long, securityPosition: SecurityPosition, asOfDate: LocalDate): Unit = {
    val settlementPrice = getSettlementPrice(securityPosition.getSecurityId, asOfDate.minusDays(1))
    val quantity = getQuantity(securityPosition.getId, asOfDate)
    val totalAmount = settlementPrice * quantity * DefaultValues.STOCK_INDEX_FUTURE_PRICE_RATIO
    
    if (quantity == 0) {
      return
    }
    val carryingValue = updateCarryingValue(securityPosition.getId, totalAmount, asOfDate)
    
    var marginChange: BigDecimal = 0
    if (securityPosition.getLedgerId == LedgerType.FUTURE_LONG.getDbValue) {
      marginChange = totalAmount - carryingValue
    } else {
      marginChange = carryingValue - totalAmount
    }

    if (marginChange != 0) {
      updateMarginPosition(accountId, marginChange, asOfDate)
    }
  }

  private def getSettlementPrice(securityId: Long, asOfDate: LocalDate): BigDecimal = {
    val md = marketDataService.getMarketData(securityId, asOfDate)
    if (md == null || md.getPrice() == null) {
      logger.error("Unable to find market data for security #{} on {}", securityId, asOfDate)
      BigDecimalConstants.ZERO
    } else {
      md.getPrice()
    }
  }

  private def getQuantity(positionId: Long, asOfDate: LocalDate): BigDecimal = {
    val positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)
    if (positionHist == null) {
      logger.error("Failed to find position hist for position #{} on {}", positionId, asOfDate)
      0
    } else {
      positionHist.getQuantity
    }
  }

  private def updateCarryingValue(positionId: Long, totalAmount: BigDecimal, asOfDate: LocalDate): BigDecimal = {
    val pk = new CarryingValueHist.PK(positionId, DefaultValues.CARRYING_VALUE_TYPE, asOfDate)
    val carryingValueHist = carryingValueHistDao.findById(pk)
    if (carryingValueHist == null) {
      logger.error("Failed to find carrying value hist for position #{} on {}", positionId, asOfDate)
      0
    } else {
      val carryingValue = carryingValueHist.getValueAmount
      if (carryingValue != totalAmount) {
        carryingValueHist.setValueAmount(totalAmount.bigDecimal)
        carryingValueHistDao.update(carryingValueHist)
      }
      carryingValue
    }
  }

  private def updateMarginPosition(accountId: Long, marginChange: BigDecimal, asOfDate: LocalDate): Unit = {
    val marginPosition = cashPositionDao.findByAccountIdLedgerId(accountId, LedgerType.MARGIN.getDbValue)
    if (marginPosition == null) {
      logger.error("Failed to find margin position for account #{} on {}", accountId, asOfDate)
    } else {
      val marginPositionHist = positionHistDao.findByPositionIdAsOfDate(marginPosition.getId, asOfDate)
      if (marginPositionHist == null) {
        logger.error("Failed to find position hist for position #{} on {}", marginPosition.getId, asOfDate)
      } else {
        var marginAmount = marginPositionHist.getQuantity
        marginAmount = (marginAmount + marginChange).bigDecimal
        marginPositionHist.setQuantity(marginAmount)
        positionHistDao.save(marginPositionHist)
        saveTransaction(accountId, marginChange, asOfDate)
      }
    }
  }

  private def saveTransaction(accountId: Long, marginChange: BigDecimal, asOfDate: LocalDate): Unit = {
    val cashTransaction = createTransaction(accountId, asOfDate)
    cashTransaction.setAmount(marginChange.bigDecimal)
    cashTransactionDao.save(cashTransaction)
  }

  private def createTransaction(accountId: Long, asOfDate: LocalDate): CashTransaction = {

    val cashTransaction = new CashTransaction(accountId, TransactionSource.PMS.getDbValue,
      TransactionClass.CASH.getDbValue(), CashTransactionType.DEBIT.getDbValue(), CashTransactionMethod.TRANSFER.getDbValue(),
      CashTransactionReason.CAPMOVE.getDbValue())

    cashTransaction.setOrderId(null)
    cashTransaction.setSourceTransactionId("MARGIN PROCESS")    // TODO fix this
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
}