package com.datayes.invest.pms.web.sso

import play.api.mvc._
import com.datayes.paas.sso.User
import play.api.mvc.Cookie

trait UserContextHandler {

  def withUserContext(result: Result, user: User): Result

  def removeUserContext(result: Result, username: String): Result

  def getUser[A](request: Request[A]): Option[User]

}

object UserContextHandler {

  private val USER = "user"

  /* Handler implemented with cookie */
  object CookieHandler extends UserContextHandler {

    def withUserContext(result: Result, user: User): Result = {
      result.withCookies(Cookie(USER, user.getName()))
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

    def withUserContext(result: Result, user: User): Result = {
//      result.withCookies(Cookie(USER, username))
      result.withSession((USER, user.getName()))
    }

    def removeUserContext(result: Result, username: String): Result = {
//      result.discardingCookies(DiscardingCookie(USER, "/"))
      result.withNewSession
    }

    def getUser[A](request: Request[A]): Option[User] = {
      request.session.get(USER) match {
        case Some(s) => Some(new User(s))
        case None => None
      }
    }
  }
}
