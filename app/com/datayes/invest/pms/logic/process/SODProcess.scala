package com.datayes.invest.pms.logic.process

import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.{AccountDao, TransactionDao}
import com.datayes.invest.pms.logic.process.sod.CashSettlementProcessor
import com.datayes.invest.pms.logic.process.sod.MarginProcessor
import com.datayes.invest.pms.logic.process.sod.PositionRenewProcessor
import com.datayes.invest.pms.logic.process.sod.DividendProcessor
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.persist.dsl.transaction
import scala.collection.JavaConversions._
import javax.inject.Inject
import com.datayes.invest.pms.logic.process.sod.ApplyTransactionProcessor

class SODProcess extends Logging {
  
  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var transactionDao: TransactionDao = null
  
  @Inject
  private var positionRenewProcess: PositionRenewProcessor = null
  
  @Inject
  private var cashSettlementProcessor: CashSettlementProcessor = null

  @Inject
  private var marginProcessor: MarginProcessor = null
  
  @Inject
  private var dividendProcessor: DividendProcessor = null

  @Inject
  private var transactionProcessor: ApplyTransactionProcessor = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    deleteTransactions(account, asOfDate)
    renewPosition(account, asOfDate)
    doCashSettlement(account, asOfDate)
    doMarginSettlement(account, asOfDate)
    doDividend(account, asOfDate)
    redoTransaction(account, asOfDate)
  }
  
  def processAllAccounts(asOfDate: LocalDate): Unit = {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    for (a <- accounts) {
      process(a, asOfDate)
    }
  }
  
  private def deleteTransactions(account: Account, asOfDate: LocalDate): Unit = {
    transactionDao.deleteByAccountIdAsOfDate(account.getId, asOfDate)
  }
  
  private def renewPosition(account: Account, asOfDate: LocalDate): Unit = {
    positionRenewProcess.process(account, asOfDate)
  }
  
  private def doCashSettlement(account: Account, asOfDate: LocalDate): Unit = {
    cashSettlementProcessor.process(account, asOfDate)
  }
  
  private def doMarginSettlement(account: Account, asOfDate: LocalDate): Unit = {
    marginProcessor.process(account, asOfDate)
  }
  
  private def doDividend(account: Account, asOfDate: LocalDate): Unit = {
    dividendProcessor.process(account, asOfDate)
  }
  
  private def redoTransaction(account: Account, asOfDate: LocalDate): Unit = {
    transactionProcessor.process(account, asOfDate)
  }
}