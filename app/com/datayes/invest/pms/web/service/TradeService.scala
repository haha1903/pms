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

class TradeService {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var securityDao: SecurityDao = null

  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null

  def getHistory(accountIdOpt: Option[Long], startDate: LocalDate, endDate: LocalDate): List[Trade] = transaction {
    val accounts = accountIdOpt match {
      case Some(aid) =>
        val a = accountDao.findById(aid)
        if (a == null) {
          throw new ClientException("Failed to find account by id " + aid)
        }
        List(a)
      case None =>
        val list = accountDao.findEffectiveAccounts(endDate)
        list.toList
    }
    val accountIdList = accounts.map(_.getId)
    val accountMap = accounts.map { a => (a.getId, a)}.toMap

    val transactions = securityTransactionDao.findByAccountIdListBetweenDates(accountIdList, startDate, endDate)
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
        executionTime = tran.getExecutionDate
      )

      trades.append(trd)
    }

    trades.toList
  }
}