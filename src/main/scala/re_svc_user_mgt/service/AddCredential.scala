package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Associate a new user credentials record with an existing user profile record,
 * but only if the credentials for the existing username / password / auth_type
 * match and are validated and the new credentials do not already exist.
 *
 * Params: username, password, auth_type, new_username, new_password, new_auth_type
 */
class AddCredential(username: String, authType: Int) extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
