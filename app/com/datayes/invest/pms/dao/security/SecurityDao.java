package com.datayes.invest.pms.dao.security;

import java.util.List;

import com.datayes.invest.pms.entity.security.Security;

public interface SecurityDao extends GenericSecurityMasterDao<Security, Long> {

    public List<Security> findByTickerSymbol(String tickerSymbol);
}
