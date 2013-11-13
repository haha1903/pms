package com.datayes.invest.pms.service.marketindex.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.math.BigDecimal;

import com.datayes.invest.pms.dao.security.MarketIndexDao;
import com.datayes.invest.pms.service.calendar.CalendarService;
import com.datayes.invest.pms.service.marketindex.Index;
import com.datayes.invest.pms.service.marketindex.MarketIndexService;
import com.datayes.invest.pms.util.BigDecimalConstants;

@Singleton
public class MarketIndexServiceImpl implements MarketIndexService {
	
	private static final List<Index> indexList = Arrays.asList(
	    new Index("HSSLL", "沪深300"),
	    new Index("SZWL", "上证50"),
	    new Index("SZYBL", "上证180"),
	    new Index("ZZWLL", "中证500")
	);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MarketIndexServiceImpl.class);
	
	@Inject
	private CalendarService calendarService;
	
	@Inject
	private MarketIndexDao marketIndexDao;
	
	private ConcurrentMap<CacheKey, MarketIndex> indexCache = new ConcurrentHashMap<>();

	@Override
	public List<Index> getIndexes() {
		return indexList;
	}

	@Override
	public BigDecimal getIndexWeight(String indexId, LocalDate asOfDate, Long securityId) {
		if (! checkIndex(indexId) ) {
		    LOGGER.warn("{} is not a valid market index");
		    return BigDecimalConstants.ZERO();
		}
		
		// Need to use data of previous trade day because today's data might not be ready yet
		LocalDate tradeDate = calendarService.previousTradeDay(asOfDate);
		
		CacheKey key = new CacheKey(indexId, tradeDate);
		MarketIndex marketIndex = indexCache.get(key);
		if (marketIndex == null) {
			MarketIndex newMarketIndex = loadMarketIndex(indexId, tradeDate);
			if (newMarketIndex != null) {
				marketIndex = indexCache.putIfAbsent(key, newMarketIndex);
				if (marketIndex == null) {
					marketIndex = newMarketIndex;
				}
			}
		}
		
		if (marketIndex == null || marketIndex.getComponents() == null || marketIndex.getComponents().isEmpty()) {
			return BigDecimalConstants.ZERO();
		}
		
		MarketIndexComponent comp = marketIndex.getComponents().get(securityId);
		if (comp == null || comp.getWeight() == null) {
			return BigDecimalConstants.ZERO();
		}
		
		return comp.getWeight();
	}

	private MarketIndex loadMarketIndex(String indexId, LocalDate asOfDate) {
		List<com.datayes.invest.pms.entity.security.MarketIndex> list = marketIndexDao.findByMarketIndexEndDate(indexId, asOfDate);
		Map<Long, MarketIndexComponent> components = new HashMap<>();
		if (list == null || list.isEmpty()) {
			return new MarketIndex(indexId, components);
		}
		
		for (com.datayes.invest.pms.entity.security.MarketIndex mi : list) {
		    // Divide weightedRatio by 100 since it's in percent form 
		    BigDecimal weight = mi.getWeightedRatio().$div(BigDecimalConstants.HUNDRED());
			MarketIndexComponent comp = new MarketIndexComponent(mi.getSecurityId(), weight);
			components.put(mi.getSecurityId(), comp);
		}
		MarketIndex marketIndex = new MarketIndex(indexId, components);
		return marketIndex;
	}
	
	private boolean checkIndex(String indexId) {
	    for (Index ind : indexList) {
	        if (ind.getId().equals(indexId)) {
	            return true;
	        }
	    }
	    return false;
	}
}