package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.Config.log
import re_svc_user_mgt.model.ClientMachine

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class FilterNonceCheck[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val clientId = requireParamInt(request, "client_id")

    val erroro = ClientMachine.getSharedSecret(clientId) match {
      case None =>
        Some("client not found")

      case Some(sharedSecret) =>
        checkNonce(request, sharedSecret)
    }

    erroro match {
      case None =>
        service(request)

      case Some(error) =>
        val msg = s"Nonce check failed ($error)"
        log.warning(msg + ": " + request.toString)

        val response           = request.response
        response.status        = Status.Unauthorized
        response.contentString = Json(Map("error" -> msg))
        response.setContentTypeJson()
        Future.value(response)
    }
  }

  /** @return Some(error) or None */
  private def checkNonce(request: Request, sharedSecret: String): Option[String] = {
    Some("TODO")
  }
}

object FilterNonceCheck extends FilterNonceCheck[Request]
