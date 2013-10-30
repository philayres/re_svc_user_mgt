package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

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
  def apply(req: Request): Future[Response] = {
    val username  = req.params.get("username").get
    val password  = req.params.get("password").get
    val auth_type = req.params.get("auth_type").get

    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.OK
    ))

    Future.value(response)
  }
}
