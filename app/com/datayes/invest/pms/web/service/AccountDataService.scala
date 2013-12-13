package com.datayes.invest.pms.web.service


import org.joda.time.LocalDate

import com.datayes.invest.pms.dao.account._
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction

import javax.inject.Inject
import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.dbtype.LedgerType
import scala.collection.JavaConversions._
import java.{util, lang}
import com.datayes.invest.pms.entity.account.{Position, PositionHist, CarryingValueHist}
import scala.collection.mutable

class AccountDataService extends Logging {

  @Inject
  private var accountDao: AccountDao = _

  @Inject
  private var securityDao: SecurityDao = _

  @Inject
  private var securityPositionDao: SecurityPositionDao = _

  @Inject
  private var cashPositionDao: CashPositionDao = _

  @Inject
  private var positionHistDao: PositionHistDao = _

  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = _

  private val clientId = Config.INSTANCE.getString("pms.client.id")

  private def accounts = accountDao.findAll()

  def exportClients() = s"通联数据~99 West Lujiazui Road, Pudong, Shanghai, P.R.China 200120~CN~CNY~$clientId~"

  def exportAccounts() = transaction {
    accounts.map(a => {
      val accountName = a.getAccountName
      val id = a.getId
      s"通联数据~$accountName~CNY~$id~"
    }).mkString("\n")
  }

  def exportSubaccounts() = transaction {
    accounts.map(a => {
      val accountName = a.getAccountName
      s"通联数据~$accountName~SubaccountName~1~"
    }).mkString("\n")
  }

  def exportEquityPosition(accountId: Long, asOfDate: LocalDate) = transaction {
    getEquityPosition(accountId, asOfDate)
  }

  def exportEquityPositions(asOfDate: LocalDate) = transaction {
    accounts.map { a => {
      (a.getId, getEquityPosition(a.getId, asOfDate))
    }
    }
  }

  private def getEquityPosition(accountId: Long, asOfDate: LocalDate) = {
    val accountName = accountDao.findById(accountId).getAccountName
    val securityPositions = securityPositionDao.findByAccountId(accountId).toList
    val carryingValueHists = carryingValueHistDao.findByPositionIdListAsOfDate(seqAsJavaList(securityPositions.map(_.getId)), asOfDate)
    val securityValues = securityPositions.map { sp => {
      val security = securityDao.findById(sp.getSecurityId)
      val exchangeCode = security.getExchangeCode
      val tickerSymbol = security.getTickerSymbol
      val positionHist = positionHistDao.findByPositionIdAsOfDate(sp.getId, asOfDate)
      val ledgerType = LedgerType.fromDbValue(sp.getLedgerId) match {
        case LedgerType.SECURITY => "LONG"
        case LedgerType.FUTURE_LONG => "LONG"
        case LedgerType.FUTURE_SHORT => "SHORT"
      }
      val amount = carryingValueHists.filter(_.getPK.getPositionId == sp.getId).head.getValueAmount
      val quantity = positionHist.getQuantity
      s"$exchangeCode.$tickerSymbol,$ledgerType,$quantity,$amount"
    }
    }.mkString("\n")
    val quantity = positionHistDao.findByPositionIdAsOfDate(cashPositionDao.findByAccountIdLedgerId(accountId, 2L).getId, asOfDate).getQuantity
    s"""#comments
      |Date,$asOfDate
      |Client,通联数据
      |Account,$accountName
      |Broker,CITIC1
      |#Security,type,amount,average-price
      |$securityValues
      |#CASH,amount,margin-rate
      |CASH,$quantity,1.00`
      |#EOF
    """.stripMargin
  }
}