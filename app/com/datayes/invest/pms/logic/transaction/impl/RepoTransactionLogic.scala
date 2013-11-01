package com.datayes.invest.pms.logic.transaction.impl

import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.security.RepoDao
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.logging.Logging
import java.math.RoundingMode
import scala.math.BigDecimal.double2bigDecimal
import scala.math.BigDecimal.int2bigDecimal
import scala.math.BigDecimal.javaBigDecimal2bigDecimal
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import javax.inject.Inject
import com.datayes.invest.pms.service.calendar.CalendarService
import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.logic.transaction.BusinessException
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.security.Repo
import com.datayes.invest.pms.dbtype.AssetClass
import com.datayes.invest.pms.dbtype.RateType


class RepoTransactionLogic extends TransactionLogicBase with Logging {

  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var calendarService: CalendarService = null

  @Inject
  private var repoDao: RepoDao = null
  
  

  override def process(t: Transaction) = {
    checkParameter(t)

    var asOfDate = t.executionDate

    // security pos
    updateSecurityPosition(t, asOfDate)
    // principal cash pos
    updateCashPositionHist(t.accountId, getPrincipalLedgerType(t.side), asOfDate, t.amount)
    // settlment amount cash pos for T+1
    updateCashPositionHist(t.accountId, getExecutionSettlementLedgerType(t.side), asOfDate, t.amount)
    // commission cash pos
    var commission = updateCommissionCashPosition(t, asOfDate)

    var fee = 0
    var repoFields = calculateRepoFields(t)
    saveTransaction(t, (commission, fee), asOfDate, repoFields)
  }

  def updateCashPositionHistOnReturnDate(accountId: Long, transactions: Seq[SecurityTransaction], returnDate: LocalDate) = {
    for (t <- transactions) {
      var repo = repoDao.findById(t.getSecurityId)
      var settleDate = calendarService.nextCalendarDay(t.getExecutionDate().toLocalDate())
      var interestDays = Days.daysBetween(settleDate, t.getReturnDate).getDays() + 1
      var previousDate = calendarService.previousCalendarDay(returnDate)
      var interest = calculatePartialInterest(settleDate, previousDate, interestDays, t.getInterest)
      var side = TradeSide.fromDbValue(t.getTradeSideCode)

      // move principle and full interest to settlement amount
      updateCashPositionHist(accountId, getInterestLedgerType(side), returnDate, -interest)
      updateCashPositionHist(accountId, getPrincipalLedgerType(side), returnDate, -t.getAmount)
      updateCashPositionHist(accountId, getSettlementLedgerType(side), returnDate, t.getInterest + t.getAmount)
    }
  }

  private def updateCashPositionHist(accountId: Long, ledgerType: LedgerType, date: LocalDate, deltaAmount: BigDecimal): PositionHist = {
    var posHist = findCashPositionHist(accountId, ledgerType, date)
    if (deltaAmount != 0) {
      posHist.setQuantity(posHist.getQuantity + deltaAmount)
      positionHistDao.update(posHist)
    }
    posHist
  }

  def findCashPositionHist(accountId: Long, ledgerType: LedgerType, asOfDate: LocalDate): PositionHist =
    Option(cashPositionDao.findByAccountIdLedgerId(accountId, ledgerType.getDbValue)) match {
      case None => throw new BusinessException(
        "Cash position of account %d on ledgerType not setup." format (accountId, ledgerType.getDbValue))
      case Some(position) => findPositionHist(position.getId, asOfDate)
    }

  def createCashInterestPositions(accountId: Long, transactions: Seq[SecurityTransaction], asOfDate: LocalDate): Unit = {
    if (transactions.isEmpty) return

    // reset interests
    for (ledgerType <- Array(LedgerType.PAYABLE_REPO_INTEREST, LedgerType.RECEIVABLE_REPO_INTEREST)) {
      findCashPositionHist(accountId, ledgerType, asOfDate).setQuantity(0)
    }

    // add interest
    for (transaction <- transactions) {
      createInterestCashPosition(transaction, asOfDate)
    }
  }

  def createInterestCashPosition(t: SecurityTransaction, asOfDate: LocalDate) = {
    var settleDate = calendarService.nextCalendarDay(t.getExecutionDate().toLocalDate())
    var interestDays = Days.daysBetween(settleDate, t.getReturnDate).getDays() + 1
    var days = Days.daysBetween(settleDate, asOfDate).getDays() + 1
    var interest = calculatePartialInterest(settleDate, asOfDate, interestDays, t.getInterest)
    val ledgerType = getInterestLedgerType(TradeSide.fromDbValue(t.getTradeSideCode))

    updateCashPositionHist(t.getAccountId, ledgerType, asOfDate, interest)
  }

  private def getSecurityPositionLedgerType(side: TradeSide): LedgerType = side match {
    case TradeSide.SELL => LedgerType.REPO_LONG
    case TradeSide.BUY => LedgerType.REPO_SHORT
    case _ => throw new BusinessException("side not valid on Repo transaction")
  }

