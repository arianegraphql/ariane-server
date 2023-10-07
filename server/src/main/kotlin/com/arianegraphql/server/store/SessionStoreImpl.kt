package com.arianegraphql.server.store

import graphql.GraphQLContext
import java.util.concurrent.ConcurrentHashMap

class SessionStoreImpl(
    private val contextStore: MutableMap<String, GraphQLContext> = ConcurrentHashMap<String, GraphQLContext>()
) : SessionStore {

    override suspend fun saveContext(sessionId: String, context: GraphQLContext) {
        contextStore[sessionId] = context
    }

    override suspend fun getContext(sessionId: String): Result<GraphQLContext> {
        return contextStore[sessionId]?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Session with id '$sessionId' not found."))
    }

    override suspend fun clearContext(sessionId: String) {
        contextStore.remove(sessionId)
    }
}