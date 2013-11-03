package re_svc_user_mgt.model

import java.sql.Connection
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import re_svc_user_mgt.Config.config

object DB {
  private val pool: BoneCP = {
    Class.forName("com.mysql.jdbc.Driver")
    val bc = new BoneCPConfig
    bc.setJdbcUrl(config.mysqlUrl)
    new BoneCP(bc)
  }

  def withConnection[T](fun: Connection => T): T = {
    var con: Connection = null
    try {
      con = pool.getConnection()
      fun(con)
    } finally {
      if (con != null) con.close()
    }
  }
}
