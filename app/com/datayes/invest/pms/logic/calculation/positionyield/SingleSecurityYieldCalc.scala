package com.datayes.invest.pms.logic.calculation.positionyield

import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.logic.calculation.marketvalue.MarketValueCalc


trait SingleSecurityYieldCalc extends SingleGenericYieldCalc with Logging {

  def calculateSingleDividend(cashDividendRate: BigDecimal, posQuantity: BigDecimal): BigDecimal = {
    val factor = BigDecimal(10)
    cashDividendRate * posQuantity / factor
  }

  def calculateSinglePriceDiff(carryingValue: BigDecimal, positionQuantity: BigDecimal, sellTransaction: (BigDecimal, BigDecimal)): BigDecimal = {
    val sellTransactionValue = sellTransaction._2
    val sellTransactionQuantity = sellTransaction._1

    sellTransactionValue - sellTransactionQuantity / positionQuantity * carryingValue
  }

  def sumSingleSecurityTransaction(transactions: List[SecurityTransaction], ledgerType: LedgerType, ratio: Option[BigDecimal]): (BigDecimal, BigDecimal) = {
    val sumQuantity = transactions.foldLeft(BigDecimalConstants.ZERO)(_ + _.getAmount)
    val sumValue = transactions.foldLeft(BigDecimalConstants.ZERO)((start, transaction) => {
      val value = if ( LedgerType.SECURITY == ledgerType ) {
        MarketValueCalc.calculateEquityValue(transaction.getAvgPrice, transaction.getAmount)
      }
      else {
        MarketValueCalc.calculateFutureValue(transaction.getAvgPrice, transaction.getAmount, ratio.getOrElse(BigDecimalConstants.ONE))
      }
      start + value
    })
    (sumQuantity, sumValue)
  }

  def calculateSingleIncrement(earnLoss: BigDecimal, priceDiff: BigDecimal): BigDecimal = {
    earnLoss - priceDiff
  }

  def calculateSingleTradeEarn(buyValue: BigDecimal, sellValue: BigDecimal, buySellDiff: BigDecimal): BigDecimal = {
    buySellDiff - buyValue + sellValue
  }
}
