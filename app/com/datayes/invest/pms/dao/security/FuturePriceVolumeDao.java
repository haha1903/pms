package com.datayes.invest.pms.dao.security;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.FuturePriceVolume;

public interface FuturePriceVolumeDao extends GenericSecurityMasterDao<FuturePriceVolume, Long> {

    FuturePriceVolume findBySecurityIdTradeDate(Long securityId, LocalDate tradeDate);

//    List<FuturePriceVolume> findBySecurityIdBetweenDates(Long securityId, LocalDate startDate, LocalDate endDate);

    FuturePriceVolume findOneBySecurityIdAfterDate(Long securityId, LocalDate afterDate);

    List<FuturePriceVolume> findSomeBySecurityIdInPeriod(Long securityId, LocalDate startDate, LocalDate endDate);
    
    List<FuturePriceVolume> findByTradeDate(LocalDate tradeDate);
    
    List<FuturePriceVolume> findBySecurityIdListTradeDate(Collection<Long> securityIds, LocalDate tradeDate);
}
