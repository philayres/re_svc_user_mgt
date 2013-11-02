package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.{Request, Response}

import re_svc_user_mgt.model.DB

/**
 * Add / remove registered client machine and shared secret if the user is
 * authenticated and matchs an 'admin user / client' record in the database
 *
 * Params: add_client_name, username, auth_type, password
 *
 * If the client_name does not already exist, return successful result: HTTP OK
 * (JSON returning newly generated shared secret)
 */
class ClientMachinesCreate extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = {
    val username = request.params.get("username").get
    val authType = request.params.getInt("auth_type").get
    val password = request.params.get("password").get

    val clientName = request.params.get("client_name").get
    val clientType = request.params.getInt("client_type").get

//    DB.addClientMachine(username, authType, password, clientName, clientType) match {
//      case Left(reason) =>
//    }

    Future.value(request.response)
  }
}

object ClientMachinesCreate extends ClientMachinesCreate
