package controllers

import org.joda.time.LocalDate
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.userpref.UserPref
import com.datayes.invest.pms.web.model.fastjson.asset.AssetTreeConverter
import com.datayes.invest.pms.web.model.models.AssetClassType
import com.datayes.invest.pms.web.model.models.FilterParam
import com.datayes.invest.pms.web.model.models.RangeFilterType
import com.datayes.invest.pms.web.service.PortfolioService
import javax.inject.Inject
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.pms.PmsAction
import play.pms.PmsController
import play.pms.PmsResult
import com.datayes.invest.pms.userpref.GroupingItem
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import com.datayes.invest.pms.web.model.models.AssetNodeType

class PortfolioController extends PmsController with Logging {
  
  @Inject
  private var portfolioService: PortfolioService = null
  
  @Inject
  private var userPref: UserPref = null

  def list = PmsAction { implicit req =>
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    val benchmarkIndexOpt: Option[String] = param("benchmarkIndex")
    val filterParam = paramFilterParam
    
    val currentGroupingSettings = userPref.getCurrentPortfolioGroupingSettings()
    
    val assetTree = portfolioService.getAssetTree(asOfDate, currentGroupingSettings, filterParam, benchmarkIndexOpt)
    val fjAssetTree = AssetTreeConverter.toFastJsonObject(assetTree)
    val jsonStr = com.alibaba.fastjson.JSON.toJSONString(fjAssetTree, false)

    PmsResult(jsonStr)
  }
  
  private def paramFilterParam(implicit req: Request[AnyContent]): FilterParam = {
    val assetClassOpt = req.getQueryString("filter.assetClass").map(AssetClassType.withName(_))
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
      val settings = list.map(AssetNodeType.withName(_))
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