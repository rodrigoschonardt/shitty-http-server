package rodrigoshonardt

import rodrigoshonardt.data.Request
import rodrigoshonardt.data.Response
import java.net.ServerSocket

class Server(private val port : Int) : AutoCloseable {
    private lateinit var serverSocket : ServerSocket
    private var running = false
    private val routes = mutableMapOf<String, (Request) -> Response>()

    fun startServer() {
        serverSocket = ServerSocket(port)
        serverSocket.reuseAddress = true

        running = true

        println("""
                      __     _  __   __            __     __   __                                               
               _____ / /_   (_)/ /_ / /_ __  __   / /_   / /_ / /_ ____     _____ ___   _____ _   __ ___   _____
              / ___// __ \ / // __// __// / / /  / __ \ / __// __// __ \   / ___// _ \ / ___/| | / // _ \ / ___/
             (__  )/ / / // // /_ / /_ / /_/ /  / / / // /_ / /_ / /_/ /  (__  )/  __// /    | |/ //  __// /    
            /____//_/ /_//_/ \__/ \__/ \__, /  /_/ /_/ \__/ \__// .___/  /____/ \___//_/     |___/ \___//_/     
                                      /____/                   /_/                                              
        """.trimIndent())

        Thread {
            while (running) {
                try {
                    val clientSocket = serverSocket.accept()
                    Thread {
                        clientSocket.use {
                            val input = it.getInputStream().bufferedReader()
                            val output = it.getOutputStream()

                            val firstLine = input.readLine() ?: return@Thread
                            val route = firstLine.split(' ')[1] // TODO convert all to Request object

                            var response = "HTTP/1.1 404 Not Found\r\nContent-Length: 9\r\n\r\nNot found"

                            if (routes.containsKey(route)) {
                                response = routes[route]?.invoke(Request(route))?.data.toString()
                            }

                            output.write(response.toByteArray())
                            output.flush()
                        }
                    }.start()

                } catch (e: Exception) {
                    if (running) {
                        println("Error handling client: ${e.message}")
                    }
                }
            }
        }.start()
    }

    fun addRoute(route : String, handler : (Request) -> Response) {
        routes[route] = handler // TODO add params handling
    }

    override fun close() {
        running = false
        serverSocket.close()
        println("Connection closed!")
    }
}