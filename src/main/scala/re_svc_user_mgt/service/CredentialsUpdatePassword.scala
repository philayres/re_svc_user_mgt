package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

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
    val newPassword = request.params.get("new_password").get
    val forceNew    = request.params.getBoolean("force_new").getOrElse(false)

    if (forceNew) {
      Credential.updatePassword(username, authType, newPassword)
    } else {
      if (FilterRequireCredential.checkUser(request))
        Credential.updatePassword(username, authType, newPassword)
    }
    Future.value(request.response)
  }
}
