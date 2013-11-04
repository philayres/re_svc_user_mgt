package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class UserEnableSpec extends Specification {
  enable(true)
  enable(false)

  "not enable nonexisting user" in {
    val r2 = Await.result(
      User.enable(requester, -1, true)
    )
    r2 must beSome
  }

  private def enable(validated: Boolean) {
    s"enable existing user (previously validated = $validated)" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", validated)
      )
      r1 must beRight

      val userId = r1.right.get

      val r2 = Await.result(
        User.enable(requester, userId, true)
      )
      r2 must beNone

      val r3 = Await.result(
        Credential.authenticate(requester, username, 1, "p")
      )
      if (validated) r3 must beRight else r3 must beLeft
    }
  }
}
