package com.arianegraphql.codegen.options

import kotlinx.serialization.Serializable

@Serializable
data class CodegenOptions(
    val scalars: Map<String, String>? = null,
    val packageName: String? = null,
    val typeSuffix: String? = null,
    val typePrefix: String? = null,
) {

    companion object {
        val DefaultOptions = CodegenOptions()
    }
}