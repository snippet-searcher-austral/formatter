package com.formatter.repository

import com.formatter.entity.FormatRule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormatRulesRepository: JpaRepository<FormatRule, UUID> {
    fun findByUserId(userId: String): List<FormatRule>

}