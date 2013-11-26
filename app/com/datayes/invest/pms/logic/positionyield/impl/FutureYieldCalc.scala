package com.datayes.invest.pms.logic.positionyield.impl

import com.datayes.invest.pms.logic.positionyield.impl.generic.GenericSecurityYieldCalc
import com.datayes.invest.pms.entity.account.Position
import org.joda.time.LocalDate
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.util.BigDecimalConstants


class FutureYieldCalc extends GenericSecurityYieldCalc {

  override protected def calculateInCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    val buyFutureMap = calculateInOutCamt(positions, asOfDate, TradeSide.BUY)
    val shortFutureMap = calculateInOutCamt(positions, asOfDate, TradeSide.SHORT)

    mergeMap(buyFutureMap, shortFutureMap)
  }

  override protected def calculateOutCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    val sellFutureMap = calculateInOutCamt(positions, asOfDate, TradeSide.SELL)
    val shortFutureMap = calculateInOutCamt(positions, asOfDate, TradeSide.COVER)

    mergeMap(sellFutureMap, shortFutureMap)
  }

  private def mergeMap(map1: Map[Long, (BigDecimal, BigDecimal)], map2: Map[Long, (BigDecimal, BigDecimal)]): Map[Long, (BigDecimal, BigDecimal)] = {
    map1 ++ map2.map{ case(k, v) => {
      val tupleValue = map1.get(k).getOrElse((BigDecimalConstants.ZERO, BigDecimalConstants.ZERO))
      k -> ((v._1 + tupleValue._1, v._2 + tupleValue._2))
    }}
  }

}
