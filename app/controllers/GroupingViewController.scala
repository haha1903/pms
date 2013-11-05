package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.service.{ PortfolioChartService, PortfolioService }
import com.datayes.invest.pms.userpref.{GroupingItem, UserPref}
import com.datayes.invest.pms.web.model.models._
import com.datayes.invest.pms.web.model.fastjson.asset.{Asset => FAsset, AssetNode => FAssetNode, AssetTree => FAssetTree, AssetNodeType => FAssetNodeType, AssetTreeConverter}
import com.datayes.invest.pms.web.model.models.ModelWrites._
import controllers.util.Jsonp

import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import com.datayes.invest.pms.web.sso.AuthAction


class GroupingViewController extends Controller with AsOfDateSupport with Jsonp with Logging {

  @Inject
  private var portfolioService: PortfolioService = null

  @Inject
  private var portfolioChartService: PortfolioChartService = null

  @Inject
  private var userPref: UserPref = null

  def all = AuthAction { implicit req =>
    val grouping = userPref.getPortfolioGroupingSetting
    val asOfDateOpt = getAsOfDateOpt
    val filterParam = getFilterParam
    val assetTree = asOfDateOpt match {
      case Some(asOfDate) => transaction {
        portfolioService.getAssetTree(asOfDate, grouping, filterParam)
      }
      case None => AssetTree(AssetNodeType.root, "root", "root", Seq.empty[AssetNode])
    }

    val fjAssetTree = AssetTreeConverter.toFastJsonObject(assetTree)
    val jsonStr = com.alibaba.fastjson.JSON.toJSONString(fjAssetTree, false)

    respondJsonOrJsonp(jsonStr)
  }

  def chart(view: String, accountId: Long) = AuthAction { implicit req =>
    val portfolioView = PortfolioView.withName(view)
    val asOfDate = getAsOfDate
    val filterParam = getFilterParam
    val chart = transaction {
      portfolioChartService.getChart(accountId, asOfDate, portfolioView, filterParam)
    }

    val json = Json.toJson(chart)
    respondJsonOrJsonp(json)
  }

  def getGroupingSetting = AuthAction { implicit req =>
    val setting = userPref.getPortfolioGroupingSetting().flatMap { assetNodeType =>
      userPref.availablePortfolioGroupingItems.find(_.nodeType == assetNodeType)
    }
    val json = Json.obj(
      "availableItems" -> userPref.availablePortfolioGroupingItems.map(groupingItemToJson(_)),
      "setting" -> setting.map(groupingItemToJson(_))
    )
    respondJsonOrJsonp(json)
  }

  private def groupingItemToJson(it: GroupingItem): JsValue = {
    Json.obj(
      "name" -> it.nodeType.toString(),
      "displayName" -> it.displayName
    )
  }

  def saveGroupingSetting(setting: String) = AuthAction { implicit req =>
    val settingList = setting.split(",").map(_.trim).toList
    logger.debug("Saving grouping settings: {}", settingList.toString)
    val (result, message) = try {
      val setting = settingList.map(AssetNodeType.withName(_))
      userPref.savePortfolioGroupingSetting(setting)
      (true, "Portfolio grouping settings saved succesfully")
    } catch {
      case e: Throwable =>
        logger.error("Error saving grouping setting: " + e.getMessage, e)
        (false, e.getMessage)
    }
    val json = Json.obj(
      "success" -> result,
      "message" -> message
    )
    respondJsonOrJsonp(json)
  }

  def getAvailableIndustry = AuthAction { implicit req =>
    val availableIndustry = portfolioService.getAvailableIndustry()

    val jArray = Json.toJson(availableIndustry)
    respondJsonOrJsonp(jArray)
  }

  private def getFilterParam(implicit req: Request[AnyContent]): FilterParam = {
    val assetClass = getParameterOption("assetClass").map(AssetClassType.withName(_))
    val exchange = getParameterOption("exchange")
    val industry = getParameterOption("industry")
    val rangeFilterType = getParameterOption("rangeFilterType").map(RangeFilterType.withName(_))
    val rangeFilterMax = getParameterOption("rangeFilterMax").map(BigDecimal(_))
    val rangeFilterMin = getParameterOption("rangeFilterMin").map(BigDecimal(_))

    val p = FilterParam(assetClass, exchange, industry, rangeFilterType, rangeFilterMax, rangeFilterMin)
    logger.debug("Filter parameter: {}", p)
    p
  }

  private def getParameterOption(key: String)(implicit req: Request[AnyContent]): Option[String] =
    req.queryString.get(key) flatMap (_.headOption)
}
