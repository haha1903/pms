package controllers

import org.joda.time.LocalDate

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.userpref.UserPref
import com.datayes.invest.pms.web.model.models.ModelWrites.AccountOverviewWrites
import com.datayes.invest.pms.web.model.models.ModelWrites.AssetClassWeightWrites
import com.datayes.invest.pms.web.model.models.ModelWrites.IndustryWeightNodeWrites
import com.datayes.invest.pms.web.model.models.ModelWrites.NetValueTrendItemWrites
import com.datayes.invest.pms.web.model.models.ModelWrites.PerformanceWrites
import com.datayes.invest.pms.web.model.models.ModelWrites.TopHoldingStockWrites
import com.datayes.invest.pms.web.service.FundService

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.pms.PmsAction
import play.pms.PmsController
import play.pms.PmsResult


class FundController extends PmsController with Logging {
  
  @Inject
  private var fundService: FundService = null
  
  @Inject
  private var userPref: UserPref = null

  def summary() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate: LocalDate = paramAsOfDateOrToday
    val summary = fundService.getSummary(accountId, asOfDate)
    val json = Json.toJson(summary)
    PmsResult(json)
  }

  def netTrend() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate: LocalDate = paramAsOfDateOrToday
    val benchmarkIndexTicker: String = param("benchmarkIndexTicker")
    val netValueTrendItems = fundService.getNetTrend(accountId, asOfDate, benchmarkIndexTicker)
    val json = Json.toJson(netValueTrendItems)
    PmsResult(json)
  }

  def industryProportion() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate = paramAsOfDateOrToday
    val industryProportions = fundService.getIndustryProportion(accountId, asOfDate)
    val json = Json.toJson(industryProportions)
    PmsResult(json)
  }

  def performanceOverview() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate = paramAsOfDateOrToday
    val standardFund = List(("本基金", accountId),("上证综指", 1L), ("沪深300", 1782L), ("上证基指", 20L))
    val performance = fundService.getPerformanceOverview(accountId, asOfDate, standardFund)
    val json = Json.toJson(performance)
    PmsResult(json)
  }

  def assetProportion() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate = paramAsOfDateOrToday
    val assetProportions = fundService.getAssetProportion(accountId, asOfDate)
    val json = Json.toJson(assetProportions)
    PmsResult(json)
  }

  def topHoldingStock() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val number: Int = param("number")
    val asOfDate = paramAsOfDateOrToday
    val topHoldingStock = fundService.getTopHoldingStock(accountId, number, asOfDate)
    val json = Json.toJson(topHoldingStock)
    PmsResult(json)
  }
  
  def getDashboardLayout = PmsAction { implicit req =>
    val config = userPref.getDashboardLayoutConfig()
    val json = Json.obj("config" -> config)
    PmsResult(json)
  }
  
  def setDashboardLayout = PmsAction { implicit req =>
    val config: String = param("config")
    
    val (result, message) = try {
      userPref.setDashboardLayoutConfig(config)
      (true, "layout config has bean saved")
    } catch {
      case e: Throwable => (false, e.getMessage)
    }
    val json = Json.obj("success" -> result, "message" -> message)
    PmsResult(json)
  }
}
