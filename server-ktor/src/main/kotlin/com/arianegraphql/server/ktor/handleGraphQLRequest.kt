package com.arianegraphql.server.ktor

import com.arianegraphql.server.ArianeServer
import com.arianegraphql.server.request.HttpRequest
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.handleGraphQLRequest(arianeServer: ArianeServer) {
    arianeServer.handleHttpRequest(httpRequest()) { response ->
        call.respondText(response.body, ContentType.parse(response.contentType))
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.httpRequest() = HttpRequest(
    call.receiveText(),
    call.request.httpMethod.value,
    call.request.headers.toMap()
)