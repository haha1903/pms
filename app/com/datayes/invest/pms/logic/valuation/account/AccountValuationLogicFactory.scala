package com.datayes.invest.pms.logic.valuation.account

import javax.inject.Inject
import com.datayes.invest.pms.logic.valuation.account.impl.AccountValuationLogicImpl

object AccountValuationLogicFactory {
  
  @Inject
  private var accountValuationLogic: AccountValuationLogicImpl = null
  
  def get(): AccountValuationLogic = {
    accountValuationLogic
  }
}
