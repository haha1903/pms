package controllers.debug

import play.api.mvc.Controller
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.sso.AuthAction
import org.joda.time.LocalDateTime
import scala.collection.JavaConversions._
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.datayes.invest.pms.web.model.gson._
import com.datayes.invest.pms.service.marketdata.MarketDataService
import javax.inject.Inject
import com.datayes.invest.pms.entity.account.MarketData
import com.datayes.invest.pms.service.marketdata.impl.MarketDataServiceImpl
import org.joda.time.LocalDate
import java.util.{ Set => JSet, Collections => JCollections }
import java.lang.{ Long => JLong }

class MarketDataServiceMonitorController extends Controller with Logging {
  
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
