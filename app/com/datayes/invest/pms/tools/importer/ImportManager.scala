package com.datayes.invest.pms.tools.importer

import java.io.File
import com.datayes.invest.pms.persist.dsl.transaction
import org.joda.time.{Days, LocalDate}
import scala.collection.concurrent
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.progress.{ProgressStatus, ProgressReport, ProgressListener, ProgressEvent}
import com.google.inject.Inject
import com.datayes.invest.pms.system.SystemInjectors
import com.datayes.invest.pms.dao.account.AccountDao
import java.sql.Timestamp
import com.datayes.invest.pms.entity.account.Account


class ImportManager extends ProgressListener with Logging {
  
  // TODO refactor with provider
  @Inject
  private var accountImporter: AccountCsvImporter = null
  
  @Inject
  private var accountDao: AccountDao = null
  
  private val PROGRESS_TTL = 1000 * 60 * 60 * 5    // TTL 5 hours

  private val progresses = concurrent.TrieMap.empty[String, ProgressReport]

  def importAccountCsv(file: File, shouldRunSimulation: Boolean): ImportResponse = {
      logger.debug("Start importing account csv file: " + file.getAbsolutePath)
    val account = transaction {
        accountImporter.importCsv(file)
    }

    if (shouldRunSimulation) {
      val today = LocalDate.now
      val openDate = account.getOpenDate
      val estimateTimeInMinutes = calcEstimateTime(openDate.toLocalDate, today)

      val injector = SystemInjectors.INSTANCE.getImporterInjector()
      val runner = injector.getInstance(classOf[SimulationRunner])
      runner.setAccount(account)
      runner.setEndDate(today)
      
      runner.addProgressListener(this)
      val startTime = System.currentTimeMillis()
      progresses.put(runner.uuid, new ProgressReport(ProgressStatus.NOT_STARTED, startTime, 0, 0L))
      runner.start()

      ImportResponse(runner.uuid, estimateTimeInMinutes)
    } else {
      // if not run simulation, set account active and return an empty string
      setAccountActive(account)
      ImportResponse("", 0)
    }
  }
  
  private def setAccountActive(account: Account): Unit = transaction {
    account.setStatus(null)
    val ts = new Timestamp(System.currentTimeMillis())
    account.setStatusChangeDate(ts)
    accountDao.update(account)
  }

  private def calcEstimateTime(openDate: LocalDate, endDate: LocalDate): Int = {
    val days = Days.daysBetween(openDate, endDate).getDays()
    val est = days.abs.toDouble * 2 / 30
    if (est < 1) 1 else (est + 0.5).toInt
  }

  def getProgressByUUID(uuid: String): Option[ProgressReport] = {
    val reportOpt = progresses.get(uuid)
    cleanupProgresses()
    reportOpt
  }

  private def cleanupProgresses() {
    val threshold = System.currentTimeMillis() - PROGRESS_TTL
    for ((uuid, report) <- progresses) {
      if (report.getStartTime < threshold &&
        (report.getStatus == ProgressStatus.COMPLETED || report.getStatus == ProgressStatus.FAILED)) {

        progresses.remove(uuid)
      }
    }
  }

  override def onProgressChanged(event: ProgressEvent) {
    val source = event.getSource.asInstanceOf[SimulationRunner]
    val oldReportOpt = progresses.get(source.uuid)
    oldReportOpt match {
      case Some(report) =>
        val newReport = new ProgressReport(event.getStatus, report.getStartTime, event.getPercent, event.getTimeElapsed)
        progresses.put(source.uuid, newReport)
      case None =>
        logger.warn("No progress report found for {} {}. There might be a code bug.", source.uuid)
    }
  }
}

case class ImportResponse(uuid: String, estimatedTimeInMinutes: Int)