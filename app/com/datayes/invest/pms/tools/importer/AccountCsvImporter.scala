package com.datayes.invest.pms.tools.importer

import java.io.File
import javax.inject.Inject
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.logic.accountinit.{AccountInitializer, PositionSourceData, AccountSourceData}
import scala.io.Source
import org.joda.time.{LocalDate, LocalDateTime}
import scala.collection.JavaConversions._
import com.datayes.invest.pms.entity.security.{Future, Equity, Security}
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.entity.account.{Account, SourceTransaction}
import com.datayes.invest.pms.dao.account.{AccountDao, SourceTransactionDao}
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.dbtype.TransactionSource
import com.datayes.invest.pms.dbtype.TransactionClass
import com.datayes.invest.pms.dbtype.AccountClassType
import com.datayes.invest.pms.dbtype.AccountTypeType
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.dbtype.PositionClass
import scala.collection.mutable

class AccountCsvImporter extends Logging {

  private val POSITION = "POSITION"

  private val TURN = "TURN"
    
  @Inject
  private var cashRecordHandler: CashRecordHandler = null
  
  @Inject
  private var equityRecordHandler: EquityRecordHandler = null
  
  @Inject var indexFutureRecordHandler: IndexFutureRecordHandler = null

  @Inject
  private var accountDao: AccountDao = null

//  @Inject
//  private var securityDao: SecurityDao = null

//  @Inject
//  private var marketDataService: MarketDataService = null

  @Inject
  private var accountInitializer: AccountInitializer = null

  @Inject
  private var sourceTransactionDao: SourceTransactionDao = null
  
  private var recordHandlers: List[RecordHandler] = null

  def importCsv(file: File): Account = {
    recordHandlers = List(cashRecordHandler, equityRecordHandler, indexFutureRecordHandler)
    val accountId = try {
      parseAndCreateAccount(file)
    } catch {
      case e: Throwable =>
        logger.warn("Parsing error on import file " + file.getAbsolutePath, e)
        throw e
    }
    accountId
  }

  private def parseAndCreateAccount(file: File): Account = {
    val lines = tryGetLines(file)

    // Remove empty lines and comments line
    val nonEmptyLines = lines.filter(s => s.trim.nonEmpty && !s.trim.startsWith("#"))
    val csvList = nonEmptyLines.map { s =>
      val values = s.split(",")
      values.map(_.trim())
    }.toList

    // split at POSITION to find Account Info
    val (accountInfoList, positionList, transactionList) = splitParts(csvList)
    val accountInfoMap = accountInfoList.map { l => (l(0), l(1)) }.toMap
    val sDate = accountInfoMap("Date")
    val openDate = LocalDateTime.parse(sDate)

    val context = new Context(openDate, accountInfoMap("Currency"))
    
    // create position source data list
    val positions = createPositionList(positionList, context)
    val accountSourceData = getAccountSourceData(accountInfoMap, openDate, positions)

    // initialize account and positions in database
    val accountId = accountInitializer.initializeAccount(accountSourceData)

    context.accountId = accountId
    // create source transactions
    createSourceTransactionsRecursive(context, transactionList)

    val account = accountDao.findById(accountId)
    account
  }

  private def tryGetLines(file: File): List[String] = {
    var lines: List[String] = try {
      Source.fromFile(file, "UTF-8").getLines().toList
    } catch {
      case e: Throwable => null
    }
    if (lines != null) {
      return lines
    }
    lines = try {
      Source.fromFile(file, "GBK").getLines().toList
    } catch {
      case e: Throwable => null
    }
    if (lines == null) {
      throw new RuntimeException("Failed to parse file: " + file.getName)
    }
    lines
  }

  private def splitParts(csvList: List[Array[String]]): (List[Array[String]], List[Array[String]], List[Array[String]]) = {
    val (accountInfoList, rest1) = csvList.span(l => l(0) != POSITION)
    val (positionList, transactionList) = rest1.span(l => l(0) != TURN)
    (accountInfoList, positionList, transactionList)
  }

