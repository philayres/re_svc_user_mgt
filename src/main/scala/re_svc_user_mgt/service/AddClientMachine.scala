package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import org.jboss.netty.handler.codec.http._

/**
 * Add / remove registered client machine and shared secret if the user is
 * authenticated and matchs an 'admin user / client' record in the database
 *
 * Params: add_client_name, username, auth_type, password
 *
 * If the client_name does not already exist, return successful result: HTTP OK
 * (JSON returning newly generated shared secret)
 */
class AddClientMachine extends Service[Request, Response] {
  def apply(req: Request): Future[Response] = {
    val response = Response(new DefaultHttpResponse(
      req.getProtocolVersion, HttpResponseStatus.NOT_FOUND
    ))
    response.write("Not found")
    Future.value(response)
  }
}
