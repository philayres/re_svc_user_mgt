package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class ClientMachineDeleteSpec extends Specification {
  "delete existing client" in {
    val clientName = UUID.randomUUID().toString
    val r1 = Await.result(
      ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r1 must beRight

    val r2 = Await.result(
      ClientMachine.delete(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName)
    )
    r2 must beNone
  }

  "not delete nonexisting client" in {
    val nonexistingClientName = UUID.randomUUID().toString
    val r = Await.result(
      ClientMachine.delete(requester, USERNAME, AUTH_TYPE, PASSWORD, nonexistingClientName)
    )
    r must beSome
  }

  "not delete existing client for wrong username" in {
    val clientName = UUID.randomUUID().toString
    val r1 = Await.result(
      ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r1 must beRight

    val r2 = Await.result(
      ClientMachine.delete(requester, "wrong username", AUTH_TYPE, PASSWORD, clientName)
    )
    r2 must beSome
  }
}
