package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
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
}
