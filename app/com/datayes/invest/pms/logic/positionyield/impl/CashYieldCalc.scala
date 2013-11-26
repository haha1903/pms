package com.datayes.invest.pms.logic.positionyield.impl

import com.datayes.invest.pms.logic.positionyield.impl.generic.GenericCashYieldCalc
import com.datayes.invest.pms.entity.account.Position
import org.joda.time.LocalDate


class CashYieldCalc extends GenericCashYieldCalc {

  override protected def calculateDividend(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

  override protected def calculatePriceDiff(positions: List[Position], outCamts: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

  override protected def calculateInCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    defaultTupleMap
  }

  override protected def calculateOutCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    defaultTupleMap
  }

  override protected def calculateIncrement(earnLoss: Map[Long, BigDecimal], priceDiffs: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

  override protected def calculateTradeEarn(positions: List[Position], inCamt: Map[Long, (BigDecimal, BigDecimal)], outCamt: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

}
