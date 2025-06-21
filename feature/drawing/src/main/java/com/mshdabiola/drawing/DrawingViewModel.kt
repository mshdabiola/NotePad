package com.mshdabiola.drawing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.PathData
import com.mshdabiola.worker.util.changeToPathAndData
import com.mshdabiola.worker.util.getBitMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentManager: IContentManager,
    private val drawingPathRepository: IDrawingPathRepository,
    private val notepadRepository: INotePadRepository,

) : ViewModel() {

    private val drawingArgs = savedStateHandle.toRoute<DrawingArgs>()
    private var imageID = drawingArgs.imageId

    val controller = DrawingController()

    val drawingPath = drawingPathRepository
        .getAll(imageID)
        .map { toPathMap(it) }
    private var isInit = false

    @OptIn(FlowPreview::class)
    val drawingState = combine(
        drawingPath,
        snapshotFlow { controller.completePathData.value }
            .debounce(500),
    ) { pathMapSource, pathDataScreen ->
        if (!isInit) {
            isInit = true
            controller.setPathData(pathMapSource)
        }
        val path = if (isInit && pathMapSource != pathDataScreen) {
            saveImage2(pathDataScreen).await()
        } else {
            null
        }
        DrawingUiState(
            filePath = path,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DrawingUiState(),
    )

    fun saveImage2(paths: ImmutablePath): Deferred<String?> {
        return viewModelScope.async {
            try {
                val pathsMap = changeToDrawPath(paths)

                // delete exist drawing from db
                drawingPathRepository.delete(imageID)
                if (pathsMap.isEmpty()) {
                    // delete image too
                    File(contentManager.getImagePath(imageID)).deleteOnExit()
                    null
                } else {
                    val width = drawingArgs.width
                    val height = drawingArgs.height
                    val density = drawingArgs.density

                    val bitmap = getBitMap(
                        changeToPathAndData(paths),
                        width,
                        height,
                        density,
                    )
                    val path = contentManager.getImagePath(imageID)
                    contentManager.saveBitmap(path, bitmap)

                    drawingPathRepository.insert(pathsMap)
                    path
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteImage() {
        withContext(Dispatchers.IO) {
            notepadRepository.deleteImageNote(imageID)

            File(contentManager.getImagePath(imageID)).deleteOnExit()
            drawingPathRepository.delete(imageID)
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
