package re_svc_user_mgt_client

import java.net.{InetSocketAddress, URL, URLEncoder}

import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http, Request, RequestBuilder, Response, RichHttp, SimpleElement}
import com.twitter.util.Future

import org.jboss.netty.handler.codec.http.HttpMethod

class Requester(
  clientId: Int, sharedSecret: String,
  https: Boolean, host: String, port: Int
) {
  private val protocol = if (https) "https" else "http"

  private val client: Service[Request, Response] = ClientBuilder()
    .codec(RichHttp[Request](Http()))
    .hosts(new InetSocketAddress(port))
    .hostConnectionLimit(1)
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
      .addHeader("Authorization", s"$clientId nonce ${System.currentTimeMillis()}")
      .url(new URL(protocol, host, port, mkPath(path)))

    if (form.nonEmpty) {
      val elems = form.toSeq.map { case (k, v) => SimpleElement(k, v.toString) }
      val req   = builder.add(elems).buildFormPost()
      req.setMethod(method)
      client(Request(req))
    } else {
      val req = builder.build(method, None)
      client(Request(req))
    }
  }

  private def mkPath(path: Seq[Any]): String = {
    val encoded = path.map { elem => URLEncoder.encode(elem.toString, "UTF8") }
    "/" + encoded.mkString("/")
  }
}
