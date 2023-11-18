package com.arianegraphql.codegen.ktx

import java.io.File

fun File.clear() {
    listFiles()?.forEach {
        it.clear()
        it.delete()
    }
}