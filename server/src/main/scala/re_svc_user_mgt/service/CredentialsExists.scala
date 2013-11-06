package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.Credential

/**
 * Params: username, auth_type
 *
 * Found return: HTTP OK (with JSON user_id body)
 *
 * Not found return: HTTP not found
 */
class CredentialsExists(username: String, authType: Int) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val response = request.response
    Credential.exists(username, authType) match {
      case Some(userId) =>
        response.contentString = Json(Map("user_id" -> userId))

      case None =>
        response.status = Status.NotFound
    }
    response.setContentTypeJson()
    Future.value(request.response)
  }
}
