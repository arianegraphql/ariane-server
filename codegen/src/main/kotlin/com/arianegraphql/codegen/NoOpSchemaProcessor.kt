package com.arianegraphql.codegen

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

object NoOpSchemaProcessor: SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
}