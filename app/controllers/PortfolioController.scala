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

class PortfolioController extends PmsController with Logging {
  
  @Inject
  private var portfolioService: PortfolioService = null
  
  @Inject
  private var userPref: UserPref = null

  def list = PmsAction { implicit req =>
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    val benchmarkIndexOpt: Option[String] = param("benchmarkIndex")
    val filterParam = paramFilterParam
    
    val currentGroupingSettings = userPref.getPortfolioGroupingSettings()
    
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
}