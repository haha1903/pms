package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.OrderIdGenerator;

import javax.persistence.TypedQuery;
import java.util.List;

public class OrderIdGeneratorImpl extends AbstractIdGenerator implements OrderIdGenerator {

    @Override
    protected long findMaxId() {
        TypedQuery<Long> q = getEntityManager().createQuery("select max(pk.id) from Order", Long.class);
        List<Long> list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return list.get(0);
    }
}
