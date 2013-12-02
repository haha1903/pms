package test

import com.datayes.invest.pms.dao.account.TransactionDao
import com.datayes.invest.pms.dao.account.TransactionDao
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.system.SystemInjectors
import com.datayes.invest.pms.persist.dsl._
import org.joda.time.LocalDateTime

object TestTransaction {

  def main(args: Array[String]): Unit = {
    val injector = SystemInjectors.INSTANCE.getInjector()

    val securityTransaction = new SecurityTransaction(1L, 1,
      TransactionClass.TRADE.getDbValue(), 1L, TradeSide.BUY.getDbValue())

    securityTransaction.setSourceTransactionDate(LocalDateTime.now())

    transaction {
      val transactionDao = injector.getInstance(classOf[TransactionDao])
      transactionDao.save(securityTransaction)
    }
  }
}
