package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Associate a new user credentials record with an existing user profile record,
 * but only if the credentials for the existing username / password / auth_type
 * match and are validated and the new credentials do not already exist.
 *
 * Params: username, auth_type, password, new_username, new_auth_type, new_password
 */
class CredentialsCreate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    // User check is done at Routes

    val userId      = FilterAccessLog.getUserId(request).get
    val newUsername = requireParamString(request, "new_username")
    val newAuthType = requireParamInt(request, "new_auth_type")
    val newPassword = requireParamString(request, "new_password")

    val response = request.response
    if (!Credential.create(userId, newUsername, newAuthType, newPassword, true)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "Duplicate username + auth_type pair"))
    }
    response.setContentTypeJson()
    Future.value(response)
  }
}

object CredentialsCreate extends CredentialsCreate
