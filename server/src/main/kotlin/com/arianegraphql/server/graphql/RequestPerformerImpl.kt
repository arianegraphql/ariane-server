package com.arianegraphql.server.graphql

import com.arianegraphql.server.listener.RequestListener
import graphql.ExecutionInput
import graphql.GraphQL

class RequestPerformerImpl(private val graphQL: GraphQL) : RequestPerformer {

    override suspend fun performRequest(graphQLRequest: GraphQLRequest, context: Any?, requestListener: RequestListener?) = runCatching {
        val executionInput = ExecutionInput.Builder()
            .operationName(graphQLRequest.operationName)
            .query(graphQLRequest.query)
            .variables(graphQLRequest.variables)
            .context(context)
            .build()

        requestListener?.onValidated(executionInput)

        graphQL.execute(executionInput)
    }
}