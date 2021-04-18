package com.arianegraphql.server.store

import java.util.concurrent.ConcurrentHashMap

class SessionStoreImpl(
    private val contextStore: MutableMap<String, Any> = ConcurrentHashMap<String, Any>()
): SessionStore {

    override suspend fun saveContext(sessionId: String, context: Any?) {
        context?.let { contextStore[sessionId] = context }
    }

    override suspend fun getContext(sessionId: String): Any? {
        return contextStore[sessionId]
    }

    override suspend fun clearContext(sessionId: String) {
        contextStore.remove(sessionId)
    }
}