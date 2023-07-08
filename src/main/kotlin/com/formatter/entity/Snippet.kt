package com.formatter.entity

import java.time.LocalDateTime
import java.util.*

enum class SnippetType {
    PRINTSCRIPT
}

data class Snippet(
    val id: String = "",

    val userId: String = "",

    var content: String = "",
)
