package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response, Status}

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
    val username  = request.params.get("username").get
    val authType  = request.params.getInt("auth_type").get
    val password  = request.params.get("password").get
    val validated = request.params.getBooleanOrElse("validated", false)

    val response = request.response
    User.create(username, authType, password, validated) match {
      case Left(error) =>
        response.status        = Status.BadRequest
        response.contentString = Json(Map("error" -> error))

      case Right(userId) =>
        response.contentString = Json(Map("user_id" -> userId))
    }
    Future.value(request.response)
  }
}

object UsersCreate extends UsersCreate
