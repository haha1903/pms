package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.dao.account.{SecurityTransactionDao, AccountDao}
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.model.models.Trade
import org.joda.time.LocalDate
import scala.collection.mutable.ListBuffer
import play.pms.ClientException
import scala.collection.JavaConversions._
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.util.BigDecimalConstants
import com.datayes.invest.pms.dbtype.TradeSide
import javax.inject.Inject
import com.datayes.invest.pms.entity.account.Account

class TradeService {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  def getHistory(accountIdOpt: Option[Long], startDateOpt: Option[LocalDate], endDateOpt: Option[LocalDate]): List[Trade] = transaction {
    val accounts = accountIdOpt match {
      case Some(aid) =>
        val a = accountDao.findById(aid)
        if (a == null) {
          throw new ClientException("Failed to find account by id " + aid)
        }
        List(a)
      case None =>
        val list = accountDao.findEffectiveAccounts(endDateOpt.getOrElse(LocalDate.now()))
        list.toList
    }
    val accountIdList = accounts.map(_.getId)
    val accountMap = accounts.map { a => (a.getId, a)}.toMap
    val minOpenDate = findMinOpenDate(accounts)

    val transactions = securityTransactionDao.findByAccountIdListBetweenDates(
      accountIdList, startDateOpt.getOrElse(minOpenDate), endDateOpt.getOrElse(LocalDate.now()))
    val trades = ListBuffer.empty[Trade]

    for (tran <- transactions) {
      val accountId = tran.getAccountId()
      val account = accountMap(accountId)
      val security = securityDao.findById(tran.getSecurityId())
      val securityName = if (security.getNameAbbr != null && security.getNameAbbr.trim.nonEmpty) {
        security.getNameAbbr
      } else {
        security.getName
      }

      val trd = Trade(
        accountId = account.getId,
        accountNo = account.getAccountNo,
        securityName = securityName,
        securitySymbol = security.getTickerSymbol,
        exchange = security.getExchangeCode,
        tradeSide = TradeSide.fromDbValue(tran.getTradeSideCode),
        amount = tran.getAmount,
        orderPrice = BigDecimalConstants.ZERO,
        executionPrice = tran.getAvgPrice,
        executionDate = tran.getAsOfDate
      )

      trades.append(trd)
    }

    trades.toList
  }

  private def findMinOpenDate(accounts: List[Account]): LocalDate = {
    var min: LocalDate = null
    for (a <- accounts) {
      if (a.getOpenDate != null) {
        val openDate = a.getOpenDate.toLocalDate
        if (min == null) {
          min = openDate
        } else if (openDate.isBefore(min)) {
          min = openDate
        }
      }
    }

    min
  }
}