package re_svc_user_mgt_client

object Bootstrap {
  val HTTPS = false
  val HOST  = "localhost"
  val PORT  = 8000

  val CLIENT_ID     = 1
  val SHARED_SECRET = "test123!"

  val USERNAME  = "opadmin"
  val AUTH_TYPE = 999
  val PASSWORD  = "test123!"

  val requester = new Requester(CLIENT_ID, SHARED_SECRET, HTTPS, HOST, PORT)
}
