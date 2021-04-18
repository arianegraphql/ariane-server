package com.arianegraphql.server.listener

import com.arianegraphql.server.graphql.GraphQLRequest
import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.request.HttpRequest
import graphql.ExecutionInput

interface RequestListener {

    /**
     * Method called when a request has been received.
     */
    fun onReceived(httpRequest: HttpRequest) {}

    /**
     * Method called when a request has been successfully parsed.
     */
    fun onParsed(graphQLRequest: GraphQLRequest) {}

    /**
     * Method called when a GraphQL request has been validated.
     */
    fun onValidated(executionInput: ExecutionInput) {}

    /**
     * Method called when a GraphQL request has been executed.
     */
    fun onExecuted(graphQLResponse: GraphQLResponse) {}

    /**
     * Method called when a HTTP request has been responded.
     */
    fun onResponded() {}

    /**
     * Method called when an error occurred during the request.
     */
    fun onError(e: Throwable) {}
}