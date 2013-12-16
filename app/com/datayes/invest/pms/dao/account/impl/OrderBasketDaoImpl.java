package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.OrderBasketDao;
import com.datayes.invest.pms.entity.account.OrderBasket;

import javax.persistence.Query;
import java.util.Collection;

public class OrderBasketDaoImpl extends GenericAccountMasterDaoImpl<OrderBasket, Long> implements OrderBasketDao {

    protected OrderBasketDaoImpl() {
        super(OrderBasket.class);
    }

    @Override
    public void deleteByIdList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Query query = getEntityManager().createQuery("delete from OrderBasket where id in (:ids)");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }
}
