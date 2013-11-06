package controllers.tools

import com.datayes.invest.pms.logging.Logging
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import com.datayes.invest.pms.web.sso.AuthAction
import com.datayes.invest.pms.persist.dsl.transaction
import org.joda.time.LocalDate
import javax.inject.Inject
import com.datayes.invest.pms.logic.valuation.ValuationFacade
import com.datayes.invest.pms.dao.account.AccountDao
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logic.process.SODProcess
import com.datayes.invest.pms.logic.process.EODProcess

class RunProcessController extends Controller with Logging {
  
  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var eodProcess: EODProcess = null
  
  @Inject
  private var sodProcess: SODProcess = null
  
  @Inject
  private var valuationFacade: ValuationFacade = null

  private val operationForm = Form(tuple(
    "asOfDate" -> text,
    "endDate" -> text,
    "sod" -> optional(text),
    "valuation" -> optional(text),
    "eod" -> optional(text)
  ))

  def runProcess = AuthAction { implicit req =>
    Ok(views.html.tools.run_process())
  }

  def runProcessPost = AuthAction { implicit req =>
    val (sAsOfDate, sEndDate, sodOpt, valuationOpt, eodOpt) = operationForm.bindFromRequest().get
    val asOfDate = parseDate(sAsOfDate) match {
      case Some(d) => d
      case None =>
        throw new RuntimeException("Invalid request: " + sAsOfDate + " is not a valid date")
    }
    val endDateOpt = parseDate(sEndDate)

    logger.info("Running testing operations for as of date " + asOfDate + ", end date " + endDateOpt)

    val startTime = System.currentTimeMillis()

    endDateOpt match {
      case Some(endDate) =>
        if (asOfDate.compareTo(endDate) > 0) {
          throw new RuntimeException("End date " + endDate + " is earlier than As of date (start date) " + asOfDate)
        }
        val today = LocalDate.now()
        if (endDate.isAfter(today)) {
          throw new RuntimeException("End date " + endDate + " is later than current date " + today)
        }
        
        var date = asOfDate
        while (date.isBefore(endDate) || date.isEqual(endDate)) {
          processSingleDay(date, sodOpt, valuationOpt, eodOpt)
          date = date.plusDays(1)
        }

      case None =>
        processSingleDay(asOfDate, sodOpt, valuationOpt, eodOpt)
    }

    val duration = (System.currentTimeMillis() - startTime) / 1000

    Ok("Process finished in " + duration + " seconds")
  }

  private def processSingleDay(asOfDate: LocalDate, sodOpt: Option[String], valuationOpt: Option[String], eodOpt: Option[String]) {
    sodOpt.map { _ =>
      transaction {
        val accounts = accountDao.findEffectiveAccounts(asOfDate)
        for (a <- accounts) {
          sodProcess.process(a, asOfDate)
        }
      }
    }

    valuationOpt.map { _ =>
      transaction {
        val accounts = accountDao.findEffectiveAccounts(asOfDate)
        for (a <- accounts) {
          valuationFacade.valuate(a, asOfDate)
        }
      }
    }

    eodOpt.map { _ =>
      transaction {
        val accounts = accountDao.findEffectiveAccounts(asOfDate)
        for (a <- accounts) {
          eodProcess.process(a, asOfDate)
        }
      }
    }
  }

  private def parseDate(s: String): Option[LocalDate] = {
    try {
      Some(new LocalDate(s))
    } catch {
      case e: Throwable => None
    }
  }
}
