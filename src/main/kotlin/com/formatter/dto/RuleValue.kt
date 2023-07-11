package com.formatter.dto

import com.formatter.entity.Rule

data class RuleValue (
    val rule: Rule,
    val value: String = "",
)