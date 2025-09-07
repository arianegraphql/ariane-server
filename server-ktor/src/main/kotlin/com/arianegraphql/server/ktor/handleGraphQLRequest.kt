package com.arianegraphql.server.ktor

import com.arianegraphql.server.ArianeServer
import com.arianegraphql.server.request.HttpRequest
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingHandler
import io.ktor.util.*
import io.ktor.util.pipeline.*

context(server: ArianeServer)
suspend fun RoutingContext.handleGraphQLRequest() = server.handleHttpRequest(httpRequest()) {
    call.respondText(it.body, ContentType.parse(it.contentType))
}

suspend fun RoutingContext.httpRequest() = HttpRequest(
    call.receiveText(),
    call.request.httpMethod.value,
    call.request.headers.toMap()
)