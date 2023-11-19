package com.arianegraphql.dsl

import io.ktor.server.application.Application

interface KtorPlugins {
    fun invoke(application: Application)
}

@JvmInline
value class FunctionalKtorPlugins(
    private val receiver: Application.() -> Unit
) : KtorPlugins {

    override fun invoke(application: Application) = receiver(application)
}