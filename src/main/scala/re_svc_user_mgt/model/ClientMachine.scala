package re_svc_user_mgt.model

object ClientMachine {
  /** @return Left(error) or Right(sharedSecret) */
  def create(clientName: String, clientType: Int): Either[String, String] = {
    Left("TODO")
  }

  /** @return Some(error) or None */
  def delete(clientName: String): Option[String] = {
    Some("TODO")
  }
}
