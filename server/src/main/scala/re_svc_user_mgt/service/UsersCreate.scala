package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.User

/**
 * Create new user profile with associated credentials record; fail if username
 * already exists for auth_type; set the credentials to 'validated' if optional
 * validated=true otherwise assume false.
 *
 * Params: username, auth_type, password, [validated=true|false]
 *
 * Successful return: HTTP OK with a JSON containing user_id
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 * (username / auth_type combo exists, etc)
 */
class UsersCreate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val username  = requireParamString(request, "username")
    val authType  = requireParamInt(request, "auth_type")
    val password  = requireParamString(request, "password")
    val validated = request.params.getBooleanOrElse("validated", false)

    val response = request.response
    User.create(username, authType, password, validated) match {
      case Some(userId) =>
        FilterAccessLog.setUserId(request, userId)
        response.contentString = Json(Map("user_id" -> userId))

      case None =>
        response.status        = Status.Conflict
        response.contentString = Json(Map("error" -> "Duplicate username + auth_type pair"))
    }
    Future.value(request.response)
  }
}

object UsersCreate extends UsersCreate
