package com.datayes.invest.pms.web.sso

import play.api.mvc._
import com.datayes.paas.sso.{User, SsoContext}
import play.api.mvc.Results._
import org.opensaml.xml.XMLObject
import scala.collection.mutable
import java.io.IOException
import org.opensaml.saml2.core.impl.LogoutRequestImpl
import java.net.URL
import com.datayes.invest.pms.logging.Logging
import scala.collection.JavaConversions._
import com.datayes.invest.pms.config.Config

trait SamlActionTrait extends Logging {

  val userContextHandler: UserContextHandler

  private object Constants {

    val SAMLRequest = "SAMLRequest"

    val SAMLResponse = "SAMLResponse"

    val RelayState = "RelayState"

    val COOKIE_USER = "user"

    val PROTOCOL = "http://"
  }

  private lazy val authUrl = Config.INSTANCE.getString("paas.sso.auth.url")

  private lazy val consumerUrl = Config.INSTANCE.getString("paas.sso.consumer.url")

  private lazy val consumerPath = new URL(consumerUrl).getPath

  def doSaml(block: Request[AnyContent] => Result): Action[AnyContent] =
    doSaml(BodyParsers.parse.anyContent)(block)

  def doSaml[A](bodyParser: BodyParser[A])(block: Request[A] => Result): Action[A] = new Action[A] { request =>

    def parser = bodyParser
    def apply(request: Request[A]) = try {
      // TODO refactor this code
      SsoContext.removeUser()
      if (request.path != consumerPath) {
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
          val samlRequestOpt = getFormParameter(request, Constants.SAMLRequest)
          val samlResponseOpt = getFormParameter(request, Constants.SAMLResponse)

          if (samlRequestOpt.isDefined) {  // single sign out request
            singleSignOut(samlRequestOpt.get)
          } else if (samlResponseOpt.isDefined) {
            val samlResponseObj = SamlHelper.unmarshall(samlResponseOpt.get)
            if (SamlHelper.SAML_LOGOUT_RESPONSE == samlResponseObj.getDOM().getNodeName()) {
              doLogout(request)
            } else {  // login response
              doLogin(request, samlResponseObj)
            }
          } else {
            MethodNotAllowed  // invalid request
          }

        } else if (request.getQueryString("logout").isDefined) {
          userContextHandler.getUser(request) match {
            case Some(user) => sendLogoutRequest(user)
            case None => Redirect(Constants.PROTOCOL + request.host)
          }
        } else {
          MethodNotAllowed  // invalid request
        }
      }

    }
  }

  private def sendLogoutRequest(user: User): Result = {
    val logoutRequest = SamlHelper.buildLogoutRequest(user, consumerUrl)
    val queryString = Map(
      Constants.SAMLRequest -> Seq(SamlHelper.marshall(logoutRequest)),
      Constants.RelayState  -> Seq("")
    )
    userContextHandler.removeUserContext(Redirect(authUrl, queryString), user.getName())
  }

  private def doLogin[A](request: Request[A], samlResponseObj: XMLObject): Result = {

    import org.opensaml.saml2.core. { Response => SamlResponse }

    val resp = samlResponseObj.asInstanceOf[SamlResponse]
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
      val relayState = getFormParameter(request, Constants.RelayState).getOrElse("")  // TODO What if relay state is empty?
      userContextHandler.withUserContext(Redirect(relayState), username)
    }
  }

  private def doLogout[A](request: Request[A]): Result = {
    // TODO request.getContextpath()
    Redirect(Constants.PROTOCOL + request.host)
  }

  private def singleSignOut(samlRequest: String): Result = {
    val samlRequestObj = SamlHelper.unmarshall(samlRequest)
    if (SamlHelper.SAML_LOGOUT_REQUEST != samlRequestObj.getDOM().getNodeName()) {
      throw new IOException("invalid do logout request")
    }
    val name = samlRequestObj.asInstanceOf[LogoutRequestImpl].getNameID().getValue()
    userContextHandler.removeUserContext(Ok(""), name)
  }

  private def logoutRequest(user: User): Result = {
    val logoutRequest = SamlHelper.buildLogoutRequest(user, consumerUrl)
    val queryString = Map(
      Constants.SAMLRequest -> Seq(SamlHelper.marshall(logoutRequest)),
      Constants.RelayState  -> Seq("")
    )
    Redirect(authUrl, queryString)
  }

  private def doAuth[A](request: Request[A]): Result = {
    val authRequest = SamlHelper.buildAuthRequest(consumerUrl)
    // TODO find a way to get the full request URL
    val requestUrl = "http://" + request.host + request.uri
    val queryString = Map(
      Constants.SAMLRequest -> Seq(SamlHelper.marshall(authRequest)),
      Constants.RelayState  -> Seq(requestUrl)
    )
    Redirect(authUrl, queryString)
  }

  private def getFormParameter[A](request: Request[A], name: String): Option[String] = {
    val formBody = request.body.asInstanceOf[AnyContentAsFormUrlEncoded]
    formBody.asFormUrlEncoded flatMap { params => params.get(name) } flatMap (_.headOption)
  }
}
