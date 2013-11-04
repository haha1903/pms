package com.datayes.invest.pms.service.calendar;

import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.dao.security.ExchangeCalendarMockDaoImpl;
import com.google.inject.*;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

class CalendarServiceTestModuleJava extends AbstractModule {

    @Override
    protected void configure() {
        bind(ExchangeCalendarDao.class).to(ExchangeCalendarMockDaoImpl.class).in(Scopes.SINGLETON);
    }
}


public class CalendarServiceTest{

    Injector injector = Guice.createInjector(new CalendarServiceTestModuleJava());

    private CalendarService calendarService = injector.getInstance(CalendarService.class);

    private LocalDate holiday = new LocalDate(2013, 9, 19);
    private LocalDate tradeDay = new LocalDate(2013, 9, 18);
    private LocalDate notMockedDay = new LocalDate();

    private String shExchangeCode = "XSHG";
    private String szExchangeCode = "XSHE";

    @Test(expected = RuntimeException.class)
    public void testIsTradeDay() {
        // test mocked data
        Assert.assertTrue(!calendarService.isTradeDay(holiday));
        Assert.assertTrue(calendarService.isTradeDay(tradeDay));
        Assert.assertTrue(calendarService.isTradeDay(holiday, shExchangeCode));
        Assert.assertTrue(!calendarService.isTradeDay(tradeDay, shExchangeCode));

        // test not mocked data
        Assert.assertTrue(!calendarService.isTradeDay(notMockedDay));
        Assert.assertTrue(!calendarService.isTradeDay(holiday, szExchangeCode));
        Assert.assertTrue(!calendarService.isTradeDay(tradeDay, szExchangeCode));
        Assert.assertTrue(!calendarService.isTradeDay(notMockedDay, shExchangeCode));
    }

    @Test(expected = RuntimeException.class)
    public void testPreviousTradeDay() {
        // test mocked data
        Assert.assertEquals(tradeDay, calendarService.previousTradeDay(holiday));
    }

    @Test(expected = RuntimeException.class)
    public void sameOrPreviousTradeDay() {
        // test mocked data
        Assert.assertEquals(tradeDay, calendarService.sameOrPreviousTradeDay(holiday));
        Assert.assertEquals(tradeDay, calendarService.sameOrPreviousTradeDay(tradeDay));
    }

    @Test(expected = RuntimeException.class)
    public void nextCalendarDay() {
        // test mocked data
        Assert.assertEquals(holiday, calendarService.nextCalendarDay(tradeDay));
    }

    @Before
    public void setup() {

    }
}
