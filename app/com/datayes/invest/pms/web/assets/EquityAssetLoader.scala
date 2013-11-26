package com.datayes.invest.pms.web.assets

import scala.math.BigDecimal.int2bigDecimal

import org.joda.time.LocalDate

import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.web.assets.enums.AssetClassType


class EquityAssetLoader(position: SecurityPosition, asOfDate: LocalDate, tranObj: TransferObject)
    extends CommonAssetLoader(position, asOfDate, tranObj) with Logging {
  
  
  def create(): Option[models.AssetCommon] = {
    val security = tranObj.securityDao.findById(position.getSecurityId())
    if (security == null) {
      logger.warn("Failed to load security by id {}", position.getSecurityId())
      None
    } else {
      val secId = position.getSecurityId()
      val code = security.getTickerSymbol()
      val name = {
        val abbr = security.getNameAbbr
        if (abbr == null || abbr.trim.isEmpty)
          security.getName
        else
          abbr
      }
      
      val asset = new models.AssetEquity(secId, code, name )
      Some(asset)
    }
  }
  
  def loadSpecific(asset: models.AssetCommon): Unit = {
    
    val equityAsset = asset.asInstanceOf[models.AssetEquity]
    
    // price change
    val previousDate = asOfDate.minusDays(1)
    val previousMd = tranObj.marketDataService.getMarketData(position.getSecurityId(), previousDate)
    val previousPrice = if (previousMd == null) null else previousMd.getPrice()
    if (previousPrice != null && asset.marketPrice != null) {
      equityAsset.priceChange = asset.marketPrice / previousPrice - 1
    }
    
    // daily PnL
    if (tranObj.positionValuationHist != null && tranObj.prevPositionValuationHist != null) {
      equityAsset.dailyPnL = tranObj.positionValuationHist.getValueAmount() - tranObj.prevPositionValuationHist.getValueAmount()
    }
    
    // benchmarkIndexWeight
    val benchmarkIndex = tranObj.benchmarkIndexOpt.getOrElse(DefaultValues.BENCHMARK_MARKET_INDEX)
    val security = tranObj.securityDao.findById(position.getSecurityId())
    equityAsset.benchmarkIndexWeight = tranObj.marketIndexService.getIndexWeight(benchmarkIndex, asOfDate, security.getId())
    
    // trade PnL and holding PnL
    if (tranObj.positionYield != null) {
      val positionYield = tranObj.positionYield;
      equityAsset.tradePnL = positionYield.getTradeEarnCamt()
      equityAsset.holdingPnL = positionYield.getEarnLossCamt() - positionYield.getTradeEarnCamt()
    }
    
    // other fields
    equityAsset.assetClass = AssetClassType.EQUITY
    equityAsset.exchange = security.getExchangeCode()
    equityAsset.industry = tranObj.industryService.getIndustryBySecurityId(position.getSecurityId())
  }
}