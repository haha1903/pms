package com.datayes.invest.pms.web.assets

import scala.math.BigDecimal.int2bigDecimal

import org.joda.time.LocalDate

import com.datayes.invest.pms.dbtype.{AssetClass, LedgerType}
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.util.FutureMultiplierHelper

import com.datayes.invest.pms.web.assets.enums.LongShort

class IndexFutureAssetLoader(position: SecurityPosition, asOfDate: LocalDate, tranObj: TransferObject)
    extends CommonAssetLoader(position, asOfDate, tranObj) with Logging {

  protected def create(): Option[models.AssetCommon] = {
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
      
      val asset = new models.AssetIndexFuture(secId, code, name )
      Some(asset)
    }
  }
  
  def loadSpecific(asset: models.AssetCommon): Unit = {
    
    val futureAsset = asset.asInstanceOf[models.AssetIndexFuture]
    
    // long short
    val ledger: LedgerType = LedgerType.fromDbValue(position.getLedgerId())
    if (ledger == LedgerType.FUTURE_LONG) {
      futureAsset.longShort = LongShort.LONG
    } else if (ledger == LedgerType.FUTURE_SHORT) {
      futureAsset.longShort = LongShort.SHORT
    } else {
      logger.warn("Position {} should be an index future position but has incompatible ledger {}", position.getId(), ledger)
    }
    
    // margin occupied
    val security = tranObj.securityDao.findById(position.getSecurityId())
    val future: Future = if (security.isInstanceOf[Future]) {
      security.asInstanceOf[Future]
    } else {
      logger.warn("Security {} should be an index future but has incompatible type {}", position.getSecurityId(), security)
      null
    }
    if (futureAsset.longShort != null && future != null && tranObj.positionHist != null) {
      val multiplier = FutureMultiplierHelper.getRatio(future.getContractMultiplier())
      futureAsset.marginOccupied = tranObj.positionHist.getQuantity() * multiplier
    }
    
    // other fields
    futureAsset.assetClass = AssetClass.INDEX_FUTURE
    futureAsset.exchange = security.getExchangeCode()
    futureAsset.industry = DefaultValues.UNKNOWN_INDUSTRY
  }
}