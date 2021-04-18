package com.arianegraphql.server.playground

import com.arianegraphql.server.request.HttpRequest
import com.arianegraphql.server.response.HttpResponse

interface PlaygroundRenderer {

    fun isPlaygroundRequest(httpRequest: HttpRequest): Boolean

    suspend fun renderExplorer(): HttpResponse
}