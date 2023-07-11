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

data class RuleValue (
    val rule: Rule,
    val value: String,
)

@Entity
@Table(
    name = "format_rules",
    uniqueConstraints = [UniqueConstraint(columnNames = ["userId", "rule"])]
)
@EntityListeners(AuditingEntityListener::class)
data class FormatRule(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val userId: String = "",

    @Column(nullable = false)
    val rule: Rule = Rule.NIL,

    @Column(nullable = true)
    val value: String = "",
)