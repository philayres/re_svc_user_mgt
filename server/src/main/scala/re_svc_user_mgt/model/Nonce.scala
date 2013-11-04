package re_svc_user_mgt.model

import org.apache.commons.codec.digest.DigestUtils

object Nonce {
  private val NONCE_TTL = 1 * 60  // 1 minute

  /** @return Some(error) or None */
  def check(
    method: String, path: String, content: String,
    nonce: String, clientName: String, timestamp: Int
  ): Option[String] = {
    // Use abs because time on different systems can be slightly different
    val nowSecs = (System.currentTimeMillis() / 1000).toInt
    if (timestamp < 0 || Math.abs(nowSecs - timestamp) > NONCE_TTL) {
      Some("Nonce expired")
    } else {
      ClientMachine.getSharedSecret(clientName) match {
        case None =>
          Some("Client not found")

        case Some(sharedSecret) =>
          check(method, path, content, nonce, clientName, timestamp, nowSecs, sharedSecret)
      }
    }
  }

  private def deleteExpiredNonces() {

  }

  //----------------------------------------------------------------------------

  /** @return Some(error) or None */
  private def check(
    method: String, path: String, content: String,
    nonce: String, clientName: String, timestamp: Int, nowSecs: Int, sharedSecret: String
  ): Option[String] = {
    val recreatedNonce = DigestUtils.sha256Hex(method + path + content + clientName + sharedSecret + timestamp)
    if (recreatedNonce != nonce) {
      Some("Wrong nonce")
    } else if (isUsed(nonce, nowSecs)) {
      Some("Nonce has been used")
    } else {
      save(nonce, timestamp)
      None
    }
  }

  private def isUsed(nonce: String, nowSecs: Int): Boolean = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("SELECT created_at FROM nonces WHERE nonce = ?")
      ps.setString(1, nonce)

      val rs  = ps.executeQuery()
      if (rs.next()) {
        val secs = rs.getInt("created_at")

        // Use abs because time on different systems can be slightly different
        if (Math.abs(nowSecs - secs) > NONCE_TTL) {
          delete(nonce)
          false
        } else {
          true
        }
      } else {
        rs.close()
        ps.close()
        false
      }
    }
  }

  private def save(nonce: String, timestamp: Int) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("INSERT INTO nonces(nonce, created_at) VALUES (?, ?)")
      ps.setString(1, nonce)
      ps.setInt   (2, timestamp)
      ps.executeUpdate()
      ps.close()
    }
  }

  private def delete(nonce: String) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("DELETE FROM nonces WHERE nonce = ?")
      ps.setString(1, nonce)
      ps.executeUpdate()
      ps.close()
    }
  }
}
