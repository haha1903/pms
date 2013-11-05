package controllers

import play.api.mvc.Controller
import com.google.gson._
import java.lang.reflect.Type
import org.joda.time.LocalDateTime
import com.datayes.invest.pms.service.marketdata.impl.data.{BigDecimalDeserializer, BigDecimalSerializer}
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.sso.AuthAction
import com.datayes.invest.pms.entity.account.MarketData
import com.google.inject.Inject
import com.datayes.invest.pms.service.marketdata.MarketDataService
import com.datayes.invest.pms.service.marketdata.impl.MarketDataServiceImpl

import scala.collection.JavaConversions._
import controllers.util.Jsonp

class CacheObservationController extends Controller with Jsonp with Logging {

  @Inject
  private var marketDataService: MarketDataService = null

  private val builder = new GsonBuilder
  builder.registerTypeAdapter(classOf[BigDecimal], new BigDecimalSerializer)
  builder.registerTypeAdapter(classOf[BigDecimal], new BigDecimalDeserializer)
  private val gson = builder.create()

  def monitorCache() = AuthAction { implicit req =>

    Ok(views.html.monitor())
  }

  def monitorCacheData() = AuthAction { implicit req =>
    val marketDataCache = (marketDataService.asInstanceOf[MarketDataServiceImpl]).getMarketDataCache
    val cache: java.util.Collection[MarketData] = marketDataCache.map{ case(k, v) => (v) }.toList

    val json = gson.toJson(cache)
    Ok(json)
  }

  def monitorCacheSize() = AuthAction { implicit req =>
    val marketDataCache = (marketDataService.asInstanceOf[MarketDataServiceImpl]).getMarketDataCache
    val size = marketDataCache.size

    val json = gson.toJson(size)
    Ok(json)
  }

  def monitorCacheMissingId() = AuthAction { implicit req =>
    val missingId = (marketDataService.asInstanceOf[MarketDataServiceImpl]).getCacheMissingSecurityId

    val json = gson.toJson(missingId)
    Ok(json)
  }
}
