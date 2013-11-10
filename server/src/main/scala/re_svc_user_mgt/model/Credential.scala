package re_svc_user_mgt.model

import java.sql.Connection
import scala.util.{Try, Success, Failure}
import org.apache.commons.codec.digest.DigestUtils

object Credential {
  /** @return Left(error) or Right((credentialId, userId)) */
  def exists(username: String, authType: Int): Either[String, (Int, Int)] = {
    authenticateOrCheckExistence(username, authType, None)
  }

  /** @return Left(error) or Right((credentialId, userId)) */
  def authenticate(username: String, authType: Int, password: String): Either[String, (Int, Int)] = {
    authenticateOrCheckExistence(username, authType, Some(password))
  }

  /** @return false if duplicate username + authType pair */
  def create(userId: Int, username: String, authType: Int, password: String, validated: Boolean): Boolean = {
    DB.withConnection { con =>
      create(con, userId, username, authType, password, validated)
    }
  }

  /** @return false if duplicate username + authType pair */
  def create(con: Connection, userId: Int, username: String, authType: Int, password: String, validated: Boolean): Boolean = {
    val salt           = Secure.makeSecret()
    val hashedPassword = Secure.hashPassword(password, salt)

    val ps = con.prepareStatement("INSERT INTO credentials(created_at, user_id, username, auth_type, password, salt, validated) VALUES (NOW(), ?, ?, ?, ?, ?, ?)")
    ps.setInt   (1, userId)
    ps.setString(2, username)
    ps.setInt   (3, authType)
    ps.setString(4, hashedPassword)
    ps.setString(5, salt)
    ps.setInt   (6, if (validated) 1 else 0)

    val ret = Try(ps.executeUpdate()).isSuccess
    ps.close()
    ret
  }

  /** @return false if username + authType does not exist */
  def setValidated(username: String, authType: Int, validated: Boolean): Boolean = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("UPDATE credentials SET validated = ? WHERE username = ? AND auth_type = ?")
      ps.setInt   (1, if (validated) 1 else 0)
      ps.setString(2, username)
      ps.setInt   (3, authType)

      val updatedRows = ps.executeUpdate()
      val ret         = updatedRows > 0
      ps.close()
      ret
    }
  }

  /** @return false if username + authType does not exist */
  def updatePassword(username: String, authType: Int, newPassword: String): Boolean = {
    val salt           = Secure.makeSecret()
    val hashedPassword = Secure.hashPassword(newPassword, salt)

    DB.withConnection { con =>
      val ps = con.prepareStatement("UPDATE credentials SET password = ?, salt = ? WHERE username = ? AND auth_type = ?")
      ps.setString(1, hashedPassword)
      ps.setString(2, salt)
      ps.setString(3, username)
      ps.setInt   (4, authType)

      val updatedRows = ps.executeUpdate()
      val ret         = updatedRows > 0
      ps.close()
      ret
    }
  }

  def delete(username: String, authType: Int): Boolean = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("DELETE FROM credentials WHERE username = ? AND auth_type = ?")
      ps.setString(1, username)
      ps.setInt   (2, authType)

      val deletedRows = ps.executeUpdate()
      val ret         = deletedRows > 0
      ps.close()
      ret
    }
  }

  //----------------------------------------------------------------------------

  /**
   * @param passwordo None: do not check password
   *
   * @return Left(error) or Right((credentialId, userId))
   */
  def authenticateOrCheckExistence(username: String, authType: Int, passwordo: Option[String]): Either[String, (Int, Int)] = {
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
          val credentialId = rs.getInt("id")
          val userId       = rs.getInt("user_id")
          passwordo match {
            case Some(password) =>
              val hashedPassword = rs.getString("password")
              val salt           = rs.getString("salt")
              if (Secure.checkPassword(password, salt, hashedPassword)) {
                getUserEnabled(con, userId) match {
                  case None =>
                    Left("User not found")

                  case Some(enabled) =>
                    if (enabled) Right((credentialId, userId)) else Left("User disabled")
                }
              } else {
                Left("Wrong password")
              }

            case None =>
              getUserEnabled(con, userId) match {
                case None =>
                  Left("User not found")

                case Some(enabled) =>
                  if (enabled) Right((credentialId, userId)) else Left("User disabled")
              }
          }
        }
      } else {
        Left("Incorrect username or auth type")
      }

      rs.close()
      ps.close()
      ret
    }
  }

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
