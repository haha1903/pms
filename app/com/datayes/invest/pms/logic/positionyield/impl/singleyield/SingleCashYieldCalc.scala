package com.datayes.invest.pms.logic.positionyield.impl.singleyield


trait SingleCashYieldCalc extends SingleGenericYieldCalc {

  def calculateSingleDailyInterest(carryingValue: BigDecimal, rate: BigDecimal): BigDecimal = {
    carryingValue * rate
  }

}
