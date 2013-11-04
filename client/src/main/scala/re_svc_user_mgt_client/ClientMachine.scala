package re_svc_user_mgt_client

import com.twitter.util.Future

import org.json4s._
import org.json4s.native.JsonMethods._

object ClientMachine {
  /** @return Left(error) or Right((clientId, sharedSecret)) */
  def create(
    requester: Requester,
    username: String, authType: Int, password: String,
    clientName: String, clientType: Int
  ): Future[Either[String, (Int, String)]] = {
    val path = Seq("client_machines")
    val form = Map(
      "username" -> username, "auth_type" -> authType, "password" -> password,
      "client_name" -> clientName, "client_type" -> clientType
    )

    requester.post(path, form)
    .map { res =>
      res.statusCode match {
        case 200 =>
          val content = res.contentString
          val json    = parse(content)

          val list = for {
            JObject(child) <- json
            JField("client_id", JInt(clientId))  <- child
            JField("shared_secret", JString(sharedSecret))  <- child
          } yield Right((clientId.toInt, sharedSecret))
          list(0)

        case _ =>
          Left(ErrorMsg(res))
      }
    }
  }

  def delete(
    requester: Requester,
    username: String, authType: Int, password: String,
    clientName: String
  ): Future[Option[String]] = {
    val path = Seq("client_machines", clientName)
    val form = Map("username" -> username, "auth_type" -> authType, "password" -> password)

    requester.delete(path, form)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }
}
