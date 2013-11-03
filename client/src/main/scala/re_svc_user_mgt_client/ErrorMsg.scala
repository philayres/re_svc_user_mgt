package re_svc_user_mgt_client

import com.twitter.finagle.http.Response

import org.json4s._
import org.json4s.native.JsonMethods._

/** Extracts error message from response {"error": <msg>}. */
object ErrorMsg {
  def apply(response: Response): String = {
    val content = response.contentString
    val json    = parse(content)

    val list = for {
      JObject(child) <- json
      JField("error", JString(error))  <- child
    } yield error
    list(0)
  }
}
