package com.formatter.services

import FormatterImpl
import com.formatter.entity.Snippet
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import printscript.language.lexer.LexerFactory
import printscript.language.lexer.TokenListIterator
import printscript.language.parser.ASTIterator
import printscript.language.parser.ParserFactory
import java.io.File
import java.io.InputStream
import java.nio.file.Files

@Service
class FormatService() {
     fun format(snippet: Snippet): Snippet {
         try {
             snippet.content = formatString(snippet.content, "1.1")
             return snippet
         }catch (e: Exception) {
             throw ResponseStatusException(HttpStatus.BAD_REQUEST, "${e.message}")
         }
     }
    fun formatString(input: String, version: String): String {
        val fileContent = input.byteInputStream()
        val astIterator = getAstIterator(fileContent, version)
        val tempFile = File.createTempFile("formatted", ".txt")
        val formatter = FormatterImpl(tempFile.path)
        val formattedString = StringBuilder()
        while (astIterator.hasNext()) {
            formattedString.append(formatter.format(astIterator.next() ?: return ""))
        }
        val formattedContent = Files.readString(tempFile.toPath())
        tempFile.delete()
        return formattedContent
    }

    private fun getAstIterator(fileContent: InputStream, version: String): ASTIterator {
        val lexer = LexerFactory().createLexer(version, fileContent)
        val tokenListIterator = TokenListIterator(lexer)
        val parser = ParserFactory().createParser(version)
        return ASTIterator(parser, tokenListIterator)
    }

}