package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.assets.models.AssetCommon
import com.datayes.invest.pms.web.model.models.FilterParam
import com.datayes.invest.pms.web.model.models.RangeFilterType

object FilterHelper extends Logging {

  def filterAssets(assets: Seq[AssetCommon], param: FilterParam): Seq[AssetCommon] = {
    logger.debug("Filtering assets with filter: {}", param)
    assets.filter { a =>
      param.assetClass.map(c => a.assetClass == c).getOrElse(true) &&
      param.exchange.map(e => a.exchange == e).getOrElse(true) &&
      param.industry.map(i => a.industry == i).getOrElse(true) &&
      param.rangeFilterType.map { t =>
        filterByRange(a, t, param.rangeFilterMin, param.rangeFilterMax)
      }.getOrElse(true)
    }
  }

  private def filterByRange(asset: AssetCommon, filterType: RangeFilterType.Type, min: Option[BigDecimal],
                            max: Option[BigDecimal]): Boolean = {
    val value = filterType match {
      case RangeFilterType.marketValue => asset.marketPrice
      case RangeFilterType.holdingValue => asset.holdingValue
      case RangeFilterType.floatPnL => asset.floatPnL
    }
    min.map(_ < value).getOrElse(true) && max.map(value < _).getOrElse(true)
  }

}
