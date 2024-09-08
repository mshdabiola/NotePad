package com.mshdabiola.model

enum class NoteType(val index: Long = 0) {
    NOTE(-1),
    ARCHIVE(-2),
    TRASH(-3),
    LABEL,
    REMAINDER(-4),
}
