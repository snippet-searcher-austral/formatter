package com.formatter.dto

import com.formatter.entity.Rule
import com.formatter.entity.RuleValue

data class CreateRulesDTO (
    val userId: String,
    val formatRules: List<RuleValue>,
)