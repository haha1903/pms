package play.pms

import com.datayes.invest.pms.logging.Logging
import play.api.http.Status
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
import com.datayes.invest.pms.web.sso.SamlActionTrait
import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.web.sso.AuthAction


trait PmsActionBuilder extends Logging {
  
  // This flag is used by PMS developer
  // Should be set to true when deployed
  private val useDyResponse = true
  
  
  def apply(block: Request[AnyContent] => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)
  
  def apply(block: => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(_ => block)
  
  
  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => PmsResult): Action[A] = new AuthAction[A](bodyParser)({ req =>

    try {
      val result = block(req)
      result match {
        case res: PmsJsResult => respond(res)
        case res: PmsStrResult => respond(res)
      }
        
    } catch {
      case e: NotImplementedError => throw new RuntimeException(e)
      case e: LinkageError => throw new RuntimeException(e)
      case e: ClientException => exception(e)
      case th: Throwable => exception(th)
    }    
  })

  private def respond(res: PmsJsResult) = {
    val respjson = if (useDyResponse) {
      val dyresp = DYJsResponse(Status.OK, "", res.json)
      Json.toJson(dyresp)
    } else {
      res.json
    }
    Ok(respjson)
  }
    
  private def respond(res: PmsStrResult) = {
    val content = res.content
    val resp = if (useDyResponse) {
      val dyresp = DYStrResponse.create(Status.OK, "", content)
      dyresp
    } else {
      content
    }
    Ok(resp).as("application/json; charset=utf-8")
  }
    
  private def exception(e: ClientException) = {
    if (useDyResponse) {
      val dyresp = DYJsResponse(Status.BAD_REQUEST, e.getMessage(), JsString(e.getStackTraceString))
      val respjson = Json.toJson(dyresp)
      BadRequest(respjson)
    } else {
      throw e
    }
  }
    
  private def exception(e: Throwable) = {
    logger.warn("Internal server error: {}", e.getMessage(), e)
    if (useDyResponse) {
      val dyresp = DYJsResponse(Status.INTERNAL_SERVER_ERROR, e.getMessage, JsString(e.getStackTraceString))
      val respjson = Json.toJson(dyresp)
      InternalServerError(respjson)
    } else {
      throw e
    }
  }
}

object PmsAction extends PmsActionBuilder {
  
  lazy val ssoEnabled = Config.INSTANCE.getBoolean("paas.sso.enabled", false)
}