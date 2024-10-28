package com.mshdabiola.model

data class NotePad(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val focus: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0,
    val editDateString: String = "Jul 3",
    val reminderString: String = "feb 1",
    val noteType: NoteType = NoteType.NOTE,
    val selected: Boolean = false,
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteCheck> = emptyList(),
    val labels: List<Label> = emptyList(),
    val uris: List<NoteUri> = emptyList(),

) {
    fun toString2(): String {
        val checkString = checks.joinToString(separator = "\n") {
            if (it.isCheck) {
                "[*] ${it.content}"
            } else {
                "[ ] ${it.content}"
            }
        }
        return if (checkString.isNotBlank()) {
            "$title \n $checkString"
        } else {
            "$title \n $detail"
        }
    }

    override fun toString(): String {
        return """
            $title
            $detail
            ${checks.joinToString(separator = " ")}
        """.trimIndent()
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && !emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }
}
