package com.datayes.invest.pms.service.fee;

import com.datayes.invest.pms.dbtype.RateType;
import com.datayes.invest.pms.dbtype.TradeSide;
import scala.math.BigDecimal;

public interface FeeService {

    BigDecimal getRate(Long accountId, RateType rateType, TradeSide tradeSide, Long securityId);
}
