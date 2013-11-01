package com.datayes.invest.pms.dao.account.impl;

import java.util.ArrayList;
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

    @SuppressWarnings("unchecked")
    @Override
    public CarryingValueHist findByPositionIdAsOfDate(Long id, Long typeId, LocalDate asOfDate) {
        CarryingValueHist.PK pk = new CarryingValueHist.PK(id, typeId, asOfDate);
        return findById(pk);
        /*Query q = getEntityManager().createQuery(
                        "from CarryingValueHist where positionId = :positionId and typeId = :typeId and asOfDate = :asOfDate");
        q.setParameter("positionId", id);
        q.setParameter("typeId", typeId);
        q.setParameter("asOfDate", asOfDate);
        List<CarryingValueHist> list = (List<CarryingValueHist>) q.getResultList();
        if (! list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }*/
    }

    @Override
    public List<CarryingValueHist> findByPositionIdListAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.<CarryingValueHist>emptyList();
        }
        // TODO refine this
        List<CarryingValueHist> list = new ArrayList<CarryingValueHist>();
        for (Long positionId : positionIdList) {
            CarryingValueHist.PK pk = new CarryingValueHist.PK(positionId, typeId, asOfDate);
            CarryingValueHist cvh = findById(pk);
            if (cvh != null) {
                list.add(cvh);
            }
        }
        return list;

        /*Query q = getEntityManager().createQuery(
                "from CarryingValueHist where positionId in (:positionIdList) and typeId = :typeId and asOfDate = :asOfDate");
        q.setParameter("positionIdList", positionIdList);
        q.setParameter("typeId", typeId);
        q.setParameter("asOfDate", asOfDate);
        List<CarryingValueHist> list = (List<CarryingValueHist>) q.getResultList();
        return list;*/
    }

    @Override
    public List<CarryingValueHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
                        "from CarryingValueHist where PK.positionId in :positionIdList and PK.asOfDate = :asOfDate");
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
