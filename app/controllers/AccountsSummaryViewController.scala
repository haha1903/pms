package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import controllers.util.Jsonp
import com.datayes.invest.pms.web.model.models.ModelWrites._

import play.api.mvc._
import javax.inject.Inject
import com.datayes.invest.pms.web.service.AccountsSummaryService
import play.api.libs.json.Json
import com.datayes.invest.pms.web.sso.AuthAction


class AccountsSummaryViewController extends Controller with AsOfDateSupport with Jsonp with Logging {

  @Inject
  private var accountsSummaryService: AccountsSummaryService = null

  def getSummary() = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()
    val accountsSummary = transaction {
      accountsSummaryService.getSummary(asOfDate)
    }
    val json = Json.toJson(accountsSummary)
    respondJsonOrJsonp(json)
  }
}
