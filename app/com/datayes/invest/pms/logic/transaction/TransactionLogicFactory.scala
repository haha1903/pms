package com.datayes.invest.pms.logic.transaction

import javax.inject.Inject
import com.datayes.invest.pms.logic.transaction.impl.StockIndexFutureTransactionLogic
import com.datayes.invest.pms.logic.transaction.impl.StockTradeTransactionLogic
import com.datayes.invest.pms.logic.transaction.impl.RepoTransactionLogic
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.entity.security.Equity
import com.datayes.invest.pms.entity.security.Future
import com.datayes.invest.pms.entity.security.Repo
import com.datayes.invest.pms.logging.Logging

class TransactionLogicFactory extends Logging {

  @Inject
  private var stockTradeLogic: StockTradeTransactionLogic = null

  @Inject
  private var stockIndexFutureLogic: StockIndexFutureTransactionLogic = null

  @Inject
  private var repoLogic: RepoTransactionLogic = null

  @Inject
  private var securityDao: SecurityDao = null

  
  def get(t: Transaction): TransactionLogic = {
    val security = securityDao.findById(t.securityId)

    val logic = security match {
      case equity: Equity => stockTradeLogic
      case future: Future => stockIndexFutureLogic
      case repo: Repo => repoLogic
      case x =>
        logger.error("Failed to find transaction logic for security " + x)
        null
    }

    logic
  }
}