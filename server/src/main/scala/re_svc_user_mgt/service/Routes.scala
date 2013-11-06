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
        FilterRequireCredential andThen FilterRequireAdmin andThen ClientMachinesCreate

      case Method.Delete -> Root / "client_machines" / clientName =>
        FilterRequireCredential andThen FilterRequireAdmin andThen { new ClientMachinesDelete(clientName) }

      // Users -----------------------------------------------------------------

      case Method.Post -> Root / "users" =>
        UsersCreate

      case Method.Patch -> Root / "users" / Integer(userId) / "enable" =>
        new UsersEnable(userId)

      case Method.Patch -> Root / "users" / Integer(userId) / "disable" =>
        new UsersDisable(userId)

      // Credentials -----------------------------------------------------------

      case Method.Get -> Root / "credentials" / username / Integer(authType) =>
        new CredentialsExists(username, authType)

      case Method.Post -> Root / "credentials" / "authenticate" =>
        FilterRequireCredential andThen CredentialsAuthenticate

      case Method.Post -> Root / "credentials" =>
        FilterRequireCredential andThen CredentialsCreate

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
