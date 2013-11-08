package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.sso.AuthAction
import play.api.libs.ws.WS
import scala.concurrent.Await
import scala.concurrent.duration._
import controllers.util.Jsonp
import play.api.mvc._
import play.api.libs.json.Json
import com.datayes.invest.pms.config.Config

class ProxyController extends Controller with Jsonp with Logging {
  
  val attributionHost = Config.INSTANCE.getString("proxy.attribution.host")
      
  val timeout = Config.INSTANCE.getString("proxy.attribution.timeout")

  val uploadPath = Config.INSTANCE.getString("proxy.upload.path")

  val clientId = Config.INSTANCE.getString("proxy.attribution.client.id")

  val odbcName = Config.INSTANCE.getString("proxy.attribution.odbc.name")
      
  private def getMapOrNull(value: Option[Map[String, Seq[String]]]): Map[String, Seq[String]] = {
    value match {
      case Some(x) => x
      case None => null
    }
  }
  
  def attribution() = AuthAction { implicit req =>
    val queryStrings = req.queryString.map { case (k, seq) => (k, seq.head) }.toSeq


    val response = WS.url(attributionHost).
      withHeaders(CONTENT_TYPE -> "application/x-www-form-urlencoded").
      withQueryString(queryStrings: _*).withQueryString("clientid" -> clientId).
      withQueryString("odbc" -> odbcName).get

    val json = try {
      Await.result(response, timeout.toLong seconds).body
    } catch {
      case e: Throwable => logger.error("the host:{} does not respond within the timeout period", attributionHost); null
    }

    Ok(json).as("application/javascript")
  }

  def upload = AuthAction(parse.multipartFormData) { request =>
    try {
      request.body.file("upload").map { file =>
        import java.io.File
        val filename = file.filename
        val contentType = file.contentType
        file.ref.moveTo(new File(uploadPath, filename))
        val json = Json.obj(
          "success" -> true,
          "message" -> "Upload is successful"
        )
        Ok(json)
      }.getOrElse {
        val json = Json.obj(
          "success" -> false,
          "message" -> "Missing file"
        )
        BadRequest(json)
      }
    } catch {
      case e: Throwable =>
        logger.error("Error receiving uploaded file: " + e.getMessage, e)
        val json = Json.obj(
          "success" -> false,
          "message" -> e.getMessage
        )
        InternalServerError(json)
    }
  }
}
