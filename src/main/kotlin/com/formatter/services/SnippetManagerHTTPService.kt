package com.formatter.services

import com.formatter.entity.Snippet
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL

@Service
class SnippetManagerHTTPService {
    @Value("\${snippet-manager.url}")
    private lateinit var baseUrl: String

    fun getSnippet(snippetId: String, accessToken: String): Snippet {
        val url = URL(baseUrl + snippetId)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        val jsonResponse = JSONObject(response)
        val id = jsonResponse.getString("id")
        val content = jsonResponse.getString("content")
        val userId = jsonResponse.getString("userId")
        return Snippet(id, userId, content)
    }

    fun saveSnippet(snippet: Snippet, accessToken: String) {
        val content: String = snippet.content
        val url = baseUrl + snippet.id
        val requestBody = JSONObject()
        requestBody.put("content", content)
        requestBody.put("compliance", "PENDING")
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.outputStream.use { os ->
            val input = requestBody.toString().toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }
        connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
    }
}