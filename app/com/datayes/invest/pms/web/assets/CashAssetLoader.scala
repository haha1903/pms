package com.datayes.invest.pms.web.assets

import scala.math.BigDecimal.long2bigDecimal

import org.joda.time.LocalDate

import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.account.CashPosition
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.web.assets.enums.AssetClassType

/*
 * TODO CashAssetLoader fill in fake or default values, this is only used for convenience...
 */
class CashAssetLoader(position: CashPosition, asOfDate: LocalDate, tranObj: TransferObject)
    extends AssetLoader with Logging {

  def load(): Option[models.AssetCommon] = {
    
    val ledger = LedgerType.fromDbValue(position.getLedgerId())
    if (ledger == LedgerType.SHARE) {
      // Share is not asset
      return None
    }
    
    val asset = new models.AssetCash(ledger.toString())
    
    asset.accountId = position.getAccountId()
    asset.assetClass = AssetClassType.CASH
    asset.exchange = null
    asset.industry = null
    
    // holding price
    asset.holdingValuePrice = BigDecimalConstants.ONE
    
    // market price
    asset.marketPrice = BigDecimalConstants.ONE
    
    // floatPnL
    asset.floatPnL = BigDecimalConstants.ZERO
    
    // holding quantity
    if (tranObj.positionHist != null) {
      val quantity: Long = tranObj.positionHist.getQuantity().toLong
      asset.holdingQuantity = quantity
      asset.marketValue = quantity * asset.marketPrice
    }
    
    // holdingValue
    asset.holdingValue = asset.holdingValuePrice * asset.holdingQuantity
    
    return Some(asset)
  }
}