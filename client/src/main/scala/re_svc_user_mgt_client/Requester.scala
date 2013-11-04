package re_svc_user_mgt_client

import java.net.{InetSocketAddress, URL, URLEncoder}

import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http, Request, RequestBuilder, Response, RichHttp, SimpleElement}
import com.twitter.util.Future

import org.apache.commons.codec.digest.DigestUtils
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpMethod}
import org.jboss.netty.util.CharsetUtil

class Requester(
  clientName: String, sharedSecret: String,
  https: Boolean, host: String, port: Int
) {
  private val protocol = if (https) "https" else "http"

  private val client: Service[Request, Response] = ClientBuilder()
    .codec(RichHttp[Request](Http()))
    .hosts(new InetSocketAddress(port))
    .hostConnectionLimit(10)
    .build()

  def close() {
    client.close()
  }

  def get(path: Seq[Any]) =
    request(HttpMethod.GET, path)

  def post(path: Seq[Any], form: Map[String, Any] = Map.empty) =
    request(HttpMethod.POST, path, form)

  def put(path: Seq[Any], form: Map[String, Any] = Map.empty) =
    request(HttpMethod.PUT, path, form)

  def patch(path: Seq[Any], form: Map[String, Any] = Map.empty) =
    request(HttpMethod.PATCH, path, form)

  def delete(path: Seq[Any], form: Map[String, Any] = Map.empty) =
    request(HttpMethod.DELETE, path, form)

  //----------------------------------------------------------------------------

  private def request(method: HttpMethod, path: Seq[Any], form: Map[String, Any] = Map.empty): Future[Response] = {
    val builder = RequestBuilder()
      .url(new URL(protocol, host, port, mkPath(path)))

    val req = if (form.nonEmpty) {
      val elems = form.toSeq.map { case (k, v) => SimpleElement(k, v.toString) }
      val req   = builder.add(elems).buildFormPost()
      req.setMethod(method)
      req
    } else {
      builder.build(method, None)
    }

    addNonce(req)
    client(Request(req))
  }

  private def mkPath(path: Seq[Any]): String = {
    val encoded = path.map { elem => URLEncoder.encode(elem.toString, "UTF8") }
    "/" + encoded.mkString("/")
  }

  private def addNonce(request: HttpRequest) {
    val method    = request.getMethod
    val path      = request.getUri
    val content   = request.getContent.toString(CharsetUtil.UTF_8)  // Empty string (not null) if the content is empty
    val timestamp = System.currentTimeMillis() / 1000
    val nonce     = DigestUtils.sha256Hex(method + path + content + clientName + sharedSecret + timestamp)
    request.addHeader(AUTHORIZATION, s"$nonce $clientName $timestamp")
  }
}
