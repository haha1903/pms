package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.PositionClass;

import java.util.List;


public interface PositionClassDao extends GenericAccountMasterDao<PositionClass, String> {
    
    List<PositionClass> findAll();
}
