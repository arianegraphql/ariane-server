package com.arianegraphql.sample

import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.sample.model.Director
import com.arianegraphql.sample.model.Movie
import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.launch
import com.arianegraphql.server.listener.ServerListener
import kotlinx.coroutines.flow.*

val movies = mutableListOf<Movie>()
val onMovieAdded = MutableSharedFlow<Movie>()

fun main() {
    populateMovies()

    arianeServer {
        schema = loadSchema("schema.graphql")
        port = 3000

        serverListener = object : ServerListener {

            override fun onStart(host: String, port: Int, path: String) {
                println("Server ready 🚀")
                println("Listening at http://localhost:$port$path")
            }
        }

        resolvers {
            Query {
                resolve("movies") {
                    movies
                }

                resolve("directors") {
                    movies.map { it.director }.distinctBy { it.name }
                }

                resolve("movie") {
                    movies.find { it.title == arguments["title"] }
                }
            }

            Mutation {
                resolve("addMovie") {
                    val movie = Movie(arguments["title"], Director(arguments["director"]))
                    movies.add(movie)

                    onMovieAdded.emit(movie)
                    movie
                }
            }

            type<Director>("Director") {
                resolve("movies") {
                    movies.filter { it.director.name == source.name }
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