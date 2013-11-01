package com.datayes.invest.pms.service.marketdata.impl;

import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.service.marketdata.MarketData;
import com.datayes.invest.pms.service.marketdata.MarketDataService;

public class MarketDataServiceImpl implements MarketDataService {

    @Override
    public Map<Long, MarketData> getEquityMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Long, MarketData> getFutureMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        // TODO Auto-generated method stub
        return null;
    }

}
