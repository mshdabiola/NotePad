package com.mshdabiola.drawing

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.drawing.navigation.DrawingArgs
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.ui.DrawingController
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@HiltViewModel(assistedFactory = DrawingViewModel.Factory::class)
class DrawingViewModel @AssistedInject constructor(
    @Assisted val drawing: DrawingArgs,
    private val drawingRepository: NoteDrawingRepository,

) : ViewModel() {

    private val detailArgs = MutableStateFlow(drawing)

    val controller = DrawingController()

    private var isInit = false

    @OptIn(FlowPreview::class)
    val drawingState = combine(
        snapshotFlow { controller.drawingPaths.toList() }
            .debounce(500)
            .distinctUntilChanged(),
        detailArgs,
    ) { drawingPaths, i ->

        val state = when {
            !isInit && i.id != null -> {
                val path = drawingRepository.get(i.id)
                    .first()
                    ?.drawingPaths
                val drawingPathsMutableList = controller.drawingPaths.toMutableList()
                drawingPathsMutableList.addAll(path!!)
                controller.drawingPaths.addAll(drawingPathsMutableList)

                isInit = true
                DrawingUiState(
                    drawingId = i.id,
                    drawings = path,
                )
            }
            !isInit && i.id == null -> {
                val id = drawingRepository.upsert(
                    NoteDrawing(
                        id = -1,
                        drawingPaths = drawingPaths,
                        noteId = detailArgs.value.noteId,
                    ),
                )
                detailArgs.update {
                    it.copy(id = id)
                }

                isInit = true
                DrawingUiState(
                    drawingId = id,
                    drawings = emptyList(),
                )
            }
            else -> {
                drawingRepository.upsert(
                    NoteDrawing(
                        id = detailArgs.value.id!!,
                        drawingPaths = drawingPaths,
                        noteId = detailArgs.value.noteId,
                    ),
                )

                DrawingUiState(
                    drawingId = detailArgs.value.id,
                    drawings = drawingPaths,
                )
            }
        }

        state
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
//                // deleteByNoteId exist drawing from db
//                drawingPathRepository.deleteByNoteId(imageID)
//                if (pathsMap.isEmpty()) {
//                    // deleteByNoteId image too
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

    suspend fun deleteDrawing() {
        drawingRepository.delete(detailArgs.value.id!!)
    }

    @AssistedFactory
    interface Factory {
        fun create(drawingArgs: DrawingArgs): DrawingViewModel
    }
}
