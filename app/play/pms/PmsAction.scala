package play.pms

import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.sso.AuthAction
import play.api.http.Status
import play.api.libs.Jsonp
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers
import play.api.mvc.Request
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Results.Ok
import play.api.mvc.Result
import play.api.libs.json.JsValue


trait PmsActionBuilder extends Logging {
  
  // This flag is used by PMS developer
  // Should be set to true when deployed
  private val useDyResponse = true
  
  
  def apply(block: Request[AnyContent] => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)
  
  def apply(block: => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(_ => block)
  
  
  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => PmsResult): Action[A] = new AuthAction[A](bodyParser)({ req =>
    val callback = req.getQueryString("callback")
    try {
      val result = block(req)
      result match {
        case res: PmsJsResult => respond(res, callback)
        case res: PmsStrResult => respond(res, callback)
      }
        
    } catch {
      case e: NotImplementedError => throw new RuntimeException(e)
      case e: LinkageError => throw new RuntimeException(e)
      case e: ClientException => exception(e, callback)
      case th: Throwable => exception(th, callback)
    }    
  })

  private def respond(res: PmsJsResult, callback: Option[String]) = {
    val respjson = if (useDyResponse) {
      val dyresp = DYJsResponse(Status.OK, "", res.json)
      Json.toJson(dyresp)
    } else {
      res.json
    }
    callback match {
      case Some(c) => Ok(Jsonp(c, respjson))
      case None => Ok(respjson)
    }
  }
    
  private def respond(res: PmsStrResult, callback: Option[String]) = {
    val content = res.content
    val resp = if (useDyResponse) {
      val dyresp = DYStrResponse.create(Status.OK, "", content)
      dyresp
    } else {
      content
    }
    val resp2 = callback match {
      case Some(c) => jsonp(resp, c)
      case None => resp
    }
    Ok(resp2).as("application/json; charset=utf-8")
  }
  
  private def jsonp(data: String, callback: String): String = callback + "(" + data + ");"
  
  private def exception(e: ClientException, callback: Option[String]) = {
    if (useDyResponse) {
      val dyresp = DYJsResponse(Status.BAD_REQUEST, e.getMessage(), JsString(e.getStackTraceString))
      val respjson = Json.toJson(dyresp)
      callback match {
        case Some(c) => BadRequest(Jsonp(c, respjson))
        case None => BadRequest(respjson)
      }
    } else {
      throw e
    }
  }
    
  private def exception(e: Throwable, callback: Option[String]) = {
    logger.warn("Internal server error: {}", e.getMessage(), e)
    if (useDyResponse) {
      val dyresp = DYJsResponse(Status.INTERNAL_SERVER_ERROR, e.getMessage, JsString(e.getStackTraceString))
      val respjson = Json.toJson(dyresp)
      callback match {
        case Some(c) => InternalServerError(Jsonp(c, respjson))
        case None => InternalServerError(respjson)
      }
    } else {
      throw e
    }
  }
}

object PmsAction extends PmsActionBuilder {
  
  lazy val ssoEnabled = Config.INSTANCE.getBoolean("paas.sso.enabled", false)
}