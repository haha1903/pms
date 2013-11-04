package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionHistDao;
import com.datayes.invest.pms.entity.account.PositionHist;
import org.joda.time.LocalDate;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionHistDaoImpl extends GenericAccountMasterDaoImpl<PositionHist, PositionHist.PK>
    implements PositionHistDao {

    protected PositionHistDaoImpl() {
        super(PositionHist.class);
    }

    @SuppressWarnings("unchecked")
    public PositionHist findByPositionIdAsOfDate(Long positionId, LocalDate asOfDate) {
        PositionHist.PK pk = new PositionHist.PK(positionId, asOfDate);
        return findById(pk);
    }

    @Override
    public List<PositionHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
        if (positionIdList == null || positionIdList.isEmpty()) {
            return Collections.<PositionHist>emptyList();
        }

        List<PositionHist> list = new ArrayList<PositionHist>();
        for (Long positionId : positionIdList) {
            PositionHist.PK pk = new PositionHist.PK(positionId, asOfDate);
            PositionHist ph = findById(pk);
            if (ph != null) {
                list.add(ph);
            }
        }
        return list;
    }

    @Override
    public void deleteByPositionId(Long positionId) {
        Query q = getEntityManager().createQuery(
                "delete from PositionHist h where h.PK.positionId = :positionId");
        q.setParameter("positionId", positionId);
        q.executeUpdate();
    }
}
