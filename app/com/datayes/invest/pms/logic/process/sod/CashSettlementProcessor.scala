package com.datayes.invest.pms.logic.process.sod

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import scala.math.BigDecimal._
import scala.collection.JavaConversions._
import org.joda.time.{LocalTime, LocalDate, LocalDateTime}
import com.datayes.invest.pms.logic.process.Processor
import com.datayes.invest.pms.dao.account.CashPositionDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.CashTransactionDao
import com.datayes.invest.pms.dao.account.SecurityTransactionDao
import com.datayes.invest.pms.logic.transaction.impl.RepoTransactionLogic
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.service.calendar.CalendarService
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.dbtype.CashTransactionType
import com.datayes.invest.pms.dbtype.CashTransactionReason
import com.datayes.invest.pms.entity.account.CashTransaction
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.dbtype.CashTransactionMethod
import com.datayes.invest.pms.entity.account.SourceTransaction
import com.datayes.invest.pms.util.DefaultValues


/*
 * 清算款根据目前的T+1交收规则在T+1(工作日）转换到现金科目。也就是说应付的减少现金，应收的增加现金。
 */

class CashSettlementProcessor extends Processor with Logging {
  
  @Inject
  private var calendarService: CalendarService = null

  @Inject
  private var cashPositionDao: CashPositionDao = null

  @Inject
  private var cashTransactionDao: CashTransactionDao = null
  
  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  @Inject
  private var repoTransactionLogic: RepoTransactionLogic= null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    //FIXME: change to settlement date when calendar is ready
    if (! calendarService.isTradeDay(asOfDate)) {
      logger.debug("{} is not settlment date, skip cash settlement for account #{}", asOfDate, account.getId)
      return
    }
    
    logger.debug("Start cash settlement process on {} (before market open)", asOfDate)
    refreshAccount(account.getId, asOfDate)
    logger.info("Settlement cash refreshed for account #{} on {}", account.getId, asOfDate)
  }

  private def refreshAccount(accountId: Long, asOfDate: LocalDate): Unit = {
    val payableAmountAvailable = getSettlementAmount(accountId: Long, LedgerType.PAYABLE_SETT_ACCOUNTS, asOfDate)
    val receivablePositionAmountAvailable = getSettlementAmount(accountId: Long, LedgerType.RECEIVABLE_SETT_ACCOUNTS, asOfDate)
    if (payableAmountAvailable != 0 || receivablePositionAmountAvailable != 0) {
      updateCashAmount(accountId, payableAmountAvailable, receivablePositionAmountAvailable, asOfDate)
      saveTransaction(accountId, payableAmountAvailable, receivablePositionAmountAvailable, asOfDate)
    }
    
    processRepoTransactions(accountId, asOfDate)
  }
  
  private def processRepoTransactions(accountId: Long, asOfDate: LocalDate) = {
    var transactions = securityTransactionDao.findRepoTransactionOnReturnDate(accountId, asOfDate)
    repoTransactionLogic.updateCashPositionHistOnReturnDate(accountId, transactions, asOfDate)
  }
  
  private def getSettlementAmount(accountId: Long, ledgerType: LedgerType, asOfDate: LocalDate): BigDecimal = {
    var settleAmount: BigDecimal = 0
    val settlePositionHist = getPositionHist(accountId, ledgerType, asOfDate)
    if (settlePositionHist != null) {
      settleAmount = settlePositionHist.getQuantity
      settlePositionHist.setQuantity(BigDecimal.valueOf(0).bigDecimal)
      positionHistDao.update(settlePositionHist)
    }
    settleAmount
  }
  
  // on settlment date
  private def updateCashAmount(accountId: Long, payableAmountAvailable: BigDecimal,
      receivablePositionAmountAvailable: BigDecimal, asOfDate: LocalDate): Unit = {
    val cashPositionHist = getPositionHist(accountId, LedgerType.CASH, asOfDate)
    if (cashPositionHist != null) {
      var cashAmountAvailable = cashPositionHist.getQuantity
      cashAmountAvailable = (cashAmountAvailable - payableAmountAvailable + receivablePositionAmountAvailable).bigDecimal
      cashPositionHist.setQuantity(cashAmountAvailable)
      positionHistDao.update(cashPositionHist)
    }
  }
  
  private def getPositionHist(accountId: Long, ledgerType: LedgerType, asOfDate: LocalDate): PositionHist = {
    val ledgerTypeId = ledgerType.getDbValue
    val position = cashPositionDao.findByAccountIdLedgerId(accountId, ledgerTypeId)
    if (position == null) {
      logger.error("account id = #{}, position of ledger {} is null", accountId, ledgerType)
      return null
    }
    val positionHist = positionHistDao.findByPositionIdAsOfDate(position.getId, asOfDate)
    positionHist
  }

  private def saveTransaction(accountId: Long, payableAmount: BigDecimal,
    receivableAmount: BigDecimal, asOfDate: LocalDate): Unit = {

    if (payableAmount != 0) {
      val payableTransaction = createTransaction(
        accountId, CashTransactionType.DEBIT, CashTransactionReason.CAPMOVE.toString, asOfDate)
      payableTransaction.setAmount(payableAmount.bigDecimal)
      cashTransactionDao.save(payableTransaction)
    }
    if (receivableAmount != 0) {
      val receivableTransaction = createTransaction(
        accountId, CashTransactionType.CREDIT, CashTransactionReason.ACCTINTEREST.toString, asOfDate)
      receivableTransaction.setAmount(receivableAmount.bigDecimal)
      cashTransactionDao.save(receivableTransaction)
    }
  }
  
  private def createTransaction(accountId: Long, transactionType: CashTransactionType, transactionReason: String,
      asOfDate: LocalDate): CashTransaction = {

    val cashTransaction = new CashTransaction(accountId, TransactionSource.PMS.getDbValue,
      TransactionClass.CASH.getDbValue(), transactionType.getDbValue(), CashTransactionMethod.TRANSFER.getDbValue(),
      transactionReason)

    cashTransaction.setOrderId(null)
    cashTransaction.setSourceTransactionId("SETTLEMENT")    // TODO what is this source transaction id?
    cashTransaction.setSourceTransactionDate(asOfDate.toLocalDateTime(LocalTime.MIDNIGHT))
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