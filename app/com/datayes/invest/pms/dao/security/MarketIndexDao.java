package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.MarketIndex;

import java.util.List;

public interface MarketIndexDao extends GenericSecurityMasterDao<MarketIndex, Long> {

    public List<MarketIndex> findAll();
}
