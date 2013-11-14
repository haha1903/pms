package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.service.PortfolioService
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.pms.PmsAction
import play.pms.PmsController
import org.joda.time.LocalDate
import play.api.libs.json.JsNumber

class CommonController extends PmsController with Logging {
  
  @Inject
  private var portfolioService: PortfolioService = null

  def industries = PmsAction { implicit req =>
    val asOfDate: Option[Int] = param("asOfDate")
    println(asOfDate)
    JsNumber(asOfDate.get)
//    
//    val inds = transaction {
//      portfolioService.getAvailableIndustry()
//    }
//    val arr = Json.toJson(inds)
//    arr
  }
}