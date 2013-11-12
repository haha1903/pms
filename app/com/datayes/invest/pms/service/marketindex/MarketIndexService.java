package com.datayes.invest.pms.service.marketindex;

import java.util.List;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

public interface MarketIndexService {

    List<String> getIndexes();
    
    BigDecimal getIndexWeight(String indexName, LocalDate asOfDate, Long securityId);
}
