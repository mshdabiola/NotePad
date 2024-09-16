package com.mshdabiola.drawing

// import com.mshdabiola.worker.Saver
// import com.mshdabiola.ui.util.Converter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.PathData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentManager: IContentManager,
    private val drawingPathRepository: IDrawingPathRepository,
) : ViewModel() {

    val imageID = savedStateHandle.toRoute<DrawingArgs>().imageId

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
            val drawPaths = drawingPathRepository.getAll(imageID).firstOrNull()
            drawPaths?.let {
                val map = toPathMap(it)
                controller.setPathData(map)
            }
        }
    }

    fun saveData() {
        // Saver.saveGame(imageId = imageID, noteId = noteId)
    }

    fun deleteImage() {
        viewModelScope.launch(Dispatchers.IO) {
//            noteImageRepository.delete(imageID)
//            noteImageRepository.delete(imageID)
            File(contentManager.getImagePath(imageID)).deleteOnExit()
        }
    }

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
    private fun toPathMap(list: List<DrawPath>): Map<PathData, List<Coordinate>> {
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
}
