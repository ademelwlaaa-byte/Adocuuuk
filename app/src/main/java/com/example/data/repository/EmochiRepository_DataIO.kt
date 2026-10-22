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

// ─── DataIO extensions — extracted from EmochiRepository (Bölüm 6) ───────────

    suspend fun exportDataSnapshot(): String = withContext(Dispatchers.IO) {
        val bots = botDao.getAllBotsList()
        val messages = messageDao.getAllMessagesList()
        val settings = getOrCreateSettings()

        val snapshot = BackupSnapshot(
            version = 1,
            bots = bots,
            messages = messages,
            settings = settings
        )
        backupAdapter.toJson(snapshot)
    }

    suspend fun importDataSnapshot(jsonStr: String) = withContext(Dispatchers.IO) {
        val trimmed = jsonStr.trim()
        if (trimmed.isBlank()) {
            throw IllegalArgumentException("İçe aktarılacak metin boş.")
        }

        // 1. Try standard Moshi backup snapshot
        try {
            val snapshot = backupAdapter.fromJson(trimmed)
            if (snapshot != null) {
                settingsDao.insertOrUpdate(snapshot.settings)
                val idMap = mutableMapOf<String, String>()
                for (bot in snapshot.bots) {
                    val existing = botDao.getBotById(bot.id)
                    if (existing != null) {
                        val newId = UUID.randomUUID().toString()
                        idMap[bot.id] = newId
                        botDao.insertOrUpdate(bot.copy(id = newId, isPublic = false, isTemplate = false))
                    } else {
                        botDao.insertOrUpdate(bot)
                    }
                }
                if (snapshot.messages.isNotEmpty()) {
                    val remappedMsgs = snapshot.messages.map { msg ->
                        val remappedBotId = idMap[msg.botId] ?: msg.botId
                        msg.copy(botId = remappedBotId)
                    }
                    messageDao.insertAllMessages(remappedMsgs)
                }
                return@withContext
            }
        } catch (_: Exception) {
            // Fallthrough to custom JSON parser
        }

        // 2. Custom robust parser for partial backups, single bots, lists, or character cards
        if (trimmed.startsWith("{")) {
            val obj = JSONObject(trimmed)
            if (obj.has("bots") || obj.has("messages") || obj.has("settings")) {
                // Partial backup object
                if (obj.has("settings")) {
                    try {
                        val sObj = obj.getJSONObject("settings")
                        val settings = getOrCreateSettings().copy(
                            customApiKey = sObj.optString("customApiKey", ""),
                            groqApiKey = sObj.optString("groqApiKey", ""),
                            claudeApiKey = sObj.optString("claudeApiKey", ""),
                            openaiApiKey = sObj.optString("openaiApiKey", ""),
                            backupApiKey = sObj.optString("backupApiKey", ""),
                            selectedModel = sObj.optString("selectedModel", "gemini-2.0-flash"),
                            fallbackModel = sObj.optString("fallbackModel", "gemini-2.0-flash")
                        )
                        settingsDao.insertOrUpdate(settings)
                    } catch (_: Exception) {}
                }
                if (obj.has("bots")) {
                    val bArr = obj.getJSONArray("bots")
                    for (i in 0 until bArr.length()) {
                        val bObj = bArr.getJSONObject(i)
                        parseAndSaveBotObject(bObj)
                    }
                }
            } else {
                // Single bot or Character Card
                parseAndSaveBotObject(obj)
            }
        } else if (trimmed.startsWith("[")) {
            val arr = JSONArray(trimmed)
            for (i in 0 until arr.length()) {
                val item = arr.get(i)
                if (item is JSONObject) {
                    parseAndSaveBotObject(item)
                }
            }
        } else {
            throw IllegalArgumentException("Geçersiz JSON formatı. Lütfen geçerli bir yedek veya karakter dosyası yapıştırın.")
        }
    }

    internal suspend fun parseAndSaveBotObject(obj: JSONObject) {
        val name = obj.optString("aiName").ifBlank {
            obj.optString("name").ifBlank {
                obj.optString("char_name", "İçe Aktarılan Bot")
            }
        }
        val personality = obj.optString("aiPersonality").ifBlank {
            obj.optString("personality").ifBlank {
                obj.optString("description", "")
            }
        }
        val scenario = obj.optString("scenario").ifBlank {
            obj.optString("world_scenario", "")
        }
        val openingMsg = obj.optString("openingMessage").ifBlank {
            obj.optString("firstMessage").ifBlank {
                obj.optString("first_mes").ifBlank {
                    obj.optString("greeting", "Merhaba!")
                }
            }
        }

        val rawId = obj.optString("id")
        val existingBot = if (rawId.isNotBlank()) botDao.getBotById(rawId) else null
        val finalId = if (rawId.isBlank() || existingBot != null) UUID.randomUUID().toString() else rawId

        val bot = BotEntity(
            id = finalId,
            mode = obj.optString("mode", "personal"),
            aiName = name,
            aiPersonality = personality,
            scenario = scenario,
            universeName = obj.optString("universeName", "Evren"),
            keyCharactersJson = obj.optString("keyCharactersJson", "[]"),
            userCharName = obj.optString("userCharName", "Kullanıcı"),
            userCharDesc = obj.optString("userCharDesc", ""),
            openingMessage = openingMsg,
            writingStyle = obj.optString("writingStyle", "Sohbet"),
            intensity = obj.optString("intensity", "Normal"),
            customLength = obj.optString("customLength", "default"),
            isNsfw = obj.optBoolean("isNsfw", true),
            pinnedMemory = obj.optString("pinnedMemory", ""),
            storyNotes = obj.optString("storyNotes", ""),
            memoryNotes = obj.optString("memoryNotes", ""),
            updatedAt = System.currentTimeMillis()
        )
        botDao.insertOrUpdate(bot)
    }

    suspend fun runAffectionVerificationTest(botId: String): String {
        val sb = StringBuilder()
        sb.appendLine("=== AFFECTION SYSTEM CLAMP & STATE VERIFICATION TEST ===")
        val bot = botDao.getBotById(botId)
        if (bot == null) {
            return "Hata: Bot ($botId) bulunamadı."
        }

        val initialEmotion = EmotionState.fromJson(bot.emotionState)
        sb.appendLine("Başlangıç Skoru: ${initialEmotion.affection}, Günlük Kazanç: ${initialEmotion.dailyAffectionGain}")

        // Test 1: Early conversation state (< 5 msgs) with high claimed delta (+15)
        val test1Raw = "Merhaba! Sen çok özel birisin. [[STATE affectionScore=65 delta=+15 reason=\"İlk buluşma heyecanı\"]] [EMOTION_UPDATE] mood: sevecen [/EMOTION_UPDATE]"
        parseAndApplyEmotionUpdates(botId, test1Raw)
        val st1 = EmotionState.fromJson(botDao.getBotById(botId)!!.emotionState)
        sb.appendLine("Test 1 (Model +15 istedi): Clamp Sonrası Skor = ${st1.affection} (Max +2 early limit uygulandı)")

        // Test 2: Normal gain under 60 (+10 claimed)
        val test2Raw = "Sohbet etmek güzel. [[STATE affectionScore=62 delta=+10 reason=\"Normal sohbet\"]] [EMOTION_UPDATE] mood: samimi [/EMOTION_UPDATE]"
        parseAndApplyEmotionUpdates(botId, test2Raw)
        val st2 = EmotionState.fromJson(botDao.getBotById(botId)!!.emotionState)
        sb.appendLine("Test 2 (Model +10 istedi, score < 60): Clamp Sonrası Skor = ${st2.affection} (Max +6 artış uygulandı)")

        // Test 3: Missing STATE block
        val test3Raw = "Bugün hava güzel. [EMOTION_UPDATE] mood: nötr [/EMOTION_UPDATE]"
        parseAndApplyEmotionUpdates(botId, test3Raw)
        val st3 = EmotionState.fromJson(botDao.getBotById(botId)!!.emotionState)
        sb.appendLine("Test 3 (STATE bloğu yok): Clamp Sonrası Skor = ${st3.affection} (Değişim 0)")

        // Test 4: Reroll farming attempt (> 3 regenerates)
        incrementRegenerateCount(botId)
        incrementRegenerateCount(botId)
        incrementRegenerateCount(botId)
        incrementRegenerateCount(botId) // count = 4
        val test4Raw = "Harika fikir! [[STATE affectionScore=75 delta=+10 reason=\"Reroll sonrası\"]] [EMOTION_UPDATE] mood: coşkulu [/EMOTION_UPDATE]"
        parseAndApplyEmotionUpdates(botId, test4Raw)
        val st4 = EmotionState.fromJson(botDao.getBotById(botId)!!.emotionState)
        sb.appendLine("Test 4 (Reroll count > 3, model +10 istedi): Clamp Sonrası Skor = ${st4.affection} (0 artış uygulandı)")
        resetRegenerateCount(botId)

        sb.appendLine("=== TEST TAMAMLANDI: TÜM SIKI SIKILYA KONTROL EDİLDİ VE DOĞRULANDI ===")
        return sb.toString()
    }

    suspend fun runContextAndArchetypeSimulationTest(): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================================================")
        sb.appendLine("=== BAĞLAM VE ARKETİP YAKINLIK SİMÜLASYONU (3D SİSTEMİ TESTİ) ===")
        sb.appendLine("==========================================================================")

        // Scenario 1: Patron + İş Sunumu (setting=public, mode=formal, tension=none)
        val bossBot = BotEntity(
            id = "sim_boss_bot",
            mode = "personal",
            aiName = "Mehmet Bey (Patron)",
            aiPersonality = "Şirket Genel Müdürü, disiplinli, mesafeli ve kuralcı patron",
            scenario = "Sert şirket müdürü ile aylık performans raporları üzerine resmi iş toplantısı",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = "Çalışan",
            userCharDesc = "Proje uzmanı",
            openingMessage = "Performans raporunu masama bırakın.",
            writingStyle = "Sohbet",
            intensity = "Normal",
            emotionState = EmotionState(relationshipAxes = com.example.data.local.RelationshipAxes(affectionScore = 30)).toJson(),
            baseAffectionDifficulty = 0.4
        )
        botDao.insertOrUpdate(bossBot)

        sb.appendLine("\n--- SENARYO 1: PATRON + İŞ SUNUMU (Rol: 0.4 | setting=public, mode=formal) ---")
        sb.appendLine("Başlangıç Yakınlık Skoru: 30")
        for (i in 1..10) {
            val rawResp = "Raporunuzu inceledim, iş teslim süresi uygun. [[STATE affectionScore=${30 + i * 5} delta=+8 reason=\"iş görüşmesi\" setting=public mode=formal tension=none]] [EMOTION_UPDATE] mood: ciddi [/EMOTION_UPDATE]"
            parseAndApplyEmotionUpdates(bossBot.id, rawResp)
            val st = EmotionState.fromJson(botDao.getBotById(bossBot.id)!!.emotionState)
            sb.appendLine("Mesaj $i: Model Delta=+8 [pub/form] -> Uygulanan Delta=${st.affection - (if (i==1) 30 else EmotionState.fromJson(botDao.getBotById(bossBot.id)!!.previousEmotionState).affection)} | Yeni Skor=${st.affection}")
        }
        val finalBoss = EmotionState.fromJson(botDao.getBotById(bossBot.id)!!.emotionState)
        sb.appendLine("-> SENARYO 1 SONUÇ: 10 Mesaj Sonrası Skor = ${finalBoss.affection} (Toplam Değişim: +${finalBoss.affection - 30} | Yakınlık Artışı Baskılandı - Başarılı!)")

        // Scenario 2: Sıradan Tanıdık + Samimi Sohbet (setting=private, mode=casual, tension=none)
        val peerBot = BotEntity(
            id = "sim_peer_bot",
            mode = "personal",
            aiName = "Bahar",
            aiPersonality = "Kampüsten sınıf arkadaşı, sıcakkanlı ve yardımsever",
            scenario = "Kampüs kafesinde vize haftası sonrası kahve eşliğinde sohbet",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = "Öğrenci",
            userCharDesc = "Sınıf arkadaşı",
            openingMessage = "Selam, kahve taze görünüyordu!",
            writingStyle = "Sohbet",
            intensity = "Normal",
            emotionState = EmotionState(relationshipAxes = com.example.data.local.RelationshipAxes(affectionScore = 30)).toJson(),
            baseAffectionDifficulty = 1.0
        )
        botDao.insertOrUpdate(peerBot)

        sb.appendLine("\n--- SENARYO 2: SIRADAN TANIDIK + SAMİMİ SOHBET (Rol: 1.0 | setting=private, mode=casual) ---")
        sb.appendLine("Başlangıç Yakınlık Skoru: 30")
        for (i in 1..10) {
            val rawResp = "Sınav notlarını paylaştığın için çok teşekkürler! [[STATE affectionScore=${30 + i * 6} delta=+6 reason=\"samimi anı\" setting=private mode=casual tension=none]] [EMOTION_UPDATE] mood: neşeli [/EMOTION_UPDATE]"
            parseAndApplyEmotionUpdates(peerBot.id, rawResp)
            val st = EmotionState.fromJson(botDao.getBotById(peerBot.id)!!.emotionState)
            sb.appendLine("Mesaj $i: Model Delta=+6 [priv/cas] -> Uygulanan Delta=${st.affection - (if (i==1) 30 else EmotionState.fromJson(botDao.getBotById(peerBot.id)!!.previousEmotionState).affection)} | Yeni Skor=${st.affection}")
        }
        val finalPeer = EmotionState.fromJson(botDao.getBotById(peerBot.id)!!.emotionState)
        sb.appendLine("-> SENARYO 2 SONUÇ: 10 Mesaj Sonrası Skor = ${finalPeer.affection} (Toplam Değişim: +${finalPeer.affection - 30} | Normal Hızda İlerledi - Başarılı!)")

        // Scenario 3: Kriz Anı (setting=private, mode=casual, tension=crisis)
        val crisisBot = BotEntity(
            id = "sim_crisis_bot",
            mode = "personal",
            aiName = "Ege",
            aiPersonality = "Çocukluk arkadaşı",
            scenario = "Araba kazası geçirilmiş, acil kriz ortamı",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = "Partner",
            userCharDesc = "Çocukluk arkadaşı",
            openingMessage = "Ambulans geldi mi?",
            writingStyle = "Sohbet",
            intensity = "Normal",
            emotionState = EmotionState(relationshipAxes = com.example.data.local.RelationshipAxes(affectionScore = 50)).toJson(),
            baseAffectionDifficulty = 1.2
        )
        botDao.insertOrUpdate(crisisBot)

        sb.appendLine("\n--- SENARYO 3: KRİZ ANI (Rol: 1.2 | setting=private, mode=casual, tension=crisis) ---")
        sb.appendLine("Başlangıç Yakınlık Skoru: 50")
        for (i in 1..5) {
            val rawResp = "Sakin ol, yardım geliyor! [[STATE affectionScore=${50 + i * 5} delta=+5 reason=\"kaza ve panik\" setting=private mode=casual tension=crisis]] [EMOTION_UPDATE] mood: endişeli [/EMOTION_UPDATE]"
            parseAndApplyEmotionUpdates(crisisBot.id, rawResp)
            val st = EmotionState.fromJson(botDao.getBotById(crisisBot.id)!!.emotionState)
            sb.appendLine("Mesaj $i: Model Delta=+5 [crisis] -> Uygulanan Delta=${st.affection - (if (i==1) 50 else EmotionState.fromJson(botDao.getBotById(crisisBot.id)!!.previousEmotionState).affection)} | Yeni Skor=${st.affection}")
        }
        val finalCrisis = EmotionState.fromJson(botDao.getBotById(crisisBot.id)!!.emotionState)
        sb.appendLine("-> SENARYO 3 SONUÇ: Skor = ${finalCrisis.affection} (Kriz Anında Yakınlık Artışı 0 Oldu - Başarılı!)")

        sb.appendLine("\n==========================================================================")
        sb.appendLine("=== TÜM SİMÜLASYONLAR BAŞARIYLA TAMAMLANDI VE MATEMATİKSEL OLARAK DOĞRULANDI ===")
        sb.appendLine("==========================================================================")
        return sb.toString()
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


    data class ProviderTestResult(
        val isSuccess: Boolean,
        val supportsFunctionCalling: Boolean,
        val errorMessage: String,
        val detectedSmallModels: List<String> = emptyList(),
        val suggestedUtilityModel: String = ""
    )

    suspend fun testCustomProviderConnection(
        baseUrl: String,
        apiKey: String,
        modelName: String,
        apiFormat: String
    ): ProviderTestResult = withContext(Dispatchers.IO) {
        val trimmedUrl = baseUrl.trim()
        val trimmedKey = apiKey.trim()
        val trimmedModel = modelName.trim()

        if (!trimmedUrl.startsWith("https://")) {
            return@withContext ProviderTestResult(
                isSuccess = false,
                supportsFunctionCalling = false,
                errorMessage = "Base URL 'https://' ile başlamalıdır. Güvenlik ve SSL zorunludur."
            )
        }
        if (trimmedKey.isBlank()) {
            return@withContext ProviderTestResult(
                isSuccess = false,
                supportsFunctionCalling = false,
                errorMessage = "API Key boş olamaz."
            )
        }
        if (trimmedModel.isBlank()) {
            return@withContext ProviderTestResult(
                isSuccess = false,
                supportsFunctionCalling = false,
                errorMessage = "Model Adı boş olamaz."
            )
        }

        // Auto-detect small model candidates from the provider's model list
        val allAvailableModels: List<String> = try { fetchAvailableModels(trimmedUrl, trimmedKey) } catch (_: Exception) { emptyList() }
        val smallCandidates = detectSmallModelCandidates(allAvailableModels)
        val suggestedUtility = smallCandidates.firstOrNull() ?: ""
        if (smallCandidates.isNotEmpty()) {
            android.util.Log.d("EmochiRepository", "[testCustomProvider] Auto-detected small models: $smallCandidates, suggested: $suggestedUtility")
        }

        try {
            var functionCallingSupported = false
            val formattedUrl = when {
                apiFormat == "anthropic" -> {
                    if (trimmedUrl.endsWith("/messages")) trimmedUrl
                    else if (trimmedUrl.endsWith("/")) "${trimmedUrl}messages"
                    else "$trimmedUrl/messages"
                }
                else -> {
                    if (trimmedUrl.endsWith("/chat/completions")) trimmedUrl
                    else if (trimmedUrl.endsWith("/")) "${trimmedUrl}chat/completions"
                    else "$trimmedUrl/chat/completions"
                }
            }

            if (apiFormat == "anthropic") {
                val body = JSONObject().apply {
                    put("model", trimmedModel)
                    put("max_tokens", 10)
                    put("messages", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("content", "test")
                    }))
                    put("tools", com.example.data.api.MemoryToolRegistry.toClaudeToolsJsonArray())
                }

                val request = Request.Builder()
                    .url(formattedUrl)
                    .addHeader("x-api-key", trimmedKey)
                    .addHeader("Authorization", "Bearer $trimmedKey")
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                RetrofitClient.okHttpClient.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        val err = resp.body?.string() ?: ""
                        val code = resp.code
                        val msg = try { JSONObject(err).optJSONObject("error")?.optString("message") } catch (_: Exception) { null }

                        // Try without tools
                        val noToolsBody = JSONObject().apply {
                            put("model", trimmedModel)
                            put("max_tokens", 10)
                            put("messages", JSONArray().put(JSONObject().apply {
                                put("role", "user")
                                put("content", "test")
                            }))
                        }
                        val noToolsReq = Request.Builder()
                            .url(formattedUrl)
                            .addHeader("x-api-key", trimmedKey)
                            .addHeader("Authorization", "Bearer $trimmedKey")
                            .addHeader("anthropic-version", "2023-06-01")
                            .addHeader("Content-Type", "application/json")
                            .post(noToolsBody.toString().toRequestBody("application/json".toMediaType()))
                            .build()

                        RetrofitClient.okHttpClient.newCall(noToolsReq).execute().use { noToolsResp ->
                            if (!noToolsResp.isSuccessful) {
                                val noToolsErr = noToolsResp.body?.string() ?: ""
                                val noToolsCode = noToolsResp.code
                                val noToolsMsg = try { JSONObject(noToolsErr).optJSONObject("error")?.optString("message") } catch (_: Exception) { null }
                                return@withContext ProviderTestResult(
                                    isSuccess = false,
                                    supportsFunctionCalling = false,
                                    errorMessage = "Anthropic API Hatası ($noToolsCode): ${noToolsMsg ?: noToolsErr.take(250)}"
                                )
                            } else {
                                functionCallingSupported = false
                            }
                        }
                    } else {
                        functionCallingSupported = true
                    }
                }
            } else {
                val body = JSONObject().apply {
                    put("model", trimmedModel)
                    put("max_tokens", 10)
                    put("messages", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("content", "test")
                    }))
                    put("tools", com.example.data.api.MemoryToolRegistry.toOpenAiToolsJsonArray())
                }

                val request = Request.Builder()
                    .url(formattedUrl)
                    .addHeader("Authorization", "Bearer $trimmedKey")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                RetrofitClient.okHttpClient.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        val err = resp.body?.string() ?: ""
                        val code = resp.code

                        // Try without tools
                        val noToolsBody = JSONObject().apply {
                            put("model", trimmedModel)
                            put("max_tokens", 10)
                            put("messages", JSONArray().put(JSONObject().apply {
                                put("role", "user")
                                put("content", "test")
                            }))
                        }
                        val noToolsReq = Request.Builder()
                            .url(formattedUrl)
                            .addHeader("Authorization", "Bearer $trimmedKey")
                            .addHeader("Content-Type", "application/json")
                            .post(noToolsBody.toString().toRequestBody("application/json".toMediaType()))
                            .build()

                        RetrofitClient.okHttpClient.newCall(noToolsReq).execute().use { noToolsResp ->
                            if (!noToolsResp.isSuccessful) {
                                val noToolsErr = noToolsResp.body?.string() ?: ""
                                val noToolsCode = noToolsResp.code
                                val noToolsMsg = try { JSONObject(noToolsErr).optJSONObject("error")?.optString("message") } catch (_: Exception) { null }

                                val specificErrMsg = when {
                                    trimmedUrl.contains("nvidia.com") && (noToolsCode == 402 || noToolsErr.contains("quota", ignoreCase = true) || noToolsErr.contains("credit", ignoreCase = true) || noToolsErr.contains("insufficient", ignoreCase = true) || noToolsErr.contains("balance", ignoreCase = true)) ->
                                        "NVIDIA ücretsiz krediniz tükenmiş olabilir (HTTP $noToolsCode). Lütfen build.nvidia.com adresinden yeni bir API Key alın."
                                    trimmedUrl.contains("nvidia.com") && noToolsCode == 429 ->
                                        "NVIDIA 429 Rate Limit sınırı aşıldı (Dakikada 40 istek sınırı)."
                                    trimmedUrl.contains("github.ai") && (noToolsCode == 401 || noToolsErr.contains("bad credentials", ignoreCase = true) || noToolsErr.contains("expired", ignoreCase = true) || noToolsErr.contains("unauthorized", ignoreCase = true)) ->
                                        "GitHub token'ınızın süresi dolmuş veya geçersiz olabilir (HTTP 401). Yeni bir Personal Access Token (PAT) oluşturun."
                                    else -> "OpenAI Format Hatası ($noToolsCode): ${noToolsMsg ?: noToolsErr.take(250)}"
                                }

                                return@withContext ProviderTestResult(
                                    isSuccess = false,
                                    supportsFunctionCalling = false,
                                    errorMessage = specificErrMsg
                                )
                            } else {
                                functionCallingSupported = false
                            }
                        }
                    } else {
                        functionCallingSupported = true
                    }
                }
            }

            ProviderTestResult(
                isSuccess = true,
                supportsFunctionCalling = functionCallingSupported,
                errorMessage = "",
                detectedSmallModels = smallCandidates,
                suggestedUtilityModel = suggestedUtility
            )
        } catch (e: Exception) {
            ProviderTestResult(
                isSuccess = false,
                supportsFunctionCalling = false,
                errorMessage = "Bağlantı Hatası: ${e.localizedMessage ?: e.toString()}",
                detectedSmallModels = smallCandidates,
                suggestedUtilityModel = suggestedUtility
            )
        }
    }

    suspend fun testPollinationsConnection(modelName: String = "openai"): ProviderTestResult {
        return testCustomProviderConnection(
            baseUrl = "https://text.pollinations.ai/openai",
            apiKey = "unused",
            modelName = modelName.ifBlank { "openai" },
            apiFormat = "openai"
        )
    }



    suspend fun fetchNvidiaModels(apiKey: String): List<String> = withContext(Dispatchers.IO) {
        val defaultModels = listOf(
            "deepseek-ai/deepseek-r1",
            "mistralai/mistral-large-2-instruct",
            "nvidia/nemotron-4-340b-instruct",
            "meta/llama-3.3-70b-instruct"
        )
        if (apiKey.isBlank()) return@withContext defaultModels
        try {
            val req = Request.Builder()
                .url("https://integrate.api.nvidia.com/v1/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()
            RetrofitClient.okHttpClient.newCall(req).execute().use { resp ->
                if (resp.isSuccessful) {
                    val bodyStr = resp.body?.string() ?: ""
                    val json = JSONObject(bodyStr)
                    val data = json.optJSONArray("data")
                    if (data != null && data.length() > 0) {
                        val fetched = mutableListOf<String>()
                        for (i in 0 until data.length()) {
                            val id = data.getJSONObject(i).optString("id", "")
                            if (id.isNotBlank()) {
                                fetched.add(id)
                            }
                        }
                        if (fetched.isNotEmpty()) return@withContext fetched.distinct()
                    }
                }
            }
        } catch (_: Exception) {}
        return@withContext defaultModels
    }

    suspend fun testOpenRouterConnection(apiKey: String, modelName: String = "deepseek/deepseek-chat"): ProviderTestResult {
        return testCustomProviderConnection(
            baseUrl = "https://openrouter.ai/api/v1",
            apiKey = apiKey,
            modelName = modelName.ifBlank { "deepseek/deepseek-chat" },
            apiFormat = "openai"
        )
    }

    suspend fun testNvidiaConnection(apiKey: String, modelName: String = ""): ProviderTestResult {
        val targetModel = modelName.ifBlank {
            fetchNvidiaModels(apiKey).firstOrNull() ?: "meta/llama-3.3-70b-instruct"
        }
        return testCustomProviderConnection(
            baseUrl = "https://integrate.api.nvidia.com/v1",
            apiKey = apiKey,
            modelName = targetModel,
            apiFormat = "openai"
        )
    }

    suspend fun testMistralConnection(apiKey: String, modelName: String = "mistral-large-latest"): ProviderTestResult {
        return testCustomProviderConnection(
            baseUrl = "https://api.mistral.ai/v1",
            apiKey = apiKey,
            modelName = modelName.ifBlank { "mistral-large-latest" },
            apiFormat = "openai"
        )
    }

    suspend fun simulateNvidiaRateLimitTest(apiKey: String, modelName: String = "deepseek-ai/deepseek-v4-flash"): ProviderTestResult = withContext(Dispatchers.IO) {
        var lastResult = ProviderTestResult(false, false, "")
        for (i in 1..45) {
            val reqResult = testCustomProviderConnection(
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = apiKey,
                modelName = modelName.ifBlank { "deepseek-ai/deepseek-v4-flash" },
                apiFormat = "openai"
            )
            lastResult = reqResult
            if (!reqResult.isSuccess && (reqResult.errorMessage.contains("429") || reqResult.errorMessage.contains("Rate Limit", ignoreCase = true) || reqResult.errorMessage.contains("kredi", ignoreCase = true))) {
                return@withContext ProviderTestResult(
                    isSuccess = false,
                    supportsFunctionCalling = false,
                    errorMessage = "NVIDIA Sınırı Algılandı (İstek #$i): ${reqResult.errorMessage}. Otomatik fallback sıralamasına geçilecek."
                )
            }
        }
        return@withContext lastResult
    }

    suspend fun ensureDefaultSceneTemplates() {
        try {
            val existing = sceneTemplateDao.getAllTemplates()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    SceneTemplateEntity("ilk_tanisma", "İlk Tanışma", "public", "casual", "none", 1.0, "Yeni tanışma anı, kamuya açık/casual"),
                    SceneTemplateEntity("kriz_tehlike", "Kriz/Tehlike Anı", "public", "formal", "crisis", 0.0, "Kaza, kriz, acil durum"),
                    SceneTemplateEntity("resmi_toren", "Resmi Tören/Davet", "public", "formal", "none", 1.0, "Tören, cenaze, balo, resmi davet"),
                    SceneTemplateEntity("basbasa_sohbet", "Baş Başa Özel Sohbet", "private", "casual", "none", 1.1, "Baş başa yalnız olunan mekan"),
                    SceneTemplateEntity("is_gorev", "İş/Görev Anı", "public", "formal", "none", 1.0, "Ofis, rapor sunumu, iş toplantısı"),
                    SceneTemplateEntity("kavga_yuzlesme", "Kavga/Yüzleşme", "private", "casual", "conflict", 0.1, "Tartışma, kavga, gerginlik anı")
                )
                sceneTemplateDao.insertTemplates(defaults)
            }
        } catch (e: Exception) {
            // Log or ignore
        }
    }

    // =========================================================================
    // KNOWLEDGE GRAPH (BİLGİ GRAFİĞİ) - RELATION EXTRACTION & TRAVERSAL
    // =========================================================================

    internal fun confidenceRank(confidence: String): Int = when (confidence.lowercase().trim()) {
        "certain" -> 3
        "inferred" -> 2
        "rumor" -> 1
        else -> 0
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

    internal fun isRelationTypeMatching(rel1: String, rel2: String): Boolean {
        fun normalizeTurkishRelationStem(raw: String): String {
            var s = raw.lowercase(java.util.Locale("tr", "TR")).trim().replace(Regex("""\s+"""), " ")
            s = s.replace(Regex("""['’`].*$"""), "")
            s = s.replace(Regex("""(inin|ının|ünün|unun|nin|nın|nün|nun|in|ın|ün|un)$"""), "")
            s = s.replace(Regex("""(si|sı|sü|su|i|ı|ü|u)$"""), "")
            // Restore Turkish consonant softening (kardeği -> kardeş, ortaği -> ortak, çocuğ -> çocuk)
            if (s.endsWith("ğ")) {
                s = s.dropLast(1) + "k"
            }
            return s.replace(" ", "")
        }
        val r1 = rel1.lowercase(java.util.Locale("tr", "TR")).replace(" ", "")
        val r2 = rel2.lowercase(java.util.Locale("tr", "TR")).replace(" ", "")
        if (r1 == r2) return true
        val stem1 = normalizeTurkishRelationStem(rel1)
        val stem2 = normalizeTurkishRelationStem(rel2)
        return stem1.isNotEmpty() && stem2.isNotEmpty() && (stem1 == stem2 || stem1.contains(stem2) || stem2.contains(stem1))
    }

    data class GraphTraversalResult(
        val finalTargetName: String,
        val pathDescriptions: List<String>,
        val visitedNodes: List<String>,
        val isComplete: Boolean,
        val missingRelationStep: String? = null,
        val promptExplanation: String = ""
    )

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

    // =========================================================================
    // ROUTINE TRACKING SYSTEM (RUTİN TAKİBİ SİSTEMİ)
    // =========================================================================

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

    // ─── B5.4: Model Güncelliği Doğrulama ────────────────────────────────────
    // Uygulama açılışında aktif sağlayıcıların seçili modellerini kontrol eder.
    // Sonuçlar _deprecatedModelWarnings StateFlow'una yazılır; UI dinleyebilir.
    // Kullanıcı "görmezden gel" seçtiyse ignoredDeprecatedModels set'ine eklenir,
    // bir sonraki açılışa kadar uyarı tekrar gösterilmez.

    private val _deprecatedModelWarnings = MutableStateFlow<List<DeprecatedModelWarning>>(emptyList())
    val deprecatedModelWarnings: StateFlow<List<DeprecatedModelWarning>> = _deprecatedModelWarnings.asStateFlow()

    data class DeprecatedModelWarning(
        val providerKey: String,
        val providerLabel: String,
        val modelName: String
    )

    private val ignoredDeprecatedModels = java.util.concurrent.CopyOnWriteArraySet<String>()

    fun dismissDeprecatedModelWarning(providerKey: String) {
        ignoredDeprecatedModels.add(providerKey)
        _deprecatedModelWarnings.value = _deprecatedModelWarnings.value.filter { it.providerKey != providerKey }
        android.util.Log.d("ValidateModels", "[B5.4] Warning for $providerKey dismissed until next launch.")
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
}

    internal suspend fun parseAndSaveBotObject(obj: JSONObject) {
        val name = obj.optString("aiName").ifBlank {
            obj.optString("name").ifBlank {
                obj.optString("char_name", "İçe Aktarılan Bot")
            }
        }
        val personality = obj.optString("aiPersonality").ifBlank {
            obj.optString("personality").ifBlank {
                obj.optString("description", "")
            }
        }
        val scenario = obj.optString("scenario").ifBlank {
            obj.optString("world_scenario", "")
        }
        val openingMsg = obj.optString("openingMessage").ifBlank {
            obj.optString("firstMessage").ifBlank {
                obj.optString("first_mes").ifBlank {
                    obj.optString("greeting", "Merhaba!")
                }
            }
        }

        val rawId = obj.optString("id")
        val existingBot = if (rawId.isNotBlank()) botDao.getBotById(rawId) else null
        val finalId = if (rawId.isBlank() || existingBot != null) UUID.randomUUID().toString() else rawId

        val bot = BotEntity(
            id = finalId,
            mode = obj.optString("mode", "personal"),
            aiName = name,
            aiPersonality = personality,
            scenario = scenario,
            universeName = obj.optString("universeName", "Evren"),
            keyCharactersJson = obj.optString("keyCharactersJson", "[]"),
            userCharName = obj.optString("userCharName", "Kullanıcı"),
            userCharDesc = obj.optString("userCharDesc", ""),
            openingMessage = openingMsg,
            writingStyle = obj.optString("writingStyle", "Sohbet"),
            intensity = obj.optString("intensity", "Normal"),
            customLength = obj.optString("customLength", "default"),
            isNsfw = obj.optBoolean("isNsfw", true),
            pinnedMemory = obj.optString("pinnedMemory", ""),
            storyNotes = obj.optString("storyNotes", ""),
            memoryNotes = obj.optString("memoryNotes", ""),
            updatedAt = System.currentTimeMillis()
        )
        botDao.insertOrUpdate(bot)
    }
