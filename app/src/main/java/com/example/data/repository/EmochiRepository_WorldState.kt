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

// ─── WorldState extensions — extracted from EmochiRepository (Bölüm 6) ───────────

    fun getSeasonFromDate(dateStr: String): String {
        if (dateStr.isBlank()) return ""
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateStr) ?: return ""
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { time = date }
            val month = cal.get(java.util.Calendar.MONTH) + 1 // 1-12
            when (month) {
                3, 4, 5 -> "ilkbahar"
                6, 7, 8 -> "yaz"
                9, 10, 11 -> "sonbahar"
                else -> "kış"
            }
        } catch (_: Exception) { "" }
    }

    suspend fun getMemoryEventsBySeason(botId: String, season: String): List<MemoryEventEntity> {
        if (season.isBlank()) return emptyList()
        val allEvents = memoryEventDao.getActiveEvents(botId)
        return allEvents.filter { event ->
            val eventSeason = getSeasonFromDate(event.storyCalendarDateStr)
            eventSeason.equals(season.trim().lowercase(), ignoreCase = true)
        }
    }

    internal suspend fun detectInterMessageTimeConflict(botId: String, newAiText: String, provider: String) {
        val recentMsgs = messageDao.getMessagesForBotList(botId)
            .filter { it.role == "assistant" }
            .takeLast(3)
            .map { it.text.lowercase(java.util.Locale("tr", "TR")) }

        if (recentMsgs.size < 2) return

        val newLower = newAiText.lowercase(java.util.Locale("tr", "TR"))
        val prevLower = recentMsgs.dropLast(1).lastOrNull() ?: return

        // Önceki mesaj akşam, yeni mesaj sabah — aralarında geçiş anlatılmadıysa çelişki
        val prevWasEvening = EVENING_INDICATORS.any { prevLower.contains(it) }
        val prevWasNight = NIGHT_INDICATORS.any { prevLower.contains(it) }
        val newIsMorning = DAY_INDICATORS.any { newLower.contains(it) }
        val newIsEvening = EVENING_INDICATORS.any { newLower.contains(it) }
        val hasTransition = TRANSITION_MARKERS.any { newLower.contains(it) }

        val conflictDetected = !hasTransition && (
            (prevWasEvening && newIsMorning) ||
            (prevWasNight && newIsEvening && !newLower.contains("hâlâ gece") && !newLower.contains("hala gece"))
        )

        if (conflictDetected) {
            android.util.Log.w("SceneTimeConflict", "[interMessageTimeConflict] prev=${prevLower.take(60)} → new=${newLower.take(60)} provider=$provider")
            try {
                db.timePerceptionMismatchLogDao().insertLog(
                    com.example.data.local.TimePerceptionMismatchLogEntity(
                        botId = botId,
                        userMessageText = "interMessageTimeConflict",
                        modelResponseText = newAiText.take(300),
                        detectedTimeExpression = if (prevWasEvening) "akşam→sabah" else "gece→akşam",
                        codeCalculatedDays = 0,
                        mismatchReason = "interMessageTimeConflict:noTransition provider=$provider",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {}
        }
    }

    suspend fun extractAndSaveRoutinesFromBotPersonality(botId: String, personality: String, scenario: String): List<RoutineEntity> {
        val combined = "$personality\n$scenario"
        if (combined.isBlank()) return emptyList()

        val existingRoutines = routineDao.getRoutinesForBot(botId)
        val existingDescriptions = existingRoutines.map { it.routineDescription.lowercase() }.toSet()

        val saved = mutableListOf<RoutineEntity>()
        val days = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar")

        for (day in days) {
            val dayRegex = Regex("""(?i)(?:her\s+)?$day(?:leri|ları)?\s+(.+?)(?:[.,;\n]|$)""")
            dayRegex.findAll(combined).forEach { match ->
                val action = match.groupValues[1].trim()
                if (action.length in 5..120 && !existingDescriptions.contains(action.lowercase())) {
                    val entity = RoutineEntity(
                        botId = botId,
                        routineDescription = "$day günleri $action",
                        frequency = "weekly",
                        dayOfWeek = day
                    )
                    routineDao.insertRoutine(entity)
                    saved.add(entity)
                    android.util.Log.d("EmochiRoutine", "Saved weekly routine for bot $botId: $day -> $action")
                }
            }
        }

        // Daily / generic frequency routines
        val dailyRegex = Regex("""(?i)(?:her\s+gün|her\s+sabah|her\s+akşam|günlük\s+olarak)\s+(.+?)(?:[.,;\n]|$)""")
        dailyRegex.findAll(combined).forEach { match ->
            val action = match.groupValues[1].trim()
            if (action.length in 5..120 && !existingDescriptions.contains(action.lowercase())) {
                val entity = RoutineEntity(
                    botId = botId,
                    routineDescription = action,
                    frequency = "daily",
                    dayOfWeek = null
                )
                routineDao.insertRoutine(entity)
                saved.add(entity)
                android.util.Log.d("EmochiRoutine", "Saved daily routine for bot $botId: $action")
            }
        }

        return saved
    }

    fun getDayOfWeekTurkish(storyCalendarDateStr: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val date = sdf.parse(storyCalendarDateStr) ?: return ""
            val cal = java.util.Calendar.getInstance()
            cal.time = date
            when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.MONDAY -> "Pazartesi"
                java.util.Calendar.TUESDAY -> "Salı"
                java.util.Calendar.WEDNESDAY -> "Çarşamba"
                java.util.Calendar.THURSDAY -> "Perşembe"
                java.util.Calendar.FRIDAY -> "Cuma"
                java.util.Calendar.SATURDAY -> "Cumartesi"
                java.util.Calendar.SUNDAY -> "Pazar"
                else -> ""
            }
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun getActiveRoutineReminder(botId: String, storyCalendarDateStr: String): RoutineEntity? {
        val currentDay = getDayOfWeekTurkish(storyCalendarDateStr) // "" if no date → weekly routines won't fire
        val routines = routineDao.getRoutinesForBot(botId)
        if (routines.isEmpty()) return null

        for (routine in routines) {
            val isEligible = when (routine.frequency.lowercase()) {
                "daily" -> routine.lastMentionedStoryDate != storyCalendarDateStr
                "weekly" -> {
                    // tarihsiz botlarda currentDay == "" → dayOfWeek eşleşmez → weekly çalışmaz (tasarım gereği)
                    currentDay.isNotBlank() &&
                            (routine.dayOfWeek != null && routine.dayOfWeek.equals(currentDay, ignoreCase = true)) &&
                            routine.lastMentionedStoryDate != storyCalendarDateStr
                }
                else -> routine.lastMentionedStoryDate != storyCalendarDateStr
            }

            if (isEligible) {
                return routine
            }
        }
        return null
    }

    suspend fun markRoutineMentioned(routineId: Long, storyCalendarDateStr: String, botId: String = "") {
        val candidateBots = if (botId.isNotBlank()) listOf(botId) else botDao.getAllBotsList().map { it.id }
        for (bId in candidateBots) {
            val target = routineDao.getRoutinesForBot(bId).firstOrNull { it.id == routineId }
            if (target != null) {
                routineDao.updateRoutine(target.copy(lastMentionedStoryDate = storyCalendarDateStr))
                return
            }
        }
    }

    suspend fun validateModelsOnStartup() = withContext(Dispatchers.IO) {
        val settings = try { getOrCreateSettings() } catch (_: Exception) { return@withContext }
        val warnings = mutableListOf<DeprecatedModelWarning>()

        data class ProviderCheck(val key: String, val label: String, val enabled: Boolean, val apiKey: String, val model: String, val modelsUrl: String)

        val checks = listOf(
            ProviderCheck("gemini",      "Gemini",      true,                               settings.customApiKey,     settings.geminiModel,      "https://generativelanguage.googleapis.com/v1beta/models?key=${settings.customApiKey}"),
            ProviderCheck("groq",        "Groq",        settings.groqApiKey.isNotBlank(),   settings.groqApiKey,       settings.groqModel,        "https://api.groq.com/openai/v1/models"),
            ProviderCheck("claude",      "Claude",      settings.claudeApiKey.isNotBlank(), settings.claudeApiKey,     settings.claudeModel,      "https://api.anthropic.com/v1/models"),
            ProviderCheck("openai",      "OpenAI",      settings.openaiApiKey.isNotBlank(), settings.openaiApiKey,     settings.openaiModel,      "https://api.openai.com/v1/models"),
            ProviderCheck("openrouter",  "OpenRouter",  settings.openRouterApiKey.isNotBlank(), settings.openRouterApiKey, settings.openRouterModel, "https://openrouter.ai/api/v1/models"),
            ProviderCheck("nvidia",      "NVIDIA NIM",  settings.nvidiaApiKey.isNotBlank(), settings.nvidiaApiKey,     settings.nvidiaModel,      "https://integrate.api.nvidia.com/v1/models"),
            ProviderCheck("mistral",     "Mistral",     settings.mistralApiKey.isNotBlank(), settings.mistralApiKey,   settings.mistralModel,     "https://api.mistral.ai/v1/models")
        )

        for (check in checks) {
            if (!check.enabled || check.apiKey.isBlank() || check.model.isBlank()) continue
            if (ignoredDeprecatedModels.contains(check.key)) continue

            try {
                val req = okhttp3.Request.Builder()
                    .url(check.modelsUrl)
                    .addHeader("Authorization", "Bearer ${check.apiKey}")
                    .addHeader("x-api-key", check.apiKey)
                    .get()
                    .build()
                RetrofitClient.okHttpClient.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string() ?: ""
                        val available = mutableListOf<String>()
                        try {
                            val json = JSONObject(body)
                            val data = json.optJSONArray("data") ?: json.optJSONArray("models")
                            if (data != null) {
                                for (i in 0 until data.length()) {
                                    val item = data.optJSONObject(i)
                                    val id = item?.optString("id") ?: item?.optString("name") ?: ""
                                    if (id.isNotBlank()) available.add(id)
                                }
                            }
                        } catch (_: Exception) {}
                        if (available.isNotEmpty() && !available.any { it.equals(check.model, ignoreCase = true) || it.contains(check.model, ignoreCase = true) }) {
                            android.util.Log.w("ValidateModels", "[B5.4] DEPRECATED: ${check.label} model '${check.model}' not in live list (${available.size} models found)")
                            warnings.add(DeprecatedModelWarning(check.key, check.label, check.model))
                        } else {
                            android.util.Log.d("ValidateModels", "[B5.4] OK: ${check.label} model '${check.model}' confirmed.")
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.d("ValidateModels", "[B5.4] Skipping ${check.label}: ${e.message}")
            }
        }

        if (warnings.isNotEmpty()) {
            _deprecatedModelWarnings.value = warnings
            android.util.Log.w("ValidateModels", "[B5.4] ${warnings.size} deprecated model warning(s) emitted.")
        }
    }

    fun dismissDeprecatedModelWarning(providerKey: String) {
        ignoredDeprecatedModels.add(providerKey)
        _deprecatedModelWarnings.value = _deprecatedModelWarnings.value.filter { it.providerKey != providerKey }
        android.util.Log.d("ValidateModels", "[B5.4] Warning for $providerKey dismissed until next launch.")
    }

    suspend fun updateCastMembersAgeForBot(botId: String, currentStoryCalendarDateStr: String) {
        if (currentStoryCalendarDateStr.isBlank()) return
        try {
            val members = castMemberDao.getCastMembersForBot(botId)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val currDate = sdf.parse(currentStoryCalendarDateStr) ?: return
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            cal.time = currDate

            for (cm in members) {
                if (cm.birthDate.isNotBlank()) {
                    val birthDate = try { sdf.parse(cm.birthDate) } catch (_: Exception) { null }
                    if (birthDate != null) {
                        val birthCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                        birthCal.time = birthDate
                        var age = cal.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR)
                        val newMM = cal.get(java.util.Calendar.MONTH) * 100 + cal.get(java.util.Calendar.DAY_OF_MONTH)
                        val birthMM = birthCal.get(java.util.Calendar.MONTH) * 100 + birthCal.get(java.util.Calendar.DAY_OF_MONTH)
                        if (newMM < birthMM) age--
                        if (age > 0 && age != cm.currentAge) {
                            castMemberDao.insertCastMember(cm.copy(currentAge = age))
                        }
                    }
                }
            }
        } catch (_: Exception) {}
    }
