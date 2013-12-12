package com.datayes.invest.pms.test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.AccountController
import com.datayes.invest.pms.web.service.AccountService
import com.datayes.invest.pms.dao.account.impl.AccountDaoImpl
import com.datayes.invest.pms.persist.hibernate.PersistServiceImpl
import com.datayes.invest.pms.persist.{Persist, Transaction}
import scala.reflect.ClassTag
import java.lang.reflect.Constructor
import com.datayes.invest.pms.util.{SpecService, SpecUtil}

class AccountDataSpec extends Specification with SpecService {
  "The 'Hello world' string" should {
    "contain 12 characters" in {
      "Hello world!" must have size (12)
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
  }
  "Computer model" should {

    "be retrieved by id" in {
      running(FakeApplication()) {
        "haha" in {
          "hello" must have size 5
        }
      }
    }
    "respond to the index Action" in {
      val controller = new AccountController
      controller.setValue("accountService", accountService)
      val result = controller.list(FakeRequest("GET", "/list"))
      result must not beNull
    }
  }
}