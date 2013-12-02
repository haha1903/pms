package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.web.model.models.Trade
import org.joda.time.LocalDate

class TradeService {

  def getHistory(accountIdOpt: Option[Long], asOfDateOpt: Option[LocalDate]): List[Trade] = {
    List()
  }
}