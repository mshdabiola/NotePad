package com.mshdabiola.drawing

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.DrawingPathRepository
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.NoteImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val contentManager: ContentManager,
    private val noteImageRepository: NoteImageRepository,
    private val drawingPathRepository: DrawingPathRepository
) : ViewModel() {

    val noteId = savedStateHandle.get<Long>(noteIdArg)!!
    val imageI = savedStateHandle.get<Long>(imageIdArg)!!
    val imageID = if (imageI == (-1L)) System.currentTimeMillis() else imageI

    var drawingUiState by mutableStateOf(
        DrawingUiState(
            filePath = contentManager.getImagePath(
                imageID
            )
        )
    )

    init {
        viewModelScope.launch {
            if (imageI != (-1L)) {
                val drawPaths = drawingPathRepository.getAll(imageID).firstOrNull()
                drawPaths?.let {
                    val map = toPathMap(it)
                    drawingUiState = drawingUiState.copy(paths = map.toImmutableMap())
                }
            }
        }

    }


    fun saveImage(bitmap: Bitmap, map: Map<PathData, List<Offset>>) {
        saveDrawing(map)
        viewModelScope.launch {


            val path = contentManager.getImagePath(imageID)

            noteImageRepository.upsert(NoteImage(imageID, noteId, path, true))

            contentManager.saveBitmap(path, bitmap)
        }
    }

    fun deleteImage() {
        viewModelScope.launch(Dispatchers.IO) {
            noteImageRepository.delete(imageID)
            File(contentManager.getImagePath(imageID)).delete()
        }

    }

    var job: Job? = null
    fun saveDrawing(map: Map<PathData, List<Offset>>) {
        val data = changeToDrawPath(map)
        Log.e("saveDrawing", data.joinToString())
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            if (map.isEmpty()) {
                drawingPathRepository.delete(imageID)
            } else {
                drawingPathRepository.delete(imageID)
                drawingPathRepository.insert(data)
            }
        }
    }


    fun changeToDrawPath(map: Map<PathData, List<Offset>>): List<DrawPath> {
        return map.map { entry ->
            DrawPath(
                imageID,
                entry.key.id,
                entry.key.color,
                entry.key.lineWidth,
                entry.key.lineJoin,
                entry.key.colorAlpha,
                entry.key.lineCap,
                entry.value.joinToString { "${it.x}, ${it.y}" })
        }
    }

    fun toPathMap(list: List<DrawPath>): Map<PathData, List<Offset>> {
        val map = HashMap<PathData, List<Offset>>()
        list.forEach { drawPath ->
            val path = PathData(
                drawPath.color,
                drawPath.width,
                drawPath.cap,
                drawPath.join,
                drawPath.alpha,
                drawPath.pathId
            )
            val list = drawPath.paths
                .split(",")
                .map { it.trim().toFloat() }
                .chunked(2)
                .map { Offset(it[0], it[1]) }
            map[path] = list
        }
        return map
    }


}