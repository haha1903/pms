package play.pms

import play.api.mvc.Controller
import org.joda.time.LocalDate
import play.api.mvc.Request
import play.api.mvc.AnyContent

abstract class PmsController extends Controller with ParamExtractor {

  protected def paramAsOfDateOrToday()(implicit req: Request[AnyContent]): LocalDate = {
    val date: LocalDate = param("asOfDate").default(LocalDate.now)
    date
  }
}