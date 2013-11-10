package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.User

/** Set the enabled flag for a user profile to true. */
class UsersEnable(userId: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    FilterAccessLog.setUserId(request, userId)

    val response = request.response
    if (!User.setEnabled(userId, true)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "User does not exist"))
    }
    Future.value(response)
  }
}
