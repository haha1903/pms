package com.datayes.invest.pms.dao.account.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.entity.account.PositionValuationHist;

public class PositionValuationHistDaoImpl extends GenericAccountMasterDaoImpl<PositionValuationHist, PositionValuationHist.PK>
        implements PositionValuationHistDao {

    protected PositionValuationHistDaoImpl() {
        super(PositionValuationHist.class);
    }

    @Override
    public List<PositionValuationHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.emptyList();
        }

        Query q = getEntityManager().createQuery("from PositionValuationHist where " +
                "PK.positionId in (:positionIdList) and PK.typeId = :typeId and PK.asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("typeId", typeId);
        q.setParameter("asOfDate", asOfDate);
        List<PositionValuationHist> list = (List<PositionValuationHist>) q.getResultList();

        return list;
    }

    @Override
    public void deleteByPositionId(Long positionId) {
        Query q = getEntityManager().createQuery(
                "delete from PositionValuationHist where PK.positionId = :positionId");
        q.setParameter("positionId", positionId);
        q.executeUpdate();
    }

    @Override
    public List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.emptyList();
        }

        Query q = getEntityManager().createQuery("from PositionValuationHist where " +
                "PK.positionId in (:positionIdList) and PK.asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("asOfDate", asOfDate);
        List<PositionValuationHist> list = (List<PositionValuationHist>) q.getResultList();

        return list;
    }
}
