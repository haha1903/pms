package com.datayes.invest.pms.service.marketdata.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.math.BigDecimal;

import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.util.gson.BigDecimalDeserializer;
import com.datayes.invest.pms.util.gson.BigDecimalSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FutureCacheUpdateTask implements Runnable {
    private final JedisPool pool;
    private final FutureMarketDataListener listener;
    private final Gson gson;

    private final static Config config = Config.INSTANCE;
    private final static String CHANNEL = config.getString("redis.future_channel");

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureCacheUpdateTask.class);

    public FutureCacheUpdateTask(JedisPool pool, MarketDataCache marketDataCache) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer());
        builder.registerTypeAdapter(BigDecimalSerializer.class, new BigDecimalDeserializer());
        gson = builder.create();

        this.pool = pool;
        listener = new FutureMarketDataListener(gson, marketDataCache);
    }

    @Override
    public void run() {
        LOGGER.info("Future Cache Scheduler begins");

        try {
            Jedis jedis = pool.getResource();
            jedis.subscribe(listener, CHANNEL);
        } catch (Throwable th){
            LOGGER.error(th.getMessage(), th);
        }
    }
}
