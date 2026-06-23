package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.validation.QueryComplexityLimits
import org.dataloader.DataLoaderRegistry

class RequestPerformerImpl(
    private val graphQL: GraphQL,
    private val queryComplexityLimits: QueryComplexityLimits? = null,
) : RequestPerformer {

    override suspend fun performRequest(
        graphQLRequest: GraphQLRequest,
        context: GraphQLContext,
        dataLoaderRegistry: DataLoaderRegistry?,
        requestListener: RequestListener?
    ) = runCatching {
        val executionInputBuilder = ExecutionInput.Builder()
            .operationName(graphQLRequest.operationName)
            .query(graphQLRequest.query)
            .variables(graphQLRequest.variables ?: emptyMap())
            .graphQLContext {
                // Server-wide limits first, so a per-request entry in the resolved
                // context can override them.
                queryComplexityLimits?.let { limits -> it.put(QueryComplexityLimits.KEY, limits) }
                it.of(context)
            }

        if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry)
        }

        val executionInput = executionInputBuilder.build()

        requestListener?.onValidated(executionInput)

        graphQL.execute(executionInput)
    }
}