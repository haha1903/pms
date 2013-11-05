package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.security.IndustryDao;
import com.datayes.invest.pms.entity.security.Industry;

public class IndustryDaoImpl extends GenericSecurityMasterDaoImpl<Industry, Integer> implements IndustryDao {

    protected IndustryDaoImpl() {
        super(Industry.class);
    }

    public List<Industry> findByDataSourceIdClassLevel(int dataSourceId, int classLevel) {
        TypedQuery<Industry> q = getEntityManager().createQuery(
                "from Industry where dataSourceId = :dataSourceId AND classLevel = :classLevel", Industry.class);

        q.setParameter("dataSourceId", dataSourceId);
        q.setParameter("classLevel", classLevel);
        enableCache(q);

        return q.getResultList();
    }

}
