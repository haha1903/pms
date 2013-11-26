package com.datayes.invest.pms.logic.positionyield.impl.generic

import java.lang.{Long=> JLong}
import com.datayes.invest.pms.dao.account._
import javax.inject.Inject
import com.datayes.invest.pms.entity.account._
import org.joda.time.LocalDate
import scala.collection.JavaConversions._
import com.datayes.invest.pms.logic.positionyield.PositionYieldCalc
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.account.PositionYield
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.security.EquityDividend
import scala.Some
import com.datayes.invest.pms.service.calendar.CalendarService
import com.datayes.invest.pms.logic.positionyield.impl.singleyield.SingleGenericYieldCalc


abstract class GenericYieldCalc extends PositionYieldCalc with SingleGenericYieldCalc with Logging {
  protected val currency_type: Char = 'A'
  protected val isLocked = 'N'
  protected val lastUserId = "SYSTEM"
  protected val valuationType = DefaultValues.POSITION_VALUATION_TYPE

  protected var defaultSimpleMap = Map.empty[Long, BigDecimal]
  protected var defaultTupleMap = Map.empty[Long, (BigDecimal, BigDecimal)]

  @Inject
  protected var calendarService: CalendarService = null

  @Inject
  protected var positionHistDao: PositionHistDao = null

  @Inject
  protected var positionValuationHistDao: PositionValuationHistDao = null

  @Inject
  protected var positionYieldDao: PositionYieldDao = null


  override def process(positions: List[Position], asOfDate: LocalDate) {
    if ( !positions.isEmpty ) {
      defaultSimpleMap = createDefaultSimpleMap(positions)
      defaultTupleMap = createDefualtTupleMap(positions)

      val posCarryingValues = calculatePositionCarryingValue(positions, asOfDate)
      val secCarryingValues = calculateSecurityCarryingValue(positions, asOfDate)
      val dailyInterest = calculateDailyInterest(positions, asOfDate, posCarryingValues)
      val dividends = calculateDividend(positions, asOfDate)
      val inCamts = calculateInCamt(positions, asOfDate)
      val outCamts = calculateOutCamt(positions, asOfDate)
      val priceDiffs = calculatePriceDiff(positions, outCamts, asOfDate)
      val beginValues = calculateBeginValue(positions, asOfDate)
      val endValues = calculateEndValue(positions, asOfDate)
      val earnLoss = calculateEarnLoss(beginValues, endValues, inCamts, outCamts, dividends)
      val increments = calculateIncrement(earnLoss, priceDiffs)
      val tradeEarn = calculateTradeEarn(positions, inCamts, outCamts, asOfDate)

      val positionYields = positions.map(position => {
        val positionId: Long = position.getId
        val securityId: JLong = position match {
          case p: SecurityPosition => p.getSecurityId
          case _ => null
        }
        new PositionYield(
          asOfDate,
          positionId,
          position.getAccountId,
          securityId,
          currency_type,
          position.getCurrencyCode,
          posCarryingValues(positionId),
          secCarryingValues(positionId),
          dailyInterest(positionId),
          dividends(positionId),
          increments(positionId),
          priceDiffs(positionId),
          beginValues(positionId),
          endValues(positionId),
          inCamts(positionId)._2,
          outCamts(positionId)._2,
          earnLoss(positionId),
          tradeEarn(positionId),
          lastUserId,
          isLocked)
      })
      savePositionYields(positionYields, asOfDate)
    }
    else {
      logger.warn("No position found, to check the ledger type, see last log")
    }
  }


  /*
    *
    * Common calculation logic
    * 1. security carrying value
    * 2. Begin Value
    * 3. End Value
    * 4. Earn Loss
    *
   */
  override protected def calculateSecurityCarryingValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    //TODO: Need to check the difference between position carrying value and this one
    defaultSimpleMap
  }

  override protected def calculateBeginValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val lastDay = calendarService.previousCalendarDay(asOfDate)
    getPositionValuationHistAmount(positions, lastDay)
  }

  override protected def calculateEndValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    getPositionValuationHistAmount(positions, asOfDate)
  }

  override protected def calculateEarnLoss(beginValues: Map[Long, BigDecimal], endValues: Map[Long, BigDecimal], inCamts: Map[Long, (BigDecimal, BigDecimal)], outCamts: Map[Long, (BigDecimal, BigDecimal)], dividends: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    beginValues.map( kv => {
      val positionId = kv._1
      val beginValue = kv._2
      val endValue = endValues(positionId)
      val inCamtValue = inCamts(positionId)._2
      val outCamtValue = outCamts(positionId)._2
      val dividend = dividends(positionId)
      val earnLoss = calculateSingleEarnLoss(beginValue, endValue, inCamtValue, outCamtValue, dividend)
      (positionId, earnLoss)
    })
  }

  /*
    *
    * Common operation logic
    * 1. save Position Yield to DB
    * 2. convert a list to a map by setting its id as the key of map
    *
   */
  protected def getPositionValuationHistAmount(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val positionIds = positions.map(_.getId)
    val pvhistList = positionValuationHistDao.findByPositionIdListTypeIdAsOfDate(positionIds, valuationType.getDbValue, asOfDate).toList

    val pvhistMap = convertListToMapWithId(pvhistList, positionIds, {hist => hist.asInstanceOf[PositionValuationHist].getPK.getPositionId})
    convertToValueMap(pvhistMap, {hist => hist.asInstanceOf[PositionValuationHist].getValueAmount})
  }

  protected def savePositionYields(positionYields: List[PositionYield], asOfDate: LocalDate) {
    // Yields to be saved / updated in the DB
    val yieldPositionIds = positionYields.map(_.getPositionId)
    // Yields that are already in the DB
    val stockedYields = positionYieldDao.findByPositionIdsAsOfDate(yieldPositionIds, asOfDate).toList
    val stockedYieldsMap = stockedYields.map(sy => (sy.getPositionId, sy)).toMap

    positionYields.foreach(py => {
      val positionId = py.getPositionId
      val sy = stockedYieldsMap.get(positionId)
      if ( sy.nonEmpty ) {
        val id = sy.get.getId
        py.setId(id)
        positionYieldDao.update(py)
      }
      else {
        positionYieldDao.save(py)
      }
    })
  }

  protected def convertListToMapWithId[T >: AnyRef](list: List[T], idList: List[JLong], idGetter: T => Long): Map[Long, Option[T]] = {
    val tmpMap = collection.mutable.Map.empty[Long, Option[T]]
    idList.foreach(id => tmpMap.put(id.toLong, None))

    list.foreach{
      element =>
        val id = idGetter(element)
        tmpMap.put(id, Some(element))
    }

    // Convert mutable map to immutable
    tmpMap.map(kv => (kv._1, kv._2)).toMap
  }

  protected def convertToValueMap[T >: AnyRef](objectMap: Map[Long, Option[T]], valueGetter: T => BigDecimal): Map[Long, BigDecimal] = {
    objectMap.map(kv => {
      val key = kv._1
      kv._2 match {
        case Some(o) => (key, valueGetter(o))
        case _ => (key, BigDecimal(0))
      }
    }).toMap
  }


  /*
    *
    * Private methods
    *
   */
  private def createDefaultSimpleMap(positions: List[Position]): Map[Long, BigDecimal] = {
    positions.map(position => (position.getId.toLong, BigDecimal(0))).toMap
  }
  
  private def createDefualtTupleMap(positions: List[Position]): Map[Long, (BigDecimal, BigDecimal)] = {
    positions.map(position => (position.getId.toLong, (BigDecimal(0), BigDecimal(0)))).toMap
  }

}



