package com.arianegraphql.server.listener

data class ServerListener(
    /**
     * Method called when the server has been started and launched.
     */
    val onStart: OnServerStarted,

    /**
     * Method called when the server has been stopped.
     */
    val onStop: OnServerStopped,
)

typealias OnServerStarted = (host: String, port: Int, path: String) -> Unit
typealias OnServerStopped = () -> Unit