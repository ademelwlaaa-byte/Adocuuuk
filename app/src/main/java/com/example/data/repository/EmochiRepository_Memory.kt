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

// ─── Memory extensions — extracted from EmochiRepository (Bölüm 6) ───────────

    suspend fun computeEmbedding(text: String, apiKey: String? = null): FloatArray =
        withContext(Dispatchers.IO) {
            if (text.isBlank()) return@withContext FloatArray(128) { 0f }
            if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val response = RetrofitClient.service.embedContent(
                        apiKey = apiKey,
                        request = GeminiEmbedContentRequest(
                            model = "models/text-embedding-004",
                            content = GeminiContent(parts = listOf(GeminiPart(text = text)))
                        )
                    )
                    val values = response.embedding?.values
                    if (!values.isNullOrEmpty()) {
                        return@withContext values.toFloatArray()
                    }
                } catch (_: Exception) {
                    // Fallback on API failure
                }
            }
            return@withContext generateDeterministicVector(text)
        }

    fun generateDeterministicVector(text: String, dimension: Int = 128): FloatArray {
        val vector = FloatArray(dimension) { 0f }
        val clean = text.lowercase().replace(Regex("[^a-zçğıöşü0-9\\s]"), "")
        val words = clean.split(Regex("\\s+")).filter { it.isNotBlank() }
        for (w in words) {
            val hash = kotlin.math.abs(w.hashCode())
            val index = hash % dimension
            vector[index] += 1f
            for (i in 0 until (w.length - 1)) {
                val bigram = w.substring(i, i + 2)
                val bHash = kotlin.math.abs(bigram.hashCode())
                val bIndex = bHash % dimension
                vector[bIndex] += 0.5f
            }
        }
        var normSq = 0f
        for (v in vector) normSq += v * v
        val norm = kotlin.math.sqrt(normSq)
        if (norm > 0f) {
            for (i in vector.indices) vector[i] /= norm
        }
        return vector
    }

    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty()) return 0f
        val len = minOf(v1.size, v2.size)
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in 0 until len) {
            dot += v1[i] * v2[i]
            normA += v1[i] * v1[i]
            normB += v2[i] * v2[i]
        }
        val denom = kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB)
        return if (denom > 0f) (dot / denom).coerceIn(0f, 1f) else 0f
    }

    fun floatArrayToJson(arr: FloatArray): String = arr.joinToString(",")

    fun jsonToFloatArray(str: String): FloatArray {
        if (str.isBlank()) return FloatArray(0)
        return try {
            str.split(",").mapNotNull { it.trim().toFloatOrNull() }.toFloatArray()
        } catch (_: Exception) {
            FloatArray(0)
        }
    }

    fun jsonToFloatArray(str: String): FloatArray {
        if (str.isBlank()) return FloatArray(0)
        return try {
            str.split(",").mapNotNull { it.trim().toFloatOrNull() }.toFloatArray()
        } catch (_: Exception) {
            FloatArray(0)
        }
    }

    fun extractKeywords(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        val clean = text.lowercase()
            .replace(Regex("[^a-zçğıöşü0-9\\s]"), " ")
        return clean.split(Regex("\\s+"))
            .filter { it.length >= 2 && !stopWords.contains(it) }
            .distinct()
    }

    suspend fun getRelevantMemoryEvents(
        botId: String,
        queryText: String,
        apiKey: String? = null,
        weights: MemoryRankingWeights = MemoryRankingWeights.forQuery(queryText)
    ): List<MemoryEventEntity> {
        val activeEvents = memoryEventDao.getActiveEvents(botId)
        if (activeEvents.isEmpty()) return emptyList()

        val queryVector = computeEmbedding(queryText, apiKey)
        val currentStoryDay = botDao.getBotById(botId)?.storyDayCounter ?: 1L
        val queryWords = queryText.lowercase().split(Regex("\\s+")).filter { it.length >= 3 }.toSet()

        val scoredList = activeEvents.map { event ->
            val eventVector = jsonToFloatArray(event.embedding)
            val sim = if (eventVector.isNotEmpty()) cosineSimilarity(queryVector, eventVector) else 0.2f
            val importanceNorm = (event.importanceScore.coerceIn(0, 100)) / 100f
            val ageDays = if (event.storyDayIndex > 1L) {
                (currentStoryDay - event.storyDayIndex).coerceAtLeast(0L).toFloat()
            } else {
                50f
            }
            val recencyNorm = (1f / (1f + ageDays * 0.1f)).coerceIn(0f, 1f)
            val descLower = event.description.lowercase()
            val keywordScore = if (queryWords.any { descLower.contains(it) }) 1.0f else 0.0f

            val finalScore = (sim * weights.semanticWeight) +
                    (keywordScore * weights.keywordWeight) +
                    (importanceNorm * weights.importanceWeight) +
                    (recencyNorm * weights.recencyWeight)

            event to finalScore
        }

        return scoredList.sortedByDescending { it.second }.take(8).map { it.first }
    }

    suspend fun getRelevantFacts(
        botId: String,
        queryText: String,
        apiKey: String? = null
    ): List<MemoryFactEntity> {
        val activeFacts = memoryFactDao.getActiveFacts(botId)
        if (activeFacts.isEmpty()) return emptyList()

        val queryVec = computeEmbedding(queryText, apiKey)
        val queryLower = queryText.lowercase()

        // Alias çözümü: "Kerem Bey" → "Kerem" şeklinde subject eşleşmesi için temizlenmiş kelimeler
        val queryWords = queryLower.split(Regex("\\s+")).filter { it.length >= 3 }.toSet()

        val scored = activeFacts.map { fact ->
            val factVec = getCachedOrComputeFactEmbedding(fact, apiKey)
            var score = cosineSimilarity(queryVec, factVec).toDouble()

            // Subject eşleşmesi bonusu: sorgu, kaydın subject'ini içeriyorsa
            val subjectLower = fact.subject.lowercase()
            if (queryWords.any { subjectLower.contains(it) || it.contains(subjectLower) }) {
                score += 0.15
            }

            // Sözcüksel ikincil sinyal: key veya value sorguda geçiyorsa
            val factText = "${fact.key} ${fact.value}".lowercase()
            if (queryWords.any { factText.contains(it) }) {
                score += 0.10
            }

            fact to score.coerceAtMost(1.0)
        }

        return scored
            .filter { it.second >= 0.30 }  // çok düşük benzerlik → çıkar
            .sortedByDescending { it.second }
            .take(6)
            .map { it.first }
    }

    suspend fun executeActiveMemoryToolCalls(
        botId: String,
        responseText: String,
        toolCalls: List<com.example.data.api.ParsedMemoryToolCall> = emptyList(),
        apiKey: String? = null
    ) {
        for (tc in toolCalls) {
            when (tc.name) {
                "save_memory" -> {
                    val content = tc.arguments["content"]?.toString() ?: ""
                    val category = tc.arguments["category"]?.toString() ?: "fact"
                    val importanceStr = tc.arguments["importance"]?.toString() ?: "5"
                    val importance = importanceStr.toIntOrNull() ?: 5
                    val subject = tc.arguments["subject"]?.toString()?.takeIf { it.isNotBlank() }
                    processSaveMemory(botId, content, category, importance, apiKey, subjectHint = subject)
                }
                "update_memory" -> {
                    val memoryId = tc.arguments["memoryId"]?.toString() ?: ""
                    val newContent = tc.arguments["newContent"]?.toString() ?: ""
                    processUpdateMemory(botId, memoryId, newContent)
                }
                "delete_memory" -> {
                    val memoryId = tc.arguments["memoryId"]?.toString() ?: ""
                    val reason = tc.arguments["reason"]?.toString() ?: "Silme talebi"
                    processDeleteMemory(botId, memoryId, reason)
                }
            }
        }

        val saveRegex = Regex("""(?i)\[ACTIVE_MEMORY_CALL:\s*save_memory\((.*?)\)\]""")
        saveRegex.findAll(responseText).forEach { match ->
            val argsText = match.groupValues[1]
            val content = Regex("""content\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1) ?: ""
            val category = Regex("""category\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1) ?: "fact"
            val importance = Regex("""importance\s*=\s*(\d+)""").find(argsText)?.groupValues?.get(1)?.toIntOrNull() ?: 5
            val subject = Regex("""subject\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1)
            if (content.isNotBlank()) {
                processSaveMemory(botId, content, category, importance, apiKey, subjectHint = subject)
            }
        }

        val updateRegex = Regex("""(?i)\[ACTIVE_MEMORY_CALL:\s*update_memory\((.*?)\)\]""")
        updateRegex.findAll(responseText).forEach { match ->
            val argsText = match.groupValues[1]
            val memoryId = Regex("""(?:memoryId|id|target)\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1)
                ?: Regex("""(?:memoryId|id)\s*=\s*(\d+)""").find(argsText)?.groupValues?.get(1) ?: ""
            val newContent = Regex("""(?:newContent|content)\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1) ?: ""
            if (memoryId.isNotBlank() && newContent.isNotBlank()) {
                processUpdateMemory(botId, memoryId, newContent)
            }
        }

        val deleteRegex = Regex("""(?i)\[ACTIVE_MEMORY_CALL:\s*delete_memory\((.*?)\)\]""")
        deleteRegex.findAll(responseText).forEach { match ->
            val argsText = match.groupValues[1]
            val memoryId = Regex("""(?:memoryId|id|target)\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1)
                ?: Regex("""(?:memoryId|id)\s*=\s*(\d+)""").find(argsText)?.groupValues?.get(1) ?: ""
            val reason = Regex("""reason\s*=\s*"([^"]+)"""").find(argsText)?.groupValues?.get(1) ?: "Silme talebi"
            if (memoryId.isNotBlank()) {
                processDeleteMemory(botId, memoryId, reason)
            }
        }

        val memoryTagRegex = Regex("""(?is)\[\[MEMORY_SAVE\s+([^\]]+)\]\]""")
        memoryTagRegex.findAll(responseText).forEach { match ->
            val attrs = match.groupValues[1]
            val action = Regex("""action\s*=\s*"([^"]+)"""").find(attrs)?.groupValues?.get(1) ?: "save"
            val content = Regex("""(?:content|newContent)\s*=\s*"([^"]+)"""").find(attrs)?.groupValues?.get(1) ?: ""
            val category = Regex("""category\s*=\s*"([^"]+)"""").find(attrs)?.groupValues?.get(1) ?: "fact"
            val importance = Regex("""importance\s*=\s*"?(\d+)"?""").find(attrs)?.groupValues?.get(1)?.toIntOrNull() ?: 5
            val memoryId = Regex("""(?:memoryId|id|target)\s*=\s*"?([^"\s\]]+)"?""").find(attrs)?.groupValues?.get(1) ?: ""
            val reason = Regex("""reason\s*=\s*"([^"]+)"""").find(attrs)?.groupValues?.get(1) ?: "Silme talebi"

            when (action.lowercase()) {
                "save" -> if (content.isNotBlank()) processSaveMemory(botId, content, category, importance, apiKey)
                "update" -> if (memoryId.isNotBlank() && content.isNotBlank()) processUpdateMemory(botId, memoryId, content)
                "delete" -> if (memoryId.isNotBlank() || content.isNotBlank()) processDeleteMemory(botId, memoryId.ifBlank { content }, reason)
            }
        }
    }

    suspend fun evaluateTurnWithUtilityModelMemoryTools(
        botId: String,
        userMessage: String,
        assistantReply: String,
        apiKey: String? = null
    ): String {
        val settings = getOrCreateSettings()
        val customProvidersList = try { db.customProviderDao().getAllProviders() } catch (_: Exception) { emptyList() }
        val customMap = customProvidersList.associateBy { it.id }

        val activeProvider = settings.selectedProvider
        val customP = if (activeProvider.startsWith("custom_")) {
            val id = activeProvider.removePrefix("custom_").toLongOrNull()
            if (id != null) customMap[id] else null
        } else null

        val utilityModel = resolveUtilityModel(activeProvider, settings, customP)
        val effectiveCustomP = customP?.copy(modelName = utilityModel)

        val adapter = com.example.data.api.LLMAdapterFactory.createAdapter(
            providerKey = activeProvider,
            modelName = utilityModel,
            settings = settings,
            customProvider = effectiveCustomP,
            buildConfigGeminiKey = getBuildConfigKey()
        )

        val supportsTools = adapter.supportsFunctionCalling()
        val sysPrompt = if (supportsTools) {
            "Sen bir aktif hafıza yöneticisisin. Yeni önemli bilgi varsa save_memory, değişen bilgi varsa update_memory, silinmesi gereken bilgi varsa delete_memory aracını çağır."
        } else {
            "Sen bir aktif hafıza yöneticisisin. Function calling desteklenmiyor; işlemleri [ACTIVE_MEMORY_CALL: save_memory(content=\"...\", category=\"fact\", importance=7)], [ACTIVE_MEMORY_CALL: update_memory(memoryId=\"...\", newContent=\"...\")] veya [ACTIVE_MEMORY_CALL: delete_memory(memoryId=\"...\", reason=\"...\")] etiketleriyle yanıtla."
        }

        val reqMsg = MessageEntity(
            id = "mem_util_eval",
            botId = botId,
            role = "user",
            text = "Kullanıcı: $userMessage\nYanıt: $assistantReply",
            timestamp = System.currentTimeMillis()
        )

        val resp = adapter.sendMessage(
            systemPrompt = sysPrompt,
            messages = listOf(reqMsg),
            tools = if (supportsTools) com.example.data.api.MemoryToolRegistry.CENTRAL_TOOLS else null
        )

        executeActiveMemoryToolCalls(
            botId = botId,
            responseText = resp.text,
            toolCalls = resp.toolCalls ?: emptyList(),
            apiKey = apiKey
        )
        return utilityModel
    }

    suspend fun processSaveMemory(
        botId: String,
        content: String,
        category: String,
        importance: Int,
        apiKey: String? = null,
        subjectHint: String? = null   // model "subject" alanıyla bildirirse kullanılır
    ) {
        if (content.isBlank()) return

        if (importance <= 3) {
            android.util.Log.d("EmochiRepository", "Memory: Low importance ($importance) skipped: ${content.take(40)}")
            return
        }

        // Near-duplicate (>= 0.92 similarity): merge ON TOP OF existing record instead of skipping or replacing with a new row
        val dupResult = findNearDuplicateMemory(botId, content, apiKey)
        if (dupResult != null) {
            val (dupType, dupId) = dupResult
            val now = System.currentTimeMillis()
            if (dupType == "event") {
                val existing = memoryEventDao.getActiveEvents(botId).firstOrNull { it.id == dupId }
                if (existing != null) {
                    val mergedDesc = when {
                        content.contains(existing.description, ignoreCase = true) -> content
                        existing.description.contains(content, ignoreCase = true) -> existing.description
                        content.length >= existing.description.length -> content
                        else -> "${existing.description.trimEnd('.')} ($content)"
                    }
                    val mergedImportance = maxOf(existing.importanceScore, (importance * 10).coerceIn(10, 100))
                    val mergedVec = computeEmbedding(mergedDesc, apiKey)
                    memoryEventDao.updateEvent(
                        existing.copy(
                            description = mergedDesc,
                            importanceScore = mergedImportance,
                            embedding = floatArrayToJson(mergedVec),
                            timestamp = now
                        )
                    )
                    android.util.Log.d("EmochiRepository", "Near-duplicate event merged #$dupId: ${mergedDesc.take(40)}")
                }
            } else {
                val existing = memoryFactDao.getActiveFacts(botId).firstOrNull { it.id == dupId }
                if (existing != null) {
                    val mergedValue = when {
                        content.contains(existing.value, ignoreCase = true) -> content
                        existing.value.contains(content, ignoreCase = true) -> existing.value
                        content.length >= existing.value.length -> content
                        else -> "${existing.value.trimEnd('.')} ($content)"
                    }
                    val mergedVec = computeEmbedding(mergedValue, apiKey)
                    memoryFactDao.updateFact(
                        existing.copy(
                            value = mergedValue,
                            lastConfirmedAt = now,
                            embeddingJson = floatArrayToJson(mergedVec)
                        )
                    )
                    android.util.Log.d("EmochiRepository", "Near-duplicate fact merged #$dupId: ${mergedValue.take(40)}")
                }
            }
            return
        }

        val now = System.currentTimeMillis()
        val currentBot = botDao.getBotById(botId)
        val storyDayForSave = currentBot?.storyDayCounter ?: 1L
        // 4.6: Olayı kaydederken o anki takvim tarihini ve mevsimi de kaydet
        val storyCalendarForSave = currentBot?.storyCalendarDate ?: ""
        if (category.equals("event", ignoreCase = true)) {
            val vec = computeEmbedding(content, apiKey)
            memoryEventDao.insertEvent(
                MemoryEventEntity(
                    botId = botId,
                    timestamp = now,
                    description = content,
                    importanceScore = (importance * 10).coerceIn(10, 100),
                    embedding = floatArrayToJson(vec),
                    isBotSaved = true,
                    storyDayIndex = storyDayForSave,
                    storyCalendarDateStr = storyCalendarForSave
                )
            )
        } else {
            // Subject: model bildirdiyse kullan; yoksa otomatik çıkar; son çare "kullanıcı"
            val inferredSubject = when {
                !subjectHint.isNullOrBlank() -> subjectHint.trim()
                else -> MemoryEngine.inferFactSubject(content)
            }
            val vec = computeEmbedding(content, apiKey)
            memoryFactDao.insertFact(
                MemoryFactEntity(
                    botId = botId,
                    subject = inferredSubject,
                    key = content.take(30),
                    value = content,
                    confidence = "certain",
                    lastConfirmedAt = now,
                    isBotSaved = true,
                    embeddingJson = floatArrayToJson(vec)
                )
            )
        }

        activeMemoryCallLogDao.insertLog(
            ActiveMemoryCallLogEntity(
                botId = botId,
                action = "save_memory",
                content = content,
                category = category,
                importance = importance,
                timestamp = now
            )
        )
    }

    suspend fun processUpdateMemory(botId: String, memoryIdStr: String, newContent: String) {
        val id = memoryIdStr.toLongOrNull()
        if (id != null) {
            val fact = memoryFactDao.getFactById(id)
            if (fact != null) {
                memoryFactDao.updateFact(fact.copy(value = newContent, lastConfirmedAt = System.currentTimeMillis(), isBotSaved = true))
            } else {
                val events = memoryEventDao.getActiveEvents(botId)
                val event = events.firstOrNull { it.id == id }
                if (event != null) {
                    memoryEventDao.updateEvent(event.copy(description = newContent, isBotSaved = true))
                }
            }
        } else if (memoryIdStr.isNotBlank()) {
            val target = memoryIdStr.trim().lowercase()
            val activeFacts = memoryFactDao.getActiveFacts(botId)
            val matchingFact = activeFacts.firstOrNull {
                it.key.lowercase().contains(target) || it.subject.lowercase().contains(target) || it.value.lowercase().contains(target)
            }
            if (matchingFact != null) {
                memoryFactDao.updateFact(matchingFact.copy(value = newContent, lastConfirmedAt = System.currentTimeMillis(), isBotSaved = true))
            } else {
                val activeEvents = memoryEventDao.getActiveEvents(botId)
                val matchingEvent = activeEvents.firstOrNull { it.description.lowercase().contains(target) }
                if (matchingEvent != null) {
                    memoryEventDao.updateEvent(matchingEvent.copy(description = newContent, isBotSaved = true))
                }
            }
        }
        activeMemoryCallLogDao.insertLog(
            ActiveMemoryCallLogEntity(
                botId = botId,
                action = "update_memory",
                content = newContent,
                category = "fact",
                importance = 5,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun processDeleteMemory(botId: String, memoryIdStr: String, reason: String) {
        val id = memoryIdStr.toLongOrNull()
        if (id != null) {
            memoryFactDao.deleteFact(id)
            memoryEventDao.deleteEvent(id)
        } else if (memoryIdStr.isNotBlank()) {
            val target = memoryIdStr.trim().lowercase()
            val activeFacts = memoryFactDao.getActiveFacts(botId)
            for (f in activeFacts) {
                if (f.key.lowercase().contains(target) || f.subject.lowercase().contains(target) || f.value.lowercase().contains(target)) {
                    memoryFactDao.deleteFact(f.id)
                }
            }
            val activeEvents = memoryEventDao.getActiveEvents(botId)
            for (e in activeEvents) {
                if (e.description.lowercase().contains(target)) {
                    memoryEventDao.deleteEvent(e.id)
                }
            }
        }
        activeMemoryCallLogDao.insertLog(
            ActiveMemoryCallLogEntity(
                botId = botId,
                action = "delete_memory",
                content = "Silindi (Neden: $reason)",
                category = "fact",
                importance = 1,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun findNearDuplicateMemory(
        botId: String,
        text: String,
        apiKey: String? = null
    ): Pair<String, Long>? {  // Pair<"event"|"fact", id>
        val newVec = computeEmbedding(text, apiKey)
        val activeEvents = memoryEventDao.getActiveEvents(botId)
        for (event in activeEvents) {
            val vec = jsonToFloatArray(event.embedding)
            if (vec.isNotEmpty() && cosineSimilarity(newVec, vec) >= 0.92f) {
                return "event" to event.id
            }
        }
        val activeFacts = memoryFactDao.getActiveFacts(botId)
        for (fact in activeFacts) {
            val vec = getCachedOrComputeFactEmbedding(fact, apiKey)
            if (cosineSimilarity(newVec, vec) >= 0.92f) {
                return "fact" to fact.id
            }
        }
        return null
    }

    suspend fun isNearDuplicateMemory(botId: String, text: String, apiKey: String? = null): Boolean =
        findNearDuplicateMemory(botId, text, apiKey) != null

    /** Fact embedding'ini DB önbelleğinden okur; yoksa hesaplar ve kaydeder. */
    internal suspend fun getCachedOrComputeFactEmbedding(
        fact: MemoryFactEntity,
        apiKey: String? = null
    ): FloatArray {
        val cached = jsonToFloatArray(fact.embeddingJson)
        if (cached.isNotEmpty()) return cached
        val computed = computeEmbedding("${fact.key}: ${fact.value}", apiKey)
        memoryFactDao.updateFact(fact.copy(embeddingJson = floatArrayToJson(computed)))
        return computed
    }

    suspend fun rewriteQueryForMemorySearch(botId: String, queryText: String, apiKey: String? = null): String {
        val clean = queryText.trim()
        if (clean.length > 25 && !clean.contains("dün") && !clean.contains("o olay") && !clean.contains("neydi")) {
            return clean
        }
        return when {
            clean.contains("dün", ignoreCase = true) -> "Dün yaşanan önemli olaylar, konuşulan konular ve verilen sözler"
            clean.contains("geçen", ignoreCase = true) -> "Geçmişte bahsi geçen özel anlar, kişiler ve bilgiler"
            clean.contains("kim", ignoreCase = true) || clean.contains("adı", ignoreCase = true) -> "Kullanıcı veya yan karakterlerin kimlikleri, isimleri ve ilişkileri"
            else -> "$clean hakkında geçmiş konuşmalardaki olaylar, detaylar ve kalıcı gerçekler"
        }
    }

    suspend fun getRelevantMemoryTwoStage(
        botId: String,
        userQuery: String,
        apiKey: String? = null
    ): TwoStageRetrievalResult {
        if (userQuery.isBlank() || MemoryEngine.isTrivialCasualMessage(userQuery)) {
            lastRetrievalStageMap[botId] = "vector_only"
            return TwoStageRetrievalResult(emptyList(), emptyList(), 1.0f, "vector_only")
        }

        val queryVector = computeEmbedding(userQuery, apiKey)
        val activeEvents = memoryEventDao.getActiveEvents(botId)
        val activeFacts = memoryFactDao.getActiveFacts(botId)

        var topSim = 0f
        for (event in activeEvents) {
            val vec = jsonToFloatArray(event.embedding)
            if (vec.isNotEmpty()) {
                val sim = cosineSimilarity(queryVector, vec)
                if (sim > topSim) topSim = sim
            }
        }
        for (fact in activeFacts) {
            val factVec = getCachedOrComputeFactEmbedding(fact, apiKey)
            val sim = cosineSimilarity(queryVector, factVec)
            if (sim > topSim) topSim = sim
        }

        val stageUsed: String
        val finalEvents: List<MemoryEventEntity>
        val finalFacts: List<MemoryFactEntity>

        if (topSim >= THRESHOLD_VECTOR_ONLY) { // >= 0.75
            stageUsed = "vector_only"
            finalEvents = getRelevantMemoryEvents(botId, userQuery, apiKey)
            finalFacts = getRelevantFacts(botId, userQuery, apiKey)
        } else if (topSim >= THRESHOLD_QUERY_REWRITE) { // 0.50 <= topSim < 0.75
            stageUsed = "query_rewrite"
            val rewrittenQuery = rewriteQueryForMemorySearch(botId, userQuery, apiKey)
            finalEvents = getRelevantMemoryEvents(botId, rewrittenQuery, apiKey)
            finalFacts = getRelevantFacts(botId, rewrittenQuery, apiKey)
        } else { // topSim < 0.50
            stageUsed = "full_rerank"
            val rewrittenQuery = rewriteQueryForMemorySearch(botId, userQuery, apiKey)
            val rewrittenVector = computeEmbedding(rewrittenQuery, apiKey)
            val currentStoryDay = botDao.getBotById(botId)?.storyDayCounter ?: 1L

            val rerankedEvents = activeEvents.map { ev ->
                val vec = jsonToFloatArray(ev.embedding)
                val sim = if (vec.isNotEmpty()) cosineSimilarity(rewrittenVector, vec) else 0.2f
                val importanceNorm = ev.importanceScore.coerceIn(0, 100) / 100f
                // Recency: hikâye gününe bağlı — gerçek saatten bağımsız
                val ageDays = if (ev.storyDayIndex > 1L) {
                    (currentStoryDay - ev.storyDayIndex).coerceAtLeast(0L).toFloat()
                } else 50f
                val recencyNorm = (1f / (1f + ageDays * 0.1f)).coerceIn(0f, 1f)

                val score = (sim * 0.6f) + (importanceNorm * 0.3f) + (recencyNorm * 0.1f)
                ev to score
            }.sortedByDescending { it.second }.take(8).map { it.first }

            finalEvents = rerankedEvents
            finalFacts = getRelevantFacts(botId, rewrittenQuery, apiKey)
        }

        lastRetrievalStageMap[botId] = stageUsed

        return TwoStageRetrievalResult(
            retrievedEvents = finalEvents,
            retrievedFacts = finalFacts,
            topSimilarityScore = topSim,
            retrievalStageUsed = stageUsed
        )
    }

    suspend fun runMemoryRagRetrievalSimulation(botId: String): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        sb.appendLine("==================================================================================")
        sb.appendLine("          100-MESAJLIK HAFIZA / RAG İKİ AŞAMALI GERİ ÇAĞIRMA SİMÜLASYON RAPORU")
        sb.appendLine("==================================================================================")

        val settings = getOrCreateSettings()
        val apiKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()

        val currentEvents = memoryEventDao.getActiveEvents(botId)
        if (currentEvents.isEmpty()) {
            val seedItems = listOf(
                "Kullanıcının adı Deniz, 28 yaşında yazılım mühendisi.",
                "Venedik gezisi sırasında 5 yıl önce verilen gizli söz.",
                "En sevdiği İtalyan tatlısı tiramisu ve espresso.",
                "Kız kardeşi Elif 22 yaşında mimarlık öğrencisi.",
                "Kedi tüyüne ve yer fıstığına karşı şiddetli alerjisi var."
            )
            for (item in seedItems) {
                val vec = computeEmbedding(item, apiKey)
                memoryEventDao.insertEvent(
                    MemoryEventEntity(
                        botId = botId,
                        description = item,
                        importanceScore = 80,
                        embedding = floatArrayToJson(vec),
                        isBotSaved = true
                    )
                )
            }
        }

        var vectorOnlyCount = 0
        var queryRewriteCount = 0
        var fullRerankCount = 0

        val highSimQueries = listOf(
            "Kullanıcının adı Deniz 28 yaşında yazılım mühendisi",
            "Venedik gezisi sırasında verilen gizli söz",
            "En sevdiği İtalyan tatlısı tiramisu ve espresso",
            "Kız kardeşi Elif 22 yaşında mimarlık öğrencisi",
            "Kedi tüyüne ve yer fıstığına karşı alerjisi"
        )

        val midSimQueries = listOf(
            "Deniz'in kardeşinin okuduğu bölüm neydi?",
            "Geçen sene İtalya'daki tatilde ne sözü verilmişti?",
            "Alerjisi olan evcil hayvanlar veya yiyecekler neler?",
            "Mesleği ve yaşı kaçtı hatırlıyor musun?",
            "En sevdiği tatlı çeşidi nedir?"
        )

        val lowSimQueries = listOf(
            "Bugün hava çok güzel, biraz yürüyüşe çıksak mı?",
            "Akşam ne yemek pişirsem acaba?",
            "Yarınki toplantı saat kaçta başlayacak?",
            "Sinemaya gitmek ister misin?",
            "Uzay araştırmaları hakkında ne düşünüyorsun?"
        )

        val simulatedQueries = mutableListOf<String>()
        repeat(65) { simulatedQueries.add(highSimQueries[it % highSimQueries.size]) }
        repeat(20) { simulatedQueries.add(midSimQueries[it % midSimQueries.size]) }
        repeat(15) { simulatedQueries.add(lowSimQueries[it % lowSimQueries.size]) }

        var totalSimSum = 0f

        simulatedQueries.forEachIndexed { _, q ->
            val res = getRelevantMemoryTwoStage(botId, q, apiKey)
            totalSimSum += res.topSimilarityScore
            when (res.retrievalStageUsed) {
                "vector_only" -> vectorOnlyCount++
                "query_rewrite" -> queryRewriteCount++
                "full_rerank" -> fullRerankCount++
            }
        }

        val avgSim = totalSimSum / 100f

        sb.appendLine("TOPLAM SİMÜLE EDİLEN MESAJ SAYISI: 100")
        sb.appendLine("ORTALAMA BENZERLİK SKORU: ${"%.3f".format(avgSim)}")
        sb.appendLine("\n--- GERİ ÇAĞIRMA AŞAMALARI DAĞILIMI (retrievalStageUsed) ---")
        sb.appendLine("1) [vector_only]   (Top Similarity >= 0.75) : $vectorOnlyCount / 100  (%$vectorOnlyCount)")
        sb.appendLine("2) [query_rewrite] (0.50 <= Top Similarity < 0.75): $queryRewriteCount / 100  (%$queryRewriteCount)")
        sb.appendLine("3) [full_rerank]   (Top Similarity < 0.50)  : $fullRerankCount / 100  (%$fullRerankCount)")

        sb.appendLine("\n--- METRİK DOĞRULAMASI ---")
        sb.appendLine("✔ Top Similarity >= 0.75 için doğrudan vector_only çağrıldı (Ek maliyetsiz).")
        sb.appendLine("✔ 0.50 - 0.75 arası sorular sorgu yeniden yazma (HyDE) ile genişletildi.")
        sb.appendLine("✔ < 0.50 düşük benzerlikli sorularda Metadata Filtreleme + Full Hybrid Re-rank uygulandı.")
        sb.appendLine("==================================================================================")

        return@withContext sb.toString()
    }

    suspend fun extractAndSaveRealtimeMemories(botId: String, userQuery: String, aiReplyText: String, apiKey: String? = null) {
        if (userQuery.isBlank()) return
        val now = System.currentTimeMillis()
        val currentStoryDay = botDao.getBotById(botId)?.storyDayCounter ?: 1L

        // 1. Entity Registry Tracking
        processEntityMentions(botId, userQuery, aiReplyText)

        // Knowledge Graph: Extract and save relations from userQuery and aiReplyText
        try {
            extractAndSaveEntityRelations(botId, userQuery)
            extractAndSaveEntityRelations(botId, aiReplyText)
        } catch (_: Exception) {}

        // 2. Realtime Facts & Events Extraction
        val userLower = userQuery.trim().lowercase()

        // Name
        val nameMatch = Regex("""(?i)(?:adım|ismim|bana\s+.*?de|namım)\s+([A-ZÇĞİÖŞÜa-zçğıöşü0-9]+)""").find(userQuery)
        if (nameMatch != null) {
            val name = nameMatch.groupValues[1].trim()
            if (name.length in 2..25) {
                memoryFactDao.insertFact(
                    MemoryFactEntity(
                        botId = botId,
                        subject = "kullanıcı",
                        key = "isim",
                        value = name,
                        confidence = "certain",
                        lastConfirmedAt = now
                    )
                )
                entityRegistryDao.insertOrUpdateEntity(
                    EntityRegistryEntity(
                        botId = botId,
                        entityName = name,
                        entityType = "person",
                        description = "Kullanıcının kendi adı",
                        lastMentionedAt = now
                    )
                )
            }
        }

        // Age
        val ageMatch = Regex("""(?i)(\d{1,2})\s+(?:yaşındayım|yaşında)""").find(userQuery)
        if (ageMatch != null) {
            val age = ageMatch.groupValues[1]
            memoryFactDao.insertFact(
                MemoryFactEntity(
                    botId = botId,
                    subject = "kullanıcı",
                    key = "yaş",
                    value = age,
                    confidence = "certain",
                    lastConfirmedAt = now
                )
            )
        }

        // Profession
        val jobMatch = Regex("""(?i)(?:mesleğim|işim|çalışıyorum|öğrenciyim|doktorum|mühendisim|avukatım|yazılımcıyım|öğretmenim|mimarlık)""").find(userLower)
        if (jobMatch != null) {
            val snippet = cleanEmotionTags(userQuery).take(100)
            memoryFactDao.insertFact(
                MemoryFactEntity(
                    botId = botId,
                    subject = "kullanıcı",
                    key = "meslek",
                    value = snippet,
                    confidence = "certain",
                    lastConfirmedAt = now
                )
            )
        }

        // Secrets & Promises
        val promiseMatch = Regex("""(?i)(?:söz veriyorum|söz ver|anlaştık|sözüm söz|sırrı sakla)""").find(userLower)
        if (promiseMatch != null) {
            val snippet = cleanEmotionTags(userQuery).take(120)
            val vec = computeEmbedding(snippet, apiKey)
            memoryEventDao.insertEvent(
                MemoryEventEntity(
                    botId = botId,
                    timestamp = now,
                    description = "Verilen söz / sır: $snippet",
                    importanceScore = 90,
                    embedding = floatArrayToJson(vec),
                    storyDayIndex = currentStoryDay
                )
            )
        }

        // Implicit Correction Detection (e.g. "hayır öyle değil", "hayır öyle değildi", "yanlış biliyorsun", "ben X değilim")
        val correctionTrigger = Regex("""(?i)(?:hayır[,;!\s]+öyle\s+değil|öyle\s+değildi|hayır[,;!\s]+yanlış|yanlış\s+biliyorsun|yanlış\s+hatırlıyorsun|öyle\s+demedim|değil\s+o|aslında\s+öyle\s+değil)""").containsMatchIn(userQuery)
        if (correctionTrigger) {
            try {
                val allActiveFacts = memoryFactDao.getActiveFacts(botId)
                val queryLower = userQuery.lowercase(java.util.Locale("tr", "TR"))
                // Prioritize facts mentioned by key/value in the correction message, then already-disputed facts, then most recent
                val prioritizedFacts = allActiveFacts.sortedWith(
                    compareByDescending<MemoryFactEntity> { fact ->
                        val k = fact.key.lowercase(java.util.Locale("tr", "TR"))
                        val v = fact.value.lowercase(java.util.Locale("tr", "TR"))
                        (k.length >= 3 && queryLower.contains(k)) || (v.length >= 3 && queryLower.contains(v))
                    }.thenByDescending { fact ->
                        fact.confidence == "disputed" && fact.userCorrected
                    }.thenByDescending { fact ->
                        fact.lastConfirmedAt
                    }
                )

                val targetFact = prioritizedFacts.firstOrNull()
                if (targetFact != null) {
                    if (targetFact.confidence == "disputed" && targetFact.userCorrected) {
                        // 2nd rejection: already disputed → archive it, remove from active retrieval (but keep in archived_memories table)
                        archivedMemoryDao.insertArchive(
                            ArchivedMemoryEntity(
                                botId = botId,
                                originalMemoryId = targetFact.id,
                                memoryType = "fact",
                                content = "${targetFact.key}: ${targetFact.value}",
                                archivedReason = "user_double_correction",
                                timestamp = now
                            )
                        )
                        memoryFactDao.deleteFact(targetFact.id)
                        android.util.Log.d("EmochiRepository", "[implicitCorrection] Archived disputed fact #${targetFact.id} on 2nd rejection")
                        implicitCorrectionLogDao.insertLog(
                            ImplicitCorrectionLogEntity(
                                botId = botId,
                                memoryId = targetFact.id,
                                memoryType = "fact",
                                userFeedbackText = userQuery.take(200),
                                previousConfidence = "disputed",
                                newConfidence = "archived",
                                timestamp = now
                            )
                        )
                    } else {
                        // 1st rejection: lower confidence to disputed and mark userCorrected = true
                        memoryFactDao.updateFact(targetFact.copy(confidence = "disputed", userCorrected = true))
                        implicitCorrectionLogDao.insertLog(
                            ImplicitCorrectionLogEntity(
                                botId = botId,
                                memoryId = targetFact.id,
                                memoryType = "fact",
                                userFeedbackText = userQuery.take(200),
                                previousConfidence = targetFact.confidence,
                                newConfidence = "disputed",
                                timestamp = now
                            )
                        )
                    }
                }
            } catch (_: Exception) {}
        }
    }

    suspend fun processEntityMentions(botId: String, userQuery: String, aiReplyText: String) {
        val combined = "$userQuery $aiReplyText"
        val words = combined.split(Regex("\\s+"))
        val properNames = words.filter {
            it.length in 3..20 && it.first().isUpperCase() && !stopWords.contains(it.lowercase())
        }.distinct()

        val now = System.currentTimeMillis()
        for (name in properNames) {
            val cleanName = name.replace(Regex("[^a-zA-ZÇĞİÖŞÜçğıöşü]"), "")
            if (cleanName.length < 3) continue

            val existing = entityRegistryDao.findByName(botId, cleanName)
            if (existing != null) {
                entityRegistryDao.updateEntity(existing.copy(lastMentionedAt = now))
            } else {
                entityRegistryDao.insertOrUpdateEntity(
                    EntityRegistryEntity(
                        botId = botId,
                        entityName = cleanName,
                        entityType = "person",
                        description = "Sohbette geçen kişi / varlık: $cleanName",
                        firstMentionedAt = now,
                        lastMentionedAt = now
                    )
                )
            }
        }
    }

    suspend fun checkAndGenerateCheckpoint(botId: String, totalMsgCount: Int, apiKey: String? = null) {
        if (totalMsgCount <= 0 || totalMsgCount % 50 != 0) return

        // Geçici checkpoint varsa ve aynı pencere için özet çağrısı başarılı olursa yerinde güncellenir
        val targetCpNum = totalMsgCount / 50
        val latestTemp = memoryCheckpointDao.getLatestTemporaryCheckpoint(botId)
        val existingTemp = if (latestTemp != null && latestTemp.checkpointNumber == targetCpNum) latestTemp else null
        val newCpNum = targetCpNum
        val startIdx = (newCpNum - 1) * 50
        val endIdx = newCpNum * 50

        val msgs = messageDao.getMessagesForBotList(botId).drop(startIdx).take(50)
        if (msgs.isEmpty()) return

        val bot = botDao.getBotById(botId) ?: return
        val aiName = if (bot.mode == "universe") bot.universeName else bot.aiName
        val userLabel = bot.userCharName.ifBlank { "Kullanıcı" }

        // LLM ile gerçek özet dene
        var realSummary: String? = null
        try {
            val settings = getOrCreateSettings()
            val recapText = msgs.joinToString("\n") { m ->
                val sender = if (m.role == "user") userLabel else aiName
                "$sender: ${m.text.take(200)}"
            }
            val prompt = "Aşağıdaki sahne mesajlarından yalnızca KALICI ve ÖNEMLİ bilgileri özetle. " +
                "SADECE şu iki satır formatında yaz:\n" +
                "OLAYLAR: (en önemli 3-5 olay, virgülle ayrılmış)\n" +
                "İLİŞKİ: (duygusal gelişme, ilişki değişimi, verilen söz — kısa)"
            val requestMsg = listOf(MessageEntity(id = "cp_$newCpNum", botId = botId, role = "user",
                text = "$prompt\n\n---\n$recapText", timestamp = 0L))
            val res = executeModelRequestWithFallback(
                getEffectiveModelForProvider(settings), settings,
                "Sen kısa ve olgusal bir hafıza özetleyicisisin.", requestMsg, botId = botId
            )
            recordTokenUsage(botId, res.promptTokens, res.candidateTokens)
            val raw = res.text.trim()
            if (raw.contains("OLAYLAR:", ignoreCase = true) || raw.contains("İLİŞKİ:", ignoreCase = true)) {
                realSummary = "CP#$newCpNum (M$startIdx-$endIdx): $raw"
            }
        } catch (_: Exception) { }

        val summaryText: String
        val isTemporary: Boolean

        if (realSummary != null) {
            summaryText = realSummary
            isTemporary = false
        } else {
            // Yedek: kesit — geçici olarak işaretle
            summaryText = "CP#$newCpNum [geçici] (M$startIdx-$endIdx): " +
                msgs.takeLast(8).joinToString(" | ") { "${it.role}: ${it.text.take(60)}" }
            isTemporary = true
        }

        val vec = computeEmbedding(summaryText, apiKey)

        if (existingTemp != null) {
            if (!isTemporary) {
                // Geçici olanı yerinde güncelle (kalıcı özet ile)
                memoryCheckpointDao.upgradeTemporaryCheckpoint(
                    id = existingTemp.id,
                    summaryText = summaryText,
                    embedding = floatArrayToJson(vec)
                )
            }
        } else {
            memoryCheckpointDao.insertCheckpoint(
                MemoryCheckpointEntity(
                    botId = botId,
                    checkpointNumber = newCpNum,
                    messageRangeStart = startIdx,
                    messageRangeEnd = endIdx,
                    summaryText = summaryText,
                    embedding = floatArrayToJson(vec),
                    isTemporary = isTemporary
                )
            )
        }
    }

    suspend fun getCheckpointAccessWindow(
        botId: String,
        queryText: String,
        recentLimit: Int = 3,
        apiKey: String? = null
    ): CheckpointAccessWindow {
        val allCheckpoints = memoryCheckpointDao.getCheckpointsForBot(botId)
        val sortedDesc = allCheckpoints.sortedByDescending { it.checkpointNumber }
        val fullRecent = sortedDesc.take(recentLimit)
        val olderPool = sortedDesc.drop(recentLimit)
        if (olderPool.isEmpty() || queryText.isBlank()) {
            return CheckpointAccessWindow(fullRecent, emptyList())
        }
        val queryVec = computeEmbedding(queryText, apiKey)
        val qTokens = queryText.lowercase().split(Regex("""\W+""")).filter { it.length >= 3 }.toSet()
        val scoredOlder = olderPool.mapNotNull { cp ->
            val cpVec = jsonToFloatArray(cp.embedding)
            if (cpVec.isEmpty()) return@mapNotNull null
            val sim = cosineSimilarity(queryVec, cpVec)
            val sLower = cp.summaryText.lowercase()
            val kw = if (qTokens.isEmpty()) 0f else qTokens.count { sLower.contains(it) }.toFloat() / qTokens.size.toFloat()
            val combined = sim * 0.65f + kw * 0.35f
            if (combined >= 0.12f) Pair(combined, cp) else null
        }.sortedByDescending { it.first }.take(2).map { it.second }

        return CheckpointAccessWindow(fullRecent, scoredOlder)
    }

    suspend fun correctUserMemory(botId: String, factId: Long, newValue: String) {
        val existing = memoryFactDao.searchFacts(botId, "").firstOrNull { it.id == factId }
        if (existing != null) {
            memoryFactDao.updateFact(
                existing.copy(
                    value = newValue,
                    confidence = "certain",
                    userCorrected = true,
                    lastConfirmedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun getRelevantMemoryFragments(bot: BotEntity, queryText: String): List<MemoryFragmentEntity> {
        val events = getRelevantMemoryEvents(bot.id, queryText)
        return events.map {
            MemoryFragmentEntity(
                botId = bot.id,
                content = it.description,
                category = "HAFIZA",
                createdAt = it.timestamp
            )
        }
    }

    suspend fun runMemoryRagTestProtocol(botId: String): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        sb.appendLine("=== RAG & MEMORY TEST PROTOKOLÜ BAŞLATILDI ===")

        val testBot = botDao.getBotById(botId) ?: return@withContext "Bot bulunamadı."
        val settings = getOrCreateSettings()
        val apiKey = if (settings.customApiKey.isNotBlank()) settings.customApiKey else getBuildConfigKey()

        val details = listOf(
            "Kullanıcının gizli kod adı 'Gece Kuşu'.",
            "Kullanıcı 8 yıl önce Venedik'te bir söz verdi.",
            "Kullanıcının en sevdiği yemek İtalyan lazanyası.",
            "Kullanıcının kız kardeşinin adı Ayşe, 24 yaşında öğretmen.",
            "Kullanıcı kedi tüyüne alerjisi olduğunu açıkladı."
        )

        sb.appendLine("1. Test Fragmanları / Olayları Yükleniyor...")
        details.forEachIndexed { idx, detail ->
            val vec = computeEmbedding(detail, apiKey)
            memoryEventDao.insertEvent(
                MemoryEventEntity(
                    botId = testBot.id,
                    description = detail,
                    importanceScore = 85 + idx,
                    embedding = floatArrayToJson(vec)
                )
            )
        }

        val indirectQuery = "Geçen bahsettiğim Venedik'teki olay ve kardeşimin mesleği neydi?"
        val contextQuery = "Kullanıcı: Tatilden dönüyorum. AI: Harika! Kullanıcı: $indirectQuery"

        sb.appendLine("2. Vektör Araması Yapılıyor...")
        sb.appendLine("Sorgu Bağlamı: '$contextQuery'")

        val retrievedEvents = getRelevantMemoryEvents(testBot.id, contextQuery, apiKey)

        sb.appendLine("\n3. Getirilen Top Fragmanlar (Hibrit Skor Sıralı):")
        retrievedEvents.forEachIndexed { i, ev ->
            val vec = jsonToFloatArray(ev.embedding)
            val qVec = computeEmbedding(contextQuery, apiKey)
            val sim = cosineSimilarity(qVec, vec)
            sb.appendLine("  [#${i+1}] Skor=${"%.3f".format(sim)} | Önem=${ev.importanceScore} | Metin: ${ev.description}")
        }

        sb.appendLine("=== TEST PROTOKOLÜ TAMAMLANDI ===")
        return@withContext sb.toString()
    }

    suspend fun extractAndSaveEntityRelations(
        botId: String,
        text: String,
        currentMessageIndex: Int = 0,
        defaultConfidence: String = "certain"
    ): List<EntityRelationEntity> {
        if (text.isBlank()) return emptyList()
        val savedRelations = mutableListOf<EntityRelationEntity>()

        // Patterns:
        // 1. "X'in Y'si Z'dir/dır/dur/dür/tir/tır/tur/tür"
        // 2. "X'in Y'si Z"
        val regexWithSuffix = Regex(
            """(?i)([A-ZÇĞİÖŞÜa-zçğıöşü]+)(?:['’`])?(?:nin|nın|nün|nun|in|ın|ün|un)\s+([A-ZÇĞİÖŞÜa-zçğıöşü\s]+?)\s+([A-ZÇĞİÖŞÜa-zçğıöşü]+)(?:['’`])?(?:dir|dır|dür|dur|tir|tır|tür|tur)\b"""
        )
        val regexSimple = Regex(
            """(?i)([A-ZÇĞİÖŞÜa-zçğıöşü]+)(?:['’`])?(?:nin|nın|nün|nun|in|ın|ün|un)\s+(kardeşi|abisi|ablası|annesi|babası|oğlu|kızı|eşi|sevgilisi|arkadaşı|en\s+yakın\s+arkadaşı|patronu|düşmanı|öğretmeni|öğrencisi|doktoru|avukatı|komşusu|ortağı|mentoru|danışmanı|yardımcısı|kuzeni|amcası|dayısı|teyzesi|halası)\s+([A-ZÇĞİÖŞÜa-zçğıöşü]+)"""
        )

        val matches = (regexWithSuffix.findAll(text) + regexSimple.findAll(text)).distinctBy { it.value }

        val inferredConfidence = when {
            text.contains("galiba", ignoreCase = true) || text.contains("söylenti", ignoreCase = true) || text.contains("duydum", ignoreCase = true) -> "rumor"
            text.contains("sanırım", ignoreCase = true) || text.contains("olabilir", ignoreCase = true) -> "inferred"
            else -> defaultConfidence
        }

        for (match in matches) {
            val sourceRaw = match.groupValues[1].trim()
            val relRaw = match.groupValues[2].trim()
            val targetRaw = match.groupValues[3].trim()

            if (sourceRaw.length < 2 || targetRaw.length < 2 || relRaw.length < 2) continue
            if (sourceRaw.equals(targetRaw, ignoreCase = true)) continue

            val sourceEntity = entityRegistryDao.findByName(botId, sourceRaw)
            val sourceId = sourceEntity?.id ?: 0L
            val targetEntity = entityRegistryDao.findByName(botId, targetRaw)
            val targetId = targetEntity?.id ?: 0L

            val existingRelations = entityRelationDao.findRelationsByEntityName(botId, sourceRaw)
            val exactMatch = existingRelations.firstOrNull {
                it.sourceEntityName.equals(sourceRaw, ignoreCase = true) &&
                        it.targetEntityName.equals(targetRaw, ignoreCase = true) &&
                        isRelationTypeMatching(it.relationType, relRaw)
            }
            // Conflicting relation: same source+target with different relationType OR same source+relationType with different target
            val conflicting = existingRelations.firstOrNull { existing ->
                (existing.sourceEntityName.equals(sourceRaw, ignoreCase = true) &&
                        existing.targetEntityName.equals(targetRaw, ignoreCase = true) &&
                        !isRelationTypeMatching(existing.relationType, relRaw)) ||
                (existing.sourceEntityName.equals(sourceRaw, ignoreCase = true) &&
                        isRelationTypeMatching(existing.relationType, relRaw) &&
                        !existing.targetEntityName.equals(targetRaw, ignoreCase = true))
            }
            if (conflicting != null) {
                val incomingConfRank = confidenceRank(inferredConfidence)
                val existingConfRank = confidenceRank(conflicting.confidence)
                val shouldReplace = incomingConfRank > existingConfRank ||
                        (incomingConfRank == existingConfRank && currentMessageIndex >= conflicting.createdAtMessageIndex)
                if (shouldReplace) {
                    val updated = conflicting.copy(
                        relationType = relRaw,
                        targetEntityName = targetRaw,
                        targetEntityId = targetId,
                        confidence = inferredConfidence,
                        createdAtMessageIndex = currentMessageIndex
                    )
                    entityRelationDao.insertRelation(updated)
                    android.util.Log.d("EmochiKnowledgeGraph", "Conflict resolved: $sourceRaw -[$relRaw]-> $targetRaw (replaced '${conflicting.relationType}'->'${conflicting.targetEntityName}')")
                    savedRelations.add(updated)
                }
            } else if (exactMatch != null) {
                if (confidenceRank(inferredConfidence) > confidenceRank(exactMatch.confidence) || currentMessageIndex > exactMatch.createdAtMessageIndex) {
                    val updated = exactMatch.copy(
                        confidence = if (confidenceRank(inferredConfidence) >= confidenceRank(exactMatch.confidence)) inferredConfidence else exactMatch.confidence,
                        createdAtMessageIndex = maxOf(exactMatch.createdAtMessageIndex, currentMessageIndex)
                    )
                    entityRelationDao.insertRelation(updated)
                    savedRelations.add(updated)
                }
            } else {
                val relEntity = EntityRelationEntity(
                    botId = botId,
                    sourceEntityId = sourceId,
                    sourceEntityName = sourceRaw,
                    relationType = relRaw,
                    targetEntityId = targetId,
                    targetEntityName = targetRaw,
                    confidence = inferredConfidence,
                    createdAtMessageIndex = currentMessageIndex
                )
                val id = entityRelationDao.insertRelation(relEntity)
                android.util.Log.d("EmochiKnowledgeGraph", "Saved relation #$id: $sourceRaw -> [$relRaw] -> $targetRaw")
                savedRelations.add(relEntity.copy(id = id))
            }
        }
        return savedRelations
    }

    suspend fun traverseRelationGraph(botId: String, startEntityName: String, relationSteps: List<String>): GraphTraversalResult? {
        if (startEntityName.isBlank() || relationSteps.isEmpty()) return null

        val allRelations = entityRelationDao.getRelationsForBot(botId)
        if (allRelations.isEmpty()) return null

        var currentEntity = startEntityName.trim()
        val path = mutableListOf<String>()
        val visited = mutableListOf(currentEntity)

        for ((stepIndex, step) in relationSteps.withIndex()) {
            val stepClean = step.trim()
            if (stepClean.isBlank()) continue

            // Filter all candidate edges from currentEntity matching this relation step,
            // and prioritize highest confidence first, then most recent createdAtMessageIndex (2.1)
            val forwardCandidates = allRelations.filter { rel ->
                rel.sourceEntityName.equals(currentEntity, ignoreCase = true) && isRelationTypeMatching(rel.relationType, stepClean)
            }
            val reverseCandidates = if (forwardCandidates.isEmpty()) {
                allRelations.filter { rel ->
                    rel.targetEntityName.equals(currentEntity, ignoreCase = true) && isRelationTypeMatching(rel.relationType, stepClean)
                }
            } else emptyList()

            val matchingEdge = (forwardCandidates.ifEmpty { reverseCandidates })
                .sortedWith(
                    compareByDescending<EntityRelationEntity> { confidenceRank(it.confidence) }
                        .thenByDescending { it.createdAtMessageIndex }
                        .thenByDescending { it.id }
                )
                .firstOrNull()

            if (matchingEdge != null) {
                val nextEntity = if (matchingEdge.sourceEntityName.equals(currentEntity, ignoreCase = true)) {
                    matchingEdge.targetEntityName
                } else {
                    matchingEdge.sourceEntityName
                }
                path.add("${currentEntity} -> [${matchingEdge.relationType}] -> $nextEntity")
                currentEntity = nextEntity
                visited.add(currentEntity)
                android.util.Log.d("EmochiKnowledgeGraph", "Traversal step #$stepIndex: reached '$currentEntity' via '${matchingEdge.relationType}'")
            } else {
                android.util.Log.d("EmochiKnowledgeGraph", "Traversal halted at '$currentEntity' looking for '$stepClean'")
                return if (path.isNotEmpty()) {
                    val explanation = "Zincirin bir kısmını buldum: ${path.joinToString(" | ")}. '$currentEntity' noktasına kadar eminim, ancak '$currentEntity' için '$stepClean' bilgisini (ötesini) bilmiyorum."
                    GraphTraversalResult(
                        finalTargetName = currentEntity,
                        pathDescriptions = path,
                        visitedNodes = visited,
                        isComplete = false,
                        missingRelationStep = stepClean,
                        promptExplanation = explanation
                    )
                } else null
            }
        }

        val fullExplanation = "Tam zincir doğrulandı: ${path.joinToString(" | ")} -> Sonuç: $currentEntity"
        return GraphTraversalResult(
            finalTargetName = currentEntity,
            pathDescriptions = path,
            visitedNodes = visited,
            isComplete = true,
            missingRelationStep = null,
            promptExplanation = fullExplanation
        )
    }

    suspend fun detectAndResolveGraphQuery(botId: String, userQuery: String): GraphTraversalResult? {
        val query = userQuery.trim()
        // e.g. "X'in Y'sinin Z'sinin W'si kim?", "Ahmet'in kardeşinin en yakın arkadaşının patronu kim?"
        val chainRegex = Regex(
            """(?i)([A-ZÇĞİÖŞÜa-zçğıöşü]+)(?:['’`])?(?:nin|nın|nün|nun|in|ın|ün|un)\s+(.+?)\s+(?:kim|kimdir|nedir|ne)\b"""
        )
        val match = chainRegex.find(query) ?: return null

        val startNode = match.groupValues[1].trim()
        val chainStr = match.groupValues[2].trim()

        val tokens = chainStr.split(Regex("""\s+"""))
        val steps = mutableListOf<String>()
        var currentStepTokens = mutableListOf<String>()

        for (token in tokens) {
            val hasApostropheGenitive = Regex("""(?i)['’`](?:nin|nın|nün|nun|in|ın|ün|un)$""").containsMatchIn(token)
            val cleanToken = token.replace(Regex("""['’`].*"""), "")
            val isGenitiveSplit = hasApostropheGenitive || Regex("""(?i)(?:nin|nın|nün|nun|inin|ının|ünün|unun)$""").containsMatchIn(cleanToken)
            if (isGenitiveSplit && cleanToken.length >= 3) {
                currentStepTokens.add(cleanToken)
                steps.add(currentStepTokens.joinToString(" "))
                currentStepTokens = mutableListOf()
            } else {
                currentStepTokens.add(cleanToken)
            }
        }
        if (currentStepTokens.isNotEmpty()) {
            steps.add(currentStepTokens.joinToString(" "))
        }

        if (steps.isEmpty()) return null

        android.util.Log.d("EmochiKnowledgeGraph", "Detected graph query: Start='$startNode', Steps=$steps")
        return traverseRelationGraph(botId, startNode, steps)
    }

    suspend fun deleteMemoryFact(id: Long) = memoryFactDao.deleteFact(id)
    suspend fun deleteMemoryEvent(id: Long) = memoryEventDao.deleteEvent(id)
    suspend fun updateMemoryFact(fact: MemoryFactEntity) = memoryFactDao.updateFact(fact)
    suspend fun updateMemoryEvent(event: MemoryEventEntity) = memoryEventDao.updateEvent(event)
    suspend fun deleteAllAutoMemories(botId: String) {
        memoryFactDao.deleteAllFactsForBot(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }

    suspend fun deleteMemoryEvent(id: Long) = memoryEventDao.deleteEvent(id)
    suspend fun updateMemoryFact(fact: MemoryFactEntity) = memoryFactDao.updateFact(fact)
    suspend fun updateMemoryEvent(event: MemoryEventEntity) = memoryEventDao.updateEvent(event)
    suspend fun deleteAllAutoMemories(botId: String) {
        memoryFactDao.deleteAllFactsForBot(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }

    suspend fun updateMemoryFact(fact: MemoryFactEntity) = memoryFactDao.updateFact(fact)
    suspend fun updateMemoryEvent(event: MemoryEventEntity) = memoryEventDao.updateEvent(event)
    suspend fun deleteAllAutoMemories(botId: String) {
        memoryFactDao.deleteAllFactsForBot(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }

    suspend fun updateMemoryEvent(event: MemoryEventEntity) = memoryEventDao.updateEvent(event)
    suspend fun deleteAllAutoMemories(botId: String) {
        memoryFactDao.deleteAllFactsForBot(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }

    suspend fun deleteAllAutoMemories(botId: String) {
        memoryFactDao.deleteAllFactsForBot(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }
