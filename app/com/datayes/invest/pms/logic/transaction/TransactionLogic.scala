package com.datayes.invest.pms.logic.transaction

trait TransactionLogic {

  def process(t: Transaction): Unit
}