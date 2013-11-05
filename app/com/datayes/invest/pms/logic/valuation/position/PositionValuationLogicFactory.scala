package com.datayes.invest.pms.logic.valuation.position

import com.datayes.invest.pms.logic.valuation.position.impl.MarketValuationLogic
import javax.inject.Inject
import com.datayes.invest.pms.dbtype.PositionValuationType
import com.datayes.invest.pms.logging.Logging


class PositionValuationLogicFactory extends Logging {
  
  @Inject
  private var marketValuationLogic: MarketValuationLogic = null
  
  def get(t: PositionValuationType): PositionValuationLogic = {
    if (t == PositionValuationType.MARKET) {
      return marketValuationLogic;
    }
    
    logger.error("Unable to find position valuation logic for type: " + t)
    return null;
  }
}