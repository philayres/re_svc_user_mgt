package re_svc_user_mgt_client

import java.util.UUID
import com.twitter.util.Await
import org.specs2.mutable._
import Bootstrap._

class ClientMachineCreateSpec extends Specification {
  "create new client for nonexisting client name" in {
    val clientName = UUID.randomUUID().toString
    val r = Await.result(
      ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r must beRight
  }

  "not create new client for existing client name" in {
    val clientName = UUID.randomUUID().toString
    val r1 = Await.result(
      ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r1 must beRight

    val r2 = Await.result(
      ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r2 must beLeft
  }

  "not create new client for wrong username" in {
    val clientName = UUID.randomUUID().toString
    val r = Await.result(
      ClientMachine.create(requester, "wrong username", AUTH_TYPE, PASSWORD, clientName, 1)
    )
    r must beLeft
  }
}
