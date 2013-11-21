package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions.asScalaBuffer
import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.PositionValuationHistDao
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.web.assets.PortfolioLoader
import com.datayes.invest.pms.web.assets.TreeMaker
import com.datayes.invest.pms.web.assets.enums.AssetNodeType
import com.datayes.invest.pms.web.assets.models.AssetTree
import com.datayes.invest.pms.web.model.models.Chart
import com.datayes.invest.pms.web.model.models.ChartDataPoint
import com.datayes.invest.pms.web.model.models.ChartType
import com.datayes.invest.pms.web.model.models.FilterParam
import com.datayes.invest.pms.web.model.models.PortfolioView
import javax.inject.Inject
import com.datayes.invest.pms.web.assets.enums.AssetClassType


class PortfolioService extends Logging {

  @Inject
  private var accountDao: AccountDao = null

  
  @Inject
  private var portfolioLoader: PortfolioLoader = null
  
  @Inject
  private var positionValuationHistDao: PositionValuationHistDao = null
  

  def getAssetTree(asOfDate: LocalDate, groupings: List[AssetNodeType], filterParam: FilterParam, benchmarkIndexOpt: Option[String]): AssetTree = transaction {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    val allAssets = portfolioLoader.load(accounts, asOfDate, benchmarkIndexOpt)
    val allValidAssets = allAssets.filter { a => a.assetClass != AssetClassType.CASH }
    val filtered = FilterHelper.filterAssets(allValidAssets, filterParam)
    val accountIdNameMap = createAccountIdNameMap(accounts.toList)
    val treeMaker = new TreeMaker(groupings, accounts)
    val tree = treeMaker.create(filtered)
    
    tree
  }

  def getChart(accountId: Long, asOfDate: LocalDate, view: PortfolioView.Type, filterParam: FilterParam): Chart = transaction {
    view match {
      case PortfolioView.industry =>
        val points: Seq[(String, BigDecimal)] = getIndustryChartData(accountId, asOfDate, filterParam)
        val normalized: Seq[(String, BigDecimal)] = normalize(points)
        val chartDataPoints = normalized.map { case (s, v) => ChartDataPoint(s, v) }
        Chart(ChartType.pie, chartDataPoints)
    }
  }

  private def createAccountIdNameMap(accounts: List[Account]): Map[Long, String] =
    (for (a <- accounts) yield (a.getId.toLong -> a.getAccountName)).toMap

  private def getIndustryChartData(accountId: Long, asOfDate: LocalDate, filterParam: FilterParam): List[(String, BigDecimal)] = {
//    val assets = assetsLoader.loadAssets(accountId, asOfDate, None)
    val account = accountDao.findById(accountId)
    if (account == null) {
      return null
    }
    
    val assets = portfolioLoader.load(account, asOfDate, None)
    val filteredAssets = FilterHelper.filterAssets(assets, filterParam)
    val industrySums = filteredAssets.groupBy(_.industry).map { case (industry, assets) =>
      val valueSum = sum(assets.map(_.marketValue))
      (industry, valueSum)
    }
    logger.debug("industrySums: {}", industrySums)

    industrySums.toList
  }
  
  private def getMarketValue(pos: SecurityPosition, asOfDate: LocalDate): BigDecimal = {
    val pk = new PositionValuationHist.PK(pos.getId, DefaultValues.POSITION_VALUATION_TYPE.getDbValue, asOfDate)
    val valHist = positionValuationHistDao.findById(pk)
      if (valHist == null) {
        // TODO design exception
        throw new RuntimeException("Position #" + pos.getId + " valuation history not found")
      }
      valHist.getValueAmount
  }

  private def normalize(points: Seq[(String, BigDecimal)]): Seq[(String, BigDecimal)] = {
    val values = points.map(_._2)
    val s = sum(values)
    if (s == 0) {
      return points
    }
    val normalized: Seq[(String, BigDecimal)] = points.map { case (n, v) => (n, v / s) }
    logger.debug("normalized data points: {}", normalized)
    normalized
  }

  private def sum(values: Seq[BigDecimal]): BigDecimal =
    values.foldLeft(BigDecimal("0"))(_ + _)
}