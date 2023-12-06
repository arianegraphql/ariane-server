package com.arianegraphql.codegen.task

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.codegen.CodegenTask
import com.arianegraphql.codegen.options.CodegenOptions
import com.squareup.kotlinpoet.*
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import java.math.BigDecimal
import java.math.BigInteger

fun CodegenTask.prepare(): Result<CodegenContext> {
    val graphQLSchema = getTypeDefinitionRegistry().getOrElse {
        return Result.failure(it)
    }

    val options = getCodegenOptions().getOrElse {
        return Result.failure(it)
    }

    val scalarTypes = getScalarTypes(graphQLSchema, options).getOrElse {
        return Result.failure(it)
    }

    return Result.success(
        CodegenContext(
            logger = logger,
            scalarTypes = scalarTypes,
            graphQLSchema = graphQLSchema,
            buildDir = outputDir.get().asFile,
            packageName = options.packageName ?: CodegenContext.DefaultPackageName,
            typeSuffix = options.typeSuffix ?: "",
            typePrefix = options.typePrefix ?: "",
        )
    )
}

context(CodegenTask)
private fun getTypeDefinitionRegistry(): Result<TypeDefinitionRegistry> {
    val schemaFile = schema.orNull?.asFile
        ?: return Result.failure(Throwable("No GraphQL schema provided. Please add it using ksp argument \n\nariane {\n   schema(file(\"\") \n}\n"))

    if (!schemaFile.exists()) {
        return Result.failure(Throwable("GraphQL schema not found at location `${schemaFile.absolutePath}`. Please verify your configuration."))
    }

    val typeDefinitionRegistry = runCatching { SchemaParser().parse(schemaFile) }.getOrElse {
        return Result.failure(Throwable("GraphQL schema cannot be parsed. Please verify your GraphQL schema file. Error: ${it.message}"))
    }

    return Result.success(typeDefinitionRegistry)
}

context(CodegenTask)
private fun getCodegenOptions(): Result<CodegenOptions> {
    val configFile = configuration.orNull?.asFile ?: run {
        logger.info("[ARIANE] No configuration file provided to the codegen. Default configuration will be used.")
        return Result.success(CodegenOptions.DefaultOptions)
    }

    if (!configFile.exists()) {
        return Result.failure(Throwable("No Ariane configuration file at location `${configFile.absolutePath}`. Please verify your configuration."))
    }


    val options = runCatching<CodegenOptions> {
        CodegenTask.JsonParser.decodeFromString(configFile.readText())
    }.getOrElse {
        return Result.failure(Throwable("Ariane configuration file cannot be parsed. Please verify your Ariane configuration file format. Error: ${it.message}"))
    }
    return Result.success(options)
}

private fun getScalarTypes(schema: TypeDefinitionRegistry, options: CodegenOptions): Result<Map<String, TypeName>> {
    val scalarTypes: MutableMap<String, TypeName> = mutableMapOf()
    val declaredScalarTypes = options.scalars ?: emptyMap()

    schema.scalars().forEach { (key) ->
        scalarTypes[key] = declaredScalarTypes[key]?.let { fullyQualifiedName ->
            runCatching { ClassName.bestGuess(fullyQualifiedName) }.getOrElse {
                return Result.failure(Throwable("Scalar `$key` cannot be converted in `$fullyQualifiedName`. Please verify your codegen config file."))
            }
        } ?: DefaultScalars[key] ?: run {
            return Result.failure(Throwable("Scalar `$key` is missing in the configuration. Please add it to your codegen config file."))
        }
    }

    return Result.success(scalarTypes)
}

val DefaultScalars = mapOf(
    "String" to STRING,
    "Boolean" to BOOLEAN,
    "Int" to INT,
    "Float" to FLOAT,
    "ID" to STRING,
    "Long" to LONG,
    "Short" to SHORT,
    "Byte" to BYTE,
    "BigDecimal" to typeNameOf<BigDecimal>(),
    "BigInteger" to typeNameOf<BigInteger>(),
)