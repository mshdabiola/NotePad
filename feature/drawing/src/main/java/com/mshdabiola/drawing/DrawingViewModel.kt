package com.mshdabiola.drawing

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.INoteDrawingRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.NoteVisual
import com.mshdabiola.ui.DrawingController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val drawingRepository: INoteDrawingRepository,
    private val notepadRepository: INotePadRepository,

) : ViewModel() {

    private val detailArgs = MutableStateFlow(savedStateHandle.toRoute<DrawingArgs>())

    val controller = DrawingController()

    private var isInit = false

    @OptIn(FlowPreview::class)
    val drawingState = combine(
        snapshotFlow { controller.drawingPaths }
            .debounce(500)
            .distinctUntilChanged(),
        detailArgs,
    ) { drawingPaths, i ->

        if (!isInit) {

            if (i.id != null) {
                val path = drawingRepository.get(i.id)
                    .first()
                    ?.drawingPaths
                val drawingPathsMutableList = controller.drawingPaths.toMutableList()
                drawingPathsMutableList.addAll(path!!)
                controller.drawingPaths = drawingPathsMutableList
            } else {
                val id = drawingRepository.insert(
                    NoteVisual.NoteDrawing(
                        id = -1,
                        drawingPaths = drawingPaths,
                        noteId = detailArgs.value.noteId,
                    ),
                )
                detailArgs.update {
                    it.copy(id = id)
                }
            }
            isInit = true
        } else {

            drawingRepository.insert(
                NoteVisual.NoteDrawing(
                    id = detailArgs.value.id!!,
                    drawingPaths = drawingPaths,
                    noteId = detailArgs.value.noteId,
                ),
            )
            println("insert $drawingPaths")
        }

        DrawingUiState(
            drawings = drawingPaths,
        )
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
        drawingRepository.delete(detailArgs.value.id!!)
    }
}
