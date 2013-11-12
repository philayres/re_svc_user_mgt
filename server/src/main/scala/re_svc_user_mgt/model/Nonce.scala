package re_svc_user_mgt.model

import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.Duration

import org.apache.commons.codec.digest.DigestUtils

object Nonce {
  private val NONCE_TTL = 1 * 60 * 1000  // 1 minute

  def schedulePeriodicallyDeleteExpiredNonces() {
    val period = Duration.fromMilliseconds(10 * NONCE_TTL)
    DefaultTimer.twitter.schedule(period) {
      deleteExpiredNonces()
    }
  }

  /** @return Left(error) or Right(clientId) */
  def check(
    method: String, path: String, content: String,
    nonce: String, clientName: String, timestamp: Long
  ): Either[String, Int] = {
    // Use abs because time on different systems can be slightly different
    val now = System.currentTimeMillis()
    if (timestamp < 0 || Math.abs(now - timestamp) > NONCE_TTL) {
      Left("Nonce expired")
    } else {
      ClientMachine.getSharedSecret(clientName) match {
        case None =>
          Left("Client not found")

        case Some((clientId, sharedSecret)) =>
          check(method, path, content, nonce, clientName, timestamp, now, sharedSecret) match {
            case Some(error) => Left(error)
            case None        => Right(clientId)
          }
      }
    }
  }

  //----------------------------------------------------------------------------

  /** @return Some(error) or None */
  private def check(
    method: String, path: String, content: String,
    nonce: String, clientName: String, timestamp: Long, now: Long, sharedSecret: String
  ): Option[String] = {
    val recreatedNonce = DigestUtils.sha256Hex(method + path + content + clientName + sharedSecret + timestamp)
    if (recreatedNonce != nonce) {
      Some("Wrong nonce")
    } else if (isUsed(nonce, now)) {
      Some("Nonce has been used")
    } else {
      save(nonce, timestamp)
      None
    }
  }

  private def isUsed(nonce: String, now: Long): Boolean = {
    DB.withConnection { con =>
      val ps = con.prepareStatement("SELECT created_at FROM nonces WHERE nonce = ?")
      ps.setString(1, nonce)

      val rs  = ps.executeQuery()
      if (rs.next()) {
        val createdAt = rs.getLong("created_at")

        // Use abs because time on different systems can be slightly different
        if (Math.abs(now - createdAt) > NONCE_TTL) {
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

  private def save(nonce: String, timestamp: Long) {
    DB.withConnection { con =>
      val ps = con.prepareStatement("INSERT INTO nonces(nonce, created_at) VALUES (?, ?)")
      ps.setString(1, nonce)
      ps.setLong  (2, timestamp)
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

  private def deleteExpiredNonces() {
    DB.withConnection { con =>
      val now       = System.currentTimeMillis()
      val threshold = now - NONCE_TTL
      val ps = con.prepareStatement("DELETE FROM nonces WHERE created_at < ?")
      ps.setLong(1, threshold)
      ps.executeUpdate()
      ps.close()
    }
  }
}
