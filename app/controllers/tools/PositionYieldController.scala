package controllers.tools

import javax.inject.Inject
import com.datayes.invest.pms.logic.positionyield.impl.PositionYieldLogicImpl
import play.api.mvc.Controller
import com.datayes.invest.pms.logging.Logging
import play.api.data.Form
import play.api.data.Forms._
import com.datayes.invest.pms.web.sso.AuthAction
import org.joda.time.LocalDate
import com.datayes.invest.pms.persist.dsl._
import scala.Some
import com.datayes.invest.pms.dao.account.AccountDao
import scala.collection.JavaConversions._

class PositionYieldController extends Controller with Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var positionYieldLogic: PositionYieldLogicImpl = null

  private val operationForm2 = Form(
    "asOfDate" -> optional(text)
  )

  def runProcess = AuthAction { implicit req =>
    Ok(views.html.tools.position_yield())
  }

  def runPositionYield = AuthAction { implicit req =>
    val sAsOfDateOpt = operationForm2.bindFromRequest().get
    val sAsOfDate = sAsOfDateOpt match {
      case Some(d) => d
      case None => LocalDate.now().toString()
    }

    val asOfDate = parseDate(sAsOfDate) match {
      case Some(d) => d
      case None =>
        throw new RuntimeException("Invalid request: " + sAsOfDate + " is not a valid date")
    }

    processPositionYield(asOfDate)

    Ok("Position Yield calculation finished")
  }

  private def parseDate(s: String): Option[LocalDate] = {
    try {
      Some(new LocalDate(s))
    } catch {
      case e: Throwable => None
    }
  }

  private def processPositionYield(asOfDate: LocalDate): Unit = {
    transaction {
      val accounts = accountDao.findEffectiveAccounts(asOfDate)
      for (a <- accounts) {
        positionYieldLogic.process(a, asOfDate)
      }
    }
  }

}
