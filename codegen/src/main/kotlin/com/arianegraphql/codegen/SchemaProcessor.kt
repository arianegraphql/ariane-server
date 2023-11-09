package com.arianegraphql.codegen

import com.arianegraphql.codegen.generator.generateAllResolvers
import com.arianegraphql.codegen.generator.generateFile
import com.arianegraphql.codegen.generator.generateResolverFunction
import com.arianegraphql.codegen.ktx.writeTo
import com.arianegraphql.ktx.GraphQLTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import graphql.language.EnumTypeDefinition
import graphql.language.InputObjectTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.schema.idl.TypeDefinitionRegistry

class SchemaProcessor(
    private val typeDefinitionRegistry: TypeDefinitionRegistry,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var currentStep: CurrentStep? = CurrentStep.Ready
    override fun process(resolver: Resolver): List<KSAnnotated> {
        currentStep = currentStep?.next

        return when (currentStep) {
            CurrentStep.Processing -> processTypes()
            CurrentStep.Ready, CurrentStep.Done, null -> emptyList()
        }
    }

    private fun processTypes(): List<KSAnnotated> {
        //TODO SCALARS

        typeDefinitionRegistry.types().values.forEach {
            when {
                it is ObjectTypeDefinition && it.name == "Mutation" -> processMutation(it)
                it is ObjectTypeDefinition && it.name == "Query" -> processQuery(it)
                it is ObjectTypeDefinition && it.name == "Subscription" -> processSubscription(it)
                it is ObjectTypeDefinition -> processType(it)
                it is InputObjectTypeDefinition -> processInputType(it)
                it is EnumTypeDefinition ->  processEnumType(it)
            }
        }

        return emptyList()
    }

    private fun processMutation(mutationTypeDef: ObjectTypeDefinition) {
        mutationTypeDef.generateAllResolvers(mutationTypeName, logger).writeTo(codeGenerator, true)
    }

    private fun processQuery(queryTypeDef: ObjectTypeDefinition) {
        queryTypeDef.generateAllResolvers(queryTypeName, logger).writeTo(codeGenerator, true)
    }

    private fun processSubscription(subscriptionTypeDef: ObjectTypeDefinition) {
        subscriptionTypeDef.generateAllResolvers(subscriptionTypeName, logger).writeTo(codeGenerator, true)
    }

    private fun processType(typeDef: ObjectTypeDefinition) {
        typeDef.generateFile(logger).writeTo(codeGenerator, true)

        val typeClass = ClassName("com.arianegraphql.codegen.type", typeDef.name)
        typeDef.generateAllResolvers(typeClass, logger).writeTo(codeGenerator, true)
    }

    private fun processEnumType(typeDef: EnumTypeDefinition) {
        typeDef.generateFile().writeTo(codeGenerator, true)
    }

    private fun processInputType(typeDef: InputObjectTypeDefinition) {
        typeDef.generateFile(logger).writeTo(codeGenerator, true)
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