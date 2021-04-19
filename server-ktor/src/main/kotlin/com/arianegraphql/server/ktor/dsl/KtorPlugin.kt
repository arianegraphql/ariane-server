package com.arianegraphql.server.ktor.dsl

import io.ktor.application.*

interface KtorPlugin{
    fun invoke(application: Application)
}

@JvmInline value class FunctionalKtorPlugin(
    private val receiver: Application.() -> Unit
) : KtorPlugin {

    override fun invoke(application: Application) = receiver(application)
}