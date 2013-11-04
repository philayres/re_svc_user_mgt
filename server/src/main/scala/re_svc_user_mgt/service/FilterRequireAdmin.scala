package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.{Credential, User}

/**
 * Must be put behind FilterRequireCredential.
 * Verify userId, make sure that the user is an admin.
 */
class FilterRequireAdmin extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val userId = FilterRequireCredential.getUserId(request)
    if (User.isAdmin(userId)) {
      service(request)
    } else {
      val response = request.response
      response.status        = Status.Unauthorized
      response.contentString = Json(Map("error" -> "Not admin"))
      response.setContentTypeJson()
      Future.value(response)
    }
  }
}

object FilterRequireAdmin extends FilterRequireAdmin
