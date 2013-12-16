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
import scala.collection.mutable
import com.datayes.invest.pms.entity.account.{PositionHist, CashPosition, CarryingValueHist}
import java.lang

class AccountDataService extends Logging {

  @Inject private var accountDao: AccountDao = _
  @Inject private var securityDao: SecurityDao = _
  @Inject private var securityPositionDao: SecurityPositionDao = _
  @Inject private var cashPositionDao: CashPositionDao = _
  @Inject private var positionHistDao: PositionHistDao = _
  @Inject private var carryingValueHistDao: CarryingValueHistDao = _

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
      val accountId = a.getId
      s"通联数据~$accountName~SubaccountName-$accountId~1-$accountId~"
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
      val securityPositionId = sp.getId
      val positionHist = positionHistDao.findByPositionIdAsOfDate(securityPositionId, asOfDate)
      val ledgerType = LedgerType.fromDbValue(sp.getLedgerId) match {
        case LedgerType.SECURITY => "LONG"
        case LedgerType.FUTURE_LONG => "LONG"
        case LedgerType.FUTURE_SHORT => "SHORT"
      }
      val positionCarryingValueHists = carryingValueHists.filter(_.getPK.getPositionId == securityPositionId)
      val amount: BigDecimal = positionCarryingValueHists.size match {
        case 0 => logger.error(s"position carrying value not found: accountName: $accountName, security position id: $securityPositionId, date: $asOfDate"); 0
        case _ => positionCarryingValueHists.head.getValueAmount
      }
      val quantity = positionHist.getQuantity
      f"$exchangeCode.$tickerSymbol,$ledgerType,$quantity%.11f,$amount%.11f"
    }
    }.mkString("\n")
    val cashPosition = cashPositionDao.findByAccountIdLedgerId(accountId, 2L)
    val quantity: BigDecimal = cashPosition match {
      case null => logger.error(s"cash position not found: accountName: $accountName"); 0
      case _ => {
        val positionHist = positionHistDao.findByPositionIdAsOfDate(cashPosition.getId, asOfDate)
        positionHist match {
          case null => logger.error(s"cash position not found: accountName: $accountName"); 0
          case _ => positionHist.getQuantity
        }
      }
    }
    f"""#comments
      |Date,$asOfDate
      |Client,通联数据
      |Account,$accountName
      |Broker,CITIC1
      |#Security,type,amount,average-price
      |$securityValues
      |#CASH,amount,margin-rate
      |CASH,$quantity%.11f,1.00
      |#EOF
    """.stripMargin
  }
}