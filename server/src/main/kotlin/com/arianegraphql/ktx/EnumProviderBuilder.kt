package com.arianegraphql.ktx

import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
abstract class EnumProviderBuilder {
    val enumProviders = mutableMapOf<String, TypeRuntimeWiring>()

    inline fun <reified E : Enum<E>> enum(enumName: String? = null) {
        val name = enumName ?: E::class.java.simpleName
        enumProviders[name] = TypeRuntimeWiring
            .newTypeWiring(enumName)
            .enumValues {
                enumValueOf<E>(it)
            }
            .build()
    }

    internal fun buildEnumProviders(): List<TypeRuntimeWiring> = enumProviders.map { it.value }
}