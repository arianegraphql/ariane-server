package com.arianegraphql.server.listener

interface ServerListener {

    /**
     * Method called when the server has been started and launched.
     */
    fun onStart(host: String, port: Int, path: String) {
    }

    /**
     * Method called when the server has been stopped.
     */
    fun onStop() {
    }
}