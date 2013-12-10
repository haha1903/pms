package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionIdGenerator;

import javax.persistence.TypedQuery;
import java.util.List;

public class PositionIdGeneratorImpl extends AbstractIdGenerator implements PositionIdGenerator {

    @Override
    protected long findMaxId() {
        TypedQuery<Long> q = getEntityManager().createQuery("select max(id) from Position", Long.class);
        List<Long> list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return list.get(0);
    }
}
