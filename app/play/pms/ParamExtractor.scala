package play.pms

import play.api.mvc.Request
import play.api.mvc.AnyContent
import org.joda.time.LocalDate

trait ParamExtractor {

  protected def param(name: String)(implicit request: Request[AnyContent]) = Param(name, request)

  case class Param(name: String, request: Request[AnyContent]) {
    def default[T](v: T) = ParamDefault[T](this, v)
  }

  case class ParamDefault[T](underlying: Param, default: T)
  
  /*
   * Implicit conversions
   */
  implicit def param2String(p: Param): String = {
    val s = getNonEmptyQueryString(p.request, p.name).getOrElse(throw new MissingParamException(p.name))
    s
  }
  
  implicit def param2StringOpt(p: Param): Option[String] = getNonEmptyQueryString(p.request, p.name)
  
  implicit def paramDefault2String(p: ParamDefault[String]): String = {
    param2StringOpt(p.underlying).getOrElse(p.default)
  }
  
  implicit def param2Int(p: Param): Int = {
    val s = getNonEmptyQueryString(p.request, p.name).getOrElse(throw new MissingParamException(p.name))
    parseInt(p.name, s)
  }
  
  implicit def param2IntOpt(p: Param): Option[Int] = {
    getNonEmptyQueryString(p.request, p.name) match {
      case Some(s) => Some(parseInt(p.name, s))
      case None => None
    }
  }
  
  implicit def paramDefault2Int(p: ParamDefault[Int]): Int = {
    param2IntOpt(p.underlying).getOrElse(p.default)
  }
  
  implicit def param2Long(p: Param): Long = {
    val s = getNonEmptyQueryString(p.request, p.name).getOrElse(throw new MissingParamException(p.name))
    parseLong(p.name, s)
  }
  
  implicit def param2LongOpt(p: Param): Option[Long] = {
    getNonEmptyQueryString(p.request, p.name) match {
      case Some(s) => Some(parseLong(p.name, s))
      case None => None
    }
  }
  
  implicit def paramDefault2Long(p: ParamDefault[Int]): Long = {
    param2LongOpt(p.underlying).getOrElse(p.default)
  }
  
  implicit def param2LocalDate(p: Param): LocalDate = {
    val s = getNonEmptyQueryString(p.request, p.name).getOrElse(throw new MissingParamException(p.name))
    parseLocalDate(p.name, s)
  }
  
  implicit def param2LocalDateOpt(p: Param): Option[LocalDate] = {
    getNonEmptyQueryString(p.request, p.name) match {
      case Some(s) => Some(parseLocalDate(p.name, s))
      case None => None
    }
  }
  
  implicit def paramDefault2LocalDate(p: ParamDefault[LocalDate]): LocalDate = {
    param2LocalDateOpt(p.underlying).getOrElse(p.default)
  }
  
  /*
   * Helper functions
   */
  private def getNonEmptyQueryString(req: Request[AnyContent], name: String): Option[String] = {
    req.getQueryString(name) match {
      case Some(s) if s != null && s.trim.nonEmpty => Some(s)
      case _ => None
    }
  }
  
  private def parseInt(name: String, s: String): Int =  try {
    Integer.parseInt(s)
  } catch {
    case e: NumberFormatException => throw new ParamFormatException(name, "int", s, e)
  }
  
  private def parseLong(name: String, s: String): Long =  try {
    java.lang.Long.parseLong(s)
  } catch {
    case e: NumberFormatException => throw new ParamFormatException(name, "long", s, e)
  }
  
  private def parseLocalDate(name: String, s: String): LocalDate = try {
    LocalDate.parse(s)
  } catch {
    case e: IllegalArgumentException => throw new ParamFormatException(name, "date", s, e)
  }
}
