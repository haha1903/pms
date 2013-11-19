package controllers.debug

import java.lang.{Long => JLong}
import java.util.{Collections => JCollections}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.seqAsJavaList

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

import com.datayes.invest.pms.entity.account.MarketData
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.service.marketdata.impl.MarketDataServiceImpl
import com.datayes.invest.pms.util.gson.BigDecimalDeserializer
import com.datayes.invest.pms.util.gson.BigDecimalSerializer
import com.datayes.invest.pms.util.gson.LocalDateDeserializer
import com.datayes.invest.pms.util.gson.LocalDateSerializer
import com.datayes.invest.pms.util.gson.LocalDateTimeDeserializer
import com.datayes.invest.pms.util.gson.LocalDateTimeSerializer
import com.datayes.invest.pms.web.sso.AuthAction
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import javax.inject.Inject
import play.api.mvc.Controller


class MDSMonitorController extends Controller with Logging {
  
  @Inject
  private var marketDataService: MarketDataServiceImpl = null
  
  private val gson = createGson()

  def monitor() = AuthAction { implicit req =>
    Ok(views.html.debug.mds())
  }
  
  def initMds() = AuthAction { implicit req =>
    val s = JCollections.emptySet[JLong]()
    marketDataService.getMarketData(s, LocalDate.now)
    Ok("Market data service initialized")
  }

  def cacheData() = AuthAction { implicit req =>
    val mdMap = marketDataService.getMarketDataCache()
    val cache: java.util.Collection[MarketData] = mdMap.map{ case(k, v) => (v) }.toList

    val json = gson.toJson(cache)
    Ok(json)
  }

  def cacheSize() = AuthAction { implicit req =>
    val mdMap = marketDataService.getMarketDataCache()
    val json = gson.toJson(mdMap.size)
    Ok(json)
  }

  def cacheMissing() = AuthAction { implicit req =>
    val missingIdMap = marketDataService.getCacheMissingSecurityId()
    val json = gson.toJson(missingIdMap)
    Ok(json)
  }
  
  private def createGson(): Gson = {
    // TODO this could be shared in the code base
    val builder = new GsonBuilder
    builder.registerTypeAdapter(classOf[LocalDate], new LocalDateSerializer)
    builder.registerTypeAdapter(classOf[LocalDate], new LocalDateDeserializer)
    builder.registerTypeAdapter(classOf[LocalDateTime], new LocalDateTimeSerializer)
    builder.registerTypeAdapter(classOf[LocalDateTime], new LocalDateTimeDeserializer)
    builder.registerTypeAdapter(classOf[BigDecimal], new BigDecimalSerializer)
    builder.registerTypeAdapter(classOf[BigDecimal], new BigDecimalDeserializer)
    val gson = builder.create()
    gson
  }

}
