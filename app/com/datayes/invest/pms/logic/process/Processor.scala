package com.datayes.invest.pms.logic.process

import org.joda.time.LocalDate
import com.datayes.invest.pms.entity.account.Account

trait Processor {
  
  def process(account: Account, asOfDate: LocalDate): Unit
}
