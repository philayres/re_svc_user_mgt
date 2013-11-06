package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import re_svc_user_mgt.model.User

/** Set the enabled flag for a user profile to true. */
class UsersEnable(userId: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val enabled = requireParamBoolean(request, "enabled")
    User.setEnabled(userId, true)
    Future.value(request.response)
  }
}
