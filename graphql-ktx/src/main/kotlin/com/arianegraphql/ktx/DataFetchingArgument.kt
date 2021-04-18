package com.arianegraphql.ktx

import graphql.schema.DataFetchingEnvironment

@JvmInline
value class DataFetchingArgument(private val dataFetchingEnvironment: DataFetchingEnvironment) : Argument {

    override fun contains(name: String) = dataFetchingEnvironment.containsArgument(name)

    override fun <T> get(name: String): T = dataFetchingEnvironment.getArgument(name)

    override fun <T> getOrDefault(name: String, default: T) = dataFetchingEnvironment.getArgument(name) ?: default
}