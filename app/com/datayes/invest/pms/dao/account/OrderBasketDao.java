package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.OrderBasket;

import java.util.Collection;

public interface OrderBasketDao extends GenericAccountMasterDao<OrderBasket, Long> {

    void deleteByIdList(Collection<Long> ids);
}
