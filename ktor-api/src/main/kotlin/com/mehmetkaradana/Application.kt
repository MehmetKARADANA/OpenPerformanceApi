package com.mehmetkaradana

import com.mehmetkaradana.services.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json


fun main() {
    embeddedServer(Netty, port = 8082) {
        configureRouting()
    }.start(wait = true)
}




fun Routing.taskRoutes() {
    val client = HttpClient(CIO){
       install(HttpTimeout){
            socketTimeoutMillis =10000
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 10000
        }
    }
        val serversConfig = loadServersConfig("C:\\Users\\Mehmet\\Desktop\\Api2.0\\ktor-api\\src\\main\\resources\\servers.yaml")
        //   val serversConfig = loadServersConfig("src/main/resources/servers.yaml")
    route("/api") {
        get("/getRooms") {
            val responses = coroutineScope {
                async {
                   fetchFromAllServersAsJson(client, serversConfig.servers)

                }.await() // Sonucu bekle
            }
            // Yanıtı çağrıya gönder
            call.respondText(responses)

        }
    }

}

fun Application.configureRouting() {
    routing {
        taskRoutes()

    }
}