  private def createSourceTransactionsRecursive(context: Context, csvList: List[Array[String]]): Unit = {
    if (csvList.isEmpty) {
      return
    }
    val (turns, rest) = csvList.tail.span(l => l(0) != TURN)
    createSourceTransactions(context, csvList.head :: turns)
    if (rest.nonEmpty) {
      createSourceTransactionsRecursive(context, rest)
    }
  }

  private def createSourceTransactions(context: Context, csvList: List[Array[String]]): Unit = {
    val header = csvList(0)
    val asOfDate = LocalDate.parse(header(1))
    context.asOfDate = asOfDate

    for (line <- csvList.tail) {
      val handler = findRecordHandler(context, line)
      val srcTransaction = handler.createSourceTransaction(context, line)
      sourceTransactionDao.save(srcTransaction)
    }
  }
/*
  private def createOneSourceTransaction(symbol: String, quantityDelta: BigDecimal, priceOpt: Option[BigDecimal],
      asOfDate: LocalDate, accountId: Long): Unit = {
    
    val security = findSecurity(symbol).getOrElse(
      throw new RuntimeException(symbol + " is not a security and not supported")
    )

    val price = getPriceOfSecurity(security.getId, asOfDate)

    val tradeSide = if (quantityDelta >= 0) TradeSide.BUY else TradeSide.SELL

    val srcTransaction = new SourceTransaction(
      accountId,
      security.getId,
      "",
      null, // traderId
      null, // brokerId
      new LocalDateTime(asOfDate.toDateTimeAtCurrentTime), // executionDate
      asOfDate, // settlementData
      tradeSide.toString,
      price,
      quantityDelta.abs,
      TransactionSource.PMS.getDbValue,
      TransactionClass.TRADE.toString)

    sourceTransactionDao.save(srcTransaction)
  }
  */

  private def getAccountSourceData(accountInfoMap: Map[String, String], openDate: LocalDateTime,
                                      positions: Seq[PositionSourceData]): AccountSourceData = {

    val sAccountClass = accountInfoMap("AccountClass")
    val accountClass = AccountClassType.valueOf(sAccountClass)
    if (accountClass == null) {
      throw new RuntimeException("Invalid AccountClass: " + sAccountClass)
    }

    val sAccountType = accountInfoMap("AccountType")
    val accountType = AccountTypeType.valueOf(sAccountType)
    if (accountType == null) {
      throw new RuntimeException("Invalid AccountType: " + sAccountType)
    }

    val share = BigDecimal(accountInfoMap("Share"))
    val netValuePerShare = BigDecimal(accountInfoMap("NetValuePerShare"))

    AccountSourceData(
      partyId = 1,
      parentAccountId = None,
      countryCode = accountInfoMap("Country"),
      currencyCode = accountInfoMap("Currency"),
      classCode = accountClass,
      accountType = accountType,
      accountNo = accountInfoMap("AccountNo"),
      name = accountInfoMap("AccountName"),
      openDate = openDate,
      netWorth = netValuePerShare * share,
      share = share,
      positions = positions.toList,
      fees = DefaultValues.DEFAULT_FEES
    )
  }
    
  /*private def getSecurityLine(line: Array[String]): (String, BigDecimal, Option[BigDecimal]) = {
    val symbol = line(0)
    val quantity = BigDecimal(line(1))
    val priceOpt = if (line.size < 3) {
      None
    } else {
      Some(BigDecimal(line(2)))
    }
    (symbol, quantity, priceOpt)
  }*/

  private def createPositionList(csvList: List[Array[String]], context: Context): List[PositionSourceData] = {
    val buf = mutable.ListBuffer.empty[PositionSourceData]
    for (csv <- csvList.tail) {    // skip POSITION line
      val handler = findRecordHandler(context, csv)
      val psd = handler.createInitialPosition(context, csv)
      buf.append(psd)
    }
    buf.toList
  }
  
