package com.formatter

import com.formatter.dto.CreateRulesDTO
import com.formatter.dto.RuleValue
import com.formatter.entity.Snippet
import com.formatter.services.FormatService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class FormatterApplication

fun main(args: Array<String>) {
    runApplication<FormatterApplication>(*args)
}
@RestController
class MessageController(
    private val formatService: FormatService) {
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!"

    @PostMapping("/format/{snippetId}")
    fun format(@PathVariable snippetId: String, @RequestHeader("Authorization") authorization: String): Snippet {
        return formatService.format(snippetId, authorization.substring(7))
    }

    @PostMapping("/rules/new")
    fun addRule(@RequestBody createRulesDTO: CreateRulesDTO, authentication: Authentication) {
        formatService.addRules(createRulesDTO, getAuth0Id(authentication))
    }

    @PostMapping("/rules/remove")
    fun removeRules(@RequestBody createRulesDTO: CreateRulesDTO, authentication: Authentication) {
        formatService.removeRules(createRulesDTO, getAuth0Id(authentication))
    }

    @GetMapping("/rules/me")
    fun getMyRules(authentication: Authentication): List<RuleValue> {
        return formatService.getUserRules(getAuth0Id(authentication))
    }

    private fun getAuth0Id(authentication: Authentication): String {
        return (authentication.principal as Jwt).subject
    }
}