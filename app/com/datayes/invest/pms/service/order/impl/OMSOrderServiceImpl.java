package com.datayes.invest.pms.service.order.impl;

import com.datayes.invest.pms.config.Config;
import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.logic.transaction.BusinessException;
import com.datayes.invest.pms.service.ServiceException;
import com.datayes.invest.pms.service.order.Order;
import com.datayes.invest.pms.service.order.OrderBasket;
import com.datayes.invest.pms.service.order.OrderService;
import com.weston.jupiter.generated.*;
import com.weston.stpapi.STPClient;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class OMSOrderServiceImpl implements OrderService {

    private static Logger LOGGER = LoggerFactory.getLogger(OMSOrderServiceImpl.class);

    private final String localeName = "Tiger";

    private final String configDirectory = "/home/taot/Downloads/jupiter/stp-api/etc/";

    private String clientId = Config.INSTANCE.getString("pms.client.id");

    @Override
    public OrderBasket placeOrders(OrderBasket basket) {
        if (basket == null || basket.getOrders() == null || basket.getOrders().isEmpty()) {
            LOGGER.debug("empty basket");
            return basket;
        }

        LOGGER.debug("Placing {} orders (basketId = {})", basket.getOrders().size(), basket.getId());

        STPClient client = new STPClient();
        // TODO config for username, password, localeName, and other configs
        boolean initResult = client.init("xingen.song", "passwd", localeName, configDirectory);
        if (! initResult) {
            throw new ServiceException("Failed to initialize STP client");
        }

        try {
            OrderBasket retBasket = internalPlaceOrders(client, basket);
            return retBasket;
        } finally {
            client.fini();
        }
    }

    private OrderBasket internalPlaceOrders(STPClient client, OrderBasket basket) {
        com.weston.jupiter.generated.OrderBasket jupiterBasket = new com.weston.jupiter.generated.OrderBasket();
        jupiterBasket.stp = WireBoolean.TRUE;

        for (Order order : basket.getOrders()) {
            OrderPM orderPM = convertOrder(order);

            String jupiterOrderId = client.generateExternalOrderID(orderPM, order.getOrderId().intValue());     // TODO use long for order id
            if (jupiterOrderId != null) {
                jupiterBasket.orders.add(orderPM);
            }
        }

        client.sendNewOrderBasket(jupiterBasket);

        return basket;
    }

    private OrderPM convertOrder(Order order) {
        OrderPM jupiterOrder              = new OrderPM();

        jupiterOrder.externalClientID     = clientId;                                 // tenent client id
        jupiterOrder.externalAccountID    = String.valueOf(order.getAccountId());     // pms account id
        jupiterOrder.externalSubaccountID = "";                                       // no subaccount right now

        jupiterOrder.emsSecurityID        = order.getSecurityId();
        jupiterOrder.side                 = getJupiterTradeSide(order.getTradeSide());
        jupiterOrder.amountOpen           = order.getAmount();
        jupiterOrder.priceLimit           = order.getPriceLimit().doubleValue();
        jupiterOrder.priceGuideline       = order.getPriceGuideline().doubleValue();
        // Straight through process
        if(order.isStpFlag()) {                                                       // 自动合规, 自动拆单, etc
            jupiterOrder.stpAlgorithm      = getJupiterStpAlgorithm(order.getStpAlgorithm());
            jupiterOrder.stpStartTime      = toJupiterLocalTime(order.getStpStartTime());
            jupiterOrder.stpEndTime        = toJupiterLocalTime(order.getStpEndTime());
            jupiterOrder.stpBrokerCapacity = BrokerCapacity.Agency;                   // Not exposed in PMS, use Agency for now
        }

        return jupiterOrder;
    }

    private long toJupiterLocalTime(LocalTime time) {
        if (time == null) {
            return 0;
        }
        return time.getHourOfDay() * 10000 + time.getMinuteOfHour() * 100 + time.getSecondOfMinute();
    }

    private TradeType getJupiterStpAlgorithm(String sAlgorithm) {
        try {
            TradeType jupiterType = TradeType.valueOf(sAlgorithm);
            return jupiterType;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid STP algorithm: {}", sAlgorithm);
            return null;
        }
    }

    private Side getJupiterTradeSide(TradeSide tradeSide) {
        if (tradeSide == TradeSide.BUY) {
            return Side.Buy;
        } else if (tradeSide == TradeSide.SELL) {
            return Side.Sell;
        } else if (tradeSide == TradeSide.SHORT) {
            return Side.Short;
        } else if (tradeSide == TradeSide.COVER) {
            return Side.Cover;
        }
        throw new BusinessException("Unable to map trade side " + tradeSide + " to jupiter trade side");
    }
}
