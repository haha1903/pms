package com.datayes.invest.pms.util

import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.logic.accountinit.RateSourceData
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.TradeSide

object DefaultValues {
  
  val BENCHMARK_MARKET_INDEX = "HSSLL"
  
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
    
  val INDUSTRY_DATA_SOURCE_ID = 9    // 申万行业分类

  val MARKETDATA_SCHEDULER_INTERVAL = 60 * 1000  // 1 minute
    
  val PMS_SOURCE_TRANSACTION_ID  = "0"
    
  val POSITION_VALUATION_TYPE = PositionValuationType.MARKET
  
  val SH_STOCK_EXCHANGE_CODE = "XSHG"

  val STOCK_INDEX_FUTURE_PRICE_RATIO = 300
  
  val THIS_FUND = "本基金"

    
  val DEFAULT_FEES = List(
    RateSourceData(RateType.FutureCommission, Some(TradeSide.BUY), None, BigDecimal(0.00001)),
    RateSourceData(RateType.FutureCommission, Some(TradeSide.SELL), None, BigDecimal(0.00001)),
    RateSourceData(RateType.FutureCommission, Some(TradeSide.COVER), None, BigDecimal(0.00001)),
    RateSourceData(RateType.FutureCommission, Some(TradeSide.SHORT), None, BigDecimal(0.00001)),
    RateSourceData(RateType.FutureDeliveryCharges, Some(TradeSide.SHORT), None, BigDecimal(0.00005)),
    RateSourceData(RateType.FutureDeliveryCharges, Some(TradeSide.SELL), None, BigDecimal(0.00005)),
    RateSourceData(RateType.FutureDeliveryCharges, Some(TradeSide.COVER), None, BigDecimal(0.00005)),
    RateSourceData(RateType.FutureDeliveryCharges, Some(TradeSide.SHORT), None, BigDecimal(0.00005)),
    RateSourceData(RateType.FutureMinMarginRatio, Some(TradeSide.SHORT), None, BigDecimal(0.25)),
    RateSourceData(RateType.FutureMinMarginRatio, Some(TradeSide.SHORT), None, BigDecimal(0.25)),
    RateSourceData(RateType.FutureTransactionFee, Some(TradeSide.SHORT), None, BigDecimal(0.000035)),
    RateSourceData(RateType.FutureTransactionFee, Some(TradeSide.SELL), None, BigDecimal(0.000035)),
    RateSourceData(RateType.FutureTransactionFee, Some(TradeSide.COVER), None, BigDecimal(0.000035)),
    RateSourceData(RateType.FutureTransactionFee, Some(TradeSide.SHORT), None, BigDecimal(0.000035)),
    RateSourceData(RateType.Stamp, Some(TradeSide.SHORT), None, BigDecimal(0.001)),
    RateSourceData(RateType.Stamp, Some(TradeSide.SELL), None, BigDecimal(0.001)),
    RateSourceData(RateType.StockCommission, Some(TradeSide.SHORT), None, BigDecimal(0.0008)),
    RateSourceData(RateType.StockCommission, Some(TradeSide.SELL), None, BigDecimal(0.0008))
  )
}
