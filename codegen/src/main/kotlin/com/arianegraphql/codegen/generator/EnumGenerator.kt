package com.arianegraphql.codegen.generator

import com.squareup.kotlinpoet.*
import graphql.language.EnumTypeDefinition

fun EnumTypeDefinition.generateFile(): FileSpec {
    val packageName = "com.arianegraphql.codegen.types"

    val builder = TypeSpec.enumBuilder(name)
    enumValueDefinitions.forEach {
        builder.addEnumConstant(it.name)
    }

    return FileSpec.builder(packageName, name)
        .addType(builder.build())
        .build()
}