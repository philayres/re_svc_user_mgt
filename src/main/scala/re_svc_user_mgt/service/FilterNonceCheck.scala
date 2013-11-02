package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.Config.log

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class FilterNonceCheck[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val clientId = requireParamString(request, "client_id")

    // FIXME
    val passed = true

    if (passed) {
      service(request)
    } else {
      log.warning("Nonce check failed: " + request.toString)

      val response           = request.response
      response.status        = Status.Unauthorized
      response.contentString = Json(Map("error" -> "Nonce check failed"))
      response.setContentTypeJson()
      Future.value(response)
    }
  }
}

object FilterNonceCheck extends FilterNonceCheck[Request]
