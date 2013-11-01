package com.datayes.invest.pms.logic.transaction

import org.joda.time.LocalDate

import com.datayes.invest.pms.dbtype.TradeSide

case class Transaction(
  var accountId: Long,
  var securityId: Long,
  var sourceTransactionId: String,
  var traderId: Option[Long],
  var brokerId: Option[Long],
  var executionDate: LocalDate,
  var settlementDate: LocalDate,
  var side: TradeSide,
  var price: BigDecimal,
  var amount: BigDecimal,
  var transactionSourceId: Integer,
  var transactionClass: String)
