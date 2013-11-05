package com.datayes.invest.pms.dao.account.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.CarryingValueHistDao;
import com.datayes.invest.pms.entity.account.CarryingValueHist;

public class CarryingValueHistDaoImpl extends GenericAccountMasterDaoImpl<CarryingValueHist, CarryingValueHist.PK>
    implements CarryingValueHistDao {

    protected CarryingValueHistDaoImpl() {
        super(CarryingValueHist.class);
    }

    @Override
    public List<CarryingValueHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.emptyList();
        }
        Query q = getEntityManager().createQuery(
                "from CarryingValueHist where PK.positionId in (:positionIdList) and PK.typeId = :typeId and PK.asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("typeId", typeId);
        q.setParameter("asOfDate", asOfDate);
        List<CarryingValueHist> list = (List<CarryingValueHist>) q.getResultList();
        return list;
    }

    @Override
    public List<CarryingValueHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.emptyList();
        }
        Query q = getEntityManager().createQuery(
                        "from CarryingValueHist where PK.positionId in (:positionIdList) and PK.asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("asOfDate", asOfDate);
        List<CarryingValueHist> list = (List<CarryingValueHist>) q.getResultList();
        return list;
    }

    @Override
    public void deleteByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
            "delete from CarryingValueHist h where h.accountId = :accountId and h.PK.asOfDate = :asOfDate");
        q.setParameter("accountId", accountId);
        q.setParameter("asOfDate", asOfDate);
        q.executeUpdate();
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        Query q = getEntityManager().createQuery(
                "delete from CarryingValueHist where accountId = :accountId");
        q.setParameter("accountId", accountId);
        q.executeUpdate();
    }
}
