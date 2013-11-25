package com.datayes.invest.pms.logic.transaction.impl

import scala.math.BigDecimal.int2bigDecimal
import scala.math.BigDecimal.javaBigDecimal2bigDecimal

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.security.FutureDao
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.dbtype.AssetClass
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.transaction.BusinessException
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.util.FutureMultiplierHelper

import javax.inject.Inject

class StockIndexFutureTransactionLogic extends TransactionLogicBase with Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var futureDao: FutureDao = null

  @Inject
  private var securityDao: SecurityDao = null

  
  override def process(t: Transaction): Unit = {
    checkParameter(t)
    try {
      val asOfDate = t.executionDate
      val commissionAndFee = updateCashPositionHist(t, asOfDate)
      updateSecurityPosition(t, asOfDate)
      saveTransaction(t, commissionAndFee, asOfDate)
    } catch {
      case e: Throwable =>
        logger.error("Error processing transaction {}: {}", t, e.getMessage, e)
    }
  }

  private def updateCashPositionHist(t: Transaction, asOfDate: LocalDate): (BigDecimal, BigDecimal) = {
    val turnover = getTurnover(t)
    
    if (t.side == TradeSide.BUY || t.side == TradeSide.SHORT) {
      val (judgeResult, marginNeeded) = isMarginEnough(t.accountId, turnover, t.side, t.securityId, asOfDate)
      if (judgeResult == false) {
        throw new BusinessException("Transaction:(" + t + ") account has not enough margin, " +
          "which need " + marginNeeded + " more!")
      }
    }
    
    val commissionShouldPay = getCommission(t.accountId, turnover, t.side, RateType.FutureCommission, t.securityId, asOfDate)
    val feeShouldPay = getFee(t.accountId, turnover, t.side, t.securityId)
    
    updateSettlePosition(t.accountId, t.side, feeShouldPay, asOfDate)
    
    (commissionShouldPay, feeShouldPay)
  }

  private def isMarginEnough(accountId: Long, turnover: BigDecimal, side: TradeSide, securityId: Long,
      asOfDate: LocalDate): (Boolean, BigDecimal) = {
    var result: Boolean = true
    
    val marginPosition = cashPositionDao.findByAccountIdLedgerId(accountId, LedgerType.MARGIN.getDbValue)
    if (marginPosition == null) {
      throw new BusinessException("Failed to find margin position for account #" + accountId)
    }
    val marginPositionHist = findPositionHist(marginPosition.getId, asOfDate)
    var marginAmountAvailable = marginPositionHist.getQuantity

    val minMarginRatio = calculateRate(accountId, RateType.FutureMinMarginRatio, side, securityId)
    val allFutureCostBase = getAllFutureCarryingValue(accountId, asOfDate)
    val needMargin = (allFutureCostBase + turnover) * minMarginRatio
    if (needMargin > marginAmountAvailable) {
      result = false
    }
    (result, needMargin - marginAmountAvailable)
  }

  private def getAllFutureCarryingValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var totalCarryingValue: BigDecimal = 0
    val securityPositionList = securityPositionDao.findByAccountId(accountId)
    for (i <- 0 to securityPositionList.size - 1) {
      totalCarryingValue += getFutureCostBase(securityPositionList.get(i), accountId, asOfDate)
    }
    totalCarryingValue
  }

  private def getFutureCostBase(securityPosition: SecurityPosition, accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var carryingValue: BigDecimal = 0
    val security = securityDao.findById(securityPosition.getSecurityId)
    if (security.getAssetClassId == AssetClass.FUTURE.getDbValue) {
      val carryingValueHist = findCarryingValueHist(securityPosition.getId, DefaultValues.CARRYING_VALUE_TYPE,
        accountId, asOfDate)
      carryingValue = carryingValueHist.getValueAmount
    }
    carryingValue
  }

  private def getFee(accountId: Long, turnover: BigDecimal, side: TradeSide, securityId: Long): BigDecimal = {
    val transactionFeeRate = calculateRate(accountId, RateType.FutureTransactionFee, side, securityId)
    val deliveryChargeRate = calculateRate(accountId, RateType.FutureDeliveryCharges, side, securityId)
    val feeShouldPay: BigDecimal = turnover * (transactionFeeRate + deliveryChargeRate)
    feeShouldPay
  }
  
  private def updateSettlePosition(accountId: Long, side: TradeSide, feeShouldPay: BigDecimal, asOfDate: LocalDate): Unit = {
    val payablePosition = cashPositionDao.findByAccountIdLedgerId(accountId,
      LedgerType.PAYABLE_SETT_ACCOUNTS.getDbValue)
    if (payablePosition == null) {
      throw new BusinessException("Failed to get payableSettlePosition for account#"+ accountId + "!")
    }
    val payablePositionHist = findPositionHist(payablePosition.getId, asOfDate)
    var payableAmountAvailable = payablePositionHist.getQuantity
    payablePositionHist.setQuantity((payableAmountAvailable + feeShouldPay).bigDecimal)
    positionHistDao.update(payablePositionHist)
  }

  private def updateSecurityPosition(t: Transaction, asOfDate: LocalDate): Unit = {

    val turnover = getTurnover(t)
    
    val securityPosition = getSecurityPosition(t.accountId, t.securityId, t.side, asOfDate)

    val carryingValueHist = findCarryingValueHist(securityPosition.getId, DefaultValues.CARRYING_VALUE_TYPE,
      t.accountId, asOfDate)
    var costBase = carryingValueHist.getValueAmount

    val securityPositionHist = findPositionHist(securityPosition.getId, asOfDate)
    var currentHolding = securityPositionHist.getQuantity

    if (t.side == TradeSide.BUY || t.side == TradeSide.SHORT) {
      carryingValueHist.setValueAmount((costBase + turnover).bigDecimal)
      securityPositionHist.setQuantity((currentHolding + t.amount).bigDecimal)
    } else if (t.side == TradeSide.SELL || t.side == TradeSide.COVER) {
      if (currentHolding < t.amount) {
        throw new BusinessException("Transaction:(" + t + ") account has not enough holding! " +
          "the current holding is " + currentHolding + ", " +
          "the sell amount is " + t.amount.bigDecimal + "!")
      }
      val costBaseChange = costBase * t.amount / currentHolding
  
      updateMarginPositon(t.accountId, t.side, costBaseChange, turnover, asOfDate)

      carryingValueHist.setValueAmount((costBase - costBaseChange).bigDecimal)
      securityPositionHist.setQuantity((currentHolding - t.amount).bigDecimal)
    }

    carryingValueHistDao.update(carryingValueHist)
    positionHistDao.update(securityPositionHist)
  }

  private val tradeSideLedgerTypeMapping = Map(
    TradeSide.BUY -> LedgerType.FUTURE_LONG,
    TradeSide.SELL -> LedgerType.FUTURE_LONG,
    TradeSide.COVER -> LedgerType.FUTURE_SHORT,
    TradeSide.SHORT -> LedgerType.FUTURE_SHORT
  )
  
  private def getSecurityPosition(accountId: Long, securityId: Long, side: TradeSide,
      asOfDate: LocalDate): SecurityPosition = {

    val ledgerType = tradeSideLedgerTypeMapping.get(side) match {
      case Some(t) => t
      case None =>
        throw new RuntimeException("Cannot map trade side " + side + " to ledger type")
    }

    var securityPosition = securityPositionDao.findByAccountIdSecurityIdLedgerId(
      accountId, securityId, ledgerType.getDbValue)
    if (securityPosition == null) {
      securityPosition = createSecurityPosition(accountId, securityId, ledgerType.getDbValue,
        getExchangeCodeBySecurity(securityId), new LocalDateTime(asOfDate.toDateTimeAtCurrentTime))
    }
    securityPosition
  }
  
  private def updateMarginPositon(accountId: Long, side: TradeSide, costBaseChange: BigDecimal,
      turnover: BigDecimal, asOfDate: LocalDate): Unit = {
    var marginPosition = cashPositionDao.findByAccountIdLedgerId(accountId, LedgerType.MARGIN.getDbValue)
    var marginPositionHist = findPositionHist(marginPosition.getId, asOfDate)
    var margin = marginPositionHist.getQuantity

    if (side == TradeSide.SELL) {
      margin = (margin + turnover - costBaseChange).bigDecimal
    } else {
      margin = (margin + costBaseChange - turnover).bigDecimal
    }

    marginPositionHist.setQuantity(margin)
    positionHistDao.update(marginPositionHist)
  }

  private def getExchangeCodeBySecurity(securityId: Long): String = {
    futureDao.findById(securityId).getExchangeCode;
  }

  private def getTurnover(t: Transaction): BigDecimal = {
    var turnover: BigDecimal = 0
    val future = futureDao.findById(t.securityId)
    val contractMultiplier = future.getContractMultiplier
    val Ratio = FutureMultiplierHelper.getRatio(contractMultiplier)
    turnover = t.price * t.amount * Ratio

    turnover
  }
  // TODO how to get the ratio per hand?
//  private def getRatio(value: String): BigDecimal = {
//    var Ratio: BigDecimal = 0
//
//    for (i <- 0 to value.length - 1) {
//      if (value.charAt(i).isDigit) {
//        Ratio = Ratio * 10 + (value.charAt(i) - '0').bigDecimal
//      }
//    }
//    Ratio
//  }
}