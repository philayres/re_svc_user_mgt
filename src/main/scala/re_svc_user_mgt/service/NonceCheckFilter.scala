package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import re_svc_user_mgt.Config.log

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class NonceCheckFilter[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    // FIXME
    val passed = true

    if (passed) {
      service(request)
    } else {
      log.warning("Nonce check failed: " + request.toString)
      val response = request.response
      response.status = Status.Forbidden
      response.write("Nonce check failed")
      Future.value(response)
    }
  }
}

object NonceCheckFilter extends NonceCheckFilter[Request]
