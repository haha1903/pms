package com.datayes.invest.pms.tools.importer

import java.io.File
import javax.inject.Inject
import com.datayes.invest.pms.logic.accountinit.{AccountInitializer, PositionSourceData, AccountSourceData}
import scala.io.Source
import org.joda.time.{LocalDate, LocalDateTime}
import com.datayes.invest.pms.util.DefaultValues
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.dao.account.{AccountDao, SourceTransactionDao}
import com.datayes.invest.pms.dbtype.AccountClassType
import com.datayes.invest.pms.dbtype.AccountTypeType
import scala.collection.mutable
import java.sql.Timestamp
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import play.pms.ClientException

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
    
    val buffer = mutable.ListBuffer.empty[Array[String]]
    val csvList = (for {
      line <- lines
      if line.trim().nonEmpty && !line.trim().startsWith("#")   // skip empty line and comments
      values = line.split(",").map(_.trim)
      if values.exists(_.nonEmpty)
    } yield (values)).toList
      
    // split at POSITION to find Account Info
    val (accountInfoList, positionList, transactionList) = splitParts(csvList)
    val accountInfoMap = accountInfoList.map { l => (l(0), l(1)) }.toMap
    val sDate = accountInfoMap("Date")
    val openDate = parseDate(sDate).toLocalDateTime(LocalTime.MIDNIGHT)

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
    setAccountInactive(account)
    
    account
  }
  
  private val extraDateFormat = DateTimeFormat.forPattern("yyyy/M/d")
  
  private def parseDate(sDate: String): LocalDate = {
    try {
      return LocalDate.parse(sDate)
    } catch {
      case e: Throwable =>
    }
    try {
      return LocalDate.parse(sDate, extraDateFormat)
    } catch {
      case e: Throwable =>
    }
    throw new ClientException("Invalid date format in import file: " + sDate)
  }
  
  private def setAccountInactive(account: Account): Unit = {
    account.setStatus("INACTIVE")
    val ts = new Timestamp(System.currentTimeMillis())
    account.setStatusChangeDate(ts)
    accountDao.update(account)
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
    val asOfDate = parseDate(header(1))
    context.asOfDate = asOfDate

    for (line <- csvList.tail) {
      val handler = findRecordHandler(context, line)
      val srcTransaction = handler.createSourceTransaction(context, line)
      sourceTransactionDao.save(srcTransaction)
    }
  }

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

}
