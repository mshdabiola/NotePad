package com.mshdabiola.labelscreen

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class LabelArg(val isEditMode: Boolean) : NavKey
