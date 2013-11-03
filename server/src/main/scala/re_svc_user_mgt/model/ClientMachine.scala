package re_svc_user_mgt.model

import java.sql.Statement
import scala.util.Try

object ClientMachine {
  /** @return Some(sharedSecret) or None */
  def getSharedSecret(clientId: Int): Option[String] = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("SELECT shared_secret FROM clients WHERE id = ?")
      ps.setInt(1, clientId)

      val rs  = ps.executeQuery()
      val ret = if (rs.next()) Some(rs.getString("shared_secret")) else None

      rs.close()
      ps.close()
      ret
    }
  }

  /** @return Left(error) or Right((clientId, sharedSecret)) */
  def create(clientName: String, clientType: Int): Either[String, (Int, String)] = {
    DB.withConnection { con =>
      val sharedSecret = Secure.makeSecret()

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
}