package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialUpdatePasswordSpec extends Specification {
  "using password" should {
    "correct password => OK" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", true)
      )
      r1 must beRight

      val r2 = Await.result(
        Credential.updatePassword(requester, username, 1, "p2", "p")
      )
      r2 must beNone

      val r3 = Await.result(
        Credential.updatePassword(requester, username, 1, "p3", "p2")
      )
      r3 must beNone
    }

    "wrong password => NG" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", true)
      )
      r1 must beRight

      val r2 = Await.result(
        Credential.updatePassword(requester, username, 1, "p2", "xxx")
      )
      r2 must beSome

      val r3 = Await.result(
        Credential.updatePassword(requester, username, 1, "yyy", "p2")
      )
      r3 must beSome
    }

    "nonexisting user => OK" in {
      val r2 = Await.result(
        Credential.updatePassword(requester, "nonexisting user", 1, "p2", "p")
      )
      r2 must beSome
    }
  }

  "using force_new" should {
    "any new password => OK" in {
      val username = UUID.randomUUID().toString
      val r1 = Await.result(
        User.create(requester, username, 1, "p", true)
      )
      r1 must beRight

      val r2 = Await.result(
        Credential.updatePassword(requester, username, 1, "p2")
      )
      r2 must beNone

      val r3 = Await.result(
        Credential.authenticate(requester, username, 1, "p2")
      )
      r3 must beRight
    }

    "nonexisting user => NG" in {
      val r2 = Await.result(
        Credential.updatePassword(requester, "nonexisting user", 1, "p2")
      )
      r2 must beSome
    }
  }
}
