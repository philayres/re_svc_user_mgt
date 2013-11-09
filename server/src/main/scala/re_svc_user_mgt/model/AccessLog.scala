package re_svc_user_mgt.model

import java.sql.Types

object AccessLog {
  def logNonAuthRequest(
    clientId: Int, requestType: Int, responseStatus: Int,
    credentialId: Option[Int], userId: Option[Int])
  {
    DB.withConnection { con =>
      val sharedSecret = Secure.makeSecret()

      val ps = con.prepareStatement(
        "INSERT INTO accesses(client_id, request_type, response_status, credential_id, user_id) " +
        "VALUES (?, ?, ?, ?, ?)"
      )
      ps.setInt(1, clientId)
      ps.setInt(2, requestType)
      ps.setInt(3, responseStatus)

      credentialId match {
        case Some(id) => ps.setInt(4, id)
        case None     => ps.setNull(4, Types.INTEGER)
      }

      userId match {
        case Some(id) => ps.setInt(5, id)
        case None     => ps.setNull(5, Types.INTEGER)
      }

      ps.executeUpdate()
      ps.close()
    }
  }

  def logAuthRequest(
    clientId: Int, requestType: Int, responseStatus: Int,
    username: Option[String], authType: Option[Int],
    credentialId: Option[Int])
  {
    DB.withConnection { con =>
      val sharedSecret = Secure.makeSecret()

      val ps = con.prepareStatement(
        "INSERT INTO auth_accesses(client_id, request_type, response_status, username, auth_type, credential_id) " +
        "VALUES (?, ?, ?, ?, ?, ?)"
      )
      ps.setInt(1, clientId)
      ps.setInt(2, requestType)
      ps.setInt(3, responseStatus)

      credentialId match {
        case Some(id) =>
          ps.setNull(4, Types.VARCHAR)
          ps.setNull(5, Types.INTEGER)
          ps.setInt(6, id)

        case None =>
          username match {
            case Some(u) => ps.setString(4, u)
            case None    => ps.setNull(4, Types.VARCHAR)
          }
          authType match {
            case Some(a) => ps.setInt(5, a)
            case None    => ps.setNull(5, Types.INTEGER)
          }
          ps.setNull(6, Types.INTEGER)
      }

      ps.executeUpdate()
      ps.close()
    }
  }
}
