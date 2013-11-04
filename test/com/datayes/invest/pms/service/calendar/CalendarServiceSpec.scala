package com.datayes.invest.pms.service.calendar

import org.specs2.mutable._
import com.google.inject.{Scopes, Guice, Injector, AbstractModule}
import com.datayes.invest.pms.dao.security.{ExchangeCalendarMockDaoImpl, ExchangeCalendarDao}
import org.joda.time.LocalDate
import com.datayes.invest.pms.service.calendar.impl.CalendarServiceImpl

class CalendarServiceTestModule extends AbstractModule {

  protected def configure {
    val exchangeCalendarDao = new ExchangeCalendarMockDaoImpl
    exchangeCalendarDao.addMockData(new LocalDate(2013, 9, 18), false)
    (19 to 22).foreach(i => exchangeCalendarDao.addMockData(new LocalDate(2013, 9, i.toInt), true))
    (23 to 24).foreach(i => exchangeCalendarDao.addMockData(new LocalDate(2013, 9, i.toInt), false))

    bind(classOf[ExchangeCalendarDao]).toInstance(exchangeCalendarDao)
    bind(classOf[CalendarService]).to(classOf[CalendarServiceImpl]).in(Scopes.SINGLETON)
  }
}

class CalendarServiceSpec extends Specification {

  private val injector: Injector = Guice.createInjector(new CalendarServiceTestModule)
  private val calendarService: CalendarService = injector.getInstance(classOf[CalendarService])
  private val holiday: LocalDate = new LocalDate(2013, 9, 19)
  private val tradeDay: LocalDate = new LocalDate(2013, 9, 18)
  private val nextTradeDay: LocalDate = new LocalDate(2013, 9, 23)
  private val notMockedDay: LocalDate = new LocalDate
  private val shExchangeCode: String = "XSHG"
  private val szExchangeCode: String = "XSHE"


  "Is Trade Day" should {
    "2013-09-19 is not a trade day" in {
      calendarService.isTradeDay(holiday) must_== false
    }

    "2013-09-18 is a trade day" in {
      calendarService.isTradeDay(tradeDay) must_== true
    }

    "2013-09-19 is not a trade day for XSHG" in {
      calendarService.isTradeDay(holiday, shExchangeCode) must_== false
    }

    "2013-09-18 is a trade day for XSHG" in {
      calendarService.isTradeDay(tradeDay, shExchangeCode) must_== true
    }
  }

  "Previous Trade Day" should {
    "The Previous Trade Day of 2013-09-19 is 2013-09-18" in {
      calendarService.previousTradeDay(holiday) must_== tradeDay

      calendarService.previousTradeDay(holiday, shExchangeCode, 1) must_== tradeDay
    }
  }

  "Next Trade Day" should {
    "The Next Trade Day of 2013-09-19 is 2013-09-23" in {
      calendarService.nextTradeDay(holiday) must_== nextTradeDay

      calendarService.nextTradeDay(holiday, shExchangeCode, 1) must_== nextTradeDay
    }
  }

  "Same or Previous Trade Day" should {
    "The Same or Previous Trade Day of 2013-09-19 is 2013-09-18" in {
      calendarService.sameOrPreviousTradeDay(holiday) must_== tradeDay
    }

    "The Same or Previous Trade Day of 2013-09-18 is 2013-09-18" in {
      calendarService.sameOrPreviousTradeDay(tradeDay) must_== tradeDay
    }
  }
}
