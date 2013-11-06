package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Delete the user credential from the table.
 *
 * Params: username, auth_type
 */
class CredentialsDelete(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Credential.delete(username, authType)
    Future.value(request.response)
  }
}
