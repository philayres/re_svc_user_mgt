package re_svc_user_mgt.service

import scala.util.control.NonFatal

import com.twitter.finagle.{CancelledRequestException, Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.logging.Logger
import com.twitter.util.Future

import org.jboss.netty.handler.codec.http.HttpResponseStatus

/**
 * This is a slightly modified version of com.twitter.finagle.http.filter.ExceptionFilter
 * to handle our MissingRequiredParamException.
 *
 * https://github.com/philayres/re_svc_user_mgt/issues/16 Return 400 Bad Request instead of 500 response when required param is missing
 * https://github.com/twitter/finagle/blob/master/finagle-http/src/main/scala/com/twitter/finagle/http/filter/ExceptionFilter.scala
 */
class FilterException extends SimpleFilter[Request, Response] {
  import FilterException.ClientClosedRequestStatus

  private val log = Logger("finagle-http")

  def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    {
      try {
        service(request)
      } catch {
        // apply() threw an exception - convert to Future
        case NonFatal(e) => Future.exception(e)
      }
    } rescue {
      case e: MissingRequiredParamException =>
        // Return 400 Bad Request instead of 500 response when required param is missing
        val response           = request.response
        response.status        = Status.BadRequest
        response.contentString = Json(Map("error" -> s"Missing param: ${e.paramName}"))
        response.setContentTypeJson()
        Future.value(response)
      case e: CancelledRequestException =>
        // This only happens when ChannelService cancels a reply
        log.warning("Cancelled request: %s", request.getUri)
        val response           = request.response
        response.status        = ClientClosedRequestStatus
        response.contentString = Json(Map("error" -> "Cancelled request"))
        response.setContentTypeJson()
        Future.value(response)
      case NonFatal(e) =>
        log.error(e, "uri: %s, exception: %s", request.getUri, e)
        val response           = request.response
        response.status        = Status.InternalServerError
        response.contentString = Json(Map("error" -> "Internal Server Error"))
        response.setContentTypeJson()
        Future.value(response)
    }
}

object FilterException extends FilterException {
  private[FilterException] val ClientClosedRequestStatus =
    new HttpResponseStatus(499, "Client Closed Request")
}
