package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

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
class Authenticate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val username  = request.params.get("username").get
    val password  = request.params.get("password").get
    val auth_type = request.params.get("auth_type").get

    val response = request.response
    Future.value(response)
  }
}

object Authenticate extends Authenticate
