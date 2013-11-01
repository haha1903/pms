package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.security.EquityDividendDao;
import com.datayes.invest.pms.entity.security.EquityDividend;

public class EquityDividendDaoImpl extends GenericSecurityMasterDaoImpl<EquityDividend, Long> implements
                EquityDividendDao {

    protected EquityDividendDaoImpl() {
        super(EquityDividend.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EquityDividend findBySecurityIdExDiviDate(Long securityId, LocalDate exDiviDate) {
        Query q = getEntityManager().createQuery(
                        "from EquityDividend where securityId = :securityId and exDividate = :exDividate");
        q.setParameter("securityId", securityId);
        q.setParameter("exDividate", exDiviDate);
        enableCache(q);
        List<EquityDividend> list = (List<EquityDividend>) q.getResultList();
        
        return list.isEmpty() ? null : list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EquityDividend> findBySecurityIdsExDiviDate(List<Long> securityIdList, LocalDate exDiviDate) {
        Query q = getEntityManager().createQuery(
                        "from EquityDividend where securityId in (:securityIdList) and exDividate = :exDividate");
        q.setParameter("securityIdList", securityIdList);
        q.setParameter("exDividate", exDiviDate);
        enableCache(q);
        List<EquityDividend> list = (List<EquityDividend>) q.getResultList();
        return list;
    }
}
