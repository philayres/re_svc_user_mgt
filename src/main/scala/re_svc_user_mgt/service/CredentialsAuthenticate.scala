package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response, Status}

import re_svc_user_mgt.model.DB

/**
 * Authenticate existing user, querying username and password and auth_type.
 * Check if user profile enabled and user credentials validated.
 *
 * Params: username, password, auth_type
 *
 * Successful return: HTTP OK with a JSON user_id
 *
 * Failed return: HTTP not authorized, with a JSON body representing the reason
 * (user/password not found, not validated, not enabled)
 */
class CredentialsAuthenticate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val username = request.params.get("username").get
    val authType = request.params.getInt("auth_type").get
    val password = request.params.get("password").get

    val response = request.response
    DB.authenticate(username, authType, password) match {
      case Left(reason) =>
        response.status = Status.Unauthorized
        response.contentString = Json(Map("reason" -> reason))

      case Right(userId) =>
        response.contentString = Json(Map("user_id" -> userId))
    }
    response.setContentTypeJson()
    Future.value(response)
  }
}

object CredentialsAuthenticate extends CredentialsAuthenticate
