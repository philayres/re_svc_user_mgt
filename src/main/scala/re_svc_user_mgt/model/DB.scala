package re_svc_user_mgt.model

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.control.NonFatal

import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import org.apache.commons.codec.digest.DigestUtils

import re_svc_user_mgt.Config.{config, log}

object DB {
  private val pool: BoneCP = {
    Class.forName("com.mysql.jdbc.Driver")
    val bc = new BoneCPConfig
    bc.setJdbcUrl(config.mysqlUrl)
    new BoneCP(bc)
  }

  private def withConnection[T](fun: Connection => T): T = {
    var con: Connection = null
    try {
      con = pool.getConnection()
      fun(con)
    } finally {
      if (con != null) con.close()
    }
  }

  /** @return Left(reason) or Right(sharedSecret) */
  def addClientMachine(username: String, authType: Int, clientName: String, clientType: Int): Either[String, String] = {
      null
  }

  /** @return Left(reason) or Right(userId) */
  def authenticate(username: String, authType: Int, password: String): Either[String, Long] = {
    withConnection { con =>
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
            val userId = rs.getLong("user_id")
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

  //----------------------------------------------------------------------------

  private def checkPassword(password: String, salt: String, hashedPassword: String): Boolean =
    DigestUtils.sha256Hex(password + salt) == hashedPassword

  /** @return None if user not found */
  private def getUserEnabled(con: Connection, userId: Long): Option[Boolean] = {
    val ps = con.prepareStatement("SELECT enabled FROM users WHERE id = ?")
    ps.setLong(1, userId)

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
