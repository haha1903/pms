package com.datayes.invest.pms.service.marketdata.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.math.BigDecimal;

import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.web.model.gson.BigDecimalDeserializer;
import com.datayes.invest.pms.web.model.gson.BigDecimalSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FutureCacheUpdateTask implements Runnable {

    private MarketDataCache marketDataCache = null;

    private static Config config = Config.INSTANCE;

    private static String CHANNEL = config.getString("redis.channel");
    private static String HOST = config.getString("redis.host");
    private static int PORT = config.getInt("redis.port");
	
//    private static String CHANNEL = "future2";
//    private static String HOST = "10.20.112.103";
//    private static int PORT = 6379;
    
    final private Gson gson;

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureCacheUpdateTask.class);


    public FutureCacheUpdateTask(MarketDataCache marketDataCache) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer());
        builder.registerTypeAdapter(BigDecimalSerializer.class, new BigDecimalDeserializer());
        gson = builder.create();

        this.marketDataCache = marketDataCache;
    }

    @Override
    public void run() {
        LOGGER.info("Future Cache Scheduler begins");

        try {
            JedisPool pool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
            Jedis jedis = pool.getResource();

            FutureMarketDataListener listener = new FutureMarketDataListener(gson, marketDataCache);

            jedis.subscribe(listener, CHANNEL);
        } catch (Throwable th){
            LOGGER.error(th.getMessage(), th);
        }
    }
}
