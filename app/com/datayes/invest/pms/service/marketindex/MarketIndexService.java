package com.datayes.invest.pms.service.marketindex;

import java.util.List;

import org.joda.time.LocalDate;

import scala.math.BigDecimal;

public interface MarketIndexService {

    List<Index> getIndexes();
    
    BigDecimal getIndexWeight(String indexId, LocalDate asOfDate, Long securityId);
}
