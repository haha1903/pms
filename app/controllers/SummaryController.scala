package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.model.models.ModelWrites.AccountsSummaryWrites
import com.datayes.invest.pms.web.service.SummaryService

import javax.inject.Inject
import play.api.libs.json.Json
import play.pms.PmsAction
import play.pms.PmsController
import play.pms.PmsResult


class SummaryController extends PmsController with Logging {
  @Inject
  private var summaryService: SummaryService = null

  def getSummary() = PmsAction { implicit req =>
    val asOfDate = paramAsOfDateOrToday
    val accountsSummary = summaryService.getSummary(asOfDate)
    val json = Json.toJson(accountsSummary)
    PmsResult(json)
  }
}
