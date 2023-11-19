package com.arianegraphql.server.ktor

import com.arianegraphql.server.ArianeServer
import com.arianegraphql.server.request.HttpRequest
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

context(ArianeServer)
suspend fun PipelineContext<Unit, ApplicationCall>.handleGraphQLRequest() = handleHttpRequest(httpRequest()) {
    call.respondText(it.body, ContentType.parse(it.contentType))
}

suspend fun PipelineContext<Unit, ApplicationCall>.httpRequest() = HttpRequest(
    call.receiveText(),
    call.request.httpMethod.value,
    call.request.headers.toMap()
)