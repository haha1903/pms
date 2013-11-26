package com.datayes.invest.pms.logic.positionyield.impl

import com.datayes.invest.pms.entity.account.Position
import org.joda.time.LocalDate
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.logic.positionyield.impl.generic.GenericSecurityYieldCalc


class StockYieldCalc extends GenericSecurityYieldCalc {

  override protected def calculateDailyInterest(positions: List[Position], asOfDate: LocalDate, carryingValues: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

  override protected def calculateInCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    calculateInOutCamt(positions, asOfDate, TradeSide.BUY)
  }

  override protected def calculateOutCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    calculateInOutCamt(positions, asOfDate, TradeSide.SELL)
  }
}
