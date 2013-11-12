package com.datayes.invest.pms.web.service

import org.joda.time.LocalDate
import scala.BigDecimal._
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.model.models.Asset
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.security.Security
import com.datayes.invest.pms.entity.security.Equity
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.entity.security.Repo
import com.datayes.invest.pms.web.model.models.AssetClassType
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.service.industry.IndustryService
import com.datayes.invest.pms.service.marketindex.MarketIndexService
import com.datayes.invest.pms.util.DefaultValues

class SingleAssetLoader(position: Position, positionHistOpt: Option[PositionHist],
    carryingValueHistOpt: Option[CarryingValueHist], positionValuationHistOpt: Option[PositionValuationHist],
    previousPositionValuationHistOpt: Option[PositionValuationHist], benchmarkIndexOpt: Option[String],
    asOfDate: LocalDate, marketDataService: MarketDataService, industryService: IndustryService,
    marketIndexService: MarketIndexService, securityDao: SecurityDao) extends Logging {
  
  def load(): Option[Asset] = {
    position match {
      case secPos: SecurityPosition =>
        loadSecurityAsset(secPos)
      case x =>
        logger.error("Code error. Unable to handle position {}", position)
        None
    }
  }

  private def loadSecurityAsset(position: SecurityPosition): Option[Asset] = {

    val security = securityDao.findById(position.getSecurityId)

    if (security == null) {
      logger.error("Failed to load security position by security id {}", position.getSecurityId)
      None
    } else {
      val asset = convertSecurityToAsset(position, security)
      Some(asset)
    }
  }


  private def convertSecurityToAsset(position: SecurityPosition, security: Security): Asset = {
    val previousDate = asOfDate.minusDays(1)
    val name = {
      val abbr = security.getNameAbbr
      if (abbr == null || abbr.trim.isEmpty)
        security.getName
      else
        abbr
    }

    val code = security.getTickerSymbol

    val asset = Asset(name, code, security.getId)

    // Asset class
    asset.assetClass = security match {
      case e: Equity => AssetClassType.equity
      case f: Future => AssetClassType.future
      case g: Repo => AssetClassType.none
      case x => throw new RuntimeException("Unexpected security: " + x.getClass + " at #" + x.getId)
    }

    // Exchange
    asset.exchange = position.getExchangeCode

    // Industry
    asset.industry = industryService.getIndustryBySecurityId(security.getId)
    
    // Benchmark index weight
    val benchmarkIndex = benchmarkIndexOpt.getOrElse(DefaultValues.BENCHMARK_MARKET_INDEX)
    asset.benchmarkIndexWeight = marketIndexService.getIndexWeight(benchmarkIndex, asOfDate, security.getId)
    
    // Position valuation history
    // TODO optimize this
    val valHist = positionValuationHistOpt.getOrElse(null)
    if (valHist != null) {
      asset.marketPrice = valHist.getMarketPrice
      asset.marketValue = valHist.getValueAmount
    } else {
      logger.warn("Position valuation history for position #{} is null. " +
        "Please make sure valuation has been run.", position.getId)
    }

    // Price change
    val md = marketDataService.getMarketData(position.getSecurityId(), previousDate)
    val previousPrice = if (md == null) BigDecimalConstants.ZERO else md.getPrice
    
    if (previousPrice != 0) {
      asset.priceChange = asset.marketPrice / previousPrice - 1
    } else {
      asset.priceChange = 0
    }

    // Daily PnL
    // TODO fix Daily PnL calculatio
    val prevValHist = previousPositionValuationHistOpt match {
      case Some(hist) => hist
      case None =>
        logger.warn("Position previous valuation history for position #{} is null. " +
          "Please make sure valuation has been run.", position.getId)
        null
    }
    if (prevValHist != null && valHist != null) {
      asset.dailyPnL = valHist.getValueAmount -  prevValHist.getValueAmount
    }


    // holding Quantity
    val positionHist = positionHistOpt.getOrElse(null)
    if (positionHist == null) {
      logger.warn("Security position history for position #{} is null. ", position.getId)
    } else {
      asset.holdingQuantity = positionHist.getQuantity.toLong
    }

    // holding Value
    val carryingValueHist = carryingValueHistOpt.getOrElse(null)
    if (carryingValueHist == null) {
      logger.warn("Carrying value hist history for position #{} is null on {}", position.getId, asOfDate)
    } else {
      asset.holdingValue = carryingValueHist.getValueAmount
    }

    // holding Value Price
    if (positionHist != null && carryingValueHist != null && positionHist.getQuantity != 0) {
      asset.holdingValuePrice = carryingValueHist.getValueAmount / positionHist.getQuantity
    }

    // float PnL
    asset.floatPnL = asset.marketValue - asset.holdingValue

    asset
  }
}