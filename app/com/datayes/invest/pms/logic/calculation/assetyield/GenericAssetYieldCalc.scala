package com.datayes.invest.pms.logic.calculation.assetyield


object GenericAssetYieldCalc {

  def calculateEarnLossRate(earnLoss: BigDecimal, beginValue: BigDecimal, inCamt: BigDecimal): BigDecimal = {
    earnLoss / (beginValue + inCamt)
  }

}
