package com.mshdabiola.worker.util

import com.mshdabiola.model.DrawPath
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converter {
    fun pathToString(paths: List<DrawPath>): String {

        val drawPathPojoList = paths.map { it.toDrawPathPojo() }
        return Json.encodeToString(drawPathPojoList)

    }
}