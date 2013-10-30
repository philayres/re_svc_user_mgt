package re_svc_user_mgt

import com.twitter.finagle.http.Method
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService

import re_svc_user_mgt.service._

object Routes {
  private val notFound = new NotFound

  val routes = RoutingService.byMethodAndPath { case (method, path) =>
    (method, Path(path)) match {
      case Method.Post -> Root / "authenticate" => new Authenticate
      case _ => notFound
    }
  }
}
