package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.codegen.ktx.kotlinType
import com.arianegraphql.server.resolver.Resolver
import com.arianegraphql.ktx.TypeResolverBuilder
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.FieldDefinition
import graphql.language.ObjectTypeDefinition

context(ctx: CodegenContext)
fun ObjectTypeDefinition.generateAllResolvers(parent: ClassName, supportsBatch: Boolean = true): List<FileSpec> {
    val resolverFiles = mutableListOf<FileSpec>()

    fieldDefinitions.forEach {
        val returnType = it.type.kotlinType
        val argumentClassName = ClassName(ctx.packageNameResolvers, it.generatedArgumentClassName(parent))

        val builder = FileSpec.builder(ctx.packageNameResolvers, "${parent.simpleName}.${it.name}")

        it.generateArgument(parent, builder)
        it.generateResolverInterface(parent, argumentClassName, returnType, builder)
        it.generateFieldRefProperty(parent, argumentClassName, returnType, supportsBatch, builder)

        resolverFiles += builder.build()
    }

    return resolverFiles
}

fun FieldDefinition.generateResolverInterface(parent: ClassName, argument: ClassName, returnType: TypeName, fileSpecBuilder: FileSpec.Builder) {
    val interfaceName = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Resolver"

    val interfaceSuperType = Resolver::class.asTypeName().parameterizedBy(parent, argument, returnType)

    fileSpecBuilder.addType(TypeSpec.interfaceBuilder(interfaceName).addSuperinterface(interfaceSuperType).build())
}

/**
 * Generates the typed field reference for one field.
 *
 * The generated code is a single extension property returning a [com.arianegraphql.ktx.FieldRef]
 * (or [com.arianegraphql.ktx.RootFieldRef] for root types where batching makes no sense).
 * All registration behavior (plain resolvers, batch resolvers) lives on the reference
 * class in the core library, so generated code keeps the same shape when the resolver
 * API evolves.
 */
fun FieldDefinition.generateFieldRefProperty(
    parent: ClassName,
    argument: ClassName,
    returnType: TypeName,
    supportsBatch: Boolean,
    fileSpecBuilder: FileSpec.Builder,
) {
    val propertyReceiver = TypeResolverBuilder::class.asClassName()
        .parameterizedBy(parent)
        .copy(annotations = emptyList())

    val refClassName = ClassName(
        "com.arianegraphql.ktx",
        if (supportsBatch) "FieldRef" else "RootFieldRef"
    )
    val refType = refClassName.parameterizedBy(parent, argument, returnType)

    val kdoc = buildString {
        appendLine("Typed reference to the '$name' field of '${parent.simpleName}'.")
        appendLine()
        appendLine("Register a resolver with the invoke syntax:")
        appendLine("```")
        appendLine("$propertyName { argument -> ... }")
        appendLine("```")
        if (supportsBatch) {
            appendLine()
            appendLine("Or resolve occurrences in batch to solve N+1 queries:")
            appendLine("```")
            appendLine("$propertyName.batch(key = { _, source -> ... }) { keys -> ... }")
            appendLine("```")
        }
    }

    val property = PropertySpec.builder(propertyName, refType)
        .receiver(propertyReceiver)
        .addKdoc(kdoc)
        .getter(
            FunSpec.getterBuilder()
                .addStatement("return %T(this, %S, %T::class.java)", refClassName, name, argument)
                .build()
        )
        .build()

    fileSpecBuilder.addProperty(property)
}

/**
 * Field names that would clash with members of [TypeResolverBuilder] are escaped with a
 * `Field` suffix, since members always win over extension properties in Kotlin resolution.
 */
private val FieldDefinition.propertyName: String
    get() = if (name in RESERVED_BUILDER_MEMBERS) "${name}Field" else name

private val RESERVED_BUILDER_MEMBERS = setOf("resolve", "resolveBatch", "build", "typeResolvers")
