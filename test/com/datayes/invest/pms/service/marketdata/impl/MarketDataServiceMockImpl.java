package com.datayes.invest.pms.service.marketdata.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.service.marketdata.MarketData;
import com.datayes.invest.pms.service.marketdata.MarketDataService;

public class MarketDataServiceMockImpl implements MarketDataService {
    
    private Map<Long, MarketData> equityMarketDataMap = Collections.emptyMap();
    
    private Map<Long, MarketData> futureMarketDataMap = Collections.emptyMap();

    @Override
    public Map<Long, MarketData> getEquityMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        return equityMarketDataMap;
    }

    @Override
    public Map<Long, MarketData> getFutureMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        return futureMarketDataMap;
    }
    
    public void setEquityMarketDataMap(Map<Long, MarketData> map) {
        Map<Long, MarketData> newMap = new HashMap<>(map);
        this.equityMarketDataMap = Collections.unmodifiableMap(newMap);
    }
    
    public void setFutureMarketDataMap(Map<Long, MarketData> map) {
        Map<Long, MarketData> newMap = new HashMap<>(map);
        this.futureMarketDataMap = Collections.unmodifiableMap(newMap);
    }

    @Override
    public MarketData getEquityMarketData(Long securityId, LocalDate asOfDate) {
        MarketData md = equityMarketDataMap.get(securityId);
        return md;
    }

    @Override
    public MarketData getFutureMarketData(Long securityId, LocalDate asOfDate) {
        MarketData md = futureMarketDataMap.get(securityId);
        return md;
    }
}
