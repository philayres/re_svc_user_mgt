package re_svc_user_mgt

import com.twitter.finagle.http.Request

// https://github.com/philayres/re_svc_user_mgt/issues/16
package object service {
  class MissingRequiredParamException(val paramName: String) extends Exception

  /**
   * When the param is missing, MissingRequiredParamException will be thrown.
   * The exception will be caught at our FilterExeption.
   */
  def requireParamString(request: Request, paramName: String): String = {
    val params = request.params
    if (params.contains(paramName))
      params.get(paramName).get
    else
      throw new MissingRequiredParamException(paramName)
  }

  /**
   * When the param is missing, MissingRequiredParamException will be thrown.
   * The exception will be caught at our FilterExeption.
   */
  def requireParamInt(request: Request, paramName: String): Int = {
    val params = request.params
    if (params.contains(paramName))
      params.getInt(paramName).get
    else
      throw new MissingRequiredParamException(paramName)
  }

  /**
   * When the param is missing, MissingRequiredParamException will be thrown.
   * The exception will be caught at our FilterExeption.
   */
  def requireParamBoolean(request: Request, paramName: String): Boolean = {
    val params = request.params
    if (params.contains(paramName))
      params.getBoolean(paramName).get
    else
      throw new MissingRequiredParamException(paramName)
  }
}
