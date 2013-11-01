package com.datayes.invest.pms.logic.transaction.impl


import javax.inject.Inject
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import com.datayes.invest.pms.logic.transaction.TransactionLogic
import com.datayes.invest.pms.dao.account.SecurityTransactionDao
import com.datayes.invest.pms.dao.account.SecurityPositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.CarryingValueHistDao
import com.datayes.invest.pms.dao.account.CashPositionDao
import com.datayes.invest.pms.dao.account.FeeDao
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.logic.transaction.BusinessException
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.service.fee.FeeService


abstract class TransactionLogicBase extends TransactionLogic {
  
  @Inject
  protected var carryingValueHistDao: CarryingValueHistDao = null
  
  @Inject
  protected var cashPositionDao: CashPositionDao = null
  
  @Inject
  protected var feeService: FeeService = null
  
  @Inject
  protected var positionHistDao: PositionHistDao = null
  
  @Inject
  protected var securityTransactionDao: SecurityTransactionDao = null

  @Inject
  protected var securityPositionDao: SecurityPositionDao = null


  
  protected def getCommission(accountId: Long, cashChange: BigDecimal, side: TradeSide,
    rateType: RateType, securityId: Long, asOfDate: LocalDate): BigDecimal = {

    val commissionPosition = cashPositionDao.findByAccountIdLedgerId(
      accountId, LedgerType.COMMISSION.getDbValue)
    if (commissionPosition == null) {
      throw new BusinessException("Failed to get commissionPosition for account#"+ accountId)
    }
    val commissionPositionHist = findPositionHist(commissionPosition.getId, asOfDate)
    val commissions = commissionPositionHist.getQuantity
    
    val commissionRate = calculateRate(accountId, rateType, side, securityId)
    val commissionPayable: BigDecimal = cashChange * commissionRate

    commissionPositionHist.setQuantity((commissions + commissionPayable).bigDecimal)
    positionHistDao.update(commissionPositionHist)

    commissionPayable
  }

  protected def calculateRate(accountId: Long, rateType: RateType, side: TradeSide, securityId: Long): BigDecimal = {
//    FeeCache.get(accountId) match {
//      case Some(helper) => helper.getFee(rateType, side, securityId)
//      case _ => BigDecimal.valueOf(0)
//    }
    feeService.getRate(accountId, rateType, side, securityId)
  }

  protected def createSecurityPosition(accountID: Long, securityID: Long, ledgerID: Long,
    exchangeCode: String, openDate: LocalDateTime): SecurityPosition = {

    val securityPosition = new SecurityPosition(accountID, PositionClass.SECURITY.getDbValue , ledgerID,
      exchangeCode, DefaultValues.CURRENCY_CODE, openDate, "ACTIVE", openDate, securityID)

    securityPositionDao.save(securityPosition)

    val positionHist = new PositionHist(
      new PositionHist.PK(securityPosition.getId, openDate.toLocalDate),
      BigDecimal.valueOf(0), BigDecimal.valueOf(0))

    positionHistDao.save(positionHist)

    securityPosition
  }

  protected def findPositionHist(positionId: Long, asOfDate: LocalDate): PositionHist = {
    var positionHist = positionHistDao.findByPositionIdAsOfDate(positionId, asOfDate)

    if (null == positionHist) {
      positionHist = new PositionHist(
        new PositionHist.PK(positionId, asOfDate),
        BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0))
      positionHistDao.save(positionHist)
    }
    positionHist
  }

  protected def findCarryingValueHist(id: Long, typeId: Long, accountId: Long, asOfDate: LocalDate): CarryingValueHist = {
    var carryingValueHist = carryingValueHistDao.findByPositionIdAsOfDate(id, typeId, asOfDate)

    if (null == carryingValueHist) {
      val pk = new CarryingValueHist.PK(id, typeId, asOfDate)
      carryingValueHist = new CarryingValueHist(pk, accountId, BigDecimal.valueOf(0.0),
        DefaultValues.CURRENCY_CODE, new LocalDateTime(asOfDate.toDateTimeAtCurrentTime))
      carryingValueHistDao.save(carryingValueHist)
    }
    carryingValueHist
  }

  protected def saveTransaction(t: Transaction, commissionAndFee: (BigDecimal, BigDecimal), asOfDate: LocalDate, repoFields: (BigDecimal, LocalDate, Integer) = null): Unit = {
    val securityTransaction = new SecurityTransaction(t.accountId, t.transactionSourceId, t.transactionClass,
      t.securityId, t.side.toString)
    securityTransaction.setOrderId(null)
    securityTransaction.setSourceTransactionId(t.sourceTransactionId)
    securityTransaction.setSourceTransactionDate(new LocalDateTime(t.executionDate.toDateTimeAtCurrentTime))
    securityTransaction.setTransactionStatus(null)
    securityTransaction.setStatusChangeDate(null)
    securityTransaction.setAmount(t.amount)
    securityTransaction.setAvgPrice(t.price)
    securityTransaction.setSettleCurrCode(null)
    securityTransaction.setTraderId(getLongOrNull(t.traderId))
    securityTransaction.setBrokerId(getLongOrNull(t.brokerId))
    securityTransaction.setFxRate2(null)
    securityTransaction.setCommissions(commissionAndFee._1)
    securityTransaction.setFees(commissionAndFee._2)
    securityTransaction.setExecutionDate(new LocalDateTime(t.executionDate.toDateTimeAtCurrentTime))
    securityTransaction.setSettlementDate(t.settlementDate)
    securityTransaction.setTransactionReason(null)

    if (repoFields != null) {
      securityTransaction.setAssetClassId(repoFields._3)
      securityTransaction.setInterest(repoFields._1)
      securityTransaction.setReturnDate(repoFields._2)
    }

    securityTransactionDao.save(securityTransaction)
  }

  protected def getLongOrNull(value: Option[Long]): java.lang.Long = {
    value match {
      case Some(x) => x
      case None => null
    }
  }

  protected def checkParameter(t: Transaction): Unit = {
    val zero = BigDecimal("0.0")
    if (t.amount < zero) {
      throw new BusinessException("amount can not be negative")
    }
    if (t.price < zero) {
      throw new BusinessException("price can not be negative")
    }
  }

  protected def getCashAmount(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val cashPosition = cashPositionDao.findByAccountIdLedgerId(accountId,
      LedgerType.CASH.getDbValue)
    if (cashPosition == null) {
      throw new BusinessException("Failed to get cashposition for account#"+ accountId + "!")
    }
    val cashPositionHist = findPositionHist(cashPosition.getId, asOfDate)
    cashPositionHist.getQuantity
  }
}
