package re_svc_user_mgt.service

import org.json4s.NoTypeHints
import org.json4s.native.Serialization

object Json {
  private implicit val noTypeHints = Serialization.formats(NoTypeHints)

  /**
   * Generates JSON string from Scala object (case class, Map, Seq etc.).
   * See https://github.com/json4s/json4s#serialization
   */
  def apply(scalaObject: AnyRef): String = Serialization.write(scalaObject)(noTypeHints)
}
