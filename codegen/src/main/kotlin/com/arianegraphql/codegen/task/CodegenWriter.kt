package com.arianegraphql.codegen.task

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.FileSpec

context(ctx: CodegenContext)
fun FileSpec.write() {
    writeTo(ctx.buildDir)
}

context(ctx: CodegenContext)
fun List<FileSpec>.write() = forEach { it.write() }