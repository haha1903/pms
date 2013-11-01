package com.datayes.invest.pms.web.service

import javax.inject.Inject
import org.joda.time.LocalDate
import com.datayes.invest.pms.dao.account.{SecurityPositionDao, PositionValuationHistDao}
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.model.models._


class PortfolioChartService extends Logging {
  
  @Inject
  private var assetsLoader: AssetsLoader = null
  
  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityPositionDao: SecurityPositionDao = null

  @Inject
  private var positionValuationHistDao: PositionValuationHistDao = null


  def getChart(accountId: Long, asOfDate: LocalDate, view: PortfolioView.Type, filterParam: FilterParam): Chart = {
    view match {
      case PortfolioView.industry =>
        val points: Seq[(String, BigDecimal)] = getIndustryChartData(accountId, asOfDate, filterParam)
        val normalized: Seq[(String, BigDecimal)] = normalize(points)
        val chartDataPoints = normalized.map { case (s, v) => ChartDataPoint(s, v) }
        Chart(ChartType.pie, chartDataPoints)
    }
  }

  private def getIndustryChartData(accountId: Long, asOfDate: LocalDate, filterParam: FilterParam): List[(String, BigDecimal)] = {
    val assets = assetsLoader.loadAssets(accountId, asOfDate)
    val filteredAssets = FilterHelper.filterAssets(assets, filterParam)
    val industrySums = filteredAssets.groupBy(_.industry).map { case (industry, assets) =>
      val valueSum = sum(assets.map(_.marketValue))
      (industry, valueSum)
    }
    logger.debug("industrySums: {}", industrySums)

    industrySums.toList
  }
  /*
  private def getIndustry(securityId: Long): String = {
    val security = securityDao.findById(securityId)
    logger.info(security.toString)
    IndustryCache.getIndustryByPartyId(Option(security.getPartyId))
  }*/
  
  private def getMarketValue(pos: SecurityPosition, asOfDate: LocalDate): BigDecimal = {
    val valHist = positionValuationHistDao.findByPositionIdAsOfDate(pos.getId,
        DefaultValues.POSITION_VALUATION_TYPE.getDbValue, asOfDate)
      if (valHist == null) {
        // TODO design exception
        throw new RuntimeException("Position #" + pos.getId + " valuation history not found")
      }
      valHist.getValueAmount
  }

  private def normalize(points: Seq[(String, BigDecimal)]): Seq[(String, BigDecimal)] = {
    val values = points.map(_._2)
    val s = sum(values)
    val normalized = points.map { case (n, v) => (n, v / s) }
    logger.debug("normalized data points: {}", normalized)
    normalized
  }

  private def sum(values: Seq[BigDecimal]): BigDecimal =
    values.foldLeft(BigDecimal("0"))(_ + _)
}