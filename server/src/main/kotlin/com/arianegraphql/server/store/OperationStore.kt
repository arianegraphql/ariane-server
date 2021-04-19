package com.arianegraphql.server.store

import kotlinx.coroutines.CoroutineScope

interface OperationStore {

    suspend fun startOperation(sessionId: String, operationId: String, block: suspend CoroutineScope.() -> Unit): Boolean

    suspend fun stopOperation(sessionId: String, operationId: String)

    suspend fun stopAllOperations(sessionId: String)

    suspend fun hasOperationForSessionId(sessionId: String): Boolean
}