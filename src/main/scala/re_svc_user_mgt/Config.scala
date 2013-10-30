package re_svc_user_mgt

import com.twitter.logging.{FileHandler, Level, Logger, LoggerFactory, Policy}
import com.typesafe.config.ConfigFactory

case class MySqlConfig(host: String, port: Int, username: String, password: String)
case class Config(port: Int, mysql: MySqlConfig)

object Config {
  val LOG_NODE = "re_svc_user_mgt"

  val config = loadConfig()

  val log = {
    val level       = Level.DEBUG
    val fileHandler = FileHandler(
      filename   = "log/re_svc_user_mgt.log",
      rollPolicy = Policy.Daily
    )

    // Override node "" to force all log to be output to log/re_svc_user_mgt.log
    LoggerFactory(
      node     = "",
      level    = Some(level),
      handlers = List(fileHandler)
    )()

    // Set fileHandler once more here will cause our log to be output twice
    LoggerFactory(
      node  = LOG_NODE,
      level = Some(level)
    )()
  }

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
