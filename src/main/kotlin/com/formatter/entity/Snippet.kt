package com.formatter.entity

import java.time.LocalDateTime
import java.util.*

enum class SnippetType {
    PRINTSCRIPT
}

data class Snippet(
    val id: UUID? = null,

    val userId: String = "",

    val name: String = "",

    val type: SnippetType = SnippetType.PRINTSCRIPT,

    var content: String = "",

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now()
)
