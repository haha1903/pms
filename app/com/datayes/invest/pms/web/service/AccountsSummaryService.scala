package com.datayes.invest.pms.web.service

import javax.inject.Inject
import org.joda.time.LocalDate
import scala.collection.JavaConversions._
import scala.BigDecimal._
import com.datayes.invest.pms.dbtype.AccountTypeType
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.dao.account.AccountValuationHistDao
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.web.model.models.AccountsSummary
import com.datayes.invest.pms.web.model.models.AccountsSummaryItem
import com.datayes.invest.pms.dbtype.AccountValuationType

class AccountsSummaryService extends Logging {

  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null
  
  @Inject
  private var accountDao: AccountDao = null
  
  def getSummary(asOfDate: LocalDate): AccountsSummary = {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)

    var totalAssetValue: BigDecimal = 0
    var totalNetValue: BigDecimal = 0
    var totalPnl: BigDecimal = 0
    var totalCash: BigDecimal = 0
    
    var hists = for (account <- accounts) yield {
      val accountType = if (account.getAccountTypeId != null) {
        AccountTypeType.fromDbValue(account.getAccountTypeId)
      } else {
        null
      }
      var obj = AccountsSummaryItem(0, accountType, 0,
	      getAccountShareValue(account.getId, asOfDate),
	      getAccountNetWorthValue(account.getId, asOfDate),
	      getAccountPNLValue(account.getId, asOfDate),
	      getAccountCashValue(account.getId, asOfDate))

      totalAssetValue += obj.assetValue
      totalNetValue += obj.netValue
      totalPnl += obj.pnl
      totalCash += obj.cash
      obj
    }
    
    var items = hists.groupBy(h => h.category).map { case (category, acctHists) =>
      var assetValue: BigDecimal = 0
      var netValue: BigDecimal = 0
      var pnl: BigDecimal = 0
      var cash: BigDecimal = 0

      for (acctHist <- acctHists) {
        assetValue += acctHist.assetValue
        netValue += acctHist.netValue
        pnl += acctHist.pnl
        cash += acctHist.cash
      }
      
      AccountsSummaryItem(acctHists.size, category, BigDecimal(100.0 * acctHists.size / accounts.size), assetValue, netValue, pnl, cash)
    }.toSeq

    val allItems = completeAccountTypes(items)
    
    AccountsSummary(accounts.size, asOfDate, totalAssetValue, totalNetValue, totalPnl, totalCash, allItems)
  }

  private def completeAccountTypes(items: Seq[AccountsSummaryItem]): Seq[AccountsSummaryItem] = {

    val allItems = AccountTypeType.values().map { accountType =>
      items.find(_.category == accountType) match {
        case Some(it) => it
        case None => AccountsSummaryItem(0, accountType, 0, 0, 0, 0, 0)
      }
    }
    allItems
  }
  
  private def getAccountShareValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var acctHist = accountValuationHistDao.findByAccountIdTypeIdAsOfDate(accountId, AccountValuationType.SHARE.getDbValue, asOfDate)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }
  
  private def getAccountNetWorthValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var acctHist = accountValuationHistDao.findByAccountIdTypeIdAsOfDate(accountId, AccountValuationType.NET_WORTH.getDbValue, asOfDate)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }
    
  private def getAccountCashValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var acctHist = accountValuationHistDao.findByAccountIdTypeIdAsOfDate(accountId, AccountValuationType.CASH.getDbValue, asOfDate)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }
  
  private def getAccountPNLValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    var hists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId, AccountValuationType.PROFIT_LOSS.getDbValue, asOfDate)
    if (hists == null) 0 else hists.foldLeft(BigDecimal(0))( _ + _.getValueAmount)
  }    
}
