package com.arianegraphql.server.request

data class HttpRequest(
    override val body: String,
    val method: String,
    override val headers: Map<String, List<String>>
): IncomingRequest