package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionInitDao;
import com.datayes.invest.pms.entity.account.PositionInit;


public class PositionInitDaoImpl extends GenericAccountMasterDaoImpl<PositionInit, Long>
        implements PositionInitDao {

    protected PositionInitDaoImpl() {
        super(PositionInit.class);
    }
}
