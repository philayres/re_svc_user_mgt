package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.model.ClientMachine

/**
 * Remove the record from the database table and return HTTP OK if the user is
 * authenticated and matches an admin user record in the database;
 * if not found, return HTTP not found.
 *
 * Params: username, auth_type, password, client_name
 */
class ClientMachinesDelete(clientName: String) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    // User check is done at Routes

    val response = request.response
    if (!ClientMachine.delete(clientName)) {
      response.status        = Status.Conflict
      response.contentString = Json(Map("error" -> "Client not found"))
    }
    response.setContentTypeJson()
    Future.value(response)
  }
}
