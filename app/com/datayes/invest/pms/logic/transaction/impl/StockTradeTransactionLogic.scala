package com.datayes.invest.pms.logic.transaction.impl

import javax.inject.Inject
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.security.EquityDao
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.logic.transaction.BusinessException
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.account.SecurityPosition

class StockTradeTransactionLogic extends TransactionLogicBase with Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var equityDao: EquityDao = null
  

  override def process(t: Transaction): Unit = {
    checkParameter(t)
    val asOfDate = t.executionDate.toLocalDate
    val commissionAndFee = updateCashPositionHist(t, asOfDate)
    updateSecurityPosition(t, asOfDate)
    saveTransaction(t, commissionAndFee, asOfDate)
  }

  private def updateCashPositionHist(t: Transaction, asOfDate: LocalDate): (BigDecimal, BigDecimal) = {

    val cashChange = t.amount * t.price
    val commissionShouldPay = getCommission(t.accountId, cashChange, t.side, RateType.StockCommission, t.securityId, asOfDate)
    val feeShouldPay = getFee(t.accountId, cashChange: BigDecimal, t.side, t.securityId)
    val payableSettle = getPayableSettle(t.accountId, asOfDate)

    if (t.side != TradeSide.BUY && t.side != TradeSide.SELL) {
      throw new BusinessException("transaction side is error! which should only be buy or sell!")
    }

    updateSettlePosition(t.accountId, t.side, cashChange, feeShouldPay, asOfDate)

    (commissionShouldPay, feeShouldPay)
  }

  private def getFee(accountId: Long, cashChange: BigDecimal, side: TradeSide, securityId: Long): BigDecimal = {
    val feeRate = calculateRate(accountId, RateType.Stamp, side, securityId)
    val feeShouldPay: BigDecimal = cashChange * feeRate
    feeShouldPay
  }

  private def getPayableSettle(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val payableSettlePosition = cashPositionDao.findByAccountIdLedgerId(accountId,
      LedgerType.PAYABLE_SETT_ACCOUNTS.getDbValue)
    if (payableSettlePosition == null) {
      throw new BusinessException("Failed to get payableSettlePosition for account #"+ accountId)
    }
    val payableSettlePositionHist = findPositionHist(payableSettlePosition.getId, asOfDate)
    payableSettlePositionHist.getQuantity
  }

  private def updateSettlePosition(accountId: Long, side: TradeSide, cashChange: BigDecimal,
    feeShouldPay: BigDecimal, asOfDate: LocalDate): Unit = {

    val (feeSign, ledgerType) = side match {
      case TradeSide.BUY => (1, LedgerType.PAYABLE_SETT_ACCOUNTS)
      case TradeSide.SELL => (-1, LedgerType.RECEIVABLE_SETT_ACCOUNTS)
      case _ => throw new BusinessException("transaction side is error! which should only be buy or sell!")
    }

    val settlePosition = cashPositionDao.findByAccountIdLedgerId(accountId, ledgerType.getDbValue)
    if (settlePosition == null) {
      val positionName = if (side == TradeSide.BUY) {
        "payableSettlePosition"
      } else {
        "reciveableSettlePosition"
      }
      throw new BusinessException("Failed to get " + positionName + " for account #"+ accountId + "!")
    }
    val settlePositionHist = findPositionHist(settlePosition.getId, asOfDate)
    var settleAmountAvailable = settlePositionHist.getQuantity
    settlePositionHist.setQuantity(settleAmountAvailable + cashChange + feeSign * feeShouldPay)
    positionHistDao.update(settlePositionHist)
  }

  private def updateSecurityPosition(t: Transaction, asOfDate: LocalDate): Unit = {
    val cashChange = t.amount * t.price

    val securityPosition = getSecurityPosition(t.accountId, t.securityId, asOfDate)

    val carryingValueHist = findCarryingValueHist(securityPosition.getId, DefaultValues.CARRYING_VALUE_TYPE,
      t.accountId, asOfDate)
    var costBase = carryingValueHist.getValueAmount

    val securityPositionHist = findPositionHist(securityPosition.getId, asOfDate)
    var currentHolding = securityPositionHist.getQuantity
    if (t.side == TradeSide.BUY) {
      carryingValueHist.setValueAmount(costBase + cashChange)
      securityPositionHist.setQuantity(currentHolding + t.amount)
    } else if (t.side == TradeSide.SELL) {
      carryingValueHist.setValueAmount(costBase - costBase * t.amount / currentHolding)
      securityPositionHist.setQuantity(currentHolding - t.amount)
    } else {
      throw new BusinessException("transaction side is error! which should only be buy or sell!")
    }

    carryingValueHistDao.update(carryingValueHist)
    positionHistDao.update(securityPositionHist)
  }

  private def getSecurityPosition(accountId: Long, securityId: Long, asOfDate: LocalDate): SecurityPosition = {
    var securityPosition = securityPositionDao.findByAccountIdSecurityIdLedgerId(
      accountId, securityId, LedgerType.SECURITY.getDbValue)

    if (securityPosition == null) {
      securityPosition = createSecurityPosition(accountId, securityId, LedgerType.SECURITY.getDbValue,
        getExchangeCodeBySecurity(securityId), new LocalDateTime(asOfDate.toDateTimeAtCurrentTime))
    }
    securityPosition
  }

  private def getExchangeCodeBySecurity(securityId: Long): String = {
    equityDao.findById(securityId).getExchangeCode;
  }
}
