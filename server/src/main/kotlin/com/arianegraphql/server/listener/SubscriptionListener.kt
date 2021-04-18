package com.arianegraphql.server.listener

import com.arianegraphql.server.graphql.GraphQLRequest

interface SubscriptionListener {

    fun onNewConnection(sessionId: String) {}

    fun onConnected(sessionId: String, context: Any?) {}

    fun onStartSubscription(sessionId: String, context: Any?, operationId: String, graphQLRequest: GraphQLRequest) {}

    fun onStopSubscription(sessionId: String, context: Any?, operationId: String) {} //TODO NOT CALLED

    fun onCloseConnection(sessionId: String, context: Any?) {} //TODO NOT CALLED
}