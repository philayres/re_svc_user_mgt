package re_svc_user_mgt

import com.typesafe.config.ConfigFactory

case class MySqlConfig(host: String, port: Int, username: String, password: String)
case class Config(port: Int, mysql: MySqlConfig)

object Config {
  val config = loadConfig()

  private def loadConfig(): Config = {
    val config        = ConfigFactory.load("re_svc_user_mgt.conf").getConfig("re_svc_user_mgt")
    val port          = config.getInt("port")
    val mysqlHost     = config.getString("mysql.host")
    val mysqlPort     = config.getInt("mysql.port")
    val mysqlUsername = config.getString("mysql.username")
    val mysqlPassword = config.getString("mysql.password")
    Config(port, MySqlConfig(mysqlHost, mysqlPort, mysqlUsername, mysqlPassword))
  }
}
