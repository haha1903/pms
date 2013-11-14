package controllers

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.web.sso.AuthAction
import com.datayes.invest.pms.web.service.SummaryService
import com.datayes.invest.pms.web.model.models.ModelWrites._
import play.api.libs.json.Json
import play.pms.{PmsAction, PmsController}


class SummaryController extends PmsController with Logging {
  @Inject
  private var summaryService: SummaryService = null

  def getSummary() = PmsAction { implicit req =>
    val asOfDate = paramAsOfDateOrToday()
    val accountsSummary = summaryService.getSummary(asOfDate)
    Json.toJson(accountsSummary)
  }
}
