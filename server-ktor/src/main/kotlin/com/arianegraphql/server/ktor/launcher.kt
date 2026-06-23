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

    // Captured locally: inside routing {} the receiver is a Route, and since Ktor 3.5
    // the Route.path extension shadows this configuration's path property.
    val graphQLPath = path

    initLoggers()
    arianeServer.initializeServer()

    embeddedServer(CIO, port, host) {
        install(WebSockets)

        corsConfig?.let { install(CORS, it) }

        serverListener?.let { listener ->
            monitor.subscribe(ApplicationStarted) {
                listener.onStart(host, port, graphQLPath)
            }

            monitor.subscribe(ApplicationStopped) {
                listener.onStop()
            }
        }

        ktorPlugin?.invoke(this)

        routing {
            route(graphQLPath) {
                post {
                    call.request
                    context(arianeServer) { handleGraphQLRequest() }
                }

                get {
                    context(arianeServer) { handleGraphQLRequest() }
                }
            }

            webSocket(graphQLPath, "graphql-ws") {
                with(arianeServer) { handleGraphQLSubscription() }
            }
        }
    }.start(wait = wait)
}

fun ArianeKtorServerConfiguration.launchWithoutWaiting() = launch(false)

fun launchAriane(wait: Boolean = true, builder: ArianeServerBuilder.() -> Unit) = arianeServer(builder).launch(wait)