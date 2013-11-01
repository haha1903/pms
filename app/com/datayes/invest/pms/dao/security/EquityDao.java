package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.Equity;

import java.util.List;

public interface EquityDao extends GenericSecurityMasterDao<Equity, Long> {

    public List<Equity> findByTickerSymbol(String tickerSymbol);
    
    List<Equity> findListed();
}
