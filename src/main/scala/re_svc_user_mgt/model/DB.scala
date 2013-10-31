package re_svc_user_mgt

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.control.NonFatal
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}

import re_svc_user_mgt.Config.{config, log}

object DB {
  Class.forName("com.mysql.jdbc.Driver")

  private val pool: BoneCP = {
    val bc = new BoneCPConfig
    bc.setJdbcUrl(config.mysqlUrl)
    new BoneCP(bc)
  }

  def authenticate(username: String, authType: Int,  password: String): Boolean = {
    var con: Connection        = null
    var ps:  PreparedStatement = null
    var rs:  ResultSet         = null
    try {
      con = pool.getConnection()
      ps  = con.prepareStatement("SELECT * FROM credentials WHERE username = ? AND auth_type = ? LIMIT 1")
      ps.setString(1, username)
      ps.setInt   (2, authType)
      rs  = ps.executeQuery()

      if (rs.next()) {
        val id        = rs.getString("id")
        val password  = rs.getString("password")
        true
      } else {
        false
      }
    } catch {
      case NonFatal(e) =>
        log.error("authenticate", e)
        false
    } finally {
      if (rs  != null) rs.close()
      if (ps  != null) ps.close()
      if (con != null) con.close()
    }
  }
}
