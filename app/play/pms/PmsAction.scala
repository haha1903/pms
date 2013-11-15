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


trait PmsActionBuilder extends Logging {
  
  private val useDyResponse = false
  
  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => PmsResult): Action[A] = new Action[A] {
    
    def parser = bodyParser
    
    def apply(ctx: Request[A]) = {
      try {
        val result = block(ctx)
        result match {
          case res: PmsJsResult =>
            val respjson = if (useDyResponse) {
              val dyresp = DYJsResponse(Status.OK, "", res.json)
              Json.toJson(dyresp)
            } else {
              res.json
            }
            Ok(respjson)
            
          case res: PmsStrResult =>
            val content = res.content
            val resp = if (useDyResponse) {
              val dyresp = DYStrResponse.create(Status.OK, "", content)
              dyresp
            } else {
              content
            }
            Ok(resp).as("application/json; charset=utf-8")
        }
        
      } catch {
        case e: NotImplementedError => throw new RuntimeException(e)
        case e: LinkageError => throw new RuntimeException(e)
        case e: ClientException =>
          if (useDyResponse) {
            val dyresp = DYJsResponse(Status.BAD_REQUEST, e.getMessage(), JsString(e.getStackTraceString))
            val respjson = Json.toJson(dyresp)
            BadRequest(respjson)
          } else {
            throw e
          }
        
        case e: Throwable =>
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
  }
  
  def apply(block: Request[AnyContent] => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)
  
  def apply(block: => PmsResult): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(_ => block)
}

object PmsAction extends PmsActionBuilder