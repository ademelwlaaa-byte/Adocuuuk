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

// ─── Cast extensions — extracted from EmochiRepository (Bölüm 6) ───────────

    fun cleanCharacterNameCandidate(raw: String): String {
        // Apostroplu formları ek soyarak temizle (Ayla’nın → Ayla); apostrof yoksa dokunma
        var clean = CastDetectorEngine.stemTurkishProperName(raw.trim())
            .replace(Regex("""[.,!?:;"’()\[\]{}]+"""), "")

        val titles = listOf(
            "bay", "bayan", "doktor", "prof", "profesör", "yüzbaşı", "kaptan", "amiral", "komutan",
            "lord", "prens", "prenses", "kral", "kraliçe", "usta", "hoca", "savaşçı", "büyücü",
            "şövalye", "mimar", "aziz", "üstat", "gözcü", "dedektif", "ajan", "müfettiş", "savcı",
            "hâkim", "teğmen", "binbaşı", "albay", "general", "sultan", "paşa", "dük", "kont",
            "baron", "barones", "rahip", "cadı", "gölge", "muhafız", "hizmetkar", "tüccar", "eczacı",
            "yargıç", "bakan", "başkan", "reis", "çavuş", "efe", "kethüda", "seyis", "bey", "hanım",
            "efendi", "ağa", "abi", "abla", "hazretleri", "sn", "sayın", "sn.", "dr.", "mr.", "mrs.", "ms.", "sir", "lady"
        )

        val words = clean.split(Regex("""\s+""")).toMutableList()
        while (words.isNotEmpty() && titles.contains(words.first().lowercase())) {
            words.removeAt(0)
        }
        while (words.isNotEmpty() && titles.contains(words.last().lowercase())) {
            words.removeAt(words.size - 1)
        }

        return words.joinToString(" ").trim()
    }

    fun isMainOrUserCharacter(candidateRaw: String, bot: BotEntity): Boolean {
        val cleanCandidate = cleanCharacterNameCandidate(candidateRaw)
        val candidateLower = cleanCandidate.lowercase()
        val rawLower = candidateRaw.lowercase().trim()

        if (candidateLower.isBlank() || candidateLower.length < 2 || rawLower.length < 2) return true

        val genericWords = setOf(
            "bay", "bayan", "kaptan", "doktor", "komutan", "yüzbaşı", "kral", "prenses", "prens",
            "adam", "kadın", "çocuk", "insan", "karakter", "kullanıcı", "sistem", "yazar", "oyuncu",
            "sohbet", "arkadaş", "dost", "düşman", "sen", "ben", "o", "biz", "siz", "onlar", "biri",
            "diğeri", "herkes", "kimse", "hiçbiri", "bugün", "yarın", "dün", "sabah", "akşam", "gece",
            "gündüz", "şimdi", "sonra", "önce", "burada", "orada", "şurada", "çünkü", "sadece", "oysa",
            "fakat", "ayrıca", "ancak", "belki", "böyle", "şöyle", "öyle", "peki", "tamam", "pekala",
            "lütfen", "efendim", "istanbul", "ankara", "izmir", "türkiye", "dünya", "güneş", "ay",
            "sokak", "cadde", "oda", "masa", "kapı", "pencere", "araba", "telefon", "kitap", "kalem",
            "kılıç", "ateş", "su", "toprak", "hava", "ruh", "işte", "biri", "diğer", "evet", "hayır"
        )

        val aiNameClean = cleanCharacterNameCandidate(bot.aiName).lowercase()
        val aiNameRaw = bot.aiName.lowercase().trim()
        val aiTokens = (aiNameClean.split(Regex("""\s+""")) + aiNameRaw.split(Regex("""\s+""")))
            .filter { it.length >= 2 }.toSet()

        val userClean = cleanCharacterNameCandidate(bot.userCharName).lowercase()
        val userRaw = bot.userCharName.lowercase().trim()
        val userTokens = (userClean.split(Regex("""\s+""")) + userRaw.split(Regex("""\s+""")))
            .filter { it.length >= 2 }.toSet()

        if (aiNameRaw.isNotBlank() && (rawLower == aiNameRaw || candidateLower == aiNameRaw)) return true
        if (aiNameClean.isNotBlank() && (candidateLower == aiNameClean || rawLower == aiNameClean)) return true
        if (aiTokens.contains(candidateLower) || aiTokens.contains(rawLower)) return true

        if (userRaw.isNotBlank() && (rawLower == userRaw || candidateLower == userRaw)) return true
        if (userClean.isNotBlank() && (candidateLower == userClean || rawLower == userClean)) return true
        if (userTokens.contains(candidateLower) || userTokens.contains(rawLower)) return true

        for (token in aiTokens) {
            if (token.length >= 3 && (candidateLower == token || rawLower == token)) return true
        }
        for (token in userTokens) {
            if (token.length >= 3 && (candidateLower == token || rawLower == token)) return true
        }

        if (genericWords.contains(candidateLower) || genericWords.contains(rawLower)) return true

        return false
    }

    fun extractCharacterDescriptionFromContext(charName: String, bot: BotEntity, fallbackSentence: String = ""): String {
        val cleanName = cleanCharacterNameCandidate(charName)
        if (cleanName.isBlank()) return "Sahnede beliren yan karakter."

        val sources = listOf(
            bot.scenario,
            bot.keyCharactersJson,
            bot.storyNotes,
            bot.aiPersonality,
            bot.universeName,
            bot.openingMessage
        )

        for (source in sources) {
            if (source.isBlank()) continue
            val lines = source.lines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isBlank()) continue
                if (trimmed.contains(cleanName, ignoreCase = true) || (charName.length >= 3 && trimmed.contains(charName, ignoreCase = true))) {
                    val cleanLine = trimmed
                        .removePrefix("#")
                        .removePrefix("-")
                        .removePrefix("*")
                        .trim()
                    if (cleanLine.length >= 10 && !cleanLine.startsWith("JSON", ignoreCase = true) && !cleanLine.startsWith("[")) {
                        return cleanLine.take(200)
                    }
                }
            }
        }

        if (fallbackSentence.isNotBlank()) {
            val cleanSentence = fallbackSentence.trim().take(150)
            return "Sahnede geçen karakter. Bağlam: $cleanSentence"
        }

        return "Hikaye evreninde yer alan yan karakter ($cleanName)."
    }

    fun calculateCastMemberDifficultyMultiplier(role: String, characterName: String, cueSentence: String = ""): Double {
        return Companion.calculateCastMemberDifficultyMultiplier(role, characterName, cueSentence)
    }

    fun determineEffectiveRoleMultiplier(member: CastMemberEntity, sceneContext: String): Double {
        return Companion.determineEffectiveRoleMultiplier(member, sceneContext)
    }

    suspend fun reEvaluateCastMemberRolesIfNeeded(botId: String, currentMessageCount: Int, sceneContext: String = "") {
        val members = castMemberDao.getCastMembersForBot(botId)
        val lowerScene = sceneContext.lowercase()
        val hasMajorStoryEvent = listOf(
            "kurtar", "evlen", "sevgili", "dost", "ihanet", "kavga", "terfi", "ayrıl", "sır", "affet", "düşman", "yemin", "zafer"
        ).any { lowerScene.contains(it) }

        // Central provider resolution for utility model (3.5)
        val settings = getOrCreateSettings()
        val customProvidersList = try { db.customProviderDao().getAllProviders() } catch (_: Exception) { emptyList() }
        val customMap = customProvidersList.associateBy { it.id }
        val (activeProvider, effectiveSettings) = resolveActiveProviderAndSettings(
            settings, customMap, getBuildConfigKey()
        )

        for (member in members) {
            val isPeriodicDue = currentMessageCount - member.lastRoleEvaluationIndex >= 30
            val isAffectionShift = (member.affectionScore >= 70 && member.role.contains("Yabancı", ignoreCase = true)) ||
                (member.affectionScore >= 80 && !member.role.contains("Dost", ignoreCase = true) && !member.role.contains("Sevgili", ignoreCase = true)) ||
                (member.affectionScore <= 25 && !member.role.contains("Rakip", ignoreCase = true) && !member.role.contains("Düşman", ignoreCase = true))

            if (isPeriodicDue || isAffectionShift || hasMajorStoryEvent) {
                // 3.5: First try LLM call with utility model; fall back to heuristic if unavailable
                var updatedRole: String? = null
                if (activeProvider != null && sceneContext.isNotBlank()) {
                    try {
                        val customP = if (activeProvider.startsWith("custom_")) {
                            val id = activeProvider.removePrefix("custom_").toLongOrNull()
                            if (id != null) customMap[id] else null
                        } else null
                        val modelName = resolveUtilityModel(activeProvider, effectiveSettings, customP)
                        val adapter = com.example.data.api.LLMAdapterFactory.createAdapter(
                            providerKey = activeProvider,
                            modelName = modelName,
                            settings = effectiveSettings,
                            customProvider = customP?.copy(modelName = modelName),
                            buildConfigGeminiKey = getBuildConfigKey()
                        )
                        val revalPrompt = """
                        Hikaye bağlamına göre '${member.name}' karakterinin mevcut rolü ('${member.role}') hâlâ geçerli mi?
                        Son sahne: ${sceneContext.take(200)}
                        Mevcut rol: ${member.role}, yakınlık skoru: ${member.affectionScore}/100
                        Sadece şu JSON ile yanıtla: {"new_role": "rol adı", "changed": true|false, "reason": "tek satır neden"}
                        Rol değişmediyse changed=false ve new_role="${member.role}" döndür.
                        """.trimIndent()
                        val reqMsgs = listOf(MessageEntity(id = "reval_${member.id}", botId = botId, role = "user", text = revalPrompt, timestamp = 0L))
                        val resp = adapter.sendMessage(
                            systemPrompt = "Sen bir karakter rol değerlendirme asistanısın. Sadece JSON döndür.",
                            messages = reqMsgs,
                            tools = null
                        )
                        val jsonMatch = Regex("""(?is)\{.*?\}""").find(resp.text)?.value
                        if (jsonMatch != null) {
                            val jobj = JSONObject(jsonMatch)
                            val changed = jobj.optBoolean("changed", false)
                            val newRole = jobj.optString("new_role", member.role).ifBlank { member.role }
                            val reason = jobj.optString("reason", "")
                            android.util.Log.d("RoleReeval", "[reEvaluateCastMemberRoles] ${member.name}: changed=$changed '${member.role}'→'$newRole' reason='$reason' model=$modelName")
                            if (changed) updatedRole = newRole
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("RoleReeval", "[reEvaluateCastMemberRoles] LLM call failed for ${member.name}: ${e.message}, falling back to heuristic")
                    }
                }

                // Heuristic fallback (also used when LLM is unavailable or returned changed=false)
                if (updatedRole == null) {
                    updatedRole = when {
                        lowerScene.contains("ihanet") || lowerScene.contains("düşman") || member.affectionScore <= 20 -> "Rakip / Düşman"
                        lowerScene.contains("sevgili") || lowerScene.contains("evlen") || member.affectionScore >= 88 -> "Sevgili"
                        member.affectionScore >= 75 || lowerScene.contains("kurtar") || lowerScene.contains("can dost") -> "Yakın Dost"
                        member.affectionScore in 60..74 && (member.role.contains("Yabancı", ignoreCase = true) || member.role.contains("Yan Karakter", ignoreCase = true) || member.role.contains("Tanıdık", ignoreCase = true)) -> "Güvenilir Arkadaş"
                        member.affectionScore in 40..59 && member.role.contains("Yabancı", ignoreCase = true) -> "Tanıdık"
                        else -> member.role  // No change
                    }
                }

                // 3.5: Only update multiplier forward — do NOT reset affectionScore
                val newMult = when (updatedRole) {
                    "Yakın Dost" -> 1.3
                    else -> calculateCastMemberDifficultyMultiplier(updatedRole, member.name, sceneContext)
                }
                if (updatedRole != member.role || newMult != member.baseAffectionDifficultyMultiplier) {
                    android.util.Log.d("RoleReeval", "[reEvaluateCastMemberRoles] Updating ${member.name}: role='$updatedRole' mult=$newMult (affectionScore=${member.affectionScore} UNCHANGED)")
                    castMemberDao.updateCastMember(
                        member.copy(
                            role = updatedRole,
                            baseAffectionDifficultyMultiplier = newMult,
                            lastRoleEvaluationIndex = currentMessageCount
                            // affectionScore intentionally NOT modified — only future rate changes
                        )
                    )
                }
            }
        }
    }

    suspend fun auditExistingCastMembers(botId: String): List<CastMemberAuditResult> {
        val members = castMemberDao.getCastMembersForBot(botId)
        val bot = botDao.getBotById(botId)
        val extraKeywords = listOf("garson", "zorba", "çocuk", "adam", "kadın", "polis", "yoldan geçen", "sürücü", "taksi", "müşteri", "nöbetçi", "biri")

        return members.map { m ->
            val clean = cleanCharacterNameCandidate(m.name).lowercase()
            val isMain = bot != null && isMainOrUserCharacter(m.name, bot)
            val matchesExtraWord = extraKeywords.any { clean == it || clean.contains(it) }
            val isShortOrGeneric = clean.length < 3 || (m.description.contains("Sahnede beliren") && m.appearanceCount <= 1 && m.importanceScore <= 50)
            val isLowConfidence = m.detectionConfidence < 50

            val isLikelyExtra = isMain || matchesExtraWord || isShortOrGeneric || isLowConfidence
            val reason = when {
                isMain -> "Ana karakter veya kullanıcı ile aynı isim"
                matchesExtraWord -> "Genel meslek veya figüran kelimesi ($clean)"
                isShortOrGeneric -> "Kısa veya tanımsız tekil figüran"
                isLowConfidence -> "Düşük tespit güveni (%${m.detectionConfidence})"
                else -> "Geçerli karakter"
            }
            CastMemberAuditResult(m, isLikelyExtra, reason)
        }
    }

    suspend fun cleanupAuditedCastMembers(botId: String, memberIdsToDelete: List<Long>) {
        for (id in memberIdsToDelete) {
            val cm = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == id }
            if (cm != null) {
                castMemberDao.deleteCastMember(id)
                emotionDao.deleteByCharacterName(botId, cm.name)
            }
        }
    }

    suspend fun confirmPendingCastMember(botId: String, castMemberId: Long) {
        val cm = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId }
        if (cm != null) {
            // User explicitly confirmed → treated as manually added (isManuallyAdded = true via isAutoAdded = false)
            castMemberDao.updateCastMember(cm.copy(
                pendingUserConfirmation = false,
                importanceScore = maxOf(cm.importanceScore, 75),
                isAutoAdded = false
            ))
        }
    }

    suspend fun ensureKeyCharacterMirroredAsCastMember(botId: String, keyChar: KeyCharacter) {
        val cleanName = cleanCharacterNameCandidate(keyChar.name)
        if (cleanName.isBlank()) return
        val existing = castMemberDao.findByName(botId, cleanName)
        if (existing == null) {
            val personality = com.example.data.local.PersonalityProfile.deriveFromPersonality(
                aiName = cleanName,
                personalityText = keyChar.desc,
                scenarioText = ""
            )
            castMemberDao.insertCastMember(
                CastMemberEntity(
                    botId = botId,
                    name = cleanName,
                    description = keyChar.desc,
                    role = "Yan Karakter",
                    affectionScore = 50,
                    isAutoAdded = false,          // manually added
                    importanceScore = 80,
                    personalityJson = personality.toJson()
                )
            )
        } else if (existing.isAutoAdded) {
            // Upgrade existing auto-detected entry to manual
            castMemberDao.updateCastMember(existing.copy(isAutoAdded = false))
        }
    }

    suspend fun dismissPendingCastMember(botId: String, castMemberId: Long) {
        castMemberDao.deleteCastMember(castMemberId)
    }

    suspend fun deleteAutoAddedCastMemberById(botId: String, castMemberId: Long) {
        val cm = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId } ?: return
        if (!cm.isAutoAdded) return  // manuel eklenenler bu yolla silinmez
        castMemberDao.deleteCastMember(castMemberId)
        emotionDao.deleteByCharacterName(botId, cm.name)
    }

    suspend fun getCastMembersForBot(botId: String): List<com.example.data.local.CastMemberEntity> =
        castMemberDao.getCastMembersForBot(botId)

    // ---------------------------------------------------------------------------
    // 4.6: Mevsim yardımcıları — storyCalendarDate'ten mevsim hesaplama
    // ---------------------------------------------------------------------------

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

    suspend fun getFirstPendingCastMember(botId: String): CastMemberEntity? =
        castMemberDao.getCastMembersForBot(botId)
            .firstOrNull { it.pendingUserConfirmation && !it.isBlacklisted }

    suspend fun blacklistCastMember(botId: String, castMemberId: Long) {
        val cm = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId }
            ?: castMemberDao.getPendingCastMembersForBot(botId).firstOrNull { it.id == castMemberId }
        if (cm != null) {
            castMemberDao.updateCastMember(cm.copy(isBlacklisted = true, pendingUserConfirmation = false))
            CastDetectorEngine.addToBlacklist(context, cm.name)
            android.util.Log.d("EmochiRepository", "[blacklistCastMember] '${cm.name}' added to blacklist, will not be suggested again")
        } else {
            castMemberDao.blacklistCastMember(castMemberId)
        }
    }

    suspend fun getBlacklistedCharacterNames(botId: String): Set<String> {
        val dbBlacklisted = castMemberDao.getBlacklistedNamesForBot(botId)
            .map { cleanCharacterNameCandidate(it).lowercase(java.util.Locale("tr", "TR")) }
            .filter { it.isNotBlank() }
            .toSet()
        val globalBlacklisted = CastDetectorEngine.getBlacklist(context)
        return dbBlacklisted + globalBlacklisted
    }

    suspend fun ensureSideCharacterRegistered(
        botId: String,
        charName: String,
        role: String = "Yan Karakter",
        cueSentence: String = "",
        confidence: Int = 80,
        pendingConfirmation: Boolean = false,
        difficultyMultiplier: Double = 0.0,
        secondaryRole: String = ""
    ) {
        val bot = botDao.getBotById(botId) ?: return
        val cleanName = cleanCharacterNameCandidate(charName)
        if (cleanName.isBlank() || isMainOrUserCharacter(charName, bot) || isMainOrUserCharacter(cleanName, bot)) return

        val existingCast = castMemberDao.findByName(botId, cleanName)
        if (existingCast?.isBlacklisted == true) return

        val richDesc = extractCharacterDescriptionFromContext(cleanName, bot, cueSentence)
        val now = System.currentTimeMillis()
        val calculatedMult = if (difficultyMultiplier > 0.0) difficultyMultiplier else calculateCastMemberDifficultyMultiplier(role, cleanName, cueSentence)

        // Derive a PersonalityProfile for the side character once at first registration.
        // Uses the character's description + role as context, mirroring the main character derivation.
        val derivedPersonality = com.example.data.local.PersonalityProfile.deriveFromPersonality(
            aiName = cleanName,
            personalityText = "$role $richDesc",
            scenarioText = cueSentence
        )

        if (existingCast == null) {
            castMemberDao.insertCastMember(
                CastMemberEntity(
                    botId = botId,
                    name = cleanName,
                    description = richDesc,
                    role = role,
                    affectionScore = 50,
                    relationshipState = "Tanıdık",
                    firstAppearedAt = now,
                    importanceScore = if (pendingConfirmation) 55 else 75,
                    isAutoAdded = true,
                    detectionConfidence = confidence,
                    pendingUserConfirmation = pendingConfirmation,
                    appearanceCount = 1,
                    lastSceneContext = cueSentence,
                    baseAffectionDifficultyMultiplier = calculatedMult,
                    secondaryRole = secondaryRole,
                    personalityJson = derivedPersonality.toJson()
                )
            )
        } else {
            val updatedAppearance = existingCast.appearanceCount + 1
            val shouldPromote = updatedAppearance >= 2
            val updatedConf = if (shouldPromote) maxOf(existingCast.detectionConfidence, 90) else existingCast.detectionConfidence
            val updatedPending = if (shouldPromote) false else existingCast.pendingUserConfirmation

            castMemberDao.updateCastMember(
                existingCast.copy(
                    appearanceCount = updatedAppearance,
                    detectionConfidence = updatedConf,
                    pendingUserConfirmation = updatedPending,
                    importanceScore = if (shouldPromote) maxOf(existingCast.importanceScore, 80) else existingCast.importanceScore,
                    lastSceneContext = cueSentence.ifBlank { existingCast.lastSceneContext },
                    description = if (existingCast.description.contains("Sahnede beliren") && !richDesc.contains("Sahnede beliren")) richDesc else existingCast.description
                )
            )
        }

        try {
            val existingEmotion = emotionDao.getEmotionForCharacter(botId, cleanName)
            if (existingEmotion == null) {
                val defaultState = EmotionState(
                    dominantEmotion = "Nötr",
                    relationshipAxes = RelationshipAxes(affectionScore = 50),
                    primaryEmotions = PrimaryEmotions(trust = 50)
                )
                emotionDao.insertOrUpdate(
                    CharacterEmotionEntity(
                        botId = botId,
                        characterName = cleanName,
                        emotionState = defaultState.toJson()
                    )
                )
            }
        } catch (_: Exception) {}

        try {
            val keyChars = parseKeyCharacters(bot.keyCharactersJson).toMutableList()
            if (keyChars.none { it.name.equals(cleanName, ignoreCase = true) || cleanCharacterNameCandidate(it.name).equals(cleanName, ignoreCase = true) }) {
                keyChars.add(
                    KeyCharacter(
                        id = "auto_${now}_${cleanName.replace(" ", "_")}",
                        name = cleanName,
                        desc = richDesc
                    )
                )
                botDao.insertOrUpdate(bot.copy(keyCharactersJson = serializeKeyCharacters(keyChars)))
            }
        } catch (_: Exception) {}
    }

    suspend fun cleanupInvalidCastMembers(botId: String) {
        val bot = botDao.getBotById(botId) ?: return
        try {
            val castMembers = castMemberDao.getCastMembersForBot(botId)
            for (cm in castMembers) {
                if (isMainOrUserCharacter(cm.name, bot) || cleanCharacterNameCandidate(cm.name).length < 2) {
                    castMemberDao.deleteCastMember(cm.id)
                }
            }

            val emotions = emotionDao.getEmotionsForBot(botId)
            for (em in emotions) {
                if (isMainOrUserCharacter(em.characterName, bot) || cleanCharacterNameCandidate(em.characterName).length < 2) {
                    emotionDao.deleteEmotionById(em.id)
                }
            }

            val keyChars = parseKeyCharacters(bot.keyCharactersJson).toMutableList()
            val initialSize = keyChars.size
            keyChars.removeAll { isMainOrUserCharacter(it.name, bot) || cleanCharacterNameCandidate(it.name).length < 2 }
            if (keyChars.size != initialSize) {
                botDao.insertOrUpdate(bot.copy(keyCharactersJson = serializeKeyCharacters(keyChars)))
            }
        } catch (_: Exception) {}
    }

    suspend fun evaluateCharacterSignificance(
        candidateName: String,
        sceneContext: String,
        apiKey: String? = null
    ): Quadruple<Boolean, String, Int, Double> {
        val clean = cleanCharacterNameCandidate(candidateName)
        val lower = clean.lowercase()

        // 1. Generic extras blacklist
        val genericExtras = setOf("garson", "zorba", "çocuk", "adam", "kadın", "polis", "müşteri", "yoldan geçen", "şoför", "taksici", "nöbetçi", "biri", "herkes")
        if (genericExtras.contains(lower) || genericExtras.any { lower.contains(it) && !lower.contains("doktor") && !lower.contains("komutan") }) {
            return Quadruple(false, "Figüran", 20, 1.0)
        }

        // 2. Central provider resolution — no duplicated hasKey logic
        val settings = getOrCreateSettings()
        val customProvidersList = try { db.customProviderDao().getAllProviders() } catch (_: Exception) { emptyList() }
        val customMap = customProvidersList.associateBy { it.id }
        val (activeProvider, effectiveSettings) = resolveActiveProviderAndSettings(
            settings, customMap, getBuildConfigKey(), apiKey
        )

        // If no provider has a configured key, skip network call completely and fallback directly to heuristic
        if (activeProvider != null) {
            try {
                val customP = if (activeProvider.startsWith("custom_")) {
                    val id = activeProvider.removePrefix("custom_").toLongOrNull()
                    if (id != null) customMap[id] else null
                } else null

                val modelName = resolveUtilityModel(activeProvider, effectiveSettings, customP)
                val effectiveCustomP = customP?.copy(modelName = modelName)
                android.util.Log.d("EmochiRepository", "[evaluateCharacterSignificance] utility model='$modelName' provider='$activeProvider'")

                val adapter = com.example.data.api.LLMAdapterFactory.createAdapter(
                    providerKey = activeProvider,
                    modelName = modelName,
                    settings = effectiveSettings,
                    customProvider = effectiveCustomP,
                    buildConfigGeminiKey = getBuildConfigKey()
                )

                val shortPrompt = """
                Aşağıdaki sahnede geçen '$clean' karakteri önemli/tekrar eden bir yan karakter mi yoksa önemsiz bir figüran/arka plan unsuru mu?
                Sahne: ${sceneContext.take(250)}
                Yanıtını SADECE şu JSON ile ver:
                {"is_significant": true|false, "role": "Rol", "confidence": 0-100, "status": "authority|peer|intimate|stranger"}
                """.trimIndent()

                val reqMsgs = listOf(MessageEntity(id = "eval_char", botId = "", role = "user", text = shortPrompt, timestamp = 0L))
                val resp = adapter.sendMessage(
                    systemPrompt = "Sen bir karakter sınıflandırıcısın. Sadece istenen JSON formatında yanıt ver.",
                    messages = reqMsgs,
                    tools = null
                )
                android.util.Log.i("EmochiClassification", "Character significance classification successfully completed via provider: $activeProvider ($modelName)")

                val text = resp.text
                val jsonMatch = Regex("""(?is)\{.*?\}""").find(text)?.value
                if (jsonMatch != null) {
                    val jobj = JSONObject(jsonMatch)
                    val isSig = jobj.optBoolean("is_significant", true)
                    val role = jobj.optString("role", "Yan Karakter")
                    val conf = jobj.optInt("confidence", 80).coerceIn(0, 100)
                    val status = jobj.optString("status", "peer")
                    val mult = when (status.lowercase()) {
                        "authority" -> 0.70
                        "stranger" -> 0.85
                        "intimate" -> 1.40
                        else -> 1.05
                    }
                    return Quadruple(isSig, role, conf, mult)
                }
            } catch (e: Exception) {
                val failure = ClassificationCallFailure(
                    provider = activeProvider,
                    errorMessage = e.message ?: e.toString(),
                    timestamp = System.currentTimeMillis()
                )
                classificationCallFailureLog.add(failure)
                android.util.Log.e("EmochiRepository", "Character significance classification: utility model failed on $activeProvider (${e.message}), trying main model")
                // B5.7 Tier2: retry with main model before heuristic
                try {
                    val customP2 = if (activeProvider.startsWith("custom_")) {
                        val id2 = activeProvider.removePrefix("custom_").toLongOrNull()
                        if (id2 != null) customMap[id2] else null
                    } else null
                    val mainModelName = resolveModelForProvider(activeProvider, effectiveSettings, customP2)
                    val adapter2 = com.example.data.api.LLMAdapterFactory.createAdapter(
                        providerKey = activeProvider,
                        modelName = mainModelName,
                        settings = effectiveSettings,
                        customProvider = customP2,
                        buildConfigGeminiKey = getBuildConfigKey()
                    )
                    val shortPrompt2 = "'$clean' karakteri onemli/tekrar eden bir yan karakter mi? Sahne: ${sceneContext.take(200)}\nSADECE: {\"is_significant\": true|false, \"role\": \"Rol\", \"confidence\": 0-100, \"status\": \"authority|peer|intimate|stranger\"}"
                    val reqMsgs2 = listOf(MessageEntity(id = "eval_char2", botId = "", role = "user", text = shortPrompt2, timestamp = 0L))
                    val resp2 = adapter2.sendMessage(
                        systemPrompt = "Sen bir karakter siniflandiricisin. Sadece JSON dondur.",
                        messages = reqMsgs2,
                        tools = null
                    )
                    val jsonMatch2 = Regex("""(?is)\{.*?\}""").find(resp2.text)?.value
                    if (jsonMatch2 != null) {
                        val jobj2 = JSONObject(jsonMatch2)
                        val isSig2 = jobj2.optBoolean("is_significant", true)
                        val role2 = jobj2.optString("role", "Yan Karakter")
                        val conf2 = jobj2.optInt("confidence", 80).coerceIn(0, 100)
                        val mult2 = when (jobj2.optString("status", "peer").lowercase()) {
                            "authority" -> 0.70; "stranger" -> 0.85; "intimate" -> 1.40; else -> 1.05
                        }
                        android.util.Log.i("EmochiClassification", "[B5.7-Tier2] main model ok: provider=$activeProvider model=$mainModelName")
                        return Quadruple(isSig2, role2, conf2, mult2)
                    }
                } catch (e2: Exception) {
                    classificationCallFailureLog.add(ClassificationCallFailure(provider = "$activeProvider[main]", errorMessage = e2.message ?: e2.toString()))
                    android.util.Log.e("EmochiRepository", "B5.7: main model also failed ($activeProvider): ${e2.message}. Falling to heuristic.")
                }
            }
        } else {
            android.util.Log.d("EmochiClassification", "No AI provider key configured, using heuristic directly for: $clean")
        }

        // Heuristic fallback (B5.7 Tier3)
        val isProperName = clean.length in 3..25 && clean.first().isUpperCase()
        val hasSpeechOrTitle = sceneContext.contains("$clean dedi") || sceneContext.contains("$clean sordu") || sceneContext.contains("Bay $clean") || sceneContext.contains("Doktor $clean") || sceneContext.contains("Dr. $clean")
        val confidence = if (hasSpeechOrTitle) 85 else if (isProperName) 65 else 40
        val role = when {
            sceneContext.contains("Doktor") || sceneContext.contains("Dr.") -> "Doktor"
            sceneContext.contains("Komutan") || sceneContext.contains("Kaptan") -> "Komutan"
            sceneContext.contains("arkadaş") || sceneContext.contains("dost") -> "Arkadaş"
            else -> "Yan Karakter"
        }
        val mult = calculateCastMemberDifficultyMultiplier(role, clean, sceneContext)
        return Quadruple(confidence >= 50, role, confidence, mult)
    }

    suspend fun detectAndRegisterCastMembers(botId: String, aiReplyText: String, apiKey: String? = null, userQuery: String = "") {
        val bot = botDao.getBotById(botId) ?: return

        cleanupInvalidCastMembers(botId)

        val blacklist = getBlacklistedCharacterNames(botId)

        val combinedText = "$aiReplyText\n$userQuery"
        if (combinedText.isBlank()) return

        // Extract newChars from STATE_JSON if present
        val newCharsJson = try {
            val stateMatch = Regex("""(?is)\[\[STATE_JSON\s*(\{.*?\})\s*\]\]""").find(aiReplyText)
            val jobj = stateMatch?.let { org.json.JSONObject(it.groupValues[1]) }
            jobj?.optJSONArray("newChars")?.toString() ?: ""
        } catch (_: Exception) { "" }

        val currentTurn = globalTurnCounter.incrementAndGet()
        val botBuffer = candidateBuffer.getOrPut(botId) { mutableMapOf() }

        // Evict expired buffer entries
        val expiredKeys = botBuffer.entries.filter { currentTurn - it.value.firstTurnIndex > BUFFER_EXPIRY_TURNS }.map { it.key }
        expiredKeys.forEach { botBuffer.remove(it) }

        // Detect all candidates via CastDetectorEngine
        val hits = CastDetectorEngine.detectCandidates(combinedText, blacklist, newCharsJson)

        // Cap at 10 candidates per turn; rest go to buffer
        val (immediate, deferred) = hits.partition { it.signalStrength == CastDetectorEngine.SignalStrength.STRONG }
        val allHits = (immediate + deferred).take(10)

        val blacklistedNamesList = castMemberDao.getBlacklistedNamesForBot(botId)

        for (hit in allHits) {
            val candidate = hit.canonicalName
            val candidateLower = candidate.lowercase(java.util.Locale("tr", "TR"))
            if (candidate.isBlank() || isMainOrUserCharacter(hit.rawName, bot) || isMainOrUserCharacter(candidate, bot)) continue
            if (blacklist.contains(candidateLower) || blacklistedNamesList.any { CastDetectorEngine.isLikelyAlias(candidate, it) }) continue

            // Alias/recurrence check against existing cast
            val existingCast = castMemberDao.getCastMembersForBot(botId)
            val aliasMatch = findAliasMatch(candidate, existingCast)
            if (aliasMatch != null) {
                // Kara listedeki alias → geç
                if (aliasMatch.isBlacklisted) continue
                val nextAppearance = aliasMatch.appearanceCount + 1
                val isPromoted = nextAppearance >= 2
                castMemberDao.updateCastMember(
                    aliasMatch.copy(
                        appearanceCount = nextAppearance,
                        pendingUserConfirmation = if (isPromoted) false else aliasMatch.pendingUserConfirmation,
                        importanceScore = if (isPromoted) maxOf(aliasMatch.importanceScore, 80) else aliasMatch.importanceScore,
                        detectionConfidence = if (isPromoted) maxOf(aliasMatch.detectionConfidence, 90) else aliasMatch.detectionConfidence,
                        lastSceneContext = hit.cueSnippet
                    )
                )
                // Buffer'dan temizle
                botBuffer.remove(candidateLower)
                continue
            }

            if (hit.signalStrength == CastDetectorEngine.SignalStrength.STRONG) {
                // Güçlü sinyal → ilk görünmede kayıt (confidence 50..70 ise kullanıcı onayı iste)
                val score = CastDetectorEngine.scoreCandidate(candidate, hit.cueSnippet, 1, hit.role, context)
                if (!score.isSignificant && score.confidence < 50) continue
                val needsConfirmation = score.confidence in 50..70
                ensureSideCharacterRegistered(
                    botId = botId, charName = candidate, role = hit.role.ifBlank { score.role },
                    cueSentence = hit.cueSnippet, confidence = score.confidence,
                    pendingConfirmation = needsConfirmation, difficultyMultiplier = score.difficultyMultiplier
                )
                botBuffer.remove(candidateLower)
            } else {
                // Zayıf sinyal → tampona ekle veya sınırda ise onay diyaloğu için kaydet
                val existing = botBuffer[candidateLower]
                if (existing != null) {
                    // İkinci görünüm → kayıt
                    val score = CastDetectorEngine.scoreCandidate(candidate, hit.cueSnippet, 2, hit.role, context)
                    if (score.isSignificant && score.confidence >= 50) {
                        val needsConfirmation = score.confidence in 50..70
                        ensureSideCharacterRegistered(
                            botId = botId, charName = candidate, role = hit.role.ifBlank { score.role },
                            cueSentence = hit.cueSnippet, confidence = score.confidence,
                            pendingConfirmation = needsConfirmation, difficultyMultiplier = score.difficultyMultiplier
                        )
                    }
                    botBuffer.remove(candidateLower)
                } else {
                    val firstScore = CastDetectorEngine.scoreCandidate(candidate, hit.cueSnippet, 1, hit.role, context)
                    if (firstScore.confidence in 50..70) {
                        // Sınırda kalan (confidence 50-70) karakterler için onay diyaloğu oluştur (3.2)
                        ensureSideCharacterRegistered(
                            botId = botId, charName = candidate, role = hit.role.ifBlank { firstScore.role },
                            cueSentence = hit.cueSnippet, confidence = firstScore.confidence,
                            pendingConfirmation = true, difficultyMultiplier = firstScore.difficultyMultiplier
                        )
                    } else if (botBuffer.size < BUFFER_MAX_SIZE) {
                        botBuffer[candidateLower] = CandidateBufferEntry(hit.role, currentTurn, hit.cueSnippet, hit.signalStrength)
                    }
                }
            }
        }
    }

    suspend fun deleteCharacterAndBlacklist(botId: String, characterName: String) {
        val cleanName = cleanCharacterNameCandidate(characterName)
        if (cleanName.isBlank()) return

        try {
            emotionDao.deleteByCharacterName(botId, cleanName)
            emotionDao.deleteByCharacterName(botId, characterName)
            val allEmotions = emotionDao.getEmotionsForBot(botId)
            for (emo in allEmotions) {
                val emoClean = cleanCharacterNameCandidate(emo.characterName)
                if (emo.characterName.equals(cleanName, ignoreCase = true) ||
                    emo.characterName.equals(characterName, ignoreCase = true) ||
                    emoClean.equals(cleanName, ignoreCase = true) ||
                    emoClean.equals(characterName, ignoreCase = true)
                ) {
                    emotionDao.deleteEmotionById(emo.id)
                }
            }
        } catch (_: Exception) {}

        try {
            val existingCast = castMemberDao.findByName(botId, cleanName) ?: castMemberDao.findByName(botId, characterName)
            if (existingCast != null) {
                castMemberDao.updateCastMember(existingCast.copy(isBlacklisted = true))
            } else {
                castMemberDao.insertCastMember(
                    CastMemberEntity(
                        botId = botId,
                        name = cleanName,
                        description = "Silindi / Kara Listede",
                        role = "Kara Liste",
                        isBlacklisted = true
                    )
                )
            }
        } catch (_: Exception) {}

        try {
            val bot = botDao.getBotById(botId)
            if (bot != null) {
                val keyChars = parseKeyCharacters(bot.keyCharactersJson).toMutableList()
                val removed = keyChars.removeAll { 
                    it.name.equals(cleanName, ignoreCase = true) || 
                    it.name.equals(characterName, ignoreCase = true) ||
                    cleanCharacterNameCandidate(it.name).equals(cleanName, ignoreCase = true)
                }
                if (removed) {
                    botDao.insertOrUpdate(bot.copy(keyCharactersJson = serializeKeyCharacters(keyChars)))
                }
            }
        } catch (_: Exception) {}
    }

    suspend fun mergeDuplicateCastMembers(botId: String) {
        val bot = botDao.getBotById(botId) ?: return
        try {
            val allCast = castMemberDao.getCastMembersForBot(botId)
            val merged = mutableSetOf<Long>() // already processed ids

            for (i in allCast.indices) {
                val a = allCast[i]
                if (a.id in merged) continue
                for (j in i + 1 until allCast.size) {
                    val b = allCast[j]
                    if (b.id in merged) continue
                    if (!CastDetectorEngine.isLikelyAlias(a.name, b.name)) continue

                    // Ambiguity guard: ensure only one match exists for each side
                    val aMatches = allCast.count { it.id != a.id && CastDetectorEngine.isLikelyAlias(a.name, it.name) }
                    val bMatches = allCast.count { it.id != b.id && CastDetectorEngine.isLikelyAlias(b.name, it.name) }
                    if (aMatches > 1 || bMatches > 1) continue // ambiguous — skip

                    // Keep the one with higher importanceScore; on tie keep the older (lower id)
                    val (keep, discard) = if (a.importanceScore >= b.importanceScore) Pair(a, b) else Pair(b, a)

                    try {
                        // Merge: sum appearances, keep max importance/confidence
                        val updated = keep.copy(
                            appearanceCount = keep.appearanceCount + discard.appearanceCount,
                            importanceScore = maxOf(keep.importanceScore, discard.importanceScore),
                            detectionConfidence = maxOf(keep.detectionConfidence, discard.detectionConfidence),
                            pendingUserConfirmation = keep.pendingUserConfirmation && discard.pendingUserConfirmation,
                            lastSceneContext = if (keep.lastSceneContext.isBlank()) discard.lastSceneContext else keep.lastSceneContext
                        )
                        castMemberDao.updateCastMember(updated)
                        castMemberDao.deleteCastMember(discard.id)
                        merged.add(discard.id)

                        // Remove discarded name from keyCharactersJson
                        try {
                            val keyChars = parseKeyCharacters(bot.keyCharactersJson).toMutableList()
                            val removed = keyChars.removeAll {
                                it.name.equals(discard.name, ignoreCase = true) ||
                                cleanCharacterNameCandidate(it.name).equals(cleanCharacterNameCandidate(discard.name), ignoreCase = true)
                            }
                            if (removed) botDao.insertOrUpdate(bot.copy(keyCharactersJson = serializeKeyCharacters(keyChars)))
                        } catch (_: Exception) {}

                        // Remove discarded emotion entry
                        try { emotionDao.deleteByCharacterName(botId, discard.name) } catch (_: Exception) {}
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {}
    }
