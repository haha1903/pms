package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.Order;

import java.util.List;

public interface OrderDao extends AccountRelatedGenericDao<Order, Order.PK> {

    Order findCurrentById(Long orderId);

    List<Order> findCurrentByBasketId(Long basketId);
}
