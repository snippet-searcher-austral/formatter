package com.formatter.services

import FormatterImpl
import com.formatter.entity.Snippet
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import printscript.language.lexer.LexerFactory
import printscript.language.lexer.TokenListIterator
import printscript.language.parser.ASTIterator
import printscript.language.parser.ParserFactory
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files


@Service
class FormatService {
     fun format(snippetId: String): Snippet {
         try {
             val snippet= getSnippet(snippetId)
             snippet.content = formatContent(snippet)
                return saveSnippet(snippet)
         }catch (e: Exception) {
             throw ResponseStatusException(HttpStatus.BAD_REQUEST, "${e.message}")
         }
     }
    fun formatContent(snippet:Snippet): String {
        val fileContent = snippet.content.byteInputStream()
        val astIterator = getAstIterator(fileContent, "1.1")
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

    private fun getSnippet(snippetId: String): Snippet {
        val url = URL("https://snippet-searcher.southafricanorth.cloudapp.azure.com/snippet-manager/snippet/$snippetId")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        val jsonResponse = JSONObject(response)
        return jsonResponse as Snippet;
    }

    private fun saveSnippet(snippet: Snippet): Snippet {
        val content: String = snippet.content
        val url = "https://snippet-searcher.southafricanorth.cloudapp.azure.com/snippet-manager/snippet/${snippet.id}"
        val requestBody = JSONObject()
        requestBody.put("content", content)
        requestBody.put("compliance", "PENDING")

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.outputStream.use { os ->
            val input = requestBody.toString().toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }
        val code = connection.responseCode
        return JSONObject(code) as Snippet
    }
}