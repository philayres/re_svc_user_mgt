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
    FilterAccessLog.setUsername(request, username)
    FilterAccessLog.setAuthType(request, authType)

    val response = request.response
    Credential.exists(username, authType) match {
      case Left(error) =>
        response.status = Status.Conflict
        response.contentString = Json(Map("error" -> error))

      case Right((credentialId, userId)) =>
        FilterAccessLog.setCredentialId(request, credentialId)
        FilterAccessLog.setUserId(request, userId)

        response.contentString = Json(Map("user_id" -> userId))
    }
    response.setContentTypeJson()
    Future.value(request.response)
  }
}
