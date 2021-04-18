package com.arianegraphql.server.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

class OperationStoreImpl(
    private val operationStore: MutableMap<String, ConcurrentHashMap<String, CoroutineScope>> = ConcurrentHashMap<String, ConcurrentHashMap<String, CoroutineScope>>()
) : OperationStore {

    override suspend fun startOperation(sessionId: String, operationId: String, block: suspend CoroutineScope.() -> Unit): Boolean {
        val activeOperation = operationStore.getOrPut(sessionId) { ConcurrentHashMap() }

        var exist = true
        activeOperation.getOrPut(operationId) {
            exist = false
            coroutineScope {
                block(this)
                this
            }
        }

        return exist
    }

    override suspend fun stopOperation(sessionId: String, operationId: String) {
        operationStore[sessionId]?.get(operationId)?.cancel()
    }

    override suspend fun stopAllOperations(sessionId: String) {
        operationStore[sessionId]?.values?.forEach { it.cancel() }
    }
}