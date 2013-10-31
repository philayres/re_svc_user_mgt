package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import re_svc_user_mgt.Config

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class NonceCheckFilter[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    Config.log.warning(request.toString)

    service(request)
  }
}

object NonceCheckFilter extends NonceCheckFilter[Request]
