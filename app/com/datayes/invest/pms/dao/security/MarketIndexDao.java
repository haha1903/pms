package com.datayes.invest.pms.dao.security;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.MarketIndex;


public interface MarketIndexDao {
	
	List<MarketIndex> findByMarketIndexEndDate(String marketIndex, LocalDate endDate);
}
