package com.mshdabiola.domain

import androidx.core.net.toUri
import com.mshdabiola.model.NoteUri
import javax.inject.Inject

private val regex =
    "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

class LinkUriUseCase @Inject constructor() {
    operator fun invoke(detail: String, take: Int): List<NoteUri> {
        return if (detail.contains(regex.toRegex())) {
            detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = s.toUri().authority ?: ""
                    val icon = "https://icon.horse/icon/$path"
                    NoteUri(
                        id = index,
                        icon = icon,
                        path = path,
                        uri = s,
                    )
                }
                .take(take)
        } else {
            emptyList()
        }
    }
}
