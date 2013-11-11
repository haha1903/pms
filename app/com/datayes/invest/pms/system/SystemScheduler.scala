package com.datayes.invest.platform.system

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.system.ValuationThread
import com.datayes.invest.pms.system.TransactionThread
import com.datayes.invest.pms.persist.dsl.transaction
import org.joda.time.{LocalDateTime, LocalTime, LocalDate}
import com.datayes.invest.pms.config.Config
import javax.inject.Inject
import com.datayes.invest.pms.logic.process.SODProcess
import com.datayes.invest.pms.dao.account.AccountDao
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logic.process.EODProcess


class SystemScheduler extends Runnable with Logging {

  private var sodFlag = false
  private var eodFlag = false
  private var execDate: LocalDate = LocalDate.now
  
  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var eodProcess: EODProcess = null
  
  @Inject
  private var sodProcess: SODProcess = null
  
  @Inject
  private var valuationThread: ValuationThread = null
  
  @Inject
  private var transactionThread: TransactionThread = null
  
  private var isInitialized: Boolean = false


  override def run(): Unit = {
    try {
      while ( true ) {
        val now = LocalDateTime.now
        // 15 minutes later than market close time
        val endTime = getMarketCloseTime().plusMinutes(15)

        if ( !now.toLocalDate.equals(execDate) ) {
          execDate = now.toLocalDate
          sodFlag = false
          eodFlag = false
        }

        if ( !sodFlag ) {
          logger.info("Run start of day process on {}", now)

          processStartOfDay()
          sodFlag = true
          if ( !isInitialized ) {
            startTransactionThread()
            startValuationThread()
            isInitialized = true
            logger.info("System initialized")
          }
        }
        if ( !eodFlag ) {
          if ( now.toLocalTime.isAfter(endTime) ) {
            logger.info("Run end of day process on {}", now)

            processEndOfDay()
            eodFlag = true
          }
        }

        // Wait a minute
        Thread.sleep(60 * 1000)
      }
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
    }
  }

  private def getMarketCloseTime(): LocalTime = {
    val marketCloseTime = Config.INSTANCE.getString("market.close.time")
    new LocalTime(marketCloseTime)
  }

  private def processStartOfDay(): Unit = {
    transaction {
      val accounts = accountDao.findEffectiveAccounts(execDate)
      for (a <- accounts) {
        sodProcess.process(a, execDate)
      }
    }
  }

  private def processEndOfDay(): Unit = {
    transaction {
      val accounts = accountDao.findEffectiveAccounts(execDate)
      for (a <- accounts) {
        eodProcess.process(a, execDate)
      }
    }
  }

  @deprecated
  private def shutdownSystem(asOfDate: LocalDate): Unit = {
    logger.info("Start shut down the system process on {} (after market close)", asOfDate)
    
    if (valuationThread != null) {
      valuationThread.running = false
    }
    
    if (transactionThread != null) {
      transactionThread.closeSession
    }

    transaction {
      val today = LocalDate.now
      val accounts = accountDao.findEffectiveAccounts(today)
      for (a <- accounts) {
        eodProcess.process(a, today)
      }
    }

    logger.info("System shut down")
  }
  
  private def startTransactionThread(): Unit = {
    logger.info("Start transaction thread on {}", execDate)
    new Thread(transactionThread).start
  }
  
  private def startValuationThread(): Unit = {
    logger.info("Start valuation thread on {}", execDate)
    new Thread(valuationThread).start
  }
}