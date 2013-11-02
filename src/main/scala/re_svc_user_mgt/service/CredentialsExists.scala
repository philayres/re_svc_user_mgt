package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

/**
 * Params: username, auth_type
 *
 * Found return: HTTP OK (with JSON user_id body)
 *
 * Not found return: HTTP not found
 */
class CredentialsExists(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Future.value(request.response)
  }
}
