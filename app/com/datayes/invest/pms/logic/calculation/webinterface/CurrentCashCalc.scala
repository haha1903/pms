package com.datayes.invest.pms.logic.calculation.webinterface


object CurrentCashCalc {

  def calculateCurrentCash(cash: BigDecimal, payableValue: BigDecimal, receivableValue: BigDecimal): BigDecimal = {
    cash - payableValue + receivableValue
  }

}
