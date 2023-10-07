package com.arianegraphql.server.listener

import com.arianegraphql.server.graphql.GraphQLRequest
import graphql.GraphQLContext

interface SubscriptionListener {

    fun onNewConnection(sessionId: String) {}

    fun onConnected(sessionId: String, context: GraphQLContext) {}

    fun onStartSubscription(sessionId: String, context: GraphQLContext, operationId: String, graphQLRequest: GraphQLRequest) {}

    fun onStopSubscription(sessionId: String, context: GraphQLContext, operationId: String) {} //TODO NOT CALLED

    fun onCloseConnection(sessionId: String, context: GraphQLContext) {} //TODO NOT CALLED
}