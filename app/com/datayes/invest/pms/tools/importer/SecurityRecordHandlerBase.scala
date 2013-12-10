package com.datayes.invest.pms.tools.importer

import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.entity.security.Security
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.marketdata.MarketDataService
import javax.inject.Inject

abstract class SecurityRecordHandlerBase extends RecordHandler with Logging {
  
  @Inject
  protected var marketDataService: MarketDataService = null

  @Inject
  protected var securityDao: SecurityDao = null

  protected def loadSecurity(symbol: String): Option[Security] = {
    val resolver = new TickerResolver(securityDao)
    val security = resolver.loadSecurity(symbol)
    if (security == null) {
      throw new RuntimeException("Import error. Cannot find security for symbol " + symbol)
    } else {
      Some(security)
    }
  }
  
  /*protected def loadSecurity(symbol: String): Option[Security] = {
    val fixedSymbol = fixSecuritySymbol(symbol)
    val list = securityDao.findByTickerSymbol(fixedSymbol)
    if (list == null || list.isEmpty) {
      throw new RuntimeException("Import error. Cannot find security for symbol " + fixedSymbol)
    }
    if (list.size() > 1) {
      logger.warn("Multiple security found for symbol " + fixedSymbol)
      list.find(isAGuStock(_))
    } else {
      Some(list(0))
    }

  }
  
  private def isAGuStock(security: Security): Boolean = {
    security match {
      case e: Equity => e.getTypeCode() == 1
      case _ => false
    }
  }
  
  private def fixSecuritySymbol(symbol: String): String = {
    try {
      Integer.valueOf(symbol)
    } catch {
      case e: NumberFormatException =>
        return symbol
    }
    val builder = new StringBuilder
    for (i <- 0 until (6 - symbol.size)) {
      builder.append("0")
    }
    builder.toString + symbol
  }*/
}