package com.datayes.invest.pms.service.marketdata.impl.task;


import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.service.marketdata.impl.cache.MarketDataCache;
import com.datayes.invest.pms.service.marketdata.impl.data.Stock;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.rabbitmq.client.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EquityCacheUpdateTask implements Runnable {

    private MarketDataCache marketDataCache = null;

    private static Config config = Config.INSTANCE;

    private static String EQUITY_KEY = config.getString("rabbitmq.exchange_name");
    private static String EXCHANGE_NAME = config.getString("rabbitmq.exchange_name");
    private static String HOST = config .getString("rabbitmq.host");
    private static String QUEUE_NAME = config.getString("rabbitmq.queue_name");

    private static Schema stockSchema = RuntimeSchema.getSchema(Stock.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(EquityCacheUpdateTask.class);


    public EquityCacheUpdateTask(MarketDataCache marketDataCache) {
        this.marketDataCache = marketDataCache;
    }

    private Connection createConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try {
            return factory.newConnection();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    private void handle(QueueingConsumer consumer) {
        while(true) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String routingKey = delivery.getEnvelope().getRoutingKey();

                if(EQUITY_KEY == routingKey) {
                    // Deserialize message
                    byte[] buff = Base64.decodeBase64(delivery.getBody());
                    Stock stock = new Stock();
                    ProtobufIOUtil.mergeFrom(buff, stock, EquityCacheUpdateTask.stockSchema);

                    // Update cache
                    marketDataCache.update(stock);
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        LOGGER.debug("Equity Cache Scheduler begins");

        try {
            Channel channel = createConnection().createChannel();

            // Size of rabbitmq queue buffer
            Map<String, Object> args = new HashMap<>();
            args.put("x-max-length", 10000);

            String queue = channel.queueDeclare(QUEUE_NAME, false, false, true, args).getQueue();
            channel.queueBind(queue, EXCHANGE_NAME, EQUITY_KEY);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queue, true, consumer);

            // Receiving message and deserializing
            handle(consumer);
        }
        catch (ShutdownSignalException e) {
            LOGGER.error("RabbitMQ connection was shut down by server: {}", HOST);
        }
        catch (IOException e) {
            LOGGER.error("Cannot connect to RabbitMQ server: {}", HOST);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.debug("Equity Cache Scheduler ends");
    }
}
