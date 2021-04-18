package com.arianegraphql.server.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

fun setLoggerLevel(loggerName: String, level: Level) {
    (LoggerFactory.getLogger(loggerName) as? Logger)?.let { logger -> logger.level = level }
}