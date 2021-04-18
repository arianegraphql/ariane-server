package com.arianegraphql.server.request

data class WebSocketPayload(
    val type: String,
    val id: String?,
    val payload: Any?
)

val WebSocketPayload.isConnectionInit get() =  type == WS_CLIENT_INIT
val WebSocketPayload.isStart get() = type == WS_CLIENT_START
val WebSocketPayload.isStop get() = type == WS_CLIENT_STOP
val WebSocketPayload.isConnectionTerminate get() = type == WS_CLIENT_CONNECTION_TERMINATE

const val WS_CLIENT_INIT = "connection_init"
const val WS_CLIENT_START = "start"
const val WS_CLIENT_STOP = "stop"
const val WS_CLIENT_CONNECTION_TERMINATE = "connection_terminate"

const val WS_SERVER_CONNECTION_ACK = "connection_ack"
const val WS_SERVER_CONNECTION_ERROR = "connection_error"
const val WS_SERVER_KEEP_ACTIVE = "ka"
const val WS_SERVER_DATA = "data"
const val WS_SERVER_ERROR = "error"
const val WS_SERVER_COMPLETE = "complete"

val connectionError = WebSocketPayload(WS_SERVER_CONNECTION_ERROR, null, null)
val connectionAck = WebSocketPayload(WS_SERVER_CONNECTION_ACK, null, null)

fun connectionComplete(operationId: String) = WebSocketPayload(WS_SERVER_COMPLETE, operationId, null)