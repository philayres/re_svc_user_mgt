package re_svc_user_mgt.model

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.control.NonFatal
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}

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

  /** @return Left(reason) or Right(userId) */
  def authenticate(username: String, authType: Int,  password: String): Either[String, Long] = {
    withConnection { con =>
      val ps = con.prepareStatement("SELECT * FROM credentials WHERE username = ? AND auth_type = ? LIMIT 1")
      ps.setString(1, username)
      ps.setInt   (2, authType)
      val rs = ps.executeQuery()

      val ret = if (rs.next()) {
        val id        = rs.getString("id")
        val password  = rs.getString("password")
        true
      } else {
        false
      }

      rs.close()
      ps.close()
      Left("TODO")
    }
  }
}
