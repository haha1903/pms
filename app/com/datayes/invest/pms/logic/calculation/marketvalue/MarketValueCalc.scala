package com.datayes.invest.pms.logic.calculation.marketvalue


object MarketValueCalc {

  def calculateEquityValue(price: BigDecimal, amount: BigDecimal): BigDecimal = {
    price * amount
  }

  def calculateFutureValue(price: BigDecimal, amount: BigDecimal, ratio: BigDecimal): BigDecimal = {
    price * amount * ratio
  }

}
