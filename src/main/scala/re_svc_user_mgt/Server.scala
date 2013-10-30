package re_svc_user_mgt

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}

object Server extends App {
  ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(Config.port))
    .name("re_svc_user_mgt")
    .build(Routes.routes)
}

/*





remove_client_machine (remove_client_name, username, auth_type, password)
remove the record from the database table and return HTTP OK if the user is authenticated and matchs an 'admin user / client' record in the database
if not found, return HTTP not found

*/
