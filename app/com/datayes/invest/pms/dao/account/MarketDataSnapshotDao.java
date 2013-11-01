package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.MarketDataSnapshot;

import java.util.List;

public interface MarketDataSnapshotDao extends GenericAccountMasterDao<MarketDataSnapshot, Long> {
    MarketDataSnapshot findBySecurityId(Long securityId);

    List<MarketDataSnapshot> findAll();
}
