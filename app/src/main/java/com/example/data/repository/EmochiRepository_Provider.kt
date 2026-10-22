package com.example.data.repository

import kotlin.math.roundToInt
import androidx.room.withTransaction
import com.example.BuildConfig
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiEmbedContentRequest
import com.example.data.api.GeminiEmbedContentResponse
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.api.RetrofitClient
import com.example.data.local.AffectionEventEntity
import com.example.data.local.AppDatabase
import com.example.data.local.BotEntity
import com.example.data.local.CastMemberEntity
import com.example.data.local.CharacterEmotionEntity
import com.example.data.local.EmotionHistoryDao
import com.example.data.local.EmotionHistoryEntity
import com.example.data.local.EmotionState
import com.example.data.local.PrimaryEmotions
import com.example.data.local.RelationshipAxes
import com.example.data.local.EntityRegistryEntity
import com.example.data.local.MemoryCheckpointEntity
import com.example.data.local.MemoryEventEntity
import com.example.data.local.MemoryFactEntity
import com.example.data.local.MemoryFragmentEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PromptViolationLogDao
import com.example.data.local.PromptViolationLogEntity
import com.example.data.local.SceneTemplateDao
import com.example.data.local.SceneTemplateEntity
import com.example.data.local.SelfCheckFailureLogDao
import com.example.data.local.SelfCheckFailureLogEntity
import com.example.data.local.SensitiveTriggerEntity
import com.example.data.local.SensitiveTriggerDao
import com.example.data.local.PendingReappraisalEntity
import com.example.data.local.PendingReappraisalDao
import com.example.data.local.ActiveMemoryCallLogEntity
import com.example.data.local.ActiveMemoryCallLogDao
import com.example.data.local.StoryProgressDao
import com.example.data.local.StoryProgressEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.local.WorldAtmosphere
import com.example.data.local.EntityRelationEntity
import com.example.data.local.RoutineEntity
import com.example.data.local.ImplicitCorrectionLogEntity
import com.example.data.local.ArchivedMemoryEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// ─── Provider extensions — extracted from EmochiRepository (Bölüm 6) ───────────

    internal suspend fun <T> retryWithBackoff(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 2000L,
        factor: Double = 2.0,
        block: suspend (attempt: Int) -> T
    ): T {
        var currentDelay = initialDelayMs
        var lastException: Exception? = null
        for (attempt in 1..maxAttempts) {
            try {
                return block(attempt)
            } catch (e: Exception) {
                lastException = e
                android.util.Log.e("EmochiRepository", "API Istek Deneme $attempt/$maxAttempts Basarisiz: ${e.message}", e)
                if (attempt < maxAttempts) {
                    kotlinx.coroutines.delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong()
                }
            }
        }
        throw lastException ?: IllegalStateException("İstek $maxAttempts deneme sonrasında yanıt vermedi.")
    }

    internal suspend fun callGeminiApi(
        apiKey: String,
        model: String,
        systemPrompt: String,
        messages: List<MessageEntity>,
        enableNsfw: Boolean = true
    ): com.example.data.api.ModelResponseResult = withContext(Dispatchers.IO) {
        val sanitizedModel = sanitizeModelName(model)
        val geminiContents = formatMessagesForGemini(messages)

        val threshold = "BLOCK_NONE"
        val safetySettings = listOf(
            com.example.data.api.GeminiSafetySetting("HARM_CATEGORY_HARASSMENT", threshold),
            com.example.data.api.GeminiSafetySetting("HARM_CATEGORY_HATE_SPEECH", threshold),
            com.example.data.api.GeminiSafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", threshold),
            com.example.data.api.GeminiSafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", threshold),
            com.example.data.api.GeminiSafetySetting("HARM_CATEGORY_CIVIC_INTEGRITY", threshold)
        )

        val memoryTools = com.example.data.api.MemoryToolRegistry.toGeminiTools()

        val request = GeminiRequest(
            contents = geminiContents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GeminiGenerationConfig(temperature = 0.85f),
            safetySettings = safetySettings,
            tools = memoryTools
        )

        val modelsToTry = listOf(sanitizedModel, "gemini-2.0-flash", "gemini-1.5-flash").distinct()
        var lastException: Exception? = null

        for (currModel in modelsToTry) {
            try {
                val response = RetrofitClient.service.generateContent(
                    model = currModel,
                    apiKey = apiKey,
                    request = request
                )

                val candidate = response.candidates?.firstOrNull()
                val finishReason = candidate?.finishReason
                val parsed = com.example.data.api.MemoryToolRegistry.parseGeminiToolCalls(candidate)
                val text = parsed.first
                val toolCalls = parsed.second

                if (text.isBlank() && toolCalls.isEmpty()) {
                    val reason = if (!finishReason.isNullOrBlank() && finishReason != "STOP") " (Filtre/Neden: $finishReason)" else ""
                    throw IllegalStateException("Gemini yanıtı içerik/güvenlik filtresine takıldı$reason. Lütfen Ayarlar -> +18 Ayarları kısmından güvenlik seviyelerini kontrol edin.")
                }

                val promptTokens = response.usageMetadata?.promptTokenCount?.toLong() ?: 0L
                val candTokens = response.usageMetadata?.candidatesTokenCount?.toLong() ?: 0L

                return@withContext com.example.data.api.ModelResponseResult(
                    text = text,
                    toolCalls = toolCalls,
                    promptTokens = promptTokens,
                    candidateTokens = candTokens
                )
            } catch (e: retrofit2.HttpException) {
                val errorJson = e.response()?.errorBody()?.string()
                val serverMsg = try {
                    JSONObject(errorJson ?: "").optJSONObject("error")?.optString("message")
                } catch (_: Exception) { null }

                val rawText = serverMsg ?: e.message() ?: ""
                val isQuota = e.code() == 429 || rawText.contains("quota", ignoreCase = true) || rawText.contains("RESOURCE_EXHAUSTED", ignoreCase = true)

                val userFacingMsg = if (isQuota) {
                    "Gemini API kullanım kotası aşıldı (429 Rate Limit). Lütfen Ayarlar -> AI Model Ayarları menüsünden kendi API anahtarınızı (Gemini, Groq, Claude veya OpenAI) ekleyin."
                } else {
                    serverMsg ?: "Gemini API Hatası [${e.code()}]: ${e.message()}"
                }

                val exc = IllegalStateException(userFacingMsg)
                lastException = exc

                if (e.code() == 404 || isQuota) {
                    continue
                } else {
                    throw exc
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: IllegalStateException("Gemini API çağrısı başarısız oldu. Lütfen Ayarlar'dan API Key'inizi kontrol edin.")
    }

    internal suspend fun fetchAvailableModels(endpointUrl: String, apiKey: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val modelsUrl = when {
                endpointUrl.contains("/chat/completions") -> endpointUrl.replace("/chat/completions", "/models")
                endpointUrl.contains("/messages") -> endpointUrl.replace("/messages", "/models")
                endpointUrl.endsWith("/") -> "${endpointUrl}models"
                else -> "$endpointUrl/models"
            }
            val request = Request.Builder()
                .url(modelsUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("x-api-key", apiKey)
                .get()
                .build()
            RetrofitClient.okHttpClient.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) {
                    val str = resp.body?.string() ?: ""
                    val json = JSONObject(str)
                    val data = json.optJSONArray("data")
                    val list = mutableListOf<String>()
                    if (data != null) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            val id = item.optString("id")
                            if (id.isNotBlank()) list.add(id)
                        }
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("EmochiRepository", "Sunucu modelleri sorgulanırken hata: ${e.message}")
        }
        return@withContext emptyList()
    }

    internal suspend fun callOpenAiCompatibleApi(
        endpointUrl: String,
        apiKey: String,
        model: String,
        systemPrompt: String,
        messages: List<MessageEntity>
    ): com.example.data.api.ModelResponseResult = withContext(Dispatchers.IO) {
        val standardMsgs = formatMessagesForStandardApi(messages)
        val jsonMessages = JSONArray()
        jsonMessages.put(JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        })
        for (m in standardMsgs) {
            jsonMessages.put(JSONObject().apply {
                put("role", m.first)
                put("content", m.second)
            })
        }

        fun executeRequest(targetModel: String, includeTools: Boolean): com.example.data.api.ModelResponseResult {
            val bodyObj = JSONObject().apply {
                put("model", targetModel)
                put("messages", jsonMessages)
                put("temperature", 0.85)
                if (includeTools) {
                    put("tools", com.example.data.api.MemoryToolRegistry.toOpenAiToolsJsonArray())
                }
            }

            val requestBody = bodyObj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            RetrofitClient.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    val parsedMsg = try {
                        JSONObject(errBody).optJSONObject("error")?.optString("message")
                    } catch (_: Exception) { null }

                    val rawMsg = (parsedMsg ?: errBody).lowercase()

                    if (includeTools && (rawMsg.contains("tool") || rawMsg.contains("function") || rawMsg.contains("param") || rawMsg.contains("unsupported"))) {
                        return executeRequest(targetModel = targetModel, includeTools = false)
                    }

                    if (rawMsg.contains("content_filter") || rawMsg.contains("policy") || rawMsg.contains("refusal") || rawMsg.contains("safety") || rawMsg.contains("inappropriate") || rawMsg.contains("harm")) {
                        throw IllegalStateException("Seçili sağlayıcı ($targetModel) içerik kısıtlaması politikası gereği yanıtı reddetti. Lütfen Ayarlar -> AI Model Ayarları menüsünden farklı bir model (ör. Groq/Gemini) seçin.")
                    }
                    val code = response.code
                    if (code == 429 || rawMsg.contains("rate limit") || rawMsg.contains("quota")) {
                        throw IllegalStateException("API kullanım kotası doldu (429 Rate Limit). Lütfen Ayarlar'dan API Key'inizi veya modelinizi değiştirin.")
                    }

                    // Otomatik Model Düzeltme & Fallback Kontrolü:
                    // Eğer kullanıcının yazdığı model bulunamadıysa (404 veya model_not_found vb.):
                    val isModelNotFoundError = code == 404 ||
                            (rawMsg.contains("model") && (rawMsg.contains("not found") || rawMsg.contains("not_found") || rawMsg.contains("does not exist") || rawMsg.contains("invalid") || rawMsg.contains("unknown") || rawMsg.contains("no such model")))

                    if (isModelNotFoundError && targetModel == model) {
                        val availableModels = kotlinx.coroutines.runBlocking { fetchAvailableModels(endpointUrl, apiKey) }
                        val discoveredModel = availableModels.firstOrNull { m ->
                            m.contains("chat") || m.contains("instruct") || m.contains("llama") || m.contains("gpt") || m.contains("gemini") || m.contains("qwen") || m.contains("deepseek") || m.contains("mistral")
                        } ?: availableModels.firstOrNull()

                        val fallbackToUse = discoveredModel ?: when {
                            endpointUrl.contains("groq.com") -> "openai/gpt-oss-120b"
                            endpointUrl.contains("deepseek.com") -> "deepseek-chat"
                            endpointUrl.contains("openai.com") -> "gpt-4o-mini"
                            else -> "meta-llama/llama-3.3-70b-instruct"
                        }

                        android.util.Log.w("EmochiRepository", "Yazılan model '$model' bulunamadı. Sunucudaki '$fallbackToUse' modeli otomatik kullanılıyor...")
                        return executeRequest(targetModel = fallbackToUse, includeTools = includeTools)
                    }

                    throw IllegalStateException("API Hatası [$targetModel] ($code): ${parsedMsg ?: errBody.take(200)}")
                }
                val responseStr = response.body?.string() ?: ""
                val jsonResp = JSONObject(responseStr)
                val choices = jsonResp.optJSONArray("choices")
                if (choices == null || choices.length() == 0) throw IllegalStateException("Model yanıtı boş döndü.")

                val firstChoice = choices.getJSONObject(0)
                val finishReason = firstChoice.optString("finish_reason", "")
                val messageObj = firstChoice.optJSONObject("message")
                val refusal = messageObj?.optString("refusal", "")

                if (finishReason == "content_filter" || !refusal.isNullOrBlank()) {
                    val detail = if (!refusal.isNullOrBlank()) " Detay: $refusal" else ""
                    throw IllegalStateException("Seçili model ($targetModel) içerik filtresi politikası gereği bu yanıtı süzdü.$detail Lütfen Ayarlar menüsünden modeli değiştirin veya mesajınızı güncelleyin.")
                }

                val (text, toolCalls) = if (messageObj != null) {
                    com.example.data.api.MemoryToolRegistry.parseOpenAiToolCalls(messageObj)
                } else Pair("", emptyList())

                if (text.isBlank() && toolCalls.isEmpty()) throw IllegalStateException("Model yanıtı boş metin döndürdü.")

                val usage = jsonResp.optJSONObject("usage")
                val promptTokens = usage?.optLong("prompt_tokens") ?: 0L
                val candidateTokens = usage?.optLong("completion_tokens") ?: 0L

                return com.example.data.api.ModelResponseResult(
                    text = text,
                    toolCalls = toolCalls,
                    promptTokens = promptTokens,
                    candidateTokens = candidateTokens
                )
            }
        }

        executeRequest(targetModel = model, includeTools = true)
    }

    internal suspend fun callClaudeApi(
        apiKey: String,
        model: String,
        systemPrompt: String,
        messages: List<MessageEntity>,
        baseUrl: String = "https://api.anthropic.com/v1/messages"
    ): com.example.data.api.ModelResponseResult = withContext(Dispatchers.IO) {
        val standardMsgs = formatMessagesForStandardApi(messages)
        val jsonMessages = JSONArray()
        for (m in standardMsgs) {
            jsonMessages.put(JSONObject().apply {
                put("role", m.first)
                put("content", m.second)
            })
        }

        fun executeRequest(includeTools: Boolean): com.example.data.api.ModelResponseResult {
            val bodyObj = JSONObject().apply {
                put("model", model)
                put("max_tokens", 2048)
                put("system", systemPrompt)
                put("messages", jsonMessages)
                if (includeTools) {
                    put("tools", com.example.data.api.MemoryToolRegistry.toClaudeToolsJsonArray())
                }
            }

            val endpointUrl = when {
                baseUrl.endsWith("/messages") -> baseUrl
                baseUrl.endsWith("/") -> "${baseUrl}messages"
                else -> "$baseUrl/messages"
            }

            val requestBody = bodyObj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("x-api-key", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            RetrofitClient.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    val parsedMsg = try {
                        JSONObject(errBody).optJSONObject("error")?.optString("message")
                    } catch (_: Exception) { null }

                    val rawMsg = (parsedMsg ?: errBody).lowercase()

                    if (includeTools && (rawMsg.contains("tool") || rawMsg.contains("schema") || rawMsg.contains("unsupported"))) {
                        return executeRequest(includeTools = false)
                    }

                    if (rawMsg.contains("policy") || rawMsg.contains("refusal") || rawMsg.contains("safety") || rawMsg.contains("content") || rawMsg.contains("prohibited")) {
                        throw IllegalStateException("Claude API ($model) içerik politikası kısıtlaması nedeniyle yanıt veremedi. Lütfen Ayarlar -> AI Model Ayarları menüsünden başka bir model (ör. Groq veya Gemini) seçin.")
                    }
                    val code = response.code
                    if (code == 429 || rawMsg.contains("rate limit") || rawMsg.contains("quota")) {
                        throw IllegalStateException("Claude API kotası aşıldı (429). Lütfen Ayarlar'dan API Key veya model değiştirin.")
                    }
                    throw IllegalStateException("Claude API Hatası ($code): ${parsedMsg ?: errBody.take(200)}")
                }
                val responseStr = response.body?.string() ?: ""
                val jsonResp = JSONObject(responseStr)
                val stopReason = jsonResp.optString("stop_reason", "")
                if (stopReason == "max_tokens_exceeded_or_refusal" || jsonResp.optString("type") == "refusal") {
                    throw IllegalStateException("Claude API ($model) içerik politikası gereği bu yanıtı reddetti. Lütfen Ayarlar menüsünden modelinizi değiştirin.")
                }

                val (text, toolCalls) = com.example.data.api.MemoryToolRegistry.parseClaudeToolCalls(jsonResp)
                if (text.isBlank() && toolCalls.isEmpty()) throw IllegalStateException("Claude yanıtı boş döndü.")

                val usage = jsonResp.optJSONObject("usage")
                val promptTokens = usage?.optLong("input_tokens") ?: 0L
                val candidateTokens = usage?.optLong("output_tokens") ?: 0L

                return com.example.data.api.ModelResponseResult(
                    text = text,
                    toolCalls = toolCalls,
                    promptTokens = promptTokens,
                    candidateTokens = candidateTokens
                )
            }
        }

        executeRequest(includeTools = true)
    }

    internal suspend fun callLlm7Api(
        systemPrompt: String,
        messages: List<MessageEntity>
    ): com.example.data.api.ModelResponseResult {
        val res = callOpenAiCompatibleApi(
            endpointUrl = "https://api.llm7.io/v1/chat/completions",
            apiKey = "unused",
            model = "default",
            systemPrompt = systemPrompt,
            messages = messages
        )
        return res.copy(usedProvider = "llm7")
    }

    internal suspend fun executeSingleModelRequest(
        model: String,
        settings: UserSettingsEntity,
        systemPrompt: String,
        messages: List<MessageEntity>,
        botId: String? = null
    ): com.example.data.api.ModelResponseResult {
        val customProvider = if (settings.selectedProvider.startsWith("custom_")) {
            val customId = settings.selectedProvider.removePrefix("custom_").toLongOrNull()
            if (customId != null) db.customProviderDao().getProviderById(customId) else null
        } else null

        val adapter = com.example.data.api.LLMAdapterFactory.createAdapter(
            providerKey = settings.selectedProvider,
            modelName = model,
            settings = settings,
            customProvider = customProvider,
            buildConfigGeminiKey = getBuildConfigKey()
        )

        val isSecondary = adapter.reliabilityTier == com.example.data.api.ReliabilityTier.SECONDARY
        val providerKey = adapter.providerName

        if (isSecondary) {
            val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest(providerKey)
            if (!canSend) {
                com.example.util.ProviderRateLimitTracker.recordFallbackTrigger(providerKey)
                throw IllegalStateException("$providerKey Rate Limit Önceden Engelledi: $rateReason")
            }
        }

        var currentPrompt = if (!adapter.supportsFunctionCalling()) {
            systemPrompt + "\n\n[SİSTEM UYARISI: Bu model Function Calling desteklememektedir. Önemli bir yeni bilgi öğrendiğinde veya var olan bir bilgiyi değiştirdiğinde yanıtının sonuna [[MEMORY_SAVE action=\"save\" content=\"...\" category=\"fact\" importance=\"5\"]] formatında ekleme yap. Ayrıca her mesaj sonunda [[STATE affectionScore=... delta=... reason=\"...\"]] bloğunu eklemeyi unutma.]"
        } else {
            systemPrompt
        }

        if (isSecondary && com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
            currentPrompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
        }

        val tools = if (adapter.supportsFunctionCalling()) com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS else null

        var lastError: Exception? = null
        val startTime = System.currentTimeMillis()
        for (attempt in 1..2) {
            try {
                var resp = adapter.sendMessage(currentPrompt, messages, tools)

                if (isSecondary && com.example.util.OutputQualityValidator.isResponseTooShort(resp.text, settings.responseLength, attempt)) {
                    currentPrompt += com.example.util.OutputQualityValidator.buildElaboratePromptInstruction()
                    resp = adapter.sendMessage(currentPrompt, messages, tools)
                }

                val cleanText = cleanEmotionTags(resp.text)
                if (cleanText.isBlank() && resp.toolCalls.isNullOrEmpty()) {
                    logEmptyResponse(
                        botId = botId ?: "unknown",
                        provider = resp.usedProvider,
                        model = model,
                        finishReason = "empty_response_content",
                        rawLength = resp.text.length,
                        errorMessage = "Model etiketler temizlendikten sonra boş içerik döndürdü (attempt=$attempt)."
                    )
                    if (isSecondary) {
                        com.example.util.ProviderRateLimitTracker.recordMalformedOutput(providerKey)
                    }
                    throw IllegalStateException("Sağlayıcı (${resp.usedProvider}) boş/temizlenmiş yanıt üretti.")
                }

                if (isSecondary) {
                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest(providerKey, tokensUsed = totalTokens, responseTimeMs = duration)
                }

                return com.example.data.api.ModelResponseResult(
                    text = resp.text,
                    toolCalls = resp.toolCalls ?: emptyList(),
                    promptTokens = resp.usage?.promptTokens ?: 0L,
                    candidateTokens = resp.usage?.candidateTokens ?: 0L,
                    usedProvider = resp.usedProvider
                )
            } catch (e: Exception) {
                lastError = e
                if (isSecondary) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger(providerKey)
                }
                val errMsg = e.message ?: e.toString()
                val isContentFilter = errMsg.contains("içerik filtresi", ignoreCase = true) ||
                        errMsg.contains("content_filter", ignoreCase = true) ||
                        errMsg.contains("safety", ignoreCase = true)
                val isLength = errMsg.contains("uzunluk", ignoreCase = true) ||
                        errMsg.contains("length", ignoreCase = true) ||
                        errMsg.contains("max_tokens", ignoreCase = true)

                if (attempt == 1) {
                    if (isContentFilter) {
                        currentPrompt += "\n\n[SİSTEM DİREKTİFİ: Önceki yanıtın içerik politikasına takıldı. Sahneyi daha ölçülü/dolaylı bir şekilde, aynı olay örgüsünü koruyarak yeniden anlat.]"
                        logEmptyResponse(
                            botId = botId ?: "unknown",
                            provider = settings.selectedProvider,
                            model = model,
                            finishReason = "content_filter_retry",
                            rawLength = 0,
                            errorMessage = "1. deneme içerik filtresine takıldı, otomatik retry atılıyor."
                        )
                    } else if (isLength) {
                        currentPrompt += "\n\n[SİSTEM DİREKTİFİ: Yanıt uzunluk kısıtına takıldı. Lütfen daha kısa ve net bir yanıt ver.]"
                        logEmptyResponse(
                            botId = botId ?: "unknown",
                            provider = settings.selectedProvider,
                            model = model,
                            finishReason = "length_retry",
                            rawLength = 0,
                            errorMessage = "1. deneme uzunluk sınırına takıldı, otomatik retry atılıyor."
                        )
                    } else {
                        currentPrompt += "\n\n[SİSTEM DİREKTİFİ: Önceki yanıt oluşturulamadı. Lütfen doğrudan yanıt ver.]"
                    }
                }
            }
        }

        throw lastError ?: IllegalStateException("Sağlayıcı yanıt üretemedi.")
    }

    suspend fun generateAiReply(
        bot: BotEntity,
        messages: List<MessageEntity>
    ): AiReplyResult = withContext(Dispatchers.IO) {
        val isBookMode = bot.mode == "book" ||
                bot.id == "preset_aiden_mcu_cosmic" ||
                bot.aiName.contains("Kitap") ||
                bot.aiName.contains("Cosmic Drift") ||
                bot.aiName.contains("Kozmik Sürükleniş") ||
                bot.aiPersonality.contains("KİTAP MODU") ||
                bot.scenario.contains("KİTAP MODU")

        if (isBookMode) {
            val bookText = generateDeterministicBookReply(bot, messages)
            return@withContext AiReplyResult(replyText = bookText, usedProvider = "book_mode")
        }

        val settings = getOrCreateSettings()
        val effectiveModel = getEffectiveModelForProvider(settings)

        // Context Window & Token Budget Management
        val (effectiveBot, effectiveMessages) = prepareContextAndSummarizeIfNeeded(bot, messages, effectiveModel)

        // RAG: Multi-Message Context Query Retrieval & Memory Assembly
        val userQuery = effectiveMessages.lastOrNull { it.role == "user" }?.text ?: ""
        val contextQuery = effectiveMessages.takeLast(3).joinToString(" \n ") { "${it.role}: ${it.text}" }.ifBlank { userQuery }
        val apiKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()

        val isTrivialMsg = MemoryEngine.isTrivialCasualMessage(userQuery)

        val (ragResult, registeredEntities, recentCheckpoints, castMembers) = coroutineScope {
            val ragDeferred = async {
                if (isTrivialMsg) TwoStageRetrievalResult(emptyList(), emptyList(), 0f, "vector_only")
                else getRelevantMemoryTwoStage(effectiveBot.id, userQuery, apiKey)
            }
            val entitiesDeferred = async { entityRegistryDao.getEntitiesForBot(effectiveBot.id) }
            val checkpointsDeferred = async {
                val cpWindow = getCheckpointAccessWindow(effectiveBot.id, userQuery, recentLimit = 3, apiKey = apiKey)
                cpWindow.fullRecentCheckpoints + cpWindow.olderRetrievedByEmbedding
            }
            val castDeferred = async { castMemberDao.getCastMembersForBot(effectiveBot.id) }

            Quadruple(ragDeferred.await(), entitiesDeferred.await(), checkpointsDeferred.await(), castDeferred.await())
        }
        val relevantEvents = ragResult.retrievedEvents
        val relevantFacts = ragResult.retrievedFacts

        val now = System.currentTimeMillis()

        // Katman 1: Kullanıcı mesajından zaman atlaması — prompt kurulmadan ÖNCE takvimi ilerlet
        val userJump = TimePerceptionEngine.parseStoryTimeJump(userQuery)
        var preJumpBot = effectiveBot.copy(lastMessageTimestamp = now, updatedAt = now)

        if (userJump.daysToAdvance > 0) {
            val (newDate, newDayCounter, newAge) = TimePerceptionEngine.advanceCalendarDateAndCheckAge(
                currentCalendarDateStr = preJumpBot.storyCalendarDate,
                currentDayCounter = preJumpBot.storyDayCounter,
                elapsedDaysToAdd = userJump.daysToAdvance,
                birthDateStr = preJumpBot.birthDate,
                initialAge = preJumpBot.initialAge
            )
            preJumpBot = preJumpBot.copy(
                storyCalendarDate = newDate,
                storyDayCounter = newDayCounter,
                currentAge = newAge,
                sceneTime = userJump.newSceneTime ?: preJumpBot.sceneTime
            )
            updateCastMembersAgeForBot(preJumpBot.id, newDate)
        } else if (userJump.newSceneTime != null) {
            preJumpBot = preJumpBot.copy(sceneTime = userJump.newSceneTime)
        }

        // Duygu sönümü — yalnızca hikâye günü ilerlediyse
        // Sıra: önce decay, sonra model delta'sı (parseAndApplyEmotionUpdates içinde)
        val botAfterDecay = if (userJump.daysToAdvance > 0) {
            val emotionState = EmotionState.fromJson(preJumpBot.emotionState)
            val decayResult = EmotionEngine.applyNaturalEmotionalDecayWithNote(
                currentState = emotionState,
                storyDaysSkipped = userJump.daysToAdvance,
                regulationCapacity = preJumpBot.emotionalRegulationCapacity
            )
            // Sönüm notu varsa bir sonraki prompt'a enjekte edilmek üzere sakla
            if (decayResult.promptNote.isNotBlank()) {
                pendingDecayNoteMap[preJumpBot.id] = decayResult.promptNote
            }
            preJumpBot.copy(emotionState = decayResult.state.toJson())
        } else preJumpBot

        val updatedTimeBot = botAfterDecay
        botDao.insertOrUpdate(updatedTimeBot)

        val totalCount = messageDao.getMessageCountForBot(updatedTimeBot.id)

        // Knowledge Graph: Multi-hop question detection and relation graph traversal
        val graphTraversalResult = try {
            detectAndResolveGraphQuery(updatedTimeBot.id, userQuery)
        } catch (_: Exception) {
            null
        }

        // Routine System: Active routine reminder check for current story date
        val activeRoutine = try {
            getActiveRoutineReminder(updatedTimeBot.id, updatedTimeBot.storyCalendarDate)
        } catch (_: Exception) {
            null
        }

        // Aktif sahne filtresi: onaylanmış cast üyelerinden son 3-4 mesajda anılanlar + importanceScore≥90 çekirdek kadro
        val recentSceneText = effectiveMessages.takeLast(4).joinToString(" ") { it.text } + " " + userQuery
        try {
            reEvaluateCastMemberRolesIfNeeded(updatedTimeBot.id, totalCount, recentSceneText)
        } catch (_: Exception) {}
        val updatedCastMembers = castMemberDao.getCastMembersForBot(updatedTimeBot.id)
        val confirmedCast = updatedCastMembers.filter { !it.pendingUserConfirmation }
        val activeSceneCastMembers = CastDetectorEngine.filterActiveInSceneCastMembers(confirmedCast, recentSceneText)

        val userMood = com.example.util.UserSentimentAnalyzer.analyze(effectiveMessages)
        val moodHint = com.example.util.UserSentimentAnalyzer.buildMoodHint(userMood)

        val systemPrompt = buildSystemPrompt(
            updatedTimeBot,
            settings,
            relevantEvents = relevantEvents,
            relevantFacts = relevantFacts,
            registeredEntities = registeredEntities,
            recentCheckpoints = recentCheckpoints,
            castMembers = activeSceneCastMembers,
            totalMessageCount = totalCount,
            graphTraversalResult = graphTraversalResult,
            routineReminder = activeRoutine
        ) + moodHint

        // Atlama varsa modele tek satır bağlam ipucu ekle
        val jumpHint = if (userJump.daysToAdvance > 0) {
            val unit = if (userJump.daysToAdvance == 1) "1 gün" else "${userJump.daysToAdvance} gün"
            "\n(Sistem notu: Az önce hikâyede $unit geçti; aradaki gelişmeleri hikâyeye uygun yansıt.)"
        } else ""

        // Mark active routine mentioned so it isn't repeatedly triggered on subsequent turns of the same date
        if (activeRoutine != null) {
            try {
                routineDao.updateRoutine(activeRoutine.copy(lastMentionedStoryDate = updatedTimeBot.storyCalendarDate))
            } catch (_: Exception) {}
        }

        val messagesWithHint = if (jumpHint.isNotBlank()) {
            val lastUserIdx = effectiveMessages.indexOfLast { it.role == "user" }
            if (lastUserIdx >= 0) {
                effectiveMessages.toMutableList().also { list ->
                    list[lastUserIdx] = list[lastUserIdx].copy(text = list[lastUserIdx].text + jumpHint)
                }
            } else effectiveMessages
        } else effectiveMessages

        val result = executeModelRequestWithFallback(effectiveModel, settings, systemPrompt, messagesWithHint, botId = updatedTimeBot.id)
        recordTokenUsage(updatedTimeBot.id, result.promptTokens, result.candidateTokens)
        executeActiveMemoryToolCalls(updatedTimeBot.id, result.text, toolCalls = result.toolCalls, apiKey = apiKey)

        // Katman 2: Model yanıtındaki story_time_skip — kullanıcı tarafı ZATEN ilerlediyse model atlamasını uygulama
        val replyText = parseAndApplyEmotionUpdates(updatedTimeBot.id, result.text, userAlreadyJumped = userJump.daysToAdvance > 0)

        val finalReplyText = if (replyText.isBlank()) {
            logEmptyResponse(
                botId = updatedTimeBot.id,
                provider = result.usedProvider,
                model = effectiveModel,
                finishReason = "empty_cleaned_reply",
                rawLength = result.text.length,
                errorMessage = "Duygu/STATE etiketleri temizlendikten sonra mesaj içeriği boş kaldı. Otomatik karakter kurtarma yanıtı oluşturuldu."
            )
            "${updatedTimeBot.aiName} bir an tereddütle sessiz kaldı, ardından gözlerinin içine bakarak konuşmaya devam etti: \"Seni dinliyorum...\""
        } else {
            replyText
        }

        try {
            extractAndSaveRealtimeMemories(updatedTimeBot.id, userQuery, finalReplyText, apiKey)
            detectAndRegisterCastMembers(updatedTimeBot.id, finalReplyText, apiKey, userQuery = userQuery)
            checkAndGenerateCheckpoint(updatedTimeBot.id, totalCount + 1, apiKey)
            // 4.4: Tek yanıt içi zaman çelişkisi (JAR'daki hasInconsistentSceneTime)
            if (com.example.util.OutputQualityValidator.hasInconsistentSceneTime(finalReplyText)) {
                android.util.Log.w("SceneTimeConflict", "[sceneTimeInconsistencyLog] Intra-response conflict detected. provider=${result.usedProvider} snippet=${finalReplyText.take(120)}")
            }
            // 4.4: Mesajlar arası zaman çelişkisi (art arda 2-3 mesaj)
            detectInterMessageTimeConflict(updatedTimeBot.id, finalReplyText, result.usedProvider)
        } catch (_: Exception) {}

        return@withContext AiReplyResult(replyText = finalReplyText, usedProvider = result.usedProvider, systemPromptSnapshot = systemPrompt)
    }

    internal suspend fun tryExecuteProviderByKey(
        key: String,
        model: String,
        settings: UserSettingsEntity,
        systemPrompt: String,
        messages: List<MessageEntity>,
        botId: String?,
        customProvidersMap: Map<Long, com.example.data.local.CustomProviderEntity>,
        layer: Int
    ): com.example.data.api.ModelResponseResult? {
        when {
            key == "main" -> {
                if (settings.selectedProvider == "llm7" && !settings.enableLlm7) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = "Ana Seçim (LLM7 - Devre Dışı)", status = "FAILED", errorMessage = "LLM7 kapalı olduğu için atlandı.", layer = 1))
                    return null
                }
                if (settings.selectedProvider == "pollinations" && !settings.enablePollinations) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = "Ana Seçim (Pollinations - Devre Dışı)", status = "FAILED", errorMessage = "Pollinations kapalı olduğu için atlandı.", layer = 1))
                    return null
                }
                if (settings.selectedProvider == "ovh" && !settings.enableOvh) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = "Ana Seçim (OVH - Devre Dışı)", status = "FAILED", errorMessage = "OVH kapalı olduğu için atlandı.", layer = 1))
                    return null
                }
                val isCustom = settings.selectedProvider.startsWith("custom_")
                val customId = if (isCustom) settings.selectedProvider.removePrefix("custom_").toLongOrNull() else null
                val cp = if (customId != null) customProvidersMap[customId] else null
                val activeModel = resolveModelForProvider(settings.selectedProvider, settings, cp)
                val label = if (isCustom) {
                    "Özel Sağlayıcı (Katman 0): ${cp?.label ?: settings.selectedProvider} (${cp?.modelName ?: activeModel})"
                } else {
                    "Ana Seçim (${settings.selectedProvider}: $activeModel)"
                }
                val currentLayer = if (isCustom) 0 else 1
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = currentLayer))
                try {
                    val result = executeSingleModelRequest(activeModel, settings, systemPrompt, messages, botId)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = currentLayer))
                    return result
                } catch (e: Exception) {
                    val errRaw = e.message ?: e.toString()
                    val errLower = errRaw.lowercase()
                    val isQwenPreview = activeModel.contains("qwen") || settings.groqModel.contains("qwen")
                    val is404 = errLower.contains("404") || errLower.contains("not found") || errLower.contains("not_found") || errLower.contains("does not exist")
                    val finalErrMsg = if (settings.selectedProvider == "groq" && isQwenPreview && is404) {
                        "Bu Qwen modeli Groq tarafından kaldırılmış olabilir (Preview modeller kısa bildirimle kalkabilir), lütfen openai/gpt-oss-120b'ye geçin veya modelleri yenileyin."
                    } else errRaw
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = finalErrMsg, layer = currentLayer))
                }
            }
            key == "llm7" -> {
                if (!settings.enableLlm7 || settings.selectedProvider == "llm7") return null
                val label = "LLM7 (Ücretsiz Servis)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("llm7")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("llm7")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }

                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("llm7", "default", settings)
                    var llm7Prompt = systemPrompt

                    if (com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
                        llm7Prompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
                    }
                    val startTime = System.currentTimeMillis()
                    var resp = adapter.sendMessage(llm7Prompt, messages, null)

                    if (com.example.util.OutputQualityValidator.isResponseTooShort(resp.text, settings.responseLength, 1)) {
                        llm7Prompt += com.example.util.OutputQualityValidator.buildElaboratePromptInstruction()
                        resp = adapter.sendMessage(llm7Prompt, messages, null)
                    }

                    var textResult = resp.text
                    if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(textResult)) {
                        textResult = com.example.util.OutputQualityValidator.truncateAtRepetition(textResult)
                    }

                    textResult = com.example.util.OutputQualityValidator.enforceLengthLimits(textResult, settings.responseLength)

                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("llm7", tokensUsed = totalTokens, responseTimeMs = duration)

                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("llm7")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "pollinations" -> {
                if (!settings.enablePollinations || settings.selectedProvider == "pollinations") return null
                val pModel = settings.pollinationsModel.ifBlank { "openai" }
                val label = "Pollinations ($pModel)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("pollinations")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("pollinations")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("pollinations", pModel, settings)
                    var prompt = systemPrompt

                    if (com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
                        prompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
                    }
                    val startTime = System.currentTimeMillis()
                    var resp = adapter.sendMessage(prompt, messages, null)

                    var textResult = resp.text
                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("pollinations", tokensUsed = totalTokens, responseTimeMs = duration)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("pollinations")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "ovh" -> {
                if (!settings.enableOvh || settings.selectedProvider == "ovh") return null
                val oModel = settings.ovhModel.ifBlank { "meta-llama/Meta-Llama-3-70B-Instruct" }
                val label = "OVH AI ($oModel)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("ovh")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("ovh")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("ovh", oModel, settings)
                    var prompt = systemPrompt

                    val startTime = System.currentTimeMillis()
                    var resp = adapter.sendMessage(prompt, messages, null)

                    var textResult = resp.text
                    if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(textResult)) {
                        logFallbackAttempt(ProviderFallbackLogEntry(providerName = "$label (Tekrar Tespit)", status = "TRYING", errorMessage = "Tekrar döngüsü yakalandı, retry atılıyor.", layer = layer))
                        val retryPrompt = prompt + com.example.util.OutputQualityValidator.buildRepetitionRetryInstruction()
                        val retryResp = adapter.sendMessage(retryPrompt, messages, null)
                        if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(retryResp.text)) {
                            textResult = com.example.util.OutputQualityValidator.truncateAtRepetition(retryResp.text)
                        } else {
                            textResult = retryResp.text
                        }
                    }

                    textResult = com.example.util.OutputQualityValidator.enforceLengthLimits(textResult, settings.responseLength)

                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("ovh", tokensUsed = totalTokens, responseTimeMs = duration)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("ovh")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "gemini" -> {
                if (settings.selectedProvider == "gemini") return null
                val gemKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()
                if (gemKey.isBlank()) return null
                val gModel = settings.geminiModel.ifBlank { "gemini-2.0-flash" }
                val label = "Gemini AI ($gModel)"
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("gemini", gModel, settings, buildConfigGeminiKey = gemKey)
                    val resp = adapter.sendMessage(systemPrompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(resp.text, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "claude" -> {
                if (settings.selectedProvider == "claude" || settings.claudeApiKey.isBlank()) return null
                val cModel = settings.claudeModel.ifBlank { "claude-3-5-sonnet-20241022" }
                val label = "Claude AI ($cModel)"
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("claude", cModel, settings)
                    val resp = adapter.sendMessage(systemPrompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(resp.text, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "groq" -> {
                if (settings.selectedProvider == "groq" || settings.groqApiKey.isBlank()) return null
                val gModel = sanitizeModelName(settings.groqModel.ifBlank { "openai/gpt-oss-120b" })
                val label = "Groq ($gModel)"
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("groq", gModel, settings)
                    val resp = adapter.sendMessage(systemPrompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(resp.text, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    val errRaw = e.message ?: e.toString()
                    val errLower = errRaw.lowercase()
                    val isQwenPreview = gModel.contains("qwen")
                    val is404 = errLower.contains("404") || errLower.contains("not found") || errLower.contains("not_found") || errLower.contains("does not exist")
                    val finalErrMsg = if (isQwenPreview && is404) {
                        "Bu Qwen modeli Groq tarafından kaldırılmış olabilir (Preview modeller kısa bildirimle kalkabilir), lütfen openai/gpt-oss-120b'ye geçin veya modelleri yenileyin."
                    } else errRaw
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = finalErrMsg, layer = layer))
                }
            }
            key == "openai" -> {
                if (settings.selectedProvider == "openai" || settings.openaiApiKey.isBlank()) return null
                val oModel = settings.openaiModel.ifBlank { "gpt-4o" }
                val label = "OpenAI ($oModel)"
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("openai", oModel, settings)
                    val resp = adapter.sendMessage(systemPrompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(resp.text, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "openrouter" -> {
                if (settings.selectedProvider == "openrouter" || settings.openRouterApiKey.isBlank()) return null
                val mName = settings.openRouterModel.ifBlank { "deepseek/deepseek-chat" }
                val label = "OpenRouter ($mName)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("openrouter")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("openrouter")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("openrouter", mName, settings)
                    var prompt = systemPrompt
                    if (com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
                        prompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
                    }
                    val startTime = System.currentTimeMillis()
                    val resp = adapter.sendMessage(prompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)

                    var textResult = resp.text
                    if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(textResult)) {
                        textResult = com.example.util.OutputQualityValidator.truncateAtRepetition(textResult)
                    }

                    textResult = com.example.util.OutputQualityValidator.enforceLengthLimits(textResult, settings.responseLength)

                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("openrouter", tokensUsed = totalTokens, responseTimeMs = duration)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("openrouter")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "nvidia" -> {
                if (settings.selectedProvider == "nvidia" || settings.nvidiaApiKey.isBlank()) return null
                val mName = settings.nvidiaModel
                val label = "NVIDIA NIM ($mName)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("nvidia")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("nvidia")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("nvidia", mName, settings)
                    var prompt = systemPrompt
                    if (com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
                        prompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
                    }
                    val startTime = System.currentTimeMillis()
                    val resp = adapter.sendMessage(prompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)

                    var textResult = resp.text
                    if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(textResult)) {
                        textResult = com.example.util.OutputQualityValidator.truncateAtRepetition(textResult)
                    }

                    textResult = com.example.util.OutputQualityValidator.enforceLengthLimits(textResult, settings.responseLength)

                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("nvidia", tokensUsed = totalTokens, responseTimeMs = duration)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("nvidia")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key == "mistral" -> {
                if (settings.selectedProvider == "mistral" || settings.mistralApiKey.isBlank()) return null
                val mName = settings.mistralModel.ifBlank { "mistral-large-latest" }
                val label = "Mistral AI ($mName)"
                val (canSend, rateReason) = com.example.util.ProviderRateLimitTracker.canSendRequest("mistral")
                if (!canSend) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("mistral")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = "Önceden Engellendi (Rate Limit): $rateReason", layer = layer))
                    return null
                }
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val adapter = com.example.data.api.LLMAdapterFactory.createAdapter("mistral", mName, settings)
                    var prompt = systemPrompt
                    if (com.example.util.OutputQualityValidator.checkForRecentCliches(messages)) {
                        prompt += com.example.util.OutputQualityValidator.buildClichePromptInstruction()
                    }
                    val startTime = System.currentTimeMillis()
                    val resp = adapter.sendMessage(prompt, messages, com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS)

                    var textResult = resp.text
                    if (com.example.util.OutputQualityValidator.hasRepetitiveLoops(textResult)) {
                        textResult = com.example.util.OutputQualityValidator.truncateAtRepetition(textResult)
                    }

                    textResult = com.example.util.OutputQualityValidator.enforceLengthLimits(textResult, settings.responseLength)

                    val duration = System.currentTimeMillis() - startTime
                    val totalTokens = (resp.usage?.promptTokens ?: 0L) + (resp.usage?.candidateTokens ?: 0L)
                    com.example.util.ProviderRateLimitTracker.recordRequest("mistral", tokensUsed = totalTokens, responseTimeMs = duration)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(textResult, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    com.example.util.ProviderRateLimitTracker.recordFallbackTrigger("mistral")
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
            key.startsWith("custom_") -> {
                val idStr = key.removePrefix("custom_").toLongOrNull() ?: return null
                val cp = customProvidersMap[idStr] ?: return null
                if ("custom_${cp.id}" == settings.selectedProvider) return null
                val label = "Özel Sağlayıcı: ${cp.label} (${cp.modelName})"
                logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "TRYING", layer = layer))
                try {
                    val cpAdapter = com.example.data.api.LLMAdapterFactory.createAdapter("custom_${cp.id}", cp.modelName, settings, cp)
                    val finalPrompt = if (!cpAdapter.supportsFunctionCalling()) {
                        systemPrompt + "\n\n[SİSTEM UYARISI: Bu model Function Calling desteklememektedir. Önemli bir yeni bilgi öğrendiğinde yanıtına [[MEMORY_SAVE ...]] ekle.]"
                    } else systemPrompt
                    val tools = if (cpAdapter.supportsFunctionCalling()) com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS else null
                    val resp = cpAdapter.sendMessage(finalPrompt, messages, tools)
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "SUCCESS", layer = layer))
                    return validateAndPrepareResult(resp.text, resp.toolCalls, resp.usage?.promptTokens ?: 0L, resp.usage?.candidateTokens ?: 0L, resp.usedProvider)
                } catch (e: Exception) {
                    logFallbackAttempt(ProviderFallbackLogEntry(providerName = label, status = "FAILED", errorMessage = e.message ?: e.toString(), layer = layer))
                }
            }
        }
        return null
    }

    internal suspend fun executeModelRequestWithFallback(
        model: String,
        settings: UserSettingsEntity,
        systemPrompt: String,
        messages: List<MessageEntity>,
        botId: String? = null
    ): com.example.data.api.ModelResponseResult {
        clearFallbackLogs()

        val customProvidersList = try { db.customProviderDao().getAllProviders() } catch (_: Exception) { emptyList() }
        val customMap = customProvidersList.associateBy { it.id }

        // Primary Attempt: Deny or execute main provider first
        val primaryRes = tryExecuteProviderByKey("main", model, settings, systemPrompt, messages, botId, customMap, layer = 1)
        if (primaryRes != null) return primaryRes

        val primaryError = _providerFallbackLog.value.lastOrNull {
            (it.providerName.startsWith("Ana Seçim") || it.providerName.startsWith("Özel Sağlayıcı")) && !it.errorMessage.isNullOrBlank()
        }?.errorMessage

        if (!settings.enableAutoFallback) {
            val errDetail = if (!primaryError.isNullOrBlank()) ": $primaryError" else "."
            throw IllegalStateException("Ana sağlayıcı (${settings.selectedProvider}) başarısız oldu$errDetail (otomatik yedekleme kapalı).")
        }

        // Katman 1: Anahtarsız/keysiz ücretsiz sağlayıcılar (yalnızca kullanıcı açtıysa)
        val layer1Keys = listOf("pollinations", "llm7", "ovh").filter { key ->
            when (key) {
                "pollinations" -> (settings.enablePollinations && settings.pollinationsEnabled) && settings.selectedProvider != "pollinations"
                "llm7" -> (settings.enableLlm7 && settings.llm7Enabled) && settings.selectedProvider != "llm7"
                "ovh" -> (settings.enableOvh && settings.ovhEnabled) && settings.selectedProvider != "ovh"
                else -> false
            }
        }

        // Katman 2: Kullanıcının ANA/birincil sağlayıcıları (Claude, OpenAI, Groq, Gemini)
        val layer2Keys = listOf("gemini", "claude", "groq", "openai").filter { key ->
            val geminiKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()
            val hasValidGemini = geminiKey.isNotBlank() && geminiKey != "MY_GEMINI_API_KEY"
            when (key) {
                "gemini" -> settings.selectedProvider != "gemini" && hasValidGemini
                "claude" -> settings.selectedProvider != "claude" && settings.claudeApiKey.isNotBlank()
                "groq" -> settings.selectedProvider != "groq" && settings.groqApiKey.isNotBlank()
                "openai" -> settings.selectedProvider != "openai" && settings.openaiApiKey.isNotBlank()
                else -> false
            }
        }

        // Katman 3: Kullanıcının kendi key'iyle eklediği ikincil sağlayıcılar (OpenRouter, NVIDIA NIM, Mistral)
        val layer3Keys = listOf("openrouter", "nvidia", "mistral").filter { key ->
            when (key) {
                "openrouter" -> settings.openRouterEnabled && settings.selectedProvider != "openrouter" && settings.openRouterApiKey.isNotBlank()
                "nvidia" -> settings.nvidiaEnabled && settings.selectedProvider != "nvidia" && settings.nvidiaApiKey.isNotBlank()
                "mistral" -> settings.mistralEnabled && settings.selectedProvider != "mistral" && settings.mistralApiKey.isNotBlank()
                else -> false
            }
        }

        // Katman 0 / Özel Sağlayıcılar: Tanımlı diğer custom provider'lar
        val customKeys = customProvidersList.map { "custom_${it.id}" }.filter { it != settings.selectedProvider }

        // 1. Katman 1'i dene
        for (key in layer1Keys) {
            val res = tryExecuteProviderByKey(key, model, settings, systemPrompt, messages, botId, customMap, layer = 1)
            if (res != null) return res
        }

        // 2. Katman 2'yi dene (Gemini, Claude, Groq, OpenAI)
        for (key in layer2Keys) {
            val res = tryExecuteProviderByKey(key, model, settings, systemPrompt, messages, botId, customMap, layer = 2)
            if (res != null) return res
        }

        // 3. Katman 3'ü dene (OpenRouter, NVIDIA NIM, Mistral)
        for (key in layer3Keys) {
            val res = tryExecuteProviderByKey(key, model, settings, systemPrompt, messages, botId, customMap, layer = 3)
            if (res != null) return res
        }

        // 4. Özel Sağlayıcıları dene
        for (key in customKeys) {
            val res = tryExecuteProviderByKey(key, model, settings, systemPrompt, messages, botId, customMap, layer = 0)
            if (res != null) return res
        }

        if (!primaryError.isNullOrBlank()) {
            throw IllegalStateException(primaryError)
        }
        val failedLogs = _providerFallbackLog.value.filter { it.status == "FAILED" && !it.errorMessage.isNullOrBlank() }
        val errSummary = if (failedLogs.isNotEmpty()) {
            "\n" + failedLogs.joinToString("\n") { entry ->
                val shortErr = when {
                    entry.errorMessage?.contains("429") == true || entry.errorMessage?.contains("rate") == true -> "rate limit"
                    entry.errorMessage?.contains("404") == true || entry.errorMessage?.contains("model") == true -> "model bulunamadı"
                    entry.errorMessage?.contains("401") == true || entry.errorMessage?.contains("key") == true -> "API key geçersiz"
                    entry.errorMessage?.contains("timeout") == true || entry.errorMessage?.contains("timed out") == true -> "zaman aşımı"
                    entry.errorMessage?.contains("exhausted") == true || entry.errorMessage?.contains("credit") == true -> "kredi tükendi"
                    else -> entry.errorMessage?.take(60) ?: "bilinmeyen hata"
                }
                "• ${entry.providerName}: $shortErr"
            }
        } else ""
        throw IllegalStateException("Hiçbir sağlayıcıya ulaşılamadı.$errSummary\nLütfen API ayarlarınızı ve internet bağlantınızı kontrol edin.")
    }

    suspend fun testLlm7Connection(): ProviderTestResult {
        return testCustomProviderConnection(
            baseUrl = "https://api.llm7.io/v1",
            apiKey = "unused",
            modelName = "default",
            apiFormat = "openai"
        )
    }

    suspend fun generateOpeningMessage(bot: BotEntity): String = withContext(Dispatchers.IO) {
        val settings = getOrCreateSettings()

        val systemPrompt = buildSystemPrompt(bot, settings, includeStyleGuide = false) + if (bot.writingStyle == "rp") {
            "\n\nGörev: Bu sahneyi başlatan bir açılış anı yaz. Üçüncü tekil şahıs, roman/RP tarzı, betimleme + diyalog içersin. 3-6 cümle. Sadece sahneyi yaz."
        } else {
            "\n\nGörev: Bu senaryoya uygun kısa bir ilk mesaj yaz. Sadece mesajı yaz."
        }

        val requestMsgs = listOf(MessageEntity(id = "init", botId = bot.id, role = "user", text = "Sahneyi/mesajı başlat.", timestamp = 0L))

        val result = executeModelRequestWithFallback(getEffectiveModelForProvider(settings), settings, systemPrompt, requestMsgs, botId = bot.id)
        recordTokenUsage(bot.id, result.promptTokens, result.candidateTokens)
        return@withContext result.text
    }

    suspend fun updateMemorySummaries(bot: BotEntity, messages: List<MessageEntity>) = withContext(Dispatchers.IO) {
        if (messages.size < 6) return@withContext

        val settings = getOrCreateSettings()
        val apiKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return@withContext

        val aiName = if (bot.mode == "universe") bot.universeName else bot.aiName
        val userLabel = bot.userCharName.ifBlank { "Kullanıcı" }

        val recapText = messages.takeLast(16).joinToString("\n") { m ->
            val sender = if (m.role == "user") userLabel else aiName
            "$sender: ${m.text}"
        }

        val prompt = "Aşağıdaki sahneden iki ayrı liste çıkar. Özetlerken şu tür detayları KESİNLİKLE atlama: isimler, tarihler/zaman ifadeleri, verilen sözler/vaatler, açıklanan sırlar, büyük duygusal anlar (itiraf, ihanet, kavga), fiziksel/mekansal detaylar (nerede yaşıyor, işi ne).\nSADECE şu formatta yaz, başka hiçbir şey ekleme:\n\nDURUM:\n- (yan karakterler, mekanlar, çözülmemiş olaylar)\n\nHAFIZA:\n- (duygusal gelişmeler, ilişki değişimleri, verilen sözler, kişisel bilgiler)"

        try {
            val requestMsgs = listOf(MessageEntity(id = "sum", botId = bot.id, role = "user", text = "$prompt\n\nSAHNE:\n$recapText", timestamp = 0L))
            val customProviders = try { db.customProviderDao().getAllProviders() } catch (_: Exception) { emptyList() }
            val customMap = customProviders.associateBy { it.id }
            val providerKey = settings.selectedProvider
            val customP = if (providerKey.startsWith("custom_")) customMap[providerKey.removePrefix("custom_").toLongOrNull()] else null
            val utilityModel = resolveUtilityModel(providerKey, settings, customP)
            android.util.Log.d("EmochiRepository", "[updateMemorySummaries] utility model='$utilityModel' provider='$providerKey'")
            val res = callGeminiApi(apiKey, utilityModel, "Sen yardımcı bir özetleyicisin.", requestMsgs)
            recordTokenUsage(bot.id, res.promptTokens, res.candidateTokens)
            val raw = res.text

            if (raw.contains("DURUM:", ignoreCase = true) || raw.contains("HAFIZA:", ignoreCase = true)) {
                val durumMatch = raw.split(Regex("HAFIZA:", RegexOption.IGNORE_CASE))[0]
                    .replace(Regex("DURUM:", RegexOption.IGNORE_CASE), "").trim()

                val hafizaMatch = raw.split(Regex("HAFIZA:", RegexOption.IGNORE_CASE)).getOrNull(1)?.trim() ?: ""

                // Save to permanent RAG Memory Facts & Events tables
                saveMemoryFragmentsFromSummary(bot.id, durumMatch, hafizaMatch, apiKey)

                val newStory = listOf(bot.storyNotes, durumMatch).filter { it.isNotBlank() }.joinToString("\n")
                    .lines().map { it.trim() }.filter { it.isNotBlank() }.distinct().takeLast(50).joinToString("\n")

                val newMemory = listOf(bot.memoryNotes, hafizaMatch).filter { it.isNotBlank() }.joinToString("\n")
                    .lines().map { it.trim() }.filter { it.isNotBlank() }.distinct().takeLast(50).joinToString("\n")

                val updatedBot = bot.copy(
                    storyNotes = newStory,
                    memoryNotes = newMemory,
                    updatedAt = System.currentTimeMillis()
                )
                botDao.insertOrUpdate(updatedBot)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun performBackgroundSummarization(botId: String) = withContext(Dispatchers.IO) {
        val bot = botDao.getBotById(botId) ?: return@withContext
        val messages = messageDao.getMessagesForBotList(botId)

        if (messages.size > 14) {
            val keepCount = 12
            val olderMessages = messages.dropLast(keepCount)
            if (olderMessages.isNotEmpty()) {
                summarizeAndArchiveOlderMessages(bot, olderMessages)
            }
        }
        botDao.setNeedsSummarization(botId, false)
    }
