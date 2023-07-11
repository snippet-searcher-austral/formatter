package com.formatter.services

import FormatterImpl
import com.formatter.entity.FormatRule
import com.formatter.entity.Snippet
import com.formatter.dto.CreateRulesDTO
import com.formatter.dto.RuleValue
import com.formatter.entity.Rule

import com.formatter.repository.FormatRulesRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import prinscript.language.formatter.*
import printscript.language.lexer.LexerFactory
import printscript.language.lexer.TokenListIterator
import printscript.language.parser.ASTIterator
import printscript.language.parser.ParserFactory
import java.io.File
import java.io.InputStream
import java.nio.file.Files


@Service
class FormatService(private val formatRulesRepository: FormatRulesRepository, private val snippetManagerHTTPService: SnippetManagerHTTPService) {
     fun format(snippetId: String, accessToken: String): Snippet {
         try {
             val snippet= snippetManagerHTTPService.getSnippet(snippetId, accessToken)
             snippet.content = formatContent(snippet)
             snippetManagerHTTPService.saveSnippet(snippet,accessToken)
             return snippet
         }catch (e: Exception) {
             throw ResponseStatusException(HttpStatus.BAD_REQUEST, "${e.message}")
         }
     }

    fun formatContent(snippet:Snippet): String {
        val fileContent = snippet.content.byteInputStream()
        val astIterator = getAstIterator(fileContent, "1.1")
        val tempFile = File.createTempFile("formatted", ".txt")
        val userRules = getUserRules(snippet.userId)
        val formattingRules = userRules.map { ruleEnumToClass(it.rule, it.value) }
        val formatter = FormatterImpl(tempFile.path, formattingRules)
        val formattedString = StringBuilder()
        while (astIterator.hasNext()) {
            formattedString.append(formatter.format(astIterator.next() ?: return ""))
        }
        val formattedContent = Files.readString(tempFile.toPath())
        tempFile.delete()
        return formattedContent
    }

    fun addRules(rules: CreateRulesDTO, userId: String) {
        rules.formatRules.forEach {
            formatRulesRepository.save(
                FormatRule(
                    userId = userId,
                    rule = it.rule,
                    value = it.value,
                )
            )
        }
    }

    fun removeRules(rules: CreateRulesDTO, userId: String) {
        rules.formatRules.forEach {
            formatRulesRepository.delete(
                FormatRule(
                    userId = userId,
                    rule = it.rule,
                    value = it.value,
                )
            )
        }
    }

    fun getUserRules(userId: String): List<RuleValue> {
        val formatRules: List<FormatRule> = formatRulesRepository.findByUserId(userId)
        return formatRules.map { RuleValue(it.rule, it.value) }
    }



    private fun getAstIterator(fileContent: InputStream, version: String): ASTIterator {
        val lexer = LexerFactory().createLexer(version, fileContent)
        val tokenListIterator = TokenListIterator(lexer)
        val parser = ParserFactory().createParser(version)
        return ASTIterator(parser, tokenListIterator)
    }

    private fun ruleEnumToClass(rule: Rule, value: String = ""): FormattingRule {
        return when (rule) {
            Rule.SPACE_AFTER_COLON -> SpaceAfterColon()
            Rule.SPACE_BEFORE_COLON -> SpaceBeforeColon()
            Rule.SPACE_AROUND_EQUALS -> SpaceAroundEquals()
            Rule.NEW_LINE_BEFORE_METHOD -> NewLineBeforeMethod()
            Rule.INDENT -> IndentSize(value.toInt())
            else -> throw Exception("Rule not found")
        }
    }
}