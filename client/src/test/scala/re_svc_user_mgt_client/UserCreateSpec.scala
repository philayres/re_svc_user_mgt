package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class UserCreateSpec extends Specification {
  "create new user for nonexisting username (validated = true)" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.authenticate(requester, username, 1, "p")
    )
    r2 must beRight
  }

  "create new user for nonexisting username (validated = false)" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", false)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.authenticate(requester, username, 1, "p")
    )
    r2 must beLeft
  }

  notCreate(true,  true)
  notCreate(false, false)
  notCreate(true,  false)
  notCreate(false, true)

  private def notCreate(validated1: Boolean, validated2: Boolean) {
    s"not create new user for existing username (validated = $validated1, $validated2)" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", validated1)
      )
      r1 must beRight

      val r2 = Await.result(
        User.create(requester, username, 1, "p", validated2)
      )
      r2 must beLeft
    }
  }
}
