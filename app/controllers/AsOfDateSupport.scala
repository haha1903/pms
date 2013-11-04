package controllers

import play.api.mvc.{AnyContent, Request}
import org.joda.time.LocalDate
import com.datayes.invest.pms.logging.Logging

trait AsOfDateSupport extends Logging {

  protected def getAsOfDate()(implicit req: Request[AnyContent]): LocalDate = {
    val asOfDate = req.queryString.get("asOfDate").flatMap(_.headOption).map(LocalDate.parse(_)).
      getOrElse(LocalDate.now)
    logger.debug("As of date from parameter: {}", asOfDate)
    asOfDate
  }

  protected def getAsOfDateOpt()(implicit req: Request[AnyContent]): Option[LocalDate] = {
    try {
      val d = getAsOfDate()
      Some(d)
    } catch {
      case e: IllegalArgumentException => None
    }
  }
}
