package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import graphql.language.EnumTypeDefinition

context(CodegenContext)
fun EnumTypeDefinition.generateFile(): FileSpec {
    val builder = TypeSpec.enumBuilder("$typePrefix$name$typeSuffix")
    enumValueDefinitions.forEach {
        builder.addEnumConstant(it.name)
    }

    return FileSpec.builder(packageNameTypes, name)
        .addType(builder.build())
        .build()
}