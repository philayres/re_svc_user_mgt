package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response, Status}

import re_svc_user_mgt.model.ClientMachine

/**
 * Add registered client machine and shared secret if the user is authenticated
 * and matches an admin user record in the database.
 *
 * Params: username, auth_type, password, client_name, client_type
 *
 * If the client_name does not already exist, return successful result: HTTP OK
 * (JSON returning newly generated client ID and shared secret)
 */
class ClientMachinesCreate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    // User check is done at Routes

    val response = request.response

    val clientName = requireParamString(request, "client_name")
    if (clientName.contains(' ')) {
      response.status        = Status.BadRequest
      response.contentString = Json(Map("error" -> "client_name must be printable ASCII character, containing no spaces"))
    } else {
      val clientType = requireParamInt(request, "client_type")
      ClientMachine.create(clientName, clientType) match {
        case Left(error) =>
          response.status        = Status.BadRequest
          response.contentString = Json(Map("error" -> error))

        case Right((clientId, sharedSecret)) =>
          response.contentString = Json(Map("client_id" -> clientId, "shared_secret" -> sharedSecret))
      }
    }

    response.setContentTypeJson()
    Future.value(response)
  }
}

object ClientMachinesCreate extends ClientMachinesCreate
