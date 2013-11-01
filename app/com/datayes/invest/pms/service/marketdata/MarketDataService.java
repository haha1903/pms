package com.datayes.invest.pms.service.marketdata;

import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

public interface MarketDataService {

    Map<Long, MarketData> getEquityMarketData(Set<Long> securityIds, LocalDate asOfDate);
    
    Map<Long, MarketData> getFutureMarketData(Set<Long> securityIds, LocalDate asOfDate);
}
