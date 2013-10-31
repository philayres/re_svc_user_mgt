package re_svc_user_mgt.service

import org.json4s.NoTypeHints
import org.json4s.native.Serialization

object Json {
  implicit val formats = Serialization.formats(NoTypeHints)

  /**
   * Generates JSON string from case objects etc.
   * See https://github.com/json4s/json4s#serialization
   */
  def apply(any: AnyRef): String = Serialization.write(any)
}
