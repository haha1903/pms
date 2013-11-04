package com.datayes.invest.pms.util

import com.datayes.invest.pms.dbtype.PositionValuationType

object DefaultValues {
  
  val CALENDAR_FIND_NUMBER = 14

  val CALENDAR_FIND_RATIO = 1.5
  
  val CARRYING_VALUE_TYPE = 1L
  
  val CASH_PRICE = BigDecimalConstants.ONE
  
  val CHANGE_LAST_WEEK = 0
  
  val CHANGE_LAST_MONTH = 1
  
  val CHANGE_LAST_QUATER = 2
  
  val CHANGE_LAST_HALFYEAR = 3
  
  val CHANGE_LAST_YEAR = 4
  
  val CHANGE_YEAR_TO_DATE = 5

  val CURRENCY_CODE = "CNY"
    
  val POSITION_VALUATION_TYPE = PositionValuationType.MARKET;
  
  val SH_STOCK_EXCHANGE_CODE = "XSHG"

  val STOCK_INDEX_FUTURE_PRICE_RATIO = 300
  
  val THIS_FUND = "本基金"
}
