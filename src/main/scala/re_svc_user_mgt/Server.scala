package re_svc_user_mgt

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}

object Server extends App {
  ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(Config.port))
    .name("re_svc_user_mgt")
    .build(Routes.routes)
}
