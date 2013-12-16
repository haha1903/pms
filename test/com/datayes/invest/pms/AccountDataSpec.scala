package com.datayes.invest.pms.test

import org.specs2.mutable._

import com.datayes.invest.pms.util.SpecService
import org.joda.time.LocalDate
import java.io.{PrintWriter, File}

class AccountDataSpec extends Specification with SpecService {
  def export(path: String)(op: PrintWriter => Unit)(implicit parent: String = "/Users/changhai/tmp/pms_export") = {
    val folder = new File(parent)
    folder.mkdirs
    val pw = new PrintWriter(new File(folder, path))
    op(pw)
    pw.close
  }

  "Account Data" should {
    /*
    "export clients" in {
      val s = accountDataService.exportClients
      s must_== "通联数据~99 West Lujiazui Road, Pudong, Shanghai, P.R.China 200120~CN~CNY~dyStgClient01~"
    }

    "export accounts" in {
      val s = accountDataService.exportAccounts
      s must_== "通联数据~量化策略C~CNY~1~"
    }
    "export subaccounts" in {
      val s = accountDataService.exportSubaccounts
      s must_== "通联数据~量化策略C~SubaccountName~1~"
    }
    "respond equity position" in {
      val s = accountDataService.exportEquityPosition(1L, LocalDate.parse("2013-09-06"))
      s must not beNull
    }
    */
    "export all" in {
      export("clients") { pw =>
        pw.print(accountDataService.exportClients)
      }
      export("accounts") { pw =>
        pw.print(accountDataService.exportAccounts)
      }
      export("subaccounts") { pw =>
        pw.print(accountDataService.exportSubaccounts)
      }
      val positions = accountDataService.exportEquityPositions(LocalDate.parse("2013-12-12"))
      positions.foreach { p =>
        val (id, content) = p
        export(s"equity_position_$id") { pw =>
          pw.print(content)
        }
      }
    }
  }
}