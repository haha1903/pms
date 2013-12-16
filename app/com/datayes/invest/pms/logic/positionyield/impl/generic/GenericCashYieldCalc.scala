package com.datayes.invest.pms.logic.positionyield.impl.generic

import com.datayes.invest.pms.entity.account.{SecurityPosition, Position}
import org.joda.time.LocalDate
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.logic.calculation.positionyield.SingleCashYieldCalc
import com.datayes.invest.pms.dbtype.CashTransactionType
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.CashTransactionDao
import scala.collection.JavaConversions._

abstract class GenericCashYieldCalc extends GenericYieldCalc with SingleCashYieldCalc {

  @Inject
  private var cashTransactionDao: CashTransactionDao = null

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
      val rate = BigDecimalConstants.ZERO
      val dailyInterest = calculateSingleDailyInterest(carryingValue, rate)
      (positionId, dailyInterest)
    }).toMap
  }

  // TODO: develop in 1.3 version
  /*
  override protected def calculateInCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    calculateInOutCamt(positions, asOfDate, CashTransactionType.CREDIT)
  }

  override protected def calculateOutCamt(positions: List[Position], asOfDate: LocalDate): Map[Long, (BigDecimal, BigDecimal)] = {
    calculateInOutCamt(positions, asOfDate, CashTransactionType.DEBIT)
  }

  protected def calculateInOutCamt(positions: List[Position], asOfDate: LocalDate, typeCode: CashTransactionType): Map[Long, (BigDecimal, BigDecimal)] = {
    val accountId = positions(0).getAccountId
    val securityIds = positions.map(position => position.asInstanceOf[SecurityPosition].getSecurityId)

    val transactionList = cashTransactionDao.findByAccountIdAsOfDateTypeCode(accountId, asOfDate, typeCode)


  }
  */
}
