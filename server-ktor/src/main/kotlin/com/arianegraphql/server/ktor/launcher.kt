package com.arianegraphql.server.ktor

import com.arianegraphql.server.config.newArianeServer
import com.arianegraphql.server.dsl.ArianeServerBuilder
import com.arianegraphql.server.ktor.dsl.ArianeKtorServerConfiguration
import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.json.JacksonSerializer
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.server.plugins.cors.routing.CORS

fun ArianeKtorServerConfiguration.launch(wait: Boolean = true) {
    val arianeServer = newArianeServer(JacksonSerializer)

    initLoggers()
    arianeServer.initializeServer()

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

        ktorPlugin?.invoke(this)

        routing {
            route(path) {
                post {
                    with(arianeServer) { handleGraphQLRequest() }
                }

                get {
                    with(arianeServer) { handleGraphQLRequest() }
                }
            }

            webSocket(path, "graphql-ws") {
                with(arianeServer) { handleGraphQLSubscription() }
            }
        }
    }.start(wait = wait)
}

fun ArianeKtorServerConfiguration.launchWithoutWaiting() = launch(false)

fun launchAriane(wait: Boolean = true, builder: ArianeServerBuilder.() -> Unit) = arianeServer(builder).launch(wait)