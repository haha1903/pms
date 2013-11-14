package play.pms

import com.datayes.invest.pms.logging.Logging

import play.api.http.Status
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BodyParsers
import play.api.mvc.Request
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Results.Ok


trait PmsActionBuilder extends Logging {
  
  private val useDyResponse = false;
  
  def apply(block: Request[AnyContent] => JsValue): Action[AnyContent] = new Action[AnyContent] {
    
    def parser = BodyParsers.parse.anyContent
    
    def apply(ctx: Request[AnyContent]) = {
      try {
        val json = block(ctx)
        val respjson = if (useDyResponse) {
          val dyresp = DYResponse(Status.OK, "", json)
          Json.toJson(dyresp)
        } else {
          json
        }
        Ok(respjson)
      } catch {
        case e: NotImplementedError => throw new RuntimeException(e)
        case e: LinkageError => throw new RuntimeException(e)
        case e @ (_: MissingParamException | _: ParamFormatException) =>
          if (useDyResponse) {
            val dyresp = DYResponse(Status.BAD_REQUEST, e.getMessage(), JsString(e.getStackTraceString))
            val respjson = Json.toJson(dyresp)
            BadRequest(respjson)
          } else {
            throw e
          }
        
        case e: Throwable =>
          logger.warn("Internal server error: {}", e.getMessage(), e)
          if (useDyResponse) {
            val dyresp = DYResponse(Status.INTERNAL_SERVER_ERROR, e.getMessage, JsString(e.getStackTraceString))
            val respjson = Json.toJson(dyresp)
            InternalServerError(respjson)
          } else {
            throw e
          }
      }
    }
  }
  
  def apply(block: => JsValue): Action[AnyContent] = apply(_ => block)
}

object PmsAction extends PmsActionBuilder