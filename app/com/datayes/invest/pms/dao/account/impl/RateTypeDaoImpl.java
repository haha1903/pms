package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.RateTypeDao;
import com.datayes.invest.pms.entity.account.RateType;

public class RateTypeDaoImpl extends GenericAccountMasterDaoImpl<RateType, Long> implements RateTypeDao {

    protected RateTypeDaoImpl() {
        super(RateType.class);
    }
}
