package re_svc_user_mgt

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}
import com.twitter.finagle.http.filter.ExceptionFilter

import re_svc_user_mgt.service.{FilterNonceCheck, Routes}

object Server extends App {
  val port = Config.config.port
  Config.log.info("Server starts on port %d", port)

  ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(port))
    .name(Config.LOG_NODE)
    .build(ExceptionFilter andThen FilterNonceCheck andThen Routes.routes)
}
