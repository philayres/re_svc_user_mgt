package re_svc_user_mgt.service

import scala.util.{Try, Success}

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

import org.apache.commons.codec.digest.DigestUtils
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION

import re_svc_user_mgt.Config.log
import re_svc_user_mgt.model.ClientMachine

/** Idea: http://tyleregeto.com/article/a-guide-to-nonce */
class FilterNonceCheck extends SimpleFilter[Request, Response] {
  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    request.headers.get(AUTHORIZATION) match {
      case None =>
        respondError(request, "No Authorization header (<nonce> <client name> <timestamp in seconds>)")

      case Some(header) =>
        val array = header.split(' ')
        if (array.length != 3) {
          respondError(request, "Authorization header must be <nonce> <client name> <timestamp in seconds>")
        } else {
          Try((array(2).toInt)) match {
            case Success(timestamp) =>
              val nonce      = array(0)
              val clientName = array(1)
              checkNonce(request, nonce, clientName, timestamp) match {
                case Some(error) =>
                  respondError(request, error)

                case None =>
                  service(request)
              }

            case _ =>
              respondError(request, "Authorization header must be <nonce> <client name> <timestamp in seconds>")
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

  /** @return Some(error) or None */
  private def checkNonce(request: Request, nonce: String, clientName: String, timestamp: Int): Option[String] = {
    ClientMachine.getSharedSecret(clientName) match {
      case None =>
        Some("Client not found")

      case Some(sharedSecret) =>
        checkNonce(request, nonce, clientName, timestamp, sharedSecret)
    }
  }

  /** @return Some(error) or None */
  private def checkNonce(request: Request, nonce: String, clientName: String, timestamp: Int, sharedSecret: String): Option[String] = {
    val method  = request.method
    val path    = request.uri
    val content = request.contentString  // Empty string (not null) if the content is empty

    val recreatedNonce = DigestUtils.sha256Hex(method + path + content + clientName + sharedSecret + timestamp)
    if (recreatedNonce != nonce) {
      Some("Wrong nonce")
    } else {
      val now = System.currentTimeMillis() / 1000
      if (timestamp > 0 && now >= timestamp && (now - timestamp) < FilterNonceCheck.NONCE_TTL)
        None
      else
        Some("Nonce expired")
    }
  }
}

object FilterNonceCheck extends FilterNonceCheck {
  val NONCE_TTL = 1 * 60  // 1 minute
}
