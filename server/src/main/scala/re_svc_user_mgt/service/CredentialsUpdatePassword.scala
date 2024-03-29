package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Update password stored for a username / auth_type
 * require password to match existing password, unless force_new=true
 *
 * Params: username, auth_type, new_password, [password or force_new=true]
 *
 * Successful return: HTTP OK
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 */
class CredentialsUpdatePassword(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val newPassword = requireParamString(request, "new_password")
    val forceNew    = request.params.getBooleanOrElse("force_new", false)

    val response = request.response
    if (forceNew) {
      updatePassword(response, username, authType, newPassword)
    } else {
      val password = requireParamString(request, "password")
      if (FilterRequireCredential.checkUser(request, username, authType, password))
        updatePassword(response, username, authType, newPassword)
    }
    Future.value(response)
  }

  private def updatePassword(response: Response, username: String, authType: Int, newPassword: String) {
    if (!Credential.updatePassword(username, authType, newPassword)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "Incorrect username or auth type"))
    }
  }
}
