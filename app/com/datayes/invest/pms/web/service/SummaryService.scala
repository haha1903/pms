package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.{AccountDao, AccountValuationHistDao}
import org.joda.time.LocalDate
import com.datayes.invest.pms.web.model.models.{AccountsSummaryItem, AccountsSummary}
import com.datayes.invest.pms.dbtype.{AccountValuationType, AccountTypeType}
import com.datayes.invest.pms.entity.account.{Account, AccountValuationHist}
import com.datayes.invest.pms.persist.dsl.transaction
import scala.collection.JavaConversions._
import com.datayes.invest.pms.util.{BigDecimalConstants, DefaultValues}
import com.datayes.invest.pms.logic.calculation.webinterface.CurrentCashCalc


class SummaryService extends Logging {
  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null

  @Inject
  private var accountDao: AccountDao = null


  def getSummary(asOfDate: LocalDate): AccountsSummary = transaction {
    val accounts =  accountDao.findEffectiveAccounts(asOfDate)

    val hists = accounts.map(account => createAccountsSummaryItem(account, asOfDate))
    val totalAssetValue: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.assetValue)
    val totalNetValue: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.netValue)
    val totalPnl: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.pnl)
    val totalCash: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.cash)
    val totalPayableValue: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.payableValue)
    val totalReceivableValue: BigDecimal = hists.foldLeft(BigDecimalConstants.ZERO)(_ + _.receivableValue)
    val totalCurrentCash: BigDecimal = CurrentCashCalc.calculateCurrentCash(totalCash, totalPayableValue, totalReceivableValue)

    val items = hists.groupBy(h => h.category).map { case (category, acctHists) =>
      val assetValue: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.assetValue)
      val netValue: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.netValue)
      val pnl: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.pnl)
      val cash: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.cash)
      val payableValue: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.payableValue)
      val receivableValue: BigDecimal = acctHists.foldLeft(BigDecimalConstants.ZERO)(_ + _.receivableValue)
      val currentCash: BigDecimal = CurrentCashCalc.calculateCurrentCash(cash, payableValue, receivableValue)

      AccountsSummaryItem(acctHists.size, category, BigDecimal(100.0 * acctHists.size / accounts.size), assetValue, netValue, pnl, currentCash)
    }.toSeq

    val allItems = completeAccountTypes(items)

    AccountsSummary(accounts.size, asOfDate, totalAssetValue, totalNetValue, totalPnl, totalCurrentCash, allItems)
  }

  private def createAccountsSummaryItem(account: Account, asOfDate: LocalDate): AccountsSummaryItem = {
    val accountType = if (account.getAccountTypeId != null) {
      AccountTypeType.fromDbValue(account.getAccountTypeId)
    } else {
      null
    }
    AccountsSummaryItem(0, accountType, 0,
      getAccountFieldValue(account.getId, asOfDate, AccountValuationType.SHARE),
      getAccountFieldValue(account.getId, asOfDate, AccountValuationType.NET_WORTH),
      getAccountPNLValue(account.getId, asOfDate),
      getAccountFieldValue(account.getId, asOfDate, AccountValuationType.CASH),
      getAccountFieldValue(account.getId, asOfDate, AccountValuationType.PAYABLE_SETTLEMENT),
      getAccountFieldValue(account.getId, asOfDate, AccountValuationType.RECEIVABLE_SETTLEMENT))
  }

  private def completeAccountTypes(items: Seq[AccountsSummaryItem]): Seq[AccountsSummaryItem] = {
    AccountTypeType.values().map { accountType =>
      items.find(_.category == accountType) match {
        case Some(it) => it
        case None => AccountsSummaryItem(0, accountType, 0, 0, 0, 0, 0, 0, 0)
      }
    }
  }

  private def getAccountPNLValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val hists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId, AccountValuationType.PROFIT_LOSS.getDbValue, asOfDate)
    if (hists == null) 0 else hists.foldLeft(BigDecimalConstants.ZERO)( _ + _.getValueAmount)
  }

  private def getAccountFieldValue(accountId: Long, asOfDate: LocalDate, valuationType: AccountValuationType): BigDecimal = {
    val pk = new AccountValuationHist.PK(accountId, valuationType.getDbValue, asOfDate)
    val acctHist = accountValuationHistDao.findById(pk)
    if (null == acctHist) 0 else acctHist.getValueAmount
  }
}
