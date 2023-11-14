package com.arianegraphql.sample

import com.arianegraphql.codegen.resolver.MutationWithEnumResolver
import com.arianegraphql.codegen.type.MutationMutationWithEnumArgument
import com.arianegraphql.ktx.GraphQLTypes
import com.arianegraphql.ktx.Info
import graphql.GraphQLContext

class MutationWithEnumResolverImpl: MutationWithEnumResolver {

    override suspend fun resolve(
        arguments: MutationMutationWithEnumArgument,
        source: GraphQLTypes.Mutation,
        context: GraphQLContext,
        info: Info
    ): Any? {
        TODO("Not yet implemented")
    }

}