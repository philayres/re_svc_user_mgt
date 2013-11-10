package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialInvalidateSpec extends Specification {
  "exising user => OK" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.invalidate(requester, username, 1)
    )
    r2 must beNone
  }

  "nonexising user => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.invalidate(requester, "nonexising user", 1)
    )
    r2 must beSome
  }
}
