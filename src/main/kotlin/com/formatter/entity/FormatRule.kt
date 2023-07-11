package com.formatter.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

enum class Rule {
    SPACE_AFTER_COLON,
    SPACE_BEFORE_COLON,
    SPACE_AROUND_EQUALS,
    NEW_LINE_BEFORE_METHOD,
    INDENT,
    NIL,
}

@Entity
@Table(
    name = "format_rule",
    uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("userId", "rule"))]
)
@EntityListeners(AuditingEntityListener::class)
data class FormatRule(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val rule: Rule = Rule.NIL,

    @Column(nullable = true)
    val value: String = "",
)