package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.OrderDao;
import com.datayes.invest.pms.entity.account.Order;
import com.datayes.invest.pms.logic.transaction.BusinessException;

import javax.persistence.TypedQuery;
import java.util.List;

public class OrderDaoImpl extends AccountRelatedDaoImpl<Order, Order.PK> implements OrderDao {

    protected OrderDaoImpl() {
        super(Order.class);
    }

    @Override
    public Order findCurrentById(Long orderId) {
        TypedQuery<Order> query = getEntityManager().createQuery("from Order where id = :id and isCurrent = true", Order.class);
        query.setParameter("id", orderId);
        List<Order> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new IllegalStateException("Order #" + orderId + " has more than one current record");
        }
        return list.get(0);
    }

    @Override
    public List<Order> findCurrentByBasketId(Long basketId) {
        TypedQuery<Order> query = getEntityManager().createQuery("from Order where basketId = :basketId and isCurrent = true", Order.class);
        query.setParameter("basketId", basketId);
        List<Order> list = query.getResultList();
        return list;
    }
}