  private def getInterestLedgerType(side: TradeSide): LedgerType = side match {
    case TradeSide.BUY => LedgerType.PAYABLE_REPO_INTEREST
    case TradeSide.SELL => LedgerType.RECEIVABLE_REPO_INTEREST
    case _ => throw new BusinessException("side not valid on Repo transaction")
  }

  private def getPrincipalLedgerType(side: TradeSide): LedgerType = side match {
    case TradeSide.BUY => LedgerType.PAYABLE_REPO_PRINCIPAL
    case TradeSide.SELL => LedgerType.RECEIVABLE_REPO_PRINCIPAL
    case _ => throw new BusinessException("side not valid on Repo transaction")
  }

  private def getExecutionSettlementLedgerType(side: TradeSide): LedgerType = side match {
    case TradeSide.BUY => LedgerType.RECEIVABLE_SETT_ACCOUNTS
    case TradeSide.SELL => LedgerType.PAYABLE_SETT_ACCOUNTS
    case _ => throw new BusinessException("side not valid on Repo transaction")
  }

  private def getSettlementLedgerType(side: TradeSide): LedgerType = side match {
    case TradeSide.BUY => LedgerType.PAYABLE_SETT_ACCOUNTS
    case TradeSide.SELL => LedgerType.RECEIVABLE_SETT_ACCOUNTS
    case _ => throw new BusinessException("side not valid on Repo transaction")
  }

  private def findSecurityPositionHist(accountId: Long, side: TradeSide, securityId: Long, executionDate: LocalDate, asOfDate: LocalDate): PositionHist = {    
    var ledgerType = getSecurityPositionLedgerType(side)
    var position = findSecurityPosition(accountId, securityId, ledgerType, executionDate)
    findPositionHist(position.getId, asOfDate)
  }

  def removeSecurityPositions(transactions: Seq[SecurityTransaction], asOfDate: LocalDate) = {
    for (t <- transactions) {
      var side = TradeSide.fromDbValue(t.getTradeSideCode)
      var secPosHist = findSecurityPositionHist(t.getAccountId, side, t.getSecurityId, t.getExecutionDate.toLocalDate(), asOfDate)
      secPosHist.setQuantity(secPosHist.getQuantity - t.getAmount);
      if (secPosHist.getQuantity == 0) {
        positionHistDao.delete(secPosHist)
      } else {
        positionHistDao.update(secPosHist)
      }
    }
  }

  private def findSecurityPosition(accountId: Long, securityId: Long, ledgerType: LedgerType, openDate: LocalDate): SecurityPosition = {
    Option(securityPositionDao.findByAccountIdSecurityIdLedgerIdOpenDate(
      accountId, securityId, ledgerType.getDbValue, openDate)) match {
      case Some(position) => position
      case None => createSecurityPosition(accountId, securityId, ledgerType.getDbValue,
        getExchangeCodeBySecurity(securityId), new LocalDateTime(openDate.toDateTimeAtStartOfDay()))
    }
  }

  private def getExchangeCodeBySecurity(securityId: Long): String = {
    repoDao.findById(securityId).getExchangeCode
  }

  private def updateSecurityPosition(t: Transaction, asOfDate: LocalDate) = {
    val secPosHist = findSecurityPositionHist(t.accountId, t.side, t.securityId, asOfDate, asOfDate)
    secPosHist.setQuantity(secPosHist.getQuantity() + t.amount)
    positionHistDao.update(secPosHist)
  }

  private def setScale(money: BigDecimal): BigDecimal =
    money.bigDecimal.setScale(2, RoundingMode.HALF_UP)

  private def calculatePartialInterest(startDate: LocalDate, endDate: LocalDate, days: Integer, interest: BigDecimal): BigDecimal =
    if (endDate.isBefore(startDate)) BigDecimal(0) else {
      var interestDays = Days.daysBetween(startDate, endDate).getDays() + 1
      var result: BigDecimal = interest * interestDays / BigDecimal(days)
      setScale(result)
    }

  private def calculateRepoFields(t: Transaction): (BigDecimal, LocalDate, Integer) = {
    var (interest, returnDate) = repoDao.findById(t.securityId) match {
      case repo: Repo => {
        var settleDate = calendarService.nextCalendarDay(t.executionDate)
        var returnDate = calendarService.nextTradeDay(t.executionDate, repo.getExchangeCode(), repo.getMaturity()) // T+days
        t.settlementDate = calendarService.nextTradeDay(returnDate, repo.getExchangeCode(), 1) // T+days+1         
        var interestDays = Days.daysBetween(settleDate, returnDate).getDays() + 1
        var interest: BigDecimal = interestDays * t.amount * t.price / 100.0 / BigDecimal(repo.getBaseDays())
        (setScale(interest), returnDate)
      }
      case _ => throw new BusinessException("not valid Repo security")
    }

    (interest, returnDate, AssetClass.REPO.getDbValue())
  }
  //TODO: check when to pay commission?  current all commission are merged
  private def updateCommissionCashPosition(t: Transaction, asOfDate: LocalDate): BigDecimal = {
    getCommission(t.accountId, t.amount, t.side, RateType.RepoCommission, t.securityId, asOfDate)
  }
}