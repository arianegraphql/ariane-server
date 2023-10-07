package com.arianegraphql.sample

import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.sample.model.Director
import com.arianegraphql.sample.model.Movie
import com.arianegraphql.server.graphql.GraphQLRequest
import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.launch
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import com.arianegraphql.server.request.HttpRequest
import graphql.ExecutionInput
import graphql.GraphQLContext
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import kotlinx.coroutines.flow.*

val movies = mutableListOf<Movie>()
val onMovieAdded = MutableSharedFlow<Movie>()

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
fun main() {
    populateMovies()

    arianeServer {
        schema = loadSchema("schema.graphql")
        port = 3000

        requestListener = object : RequestListener {

            override fun onReceived(httpRequest: HttpRequest) {
                //Called when a request has been received.
            }

            override fun onParsed(graphQLRequest: GraphQLRequest) {
                //Called when a request has been successfully parsed.
            }

            override fun onValidated(executionInput: ExecutionInput) {
                //Called when a GraphQL request has been validated.
            }

            override fun onExecuted(graphQLResponse: GraphQLResponse) {
                //Called when a GraphQL request has been executed.
            }

            override fun onResponded() {
                //Called when a HTTP request has been responded.
            }

            override fun onError(e: Throwable) {
                //Called when an error occurred during the request.
            }

        }

        serverListener = object : ServerListener {

            override fun onStart(host: String, port: Int, path: String) {
                println("Server ready 🚀")
                println("Listening at http://localhost:$port$path")
            }

            override fun onStop() {
                println("Server stop. Bye. 🌜")
            }
        }

        subscriptionListener = object : SubscriptionListener {

            override fun onNewConnection(sessionId: String) {
                println("[$sessionId] onNewConnection()")
            }

            override fun onConnected(sessionId: String, context: GraphQLContext) {
                println("[$sessionId] onConnected()")
            }

            override fun onStartSubscription(sessionId: String, context: GraphQLContext, operationId: String, graphQLRequest: GraphQLRequest) {
                println("[$sessionId] onStartSubscription($operationId, ${graphQLRequest.operationName})")
            }

            override fun onStopSubscription(sessionId: String, context: GraphQLContext, operationId: String) {
                println("[$sessionId] onStopSubscription($operationId)")
            }

            override fun onCloseConnection(sessionId: String, context: GraphQLContext) {
                println("[$sessionId] onCloseConnection()")
            }
        }

        resolvers {
            Query {
                resolve("movies") { _, _: Any?, _: GraphQLContext, _ ->
                    movies
                }

                resolve("directors") { _, _: Any?, _: GraphQLContext, _ ->
                    movies.map { it.director }.distinctBy { it.name }
                }

                resolve("movie") { args, _: Any?, _: GraphQLContext, _ ->
                    movies.find { it.title == args["title"] }
                }
            }

            Mutation {
                resolve("addMovie") { args, parent: Any?, context: GraphQLContext, info ->
                    val movie = Movie(args["title"], Director(args["director"]))
                    movies.add(movie)

                    onMovieAdded.emit(movie)
                    movie
                }
            }

            type("Director") {
                resolve("movies") { args: Any, parent: Director, context: GraphQLContext, info ->
                    movies.filter { it.director.name == parent.name }
                }
            }

            Subscription {
                resolve("onMovieAdded", onMovieAdded)
            }
        }
    }.launch()
}

fun populateMovies() {
    movies.add(Movie("2001: A Space Odyssey", Director("Stanley Kubrick")))
    movies.add(Movie("Mars Attacks!", Director("Tim Burton")))
    movies.add(Movie("Interstellar", Director("Christopher Nolan")))
    movies.add(Movie("Planet of the Apes", Director("Tim Burton")))
    movies.add(Movie("Alien", Director("Ridley Scott")))
    movies.add(Movie("The Martian", Director("Ridley Scott")))
}