package re_svc_user_mgt.service

import scala.util.{Try, Success}

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION

import re_svc_user_mgt.Config.log
import re_svc_user_mgt.model.ClientMachine

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class FilterNonceCheck[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    request.headers.get(AUTHORIZATION) match {
      case None =>
        respondError(request, "No Authorization header (<client ID> <nonce> <milisecond timestamp>)")

      case Some(h) =>
        val a = h.split(' ')
        if (a.length != 3) {
          respondError(request, "Authorization header must be <client ID> <nonce> <milisecond timestamp>")
        } else {
          Try((a(0).toInt, a(2).toLong)) match {
            case Success((clientId, ms)) =>
              val nonce = a(1)
              checkNonce(request, clientId, nonce, ms) match {
                case Some(error) =>
                  respondError(request, error)

                case None =>
                  service(request)
              }

            case _ =>
              respondError(request, "Client ID and timestamp [ms] in Authorization header must be integer numbers")
          }
        }
    }
  }

  //----------------------------------------------------------------------------

  private def respondError(request: REQUEST, error: String): Future[Response] = {
    val msg = s"Nonce check failed ($error)"
    log.warning(msg + ": " + request.toString)

    val response           = request.response
    response.status        = Status.Unauthorized
    response.contentString = Json(Map("error" -> msg))
    response.setContentTypeJson()
    Future.value(response)
  }

  /** @return Some(error) or None */
  private def checkNonce(request: Request, clientId: Int, nonce: String, ms: Long): Option[String] = {
    ClientMachine.getSharedSecret(clientId) match {
      case None =>
        Some("Client not found")

      case Some(sharedSecret) =>
        checkNonce(request, sharedSecret, nonce, ms)
    }
  }

  /** @return Some(error) or None */
  private def checkNonce(request: Request, sharedSecret: String, nonce: String, ms: Long): Option[String] = {
    // FIXME
    None
  }
}

object FilterNonceCheck extends FilterNonceCheck[Request]
