package com.arianegraphql.codegen.task

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.codegen.CodegenTask
import com.arianegraphql.codegen.generator.generateAllResolvers
import com.arianegraphql.codegen.generator.generateFile
import com.arianegraphql.ktx.GraphQLTypes
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import graphql.language.EnumTypeDefinition
import graphql.language.InputObjectTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.schema.idl.TypeDefinitionRegistry

context(CodegenContext)
fun CodegenTask.generateCode(): Result<Unit> = runCatching {
    graphQLSchema.types().values.forEach {
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

context(CodegenContext)
private fun processMutation(mutationTypeDef: ObjectTypeDefinition) {
    mutationTypeDef.generateAllResolvers(mutationTypeName).write()
}

context(CodegenContext)
private fun processQuery(queryTypeDef: ObjectTypeDefinition) {
    queryTypeDef.generateAllResolvers(queryTypeName).write()
}

context(CodegenContext)
private fun processSubscription(subscriptionTypeDef: ObjectTypeDefinition) {
    subscriptionTypeDef.generateAllResolvers(subscriptionTypeName).write()
}

context(CodegenContext)
private fun processType(typeDef: ObjectTypeDefinition) {
    typeDef.generateFile().write()

    val typeClass = ClassName(packageNameTypes, typeDef.name)
    typeDef.generateAllResolvers(typeClass).write()
}

context(CodegenContext)
private fun processEnumType(typeDef: EnumTypeDefinition) {
    typeDef.generateFile().write()
}

context(CodegenContext)
private fun processInputType(typeDef: InputObjectTypeDefinition) {
    typeDef.generateFile().write()
}

val mutationTypeName = GraphQLTypes.Mutation::class.asClassName()
val queryTypeName = GraphQLTypes.Query::class.asClassName()
val subscriptionTypeName = GraphQLTypes.Subscription::class.asClassName()
