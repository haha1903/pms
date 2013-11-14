package controllers

import com.datayes.invest.pms.logging.Logging
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import javax.inject.Inject
import play.api.libs.json.Json
import com.datayes.invest.pms.tools.importer.ImportManager
import controllers.util.Jsonp
import com.datayes.invest.pms.util.progress.ProgressStatus
import com.datayes.invest.pms.web.sso.AuthAction

class ImportController extends Controller with Jsonp with Logging {

  
  @Inject
  private var importManager: ImportManager = null

  def uploadAccountCsv = AuthAction(parse.multipartFormData) { request =>
    try {
      request.body.file("upload").map { file =>
        val filename = file.filename
        val contentType = file.contentType
        val tmpFile = file.ref.file
        logger.debug("Account import csv file received: " + tmpFile)
        val resp = importManager.importAccountCsv(tmpFile, true)

        val json = Json.obj(
          "success" -> true,
          "uuid" -> resp.uuid,
          "estimatedTimeInMinutes" -> resp.estimatedTimeInMinutes,     // TODO
          "message" -> "Upload is successful"
        )
        Ok(json)

      }.getOrElse {
        val json = Json.obj(
          "success" -> false,
          "message" -> "Import csv file is missing"
        )
        BadRequest(json)
      }
    } catch {
      case e: Throwable =>
        logger.debug(e.getMessage, e)
        val json = Json.obj(
          "success" -> false,
          "message" -> e.getMessage
        )
        InternalServerError(json)
    }
  }

  def uploadAccountCsvStatus(uuid: String) = AuthAction { implicit request =>
    val progressReportOpt = importManager.getProgressByUUID(uuid)
    val (success, status, percent, timeElapsed, msg) = progressReportOpt match {
      case Some(p) => (true, p.getStatus.toString(), p.getPercent, p.getTimeElapsed, "Job " + uuid + " is " + p.getPercent + " percent completed")
      case None => (false, "", 0, 0L, "Failed to find an import job with uuid " + uuid)
    }
    val json = Json.obj(
      "success" -> success,
      "uuid" -> uuid,
      "status" -> status,
      "percentComplete" -> percent,
      "timeElapsed" -> timeElapsed,
      "message" -> msg
    )
    respondJsonOrJsonp(json)
  }

//  def deleteAccount(accountId: Long) = AuthAction { implicit request =>
//    val (success, msg) = try {
//      accountMgmtService.deleteAccount(accountId)
//      (true, "Account #" + accountId + " deleted")
//    } catch {
//      case e: Throwable =>
//        logger.warn("Error deleting account #{}", accountId, e)
//        (false, e.getMessage)
//    }
//    val json = Json.obj(
//      "success" -> success,
//      "message" -> msg
//    )
//    respondJsonOrJsonp(json)
//  }

  // TODO what is this used for?
//  def importStockIndexFutureTransactionCsv = AuthAction { implicit req =>
//    val form = Form("filename" -> text)
//    val filename = form.bindFromRequest.get
//
//    tradeReader.generateTransactions(filename)
//    Ok("filename = " + filename)
//  }
}
