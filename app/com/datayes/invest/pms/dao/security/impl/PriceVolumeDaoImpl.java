package com.datayes.invest.pms.dao.security.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.security.PriceVolumeDao;
import com.datayes.invest.pms.entity.security.PriceVolume;

public class PriceVolumeDaoImpl extends GenericSecurityMasterDaoImpl<PriceVolume, Long>
    implements PriceVolumeDao {

    protected PriceVolumeDaoImpl() {
        super(PriceVolume.class);
    }

    @Override
    public PriceVolume findBySecurityIdTradeDate(Long securityId, LocalDate tradeDate) {
        Query q = getEntityManager().createQuery(
                "from PriceVolume where securityId = :securityId and tradeDate = :tradeDate");
        q.setParameter("securityId", securityId);
        q.setParameter("tradeDate", tradeDate);
        List<PriceVolume> list = (List<PriceVolume>) q.getResultList();
        if (! list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /*
    @Override
    public List<PriceVolume> findBySecurityIdBetweenDates(Long securityId, LocalDate startDate, LocalDate endDate) {
        Query q = getEntityManager().createQuery(
                "from PriceVolume where securityId = :securityId and tradeDate >= :startDate and " +
                "tradeDate <= :endDate");
        q.setParameter("securityId", securityId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        List<PriceVolume> list = (List<PriceVolume>) q.getResultList();
        return list;
    }
    */

    @Override
    public PriceVolume findOneBySecurityIdAfterDate(Long securityId, LocalDate afterDate) {
        Query q = getEntityManager().createQuery(
                "from PriceVolume where securityId = :securityId and tradeDate >= :afterDate " +
                "order by tradeDate asc");
        q.setParameter("securityId", securityId);
        q.setParameter("afterDate", afterDate);
        q.setMaxResults(1);
        List<PriceVolume> list = (List<PriceVolume>) q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<PriceVolume> findSomeBySecurityIdInPeriod(Long securityId, LocalDate startDate, LocalDate endDate) {
        Query q = getEntityManager().createQuery(
                "from PriceVolume where securityId = :securityId and tradeDate <= :endDate "
                      + "and tradeDate >= :startDate "
                      + "order by tradeDate desc");
        q.setParameter("securityId", securityId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
//        q.setMaxResults(StaticDatas.LIMIT_DAY_FINDING());
        List<PriceVolume> list = (List<PriceVolume>) q.getResultList();
        return list;
    }
    
    @Override
    public List<PriceVolume> findByTradeDate(LocalDate tradeDate) {
        TypedQuery<PriceVolume> q = getEntityManager().createQuery("from PriceVolume where tradeDate = :tradeDate", PriceVolume.class);
        q.setParameter("tradeDate", tradeDate);
        List<PriceVolume> list = q.getResultList();
        return list;
    }
    
    @Override
    public List<PriceVolume> findBySecurityIdListTradeDate(Collection<Long> securityIds, LocalDate tradeDate) {
        TypedQuery<PriceVolume> q = getEntityManager().createQuery("from PriceVolume where securityId in (:securityIds) and tradeDate = :tradeDate", PriceVolume.class);
        q.setParameter("securityIds", securityIds);
        q.setParameter("tradeDate", tradeDate);
        List<PriceVolume> list = q.getResultList();
        return list;
    }
}
