package com.mshdabiola.database.model

import androidx.room.Entity
import com.mshdabiola.model.DrawPath

@Entity(tableName = "path_table", primaryKeys = ["imageId", "pathId"])
data class DrawPathEntity(
    val imageId: Long,
    val pathId: Int,
    val color: Int,
    val width: Int,
    val join: Int,
    val alpha: Float,
    val cap: Int,
    val paths: String
)

fun DrawPathEntity.toDrawPath() = DrawPath(imageId, pathId, color, width, join, alpha, cap, paths)
fun DrawPath.toDrawPathEntity() =
    DrawPathEntity(imageId, pathId, color, width, join, alpha, cap, paths)