package com.arianegraphql.codegen.options

import kotlinx.serialization.Serializable

@Serializable
data class CodegenOptions(
    val scalars: Map<String, String>? = null,
    val packageName: String? = null,
) {

    companion object {
        val DefaultOptions = CodegenOptions()
    }
}