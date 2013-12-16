package com.datayes.invest.pms.web.service

import scala.collection.JavaConversions.asScalaBuffer

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.service.calendar.CalendarService
import com.datayes.invest.pms.service.industry.IndustryService
import com.datayes.invest.pms.service.marketindex.{MarketIndexInfo, MarketIndexService}
import javax.inject.Inject
import org.joda.time.LocalDate

class CommonService extends Logging {
  
  @Inject
  private var industryService: IndustryService = null

  @Inject
  private var calendarService: CalendarService = null

  @Inject
  private var marketIndexService: MarketIndexService = null


  def getIndustries(): List[String] = {
    industryService.getIndustries().toList
  }

  def getMarketIndexes(): List[MarketIndexInfo] = {
    marketIndexService.getAvailableIndexes.toList
  }

  def previousTradeDay(date: LocalDate, exchange: String, numOfDays: Int): LocalDate = transaction {
    calendarService.previousTradeDay(date, exchange, numOfDays)
  }

  def nextTradeDay(date: LocalDate, exchange: String, numOfDays: Int): LocalDate = transaction {
    calendarService.nextTradeDay(date, exchange, numOfDays)
  }

  def isTradeDay(date: LocalDate, exchange: String): Boolean = transaction {
    calendarService.isTradeDay(date, exchange)
  }
}