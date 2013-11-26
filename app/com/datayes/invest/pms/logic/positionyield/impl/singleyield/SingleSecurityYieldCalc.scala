package com.datayes.invest.pms.logic.positionyield.impl.singleyield

import com.datayes.invest.pms.entity.account.SecurityTransaction
import com.datayes.invest.pms.logging.Logging


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

  def sumSingleSecurityTransaction(transactions: List[SecurityTransaction]): (BigDecimal, BigDecimal) = {
    val sumQuantity = transactions.foldLeft(BigDecimal(0))(_ + _.getAmount)
    val sumValue = transactions.foldLeft(BigDecimal(0))((start, transaction) => start + transaction.getAvgPrice * transaction.getAmount)
    (sumQuantity, sumValue)
  }

  def calculateSingleIncrement(earnLoss: BigDecimal, priceDiff: BigDecimal): BigDecimal = {
    earnLoss - priceDiff
  }

  def calculateSingleTradeEarn(buyTransaction: (BigDecimal, BigDecimal), sellTransaction: (BigDecimal, BigDecimal), price: BigDecimal): BigDecimal = {
    val buyQuantity = buyTransaction._1
    val buyValue = buyTransaction._2

    val sellQuantity = sellTransaction._1
    val sellValue = sellTransaction._2

    (buyQuantity - sellQuantity) * price - buyValue + sellValue
  }
}
