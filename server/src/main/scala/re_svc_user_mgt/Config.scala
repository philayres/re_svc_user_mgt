package re_svc_user_mgt

import java.io.FileInputStream
import java.util.Properties
import com.twitter.logging.{FileHandler, Level, Logger, LoggerFactory, Policy}

object Config {
  val LOG_NODE = "re_svc_user_mgt"

  val config = loadConfigFile()
  val log    = configLog()

  private def loadConfigFile(): Properties = {
    val is  = new FileInputStream("config/re_svc_user_mgt.properties")
    val ret = new Properties
    ret.load(is)
    is.close()
    ret
  }

  private def configLog(): Logger = {
    val level       = Level.DEBUG
    val fileHandler = FileHandler(
      filename   = "/var/log/re_services/re_svc_user_mgt.log",
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
}
