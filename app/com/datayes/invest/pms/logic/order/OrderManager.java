package com.datayes.invest.pms.logic.order;

import com.datayes.invest.pms.service.order.OrderBasket;

public interface OrderManager {

    OrderBasket createOrders(OrderBasket basket);

    void placeOrders(Long orderBasketId);
}
