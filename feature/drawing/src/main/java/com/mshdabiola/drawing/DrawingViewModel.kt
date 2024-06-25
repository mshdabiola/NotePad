package com.mshdabiola.drawing

// import com.mshdabiola.worker.Saver
// import com.mshdabiola.ui.util.Converter
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.DrawingPathRepository
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.DrawingUtil
import com.mshdabiola.model.PathData
import com.mshdabiola.ui.util.Converter
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

    val noteId = savedStateHandle.get<Long>(noteIdArg)!!
    private val imageI = savedStateHandle.get<Long>(imageIdArg)!!
    val imageID = if (imageI == (-1L)) System.currentTimeMillis() else imageI

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
                    val map = DrawingUtil.toPathMap(it)
                    controller.setPathData(map)
                }
            }
        }
    }

    fun saveData() {
        // Saver.saveGame(imageId = imageID, noteId = noteId)
    }

    fun keepDataInFile(da: Map<PathData, List<Coordinate>>) {
        val data = changeToDrawPath(da)
        val dataInText = Converter.pathToString(data)
        Log.e("drawing", dataInText)
        contentManager.dataFile(imageID).writeText(dataInText)
    }

    fun deleteImage() {
        viewModelScope.launch(Dispatchers.IO) {
            noteImageRepository.delete(imageID)
            noteImageRepository.delete(imageID)
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
}
