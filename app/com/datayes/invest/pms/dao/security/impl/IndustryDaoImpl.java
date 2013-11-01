package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.IndustryDao;
import com.datayes.invest.pms.entity.security.Industry;

import javax.persistence.Query;
import java.util.List;

public class IndustryDaoImpl extends GenericSecurityMasterDaoImpl<Industry, Integer> implements IndustryDao {

    protected IndustryDaoImpl() {
        super(Industry.class);
    }

    public List<Industry> findByDataSourceIdClassLevel(int dataSourceId, int classLevel) {
        Query q = getEntityManager().createQuery(
                "from Industry where dataSourceId = :dataSourceId AND classLevel = :classLevel");

        q.setParameter("dataSourceId", dataSourceId);
        q.setParameter("classLevel", classLevel);
        enableCache(q);

        return (List<Industry>) q.getResultList();
    }

}
