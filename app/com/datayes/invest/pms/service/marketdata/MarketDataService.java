package com.datayes.invest.pms.service.marketdata;

import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

public interface MarketDataService {

    Map<Long, MarketData> getMarketData(Set<Long> securityIds, LocalDate asOfDate);
    
    MarketData getMarketData(Long securityIds, LocalDate asOfDate);
}
