package controllers

import com.datayes.invest.pms.logging.Logging

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import com.datayes.invest.pms.web.sso.AuthAction
import org.joda.time.LocalDate

class TestingOperationController extends Controller with Logging {

  private val operationForm = Form(tuple(
    "asOfDate" -> text,
    "endDate" -> text,
    "sod" -> optional(text),
    "valuation" -> optional(text),
    "eod" -> optional(text)
  ))

  def testingOperations = AuthAction { implicit req =>
    Ok(views.html.operation())
  }

  def testingOperationsPost = AuthAction { implicit req =>
    /*
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
          throw new RuntimeException("End date cannot be earlier than As of date (start date)")
        }
        ValuationFacade.valuateAllAccounts(asOfDate, endDate)

      case None =>
        processSingleDay(asOfDate, sodOpt, valuationOpt, eodOpt)
    }

    val duration = (System.currentTimeMillis() - startTime) / 1000
    * 
    */

    //Ok("Process finished in " + duration + " seconds")
    Ok("Fake response")
  }

  /*
  private def processSingleDay(asOfDate: LocalDate, sodOpt: Option[String], valuationOpt: Option[String], eodOpt: Option[String]) {
    sodOpt.map { _ =>
      transaction {
        AppGlobal.injector.getInstance(classOf[StartOfDaySequence]).processAllAccounts(asOfDate)
      }
    }

    valuationOpt.map { _ =>
      ValuationFacade.valuateAllAccountsInCache(asOfDate)
    }

    eodOpt.map { _ =>
      transaction {
        AppGlobal.injector.getInstance(classOf[EndOfDaySequence]).processAllAccounts(asOfDate)
      }
    }
  }

  private def parseDate(s: String): Option[LocalDate] = {
    try {
      Some(new LocalDate(s))
    } catch {
      case e: Throwable => None
    }
  }*/
}
