package re_svc_user_mgt.model

import java.util.UUID
import org.apache.commons.codec.digest.DigestUtils

object Secure {
  def makeSecret(): String = {
    val secret = UUID.randomUUID().toString + System.currentTimeMillis()
    DigestUtils.sha256Hex(secret)
  }

  def hashPassword(password: String, salt: String): String =
    DigestUtils.sha256Hex(password + salt)

  def checkPassword(password: String, salt: String, hashedPassword: String): Boolean =
    hashPassword(password, salt) == hashedPassword
}
