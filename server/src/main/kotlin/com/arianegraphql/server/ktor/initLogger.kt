package com.arianegraphql.server.ktor

import ch.qos.logback.classic.Level
import com.arianegraphql.server.logging.setLoggerLevel

fun initLoggers() {
    setLoggerLevel("graphql", Level.WARN)
    setLoggerLevel("notprivacysafe.graphql", Level.WARN)
    setLoggerLevel("ktor.application", Level.WARN)
}