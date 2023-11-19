package com.arianegraphql.dsl

import java.io.FileNotFoundException

fun loadSchema(path: String) = Thread.currentThread().contextClassLoader.getResource(path)?.readText()
    ?: throw FileNotFoundException("No file found at path $path")