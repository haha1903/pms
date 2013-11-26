package com.datayes.invest.pms.logic.positionyield

import org.joda.time.LocalDate
import com.datayes.invest.pms.entity.account.Position


abstract class PositionYieldCalc {
  def process(positions: List[Position], asOfDate: LocalDate): Unit

  protected def calculatePositionCarryingValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculateSecurityCarryingValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculateDailyInterest(positions: List[Position], asOfDate: LocalDate, carryingValues: Map[Long, BigDecimal]): Map[Long, BigDecimal]

  protected def calculateDividend(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculatePriceDiff(positions: List[Position], outCamts: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculateInCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)]

  protected def calculateOutCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)]

  protected def calculateIncrement(earnLoss: Map[Long, BigDecimal], priceDiffs: Map[Long, BigDecimal]): Map[Long, BigDecimal]

  protected def calculateBeginValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculateEndValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal]

  protected def calculateEarnLoss(beginValues: Map[Long, BigDecimal], endValues: Map[Long, BigDecimal], inCamts: Map[Long, (BigDecimal, BigDecimal)], outCamts: Map[Long, (BigDecimal, BigDecimal)], dividends: Map[Long, BigDecimal]): Map[Long, BigDecimal]

  protected def calculateTradeEarn(positions: List[Position], inCamt: Map[Long, (BigDecimal, BigDecimal)], outCamt: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal]
}
