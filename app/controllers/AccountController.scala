package controllers

import org.joda.time.LocalDate

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.tools.importer.ImportManager
import com.datayes.invest.pms.web.model.writes.AccountWrites
import com.datayes.invest.pms.web.service.AccountService

import javax.inject.Inject
import play.api.libs.json.JsNull
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.pms.ClientException
import play.pms.PmsAction
import play.pms.PmsController


class AccountController extends PmsController with Logging {
  
  @Inject
  private var accountService: AccountService = null
  
  @Inject
  private var importManager: ImportManager = null

  def list = PmsAction { implicit req =>
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    
    val accounts = accountService.getAccountList(asOfDate)
    val json = Json.toJson(accounts)
    json
  }
  
  def delete = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    
    accountService.delete(accountId)
    JsNull
  }
  
  def importUpload =  PmsAction(parse.multipartFormData) { implicit request =>
    try {
      request.body.file("upload").map { file =>
        val filename = file.filename
        val contentType = file.contentType
        val tmpFile = file.ref.file
        logger.debug("Account import csv file received: " + tmpFile)
        val resp = importManager.importAccountCsv(tmpFile, true)

        val json = Json.obj("uuid" -> resp.uuid)
        json

      }.getOrElse {
        throw new ClientException("Failed to import account. Missing import csv file")
      }
    } catch {
      case e: Throwable =>
        logger.debug("Failed to import account: " + e.getMessage, e)
        throw new RuntimeException("Failed to import account: " + e.getMessage(), e)
    }
  }
  
  def importStatus = PmsAction { implicit req =>
    val uuid: String = param("uuid")
    
    val progressReportOpt = importManager.getProgressByUUID(uuid)
    val (success, status, percent, timeElapsed, msg) = progressReportOpt match {
      case Some(p) => (true, p.getStatus.toString(), p.getPercent, p.getTimeElapsed, "Job " + uuid + " is " + p.getPercent + " percent completed")
      case None => (false, "", 0, 0L, "Failed to find an import job with uuid " + uuid)
    }
    val json = Json.obj(
      "uuid" -> uuid,
      "status" -> status,
      "percentComplete" -> percent,
      "timeElapsed" -> timeElapsed,
      "message" -> msg
    )
    json
  }
}