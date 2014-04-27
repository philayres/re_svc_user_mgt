package re_svc_user_mgt

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}

import re_svc_user_mgt.model.Nonce
import re_svc_user_mgt.service.{
  FilterException, FilterReadParamsFromContentBody, FilterNonceCheck, FilterAccessLog,
  Routes
}

object Server extends App {
  val port = Config.config.getProperty("port").toInt
  val hostip = Config.config.getProperty("hostip")
  Config.log.info("Server starts on hostip %s port %d", hostip, port)

  Nonce.schedulePeriodicallyDeleteExpiredNonces()

  ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(hostip, port))
    .name(Config.LOG_NODE)
    .build(
      FilterException andThen FilterReadParamsFromContentBody andThen
      FilterNonceCheck andThen FilterAccessLog andThen
      Routes.routes
    )
}
