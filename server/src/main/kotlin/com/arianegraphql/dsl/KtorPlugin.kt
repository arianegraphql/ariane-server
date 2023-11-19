package com.arianegraphql.dsl

import io.ktor.server.application.Application

interface KtorPlugin{
    fun invoke(application: Application)
}

@JvmInline value class FunctionalKtorPlugin(
    private val receiver: Application.() -> Unit
) : KtorPlugin {

    override fun invoke(application: Application) = receiver(application)
}