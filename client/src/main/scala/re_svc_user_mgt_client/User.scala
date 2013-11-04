package re_svc_user_mgt_client

import com.twitter.util.Future

import org.json4s._
import org.json4s.native.JsonMethods._

object User {
  /**
   * Create user and one credential.
   *
   * @return Future(Left(error)) or Future(Right(userId))
   */
  def create(
    requester: Requester,
    username: String, authType: Int, password: String, validated: Boolean
  ): Future[Either[String, Int]] = {
    val path = Seq("users")
    val form = Map(
      "username" -> username, "auth_type" -> authType, "password" -> password,
      "validated" -> validated
    )

    requester.post(path, form)
    .map { res =>
      res.statusCode match {
        case 200 =>
          val content = res.contentString
          val json    = parse(content)

          val list = for {
            JObject(child) <- json
            JField("user_id", JInt(userId))  <- child
          } yield Right(userId.toInt)
          list(0)

        case _ =>
          Left(ErrorMsg(res))
      }
    }
  }

  /** @return Future(Some(error)) or Future(None) */
  def enable(requester: Requester, userId: Int, enabled: Boolean): Future[Option[String]] = {
    val path = Seq("users", userId)
    val form = Map("enabled" -> enabled)

    requester.patch(path, form)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }
}
