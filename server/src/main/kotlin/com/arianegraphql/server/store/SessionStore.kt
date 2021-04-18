package com.arianegraphql.server.store

interface SessionStore {

    suspend fun saveContext(sessionId: String, context: Any?)

    suspend fun getContext(sessionId: String): Any?

    suspend fun clearContext(sessionId: String)
}