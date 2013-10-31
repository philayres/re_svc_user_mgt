package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

/**
 * Update password stored for a username / auth_type
 * require prev_password to match existing password, unless force_new=true
 *
 * Params: username, auth_type, new_password, [prev_password or force_new=true]
 *
 * Successful return: HTTP OK with a JSON
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 */
class UpdateCredentialPassword(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Future.value(request.response)
  }
}
