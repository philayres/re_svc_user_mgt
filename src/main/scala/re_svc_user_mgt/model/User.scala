package re_svc_user_mgt.model

object User {
  def isAdmin(userId: Int) = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("SELECT user_id FROM admins WHERE user_id = ?")
      ps.setInt(1, userId)

      val rs  = ps.executeQuery()
      val ret = rs.next()

      rs.close()
      ps.close()
      ret
    }
  }

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
