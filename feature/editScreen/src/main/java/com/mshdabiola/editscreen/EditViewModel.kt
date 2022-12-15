package com.mshdabiola.editscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)

    init {
        viewModelScope.launch {
            Log.e("EditViewModel", "${editArg.id}")
        }
    }

}