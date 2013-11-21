package com.datayes.invest.pms.web.assets

import org.joda.time.LocalDate

import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.BigDecimalConstants

abstract class CommonAssetLoader(position: SecurityPosition, asOfDate: LocalDate, tranObj: TransferObject) extends AssetLoader with Logging {
  
  protected def create(): Option[models.AssetCommon]
  
  protected def loadSpecific(asset: models.AssetCommon)
  
  def load(): Option[models.AssetCommon] = {
    val assetOpt: Option[models.AssetCommon] = create()
    assetOpt match {
      case Some(asset) =>
        loadCommon(asset)
        loadSpecific(asset)
        Some(asset)
      case None =>
        None
    }
  }

  protected def loadCommon(asset: models.AssetCommon): Unit = {
    
    asset.accountId = position.getAccountId()
  
    // holding quantity
    if (tranObj.positionHist != null) {
      asset.holdingQuantity = tranObj.positionHist.getQuantity().toLong
    } else {
      logger.warn("Position history not found for position #{}. ", position.getId)
    }
  
    // holding value
    if (tranObj.carryingValueHist != null) {
      asset.holdingValue = tranObj.carryingValueHist.getValueAmount()
    }
    
    // holding value price
    if (tranObj.positionHist != null && tranObj.carryingValueHist != null && tranObj.positionHist.getQuantity() != BigDecimalConstants.ZERO) {
      asset.holdingValuePrice = tranObj.carryingValueHist.getValueAmount() / tranObj.positionHist.getQuantity()
    }
    
    // market price, market value
    if (tranObj.positionValuationHist != null) {
      asset.marketPrice = tranObj.positionValuationHist.getMarketPrice()
      asset.marketValue = tranObj.positionValuationHist.getValueAmount()
    }
    
    // floatPnL
    if (asset.marketValue != null && asset.holdingValue != null) {
      asset.floatPnL = asset.marketValue - asset.holdingValue
    }
    
  }
}