package com.mshdabiola.drawing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel()