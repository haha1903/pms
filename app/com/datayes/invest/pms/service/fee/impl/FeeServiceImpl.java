package com.datayes.invest.pms.service.fee.impl;

import com.datayes.invest.pms.dbtype.RateType;
import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.service.fee.FeeService;

import scala.math.BigDecimal;

public class FeeServiceImpl implements FeeService {

    @Override
    public BigDecimal getRate(Long accountId, RateType rateType, TradeSide tradeSide, Long securityId) {
        // TODO Auto-generated method stub
        return null;
    }

}
