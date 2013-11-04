package re_svc_user_mgt_client

import com.twitter.util.Future

import org.json4s._
import org.json4s.native.JsonMethods._

object Credential {
  /** @return Future(Right(Some(userId))), Future(Right(None)), or Future(Left(error)) */
  def exists(requester: Requester, username: String, authType: Int): Future[Either[String, Option[Int]]] = {
    val path = Seq("credentials", username, authType)

    requester.get(path)
    .map { res =>
      res.statusCode match {
        case 200 =>
          val content = res.contentString
          val json    = parse(content)

          val list = for {
            JObject(child) <- json
            JField("user_id", JInt(userId))  <- child
          } yield Right(Some(userId.toInt))
          list(0)

        case 404 =>
          Right(None)

        case _ =>
          Left(ErrorMsg(res))
      }
    }
  }

  /** @return Future(Left(error)) or Future(Right(userId)) */
  def authenticate(
    requester: Requester,
    username: String, authType: Int, password: String
  ): Future[Either[String, Int]] = {
    val path = Seq("credentials", "authenticate")
    val form = Map("username" -> username, "auth_type" -> authType, "password" -> password)

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
  def create(
    requester: Requester,
    username:    String, authType:    Int, password:    String,
    newUsername: String, newAuthType: Int, newPassword: String
  ): Future[Option[String]] = {
    val path = Seq("credentials")
    val form = Map(
      "username"     -> username,    "auth_type"     -> authType,   "password"      -> password,
      "new_username" -> newUsername, "new_auth_type" -> newAuthType, "new_password" -> newPassword
    )

    requester.post(path, form)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }

  /** @return Future(Some(error)) or Future(None) */
  def validate(
    requester: Requester,
    username: String, authType: Int, validated: Boolean
  ): Future[Option[String]] = {
    val path = Seq("credentials", username, authType)
    val form = Map("validated" -> validated)

    requester.patch(path, form)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }

  /** @return Future(Some(error)) or Future(None) */
  def updatePassword(
    requester: Requester,
    username: String, authType: Int, newPassword: String,
    password: String
  ) = doUpdatePassword(requester, username, authType, newPassword, Some(password))

  /** @return Future(Some(error)) or Future(None) */
  def updatePassword(
    requester: Requester,
    username: String, authType: Int, newPassword: String
  ) = doUpdatePassword(requester, username, authType, newPassword, None)

  /** @return Future(Some(error)) or Future(None) */
  private def doUpdatePassword(
    requester: Requester,
    username: String, authType: Int, newPassword: String,
    passwordo: Option[String]
  ): Future[Option[String]] = {
    val path = Seq("credentials", username, authType, "update_password")
    val form = passwordo match {
      case None           => Map("force_new" -> true)
      case Some(password) => Map("password"  -> password)
    }

    requester.patch(path, form)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }

  /** @return Future(Some(error)) or Future(None) */
  def delete(requester: Requester, username: String, authType: Int): Future[Option[String]] = {
    val path = Seq("credentials", username, authType)

    requester.delete(path)
    .map { res =>
      res.statusCode match {
        case 200 => None
        case _   => Some(ErrorMsg(res))
      }
    }
  }
}
