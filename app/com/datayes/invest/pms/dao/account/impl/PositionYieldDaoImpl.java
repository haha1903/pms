package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionYieldDao;
import com.datayes.invest.pms.entity.account.PositionYield;

public class PositionYieldDaoImpl extends GenericAccountMasterDaoImpl<PositionYield, Long> implements PositionYieldDao{

    protected PositionYieldDaoImpl() {
        super(PositionYield.class);
    }
}
