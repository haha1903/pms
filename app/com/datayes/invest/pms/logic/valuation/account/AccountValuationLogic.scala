package com.datayes.invest.pms.logic.valuation.account

import org.joda.time.LocalDate
import com.datayes.invest.pms.entity.account.Account

trait AccountValuationLogic {

  def process(account: Account, asOfData: LocalDate): Unit
}