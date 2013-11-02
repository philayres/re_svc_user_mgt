package re_svc_user_mgt.model

object User {
  def isAdmin(userId: Int) = true

  /** @return Left(error) or Right(userId) */
  def create(username: String, authType: Int, password: String, validated: Boolean): Either[String, Int] = {
    Left("TODO")
  }

  def enable(userId: Int, enabled: Boolean) {

  }
}
