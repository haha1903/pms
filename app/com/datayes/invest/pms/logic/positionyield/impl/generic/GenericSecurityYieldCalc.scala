package com.datayes.invest.pms.logic.positionyield.impl.generic

import java.lang.{Long=> JLong}
import com.datayes.invest.pms.dao.security.EquityDividendDao
import com.datayes.invest.pms.dao.account.{PositionValuationHistDao, CarryingValueHistDao, SecurityTransactionDao}
import com.datayes.invest.pms.entity.account._
import com.datayes.invest.pms.entity.security.EquityDividend
import com.datayes.invest.pms.dbtype.{LedgerType, TradeSide}
import javax.inject.Inject
import org.joda.time.LocalDate
import scala.collection.JavaConversions._
import com.datayes.invest.pms.service.calendar.CalendarService
import com.datayes.invest.pms.logic.positionyield.impl.singleyield.SingleSecurityYieldCalc
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.util.BigDecimalConstants


abstract class GenericSecurityYieldCalc extends GenericYieldCalc with SingleSecurityYieldCalc {
  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null

  @Inject
  private var equityDividendDao: EquityDividendDao = null

  @Inject
  private var marketDataService: MarketDataService = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  /*
      *
      * Calculation method for Security Position
      *
  */
  override protected def calculateDailyInterest(positions: List[Position], asOfDate: LocalDate, carryingValues: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    defaultSimpleMap
  }

  override protected def calculatePositionCarryingValue(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val positionIds = positions.map(_.getId)
    val carryingValueHists = carryingValueHistDao.findByPositionIdListTypeIdAsOfDate(positionIds, DefaultValues.CARRYING_VALUE_TYPE, asOfDate).toList
    // Covert Carrying Value History List to Map with its position id as the key
    val carryingValueMap = convertListToMapWithId(carryingValueHists, positionIds, {hist => hist.asInstanceOf[CarryingValueHist].getPK.getPositionId})
    convertToValueMap(carryingValueMap, {value => value.asInstanceOf[CarryingValueHist].getValueAmount})
  }

  override protected def calculateDividend(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val positionHistQuantities = getPositionHistQuantity(positions, asOfDate)
    val securityIds = positions.map(position => position.asInstanceOf[SecurityPosition].getSecurityId)

    val dividendList = equityDividendDao.findBySecurityIdsExDiviDate(securityIds, asOfDate).toList
    // Key: securityId, value: EquityDividend
    val dividendMap = convertListToMapWithId(dividendList, securityIds, {dividend => dividend.asInstanceOf[EquityDividend].getSecurityId})
    val rmbMap = convertToValueMap(dividendMap, {dividend => dividend.asInstanceOf[EquityDividend].getActualCashDivirmb})

    positions.map(position => {
      val positionId = position.getId.toLong
      val securityId = position.asInstanceOf[SecurityPosition].getSecurityId

      val posQuantity = positionHistQuantities(positionId)
      val cashDividendRate = rmbMap(securityId)

      val dividendYield =  if (cashDividendRate != null && cashDividendRate > BigDecimalConstants.ZERO) {
        calculateSingleDividend(cashDividendRate, posQuantity)
      }
      else {
        BigDecimalConstants.ZERO
      }
      (positionId, dividendYield)
    }).toMap
  }

  override protected def calculatePriceDiff(positions: List[Position], outCamts: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    // Use last day's carrying value to do calculation, because it is before transaction
    val lastDay = calendarService.previousCalendarDay(asOfDate)
    val positionHistQuantities = getPositionHistQuantity(positions, lastDay)
    val posCarryingValues = calculatePositionCarryingValue(positions, lastDay)

    posCarryingValues.map( kv => {
      val positionId = kv._1
      val carryingValue = kv._2
      val positionQuantity = positionHistQuantities(positionId)
      val sellTransaction = outCamts(positionId)
      val priceDiff = if ( BigDecimal(0) == positionQuantity ) {
        BigDecimal(0)
      }
      else {
        calculateSinglePriceDiff(carryingValue, positionQuantity, sellTransaction)
      }
      (positionId, priceDiff)
    }).toMap
  }



  override protected def calculateIncrement(earnLoss: Map[Long, BigDecimal], priceDiffs: Map[Long, BigDecimal]): Map[Long, BigDecimal] = {
    earnLoss.map( kv => {
      val positionId = kv._1
      val singleEarnLoss = kv._2
      val priceDiff = priceDiffs(positionId)
      val increment = calculateSingleIncrement(singleEarnLoss, priceDiff)
      (positionId, increment)
    })
  }

  override protected def calculateTradeEarn(positions: List[Position], inCamt: Map[Long, (BigDecimal, BigDecimal)], outCamt: Map[Long, (BigDecimal, BigDecimal)], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val securityIds = positions.map(position => position.asInstanceOf[SecurityPosition].getSecurityId).toSet
    val marketDataMap = marketDataService.getMarketData(securityIds, asOfDate).toMap
    val posSecMap: Map[Long, Long] = positions.map(position => (position.getId.toLong, position.asInstanceOf[SecurityPosition].getSecurityId.toLong)).toMap

    inCamt.map { kv =>
      val positionId = kv._1
      val buyTransaction = kv._2
      val sellTransaction = outCamt(positionId)
      val securityId = posSecMap(positionId)
      val marketDataOpt = marketDataMap.get(securityId)
      if ( marketDataOpt.nonEmpty ) {
        val price = marketDataOpt.get.getPrice
        val tradeEarn = calculateSingleTradeEarn(buyTransaction, sellTransaction, price)
        (positionId, tradeEarn)
      }
      else {
        logger.warn("Cannot get market data for Security Id: {} on {}", securityId, asOfDate)
        (positionId, BigDecimalConstants.ZERO)
      }
      
    }
  }

  /*
    *
    * Inheritance methods for sub-classes
    *
   */
  protected def calculateInOutCamt(positions: List[Position], asOfDate: LocalDate, tradeSide: TradeSide): Map[Long, (BigDecimal, BigDecimal)] = {
    val securityIds = positions.map(position => position.asInstanceOf[SecurityPosition].getSecurityId)

    val transactionList = securityTransactionDao.findTransactionsExecDateTradeSide(securityIds, asOfDate, tradeSide).toList
    val transactionMap = transactionList.groupBy(_.getSecurityId)

    positions.map(position => {
      val positionId: Long = position.getId
      val securityId: Long = position.asInstanceOf[SecurityPosition].getSecurityId

      val oneSecTransactionsOpt = transactionMap.get(securityId)

      val quantitySum = if ( oneSecTransactionsOpt.nonEmpty ) {
        sumSingleSecurityTransaction(oneSecTransactionsOpt.get)
      }
      else {
        (BigDecimal(0),BigDecimal(0))
      }
      (positionId, quantitySum)
    }).toMap
  }

  /*
    * Private methods
   */
  private def getPositionHistQuantity(positions: List[Position], asOfDate: LocalDate): Map[Long, BigDecimal] = {
    val positionIds = positions.map(_.getId)
    val positionHistList = positionHistDao.findByPositionIdListAsOfDate(positionIds, asOfDate).toList
    // Key: positionId, value: PositionHist
    val positionHistMap = convertListToMapWithId(positionHistList, positionIds, {hist => hist.asInstanceOf[PositionHist].getPK.getPositionId})
    convertToValueMap(positionHistMap, {hist => hist.asInstanceOf[PositionHist].getQuantity})
  }
}
