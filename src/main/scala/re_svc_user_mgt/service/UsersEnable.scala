package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

/**
 * Set the enabled/disabled flag for a user profile.
 *
 * Params: user_id, enabled=true|false
 */
class UsersEnable(userId: Long) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Future.value(request.response)
  }
}
