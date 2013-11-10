package re_svc_user_mgt_client

import Bootstrap._

object ClientSample {
  def main(args: Array[String]) {
    val requester = new Requester(CLIENT_NAME, SHARED_SECRET, HTTPS, HOST, PORT)

    val clientName = "c1"
    val clientType = 1
    ClientMachine.create(requester, USERNAME, AUTH_TYPE, PASSWORD, clientName, clientType)
    .onSuccess {
      case Left(error) =>
        println(s"[ClientMachine.create] error: $error")

      case Right((clientId, sharedSecret)) =>
        println(s"[ClientMachine.create] clientId: $clientId, sharedSecret: $sharedSecret")
    }
  }
}
