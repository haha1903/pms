package com.datayes.invest.pms.dao.account.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.entity.account.PositionValuationHist;

public class PositionValuationHistDaoImpl extends GenericAccountMasterDaoImpl<PositionValuationHist, Long>
    implements PositionValuationHistDao {

    protected PositionValuationHistDaoImpl() {
        super(PositionValuationHist.class);
    }

    public PositionValuationHist findByPositionIdAsOfDate(Long positionId, Long typeId, LocalDate asOfDate) {
        PositionValuationHist.PK pk = new PositionValuationHist.PK(positionId, typeId, asOfDate);
        PositionValuationHist pvh = getEntityManager().find(PositionValuationHist.class, pk);
        return pvh;
    }

    /*
    public PositionValuationHist findByPositionIdAsOfDate(Long positionId, Long typeId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery("from PositionValuationHist where " +
            "positionId = :positionId and typeId = :typeId and asOfDate = :asOfDate");
        q.setParameter("positionId", positionId);
        q.setParameter("typeId", typeId);
        q.setParameter("asOfDate", asOfDate);
        List<PositionValuationHist> list = (List<PositionValuationHist>) q.getResultList();

        if (list != null && ! list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }*/

    @Override
    public List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.<PositionValuationHist>emptyList();
        }
//
//        Query q = getEntityManager().createQuery("from PositionValuationHist where " +
//                "positionId in (:positionIdList) and typeId = :typeId and asOfDate = :asOfDate");
//        q.setParameter("positionIdList", positionIdList);
//        q.setParameter("typeId", typeId);
//        q.setParameter("asOfDate", asOfDate);
//        List<PositionValuationHist> list = (List<PositionValuationHist>) q.getResultList();
//
//        return list;

        List<PositionValuationHist> list = new ArrayList<PositionValuationHist>();
        for (Long positionId: positionIdList) {
            PositionValuationHist pvh = findByPositionIdAsOfDate(positionId, typeId, asOfDate);
            if (pvh != null) {
                list.add(pvh);
            }
        }
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
            return Collections.<PositionValuationHist>emptyList();
        }

        Query q = getEntityManager().createQuery("from PositionValuationHist where " +
                "PK.positionId in (:positionIdList) and PK.asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("asOfDate", asOfDate);
        List<PositionValuationHist> list = (List<PositionValuationHist>) q.getResultList();

        return list;
    }
}
