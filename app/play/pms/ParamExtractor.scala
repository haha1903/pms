package play.pms

import play.api.mvc.Request
import play.api.mvc.AnyContent
import org.joda.time.LocalDate

trait ParamExtractor {

  protected def param(name: String)(implicit request: Request[AnyContent]) = ParamString(name, request)
  
  /*
   * String param
   */
  case class ParamString(name: String, request: Request[AnyContent]) {
    def optional = ParamStringOpt(name, request)
    def default(v: String) = ParamStringDefault(name, v, request)
    
    def asInt = ParamInt(name, request)
    def asLong = ParamLong(name, request)
    def asLocalDate = ParamLocalDate(name, request)
  }

  case class ParamStringOpt(name: String, request: Request[AnyContent])
  
  case class ParamStringDefault(name: String, default: String, request: Request[AnyContent])

  implicit def paramStringConvert(p: ParamString) ={
    val param = p.request.getQueryString(p.name).getOrElse(throw new ParamNotFoundException(p.name))
    param
  }

  implicit def paramStringOptConvert(p: ParamStringOpt) = {
    val paramOpt = p.request.getQueryString(p.name)
    paramOpt
  }
  
  implicit def paramStringDefaultConvert(p: ParamStringDefault) = {
    val param = p.request.getQueryString(p.name).getOrElse(p.default)
    param
  }
  
  /*
   * Int param
   */
  case class ParamInt(name: String, request: Request[AnyContent]) {
    def optional = ParamIntOpt(name, request)
    def default(v: Int) = ParamIntDefault(name, v, request)
  }

  case class ParamIntOpt(name: String, request: Request[AnyContent])
  
  case class ParamIntDefault(name: String, default: Int, request: Request[AnyContent])

  implicit def paramIntConvert(p: ParamInt) = {
    val i = p.request.getQueryString(p.name) match {
      case Some(p) => Integer.parseInt(p)
      case None => throw new ParamNotFoundException(p.name)
    }
    i
  }
  
  implicit def paramIntOptConvert(p: ParamIntOpt) = {
    val iOpt = p.request.getQueryString(p.name) match {
      case Some(p) => Some(Integer.parseInt(p))
      case None => None
    }
    iOpt
  }
  
  implicit def paramIntDefaultConvert(p: ParamIntDefault) = {
    val i = p.request.getQueryString(p.name) match {
      case Some(p) => Integer.parseInt(p)
      case None => p.default
    }
    i
  }
  
  /*
   * Long param
   */
  case class ParamLong(name: String, request: Request[AnyContent]) {
    def optional = ParamLongOpt(name, request)
    def default(v: Long) = ParamLongDefault(name, v, request)
  }

  case class ParamLongOpt(name: String, request: Request[AnyContent])
  
  case class ParamLongDefault(name: String, default: Long, request: Request[AnyContent])

  implicit def paramLongConvert(p: ParamLong) = {
    val l = p.request.getQueryString(p.name) match {
      case Some(p) => java.lang.Long.parseLong(p)
      case None => throw new ParamNotFoundException(p.name)
    }
    l
  }
  
  implicit def paramLongOptConvert(p: ParamLongOpt) = {
    val lOpt = p.request.getQueryString(p.name) match {
      case Some(p) => Some(java.lang.Long.parseLong(p))
      case None => None
    }
    lOpt
  }
  
  implicit def paramLongDefaultConvert(p: ParamLongDefault) = {
    val l = p.request.getQueryString(p.name) match {
      case Some(p) => java.lang.Long.parseLong(p)
      case None => p.default
    }
    l
  }
  
  
  /*
   * LocalDate param
   */
  case class ParamLocalDate(name: String, request: Request[AnyContent]) {
    def optional = ParamLocalDateOpt(name, request)
    def default(v: LocalDate) = ParamLocalDateDefault(name, v, request)
  }

  case class ParamLocalDateOpt(name: String, request: Request[AnyContent])
  
  case class ParamLocalDateDefault(name: String, default: LocalDate, request: Request[AnyContent])

  implicit def paramLocalDateConvert(p: ParamLocalDate) = {
    val l = p.request.getQueryString(p.name) match {
      case Some(p) => LocalDate.parse(p)
      case None => throw new ParamNotFoundException(p.name)
    }
    l
  }
  
  implicit def paramLocalDateOptConvert(p: ParamLocalDateOpt) = {
    val lOpt = p.request.getQueryString(p.name) match {
      case Some(p) => Some(LocalDate.parse(p))
      case None => None
    }
    lOpt
  }
  
  implicit def paramLocalDateDefault(p: ParamLocalDateDefault) = {
    val l = p.request.getQueryString(p.name) match {
      case Some(p) => LocalDate.parse(p)
      case None => p.default
    }
    l
  }
}

class ParamNotFoundException(name: String) extends RuntimeException {
    
  override def getMessage(): String = "Parameter not found: " + name
}