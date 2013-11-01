package com.datayes.invest.pms.web.sso

import play.api.mvc._
import com.datayes.paas.sso.User
import play.api.mvc.Cookie

trait UserContextHandler {

  def withUserContext(result: Result, username: String): Result

  def removeUserContext(result: Result, username: String): Result

  def getUser[A](request: Request[A]): Option[User]

}

object UserContextHandler {

  private val USER = "user"

  /* Handler implemented with cookie */
  object CookieHandler extends UserContextHandler {

    def withUserContext(result: Result, username: String): Result = {
      result.withCookies(Cookie(USER, username))
    }

    def removeUserContext(result: Result, username: String): Result = {
      result.discardingCookies(DiscardingCookie(USER, "/"))
    }

    def getUser[A](request: Request[A]): Option[User] = {
      request.cookies.get(USER) match {
        case Some(cookie) => Some(new User(cookie.value))
        case None => None
      }
    }
  }

  /* Handler implemented with session */
  object SessionHandler extends UserContextHandler {

    def withUserContext(result: Result, username: String): Result = {
      result.withCookies(Cookie(USER, username))
    }

    def removeUserContext(result: Result, username: String): Result = {
      result.discardingCookies(DiscardingCookie(USER, "/"))
    }

    def getUser[A](request: Request[A]): Option[User] = {
      request.cookies.get(USER) match {
        case Some(cookie) => Some(new User(cookie.value))
        case None => None
      }
    }
  }
}