  private def findRecordHandler(context: Context, values: Array[String]): RecordHandler = {
    for (h <- recordHandlers) {
      if (h.matches(context, values)) {
        return h
      }
    }
    throw new RuntimeException("Cannot found matched handler for line: " + values)
  }

  /*private def createPositionSourceData(symbol: String, quantity: BigDecimal, priceOpt: Option[BigDecimal], openDate: LocalDateTime): PositionSourceData = {

    val securityOpt = findSecurity(symbol)
    val exchange = securityOpt.map(_.getExchangeCode).getOrElse("XSHG")
    val currency = getCurrencyOfSecurity(securityOpt)

    val price: BigDecimal = securityOpt match {
      case Some(sec: Security) => getPriceOfSecurity(sec.getId, openDate.toLocalDate)
      case _ => 1
    }

    val carryingValueOpt = securityOpt match {
      case Some(e: Equity) =>
        val v = quantity * price
        Some(v)

      case Some(f: Future) =>
        throw new RuntimeException("Importer does not support future")

      case _ =>
        None
    }

    val ledgerType = try {
      LedgerType.valueOf(symbol)
    } catch {
      case e: Throwable => LedgerType.SECURITY
    }

    val positionClass = getPositionClassForSymbol(symbol)

    val psd = PositionSourceData(
      positionClass = positionClass,
      ledgerType = ledgerType,
      openDate = openDate,
      currencyCode = currency,
      quantity = quantity,
      carryingValue = carryingValueOpt,
      securityId = securityOpt.map(_.getId),
      exchangeCode = exchange
    )

    psd
  }*/

  /*private def getCurrencyOfSecurity(securityOpt: Option[Security]): String = {
    val c = securityOpt match {
      case Some(e: Equity) => e.getIssueCurrency
      case Some(f: Future) => f.getCurrencyCode
      case _ => null
    }
    val currency = if (c == null || c.trim.isEmpty) {
      DefaultValues.CURRENCY_CODE
    } else {
      c
    }
    currency
  }*/

  /*private def getPriceOfSecurity(securityId: Long, asOfDate: LocalDate): BigDecimal = {
    // TODO refine this
    val md = marketDataService.getMarketData(securityId, asOfDate)
    val price = if (md == null || md.getPrice() == null) {
      throw new RuntimeException("Failed to find market data for security #" + securityId + " on " + asOfDate)
    } else {
      md.getPrice()
    }
    price
  }*/

  /*private def findSecurity(symbol: String): Option[Security] = {
    // TODO how to determine if a ledger is security ledger
    val positionClass = getPositionClassForSymbol(symbol)
    if (positionClass != PositionClass.SECURITY) {
      return None
    }

    val fixedSymbol = fixSecuritySymbol(symbol)

    val list = securityDao.findByTickerSymbol(fixedSymbol)
    if (list == null || list.isEmpty) {
      throw new RuntimeException("Import error. Cannot find security for symbol " + fixedSymbol)
    }
    if (list.size() > 1) {
      logger.warn("Multiple security found for symbol " + fixedSymbol)
      list.find(isAGuStock(_))
    } else {
      Some(list(0))
    }

  }*/

  /*private def fixSecuritySymbol(symbol: String): String = {
    try {
      Integer.valueOf(symbol)
    } catch {
      case e: NumberFormatException =>
        return symbol
    }
    val builder = new StringBuilder
    for (i <- 0 until (6 - symbol.size)) {
      builder.append("0")
    }
    builder.toString + symbol
  }

  private def isAGuStock(security: Security): Boolean = {
    security match {
      case e: Equity => e.getTypeCode() == 1
      case _ =>
        throw new RuntimeException("Error. Only equity may have duplicate symbol")
    }
  }

  private def getPositionClassForSymbol(symbol: String): PositionClass = {
    try {
      LedgerType.valueOf(symbol).getPositionClass
    } catch {
      case e: Throwable => PositionClass.SECURITY
    }
  }*/

}
