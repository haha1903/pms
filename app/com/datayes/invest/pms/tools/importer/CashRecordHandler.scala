package com.datayes.invest.pms.tools.importer

import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.logic.accountinit.PositionSourceData
import com.datayes.invest.pms.entity.account.SourceTransaction
import com.datayes.invest.pms.util.DefaultValues

class CashRecordHandler extends RecordHandler {

  def matches(context: Context, values: Array[String]): Boolean = {
    val pc = getPositionClass(values)
    pc == PositionClass.CASH
  }
  
  def createInitialPosition(context: Context, values: Array[String]): PositionSourceData = {
    val ledger = LedgerType.valueOf(values(0))
    val quantity = BigDecimal(values(1))
    
    val psd = PositionSourceData(
      positionClass = PositionClass.CASH,
      ledgerType = ledger,
      openDate = context.openDate,
      currencyCode = context.currency,
      quantity = quantity,
      carryingValue = None,
      securityId = None,
      exchangeCode = DefaultValues.SH_STOCK_EXCHANGE_CODE
    )
    
    psd
  }
  
  def createSourceTransaction(context: Context, values: Array[String]): SourceTransaction = {
    throw new UnsupportedOperationException()
  }
  
  private def getPositionClass(values: Array[String]): PositionClass = {
    try {
      val positionClass = LedgerType.valueOf(values(0)).getPositionClass()
      positionClass
    } catch {
      case e: Throwable => PositionClass.SECURITY
    }
  }
}