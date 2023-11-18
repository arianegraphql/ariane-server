package com.arianegraphql.codegen

import com.squareup.kotlinpoet.*
import graphql.schema.idl.TypeDefinitionRegistry
import org.gradle.api.logging.Logger
import java.io.File

data class CodegenContext(
    val logger: Logger,
    val scalarTypes: Map<String, TypeName>,
    val graphQLSchema: TypeDefinitionRegistry,
    val buildDir: File,
    val packageName: String,
) {

    val packageNameTypes: String
        get() = "$packageName.type"

    val packageNameResolvers: String
        get() = "$packageName.resolver"

    companion object {
        const val DefaultPackageName = "com.arianegraphql.codegen"
    }
}