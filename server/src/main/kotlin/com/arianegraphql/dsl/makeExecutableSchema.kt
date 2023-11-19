package com.arianegraphql.dsl

import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry

fun makeExecutableSchema(typeRegistry: TypeDefinitionRegistry, runtimeWiring: RuntimeWiring): GraphQLSchema =
    SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring)

fun makeExecutableSchema(schema: String, runtimeWiring: RuntimeWiring): GraphQLSchema =
    makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring)

fun makeExecutableSchema(typeRegistry: TypeDefinitionRegistry, builder: RuntimeWiringBuilder.() -> Unit): GraphQLSchema =
    SchemaGenerator().makeExecutableSchema(typeRegistry, RuntimeWiringBuilder().apply(builder).runtimeWiringBuilder.build())

fun makeExecutableSchema(schema: String, builder: RuntimeWiringBuilder.() -> Unit): GraphQLSchema =
    makeExecutableSchema(SchemaParser().parse(schema), RuntimeWiringBuilder().apply(builder).runtimeWiringBuilder.build())