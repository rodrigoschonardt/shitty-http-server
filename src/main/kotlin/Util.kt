package rodrigoshonardt

import rodrigoshonardt.data.Request
import rodrigoshonardt.data.Response

object Util {
    fun handleRequest(request : Request, handler : (Request) -> Response) : Response {
        return handler(request)
    }
}