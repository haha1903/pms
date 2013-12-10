package com.datayes.invest.pms.logic.calculation.positionyield

trait SingleCashYieldCalc extends SingleGenericYieldCalc {

  def calculateSingleDailyInterest(carryingValue: BigDecimal, rate: BigDecimal): BigDecimal = {
    carryingValue * rate
  }

}
