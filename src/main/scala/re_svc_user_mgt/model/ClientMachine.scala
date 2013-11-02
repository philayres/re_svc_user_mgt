package re_svc_user_mgt.model

import java.sql.Statement
import java.util.UUID
import scala.util.Try
import org.apache.commons.codec.digest.DigestUtils

object ClientMachine {
  /** @return Left(error) or Right((clientId, sharedSecret)) */
  def create(clientName: String, clientType: Int): Either[String, (Int, String)] = {
    DB.withConnection { con =>
      val sharedSecret = makeSecret()

      val ps = con.prepareStatement(
        "INSERT INTO clients(created_at, name, type, shared_secret) VALUES (NOW(), ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
      )
      ps.setString(1, clientName)
      ps.setInt   (2, clientType)
      ps.setString(3, sharedSecret)

      val t = Try(ps.executeUpdate())
      if (t.isFailure) {
        ps.close()
        Left("Duplicate client name")
      } else {
        val rs = ps.getGeneratedKeys()
        rs.next()
        val ret = Right((rs.getInt(1), sharedSecret))

        rs.close()
        ps.close()
        ret
      }
    }
  }

  /** @return Some(error) or None */
  def delete(clientName: String): Option[String] = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("DELETE FROM clients WHERE name = ?")
      ps.setString(1, clientName)

      val deletedRows = ps.executeUpdate()
      val ret         = if (deletedRows < 1) Some("Not found") else None

      ps.close()
      ret
    }
  }

  private def makeSecret(): String = {
    val secret = UUID.randomUUID().toString + System.currentTimeMillis()
    DigestUtils.sha256Hex(secret)
  }
}
