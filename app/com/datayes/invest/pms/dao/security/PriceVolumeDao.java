package com.datayes.invest.pms.dao.security;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.PriceVolume;

public interface PriceVolumeDao extends GenericSecurityMasterDao<PriceVolume, Long> {

    PriceVolume findBySecurityIdTradeDate(Long securityId, LocalDate tradeDate);

//    List<PriceVolume> findBySecurityIdBetweenDates(Long securityId, LocalDate startDate, LocalDate endDate);

    PriceVolume findOneBySecurityIdAfterDate(Long securityId, LocalDate afterDate);

    List<PriceVolume> findSomeBySecurityIdInPeriod(Long securityId, LocalDate startDate, LocalDate endDate);
    
    List<PriceVolume> findByTradeDate(LocalDate tradeDate);
    
    List<PriceVolume> findBySecurityIdListTradeDate(Collection<Long> securityIds, LocalDate tradeDate);
}
