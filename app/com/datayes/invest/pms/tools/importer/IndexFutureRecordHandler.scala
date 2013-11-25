package com.datayes.invest.pms.tools.importer

import com.datayes.invest.pms.logic.accountinit.PositionSourceData
import com.datayes.invest.pms.entity.account.SourceTransaction
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.security.Future
import play.pms.ClientException
import org.joda.time.LocalDate
import com.datayes.invest.pms.util.FutureMultiplierHelper
import org.joda.time.LocalTime
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass

class IndexFutureRecordHandler extends SecurityRecordHandlerBase {

  def matches(context: Context, values: Array[String]): Boolean = {
    val symbol = values(0)
    return symbol != null && symbol.toUpperCase().startsWith("IF")
  }
  
  def createInitialPosition(context: Context, values: Array[String]): PositionSourceData = {
    if (values.size < 3) {
      throw new RuntimeException("Import file format error. Not enough argument for index future.")
    }
    val sLongShort = values(1).toUpperCase()
    val ledger = if (sLongShort == "LONG") {
      LedgerType.FUTURE_LONG
    } else if (sLongShort == "SHORT") {
      LedgerType.FUTURE_SHORT
    } else {
      throw new RuntimeException("Import file format error. Wrong type for index future: " + sLongShort)
    }
    
    val symbol = values(0)
    val future = loadSecurity(symbol) match {
      case Some(f: Future) => f
      case Some(_) => throw new ClientException("Security " + symbol + " is not an index future")
      case None => throw new ClientException("Security not found for symbol " + symbol)
    }
    
    val currency = if (future.getCurrencyCode() != null) {
      future.getCurrencyCode()
    } else {
      context.currency
    }
    
    val ratio = FutureMultiplierHelper.getRatio(future.getContractMultiplier())
    val quantity = BigDecimal(values(2))
    val price = getPrice(future, context.openDate.toLocalDate(), values)
    val carryingValue = quantity * price * ratio
    
    val psd = PositionSourceData(
      positionClass = PositionClass.SECURITY,
      ledgerType = ledger,
      openDate = context.openDate,
      currencyCode = currency,
      quantity = quantity,
      carryingValue = Some(carryingValue),
      securityId = Some(future.getId),
      exchangeCode = future.getExchangeCode()
    )
    
    psd
  }
  
  def createSourceTransaction(context: Context, values: Array[String]): SourceTransaction = {
    if (values.size < 3) {
      throw new RuntimeException("Import file format error. Not enough argument for index future.")
    }
    val sLongShort = values(1).toUpperCase()
    val ledger = if (sLongShort == "LONG") {
      LedgerType.FUTURE_LONG
    } else if (sLongShort == "SHORT") {
      LedgerType.FUTURE_SHORT
    } else {
      throw new RuntimeException("Import file format error. Wrong type for index future: " + sLongShort)
    }
    val symbol = values(0)
    val future = loadSecurity(symbol) match {
      case Some(f: Future) => f
      case Some(_) => throw new ClientException("Security " + symbol + " is not an index future")
      case None => throw new ClientException("Security not found for symbol " + symbol)
    }
    
    val quantityDelta = BigDecimal(values(2))
    val tradeSide = if (ledger == LedgerType.FUTURE_LONG) {
      if (quantityDelta >= 0) TradeSide.BUY else TradeSide.SELL
    } else {
      if (quantityDelta >= 0) TradeSide.SHORT else TradeSide.COVER
    }
    val price = getPrice(future, context.openDate.toLocalDate(), values)
    
    val srcTransaction = new SourceTransaction(
      context.accountId,
      future.getId,
      "",
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

  private def getPrice(future: Future, asOfDate: LocalDate, values: Array[String]): BigDecimal = {
    if (values.size >= 4) {
      return BigDecimal(values(3))
    }
    val md = marketDataService.getMarketData(future.getId(), asOfDate)
    if (md.getPrice() != null) {
      return md.getPrice()
    }
    throw new RuntimeException("Failed to find price for index future " + future.getId())
  }
}