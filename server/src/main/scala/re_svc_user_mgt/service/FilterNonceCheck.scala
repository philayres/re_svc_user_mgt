package re_svc_user_mgt.service

import scala.util.{Try, Success}

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION

import re_svc_user_mgt.Config.log
import re_svc_user_mgt.model.Nonce

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class FilterNonceCheck extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    request.headers.get(AUTHORIZATION) match {
      case None =>
        respondError(request, "No Authorization header (<nonce> <client name> <timestamp in miliseconds>)")

      case Some(header) =>
        val array = header.split(' ')
        if (array.length != 3) {
          respondError(request, "Authorization header must be <nonce> <client name> <timestamp in miliseconds>")
        } else {
          Try((array(2).toLong)) match {
            case Success(timestamp) =>
              val nonce      = array(0)
              val clientName = array(1)

              val method  = request.method.toString
              val path    = request.uri
              val content = request.contentString  // Empty string (not null) if the content is empty

              Nonce.check(method, path, content, nonce, clientName, timestamp) match {
                case Left(error) =>
                  respondError(request, error)

                case Right(clientId) =>
                  FilterAccessLog.setClientId(request, clientId)
                  service(request)
              }

            case _ =>
              respondError(request, "Authorization header must be <nonce> <client name> <timestamp in miliseconds>")
          }
        }
    }
  }

  //----------------------------------------------------------------------------

  private def respondError(request: Request, error: String): Future[Response] = {
    val msg = s"Nonce check failed ($error)"
    log.warning(msg + ": " + request.toString)

    val response           = request.response
    response.status        = Status.Unauthorized
    response.contentString = Json(Map("error" -> msg))
    response.setContentTypeJson()
    Future.value(response)
  }
}

object FilterNonceCheck extends FilterNonceCheck
