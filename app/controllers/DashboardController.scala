package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import controllers.util.Jsonp
import com.datayes.invest.pms.web.model.models.ModelWrites._

import play.api.mvc._
import javax.inject.Inject
import com.datayes.invest.pms.web.service.DashboardService
import com.datayes.invest.pms.service.userpref.UserPref
import play.api.libs.json.Json
import com.datayes.invest.pms.web.sso.AuthAction


class DashboardController extends Controller with AsOfDateSupport with Jsonp with Logging {

  @Inject
  private var dashboardService: DashboardService = null
  
  @Inject
  private var userPref: UserPref = null

  def getAccountList = AuthAction { implicit req =>
    val accounts = transaction {
      dashboardService.getAccountList()
    }
    val json = Json.toJson(accounts)
    respondJsonOrJsonp(json)
  }

  def getAssetClassWeight(accountId: Long) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()
    val assetClassWeights = transaction {
      dashboardService.getAssetClassWeight(accountId, asOfDate)
    }
    val json = Json.toJson(assetClassWeights)

    respondJsonOrJsonp(json)
  }

  def getIndustryWeight(accountId: Long) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()

    val industryWeights = transaction {
      dashboardService.getIndustryWeight(accountId, asOfDate)
    }
    val json = Json.toJson(industryWeights)

    respondJsonOrJsonp(json)
  }
  
  def topHoldingStock(accountId: Long, number: Integer) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()
	
    val topHoldingStock = transaction {
      dashboardService.gettopHoldingStock(accountId, number, asOfDate)
    }
    val json = Json.toJson(topHoldingStock)
    respondJsonOrJsonp(json)
  }
  
  def saveLayoutConfig(content: String) = AuthAction { implicit req =>
    val (result, message) = try {
      userPref.saveLayoutConfig(content)
      (true, "layout config saved succesfully")
    } catch {
      case e: Throwable => (false, e.getMessage)
    }
    val json = Json.obj(
      "success" -> result,
      "message" -> message
    )
    respondJsonOrJsonp(json)
  }
  
  def getLayoutConfig() = AuthAction { implicit req =>
    val config = Json.toJson(userPref.getLayoutConfig)
    val json = Json.obj(
      "config" -> config
    )
    respondJsonOrJsonp(json)
  }
  
  def performanceOverview(accountId: Long) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()
    // TODO String and Long shoule be other type, and maybe add other standardFund
    // TODO The list will be get from other place later, maybe user preference
    val standardFund = List(("本基金", accountId),("上证综指", 1L), ("沪深300", 1782L), ("上证基指", 20L))
    val performance = transaction {
      dashboardService.getPerformance(accountId, asOfDate, standardFund)
    }
    val json = Json.toJson(performance)
    respondJsonOrJsonp(json)
  }

  def netValueTrend(accountId: Long, benchmarkIndexTicker: String) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()

    val netValueTrendItems = transaction {
      dashboardService.getNetValueTrends(accountId, asOfDate, benchmarkIndexTicker)
    }
    val json = Json.toJson(netValueTrendItems)
    respondJsonOrJsonp(json)
  }

  def accountOverview(accountId: Long) = AuthAction { implicit req =>
    val asOfDate = getAsOfDate()
    val accountOverview = transaction {
      dashboardService.getAccountOverview(accountId, asOfDate)
    }
    val json = Json.toJson(accountOverview)
    respondJsonOrJsonp(json)
  }
}
