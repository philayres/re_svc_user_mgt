package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Set the validated flag for a user credentials record to false.
 *
 * Params: username, auth_type
 */
class CredentialsInvalidate(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Credential.setValidated(username, authType, false)
    Future.value(request.response)
  }
}
