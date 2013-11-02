package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import re_svc_user_mgt.model.User

/**
 * Set the enabled/disabled flag for a user profile.
 *
 * Params: user_id, enabled=true|false
 */
class UsersEnable(userId: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val enabled = request.params.getBoolean("enabled").getOrElse(true)
    User.enable(userId, enabled)
    Future.value(request.response)
  }
}
