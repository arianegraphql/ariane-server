package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionResult
import graphql.GraphQLContext
import org.dataloader.DataLoaderRegistry

interface RequestPerformer {

    suspend fun performRequest(
        graphQLRequest: GraphQLRequest,
        context: GraphQLContext,
        dataLoaderRegistry: DataLoaderRegistry? = null,
        requestListener: RequestListener? = null
    ): Result<ExecutionResult>
}