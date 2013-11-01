package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.MarketIndexWeight;

public interface MarketIndexWeightDao extends GenericSecurityMasterDao<MarketIndexWeight, Long> {

    MarketIndexWeight findLatestByMarketIndexIdSecurityId(Long marketIndexId, Long securityId);
}
