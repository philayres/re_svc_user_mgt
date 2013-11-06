package re_svc_user_mgt.model

object Access {
  val CLIENT_MACHINE_CREATE = 0
  val CLIENT_MACHINE_DELETE = 1

  val USER_CREATE         = 20
  val USER_ENABLE_DISABLE = 21

  val CREDENTIAL_EXISTS       = 40
  val CREDENTIAL_AUTHENTICATE = 41

  val CREDENTIAL_CREATE              = 42
  val CREDENTIAL_VALIDATE_INVALIDATE = 43
  val CREDENTIAL_UPDATE_PASSWORD     = 44
  val CREDENTIAL_DELETE              = 45
}
