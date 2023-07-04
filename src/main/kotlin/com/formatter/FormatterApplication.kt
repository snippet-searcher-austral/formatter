package com.formatter

import com.formatter.entity.Snippet
import com.formatter.services.FormatService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FormatterApplication

fun main(args: Array<String>) {
    runApplication<FormatterApplication>(*args)
}
@RestController
class MessageController(
    private val formatService: FormatService, ) {
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!"

    @GetMapping("/format")
    fun format(@RequestBody snippet: Snippet): Snippet {
        return formatService.format(snippet)
    }
}