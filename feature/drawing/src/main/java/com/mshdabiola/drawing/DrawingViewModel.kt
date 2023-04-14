package com.mshdabiola.drawing

import android.graphics.Bitmap
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
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.DrawingUtil
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.PathData
import com.mshdabiola.worker.Saver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentManager: ContentManager,
    private val noteImageRepository: NoteImageRepository,
    private val drawingPathRepository: DrawingPathRepository,
) : ViewModel() {

    private val noteId = savedStateHandle.get<Long>(noteIdArg)!!
    private val imageI = savedStateHandle.get<Long>(imageIdArg)!!
    private val imageID = if (imageI == (-1L)) System.currentTimeMillis() else imageI

    var drawingUiState by mutableStateOf(
        DrawingUiState(
            filePath = contentManager.getImagePath(
                imageID,
            ),
        ),
    )
    val controller = DrawingController()

    init {
        viewModelScope.launch {
            if (imageI != (-1L)) {
                val drawPaths = drawingPathRepository.getAll(imageID).firstOrNull()
                drawPaths?.let {
                    val map = DrawingUtil. toPathMap(it)
                    controller.setPathData(map)
                }
            }
        }
    }

    fun saveData(){
        val data = changeToDrawPath(controller.completePathData.value)
        Saver.saveGame(paths = data, imageId = imageID, noteId = noteId)
    }

//    fun saveImage(bitmap: Bitmap) {
//        val path = contentManager.getImagePath(imageID)
//        contentManager.saveBitmap(path, bitmap)
//    }


    fun deleteImage() {
        viewModelScope.launch(Dispatchers.IO) {
            noteImageRepository.delete(imageID)
            noteImageRepository.delete(imageID)
            File(contentManager.getImagePath(imageID)).deleteOnExit()
        }
    }

    //  private var job: Job? = null
//    suspend fun saveDrawing(map: Map<PathData, List<Offset>>) {
//        val data = changeToDrawPath(map)
//        if (map.isEmpty()) {
//            drawingPathRepository.delete(imageID)
//            noteImageRepository.delete(imageID)
//            File(contentManager.getImagePath(imageID)).deleteOnExit()
//        } else {
//            noteImageRepository.upsert(NoteImage(imageID, noteId, drawingUiState.filePath, true))
//            drawingPathRepository.delete(imageID)
//            drawingPathRepository.insert(data)
//        }
//    }

    private fun changeToDrawPath(map: Map<PathData, List<Coordinate>>): List<DrawPath> {
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
//
//    private fun toPathMap(list: List<DrawPath>): Map<PathData, List<Offset>> {
//        val map = HashMap<PathData, List<Offset>>()
//        list.forEach { drawPath ->
//            val path = PathData(
//                drawPath.color,
//                drawPath.width,
//                drawPath.cap,
//                drawPath.join,
//                drawPath.alpha,
//                drawPath.pathId,
//            )
//            val offsetList = drawPath.paths
//                .split(",")
//                .map { it.trim().toFloat() }
//                .chunked(2)
//                .map { Offset(it[0], it[1]) }
//            map[path] = offsetList
//        }
//        return map
//    }
}
