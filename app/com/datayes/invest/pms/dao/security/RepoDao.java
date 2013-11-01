package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.Repo;

public interface RepoDao extends GenericSecurityMasterDao<Repo, Long> {

    public Repo findByTickerSymbol(String tickerSymbol);
}
