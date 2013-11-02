package re_svc_user_mgt.model

object User {
  def isAdmin(userId: Int) = true

  /**
   * Create user and one credential.
   *
   * @return Left(error) or Right(userId)
   */
  def create(username: String, authType: Int, password: String, validated: Boolean): Either[String, Int] = {
    Left("TODO")
  }

  def enable(userId: Int, enabled: Boolean) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("UPDATE users SET enabled = ? WHERE id = ?")
      ps.setInt(1, if (enabled) 1 else 0)
      ps.setInt(2, userId)

      ps.executeUpdate()
      ps.close()
    }
  }
}
