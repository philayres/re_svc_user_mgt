package re_svc_user_mgt_client

object Client {
  private val HTTPS = false
  private val HOST  = "localhost"
  private val PORT  = 8000

  private val CLIENT_NAME   = "opclient1"
  private val SHARED_SECRET = "test123!"

  private val USERNAME  = "opadmin"
  private val AUTH_TYPE = 999
  private val PASSWORD  = "test123!"

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
