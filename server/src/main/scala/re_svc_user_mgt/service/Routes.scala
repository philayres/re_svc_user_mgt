package re_svc_user_mgt.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.{NotFoundService, RoutingService}

// com.twitter.finagle.http.path.Integer does not match negative number
// (used in our client tests):
// https://github.com/twitter/finagle/issues/219
//
// Use our own.
object AnyInt {
  def unapply(str: String): Option[Int] = {
    try {
      Some(str.toInt)
    } catch {
      case _: NumberFormatException => None
    }
  }
}

object Routes {
  // https://github.com/twitter/finagle/blob/master/finagle-http/src/main/scala/com/twitter/finagle/http/service/RoutingService.scala
  private def byRequest(routes: PartialFunction[Request, Service[Request, Response]]) =
    new RoutingService(
      new PartialFunction[Request, Service[Request, Response]] {
        def apply(request: Request) = routes(request)
        def isDefinedAt(request: Request) = routes.isDefinedAt(request)
      })

  val routes = byRequest { case request =>
    val method = request.method
    val path   = request.path

    (method, Path(path)) match {
      // Client machines -------------------------------------------------------

      case Method.Post -> Root / "client_machines" =>
        FilterAccessLog.setRequestType(request, RequestType.CLIENT_MACHINE_CREATE)
        FilterRequireCredential andThen FilterRequireAdmin andThen ClientMachinesCreate

      case Method.Delete -> Root / "client_machines" / clientName =>
        FilterAccessLog.setRequestType(request, RequestType.CLIENT_MACHINE_DELETE)
        FilterRequireCredential andThen FilterRequireAdmin andThen { new ClientMachinesDelete(clientName) }

      // Users -----------------------------------------------------------------

      case Method.Post -> Root / "users" =>
        FilterAccessLog.setRequestType(request, RequestType.USER_CREATE)
        UsersCreate

      case Method.Patch -> Root / "users" / AnyInt(userId) / "enable" =>
        FilterAccessLog.setRequestType(request, RequestType.USER_ENABLE)
        new UsersEnable(userId)

      case Method.Patch -> Root / "users" / AnyInt(userId) / "disable" =>
        FilterAccessLog.setRequestType(request, RequestType.USER_DISABLE)
        new UsersDisable(userId)

      // Credentials -----------------------------------------------------------

      case Method.Get -> Root / "credentials" / username / AnyInt(authType) =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_EXISTS)
        new CredentialsExists(username, authType)

      case Method.Post -> Root / "credentials" / "authenticate" =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_AUTHENTICATE)
        FilterRequireCredential andThen CredentialsAuthenticate

      case Method.Post -> Root / "credentials" =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_CREATE)
        FilterRequireCredential andThen CredentialsCreate

      case Method.Patch -> Root / "credentials" / username / AnyInt(authType) / "validate" =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_VALIDATE)
        new CredentialsValidate(username, authType)

      case Method.Patch -> Root / "credentials" / username / AnyInt(authType) / "invalidate" =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_INVALIDATE)
        new CredentialsInvalidate(username, authType)

      case Method.Patch -> Root / "credentials" / username / AnyInt(authType) / "update_password" =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_UPDATE_PASSWORD)
        new CredentialsUpdatePassword(username, authType)

      case Method.Delete -> Root / "credentials" / username / AnyInt(authType) =>
        FilterAccessLog.setRequestType(request, RequestType.CREDENTIAL_DELETE)
        new CredentialsDelete(username, authType)

      // Not found -------------------------------------------------------------

      case _ =>
        NotFoundService
    }
  }
}
