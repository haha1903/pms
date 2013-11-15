package com.datayes.invest.pms.service.marketdata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datayes.invest.pms.entity.account.MarketData;

import org.joda.time.LocalDate;

public interface MarketDataService {

    Map<Long, MarketData> getMarketData(Set<Long> securityIds, LocalDate asOfDate);
    
    MarketData getMarketData(Long securityId, LocalDate asOfDate);
    
    List<MarketData> getMarketDataBetweenDates(Long securityId, LocalDate startDate, LocalDate endDate);
}
