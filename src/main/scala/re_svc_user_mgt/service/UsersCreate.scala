package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

/**
 * Create new user profile with associated credentials record; fail if username
 * already exists for auth_type; set the credentials to 'validated' if optional
 * validated=true otherwise assume false.
 *
 * Params: username, password, auth_type, [validated=true|false]
 *
 * Successful return: HTTP OK with a JSON
 *
 * Failed return: HTTP failed, with a JSON body representing the reason
 * (username / auth_type combo exists, etc)
 */
class UsersCreate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    Future.value(request.response)
  }
}

object UsersCreate extends UsersCreate
