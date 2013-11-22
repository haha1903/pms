package test.importer

import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter
import javax.inject.Inject
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.system.SystemInjectors
import com.datayes.invest.pms.dao.security.SecurityDao
import com.datayes.invest.pms.entity.security.Security
import com.datayes.invest.pms.entity.security.Equity
import scala.collection.JavaConversions._
import org.joda.time.LocalDate
import scala.util.Random
import java.io.PrintWriter
import com.datayes.invest.pms.persist.dsl.transaction
import java.math.MathContext
import java.text.DecimalFormat

object AddTradePrice {

  val dir = "/home/taot/tmp/importcsv"
    
  val filename = "2013-09-20"
    
  val asOfDate = LocalDate.parse(filename)
  
  val random = new Random()
  
  val formatter = new DecimalFormat("#.##")
    
  val marketDataService: MarketDataService = SystemInjectors.INSTANCE.getInjector().getInstance(classOf[MarketDataService])
  
  val securityDao: SecurityDao = SystemInjectors.INSTANCE.getInjector().getInstance(classOf[SecurityDao])
    
  def main(args: Array[String]): Unit = transaction {
    try {
      val source = Source.fromFile(new File(dir, filename))
      val lines = source.getLines()
      val outfile = new File(dir, filename + "_out")
      val writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile)))
      try {
        for (l <- lines) {
          processLine(writer, l)
        }
      } finally {
        writer.close()
      }
    } finally {
      sys.exit()
    }
  }
  
  private def processLine(writer: PrintWriter, line: String): Unit = {
    val values = line.split(",").map(_.trim)
    val symbol = values(0)
    val security = findSecurity(symbol) getOrElse (throw new RuntimeException("Security not found for symbol: " + symbol))
    val md = marketDataService.getMarketData(security.getId(), asOfDate)
    val price = md.getPrice()
    val modifiedPrice = getRandomPrice(price)
    
    val sPrice = formatter.format(modifiedPrice)
    println("symbol: %s, securityId: %s, real price: %s, random price: %s".format(symbol, security.getId, price, sPrice))
    
    val newValues = values ++ List(sPrice)
    writer.println(newValues.mkString(","))
  }
  
  private def getRandomPrice(price: BigDecimal): BigDecimal = {
    val multiplier = (random.nextDouble() - 0.5) / 10 + 1
    price * multiplier
  }
  
  private def findSecurity(symbol: String): Option[Security] = {
    val fixedSymbol = fixSecuritySymbol(symbol)

    val list = securityDao.findByTickerSymbol(fixedSymbol)
    if (list == null || list.isEmpty) {
      throw new RuntimeException("Import error. Cannot find security for symbol " + fixedSymbol)
    }
    if (list.size() > 1) {
      println("WARN: Multiple security found for symbol " + fixedSymbol)
      list.find(isAGuStock(_))
    } else {
      Some(list(0))
    }

  }
  
  private def isAGuStock(security: Security): Boolean = {
    security match {
      case e: Equity => e.getTypeCode() == 1
      case _ =>
        throw new RuntimeException("Error. Only equity may have duplicate symbol")
    }
  }
  
  private def fixSecuritySymbol(symbol: String): String = {
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
}
