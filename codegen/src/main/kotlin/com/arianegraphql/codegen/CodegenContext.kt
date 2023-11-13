package com.arianegraphql.codegen

import com.arianegraphql.codegen.options.CodegenOptions
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.TypeName

data class CodegenContext(
    val logger: KSPLogger,
    val scalarTypes: Map<String, TypeName>,
    val packageName: String,
) {

    val packageNameTypes: String
        get() = "$packageName.type"

    val packageNameResolvers: String
        get() = "$packageName.resolver"
}

fun CodegenOptions.codegenContext(logger: KSPLogger, scalarTypes: Map<String, TypeName>) = CodegenContext(
    logger,
    scalarTypes,
    packageName ?: DefaultPackageName
)

const val DefaultPackageName = "com.arianegraphql.codegen"