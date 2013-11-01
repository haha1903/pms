package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.Future;

public interface FutureDao extends GenericSecurityMasterDao<Future, Long> {

    public Future findByTickerSymbol(String tickerSymbol);
}
