package com.datayes.invest.pms.service.marketindex;

import java.util.List;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

public interface MarketIndexService {

    List<MarketIndexInfo> getAvailableIndexes();
    
    BigDecimal getIndexWeight(String indexId, LocalDate asOfDate, Long securityId);

    MarketIndex getMarketIndex(String indexId, LocalDate asOfDate);
}
