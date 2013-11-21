package com.datayes.invest.pms.web.assets

import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.entity.account.PositionValuationHist
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.service.industry.IndustryService
import com.datayes.invest.pms.service.marketindex.MarketIndexService
import com.datayes.invest.pms.dao.security.SecurityDao

class TransferObject (
  val positionHist: PositionHist,
  val carryingValueHist: CarryingValueHist,
  val positionValuationHist: PositionValuationHist,
  val prevPositionValuationHist: PositionValuationHist,
  val benchmarkIndexOpt: Option[String],
  val marketDataService: MarketDataService,
  val industryService: IndustryService,
  val marketIndexService: MarketIndexService,
  val securityDao: SecurityDao
)