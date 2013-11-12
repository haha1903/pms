package com.datayes.invest.pms.service;

import com.datayes.invest.pms.service.calendar.CalendarService;
import com.datayes.invest.pms.service.calendar.impl.CalendarServiceImpl;
import com.datayes.invest.pms.service.fee.FeeService;
import com.datayes.invest.pms.service.fee.impl.FeeServiceImpl;
import com.datayes.invest.pms.service.industry.IndustryService;
import com.datayes.invest.pms.service.industry.impl.IndustryServiceImpl;
import com.datayes.invest.pms.service.marketdata.MarketDataService;
import com.datayes.invest.pms.service.marketdata.impl.MarketDataServiceImpl;
import com.datayes.invest.pms.service.marketindex.MarketIndexService;
import com.datayes.invest.pms.service.marketindex.impl.MarketIndexServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CalendarService.class).to(CalendarServiceImpl.class).in(Scopes.SINGLETON);
        bind(FeeService.class).to(FeeServiceImpl.class).in(Scopes.SINGLETON);
        bind(IndustryService.class).to(IndustryServiceImpl.class).in(Scopes.SINGLETON);
        bind(MarketDataService.class).to(MarketDataServiceImpl.class).in(Scopes.SINGLETON);
        bind(MarketIndexService.class).to(MarketIndexServiceImpl.class).in(Scopes.SINGLETON);
    }
}
