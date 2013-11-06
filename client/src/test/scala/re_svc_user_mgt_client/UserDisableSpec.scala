package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class UserDisableSpec extends Specification {
  disable(true)
  disable(false)

  "not disable nonexisting user" in {
    val r2 = Await.result(
      User.disable(requester, -1)
    )
    r2 must beSome
  }

  private def disable(validated: Boolean) {
    s"disable existing user (previously validated = $validated)" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", validated)
      )
      r1 must beRight

      val userId = r1.right.get

      val r2 = Await.result(
        User.disable(requester, userId)
      )
      r2 must beNone

      val r3 = Await.result(
        Credential.authenticate(requester, username, 1, "p")
      )
      r3 must beLeft
    }
  }
}
