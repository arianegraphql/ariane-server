package com.arianegraphql.server.store

import graphql.GraphQLContext

interface SessionStore {

    suspend fun saveContext(sessionId: String, context: GraphQLContext)

    suspend fun getContext(sessionId: String): Result<GraphQLContext>

    suspend fun clearContext(sessionId: String)
}