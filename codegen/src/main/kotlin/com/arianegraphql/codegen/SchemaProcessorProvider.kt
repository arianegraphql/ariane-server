package com.arianegraphql.codegen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import graphql.schema.idl.SchemaParser
import java.io.File

class SchemaProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val schemaPath = environment.options[SCHEMA_ARGUMENT] ?: run {
            logger.warn("No GraphQL schema provided. Please add it using ksp argument \n\nksp {\n   arg(\"$SCHEMA_ARGUMENT\", \"\") \n}\n")
            return NoOpSchemaProcessor
        }

        val schemaFile = File(schemaPath).takeIf { it.exists() } ?: run {
            logger.error("GraphQL schema not found at location `$schemaPath`. Please verify your configuration.")
            return NoOpSchemaProcessor
        }

        val typeDefinitionRegistry = runCatching { SchemaParser().parse(schemaFile) }.getOrElse {
            logger.error("GraphQL schema cannot be parsed. Please verify your GraphQL schema file.")
            return NoOpSchemaProcessor
        }

        return SchemaProcessor(typeDefinitionRegistry, environment.codeGenerator, logger)
    }

    companion object {
        private const val SCHEMA_ARGUMENT = "graphql-schema"
    }
}