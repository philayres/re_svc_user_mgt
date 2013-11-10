package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialDeleteSpec extends Specification {
  "exising user => OK" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.delete(requester, username, 1)
    )
    r2 must beNone

    val r3 = Await.result(
      Credential.authenticate(requester, username, 1, "p")
    )
    r3 must beLeft
  }

  "nonexising user => NG" in {
    val r = Await.result(
      Credential.delete(requester, "nonexising user", 1)
    )
    r must beSome
  }
}
