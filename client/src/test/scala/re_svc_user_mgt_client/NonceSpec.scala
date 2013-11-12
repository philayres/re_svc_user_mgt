package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http.HttpMethod
import org.specs2.mutable._
import Bootstrap._

class NonceSpec extends Specification {
  "Wrong client name => NG" in {
    val clientName = UUID.randomUUID().toString
    val requester  = new Requester(clientName, SHARED_SECRET, HTTPS, HOST, PORT)

    val r = Await.result(
      Credential.exists(requester, USERNAME, AUTH_TYPE)
    )
    r must beLeft
  }

  "Wrong secret => NG" in {
    val secret    = UUID.randomUUID().toString
    val requester = new Requester(CLIENT_NAME, secret, HTTPS, HOST, PORT)

    val r = Await.result(
      Credential.exists(requester, USERNAME, AUTH_TYPE)
    )
    r must beLeft
  }

  "Duplicate request => 403 Forbidden" in {
    val clientName = UUID.randomUUID().toString

    val path = Seq("client_machines")
    val form = Map(
      "username" -> USERNAME, "auth_type" -> AUTH_TYPE, "password" -> PASSWORD,
      "client_name" -> clientName, "client_type" -> 1
    )
    val req = requester.mkRequest(HttpMethod.POST, path, form)

    val res1 = Await.result(
      requester.sendRequest(req)
    )
    res1.getStatusCode must_== 200

    val res2 = Await.result(
      requester.sendRequest(req)
    )
    res2.getStatusCode must_== 403

    val res3 = Await.result(
      requester.sendRequest(req)
    )
    res3.getStatusCode must_== 403
  }
}
