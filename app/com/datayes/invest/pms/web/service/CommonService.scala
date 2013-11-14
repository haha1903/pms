package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions.asScalaBuffer

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.industry.IndustryService

import javax.inject.Inject

class CommonService extends Logging {
  
  @Inject
  private var industryService: IndustryService = null

  def getIndustries(): List[String] = {
    industryService.getIndustries().toList
  }
}