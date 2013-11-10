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
    val userId = FilterAccessLog.getUserId(request).get
    if (User.isAdmin(userId)) {
      service(request)
    } else {
      val response = request.response
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "User is not admin"))
      response.setContentTypeJson()
      Future.value(response)
    }
  }
}

object FilterRequireAdmin extends FilterRequireAdmin
