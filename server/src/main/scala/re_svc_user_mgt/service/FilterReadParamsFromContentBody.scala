package re_svc_user_mgt.service

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.Future

/**
 * At this moment (November 4 2013) the latest Finagle 6.7.4 only reads params
 * from content body when the request method is POST. This filter forces it to
 * also read for PUT, PATCH, and DELETE.
 */
class FilterReadParamsFromContentBody extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val method = request.method
    if (method == Method.Put || method == Method.Patch || method == Method.Delete) {
      // Force the read, because params is lazy
      request.method = Method.Post
      request.params

      request.method = method
    }
    service(request)
  }
}

object FilterReadParamsFromContentBody extends FilterReadParamsFromContentBody
