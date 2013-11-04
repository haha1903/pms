package com.datayes.invest.pms.service.marketdata.impl.task;


import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.service.marketdata.impl.cache.MarketDataCache;
import com.datayes.invest.pms.service.marketdata.impl.data.BigDecimalDeserializer;
import com.datayes.invest.pms.service.marketdata.impl.data.BigDecimalSerializer;
import com.datayes.invest.pms.service.marketdata.impl.data.Future;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import scala.math.BigDecimal;

class FutureMarketDataListener extends JedisPubSub {

    private Gson gson = null;

    private MarketDataCache marketDataCache = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureMarketDataListener.class);


    public FutureMarketDataListener(Gson gson, MarketDataCache marketDataCach) {
        this.gson = gson;
        this.marketDataCache = marketDataCach;
    }

    @Override
    public void onMessage(String channel, String message) {
        // Deserialize message
        Future future = gson.fromJson(message, Future.class);

        // Update cache
        marketDataCache.update(future);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOGGER.error("PMessage " + pattern + "=" + channel + "=" + message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.error("Subscribe " + channel + "=" + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.error("Unsubsribe " + channel + "=" + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        LOGGER.error("PUnsubscribe " + pattern + "=" + subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.error("PSubscribe " + pattern + "=" + subscribedChannels);
    }
}

public class FutureCacheUpdateTask implements Runnable {

    private MarketDataCache marketDataCache = null;

    private static Config config = Config.INSTANCE;

    private static String CHANNEL = config.getString("redis.channel");
    private static String HOST = config.getString("redis.host");
    private static int PORT = config.getInt("redis.port");

    private GsonBuilder builder = new GsonBuilder();
    private Gson gson = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureCacheUpdateTask.class);


    public FutureCacheUpdateTask(MarketDataCache marketDataCache) {
        builder.registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer());
        builder.registerTypeAdapter(BigDecimalSerializer.class, new BigDecimalDeserializer());
        gson = builder.create();

        this.marketDataCache = marketDataCache;
    }

    @Override
    public void run() {
        LOGGER.debug("Future Cache Scheduler begins");

        try {
            JedisPool pool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
            Jedis jedis = pool.getResource();

            FutureMarketDataListener listener = new FutureMarketDataListener(gson, marketDataCache);

            jedis.subscribe(listener, CHANNEL);
        }
        catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
