package com.arianegraphql.server.playground

import com.arianegraphql.server.request.HttpRequest
import com.arianegraphql.server.response.HttpResponse
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class PlaygroundRendererImpl(
    private val playgroundPath: String = DEFAULT_PLAYGROUND_PATH
) : PlaygroundRenderer {

    override fun isPlaygroundRequest(httpRequest: HttpRequest) = httpRequest.method.uppercase() == "GET"
            && !httpRequest.contentType.contains("application/json")

    override suspend fun renderExplorer(): HttpResponse {
        val playgroundInputStream = ClassLoader.getSystemResourceAsStream(playgroundPath)
            ?: throw FileNotFoundException("No file found at path $playgroundPath")

        val inputStreamReader = InputStreamReader(playgroundInputStream, StandardCharsets.UTF_8)

        val playgroundHtml = BufferedReader(inputStreamReader)
            .lines()
            .collect(Collectors.joining("\n"))

        return HttpResponse(playgroundHtml, CONTENT_TYPE)
    }

    private companion object {
        const val DEFAULT_PLAYGROUND_PATH = "graphql-playground/packages/graphql-playground-html/withAnimation.html"
        const val CONTENT_TYPE = "text/html"
    }
}