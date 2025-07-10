/*
 *abiola 2024
 */

package com.mshdabiola.datastore

import androidx.datastore.core.okio.OkioSerializer
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import javax.inject.Inject

val json = Json

class UserDataJsonSerializer @Inject constructor() : OkioSerializer<UserData> {
    override val defaultValue: UserData
        get() =
            UserData(
                themeBrand = ThemeBrand.DEFAULT,
                darkThemeConfig = DarkThemeConfig.LIGHT,
                useDynamicColor = false,
                shouldHideOnboarding = false,
                contrast = Contrast.Normal,
                noteDisplayCategory = NoteDisplayCategory(),
                isGrid = true,
            )

    override suspend fun readFrom(source: BufferedSource): UserData {
        return json.decodeFromString<UserData>(source.readUtf8())
    }

    override suspend fun writeTo(
        userData: UserData,
        sink: BufferedSink,
    ) {
        sink.use {
            it.writeUtf8(json.encodeToString(UserData.serializer(), userData))
        }
    }
}
