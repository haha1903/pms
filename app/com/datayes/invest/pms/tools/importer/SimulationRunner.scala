package com.datayes.invest.pms.tools.importer

import org.joda.time.{Days, LocalDate}
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.progress.{ProgressStatus, ProgressEvent, ProgressListener}
import java.util.UUID
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.entity.account.Account
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace
import com.datayes.invest.pms.logic.process.SODProcess
import com.datayes.invest.pms.logic.process.EODProcess

class SimulationRunner(accountId: Long, endDate: LocalDate) extends Thread with Logging {
  
  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var cacheLoader: SimulationRunnerCacheLoader = null
  
  @Inject
  private var cacheFlusher: SimulationRunnerCacheFlusher = null
  
  @Inject
  private var cacheWorkspace: CacheWorkspace = null
  
  @Inject
  private var sodProcess: SODProcess = null
  
  @Inject
  private var eodProcess: EODProcess = null
  

  val uuid = UUID.randomUUID().toString()

  private var progressListeners = List.empty[ProgressListener]

  private var percentComplete: Int = 0

  private var startTime: Long = 0L

  override def run(): Unit = {
    transaction {
      fireProgressEvent(0, ProgressStatus.RUNNING)
      percentComplete = 0
      startTime = System.currentTimeMillis()
      
      doWork()
      
      val duration = System.currentTimeMillis() - startTime
      logger.info("total time " + (duration / 1000.0) + " seconds")
    }
  }

  private def doWork(): Unit = {
    val account = accountDao.findById(accountId)
    if (account == null) {
      throw new RuntimeException("Failed to load account by id " + accountId)
    }
    var startDate = account.getOpenDate.toLocalDate()

    try {
      process(account, startDate, endDate)
      fireProgressEvent(100, ProgressStatus.COMPLETED)
    } catch {
      case e: Throwable =>
        logger.debug("Error processing imported account #" + account.getId, e)
        fireProgressEvent(percentComplete, ProgressStatus.FAILED)
    }
  }

  private def preloadCache(account: Account, asOfDate: LocalDate): Unit = {
    cacheLoader.load(cacheWorkspace, account.getId, asOfDate)
  }
  
  private def process(account: Account, startDate: LocalDate, endDate: LocalDate): Unit = {
    val daysBetween = Days.daysBetween(startDate, endDate).getDays()
    var d = startDate
    var dayCount = 0

    preloadCache(account, d)
    
    while (d.compareTo(endDate) <= 0) {
      logger.debug("Running SOD/EOD process for account #{}", account.getId)
      sodProcess.process(account, d)
      eodProcess.process(account, d)

      cacheFlusher.flush(cacheWorkspace, account.getId, d.minusDays(1))

      d = d.plusDays(1)
      dayCount += 1
      if (daysBetween > 0) {
        percentComplete = dayCount * 100 / daysBetween
        fireProgressEvent(percentComplete, ProgressStatus.RUNNING)
      }
    }

    cacheFlusher.flush(cacheWorkspace, account.getId, d.minusDays(1))
    cacheFlusher.waitForCompletion()
  }

  private def fireProgressEvent(percent: Int, status: ProgressStatus): Unit = {
    val e = new ProgressEvent(this, percent, status)
    e.setTimeElapsed(System.currentTimeMillis() - startTime)
    for (l <- progressListeners) {
      l.onProgressChanged(e)
    }
  }

  def addProgressListener(listener: ProgressListener): Unit = {
    progressListeners ::= listener
  }
}
