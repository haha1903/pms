package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.AccountValuationHistDao
import org.joda.time.LocalDate
import com.datayes.invest.pms.web.model.models.AccountOverview
import com.datayes.invest.pms.dbtype.AccountValuationType
import com.datayes.invest.pms.entity.account.AccountValuationHist
import com.datayes.invest.pms.persist.dsl.transaction
import scala.collection.JavaConversions._

class FundService extends Logging {
  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null

  def getSummary(accountId: Long, asOfDate: LocalDate): AccountOverview = transaction {
    val unitNet = getOverviewValue(accountId, asOfDate, AccountValuationType.UNIT_NET)
    val dailyReturn = getOverviewValue(accountId, asOfDate, AccountValuationType.DAILY_RETURN)
    val marketValue = getOverviewValue(accountId, asOfDate, AccountValuationType.SECURITY)  // TODO this may not be correct
    val cashValue = getOverviewValue(accountId, asOfDate, AccountValuationType.CASH)
    val fundReturn = getFundReturnAsOfDate(accountId, asOfDate)
    val pnl = getOverviewValue(accountId, asOfDate, AccountValuationType.PROFIT_LOSS)

    AccountOverview(
      unitNet,
      dailyReturn,
      marketValue,
      cashValue,
      fundReturn,
      pnl
    )
  }

  private def getOverviewValue(accountId: Long, asOfDate: LocalDate,
                               accValType: AccountValuationType): BigDecimal = {
    val pk = new AccountValuationHist.PK(accountId, accValType.getDbValue, asOfDate)
    val valHist = accountValuationHistDao.findById(pk)
    if (valHist == null) {
      logger.warn("Failed to load account valuation hist ({}) for account #{} on {}", accValType, accountId, asOfDate)
      BigDecimal("0")
    } else if (valHist.getValueAmount == null) {
      logger.warn("Account valuation hist ({}) for account #{} on {} is null", accValType, accountId, asOfDate)
      BigDecimal("0")
    }else {
      valHist.getValueAmount
    }
  }

  private def getFundReturnAsOfDate(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val accValType = AccountValuationType.DAILY_RETURN
    val accValHists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId,
      accValType.getDbValue, asOfDate)
    val ratio = accValHists.foldLeft(BigDecimal("1")) { (product, valHist) =>
      product * (valHist.getValueAmount + 1)
    }
    ratio - 1
  }

}
