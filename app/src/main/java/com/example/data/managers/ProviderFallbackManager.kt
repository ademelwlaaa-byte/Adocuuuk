package com.example.data.managers

import com.example.data.api.LLMAdapterFactory
import com.example.data.api.MemoryToolDefinition
import com.example.data.api.MemoryToolRegistry
import com.example.data.api.ModelResponseResult
import com.example.data.api.RetrofitClient
import com.example.data.local.CustomProviderDao
import com.example.data.local.CustomProviderEntity
import com.example.data.local.MessageEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.EmochiRepository
import com.example.data.repository.ProviderFallbackLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ProviderFallbackManager(
    private val customProviderDao: CustomProviderDao,
    private val onLogFallback: (ProviderFallbackLogEntry) -> Unit
) {
    fun buildProviderChain(
        settings: UserSettingsEntity,
        customProviders: Map<Long, CustomProviderEntity>,
        buildConfigGeminiKey: String
    ): List<String> {
        val primary = settings.selectedProvider.lowercase().trim()
        val ordered = linkedSetOf<String>()

        fun isConfigured(providerKey: String): Boolean = when (providerKey) {
            "gemini" -> settings.customApiKey.isNotBlank() ||
                (buildConfigGeminiKey.isNotBlank() && buildConfigGeminiKey != "MY_GEMINI_API_KEY")
            "groq" -> settings.groqApiKey.isNotBlank() || settings.customApiKey.isNotBlank()
            "openai" -> settings.openaiApiKey.isNotBlank()
            "claude" -> settings.claudeApiKey.isNotBlank()
            "openrouter" -> settings.openRouterApiKey.isNotBlank()
            "nvidia" -> settings.nvidiaApiKey.isNotBlank()
            "mistral" -> settings.mistralApiKey.isNotBlank()
            "llm7" -> settings.enableLlm7 && settings.llm7Enabled
            "pollinations" -> settings.enablePollinations && settings.pollinationsEnabled
            "ovh" -> settings.enableOvh && settings.ovhEnabled &&
                System.currentTimeMillis() > EmochiRepository.ovhCooldownUntilTimestamp
            else -> if (providerKey.startsWith("custom_")) {
                val id = providerKey.removePrefix("custom_").toLongOrNull()
                id != null && customProviders.containsKey(id)
            } else false
        }

        if (isConfigured(primary)) {
            ordered.add(primary)
        }
        val candidates = listOf(
            "groq",
            "gemini",
            "openai",
            "claude",
            "openrouter",
            "nvidia",
            "mistral",
            "llm7",
            "pollinations",
            "ovh"
        )
        for (c in candidates) {
            if (isConfigured(c)) ordered.add(c)
        }
        for (customId in customProviders.keys) {
            ordered.add("custom_$customId")
        }
        if (ordered.isEmpty()) {
            ordered.add("pollinations")
            ordered.add("llm7")
        }
        return ordered.toList()
    }

    suspend fun executeWithFallback(
        model: String,
        settings: UserSettingsEntity,
        systemPrompt: String,
        messages: List<MessageEntity>,
        buildConfigGeminiKey: String,
        tools: List<MemoryToolDefinition> = emptyList()
    ): ModelResponseResult = withContext(Dispatchers.IO) {
        val customList = try {
            customProviderDao.getAllProviders()
        } catch (_: Exception) {
            emptyList()
        }
        val customMap = customList.associateBy { it.id }
        val chain = buildProviderChain(settings, customMap, buildConfigGeminiKey)

        var lastError: Exception? = null
        for ((idx, providerKey) in chain.withIndex()) {
            val layer = idx + 1
            val customEntity = if (providerKey.startsWith("custom_")) {
                providerKey.removePrefix("custom_").toLongOrNull()?.let { customMap[it] }
            } else null

            val resolvedModel = when {
                idx == 0 && model.isNotBlank() -> EmochiRepository.sanitizeModelName(model)
                providerKey == "groq" -> EmochiRepository.sanitizeModelName(settings.groqModel.ifBlank { "openai/gpt-oss-120b" })
                providerKey == "openai" -> settings.openaiModel.ifBlank { "gpt-4o" }
                providerKey == "gemini" -> EmochiRepository.sanitizeModelName(settings.geminiModel.ifBlank { "gemini-2.0-flash" })
                providerKey == "claude" -> settings.claudeModel.ifBlank { "claude-3-5-sonnet-20241022" }
                providerKey == "openrouter" -> settings.openRouterModel.ifBlank { "deepseek/deepseek-chat" }
                providerKey == "nvidia" -> settings.nvidiaModel.ifBlank { "deepseek-ai/deepseek-v4-flash" }
                providerKey == "mistral" -> settings.mistralModel.ifBlank { "mistral-large-latest" }
                providerKey == "pollinations" -> settings.pollinationsModel.ifBlank { "openai" }
                providerKey == "ovh" -> settings.ovhModel.ifBlank { "meta-llama/Meta-Llama-3-70B-Instruct" }
                providerKey == "llm7" -> "default"
                customEntity != null -> customEntity.modelName
                else -> "gemini-2.0-flash"
            }

            onLogFallback(
                ProviderFallbackLogEntry(
                    providerName = "$providerKey ($resolvedModel)",
                    status = "TRYING",
                    layer = layer
                )
            )

            try {
                val adapter = LLMAdapterFactory.createAdapter(
                    providerKey,
                    resolvedModel,
                    settings,
                    customEntity,
                    buildConfigGeminiKey
                )
                val effectiveTools = if (adapter.supportsFunctionCalling()) tools else emptyList()
                val llmResp = adapter.sendMessage(systemPrompt, messages, effectiveTools)
                val cleanText = EmochiRepository.cleanEmotionTags(llmResp.text)
                if (cleanText.isBlank() && llmResp.toolCalls.isNullOrEmpty() && llmResp.text.isBlank()) {
                    throw IllegalStateException("Sağlayıcı ($providerKey) boş yanıt döndürdü.")
                }

                onLogFallback(
                    ProviderFallbackLogEntry(
                        providerName = llmResp.usedProvider.ifBlank { providerKey },
                        status = "SUCCESS",
                        layer = layer
                    )
                )
                return@withContext ModelResponseResult(
                    text = llmResp.text,
                    toolCalls = llmResp.toolCalls ?: emptyList(),
                    promptTokens = llmResp.usage?.promptTokens ?: 0L,
                    candidateTokens = llmResp.usage?.candidateTokens ?: 0L,
                    usedProvider = llmResp.usedProvider.ifBlank { providerKey }
                )
            } catch (e: Exception) {
                lastError = e
                if (providerKey == "ovh") {
                    EmochiRepository.ovhCooldownUntilTimestamp = System.currentTimeMillis() + 60_000L
                }
                onLogFallback(
                    ProviderFallbackLogEntry(
                        providerName = providerKey,
                        status = "FAILED",
                        errorMessage = e.message ?: "Bilinmeyen hata",
                        layer = layer
                    )
                )
            }
        }

        throw lastError ?: IllegalStateException("Tüm AI sağlayıcıları yanıt vermekte başarısız oldu.")
    }

    suspend fun testEndpointConnection(
        url: String,
        apiKey: String = "",
        modelName: String = "gpt-4o-mini",
        apiFormat: String = "openai",
        testTools: Boolean = false
    ): EmochiRepository.ProviderTestResult = withContext(Dispatchers.IO) {
        val startMs = System.currentTimeMillis()
        try {
            val cleanUrl = url.trim().trimEnd('/')
            val endpoint = when {
                apiFormat.equals("anthropic", ignoreCase = true) || cleanUrl.contains("anthropic.com") ->
                    if (cleanUrl.endsWith("/messages")) cleanUrl else "$cleanUrl/v1/messages"
                cleanUrl.endsWith("/chat/completions") || cleanUrl.contains("pollinations.ai") -> cleanUrl
                cleanUrl.endsWith("/v1") -> "$cleanUrl/chat/completions"
                else -> "$cleanUrl/v1/chat/completions"
            }

            val payload = JSONObject().apply {
                put("model", modelName.ifBlank { "openai" })
                put(
                    "messages",
                    JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", "Merhaba, bağlantı testi için tek kelimeyle 'aktif' yaz.")
                        })
                    }
                )
                put("max_tokens", 24)
                if (testTools) {
                    put("tools", MemoryToolRegistry.toOpenAiToolsJsonArray())
                }
            }

            val reqBuilder = Request.Builder()
                .url(endpoint)
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .header("Content-Type", "application/json")

            if (apiKey.isNotBlank()) {
                reqBuilder.header("Authorization", "Bearer ${apiKey.trim()}")
            }

            RetrofitClient.okHttpClient.newCall(reqBuilder.build()).execute().use { resp ->
                val bodyStr = resp.body?.string().orEmpty()
                val latency = System.currentTimeMillis() - startMs
                if (!resp.isSuccessful) {
                    if (testTools && (resp.code == 400 || resp.code == 422)) {
                        val basicRes = testEndpointConnection(
                            url = url,
                            apiKey = apiKey,
                            modelName = modelName,
                            apiFormat = apiFormat,
                            testTools = false
                        )
                        return@withContext basicRes.copy(supportsFunctionCalling = false)
                    }
                    return@withContext EmochiRepository.ProviderTestResult(
                        isSuccess = false,
                        supportsFunctionCalling = false,
                        errorMessage = "HTTP ${resp.code}: ${bodyStr.take(180)}",
                        rawResponse = bodyStr,
                        latencyMs = latency
                    )
                }
                return@withContext EmochiRepository.ProviderTestResult(
                    isSuccess = true,
                    supportsFunctionCalling = testTools,
                    errorMessage = "",
                    rawResponse = bodyStr.take(300),
                    latencyMs = latency
                )
            }
        } catch (e: Exception) {
            EmochiRepository.ProviderTestResult(
                isSuccess = false,
                supportsFunctionCalling = false,
                errorMessage = e.message ?: "Bağlantı kurulamadı",
                latencyMs = System.currentTimeMillis() - startMs
            )
        }
    }

    suspend fun fetchModelsFromOpenAiCompatibleEndpoint(
        baseUrl: String,
        apiKey: String
    ): List<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext emptyList<String>()
        try {
            val cleanBase = baseUrl.trim().trimEnd('/')
            val modelsUrl = if (cleanBase.endsWith("/v1")) "$cleanBase/models" else "$cleanBase/v1/models"
            val req = Request.Builder()
                .url(modelsUrl)
                .get()
                .header("Authorization", "Bearer ${apiKey.trim()}")
                .build()
            RetrofitClient.okHttpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext emptyList<String>()
                val body = resp.body?.string().orEmpty()
                val arr = JSONObject(body).optJSONArray("data") ?: return@withContext emptyList<String>()
                val result = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    val id = arr.optJSONObject(i)?.optString("id")?.trim().orEmpty()
                    if (id.isNotBlank()) result.add(id)
                }
                result.sorted()
            }
        } catch (_: Exception) {
            emptyList<String>()
        }
    }
}
