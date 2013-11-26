package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionYieldDao;
import com.datayes.invest.pms.entity.account.PositionYield;

import org.joda.time.LocalDate;

import javax.persistence.Query;

import java.util.List;

public class PositionYieldDaoImpl extends GenericAccountMasterDaoImpl<PositionYield, Long> implements PositionYieldDao{

    protected PositionYieldDaoImpl() {
        super(PositionYield.class);
    }

    @Override
    public List<PositionYield> findByPositionIdsAsOfDate(List<Long> positionIds, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery("from PositionYield where positionId in (:positionIds) and asOfDate = (:asOfDate)");
        q.setParameter("positionIds", positionIds);
        q.setParameter("asOfDate", asOfDate);
        enableCache(q);

        return (List<PositionYield>) q.getResultList();
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
            "delete from PositionYield where accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }
}
