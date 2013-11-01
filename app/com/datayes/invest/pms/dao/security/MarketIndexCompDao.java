package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.MarketIndexComp;

import java.util.List;

public interface MarketIndexCompDao extends GenericSecurityMasterDao<MarketIndexComp, Long> {

    List<MarketIndexComp> findCurrentByMarketIndexId(Long marketIndexId);
}
