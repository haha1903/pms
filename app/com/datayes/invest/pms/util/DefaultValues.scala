package com.datayes.invest.pms.util

import com.datayes.invest.pms.dbtype.PositionValuationType

object DefaultValues {
  
  val CARRYING_VALUE_TYPE = 1L
  
  val CASH_PRICE = BigDecimalConstants.ONE

  val CURRENCY_CODE = "CNY"
    
  val POSITION_VALUATION_TYPE = PositionValuationType.MARKET;
  
  val STOCK_INDEX_FUTURE_PRICE_RATIO = 300
}
