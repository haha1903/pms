package controllers

import com.datayes.invest.pms.logging.Logging
import play.pms.{PmsResult, PmsAction, PmsController}
import javax.inject.Inject
import com.datayes.invest.pms.web.service.TradeService
import org.joda.time.LocalDate
import play.api.libs.json.Json
import com.datayes.invest.pms.web.model.models.ModelWrites.TradeWrites

class TradeController extends PmsController with Logging {

  @Inject
  private var tradeService: TradeService = null

  def history() = PmsAction { implicit req =>
    val accountIdOpt: Option[Long] = param("accountId")
    val startDate: LocalDate = param("startDate")
    val endDate: LocalDate = param("endDate").default(LocalDate.now())

    val history = tradeService.getHistory(accountIdOpt, startDate, endDate)
    val json = Json.toJson(history)

    PmsResult(json)
  }
}