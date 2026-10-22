package com.example.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader

/**
 * Server-Sent Events (SSE) akışını okuyup token token yayınlar.
 * OpenAI uyumlu `stream=true` endpoint'leri için çalışır.
 * Her `data: {...}` satırından `choices[0].delta.content` çekilerek emit edilir.
 */
object SseStreamParser {

    data class StreamChunk(
        val token: String,
        val isDone: Boolean = false
    )

    /**
     * Verilen [request]'i stream olarak çalıştırır.
     * Her yeni token için [StreamChunk] emit eder, bitişte `isDone=true` bir son chunk yayınlar.
     */
    fun stream(client: OkHttpClient, request: Request): Flow<StreamChunk> = flow {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val err = response.body?.string() ?: "HTTP ${response.code}"
                throw IllegalStateException("SSE bağlantı hatası: $err")
            }

            val reader = BufferedReader(response.body!!.charStream())
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val raw = line!!.trim()
                if (raw.isEmpty()) continue
                if (!raw.startsWith("data:")) continue

                val payload = raw.removePrefix("data:").trim()
                if (payload == "[DONE]") {
                    emit(StreamChunk("", isDone = true))
                    break
                }

                try {
                    val json = JSONObject(payload)
                    val choices = json.optJSONArray("choices") ?: continue
                    if (choices.length() == 0) continue
                    val delta = choices.getJSONObject(0).optJSONObject("delta") ?: continue
                    val content = delta.optString("content", "")
                    if (content.isNotEmpty()) {
                        emit(StreamChunk(content))
                    }
                } catch (_: Exception) {
                    // Kötü format — atla
                }
            }
        }
    }

    /**
     * OpenAI uyumlu bir istek body'si oluşturur (stream=true eklenmiş).
     */
    fun buildStreamRequestBody(
        endpointUrl: String,
        apiKey: String,
        model: String,
        systemPrompt: String,
        messagesJson: org.json.JSONArray
    ): Request {
        val body = JSONObject().apply {
            put("model", model)
            put("messages", messagesJson)
            put("temperature", 0.85)
            put("stream", true)
        }

        val reqBody = body.toString()
            .toByteArray()
            .let { okhttp3.RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), it) }

        return Request.Builder()
            .url(endpointUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "text/event-stream")
            .post(reqBody)
            .build()
    }

    private fun String.toMediaTypeOrNull() =
        okhttp3.MediaType.parse(this)
}