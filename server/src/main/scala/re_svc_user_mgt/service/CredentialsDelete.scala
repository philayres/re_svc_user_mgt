package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Delete the user credential from the table.
 *
 * Params: username, auth_type
 */
class CredentialsDelete(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val response = request.response
    if (!Credential.delete(username, authType)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "Incorrect username or auth type"))
    }
    Future.value(response)
  }
}
