package com.datayes.invest.pms.logic.accountinit

import com.google.inject.Inject
import com.datayes.invest.pms.entity.account.{AccountValuationInit, Account, Fee}
import com.datayes.invest.pms.logging.Logging
import org.joda.time.LocalDateTime
import play.api.libs.json.{Json, JsSuccess}
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.dao.account.AccountValuationInitDao
import com.datayes.invest.pms.dao.account.FeeDao
import com.datayes.invest.pms.dbtype.AccountValuationType
import scala.collection.JavaConversions._
import play.pms.ClientException


class AccountInitializer extends Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var accountValuationInitDao: AccountValuationInitDao = null
  
  @Inject
  private var feeDao: FeeDao = null
  
  // TODO re-write it with provider
  @Inject
  private var positionInitializer: PositionInitializer = null

  def initializeAccount(accountSourceData: AccountSourceData): Long = {
    val accountId = createAccount(accountSourceData)
    createAccountValuationInit(accountId, accountSourceData.netWorth, accountSourceData.openDate)
    createFees(accountId, accountSourceData.openDate, accountSourceData.fees)
    accountId
  }

  def initializeAccountsByJson(message: String): Long = {
    val accSrcData = parseJsonValue(message)
    initializeAccount(accSrcData)
  }

  private def parseJsonValue(message: String): AccountSourceData = {
    val jsValue = Json.parse(message)
    val result = (jsValue \ "AccountSourceData").validate[AccountSourceData]

    result match {
      case success: JsSuccess[AccountSourceData] =>
        success.get
      case _ =>
        logger.error("error when parsing json")
        null
    }
  }

  private def createFees(accountId: Long, openDate: LocalDateTime, feeList: List[RateSourceData]): Unit = {
    for (f <- feeList) {
      val fee = new Fee(
        accountId,
        f.rateType.getDbValue,
        openDate,
        null,
        f.tradeSide match {
          case Some(side) => side.getDbValue()
          case None => null
        },
        f.securityId match {
          case Some(id) => id
          case None => null
        },
        f.rate.bigDecimal,
        null)

      feeDao.save(fee)
    }
  }

  private def createAccount(accSrcData: AccountSourceData): Long = {
    checkConstraints(accSrcData)
    val zero = BigDecimal("0").bigDecimal
    val account = new Account(
      accSrcData.countryCode,
      accSrcData.currencyCode,
      accSrcData.classCode.getDbValue,
      accSrcData.accountType.getDbValue,
      accSrcData.accountNo,
      accSrcData.name,
      accSrcData.openDate)

    accountDao.save(account)
    logger.info("Account #{} created", account.getId)

    val accountId = account.getId

    //Add position and share
    accSrcData.positions.foreach(p => {
      positionInitializer.initializePosition(
        accountId.toLong,
        p,
        accSrcData.openDate,
        p.quantity,
        null)
    })

    positionInitializer.createSharePositionAndInitAndHist(
      accountId,
      accSrcData.share,
      accSrcData.currencyCode,
      accSrcData.openDate)

    accountId
  }

  private def checkConstraints(accSrcData: AccountSourceData): Unit = {
    val accounts = accountDao.findAll();
    if (accounts != null) {
      for (a <- accounts) {
        if (a.getAccountNo == accSrcData.accountNo) {
          throw new ClientException("Duplicate accountNo: " + accSrcData.accountNo)
        }
      }
    }
  }

  private def createAccountValuationInit(accountId: Long, netWorth: BigDecimal, openDate: LocalDateTime): Unit = {

    val accValType = AccountValuationType.NET_WORTH.getDbValue

    val accValInit = new AccountValuationInit(
      accountId,
      accValType,
      netWorth.bigDecimal)

    accountValuationInitDao.save(accValInit)
    logger.info("Account Valuation Init #{} created", accountId)
  }
}
