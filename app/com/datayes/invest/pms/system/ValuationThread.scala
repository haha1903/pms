package com.datayes.invest.pms.system

import com.datayes.invest.pms.logging.Logging
import java.util.Timer
import org.joda.time.LocalDate
import com.datayes.invest.pms.config.Config
import javax.inject.Inject
import com.datayes.invest.pms.logic.valuation.ValuationFacade
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.persist.dsl.transaction
import scala.collection.JavaConversions._

class ValuationThread extends Runnable with Logging {

  private lazy val valuationInterval = Config.INSTANCE.getLong("system.valuation.interval")
  
  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var valuationFacade: ValuationFacade = null
  
  var running: Boolean = false
  
  def run(): Unit = {
    logger.info("Valuation thread started")
    this.running = true
    val timer = new Timer
    while(running) {
      try {
          val asOfDate = LocalDate.now
          transaction {
            val accounts = accountDao.findEffectiveAccounts(asOfDate)
            for (a <- accounts) {
              valuationFacade.valuate(a, asOfDate)
            }
          }
      } catch {
        case th: Throwable =>
          logger.error("Exception occurred in valuation thread", th)
      } finally {
        Thread.sleep(valuationInterval * 1000)
      }
    }
  }
}
