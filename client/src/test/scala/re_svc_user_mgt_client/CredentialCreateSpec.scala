package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialCreateSpec extends Specification {
  "enabled user, validated credential => OK" in {
    val username = UUID.randomUUID().toString
    val r = Await.result(
      Credential.create(requester, USERNAME, AUTH_TYPE, PASSWORD, username, 1, "p")
    )
    r must beNone
  }

  "enabled user, invalidated credential => NG" in {
    val username1 = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username1, 1, "p", false)
    )
    r1 must beRight

    val username2 = UUID.randomUUID().toString
    val r2 = Await.result(
      Credential.create(requester, username1, 1, "p", username2, 1, "p")
    )
    r2 must beSome
  }

  "disabled user, validated credential => NG" in {
    val username1 = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username1, 1, "p", true)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.disable(requester, userId)
    )
    r2 must beNone

    val username2 = UUID.randomUUID().toString
    val r3 = Await.result(
      Credential.create(requester, username1, 1, "p", username2, 1, "p")
    )
    r3 must beSome
  }

  "disabled user, invalidated credential => NG" in {
    val username1 = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username1, 1, "p", false)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.disable(requester, userId)
    )
    r2 must beNone

    val username2 = UUID.randomUUID().toString
    val r3 = Await.result(
      Credential.create(requester, username1, 1, "p", username2, 1, "p")
    )
    r3 must beSome
  }

  //----------------------------------------------------------------------------

  "duplicated enabled user, validated credential => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.create(requester, username, 1, "p", username, 1, "p")
    )
    r2 must beSome
  }

  "duplicated enabled user, invalidated credential => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", false)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.create(requester, username, 1, "p", username, 1, "p")
    )
    r2 must beSome
  }

  "duplicated disabled user, validated credential => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.disable(requester, userId)
    )
    r2 must beNone

    val r3 = Await.result(
      Credential.create(requester, username, 1, "p", username, 1, "p")
    )
    r3 must beSome
  }

  "duplicated disabled user, invalidated credential => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", false)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.disable(requester, userId)
    )
    r2 must beNone

    val r3 = Await.result(
      Credential.create(requester, username, 1, "p", username, 1, "p")
    )
    r3 must beSome
  }
}
