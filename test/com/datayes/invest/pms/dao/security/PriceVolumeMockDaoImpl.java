package com.datayes.invest.pms.dao.security;


import com.datayes.invest.pms.dao.security.impl.GenericSecurityMasterDaoImpl;
import com.datayes.invest.pms.entity.security.PriceVolume;
import org.joda.time.LocalDate;

import java.util.*;

public class PriceVolumeMockDaoImpl extends GenericSecurityMasterDaoImpl<PriceVolume, Long>
        implements PriceVolumeDao {

    Map<Long, PriceVolume> mockData = new HashMap<>();

    public PriceVolumeMockDaoImpl() {
        super(PriceVolume.class);
    }

    @Override
    public PriceVolume findBySecurityIdTradeDate(Long securityId, LocalDate tradeDate) {
        return null;
    }

    @Override
    public PriceVolume findOneBySecurityIdAfterDate(Long securityId, LocalDate afterDate) {
        return null;
    }

    @Override
    public List<PriceVolume> findSomeBySecurityIdInPeriod(Long securityId, LocalDate startDate, LocalDate endDate) {
        return null;
    }

    @Override
    public List<PriceVolume> findByTradeDate(LocalDate tradeDate) {
        return null;
    }

    @Override
    public List<PriceVolume> findBySecurityIdListTradeDate(Collection<Long> securityIds, LocalDate tradeDate) {
        List<PriceVolume> list = new LinkedList<>();

        for( Long securityId : securityIds ) {
            PriceVolume priceVolume = mockData.get(securityId);
            if( priceVolume != null ) {
                list.add(priceVolume);
            }
        }

        return list;
    }

    public void addMockData(PriceVolume priceVolume) {
        mockData.put(priceVolume.getSecurityId(), priceVolume);
    }
}
