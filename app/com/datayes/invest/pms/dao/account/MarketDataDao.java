package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.MarketData;

import java.util.List;

public interface MarketDataDao extends GenericAccountMasterDao<MarketData, Long> {
    MarketData findBySecurityId(Long securityId);

    List<MarketData> findAll();
}
