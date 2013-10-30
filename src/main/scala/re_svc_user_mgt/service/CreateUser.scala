package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Create new user profile with associated credentials record; fail if username
 * already exists for auth_type; set the credentials to 'validated' if optional
 * validated=true otherwise assume false.
 *
 * Params: username, password, auth_type, [validated=true|false]
 *
 * Successful return: HTTP OK with a JSON
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 * (username / auth_type combo exists, etc)
 */
class CreateUser extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
