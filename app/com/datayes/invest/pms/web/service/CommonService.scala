package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions.asScalaBuffer

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.industry.IndustryService
import com.datayes.invest.pms.service.marketindex.{Index, MarketIndexService}
import javax.inject.Inject

class CommonService extends Logging {
  
  @Inject
  private var industryService: IndustryService = null

  @Inject
  private var marketIndexService: MarketIndexService = null

  def getIndustries(): List[String] = {
    industryService.getIndustries().toList
  }

  def getMarketIndexes(): List[Index] = {
    marketIndexService.getIndexes.toList
  }
}