package play.pms

import com.datayes.invest.pms.service.marketindex.{MarketIndexInfo, MarketIndexService}
import javax.inject.Inject
import org.joda.time.LocalDate
import play.api.mvc.{AnyContent, Controller, Request}
import scala.collection.JavaConversions._
import com.datayes.invest.pms.util.DefaultValues

abstract class PmsController extends Controller with ParamExtractor {

  @Inject
  protected var marketIndexService: MarketIndexService = null

  protected def paramAsOfDateOrToday()(implicit req: Request[AnyContent]): LocalDate = {
    val date: LocalDate = param("asOfDate").default(LocalDate.now)
    date
  }

  protected def paramBenchmarkIndex()(implicit req: Request[AnyContent]): String = {
    val id: String = param("benchmarkIndex").default(DefaultValues.BENCHMARK_MARKET_INDEX)
    val availableIndexInfos = marketIndexService.getAvailableIndexes()
    val indexId = availableIndexInfos.map(_.getId).find(_ == id).getOrElse(DefaultValues.BENCHMARK_MARKET_INDEX)
    indexId
  }
}