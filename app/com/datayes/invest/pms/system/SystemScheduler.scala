package com.datayes.invest.platform.system

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.system.ValuationThread
import com.datayes.invest.pms.system.TransactionThread
import com.datayes.invest.pms.persist.dsl.transaction
import java.util.TimerTask
import org.joda.time.LocalTime
import org.joda.time.LocalDate
import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.logic.process.SODProcess
import javax.inject.Inject
import com.datayes.invest.pms.logic.process.SODProcess
import com.datayes.invest.pms.dao.account.AccountDao
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logic.process.EODProcess


class SystemScheduler extends TimerTask with Logging {
  
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
  
  def run(): Unit = {
    try {
      var now = LocalTime.now
      val startTime = getMarketOpenTime
      val endTime = getMarketCloseTime
      val asOfDate = LocalDate.now
      if (now.compareTo(startTime) > 0
              && now.compareTo(endTime) < 0
              && !isInitialized) {
        isInitialized = true
        initializeSystem(asOfDate)
      } else if (now.compareTo(endTime) > 0 && isInitialized) {
        isInitialized = false
        shutdownSystem(asOfDate)
      } else {
        logger.debug("system is normal or has shutdown on {}", asOfDate)
      }
    } catch {
      case e: Throwable => logger.error(e.getMessage, e)
    }
  }
  
  private def getMarketOpenTime(): LocalTime = {
    val marketOpenTime = Config.INSTANCE.getString("market.open.time")
    new LocalTime(marketOpenTime)
  }
  
  private def getMarketCloseTime(): LocalTime = {
    val marketCloseTime = Config.INSTANCE.getString("market.close.time")
    new LocalTime(marketCloseTime)
  }
  
  private def initializeSystem(asOfDate: LocalDate): Unit = {
    logger.info("Start init the system process on {} (before market open)", asOfDate)

    transaction {
      val today = LocalDate.now
      val accounts = accountDao.findEffectiveAccounts(today)
      for (a <- accounts) {
        sodProcess.process(a, today)
      }
    }

    startTransactionThread()
    startValuationThread()
    
    logger.info("init the system successfully!")
  }
  
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
    
    logger.info("shut down the system successfully!")
  }
  
  private def startTransactionThread(): Unit = {
    new Thread(transactionThread).start
  }
  
  private def startValuationThread(): Unit = {
    new Thread(valuationThread).start
  }
}