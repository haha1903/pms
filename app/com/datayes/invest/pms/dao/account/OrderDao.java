package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.Order;

import java.util.List;

public interface OrderDao extends AccountRelatedGenericDao<Order, Order.PK> {

    List<Order> findCurrentByBasketId(Long basketId);
}
