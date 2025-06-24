package com.mshdabiola.drawing

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.INoteDrawingRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.ui.DrawingController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val drawingRepository: INoteDrawingRepository,
    private val notepadRepository: INotePadRepository,

) : ViewModel() {

    private val drawingArgs = savedStateHandle.toRoute<DrawingArgs>()
    private var imageID = drawingArgs.imageId

    val controller = DrawingController()

    private var isInit = false

    @OptIn(FlowPreview::class)
    val drawingState = combine(
        snapshotFlow { controller.drawingPaths }
            .debounce(500),
        drawingRepository.get(imageID),
    ) { drawingPaths, initDrawingPath ->

        if (!isInit) {
            controller.drawingPaths.addAll(initDrawingPath.drawingPaths)
            isInit = true
        } else {
            drawingRepository.insert(
                initDrawingPath.copy(drawingPaths = drawingPaths),
            )
        }

        DrawingUiState(drawings = initDrawingPath.drawingPaths)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DrawingUiState(),
    )

//    fun saveImage2(paths: ImmutablePath): Deferred<String?> {
//        return viewModelScope.async {
//            try {
//                val pathsMap = changeToDrawPath(paths)
//
//                // delete exist drawing from db
//                drawingPathRepository.delete(imageID)
//                if (pathsMap.isEmpty()) {
//                    // delete image too
//                    File(contentManager.getImagePath(imageID)).deleteOnExit()
//                    null
//                } else {
//                    val width = drawingArgs.width
//                    val height = drawingArgs.height
//                    val density = drawingArgs.density
//
//                    val bitmap = getBitMap(
//                        changeToPathAndData(paths),
//                        width,
//                        height,
//                        density,
//                    )
//                    val path = contentManager.getImagePath(imageID)
//                    contentManager.saveBitmap(path, bitmap)
//
//                    drawingPathRepository.insert(pathsMap)
//                    path
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    suspend fun deleteImage() {
        drawingRepository.delete(imageID)
    }
}
