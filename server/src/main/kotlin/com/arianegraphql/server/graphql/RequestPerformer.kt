package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionResult

interface RequestPerformer {

    suspend fun performRequest(graphQLRequest: GraphQLRequest, context: Any?, requestListener: RequestListener?): Result<ExecutionResult>
}