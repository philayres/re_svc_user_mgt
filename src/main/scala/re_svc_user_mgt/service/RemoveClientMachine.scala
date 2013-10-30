package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Remove the record from the database table and return HTTP OK if the user is
 * authenticated and matchs an 'admin user / client' record in the database
 * if not found, return HTTP not found.
 *
 * Params: remove_client_name, username, auth_type, password
 */
class RemoveClientMachine extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
