package com.arianegraphql.server.ktor

import com.arianegraphql.server.config.ArianeServerConfiguration
import com.arianegraphql.server.config.newArianeServer
import com.arianegraphql.server.dsl.ArianeServerBuilder
import com.arianegraphql.server.dsl.arianeServer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.websocket.*

fun ArianeServerConfiguration.launch(wait: Boolean = true) {
    val arianeServer = newArianeServer(JacksonSerializer)

    initLoggers()

    embeddedServer(CIO, port, host) {
        install(WebSockets)

        if (enableCORS) {
            install(CORS)
        }

        serverListener?.let { listener ->
            environment.monitor.subscribe(ApplicationStarted) {
                listener.onStart(host, port, path)
            }

            environment.monitor.subscribe(ApplicationStopped) {
                listener.onStop()
            }
        }

        routing {
            route(path) {
                post {
                    handleGraphQLRequest(arianeServer)
                }

                get {
                    handleGraphQLRequest(arianeServer)
                }
            }

            webSocket(path, "graphql-ws") {
                handleGraphQLSubscription(arianeServer)
            }
        }
    }.start(wait = wait)
}

fun ArianeServerConfiguration.startWithoutWaiting() = launch(false)

fun launchAriane(wait: Boolean = true, builder: ArianeServerBuilder.() -> Unit) = arianeServer(builder).launch(wait)