package com.datayes.invest.pms.logic.positionyield.impl.generic

import com.datayes.invest.pms.entity.account.Position
import org.joda.time.LocalDate
import com.datayes.invest.pms.logic.positionyield.impl.singleyield.SingleCashYieldCalc


abstract class GenericCashYieldCalc extends GenericYieldCalc with SingleCashYieldCalc {
  /*
    *
    * Calculation Method for Cash Position
    *
   */
  override protected def calculatePositionCarryingValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    getPositionValuationHistAmount(positions, asOfDate)
  }

  override protected def calculateDailyInterest(positions: List[Position], asOfDate: LocalDate, carryingValues: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    positions.filter(position => carryingValues.contains(position.getId)).map(position => {
      val positionId = position.getId.toLong
      val carryingValue = carryingValues(positionId)
      val rate = BigDecimal(0)
      val dailyInterest = calculateSingleDailyInterest(carryingValue, rate)
      (positionId, dailyInterest)
    }).toMap
  }
}
