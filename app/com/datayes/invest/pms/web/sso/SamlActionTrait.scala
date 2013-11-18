package com.datayes.invest.pms.web.sso

import java.io.IOException
import java.net.URL

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable

import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.logging.Logging
import com.datayes.paas.sso.SsoContext
import com.datayes.paas.sso.User
import com.datayes.paas.sso.model.LogoutRequest
import com.datayes.paas.sso.model.Message
import com.datayes.paas.sso.model.Response

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results.MethodNotAllowed
import play.api.mvc.Results.Ok
import play.api.mvc.Results.Redirect

object SamlActionTrait {
  
  private object Constants {

    val SAMLRequest = "SAMLRequest"

    val SAMLResponse = "SAMLResponse"

    val RelayState = "RelayState"
  }

  private lazy val pmsUrl = Config.INSTANCE.getString("pms.url", "localhost:9000")
  
  private lazy val authUrl = Config.INSTANCE.getString("paas.sso.auth.url")

  private lazy val consumerIndex: String = {
    val s = Config.INSTANCE.getString("paas.sso.consumer.index", "")
    if (s == null || s.trim().isEmpty()) {
      null
    } else {
      s
    }
  }
  
  private lazy val consumerPath = Config.INSTANCE.getString("paas.sso.consumer.path")

  private lazy val consumerUrl = pmsUrl + consumerPath
}

trait SamlActionTrait[A] extends Logging {

  val userContextHandler: UserContextHandler

  private val saml2 = new Saml2()

  def doSaml(bodyParser: BodyParser[A])(request: Request[A])(block: Request[A] => Result): Result = {

      SsoContext.removeUser()
      if (request.path != SamlActionTrait.consumerPath) {
        // Normal request
        userContextHandler.getUser(request) match {
          case Some(user) =>
            SsoContext.setUser(user)  // User already authenticated, continue with normal flow
            block(request)

          case None =>
            logger.debug("Start SSO authentication. Remote address: " + request.remoteAddress)
            doAuth(request)  // User not authenticated, should start authentication process
        }
      } else {
        // Consumer request
        if ("POST" == request.method && request.body.isInstanceOf[AnyContent]) {
          val samlRequestOpt = getFormParameter(request, SamlActionTrait.Constants.SAMLRequest)
          val samlResponseOpt = getFormParameter(request, SamlActionTrait.Constants.SAMLResponse)

          if (samlRequestOpt.isDefined) {  // single sign out request
            singleSignOut(samlRequestOpt.get)
          } else if (samlResponseOpt.isDefined) {
            val samlResponseObj = saml2.unmarshall(samlResponseOpt.get)
            
            if (samlResponseObj.isInstanceOf[Response]) {
              doLogin(request, samlResponseObj)
            } else {
              doLogout(request)
            }
          } else {
            MethodNotAllowed  // invalid request
          }

        } else if (request.getQueryString("logout").isDefined) {
          userContextHandler.getUser(request) match {
            case Some(user) => sendLogoutRequest(user)
            case None => Redirect(SamlActionTrait.pmsUrl)
          }
        } else {
          MethodNotAllowed  // invalid request
        }
      }
  }

  private def sendLogoutRequest(user: User): Result = {
    val logoutRequest = saml2.buildLogoutRequest(user.getName(), SamlActionTrait.consumerUrl)
    val queryString = Map(
      SamlActionTrait.Constants.SAMLRequest -> Seq(logoutRequest),
      SamlActionTrait.Constants.RelayState  -> Seq("")
    )
    userContextHandler.removeUserContext(Redirect(SamlActionTrait.authUrl, queryString), user.getName())
  }

  private def doLogin[A](request: Request[A], samlResponseObj: Message): Result = {

    val resp = samlResponseObj.asInstanceOf[Response]
    val assertion = resp.getAssertions.get(0)

    val attributes = mutable.Map.empty[String, String]
    var username: String = null

    if (assertion != null) {
      username = assertion.getSubject().getNameID().getValue()
      val attributeStatementList = assertion.getAttributeStatements()
      if (attributeStatementList != null) {
        // we have received attributes of user
        for (statement <- attributeStatementList) {
          val attributesList = statement.getAttributes()
          for (attrib <- attributesList) {
            val elem = attrib.getAttributeValues().get(0).getDOM()
            val attribValue = elem.getTextContent()
            attributes.put(attrib.getName(), attribValue)
          }
        }
      }
    }

    if (username == null) {
      throw new IOException("sso login, user is null")
    } else {
      val relayState = getFormParameter(request, SamlActionTrait.Constants.RelayState).getOrElse("")  // TODO What if relay state is empty?
      userContextHandler.withUserContext(Redirect(relayState), new User(username))
    }
  }

  private def doLogout[A](request: Request[A]): Result = {
    // TODO request.getContextpath()
    Redirect(SamlActionTrait.pmsUrl)
  }

  private def singleSignOut(samlRequest: String): Result = {
    val message = saml2.unmarshall(samlRequest)
    if (! message.isInstanceOf[LogoutRequest]) {
      throw new IOException("invalid do logout request")
    }
    val name = message.asInstanceOf[LogoutRequest].getNameID().getValue()
    userContextHandler.removeUserContext(Ok(""), name)
  }

  private def logoutRequest(user: User): Result = {
    val logoutRequest = saml2.buildLogoutRequest(user.getName(), SamlActionTrait.consumerUrl)
    val queryString = Map(
      SamlActionTrait.Constants.SAMLRequest -> Seq(logoutRequest),
      SamlActionTrait.Constants.RelayState  -> Seq("")
    )
    Redirect(SamlActionTrait.authUrl, queryString)
  }

  private def doAuth[A](request: Request[A]): Result = {
    val authRequest = saml2.buildAuthRequest(SamlActionTrait.consumerUrl, SamlActionTrait.consumerIndex)
    // TODO find a way to get the full request URL
    val requestUrl = SamlActionTrait.pmsUrl + request.uri
    val queryString = Map(
      SamlActionTrait.Constants.SAMLRequest -> Seq(authRequest),
      SamlActionTrait.Constants.RelayState  -> Seq(requestUrl)
    ) ++ request.queryString
    Redirect(SamlActionTrait.authUrl, queryString)
  }

  private def getFormParameter[A](request: Request[A], name: String): Option[String] = {
    val formBody = request.body.asInstanceOf[AnyContentAsFormUrlEncoded]
    formBody.asFormUrlEncoded flatMap { params => params.get(name) } flatMap (_.headOption)
  }
}
