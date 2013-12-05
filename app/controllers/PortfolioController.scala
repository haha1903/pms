package controllers

import com.datayes.invest.pms.dbtype.AssetClass
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.userpref.{GroupingItem, UserPref}
import com.datayes.invest.pms.util.gson.PmsGsonBuilder
import com.datayes.invest.pms.web.assets.enums.AssetNodeType
import com.datayes.invest.pms.web.model.models.{FilterParam, PortfolioView, RangeFilterType}
import com.datayes.invest.pms.web.model.models.ModelWrites.ChartWrites
import com.datayes.invest.pms.web.service.PortfolioService
import javax.inject.Inject
import org.joda.time.LocalDate
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.{AnyContent, Request}
import play.pms.{PmsAction, PmsController, PmsResult}

class PortfolioController extends PmsController with Logging {
  
  @Inject
  private var portfolioService: PortfolioService = null
  
  @Inject
  private var userPref: UserPref = null
  
  private val gson = new PmsGsonBuilder().create()

  def list = PmsAction { implicit req =>
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    val benchmarkIndexOpt: Option[String] = param("benchmarkIndex")
    val filterParam: FilterParam = paramFilterParam()
    
    val currentGroupingSettings = userPref.getCurrentPortfolioGroupingSettings()
    
    val assetTree = portfolioService.getAssetTree(asOfDate, currentGroupingSettings, filterParam, benchmarkIndexOpt)
    val jsonStr = gson.toJson(assetTree)

    PmsResult(jsonStr)
  }
  
  def chart = PmsAction { implicit req =>
    val view: String = param("view")
    val accountId: Long = param("accountId")
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    val filterParam = paramFilterParam()
    
    val portfolioView = PortfolioView.withName(view)
    val chart = portfolioService.getChart(accountId, asOfDate, portfolioView, filterParam)

    val json = Json.toJson(chart)
    PmsResult(json)
  }

  
  private def paramFilterParam()(implicit req: Request[AnyContent]): FilterParam = {
    val assetClassOpt = try {
      req.getQueryString("filter.assetClass").map(AssetClass.valueOf(_))
    } catch {
      case e: Throwable => None
    }
    val exchangeOpt = req.getQueryString("filter.exchange")
    val industryOpt = req.getQueryString("filter.industry")
    val rangeFilterTypeOpt = req.getQueryString("filter.range.type").map(RangeFilterType.withName(_))
    val rangeFilterMaxOpt = req.getQueryString("filter.range.max").map(BigDecimal(_))
    val rangeFilterMinOpt = req.getQueryString("filter.range.min").map(BigDecimal(_))

    val p = FilterParam(assetClassOpt, exchangeOpt, industryOpt, rangeFilterTypeOpt, rangeFilterMaxOpt, rangeFilterMinOpt)
    logger.debug("Filter parameter: {}", p)
    p
  }
  
  def getGroupingSettings = PmsAction { implicit req =>
    val availableItems = userPref.getAvailablePortfolioGroupingItems
    val settings = userPref.getCurrentPortfolioGroupingSettings().flatMap { assetNodeType =>
      availableItems.find(_.nodeType == assetNodeType)
    }
    val json = Json.obj(
      "availableItems" -> availableItems.map(groupingItemToJson(_)),
      "settings" -> settings.map(groupingItemToJson(_))
    )
    PmsResult(json)
  }
  
  private def groupingItemToJson(it: GroupingItem): JsValue = {
    Json.obj(
      "name" -> it.nodeType.toString(),
      "displayName" -> it.displayName
    )
  }

  def setGroupingSettings = PmsAction { implicit req =>
    val data: String = param("data")
    
    val list = data.split(",").map(_.trim).toList
    logger.debug("Saving grouping settings: {}", list.toString)
    
    val (result, message) = try {
      val settings = list.map(AssetNodeType.valueOf(_))
      userPref.setPortfolioGroupingSettings(settings)
      (true, "Portfolio grouping settings has been saved")
    } catch {
      case e: Throwable =>
        logger.error("Error saving grouping setting: " + e.getMessage, e)
        (false, e.getMessage)
    }
    val json = Json.obj(
      "success" -> result,
      "message" -> message
    )
    
    PmsResult(json)
  }

  
}