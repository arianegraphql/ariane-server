package com.arianegraphql.server.playground

import com.arianegraphql.server.request.HttpRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.FileNotFoundException

internal class PlaygroundRendererImplTest {

    private lateinit var playgroundRenderer: PlaygroundRendererImpl

    @BeforeEach
    fun setUp() {
        playgroundRenderer = PlaygroundRendererImpl(PLAYGROUND_PATH_TEST)
    }

    @Test
    fun `isPlaygroundRequest() should detect Playground HTTP request`() {
        val httpRequest = HttpRequest(
            "foobar", "Get", mapOf(
                "content-type" to listOf("text/json", "application/html", "application-json")
            )
        )

        val result = playgroundRenderer.isPlaygroundRequest(httpRequest)

        assertTrue(result)
    }

    @Test
    fun `isPlaygroundRequest() should detect invalid content-type`() {
        val httpRequest = HttpRequest(
            "foobar", "Get", mapOf(
                "content-type" to listOf("text/json", "application/json", "application-json")
            )
        )

        val result = playgroundRenderer.isPlaygroundRequest(httpRequest)

        assertFalse(result)
    }

    @Test
    fun `isPlaygroundRequest() should detect invalid HTTP method`() {
        val httpRequest = HttpRequest("foobar", "Post", mapOf())
        val result = playgroundRenderer.isPlaygroundRequest(httpRequest)

        assertFalse(result)
    }

    @Test
    fun `renderExplorer() should returns the playground html content`() = runBlocking {
        val httpResponse = playgroundRenderer.renderExplorer()
        assertEquals(EXPECTED_PLAYGROUND_VALUE, httpResponse.body)
        assertEquals("text/html", httpResponse.contentType)
    }

    @Test
    fun `renderExplorer() should throw a FileNotFoundExcepton`() = runBlocking {
        val invalidPath = "./foo.bar"
        val result = runCatching {
            PlaygroundRendererImpl(invalidPath).renderExplorer()
        }
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is FileNotFoundException)
        assertEquals("No file found at path $invalidPath", result.exceptionOrNull()?.message)
    }

    private companion object {
        const val PLAYGROUND_PATH_TEST = "graphql-playground-test.html"

        val EXPECTED_PLAYGROUND_VALUE = """
<foo>
    bar
</foo>
        """.trim()
    }
}