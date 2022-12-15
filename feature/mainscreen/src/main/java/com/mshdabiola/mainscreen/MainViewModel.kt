package com.mshdabiola.mainscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.mainscreen.state.MainState
import com.mshdabiola.mainscreen.state.NoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {

        viewModelScope.launch {
            _mainState.value =
                mainState.value.copy(listOfNote = (1..30).map { NoteUiState() }.toImmutableList())
        }
    }

}