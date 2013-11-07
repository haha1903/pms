package com.datayes.invest.pms.service.marketdata.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.math.BigDecimal;

import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.dao.security.FuturePriceVolumeDao;
import com.datayes.invest.pms.dao.security.PriceVolumeDao;
import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.entity.security.Equity;
import com.datayes.invest.pms.entity.security.Future;
import com.datayes.invest.pms.entity.security.FuturePriceVolume;
import com.datayes.invest.pms.entity.security.PriceVolume;
import com.datayes.invest.pms.entity.security.Security;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.service.calendar.CalendarService;
import com.datayes.invest.pms.service.marketdata.MarketDataService;
import com.datayes.invest.pms.service.marketdata.impl.data.Converter;
import com.datayes.invest.pms.util.DefaultValues;

public class MarketDataServiceImpl implements MarketDataService {

    private static Config config = Config.INSTANCE;
    
    private static String configOpenTime = config.getString("market.open.time");
    
    private static String configCloseTime = config.getString("market.close.time");

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataServiceImpl.class);
    

    @Inject
    private CalendarService calendarService;
    
    @Inject
    private FuturePriceVolumeDao futurePriceVolumeDao;
    
    @Inject
    private MarketDataDao marketDataDao;

    @Inject
    private PriceVolumeDao priceVolumeDao;
    
    @Inject
    private SecurityDao securityDao;


    private MarketDataCache marketDataCache = new MarketDataCache();
    
    private boolean isInitialized = false;
    

    private void preloadCache() {
        LOGGER.info("Preloading market data cache");
        Transaction tx = Persist.getTransaction();
        try {
            LocalDate lastTradeDate = calendarService.previousTradeDay(LocalDate.now());
            Map<Long, MarketData> buffer = new HashMap<Long, MarketData>();
            
            // First load from securitymaster's PRICE_VOLUME and FUT_PRICEVOLUME
            List<PriceVolume> priceVolumes = priceVolumeDao.findByTradeDate(lastTradeDate);
            for (PriceVolume p : priceVolumes) {
                MarketData md = Converter.toMarketData(p);
                buffer.put(p.getSecurityId(), md);
            }
            
            List<FuturePriceVolume> futurePriceVolumes = futurePriceVolumeDao.findByTradeDate(lastTradeDate);
            for (FuturePriceVolume p : futurePriceVolumes) {
                MarketData md = Converter.toMarketData(p);
                buffer.put(p.getSecurityId(), md);
            }
            
            // Then load from accountmaster's MARKET_DATA
            List<MarketData>  snapshotList = marketDataDao.findAll();
            for(MarketData md : snapshotList) {
                buffer.put(md.getSecurityId(), md);
            }
            
            // Add to cache
            for (MarketData md : buffer.values()) {
                marketDataCache.update(md);
            }
            
            tx.commit();
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading realtime market data from db", e);
            tx.rollback();
        }
    }

    private void initialize() {
        // load real time market data from MarketData table in Db
        preloadCache();

        // Create new thread for receiving stock data
        EquityCacheUpdateTask equityCacheUpdateTask = new EquityCacheUpdateTask(marketDataCache);
        Thread equityThread = new Thread(equityCacheUpdateTask);
        equityThread.start();

        // Create new thread for receiving future data
        FutureCacheUpdateTask futureCacheUpdateTask = new FutureCacheUpdateTask(marketDataCache);
        Thread futureThread = new Thread(futureCacheUpdateTask);
        futureThread.start();

        // Create new timer for scheduling to update Market Data
        Timer timer = new Timer();
        MarketDataDbScheduler scheduler = new MarketDataDbScheduler(marketDataCache, marketDataDao);
        timer.schedule(scheduler, 0, DefaultValues.MARKETDATA_SCHEDULER_INTERVAL());
    }

    private boolean isTradeTime() {
        LocalTime openTime = LocalTime.parse(configOpenTime);
        LocalTime closeTime = LocalTime.parse(configCloseTime);
        LocalTime now = LocalTime.now();
        // If now is in trade time
        if( now.isAfter(openTime) && now.isBefore(closeTime) ) {
            return true;
        }
        else {
            return false;
        }
    }

    private Map<Long, MarketData> getHistoryMarketDataFromDb(Set<Long> securityIds, LocalDate asOfDate) {
        if (securityIds == null || securityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        // Split securityIds by type
        Set<Long> equityIds = new HashSet<>();
        Set<Long> futureIds = new HashSet<>();
        for (Long sid : securityIds) {
            Security security = securityDao.findById(sid);
            if (security instanceof Equity) {
                equityIds.add(sid);
            } else if (security instanceof Future) {
                futureIds.add(sid);
            }
        }

        // Get last trade day
        LocalDate tradeDay = calendarService.sameOrPreviousTradeDay(asOfDate);
        
        Map<Long, MarketData> equityDataMap = loadEquityMarketDataFromDb(equityIds, tradeDay);
        Map<Long, MarketData> futureDataMap = loadFutureMarketDataFromDb(futureIds, tradeDay);
        
        Map<Long, MarketData> marketDataMap = new HashMap<>();
        marketDataMap.putAll(equityDataMap);
        marketDataMap.putAll(futureDataMap);
        
        // Compare security ids to determine not-found prices
        Set<Long> notFound = new HashSet<>(securityIds);
        notFound.removeAll(marketDataMap.keySet());
        if (notFound != null && ! notFound.isEmpty()) {
            LOGGER.warn("Unable to find price volume for the following securities: " + notFound);
        }
        
        return marketDataMap;
    }
    
    private Map<Long, MarketData> loadEquityMarketDataFromDb(Set<Long> securityIds, LocalDate tradeDate) {
        List<PriceVolume> priceVolumes = priceVolumeDao.findBySecurityIdListTradeDate(securityIds, tradeDate);
        if (priceVolumes == null || priceVolumes.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, MarketData> map = new HashMap<Long, MarketData>();
        for (PriceVolume pv : priceVolumes) {
            MarketData md = Converter.toMarketData(pv);
            map.put(pv.getSecurityId(), md);
        }
        return map;
    }
    
    private Map<Long, MarketData> loadFutureMarketDataFromDb(Set<Long> securityIds, LocalDate tradeDate) {
        List<FuturePriceVolume> priceVolumes = futurePriceVolumeDao.findBySecurityIdListTradeDate(securityIds, tradeDate);
        if (priceVolumes == null || priceVolumes.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, MarketData> map = new HashMap<Long, MarketData>();
        for (FuturePriceVolume pv : priceVolumes) {
            MarketData md = Converter.toMarketData(pv);
            map.put(pv.getSecurityId(), md);
        }
        return map;
    }

    @Override
    public Map<Long, MarketData> getMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        if( !isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    initialize();
                    isInitialized = true;
                }
            }
        }
        if (securityIds == null || securityIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, MarketData> marketdataMap = new HashMap<>();

        // It is today, get real time market data
        if( LocalDate.now().equals(asOfDate) ) {
            if( isTradeTime() ){
                // Loop on every security id
                for(Long securityId : securityIds) {
                    MarketData md = marketDataCache.findRealTimeMarketData(securityId);
                    // find marketdata in real time cache
                    if( md != null ) {
                        marketdataMap.put(securityId, md);
                    }
                    else {
                        LOGGER.error("Cannot find Real time Marketdata for Security Id: {} in cache, " +
                                "now trying to find in Database",
                                securityId);
                    }
                }
                return marketdataMap;
            }
            else {  // if it's not trade time, then find in Price Volume
                return getHistoryMarketDataFromDb(securityIds, asOfDate);
            }
        }
        else { // If it's not today, then find in Price Volume
            return getHistoryMarketDataFromDb(securityIds, asOfDate);
        }
    }

    @Override
    public MarketData getMarketData(Long securityId, LocalDate asOfDate) {
        Set<Long> securityIds = new HashSet<>();
        securityIds.add(securityId);

        Map<Long, MarketData> marketDataMap = getMarketData(securityIds, asOfDate);
        if( marketDataMap != null && !marketDataMap.isEmpty()) {
            return marketDataMap.get(securityId);
        }

        return null;
    }

    public Map<Long, MarketData> getMarketDataCache() {
        return marketDataCache.getRealTimeCache();
    }

    public Map<Long, LocalDateTime> getCacheMissingSecurityId() {
        return marketDataCache.getCacheMissingSecurityId();
    }
}
