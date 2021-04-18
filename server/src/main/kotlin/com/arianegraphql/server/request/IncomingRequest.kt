package com.arianegraphql.server.request

interface IncomingRequest {
    val body: String
    val headers: Map<String, List<String>>

    fun containsHeader(name: String) = headers[name]?.let { if (it.isEmpty()) null else it } != null
    fun getHeader(name: String): String? = headers[name]?.firstOrNull()

    val authorization: String?
        get() = headers["Authorization"]?.firstOrNull()

    val contentType: List<String>
        get() = headers.getOrDefault("content-type", emptyList())
}