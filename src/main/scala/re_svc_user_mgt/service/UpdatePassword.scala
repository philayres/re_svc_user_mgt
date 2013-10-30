package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Update password stored for a username / auth_type
 * require prev_password to match existing password, unless force_new=true
 *
 * Params: username, auth_type, new_password, [prev_password or force_new=true]
 *
 * Successful return: HTTP OK with a JSON
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 */
class UpdatePassword extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
