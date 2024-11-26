import rodrigoshonardt.Server
import java.net.HttpURLConnection
import java.net.URI
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTests {
    @Test
    fun serverMustRespondWith200() {
        val server = Server(4232)

        server.startServer()
        server.use {
            val url = URI("http://localhost:4232/").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode

            assertEquals(200, responseCode, "Expected HTTP 200 OK")
        }
    }

    @Test
    fun serverMustRespondWith404() {
        val server = Server(4232)

        server.startServer()
        server.use {
            val url = URI("http://localhost:4232/" + UUID.randomUUID().toString()).toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode

            assertEquals(404, responseCode, "Expected HTTP 200 OK")
        }
    }
}