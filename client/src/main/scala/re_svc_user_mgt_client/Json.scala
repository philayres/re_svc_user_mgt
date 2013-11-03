package re_svc_user_mgt_client

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

object Json {
  /**
   * Parses JSON string to Scala object (case class, Map, Seq etc.).
   * See https://github.com/json4s/json4s#serialization
   */
  def apply[T](jsonString: String)(implicit m: Manifest[T]): T =
    Serialization.read[T](jsonString)(DefaultFormats, m)
}
