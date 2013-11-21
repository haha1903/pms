package test.assetsloader

import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.dao.account.impl.AccountDaoImpl
import com.datayes.invest.pms.system.SystemInjectors
import com.datayes.invest.pms.dao.account.AccountDao

object TestPortfolioLoader {
  
  def main(args: Array[String]): Unit = {
    val worker = SystemInjectors.INSTANCE.getInjector().getInstance(classOf[TestWorker])
    worker.run()
  }
}