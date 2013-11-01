package com.datayes.invest.pms.dao.security;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.EquityDividend;

public interface EquityDividendDao extends GenericSecurityMasterDao<EquityDividend, Long> {
    
    EquityDividend findBySecurityIdExDiviDate(Long securityId, LocalDate exDiviDate);
    
    List<EquityDividend> findBySecurityIdsExDiviDate(List<Long> securityIdList, LocalDate exDiviDate);
}
