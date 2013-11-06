package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Authenticate existing user, querying username and password and auth_type.
 * Check if user profile enabled and user credentials validated.
 *
 * Params: username, auth_type, password
 *
 * Successful return: HTTP OK with a JSON user_id
 *
 * Failed return: HTTP not authorized, with a JSON body representing the reason
 * (user/password not found, not validated, not enabled)
 */
class CredentialsAuthenticate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    // User check is done at Routes

    val userId             = FilterRequireCredential.getUserId(request)
    val response           = request.response
    response.contentString = Json(Map("user_id" -> userId))
    response.setContentTypeJson()
    Future.value(response)
  }
}

object CredentialsAuthenticate extends CredentialsAuthenticate
