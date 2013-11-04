package com.datayes.invest.pms.logic.process.sod

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.SourceTransactionDao
import com.datayes.invest.pms.entity.account.{Account, SourceTransaction}
import javax.inject.Inject
import org.joda.time.LocalDate
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logic.transaction.TransactionLogicFactory
import com.datayes.invest.pms.logic.transaction.Transaction
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionClass

class ApplyTransactionProcessor extends Logging {

  @Inject
  private var sourceTransactionDao: SourceTransactionDao = null
  
  @Inject
  private var transactionLogicFactory: TransactionLogicFactory = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    logger.debug("Start redo transaction for account {} on {}", account.getId, asOfDate)
    redoTransaction(account, asOfDate)
    logger.info("Transactions redo finished for account #{}", account.getId)
  }

  private def redoTransaction(account: Account, asOfDate: LocalDate): Unit = {
    val transactionList = sourceTransactionDao.findByAccountIdAsOfDate(account.getId, asOfDate)
    for (th <- transactionList) {
      logger.debug("Redo transaction {}", th)
      val t = createTransaction(th)
      val engine = transactionLogicFactory.get(t)
      engine.process(t)
    }
  }

  private def createTransaction(sourceTransaction: SourceTransaction): Transaction =
    Transaction(sourceTransaction.getAccountId,
      sourceTransaction.getSecurityId,
      sourceTransaction.getSourceTransactionId,
      getLongOption(sourceTransaction.getTraderId),
      getLongOption(sourceTransaction.getBrokerId),
      sourceTransaction.getExecutionDate.toLocalDate,
      sourceTransaction.getSettlementDate,
      TradeSide.valueOf(sourceTransaction.getTradeSideCode),
      sourceTransaction.getPrice,
      sourceTransaction.getAmount,
      sourceTransaction.getTransactionSourceId,
      TransactionClass.fromDbValue(sourceTransaction.getTransactionClassCode))

  private def getLongOption(d: java.lang.Long): Option[Long] = {
    if (d == null) {
      None
    } else {
      Some(d)
    }
  }
}