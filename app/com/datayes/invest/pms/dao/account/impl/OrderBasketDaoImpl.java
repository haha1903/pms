package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.OrderBasketDao;
import com.datayes.invest.pms.entity.account.OrderBasket;

public class OrderBasketDaoImpl extends GenericAccountMasterDaoImpl<OrderBasket, Long> implements OrderBasketDao {

    protected OrderBasketDaoImpl() {
        super(OrderBasket.class);
    }
}
