package play.pms

import play.api.mvc.Request
import play.api.mvc.AnyContent
import org.joda.time.LocalDate

trait ParamExtractor {

  protected def param(name: String)(implicit request: Request[AnyContent]) = Param(name, request)
  
  /*
   * String param
   */
  case class Param(name: String, request: Request[AnyContent])

//  case class ParamOpt(name: String, request: Request[AnyContent])
  
//  case class ParamDefault[T](name: String, default: T, request: Request[AnyContent])
  
  implicit def param2String(p: Param): String = {
    val s = p.request.getQueryString(p.name).getOrElse(throw new MissingParamException(p.name))
    s
  }
  
  implicit def paramOpt2String(p: Param): Option[String] = p.request.getQueryString(p.name)
  
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
