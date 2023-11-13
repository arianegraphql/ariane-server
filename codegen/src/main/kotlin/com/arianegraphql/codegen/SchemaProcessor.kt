package com.arianegraphql.codegen

import com.arianegraphql.codegen.generator.generateAllResolvers
import com.arianegraphql.codegen.generator.generateFile
import com.arianegraphql.codegen.ktx.writeTo
import com.arianegraphql.codegen.options.CodegenOptions
import com.arianegraphql.ktx.GraphQLTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import graphql.language.EnumTypeDefinition
import graphql.language.InputObjectTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.schema.idl.TypeDefinitionRegistry
import java.math.BigDecimal
import java.math.BigInteger

class SchemaProcessor(
    private val typeDefinitionRegistry: TypeDefinitionRegistry,
    private val options: CodegenOptions,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var currentStep: CurrentStep? = CurrentStep.Ready

    private val scalarTypes: MutableMap<String, TypeName> = mutableMapOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        currentStep = currentStep?.next

        when (currentStep) {
            CurrentStep.Processing -> {
                var shouldContinue = true

                if (shouldContinue) shouldContinue = processScalars()
                if (shouldContinue) shouldContinue = processTypes()

            }

            CurrentStep.Ready, CurrentStep.Done, null -> Unit
        }
        return emptyList()
    }

    private fun processScalars(): Boolean {
        val declaredScalarTypes = options.scalars ?: emptyMap()
        val defaultScalars = mapOf(
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

        typeDefinitionRegistry.scalars().forEach { (key) ->
            scalarTypes[key] = declaredScalarTypes[key]?.let { fullyQualifiedName ->
                runCatching { ClassName.bestGuess(fullyQualifiedName) }.getOrElse {
                    logger.error("Scalar `$key` cannot be converted in `$fullyQualifiedName`. Please verify your codegen config file.")
                    return false
                }
            } ?: defaultScalars[key] ?: run {
                logger.error("Scalar `$key` is missing in the configuration. Please add it to your codegen config file.")
                return false
            }
        }

        return true
    }

    private fun processTypes(): Boolean {
        val context = options.codegenContext(logger, scalarTypes)
        with(context) {
            typeDefinitionRegistry.types().values.forEach {
                when {
                    it is ObjectTypeDefinition && it.name == "Mutation" -> processMutation(it)
                    it is ObjectTypeDefinition && it.name == "Query" -> processQuery(it)
                    it is ObjectTypeDefinition && it.name == "Subscription" -> processSubscription(it)
                    it is ObjectTypeDefinition -> processType(it)
                    it is InputObjectTypeDefinition -> processInputType(it)
                    it is EnumTypeDefinition -> processEnumType(it)
                }
            }
        }

        return true
    }

    context(CodegenContext)
    private fun processMutation(mutationTypeDef: ObjectTypeDefinition) {
        mutationTypeDef.generateAllResolvers(mutationTypeName).writeTo(codeGenerator, true)
    }

    context(CodegenContext)
    private fun processQuery(queryTypeDef: ObjectTypeDefinition) {
        queryTypeDef.generateAllResolvers(queryTypeName).writeTo(codeGenerator, true)
    }

    context(CodegenContext)
    private fun processSubscription(subscriptionTypeDef: ObjectTypeDefinition) {
        subscriptionTypeDef.generateAllResolvers(subscriptionTypeName).writeTo(codeGenerator, true)
    }

    context(CodegenContext)
    private fun processType(typeDef: ObjectTypeDefinition) {
        typeDef.generateFile().writeTo(codeGenerator, true)

        val typeClass = ClassName("com.arianegraphql.codegen.type", typeDef.name)
        typeDef.generateAllResolvers(typeClass).writeTo(codeGenerator, true)
    }

    context(CodegenContext)
    private fun processEnumType(typeDef: EnumTypeDefinition) {
        typeDef.generateFile().writeTo(codeGenerator, true)
    }

    context(CodegenContext)
    private fun processInputType(typeDef: InputObjectTypeDefinition) {
        typeDef.generateFile().writeTo(codeGenerator, true)
    }

    companion object {
        val mutationTypeName = GraphQLTypes.Mutation::class.asClassName()
        val queryTypeName = GraphQLTypes.Query::class.asClassName()
        val subscriptionTypeName = GraphQLTypes.Subscription::class.asClassName()
    }
}

sealed class CurrentStep(val next: CurrentStep?) {
    object Ready : CurrentStep(Processing)
    object Processing : CurrentStep(Done)
    object Done : CurrentStep(null)
}