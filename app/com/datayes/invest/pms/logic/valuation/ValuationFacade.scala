package com.datayes.invest.pms.logic.valuation

import com.datayes.invest.pms.logic.valuation.account.AccountValuationLogicFactory
import javax.inject.Inject
import com.datayes.invest.pms.logic.valuation.position.PositionValuationLogicFactory
import com.datayes.invest.pms.entity.account.Account
import org.joda.time.LocalDate
import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.logging.Logging

class ValuationFacade extends Logging {

  @Inject
  private var accountValuationLogicFactory: AccountValuationLogicFactory = null
  
  @Inject
  private var positionValuationLogicFactory: PositionValuationLogicFactory = null
  
  def valuate(account: Account, asOfDate: LocalDate): Unit = {
    // TODO how to synchronize for valuation?
    for (t <- PositionValuationType.values()) {
      val logic = positionValuationLogicFactory.get(t)
      logic.process(account, asOfDate);
    }
    {
      val logic = accountValuationLogicFactory.get()
      logic.process(account, asOfDate)
    }
  }
}