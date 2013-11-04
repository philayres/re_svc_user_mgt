package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialAuthenticateSpec extends Specification {
  "enabled user, validated credential => true" in {
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

  "enabled user, invalidated credential => false" in {
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

  "disabled user, validated credential => false" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.enable(requester, userId, false)
    )
    r2 must beNone

    val r3 = Await.result(
      Credential.authenticate(requester, username, 1, "p")
    )
    r3 must beLeft
  }

  "disabled user, invalidated credential => false" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", false)
    )
    r1 must beRight

    val userId = r1.right.get

    val r2 = Await.result(
      User.enable(requester, userId, false)
    )
    r2 must beNone

    val r3 = Await.result(
      Credential.authenticate(requester, username, 1, "p")
    )
    r3 must beLeft
  }

  "nonexisting username => false" in {
    val r = Await.result(
      Credential.authenticate(requester, "nonexisting username", 1, "p")
    )
    r must beLeft
  }
}
