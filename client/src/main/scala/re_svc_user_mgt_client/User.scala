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

  def enable (requester: Requester, userId: Int) = setEnabled(requester, userId, true)
  def disable(requester: Requester, userId: Int) = setEnabled(requester, userId, false)

  /** @return Future(Some(error)) or Future(None) */
  private def setEnabled(requester: Requester, userId: Int, enabled: Boolean): Future[Option[String]] = {
    val path =
      if (enabled)
        Seq("users", userId, "enable")
      else
        Seq("users", userId, "disable")

    requester.patch(path)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }
}
