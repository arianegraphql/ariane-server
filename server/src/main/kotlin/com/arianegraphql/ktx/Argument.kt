package com.arianegraphql.ktx

interface Argument {
    fun contains(name: String): Boolean

    operator fun <T> get(name: String): T

    fun <T> getOrDefault(name: String, default: T): T
}
