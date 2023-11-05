package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionResult
import graphql.GraphQLContext

interface RequestPerformer {

    suspend fun performRequest(graphQLRequest: GraphQLRequest, context: GraphQLContext, requestListener: RequestListener?): Result<ExecutionResult>
}