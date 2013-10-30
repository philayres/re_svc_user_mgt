package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Delete the user credentials from the table.
 *
 * Params: username, auth_type
 */
class DeleteUserCredentials extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
