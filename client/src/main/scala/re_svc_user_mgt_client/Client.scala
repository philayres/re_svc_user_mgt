package re_svc_user_mgt_client

object Client {
  def main(args: Array[String]) {
    val requester = new Requester(1, "test123!", false, "localhost", 8000)

    ClientMachine.create(requester, "opadmin", 999, "test123!", "c1", 1)
    .onSuccess {
      case Left(error) =>
        println(s"[ClientMachine.create] error: $error")

      case Right((clientId, sharedSecret)) =>
        println(s"[ClientMachine.create] clientId: $clientId, sharedSecret: $sharedSecret")
    }
  }
}
