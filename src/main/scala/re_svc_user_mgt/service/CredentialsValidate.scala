package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import re_svc_user_mgt.model.Credential

/**
 * Set the validated flag for a user credentials record.
 *
 * Params: username, auth_type, validated
 */
class CredentialsValidate(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val validated = request.params.getBoolean("validated").getOrElse(false)
    Credential.validate(username, authType, validated)
    Future.value(request.response)
  }
}
