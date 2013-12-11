package com.datayes.invest.pms.tools.importer

import com.datayes.invest.pms.logic.accountinit.PositionSourceData
import com.datayes.invest.pms.entity.account.SourceTransaction
import com.datayes.invest.pms.dbtype.LedgerType
import javax.inject.Inject
import com.datayes.invest.pms.dao.security.SecurityDao
import play.pms.ClientException
import com.datayes.invest.pms.entity.security.Equity
import com.datayes.invest.pms.service.marketdata.MarketDataService
import org.joda.time.LocalDate
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.entity.security.Security
import org.joda.time.LocalTime
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass

class EquityRecordHandler extends SecurityRecordHandlerBase {
  
  def matches(context: Context, values: Array[String]): Boolean = {
    try {
      Integer.parseInt(values(0))
      true
    } catch {
      case e: NumberFormatException => false
    }
  }
  
  def createInitialPosition(context: Context, values: Array[String]): PositionSourceData = {
    val symbol =  values(0)
    val equity = loadSecurity(symbol) match {
      case Some(e: Equity) => e
      case Some(_) => throw new ClientException("Security " + symbol + " is not a stock")
      case _ => throw new ClientException("Security not found for symbol " + symbol)
    }
    
    val currency = if (equity.getIssueCurrency() != null) {
      equity.getIssueCurrency()
    } else {
      context.currency
    }
    
    val quantity = BigDecimal(values(1))
    if (quantity < 0) {
      throw new RuntimeException("Equity initial position's quantity cannot be less than zero")
    }
    
    val price = getPrice(equity, context.openDate.toLocalDate(), values)
    val carryingValue = price * quantity
    
    val psd = PositionSourceData(
      positionClass = PositionClass.SECURITY,
      ledgerType = LedgerType.SECURITY,
      openDate = context.openDate,
      currencyCode = currency,
      quantity = quantity,
      carryingValue = Some(carryingValue),
      securityId = Some(equity.getId),
      exchangeCode = equity.getExchangeCode()
    )
    
    psd
  }
  
  def createSourceTransaction(context: Context, values: Array[String]): SourceTransaction = {
    val symbol = values(0)
    val equity = loadSecurity(symbol) match {
      case Some(e: Equity) => e
      case Some(_) => throw new ClientException("Security " + symbol + " is not a stock")
      case _ => throw new ClientException("Security not found for symbol " + symbol)
    }
    
    val quantityDelta = BigDecimal(values(1))
    val tradeSide = if (quantityDelta > 0) {
      TradeSide.BUY
    } else {
      TradeSide.SELL
    }
    
    val price = getPrice(equity, context.asOfDate, values)
    
    val srcTransaction = new SourceTransaction(
      context.accountId,
      equity.getId,
      "",   // sourceTransactionId
      null, // orderId
      null, // traderId
      null, // brokerId
      context.asOfDate.toLocalDateTime(LocalTime.MIDNIGHT), // executionDate
      context.asOfDate, // settlementData
      tradeSide.getDbValue(),
      price,
      quantityDelta.abs,
      TransactionSource.PMS.getDbValue,
      TransactionClass.TRADE.toString)
    
    srcTransaction
  }
  
  private def getPrice(equity: Equity, asOfDate: LocalDate, values: Array[String]): BigDecimal = {
    if (values.size >= 3) {
      return BigDecimal(values(2))
    }
    val md = marketDataService.getMarketData(equity.getId(), asOfDate)
    if (md.getPrice() != null) {
      return md.getPrice()
    }
    throw new RuntimeException("Failed to find price for equity " + equity.getId() + " on " + asOfDate)
  }
}