package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.security.FuturePriceVolumeDao;
import com.datayes.invest.pms.entity.security.FuturePriceVolume;

public class FuturePriceVolumeDaoImpl extends GenericSecurityMasterDaoImpl<FuturePriceVolume, Long>
                implements FuturePriceVolumeDao {

    protected FuturePriceVolumeDaoImpl() {
        super(FuturePriceVolume.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FuturePriceVolume findBySecurityIdTradeDate(Long securityId, LocalDate tradeDate) {
        Query q = getEntityManager()
                        .createQuery("from FuturePriceVolume where securityId = :securityId and tradeDate = :tradeDate");
        q.setParameter("securityId", securityId);
        q.setParameter("tradeDate", tradeDate);
        List<FuturePriceVolume> list = (List<FuturePriceVolume>) q.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /*
    @SuppressWarnings("unchecked")
    @Override
    public List<FuturePriceVolume> findBySecurityIdBetweenDates(Long securityId, LocalDate startDate,
                    LocalDate endDate) {
        Query q = getEntityManager().createQuery(
                        "from FuturePriceVolume where securityId = :securityId and tradeDate >= :startDate and "
                                        + "tradeDate <= :endDate");
        q.setParameter("securityId", securityId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        List<FuturePriceVolume> list = (List<FuturePriceVolume>) q.getResultList();
        return list;
    }*/

    @Override
    public FuturePriceVolume findOneBySecurityIdAfterDate(Long securityId, LocalDate afterDate) {
        Query q = getEntityManager().createQuery(
                        "from FuturePriceVolume where securityId = :securityId and tradeDate >= :afterDate "
                                        + "order by tradeDate asc");
        q.setParameter("securityId", securityId);
        q.setParameter("afterDate", afterDate);
        q.setMaxResults(1);
        List<FuturePriceVolume> list = (List<FuturePriceVolume>) q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<FuturePriceVolume> findSomeBySecurityIdInPeriod(Long securityId, LocalDate startDate, LocalDate endDate) {
        Query q = getEntityManager().createQuery(
                "from FuturePriceVolume where securityId = :securityId and tradeDate <= :endDate "
                        + "and tradeDate >= :startDate "
                        + "order by tradeDate desc");
        q.setParameter("securityId", securityId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
//        q.setMaxResults(StaticDatas.LIMIT_DAY_FINDING());

        List<FuturePriceVolume> list = (List<FuturePriceVolume>) q.getResultList();
        return list;
    }
}
