package re_svc_user_mgt.service

import com.twitter.finagle.http.Method
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.{NotFoundService, RoutingService}

import re_svc_user_mgt.Config

object Routes {
  val routes = RoutingService.byMethodAndPath { case (method, path) =>
    Config.log.debug(s"$method $path")

    (method, Path(path)) match {
      // Client machines -------------------------------------------------------

      case Method.Post -> Root / "client_machines" =>
        ClientMachinesCreate

      case Method.Delete -> Root / "client_machines" / clientName =>
        new ClientMachinesDelete(clientName)

      // Users -----------------------------------------------------------------

      case Method.Post -> Root / "users" =>
        UsersCreate

      case Method.Patch -> Root / "users" / Long(userId) / "enable" =>
        new UsersEnable(userId)

      // Credentials -----------------------------------------------------------

      case Method.Get -> Root / "credentials" / username / Integer(authType) =>
        new CredentialsExists(username, authType)

      case Method.Post -> Root / "credentials" / "authenticate" =>
        CredentialsAuthenticate

      case Method.Post -> Root / "credentials" =>
        CredentialsCreate

      case Method.Patch -> Root / "credentials" / username / Integer(authType) / "validate" =>
        new CredentialsValidate(username, authType)

      case Method.Patch -> Root / "credentials" / username / Integer(authType) / "update_password" =>
        new CredentialsUpdatePassword(username, authType)

      case Method.Delete -> Root / "credentials" / username / Integer(authType) =>
        new CredentialsDelete(username, authType)

      // Not found -------------------------------------------------------------

      case _ =>
        NotFoundService
    }
  }
}
