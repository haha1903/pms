package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.TradeSideDao;
import com.datayes.invest.pms.entity.account.TradeSide;

public class TradeSideDaoImpl extends GenericAccountMasterDaoImpl<TradeSide, String> implements TradeSideDao {

    protected TradeSideDaoImpl() {
        super(TradeSide.class);
    }
}
