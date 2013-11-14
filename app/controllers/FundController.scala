package controllers


import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.web.service.FundService
import com.datayes.invest.pms.web.model.models.ModelWrites._
import play.api.libs.json.Json
import play.pms.{PmsAction, PmsController}


class FundController extends PmsController with Logging {
  @Inject
  private var fundService: FundService = null

  def getSummary(accountId: Long) = PmsAction { implicit req =>
    val asOfDate = paramAsOfDateOrToday
    val summary = fundService.getSummary(accountId, asOfDate)
    Json.toJson(summary)
  }
}
