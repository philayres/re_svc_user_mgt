package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.Config

class FilterAccessLog extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val ret = service(request)

    val status = request.response.status
    Config.log.debug(s"${request.method} ${request.getUri} ${status}")

    val clientId = FilterAccessLog.getClientId(request).get
    FilterAccessLog.getRequestType(request) foreach { requestType =>
    }

    ret
  }
}

object RequestType {
  val CLIENT_MACHINE_CREATE = 0
  val CLIENT_MACHINE_DELETE = 1

  val USER_CREATE  = 20
  val USER_ENABLE  = 21
  val USER_DISABLE = 22

  val CREDENTIAL_EXISTS       = 40
  val CREDENTIAL_AUTHENTICATE = 41

  val CREDENTIAL_CREATE          = 42
  val CREDENTIAL_VALIDATE        = 43
  val CREDENTIAL_INVALIDATE      = 44
  val CREDENTIAL_UPDATE_PASSWORD = 45
  val CREDENTIAL_DELETE          = 46
}

/** Use request headers to share data that needs to be logged among filters and service. */
object FilterAccessLog extends FilterAccessLog {
  private val CLIENT_ID     = "X_CLIENT_ID"
  private val REQUEST_TYPE  = "X_REQUEST_TYPE"

  private val USERNAME      = "X_USERNAME"
  private val AUTH_TYPE     = "X_AUTH_TYPE"
  private val CREDENTIAL_ID = "X_CREDENTIAL_ID"
  private val USER_ID       = "X_USER_ID"

  def getClientId    (request: Request): Option[Int]    = Option(request.getHeader(CLIENT_ID))    .map(_.toInt)
  def getRequestType (request: Request): Option[Int]    = Option(request.getHeader(REQUEST_TYPE)) .map(_.toInt)

  def getUsername    (request: Request): Option[String] = Option(request.getHeader(USERNAME))
  def getAuthType    (request: Request): Option[Int]    = Option(request.getHeader(AUTH_TYPE))    .map(_.toInt)
  def getCredentialId(request: Request): Option[Int]    = Option(request.getHeader(CREDENTIAL_ID)).map(_.toInt)
  def getUserId      (request: Request): Option[Int]    = Option(request.getHeader(USER_ID))      .map(_.toInt)

  def setClientId    (request: Request, clientId:    Int)  { request.setHeader(CLIENT_ID,     clientId.toString)     }
  def setRequestType (request: Request, requestType: Int)  { request.setHeader(REQUEST_TYPE,  requestType.toString)  }

  def setUsername    (request: Request, username: String)  { request.setHeader(USERNAME,      username)              }
  def setAuthType    (request: Request, authType: Int)     { request.setHeader(AUTH_TYPE,     authType.toString)     }
  def setCredentialId(request: Request, credentialId: Int) { request.setHeader(CREDENTIAL_ID, credentialId.toString) }
  def setUserId      (request: Request, userId: Int)       { request.setHeader(USER_ID,       userId.toString)       }
}
