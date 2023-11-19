package com.arianegraphql.sample

import com.arianegraphql.dsl.loadSchema
import com.arianegraphql.dsl.arianeServer
import graphql.language.StringValue
import java.time.Instant

val myServer = arianeServer {
    schema = loadSchema("schema.graphql")
    port = 3000

    onStart { _, port, path ->
        println("Server ready 🚀")
        println("Listening at http://localhost:$port$path")
    }

    scalar("Date") {

        serialize { result -> (result as? Instant)?.toString() }

        parseValue { input -> (input as? String)?.let(Instant::parse) }

        parseLiteral { input -> (input as? StringValue)?.value?.let(Instant::parse) }
    }

    resolvers(myResolvers)
}
