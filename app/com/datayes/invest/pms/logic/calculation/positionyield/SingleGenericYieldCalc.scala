package com.datayes.invest.pms.logic.calculation.positionyield

trait SingleGenericYieldCalc {

  def calculateSingleEarnLoss(beginValue: BigDecimal, endValue: BigDecimal, inCamt: BigDecimal, outCamt: BigDecimal, dividend: BigDecimal): BigDecimal = {
    endValue + outCamt + dividend - beginValue - inCamt
  }

}
