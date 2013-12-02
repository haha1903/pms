package com.datayes.invest.pms.logic.transaction

import org.joda.time.{LocalDateTime, LocalDate}
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionClass

case class Transaction(
  var accountId: Long,
  var securityId: Long,
  var sourceTransactionId: String,
  var traderId: Option[Long],
  var brokerId: Option[Long],
  var executionDate: LocalDateTime,
  var settlementDate: LocalDate,
  var side: TradeSide,
  var price: BigDecimal,
  var amount: BigDecimal,
  var transactionSourceId: Integer,
  var transactionClass: TransactionClass)
