package play.pms

import play.api.mvc.Request
import play.api.mvc.AnyContent
import org.joda.time.LocalDate

trait ParamExtractor {

  protected def param(name: String)(implicit request: Request[AnyContent]) = Param(name, request)
  
//  protected def param[T](name: String, default: T)(implicit request: Request[AnyContent]) = ParamDefault(Param(name, request), default)
  
  /*
   * String param
   */
  case class Param(name: String, request: Request[AnyContent]) {
    def default[T](v: T) = ParamDefault[T](this, v)
  }

  case class ParamDefault[T](underlying: Param, default: T)
  
  implicit def param2String(p: Param): String = {
    val s = p.request.getQueryString(p.name).getOrElse(throw new MissingParamException(p.name))
    s
  }
  
  implicit def param2StringOpt(p: Param): Option[String] = p.request.getQueryString(p.name)
  
  implicit def paramDefault2String(p: ParamDefault[String]): String = {
    val u = p.underlying
    u.request.getQueryString(u.name).getOrElse(p.default)
  }
  
  implicit def param2Int(p: Param): Int = {
    val s = p.request.getQueryString(p.name).getOrElse(throw new MissingParamException(p.name))
    parseInt(p.name, s)
  }
  
  implicit def param2IntOpt(p: Param): Option[Int] = {
    p.request.getQueryString(p.name) match {
      case Some(s) => Some(parseInt(p.name, s))
      case None => None
    }
  }
  
  private def parseInt(name: String, v: String): Int =  try {
    Integer.parseInt(v)
  } catch {
    case e: NumberFormatException => throw new ParamFormatException(name, "int", v, e)
  }
}
