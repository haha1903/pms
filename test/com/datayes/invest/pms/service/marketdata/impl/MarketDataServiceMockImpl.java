package com.datayes.invest.pms.service.marketdata.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.service.marketdata.MarketDataService;

public class MarketDataServiceMockImpl implements MarketDataService {
    
    private Map<Long, MarketData> marketDataMap = Collections.emptyMap();
    
    @Override
    public Map<Long, MarketData> getMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        return marketDataMap;
    }

    @Override
    public MarketData getMarketData(Long securityId, LocalDate asOfDate) {
        MarketData md = marketDataMap.get(securityId);
        return md;
    }

    public void setMarketDataMap(Map<Long, MarketData> map) {
        Map<Long, MarketData> newMap = new HashMap<>(map);
        this.marketDataMap = Collections.unmodifiableMap(newMap);
    }

}
