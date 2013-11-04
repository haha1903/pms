package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.PositionValuationType;

import java.util.List;

public interface PositionValuationTypeDao extends GenericAccountMasterDao<PositionValuationType, Long> {
    
    public List<PositionValuationType> findAll();
}
