package com.mshdabiola.editscreen

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageToText @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun toText(string: String) :String{
        return ""
    }
}
