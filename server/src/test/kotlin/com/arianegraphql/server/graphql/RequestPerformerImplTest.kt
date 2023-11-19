package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RequestPerformerImplTest {

    private val mockSchema: GraphQL = mockk()
    private lateinit var requestPerformer: RequestPerformerImpl
    private val mockRequestListener: RequestListener = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        requestPerformer = RequestPerformerImpl(mockSchema)
    }

    @Test
    fun `performRequest() executes the request and returns its result`() = runBlocking {
        val inputSlot = slot<ExecutionInput>()
        val graphQLExecutionResult: ExecutionResult = mockk()
        every { mockSchema.execute(capture(inputSlot)) } returns graphQLExecutionResult

        val result = requestPerformer.performRequest(graphQLRequest, context, mockRequestListener)

        verify { mockRequestListener.onValidated(any()) }
        assert(result.isSuccess)
        assertEquals(graphQLExecutionResult, result.getOrNull())
        assertEquals(inputSlot.captured.operationName, graphQLRequest.operationName)
        assertEquals(inputSlot.captured.query, graphQLRequest.query)
        assertEquals(inputSlot.captured.variables, graphQLRequest.variables)
        assertEquals(inputSlot.captured.context, context)
    }

    @Test
    fun `performRequest() returns a Failure if execution fails`() = runBlocking {
        val inputSlot = slot<ExecutionInput>()
        val error = RuntimeException("Boom")
        every { mockSchema.execute(capture(inputSlot)) } throws error

        val result = requestPerformer.performRequest(graphQLRequest, context, null)

        assert(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        assertEquals(inputSlot.captured.operationName, graphQLRequest.operationName)
        assertEquals(inputSlot.captured.query, graphQLRequest.query)
        assertEquals(inputSlot.captured.variables, graphQLRequest.variables)
        assertEquals(inputSlot.captured.context, context)
    }

    private companion object {
        val graphQLRequest: GraphQLRequest = GraphQLRequest("foo", "bar", emptyMap(), null)
        val context = GraphQLContext.of(mapOf("userID" to 42))
    }
}