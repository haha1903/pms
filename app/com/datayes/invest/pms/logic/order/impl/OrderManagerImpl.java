package com.datayes.invest.pms.logic.order.impl;

import com.datayes.invest.pms.dao.account.OrderBasketDao;
import com.datayes.invest.pms.dao.account.OrderDao;
import com.datayes.invest.pms.dao.account.OrderIdGenerator;
import com.datayes.invest.pms.dbtype.OrderStatus;
import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.logic.order.OrderManager;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.service.order.Order;
import com.datayes.invest.pms.service.order.OrderBasket;
import com.datayes.invest.pms.service.order.OrderService;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class OrderManagerImpl implements OrderManager {

    private static Logger LOGGER = LoggerFactory.getLogger(OrderManagerImpl.class);

    @Inject
    private OrderIdGenerator idGenerator;

    @Inject
    private OrderBasketDao orderBasketDao;

    @Inject
    private OrderDao orderDao;

    @Inject
    private OrderService orderService;

    @Override
    public OrderBasket createOrders(OrderBasket basket) {
        Transaction tx = Persist.getTransaction();
        try {
            if (basket == null || basket.getOrders() == null && basket.getOrders().isEmpty()) {
                return basket;
            }

            // Create order basket
            Long basketId = createDbOrderBasket();

            // Create orders
            OrderBasket retBasket = new OrderBasket();
            retBasket.setId(basketId);
            for (Order order : basket.getOrders()) {
                Order retOrder = createDbOrder(basketId, order);
                retBasket.getOrders().add(retOrder);
            }

            tx.commit();
            return retBasket;

        } catch (Throwable th) {
            tx.rollback();
            throw th;
        }
    }

    @Override
    public void placeOrders(Long orderBasketId) {
        // Convert from DB order
        List<com.datayes.invest.pms.entity.account.Order> orders = orderDao.findCurrentByBasketId(orderBasketId);
        OrderBasket basket = new OrderBasket(orderBasketId);
        boolean hasInvalidStatus = false;
        for (com.datayes.invest.pms.entity.account.Order dbOrd : orders) {
            OrderStatus status = OrderStatus.fromDbValue(dbOrd.getOrderStatus());
            if (status != OrderStatus.CREATED) {
                hasInvalidStatus = true;
            }
            Order ord = fromDbOrder(dbOrd);
            basket.getOrders().add(ord);
        }
        if (hasInvalidStatus) {
            throw new RuntimeException("The basket " + orderBasketId + " has orders with invalid status. Orders to be placed must be OrderStatus.CREATED");
        }

        // Invoke order service
        OrderBasket retBasket = orderService.placeOrders(basket);
    }

    private Order fromDbOrder(com.datayes.invest.pms.entity.account.Order dbOrder) {
        Order order = new Order();
        order.setOrderId(dbOrder.getPk().getId());
        order.setAccountId(dbOrder.getAccountId());
        order.setSecurityId(dbOrder.getSecurityId());
        order.setAmount(dbOrder.getAmount().longValue());
        order.setTradeSide(TradeSide.fromDbValue(dbOrder.getTradeSideCode()));
        order.setPriceLimit(dbOrder.getPriceLimit());
        order.setPriceGuideline(dbOrder.getPriceGuideline());

        order.setStpFlag(order.isStpFlag());

        order.setStpAlgorithm(dbOrder.getStpAlgorithm());
        if (order.getStpStartTime() != null) {
            order.setStpStartTime(dbOrder.getStpStartTime().toLocalTime());
        }
        if (order.getStpEndTime() != null) {
            order.setStpEndTime(dbOrder.getStpEndTime().toLocalTime());
        }

        return order;
    }

    private Order createDbOrder(Long basketId, Order order) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        com.datayes.invest.pms.entity.account.Order dbOrder = new com.datayes.invest.pms.entity.account.Order();
        Long orderId = idGenerator.getNextId();
        com.datayes.invest.pms.entity.account.Order.PK pk = new com.datayes.invest.pms.entity.account.Order.PK(orderId, 0L);
        dbOrder.setPk(pk);
        dbOrder.setIsCurrent(true);
        dbOrder.setBasketId(basketId);
        dbOrder.setAccountId(order.getAccountId());
        dbOrder.setSecurityId(order.getSecurityId());
        dbOrder.setAmount(BigDecimal.valueOf(order.getAmount()));
        dbOrder.setAsOfDate(today);
        dbOrder.setAsOfTime(now);
        dbOrder.setTradeSideCode(order.getTradeSide().getDbValue());
        dbOrder.setOrderStatus(OrderStatus.CREATED.getDbValue());
        dbOrder.setStatusChangeDate(now);
        dbOrder.setPriceLimit(order.getPriceLimit());
        dbOrder.setPriceGuideline(order.getPriceGuideline());

        dbOrder.setStpFlag(order.isStpFlag());

        if (order.isStpFlag() != null && order.isStpFlag()) {
            dbOrder.setStpAlgorithm(order.getStpAlgorithm());
            dbOrder.setStpStartTime(today.toLocalDateTime(order.getStpStartTime()));
            dbOrder.setStpEndTime(today.toLocalDateTime(order.getStpEndTime()));
        }

        orderDao.save(dbOrder);

        order.setOrderId(dbOrder.getPk().getId());

        return order;
    }

    private Long createDbOrderBasket() {
        com.datayes.invest.pms.entity.account.OrderBasket basket = new com.datayes.invest.pms.entity.account.OrderBasket();
        orderBasketDao.save(basket);
        return basket.getId();
    }
}
