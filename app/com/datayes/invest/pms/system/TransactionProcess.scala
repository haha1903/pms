package com.datayes.invest.pms.system

import com.datayes.invest.pms.dao.account.{OrderDao, SourceTransactionDao, SystemIdMappingDao}
import com.datayes.invest.pms.dbtype.{TradeSide, TransactionClass, TransactionSource}
import com.datayes.invest.pms.entity.account.SourceTransaction
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.logic.transaction.{BusinessException, Transaction, TransactionLogicFactory}
import com.datayes.invest.pms.persist.dsl.transaction
import com.weston.jupiter.generated.{Tag, Execution}
import com.weston.stpapi.STPClient
import javax.inject.Inject
import org.joda.time.{LocalDate, LocalDateTime}
import java.text.SimpleDateFormat

class TransactionProcess extends Logging {
  
//  private val OMS = "OrderManager"

  @Inject
  private var orderDao: OrderDao = null
  
  @Inject
  private var systemIdMappingDao: SystemIdMappingDao = null
  
  @Inject
  private var sourceTransactionDao: SourceTransactionDao = null
  
  @Inject
  private var transactionLogicFactory: TransactionLogicFactory = null

  private val stpClient: STPClient = new STPClient()

  def process(execution: Execution): Unit = {
    transaction {
      val t = createTransactionByExecution(execution)
      saveSourceTransaction(t)
      val engine = transactionLogicFactory.get(t)
      engine.process(t)
    }
  }

  private def createTransactionByExecution(e: Execution): Transaction = {
    val amount = BigDecimal.valueOf(e.amount)
    val price = BigDecimal.valueOf(e.price)
    val brokerId: java.lang.Long = null
    val traderId: java.lang.Long = null
//    val brokerId: java.lang.Long = systemIdMappingDao.findPmsId(e.brokerID.toString, IdName.BROKER_ID.getDbValue(), OMS)
//    if (brokerId == null) {
//      logger.warn("Execution's brokerId is {}, which does not matched any brokerId in PMS!", e.brokerID)
//    }
//    val traderId = systemIdMappingDao.findPmsId(e.traderID.toString, IdName.TRADER_ID.getDbValue(), OMS)
//    if (traderId == null) {
//      logger.warn("Execution's traderId is {}, which does not matched any traderId in PMS!", e.traderID)
//    }
//    val accountId = systemIdMappingDao.findPmsId(e.accountID.toString, IdName.ACCOUNT_ID.getDbValue(), OMS)
//    if (accountId == null) {
//      throw new BusinessException("Execution's account id is " + e.accountID + ",it does not matched any accountId in PMS!")
//    }

    if (e.getEmsSecurityID == null) {
      throw new BusinessException("emsSecurityId is null")
    }
    val orderId: Long = {
      val oid = stpClient.parseExternalOrderNumber(e.getExternalOrderID)
      if (oid > 0) {
        oid
      } else {
        throw new RuntimeException("Order id not found. FIX THIS")
      }
    }
    val accountId = getAccountId(orderId)

    val executionDate = getExecutionDateTime(e)

    val sourceTransactionId = e.executionID.toString()
    val settlementDate = if (e.getSettlementDate < 0) {
      null
    } else {
      parseLocalDate(e.getSettlementDate)
    }
//    val settlementDate = e.settlementDate.toString() match { case "-1" => null case _ => new LocalDate(e.settlementDate) }
    val side = e.side.name().toUpperCase()
    val transactionClass = TransactionClass.TRADE

    val t = Transaction(accountId, e.getEmsSecurityID, sourceTransactionId, Some(orderId), getLongOption(traderId),
      getLongOption(brokerId), executionDate, settlementDate, TradeSide.valueOf(side), price, amount,
      TransactionSource.OMS.getDbValue, transactionClass)
    t
  }

  private def getExecutionDateTime(e: Execution): LocalDateTime = {
    val dateFormat = new SimpleDateFormat(Tag.formatDateTime)
    val time = Tag.getTime(e.getCreationTime, dateFormat)
    val dateTime = new LocalDateTime(time)
    dateTime
  }

  private def parseLocalDate(iDate: Long): LocalDate = {
    val year = iDate / 10000
    val month = (iDate % 10000) / 100
    val day = iDate % 100
    val date = new LocalDate(year.toInt, month.toInt, day.toInt)
    date
  }

  private def getAccountId(orderId: Long): Long = {
    val order = orderDao.findCurrentById(orderId)
    if (order == null) {
      throw new BusinessException("Order #" + orderId + " not found")
    }
    order.getAccountId
  }
  
  private def saveSourceTransaction(t: Transaction) {
    val sourceTransaction = new SourceTransaction(t.accountId, t.securityId, t.sourceTransactionId, getLongOrNull(t.orderIdOpt),
            getLongOrNull(t.traderId), getLongOrNull(t.brokerId), t.executionDate,
            t.settlementDate, t.side.toString, t.price.bigDecimal, t.amount.bigDecimal, t.transactionSourceId,
            t.transactionClass.getDbValue())
    sourceTransactionDao.save(sourceTransaction)
  }
  
  private def getLongOrNull(value: Option[Long]): java.lang.Long = {
    value match {
      case Some(x) => x
      case None => null
    }
  }

  private def getLongOption(d: java.lang.Long): Option[Long] = {
    if (d == null) {
      None
    } else {
      Some(d)
    }
  }
}
