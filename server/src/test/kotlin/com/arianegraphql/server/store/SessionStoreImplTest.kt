package com.arianegraphql.server.store

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SessionStoreImplTest {

    private val contextStore = mutableMapOf<String, Any>()
    private lateinit var sessionStore: SessionStoreImpl

    @BeforeEach
    fun setUp() {
        sessionStore = SessionStoreImpl(contextStore)
    }

    @Test
    fun `saveContext() should store context into hashmap`() = runBlocking {
        contextStore.clear()
        val sessionId = "foobar"
        val context = FakeContext()
        sessionStore.saveContext(sessionId, context)

        assertEquals(context, contextStore[sessionId])
    }

    @Test
    fun `saveContext() should not store null context`() = runBlocking {
        contextStore.clear()
        val sessionId = "barfoo"

        sessionStore.saveContext(sessionId, null)
        assertEquals(0, contextStore.size)
    }

    @Test
    fun `getContext() should return the context`() = runBlocking {
        val sessionId = "FOO_BAR"

        val context = FakeContext()

        contextStore[sessionId] = context

        val actual = sessionStore.getContext(sessionId)
        assertEquals(context, actual)
    }
}

data class FakeContext(val favoriteGalaxy: String = Faker().space.galaxy())