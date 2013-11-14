package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.{AccountDao, AccountValuationHistDao}
import org.joda.time.LocalDate
import com.datayes.invest.pms.web.model.models.{AccountsSummaryItem, AccountsSummary}
import com.datayes.invest.pms.dbtype.{AccountValuationType, AccountTypeType}
import com.datayes.invest.pms.entity.account.{Account, AccountValuationHist}
import com.datayes.invest.pms.persist.dsl._
import scala.collection.JavaConversions._


class SummaryService extends Logging {
  @Inject
  private var accountValuationHistDao: AccountValuationHistDao = null

  @Inject
  private var accountDao: AccountDao = null


  def getSummary(asOfDate: LocalDate): AccountsSummary = transaction {
    val accounts =  accountDao.findEffectiveAccounts(asOfDate)

    val hists = accounts.map(account => createAccountsSummaryItem(account, asOfDate))
    val totalAssetValue: BigDecimal = hists.foldLeft(BigDecimal(0))(_ + _.assetValue)
    val totalNetValue: BigDecimal = hists.foldLeft(BigDecimal(0))(_ + _.netValue)
    val totalPnl: BigDecimal = hists.foldLeft(BigDecimal(0))(_ + _.pnl)
    val totalCash: BigDecimal = hists.foldLeft(BigDecimal(0))(_ + _.cash)

    val items = hists.groupBy(h => h.category).map { case (category, acctHists) =>
      val assetValue: BigDecimal = acctHists.foldLeft(BigDecimal(0))(_ + _.assetValue)
      val netValue: BigDecimal = acctHists.foldLeft(BigDecimal(0))(_ + _.netValue)
      val pnl: BigDecimal = acctHists.foldLeft(BigDecimal(0))(_ + _.pnl)
      val cash: BigDecimal = acctHists.foldLeft(BigDecimal(0))(_ + _.cash)

      AccountsSummaryItem(acctHists.size, category, BigDecimal(100.0 * acctHists.size / accounts.size), assetValue, netValue, pnl, cash)
    }.toSeq

    val allItems = completeAccountTypes(items)

    AccountsSummary(accounts.size, asOfDate, totalAssetValue, totalNetValue, totalPnl, totalCash, allItems)
  }

  private def createAccountsSummaryItem(account: Account, asOfDate: LocalDate): AccountsSummaryItem = {
    val accountType = if (account.getAccountTypeId != null) {
      AccountTypeType.fromDbValue(account.getAccountTypeId)
    } else {
      null
    }
    AccountsSummaryItem(0, accountType, 0,
      getAccountShareValue(account.getId, asOfDate),
      getAccountNetWorthValue(account.getId, asOfDate),
      getAccountPNLValue(account.getId, asOfDate),
      getAccountCashValue(account.getId, asOfDate))
  }

  private def completeAccountTypes(items: Seq[AccountsSummaryItem]): Seq[AccountsSummaryItem] = {
    AccountTypeType.values().map { accountType =>
      items.find(_.category == accountType) match {
        case Some(it) => it
        case None => AccountsSummaryItem(0, accountType, 0, 0, 0, 0, 0)
      }
    }
  }

  private def getAccountShareValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val pk = new AccountValuationHist.PK(accountId, AccountValuationType.SHARE.getDbValue, asOfDate)
    val acctHist = accountValuationHistDao.findById(pk)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }

  private def getAccountNetWorthValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val pk = new AccountValuationHist.PK(accountId, AccountValuationType.NET_WORTH.getDbValue, asOfDate)
    val acctHist = accountValuationHistDao.findById(pk)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }

  private def getAccountCashValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val pk = new AccountValuationHist.PK(accountId, AccountValuationType.CASH.getDbValue, asOfDate)
    val acctHist = accountValuationHistDao.findById(pk)
    if (acctHist == null) 0 else acctHist.getValueAmount
  }

  private def getAccountPNLValue(accountId: Long, asOfDate: LocalDate): BigDecimal = {
    val hists = accountValuationHistDao.findByAccountIdTypeIdBeforeDate(accountId, AccountValuationType.PROFIT_LOSS.getDbValue, asOfDate)
    if (hists == null) 0 else hists.foldLeft(BigDecimal(0))( _ + _.getValueAmount)
  }
}
