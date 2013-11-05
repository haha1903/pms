package com.datayes.invest.pms.service.marketdata.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.math.BigDecimal;

import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.dao.account.MarketDataDao;
import com.datayes.invest.pms.dao.security.PriceVolumeDao;
import com.datayes.invest.pms.entity.account.MarketData;
import com.datayes.invest.pms.entity.security.PriceVolume;
import com.datayes.invest.pms.service.calendar.CalendarService;
import com.datayes.invest.pms.service.marketdata.MarketDataService;
import com.datayes.invest.pms.service.marketdata.impl.cache.MarketDataCache;
import com.datayes.invest.pms.service.marketdata.impl.task.EquityCacheUpdateTask;
import com.datayes.invest.pms.service.marketdata.impl.task.FutureCacheUpdateTask;
import com.datayes.invest.pms.service.marketdata.impl.task.MarketDataDbScheduler;
import com.datayes.invest.pms.util.DefaultValues;

public class MarketDataServiceImpl implements MarketDataService {

    private MarketDataCache marketDataCache = new MarketDataCache();
    private boolean initialized = false;

    @Inject
    private MarketDataDao marketDataDao = null;

    @Inject
    private PriceVolumeDao priceVolumeDao = null;

    @Inject
    private CalendarService calendarService = null;

    private static Config config = Config.INSTANCE;
    
//    private static String configOpenTime = config.getString("market.open.time");
//    private static String configCloseTime = config.getString("market.close.time");
    private static String configOpenTime = "09:30:00";
    private static String configCloseTime = "15:00:00";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataServiceImpl.class);

    private void loadRealTimeMarketDataFromDb() {
        List<MarketData>  snapshotList;
        try {
            snapshotList = marketDataDao.findAll();
            for(MarketData marketData : snapshotList) {
                marketDataCache.update(marketData);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading realtime market data from db", e);
        }
    }

    private void startRealTimeTask() {
        // load real time market data from MarketData table in Db
        loadRealTimeMarketDataFromDb();

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
        timer.schedule(
                new MarketDataDbScheduler(marketDataCache),
                DefaultValues.MARKETDATA_SCHEDULER_INTERVAL(),
                DefaultValues.MARKETDATA_SCHEDULER_INTERVAL());

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

    private PriceVolume findPriceVolume(Long securityId, List<PriceVolume> priceVolumes) {
        for(PriceVolume priceVolume : priceVolumes) {
            if(priceVolume.getSecurityId().equals(securityId)) {
                return priceVolume;
            }
        }
        return null;
    }

    private Map<Long, MarketData> getHistoryMarketDataFromDb(Set<Long> securityIds, LocalDate asOfDate) {
        Map<Long, MarketData> marketDataMap = new HashMap<>();

        // Get last trade day
        LocalDate tradeDay = calendarService.sameOrPreviousTradeDay(asOfDate);
        List<PriceVolume> priceVolumes = priceVolumeDao.findBySecurityIdListTradeDate(securityIds, tradeDay);

        if( null == priceVolumes || priceVolumes.isEmpty() ) {
            // Get security id list as a string
            String strSecurityIds = "";
            for(Long securityId : securityIds) {
                strSecurityIds += securityIds + ", ";
            }

            LOGGER.error("Cannot find Price Volume" +
                    "for securityIds: {} on {}",
                    strSecurityIds,
                    tradeDay);
            return null;
        }
        else {
            for(Long securityId : securityIds) {
                PriceVolume priceVolume = findPriceVolume(securityId, priceVolumes);

                // Check if any input security is found in PriceVolume
                if( null == priceVolume) {
                    LOGGER.warn("Cannot find Price Volume for securityId: {} on {}",
                            securityId,
                            tradeDay);
                }
                else {
                    marketDataMap.put(securityId,
                        new MarketData(
                            securityId,
                            new Timestamp(tradeDay.toDate().getTime()),
                            new BigDecimal(new java.math.BigDecimal(priceVolume.getPriceClose())),
                            new BigDecimal(new java.math.BigDecimal(priceVolume.getPricePreviousClose()))));
                }
            }
            return marketDataMap;
        }
    }

    @Override
    public Map<Long, MarketData> getMarketData(Set<Long> securityIds, LocalDate asOfDate) {
        if( !initialized) {
            synchronized (this) {
                if (!initialized) {
                    startRealTimeTask();
                    initialized = true;
                }
            }
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
