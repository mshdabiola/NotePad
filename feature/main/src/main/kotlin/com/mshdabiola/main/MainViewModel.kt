package com.mshdabiola.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class MainViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notepadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    private val setOfSelected = MutableStateFlow<Set<Long>>(setOf())
    private val notificationUiState = MutableStateFlow<NotificationUiState?>(null)
    private val currentNotepads = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }
        .flatMapLatest {
            notepadRepository.getNotePadsWithMainData(it)
        }
    val mainState = combine(
        currentNotepads,
        userDataRepository.userData.mapLatest { it.noteDisplayCategory },
        setOfSelected,
        notificationUiState,
    ) { notepad, mainData, setOfSelected, notificationUiState ->

        MainState.Success(
            notePads = notepad,
            noteDisplayCategory = mainData,
            setOfSelected = setOfSelected,
            notificationUiState = notificationUiState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MainState.Loading,
    )

    val searchQuery = TextFieldState()
    private val searchTriple = MutableStateFlow<
        Triple<
            List<SearchSort.Type>,
            List<SearchSort.Color>,
            List<SearchSort.Label>,
            >,
        >(Triple(emptyList(), emptyList(), emptyList()))
    private val searchSort = MutableStateFlow<SearchSort?>(null)
    private var isTextAfterSearchSort = false

    val searchState = combine(
        snapshotFlow { searchQuery.text }
            .debounce(200),
        notepadRepository.getNotePads(),
        searchTriple,
        searchSort,

    ) { query, notepads, triple, searchSort ->
        val old = SearchState.Success(
            searches = notepads,
            types = triple.first,
            color = triple.second,
            label = triple.third,
            searchSort = searchSort,
        )

        val searchList = onSearch(old)

        SearchState.Success(
            searches = searchList,
            types = triple.first,
            color = triple.second,
            label = triple.third,
            searchSort = searchSort,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SearchState.Loading,
        )

    private fun onSearch(mainState: SearchState.Success): List<NotePad> {
        return when {
            mainState.searchSort != null -> {
                var list = when (val searchSort = mainState.searchSort) {
                    is SearchSort.Color -> {
                        mainState.searches.filter { it.color == searchSort.colorIndex }
                    }

                    is SearchSort.Label -> {
                        mainState.searches.filter { it.labels.any { it.id == searchSort.id } }
                    }

                    is SearchSort.Type -> {
                        when (searchSort.index) {
                            0 -> mainState.searches.filter { it.notification != null }
                            1 -> mainState.searches.filter { it.isCheck }
                            2 -> mainState.searches.filter { it.images.isNotEmpty() }
                            3 -> mainState.searches.filter { it.voices.isNotEmpty() }
                            4 -> mainState.searches.filter { it.images.any { it.isDrawing } }
                            5 -> mainState.searches.filter { it.uris.isNotEmpty() }
                            else -> mainState.searches
                        }
                    }

                    null -> TODO()
                }

                if (searchQuery.text.isNotBlank()) {
                    isTextAfterSearchSort = true

                    list = list.filter {
                        it.toString().contains(
                            searchQuery.text,
                            true,
                        )
                    }
                }

                if (isTextAfterSearchSort && searchQuery.text.isBlank()) {
                    isTextAfterSearchSort = false
                    onSetSearch(null)
                }

                list
            }

            searchQuery.text.isNotBlank() -> {
                val list = mainState.searches.filter {
                    it.toString().contains(searchQuery.text, true)
                }

                list
            }

            else -> emptyList()
        }
    }

    fun onExpandSearch(isExpand: Boolean) {
        viewModelScope.launch {
            searchTriple.update {
                if (!isExpand) {
                    Triple(
                        first = emptyList(),
                        second = emptyList(),
                        third = emptyList(),
                    )
                } else {
                    val notes = notepadRepository.getNotePads().first()

                    val labels = notes.asSequence().filter { it.labels.isEmpty().not() }
                        .map { it.labels }
                        .flatten()
                        .distinct()
                        .map { SearchSort.Label(it.label, 6, it.id) }.toList()

                    val colors = notes.asSequence()
                        .map { it.color }
                        .distinct()
                        .map { SearchSort.Color(it) }.toList()

                    val type = ArrayList<SearchSort.Type>(6)
                    if (notes.any { it.notification != null }) {
                        type.add(SearchSort.Type(0))
                    }
                    if (notes.any { it.isCheck }) {
                        type.add(SearchSort.Type(1))
                    }
                    if (notes.any { it.images.isNotEmpty() }) {
                        type.add(SearchSort.Type(2))
                    }
                    if (notes.any { it.voices.isNotEmpty() }) {
                        type.add(SearchSort.Type(3))
                    }

                    if (notes.any { it.images.any { it.isDrawing } }) {
                        type.add(SearchSort.Type(4))
                    }

                    if (notes.any { it.uris.isNotEmpty() }) {
                        type.add(SearchSort.Type(5))
                    }

                    Triple(
                        first = type,
                        second = colors,
                        third = labels,
                    )
                }
            }
        }
    }

    fun onSetSearch(searchSort: SearchSort?) {
        this.searchSort.value = searchSort
    }

    // Todo("if one is selected and is having alarm")
    fun onSelectCard(id: Long) {
        val selected = getSuccess().setOfSelected
        if (selected.contains(id)) {
            setOfSelected.value = selected - id
        } else {
            setOfSelected.value = selected + id
        }
    }

    fun clearSelected() {
        setOfSelected.value = emptySet()
    }

    fun setPin() {
        val selected = getSuccess().setOfSelected
        val selectedNotepad =
            getSuccess().notePads.filter { selected.contains(it.id) }

        clearSelected()

        if (selectedNotepad.any { !it.isPin }) {
            val pinNotepad = selectedNotepad.map { it.copy(isPin = true) }

            viewModelScope.launch {
                notepadRepository.upsert(pinNotepad)
            }
        } else {
            val unPinNote = selectedNotepad.map { it.copy(isPin = false) }

            viewModelScope.launch {
                notepadRepository.upsert(unPinNote)
            }
        }
    }

    fun setAllColor(colorId: Int) {
        val selected = getSuccess().setOfSelected
        val selectedNotes =
            getSuccess().notePads.filter { selected.contains(it.id) }

        clearSelected()
        val notes = selectedNotes.map { it.copy(color = colorId) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun setAllArchive() {
        val selected = getSuccess().setOfSelected
        val selectedNotes =
            getSuccess().notePads.filter { selected.contains(it.id) }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.ARCHIVE) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun setAllToTrash() {
        val selected = getSuccess().setOfSelected
        val selectedNotes =
            getSuccess().notePads.filter { selected.contains(it.id) }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.TRASH) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun copyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = getSuccess().setOfSelected.first()
            val notepads = notepadRepository.getOneNotePad(id).first()

            if (notepads != null) {
                val copy = notepads.copy(id = -1)

                notepadRepository.upsert(copy)
            }
        }
    }

    fun deleteLabel() {
//        val labelId = (getSuccess().noteType).id
//
//        _mainState.value = getSuccess().copy(noteType = NoteTypeUi())
//
//        viewModelScope.launch {
//            labelRepository.delete(labelId)
//            // noteLabelRepository.deleteByLabelId(labelId)
//        }
    }

    fun renameLabel(name: String) {
//        val labelId = (getSuccess().noteType).id
//
//        viewModelScope.launch {
//            labelRepository.upsert(listOf(Label(labelId, name)))
//        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            notepadRepository.deleteTrashType()
        }
    }

    fun deleteEmptyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val emptyList = notepadRepository.getNotePads().first()
                .filter { it.isEmpty() }

            if (emptyList.isNotEmpty()) {
                notepadRepository.deleteNotePad(emptyList)
            }
        }
    }

    fun setAlarm(notificationUiState: NotificationUiState) {
//        val time = timeListDefault[dateTimeState.value.currentTime]
//        val date = when (dateTimeState.value.currentDate) {
//            0 -> today.date
//            1 -> today.date.plus(1, DateTimeUnit.DAY)
//            else -> currentLocalDate
//        }
//        val interval = when (dateTimeState.value.currentInterval) {
//            0 -> null
//            1 -> DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS)
//
//            2 -> DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS)
//
//            3 -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)
//
//            else -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)
//        }
//
//        val setime = LocalDateTime(date, time)
//        if (setime > today) {
//            setAlarm(
//                setime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
//                interval,
//            )
//            Log.e("editv", "Set Alarm")
//        } else {
//            Log.e("editv", "Alarm not set $today time $time date$date")
//        }
    }

    private fun setAlarm(time: Long, interval: Long?) {
        val setOfSelected = getSuccess().setOfSelected
        val selectedNotes =
            getSuccess().notePads.filter { setOfSelected.contains(it.id) }

        clearSelected()
        val notes = selectedNotes // .map { it.copy(reminder = time, interval = interval ?: -1) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.setAlarm(
                    time,
                    interval,
                    requestCode = it.id?.toInt() ?: -1,
                    title = it.title,
                    content = it.detail,
                    noteId = it.id ?: 0L,
                )
            }
        }
    }

    fun deleteAlarm() {
        val selected = getSuccess().setOfSelected
        val selectedNotes =
            getSuccess().notePads.filter { selected.contains(it.id) }

        clearSelected()
        val notes = selectedNotes // .map { it.copy(reminder = -1, interval = -1) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.deleteAlarm(it.id?.toInt() ?: 0)
            }
        }
    }

    private fun getSuccess() = mainState.value as MainState.Success
}
