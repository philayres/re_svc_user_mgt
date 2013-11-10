package re_svc_user_mgt.model

import java.sql.{Connection, Statement}
import scala.util.Try

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
   *  @return Some(clientId) or None if duplicate username + authType pair
   */
  def create(username: String, authType: Int, password: String, validated: Boolean): Option[Int] = {
    // Transaction
    DB.withConnection { con =>
      con.setAutoCommit(false)

      val userId = create(con)

      if (Credential.create(con, userId, username, authType, password, validated)) {
        con.commit()
        Some(userId)
      } else {
        con.rollback()
        None
      }
    }
  }

  def setEnabled(userId: Int, enabled: Boolean): Boolean = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("UPDATE users SET enabled = ? WHERE id = ?")
      ps.setInt(1, if (enabled) 1 else 0)
      ps.setInt(2, userId)

      val deletedRows = ps.executeUpdate()
      val ret         = deletedRows > 0
      ps.close()
      ret
    }
  }

  //----------------------------------------------------------------------------

  /** @return userId */
  private def create(con: Connection): Int = {
    val st = con.createStatement()
    st.executeUpdate("INSERT INTO users(created_at, enabled) VALUES (NOW(), 1)", Statement.RETURN_GENERATED_KEYS)

    val rs = st.getGeneratedKeys()
    rs.next()
    val ret = rs.getInt(1)

    rs.close()
    st.close()
    ret
  }
}
