package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Verify params: username, auth_type, and password.
 * Make sure that the user is a valid user (but not necessarily an admin).
 * On success, the user ID will be set to the request header.
 */
class FilterRequireCredential extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    if (FilterRequireCredential.checkUser(request))
      service(request)
    else
      Future.value(request.response)
  }
}

object FilterRequireCredential extends FilterRequireCredential {
  private val REQUEST_HEADER_USER_ID = "X_USER_ID"

  /**
   * Verify params: username, auth_type, and password.
   * Make sure that the user is a valid user (but not necessarily an admin).
   *
   * On success, the user ID will be set to the request header.
   *
   * On failure, the request.response is set up properly so that it can be sent
   * right away to the client.
   */
  def checkUser(request: Request): Boolean = {
    val username = requireParamString(request, "username")
    val authType = requireParamInt(request, "auth_type")
    val password = requireParamString(request, "password")
    checkUser(request, username, authType, password)
  }

  def checkUser(request: Request, username: String, authType: Int, password: String): Boolean = {
    Credential.authenticate(username, authType, password) match {
      case Left(error) =>
        val response           = request.response
        response.status        = Status.Unauthorized
        response.contentString = Json(Map("error" -> error))
        response.setContentTypeJson()
        false

      case Right(userId) =>
        setUserId(request, userId)
        true
    }
  }

  def getUserId(request: Request): Int = {
    request.getHeader(REQUEST_HEADER_USER_ID).toInt
  }

  private def setUserId(request: Request, userId: Int) {
    request.setHeader(REQUEST_HEADER_USER_ID, userId.toString)
  }
}
