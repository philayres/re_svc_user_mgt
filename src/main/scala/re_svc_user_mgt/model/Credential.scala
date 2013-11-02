package re_svc_user_mgt.model

import java.sql.Connection
import org.apache.commons.codec.digest.DigestUtils

object Credential {
  /** @return Left(error) or Right(userId) */
  def authenticate(username: String, authType: Int, password: String): Either[String, Int] = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("SELECT * FROM credentials WHERE username = ? AND auth_type = ? LIMIT 1")
      ps.setString(1, username)
      ps.setInt   (2, authType)

      val rs  = ps.executeQuery()
      val ret = if (rs.next()) {
        val validated = rs.getInt("validated")
        if (validated == 0) {
          Left("Credential not validated")
        } else {
          val hashedPassword = rs.getString("password")
          val salt           = rs.getString("salt")
          if (checkPassword(password, salt, hashedPassword)) {
            val userId = rs.getInt("user_id")
            getUserEnabled(con, userId) match {
              case None =>
                Left("User not found")

              case Some(enabled) =>
                if (enabled) Right(userId) else Left("User disabled")
            }
          } else {
            Left("Wrong password")
          }
        }
      } else {
        Left("Credential not found")
      }

      rs.close()
      ps.close()
      ret
    }
  }

  /** @return Some(error) or None */
  def create(userId: Int, username: String, authType: Int, password: String, validated: Boolean): Option[String] = {
    Some("TODO")
  }

  def delete(username: String, authType: Int) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("DELETE FROM credentials WHERE username = ? AND auth_type = ?")
      ps.setString(1, username)
      ps.setInt   (2, authType)

      ps.executeUpdate()
      ps.close()
    }
  }

  /** @return Some(userId) or None if not found */
  def exists(username: String, authType: Int): Option[Int] = {
    None
  }

  def updatePassword(username: String, authType: Int, newPassword: String) {

  }

  def validate(username: String, authType: Int, validated: Boolean) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("UPDATE credentials SET validated = ? WHERE username = ? AND auth_type = ?")
      ps.setInt   (1, if (validated) 1 else 0)
      ps.setString(2, username)
      ps.setInt   (3, authType)

      ps.executeUpdate()
      ps.close()
    }
  }

  //----------------------------------------------------------------------------

  private def checkPassword(password: String, salt: String, hashedPassword: String): Boolean =
    DigestUtils.sha256Hex(password + salt) == hashedPassword

  /** @return None if user not found */
  private def getUserEnabled(con: Connection, userId: Int): Option[Boolean] = {
    val ps = con.prepareStatement("SELECT enabled FROM users WHERE id = ?")
    ps.setInt(1, userId)

    val rs  = ps.executeQuery()
    val ret = if (rs.next()) {
      val enabled = rs.getInt("enabled")
      Some(enabled != 0)
    } else {
      None
    }

    rs.close()
    ps.close()
    ret
  }
}
