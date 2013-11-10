package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Set the validated flag for a user credentials record to false.
 *
 * Params: username, auth_type
 */
class CredentialsInvalidate(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val response = request.response
    if (!Credential.setValidated(username, authType, false)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "Incorrect username or auth type"))
    }
    Future.value(response)
  }
}
