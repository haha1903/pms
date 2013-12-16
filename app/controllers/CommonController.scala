package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.model.writes.IndexWrites
import com.datayes.invest.pms.web.service.CommonService

import javax.inject.Inject
import play.api.libs.json.Json
import play.pms.PmsAction
import play.pms.PmsController
import play.pms.PmsResult
import org.joda.time.LocalDate
import com.datayes.invest.pms.util.DefaultValues
import play.api.mvc.{Request, AnyContent}

class CommonController extends PmsController with Logging {
  
  @Inject
  private var commonService: CommonService = null

  def industries = PmsAction { implicit req =>
    val inds = transaction {
      commonService.getIndustries()
    }
    val arr = Json.toJson(inds)
    PmsResult(arr)
  }

  def marketIndexes = PmsAction { implicit req =>
    val indexes = transaction {
      commonService.getMarketIndexes()
    }
    val arr = Json.toJson(indexes)
    PmsResult(arr)
  }

  def previousTradeDay = PmsAction { implicit req =>
    val (date, exchange, numOfDays) = getCalendarParams()

    val retDate = commonService.previousTradeDay(date, exchange, numOfDays)
    val json = Json.obj("date" -> retDate)

    PmsResult(json)
  }

  def nextTradeDay = PmsAction { implicit req =>
    val (date, exchange, numOfDays) = getCalendarParams()

    val retDate = commonService.nextTradeDay(date, exchange, numOfDays)
    val json = Json.obj("date" -> retDate)

    PmsResult(json)
  }

  def isTradeDay = PmsAction { implicit req =>
    val (date, exchange, _) = getCalendarParams()

    val result = commonService.isTradeDay(date, exchange)
    val json = Json.obj("result" -> result)

    PmsResult(json)
  }

  private def getCalendarParams()(implicit req: Request[AnyContent]): (LocalDate, String, Int) = {
    val date: LocalDate = param("date").default(LocalDate.now)
    val exchange: String = param("exchange").default(DefaultValues.SH_STOCK_EXCHANGE_CODE)
    val numOfDays: Int = param("numOfDays").default(1)
    (date, exchange, numOfDays)
  }
}