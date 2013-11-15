package com.datayes.invest.pms.service.marketdata;


import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.dao.account.MarketDataMockDaoImpl;
import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.dao.security.ExchangeCalendarMockDaoImpl;
import com.datayes.invest.pms.dao.security.PriceVolumeDao;
import com.datayes.invest.pms.dao.security.PriceVolumeMockDaoImpl;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.entity.security.PriceVolume;
import com.datayes.invest.pms.service.calendar.CalendarService;
import com.datayes.invest.pms.service.calendar.impl.CalendarServiceImpl;
import com.datayes.invest.pms.service.marketdata.impl.MarketDataServiceImpl;
import com.datayes.invest.pms.service.marketdata.impl.Source;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import scala.math.BigDecimal;

import java.sql.Timestamp;

class MarketDataServiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        PriceVolume priceVolume1 = new PriceVolume(
                2l,
                new LocalDate("2013-09-18"),
                5.2d,
                4.9d);

        PriceVolume priceVolume2 = new PriceVolume(
                4l,
                new LocalDate("2013-09-18"),
                10.1d,
                10.7d);

        PriceVolumeMockDaoImpl priceVolumeMockDao = new PriceVolumeMockDaoImpl();
        priceVolumeMockDao.addMockData(priceVolume1);
        priceVolumeMockDao.addMockData(priceVolume2);


        ExchangeCalendarMockDaoImpl exchangeCalendarDao = new ExchangeCalendarMockDaoImpl();
        exchangeCalendarDao.addMockData(new LocalDate(2013, 9, 18), false);
        for( int i = 19; i <= 22; i++ ) {
            exchangeCalendarDao.addMockData(new LocalDate(2013, 9, i), true);
        }
        for( int i = 23; i <= 24; i++ ) {
            exchangeCalendarDao.addMockData(new LocalDate(2013, 9, i), false);
        }
        exchangeCalendarDao.addMockData(LocalDate.now(), false);

        bind(MarketDataService.class).to(MarketDataServiceImpl.class).in(Scopes.SINGLETON);
        bind(MarketDataDao.class).to(MarketDataMockDaoImpl.class).in(Scopes.SINGLETON);
        bind(PriceVolumeDao.class).toInstance(priceVolumeMockDao);
        bind(CalendarService.class).to(CalendarServiceImpl.class).in(Scopes.SINGLETON);
        bind(ExchangeCalendarDao.class).toInstance(exchangeCalendarDao);
    }
}

public class MarketDataServiceTest {
    Injector injector = Guice.createInjector(new MarketDataServiceTestModule());

    private MarketDataService marketDataService = injector.getInstance(MarketDataService.class);

    private LocalDate holiday = new LocalDate(2013, 9, 19);
    private LocalDate tradeDay = new LocalDate(2013, 9, 18);
    private LocalDate now = LocalDate.now();

    private Long securityId1 = 2l;
    private Long securityId2 = 4l;

    private MarketData marketDataFor2 = new MarketData(securityId1, now, new Timestamp(tradeDay.toDate().getTime()), new BigDecimal(new java.math.BigDecimal(5.2d)), new BigDecimal(new java.math.BigDecimal(4.9d)), null, Source.MQ.toString());
    private MarketData marketDataFor4 = new MarketData(securityId2, now, new Timestamp(tradeDay.toDate().getTime()), new BigDecimal(new java.math.BigDecimal(10.1d)), new BigDecimal(new java.math.BigDecimal(10.7d)), null, Source.MQ.toString());

    @Test
    public void testGetMarketData() {
        Assert.assertEquals(marketDataFor2, marketDataService.getMarketData(securityId1, holiday));
        Assert.assertEquals(marketDataFor2, marketDataService.getMarketData(securityId1, tradeDay));

        Assert.assertEquals(marketDataFor4, marketDataService.getMarketData(securityId2, holiday));
        Assert.assertEquals(marketDataFor4, marketDataService.getMarketData(securityId2, tradeDay));
    }

}
