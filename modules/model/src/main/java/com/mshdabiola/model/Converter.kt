package com.mshdabiola.model

import kotlinx.serialization.json.Json

object Converter {
    val json = Json
    fun pathToString(paths: List<DrawingPath>): String {
        return json.encodeToString(paths)
    }

    fun toPath(string: String): List<DrawingPath> {
        return json.decodeFromString(string)
    }
}
