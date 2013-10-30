package re_svc_user_mgt

import com.twitter.finagle.http.Method
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService

import re_svc_user_mgt.service._

object Routes {
  private val authenticate = new Authenticate
  private val createUser   = new CreateUser
  private val notFound     = new NotFound

  val routes = RoutingService.byMethodAndPath { case (method, path) =>
    Config.log.debug(s"$method $path")

    (method, Path(path)) match {
      case Method.Post -> Root / "authenticate" =>
        authenticate

      case Method.Post -> Root / "create_user" =>
        createUser

      case Method.Patch -> Root / "enable_user" / Long(userId) =>
        new EnableUser(userId)

      case Method.Patch -> Root / "update_password" / username / Integer(authType) =>
        new UpdateCredentialPassword(username, authType)

      case Method.Patch -> Root / "validate_credential" / username / Integer(authType) =>
        new ValidateCredential(username, authType)

      case Method.Delete -> Root / "delete_credential" / username / Integer(authType) =>
        new DeleteCredential(username, authType)

      case Method.Get -> Root / "credential_exists" / username / Integer(authType) =>
        new CredentialExists(username, authType)

      case Method.Post -> Root / "add_credential" / username / Integer(authType) =>
        new AddCredential(username, authType)

      case Method.Post -> Root / "add_client_machine" / username / Integer(authType) =>
        new AddClientMachine(username, authType)

      case Method.Post -> Root / "remove_client_machine" / username / Integer(authType) =>
        new RemoveClientMachine(username, authType)

      case _ =>
        notFound
    }
  }
}
