package com.mshdabiola.model

object DrawingUtil {
    fun toPathMap(list: List<DrawPath>): Map<PathData, List<Coordinate>> {
        val map = HashMap<PathData, List<Coordinate>>()
        list.forEach { drawPath ->
            val path = PathData(
                drawPath.color,
                drawPath.width,
                drawPath.cap,
                drawPath.join,
                drawPath.alpha,
                drawPath.pathId,
            )
            val offsetList = drawPath.paths
                .split(",")
                .map { it.trim().toFloat() }
                .chunked(2)
                .map { Coordinate(it[0], it[1]) }
            map[path] = offsetList
        }
        return map
    }


    private fun changeToDrawPath(
        imageID: Long,
        map: Map<PathData, List<Coordinate>>
    ): List<DrawPath> {
        return map.map { entry ->
            DrawPath(
                imageID,
                entry.key.id,
                entry.key.color,
                entry.key.lineWidth,
                entry.key.lineJoin,
                entry.key.colorAlpha,
                entry.key.lineCap,
                entry.value.joinToString { "${it.x}, ${it.y}" },
            )
        }
    }

}