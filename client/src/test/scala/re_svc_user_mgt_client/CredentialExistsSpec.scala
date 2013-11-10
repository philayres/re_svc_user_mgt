package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class CredentialExistsSpec extends Specification {
  "enabled user, validated credential => OK" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", true)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.exists(requester, username, 1)
    )
    r2 must beRight
    r2.right.get must beSome
  }

  "enabled user, invalidated credential => NG" in {
    val username = UUID.randomUUID().toString
    val r1 = Await.result(
      User.create(requester, username, 1, "p", false)
    )
    r1 must beRight

    val r2 = Await.result(
      Credential.exists(requester, username, 1)
    )
    r2 must beLeft
  }

  "disabled user, validated credential => NG" in {
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
      Credential.exists(requester, username, 1)
    )
    r3 must beLeft
  }

  "disabled user, invalidated credential => NG" in {
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
      Credential.exists(requester, username, 1)
    )
    r3 must beLeft
  }

  "nonexisting username => NG" in {
    val r = Await.result(
      Credential.exists(requester, "nonexisting username", 1)
    )
    r must beLeft
  }
}
