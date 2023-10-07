package com.arianegraphql.server.store

import graphql.GraphQLContext
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SessionStoreImplTest {

    private val contextStore = mutableMapOf<String, GraphQLContext>()
    private lateinit var sessionStore: SessionStoreImpl

    @BeforeEach
    fun setUp() {
        sessionStore = SessionStoreImpl(contextStore)
    }

    @Test
    fun `saveContext() should store context into hashmap`() = runBlocking {
        contextStore.clear()
        val sessionId = "foobar"
        val fakeContext = GraphQLContext.of(
            mutableMapOf<Any, Any>(
                "favoriteGalaxy" to Faker().space.galaxy()
            )
        )

        sessionStore.saveContext(sessionId, fakeContext)

        assertEquals(fakeContext, contextStore[sessionId])
    }

    @Test
    fun `getContext() should return the context`() = runBlocking {
        val sessionId = "FOO_BAR"
        val fakeContext = GraphQLContext.of(
            mutableMapOf<Any, Any>(
                "favoriteGalaxy" to Faker().space.galaxy()
            )
        )

        contextStore[sessionId] = fakeContext

        val actual = sessionStore.getContext(sessionId)
        assertEquals(Result.success(fakeContext), actual)
    }
}