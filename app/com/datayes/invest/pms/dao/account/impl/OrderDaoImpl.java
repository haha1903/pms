package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.OrderDao;
import com.datayes.invest.pms.entity.account.Order;

import javax.persistence.TypedQuery;
import java.util.List;

public class OrderDaoImpl extends AccountRelatedDaoImpl<Order, Order.PK> implements OrderDao {

    protected OrderDaoImpl() {
        super(Order.class);
    }

    @Override
    public List<Order> findCurrentByBasketId(Long basketId) {
        TypedQuery<Order> query = getEntityManager().createQuery("from Order where basketId = :basketId and isCurrent = true", Order.class);
        List<Order> list = query.getResultList();
        return list;
    }
}