package com.arianegraphql.codegen

import com.arianegraphql.codegen.options.CodegenOptions
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class SchemaProcessorProvider : SymbolProcessorProvider {

    val jsonDeserializer = Json { ignoreUnknownKeys = true }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val typeDefinitionRegistry = environment.graphQLSchema.getOrElse {
            logger.error(it.message ?: "An error occurred while reading the GraphQL schema file.")
            return NoOpSchemaProcessor
        }

        val options = environment.codegenOptions.getOrElse {
            logger.error(it.message ?: "An error occurred while reading the Ariane configuration file.")
            return NoOpSchemaProcessor
        }

        return SchemaProcessor(typeDefinitionRegistry, options, environment.codeGenerator, logger)
    }

    private val SymbolProcessorEnvironment.graphQLSchema: Result<TypeDefinitionRegistry>
        get() {
            val schemaPath = options[SCHEMA_ARGUMENT]
                ?: return Result.failure(Throwable("No GraphQL schema provided. Please add it using ksp argument \n\nksp {\n   arg(\"$SCHEMA_ARGUMENT\", \"\") \n}\n"))
            val schemaFile = File(schemaPath).takeIf { it.exists() }
                ?: return Result.failure(Throwable("GraphQL schema not found at location `$schemaPath`. Please verify your configuration."))

            val typeDefinitionRegistry = runCatching { SchemaParser().parse(schemaFile) }.getOrElse {
                return Result.failure(Throwable("GraphQL schema cannot be parsed. Please verify your GraphQL schema file. Error: ${it.message}"))
            }

            return Result.success(typeDefinitionRegistry)
        }

    private val SymbolProcessorEnvironment.codegenOptions: Result<CodegenOptions>
        get() {
            val configFilePath = options[CONFIG_ARGUMENT]
                ?: return Result.failure(Throwable("No Ariane configuration file provided. Please add it using ksp argument \n\nksp {\n   arg(\"$CONFIG_ARGUMENT\", \"\") \n}\n"))
            val configFile = File(configFilePath).takeIf { it.exists() }
                ?: return Result.failure(Throwable("Ariane configuration file at location `$configFilePath`. Please verify your configuration."))


            val options = runCatching<CodegenOptions> {
                jsonDeserializer.decodeFromString(configFile.readText())
            }.getOrElse {
                return Result.failure(Throwable("Ariane configuration file cannot be parsed. Please verify your Ariane configuration file format. Error: ${it.message}"))
            }
            return Result.success(options)
        }

    companion object {
        private const val SCHEMA_ARGUMENT = "graphql-schema"
        private const val CONFIG_ARGUMENT = "ariane-config"
    }
}