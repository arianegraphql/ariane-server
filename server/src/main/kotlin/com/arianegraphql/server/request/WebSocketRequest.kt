package com.arianegraphql.server.request

data class WebSocketRequest(
    val sessionId: String,
    override val body: String,
    override val headers: Map<String, List<String>>
) : IncomingRequest