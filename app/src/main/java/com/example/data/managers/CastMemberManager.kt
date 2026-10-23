package com.example.data.managers

import com.example.data.local.BotDao
import com.example.data.local.BotEntity
import com.example.data.local.CastMemberDao
import com.example.data.local.CastMemberEntity
import com.example.data.local.CharacterEmotionDao
import com.example.data.local.CharacterEmotionEntity
import com.example.data.local.EmotionState
import com.example.data.repository.EmochiRepository
import com.example.data.repository.KeyCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Locale

class CastMemberManager(
    private val castMemberDao: CastMemberDao,
    private val emotionDao: CharacterEmotionDao,
    private val botDao: BotDao
) {
    private val turkishLocale = Locale("tr", "TR")

    fun cleanCharacterNameCandidate(raw: String): String {
        val cleaned = raw
            .replace(Regex("""\[.*?\]"""), "")
            .replace(Regex("""\(.*?\)"""), "")
            .replace(Regex("""["'«»“”‘’*`_~]"""), "")
            .trim()
        val firstPart = cleaned.split(":", "-", "—", "|", "/").firstOrNull()?.trim() ?: cleaned
        return firstPart.take(45).trim()
    }

    fun isMainOrUserCharacter(candidateRaw: String, bot: BotEntity): Boolean {
        val clean = cleanCharacterNameCandidate(candidateRaw).lowercase(turkishLocale)
        if (clean.isBlank() || clean.length < 2) return true

        val mainNames = listOf(
            bot.aiName,
            bot.userCharName,
            bot.universeName,
            "kullanıcı",
            "user",
            "anlatıcı",
            "narrator",
            "sistem",
            "system",
            "ana karakter",
            "sen",
            "ben"
        ).map { it.trim().lowercase(turkishLocale) }.filter { it.isNotBlank() }

        return mainNames.any { main ->
            clean == main ||
                (main.length >= 3 && clean.startsWith("$main ")) ||
                (clean.length >= 3 && main.startsWith("$clean "))
        }
    }

    suspend fun ensureKeyCharacterMirroredAsCastMember(botId: String, keyChar: KeyCharacter) = withContext(Dispatchers.IO) {
        val cleanName = cleanCharacterNameCandidate(keyChar.name)
        if (cleanName.length < 2) return@withContext

        val blacklisted = castMemberDao.getBlacklistedNamesForBot(botId).map { it.lowercase(turkishLocale) }
        if (blacklisted.contains(cleanName.lowercase(turkishLocale))) return@withContext

        val existing = castMemberDao.findByName(botId, cleanName)
        val baseMult = EmochiRepository.calculateCastMemberDifficultyMultiplier(
            role = keyChar.desc.ifBlank { "Anahtar Karakter" },
            characterName = cleanName,
            cueSentence = keyChar.desc
        )
        if (existing == null) {
            castMemberDao.insertCastMember(
                CastMemberEntity(
                    botId = botId,
                    name = cleanName,
                    description = keyChar.desc,
                    role = keyChar.desc.take(40).ifBlank { "Anahtar Karakter" },
                    affectionScore = 50,
                    relationshipState = "Tanıdık",
                    importanceScore = 8,
                    isAutoAdded = false,
                    isBlacklisted = false,
                    detectionConfidence = 100,
                    pendingUserConfirmation = false,
                    baseAffectionDifficultyMultiplier = baseMult
                )
            )
        } else if (existing.description.isBlank() && keyChar.desc.isNotBlank()) {
            castMemberDao.updateCastMember(
                existing.copy(
                    description = keyChar.desc,
                    pendingUserConfirmation = false
                )
            )
        }
    }

    suspend fun ensureSideCharacterRegistered(
        botId: String,
        rawCharacterName: String,
        role: String = "Yan Karakter",
        initialMood: String = "nötr",
        affectionDelta: Int = 0,
        cueSentence: String = "",
        bot: BotEntity? = null
    ) = withContext(Dispatchers.IO) {
        val cleanName = cleanCharacterNameCandidate(rawCharacterName)
        if (cleanName.length < 2) return@withContext

        val targetBot = bot ?: botDao.getBotById(botId)
        if (targetBot != null && isMainOrUserCharacter(cleanName, targetBot)) return@withContext

        val blacklisted = castMemberDao.getBlacklistedNamesForBot(botId).map { it.lowercase(turkishLocale) }
        if (blacklisted.contains(cleanName.lowercase(turkishLocale))) return@withContext

        val existing = castMemberDao.findByName(botId, cleanName)
        val (isSignificant, evalRole, confidence, difficultyMult) = EmochiRepository.evaluateCharacterSignificance(
            candidateName = cleanName,
            sceneContext = cueSentence.ifBlank { role }
        )
        val resolvedRole = if (role.isNotBlank() && role != "Yan Karakter") role else evalRole

        if (existing == null) {
            if (!isSignificant && confidence < 45) return@withContext
            val initialAffection = (50 + affectionDelta.coerceIn(-15, 15)).coerceIn(0, 100)
            castMemberDao.insertCastMember(
                CastMemberEntity(
                    botId = botId,
                    name = cleanName,
                    description = cueSentence.take(240),
                    role = resolvedRole,
                    affectionScore = initialAffection,
                    relationshipState = initialMood.ifBlank { "Tanıdık" },
                    importanceScore = if (confidence >= 80) 7 else 5,
                    isAutoAdded = true,
                    isBlacklisted = false,
                    detectionConfidence = confidence,
                    pendingUserConfirmation = confidence in 50..79,
                    appearanceCount = 1,
                    lastSceneContext = cueSentence.take(200),
                    baseAffectionDifficultyMultiplier = difficultyMult
                )
            )
        } else if (!existing.isBlacklisted) {
            val newCount = existing.appearanceCount + 1
            val updatedAffection = (existing.affectionScore + affectionDelta.coerceIn(-15, 15)).coerceIn(0, 100)
            castMemberDao.updateCastMember(
                existing.copy(
                    role = if (existing.role.isBlank() || existing.role == "Yan Karakter") resolvedRole else existing.role,
                    affectionScore = updatedAffection,
                    appearanceCount = newCount,
                    lastSceneContext = if (cueSentence.isNotBlank()) cueSentence.take(200) else existing.lastSceneContext
                )
            )
        }

        val existingEmotion = emotionDao.getEmotionForCharacter(botId, cleanName)
        if (existingEmotion == null) {
            val baseState = EmotionState.calculateBaselineEmotionState(
                aiName = cleanName,
                personality = resolvedRole,
                scenario = cueSentence
            ).copy(dominantEmotion = initialMood.ifBlank { "nötr" })
            emotionDao.insertOrUpdate(
                CharacterEmotionEntity(
                    botId = botId,
                    characterName = cleanName,
                    emotionState = baseState.toJson()
                )
            )
        }
    }

    suspend fun reEvaluateCastMemberRolesIfNeeded(
        botId: String,
        currentMessageCount: Int,
        sceneContext: String = ""
    ) = withContext(Dispatchers.IO) {
        if (sceneContext.isBlank()) return@withContext
        val members = castMemberDao.getConfirmedCastMembersForBot(botId)
        for (member in members) {
            if (currentMessageCount - member.lastRoleEvaluationIndex < 8) continue
            if (!sceneContext.contains(member.name, ignoreCase = true)) continue

            val (_, inferredRole, _, newMult) = EmochiRepository.evaluateCharacterSignificance(
                candidateName = member.name,
                sceneContext = sceneContext
            )
            val updatedSecondary = if (inferredRole != member.role && inferredRole != "Yan Karakter") {
                inferredRole
            } else {
                member.secondaryRole
            }
            val rolesSet = linkedSetOf<String>()
            if (member.role.isNotBlank()) rolesSet.add(member.role)
            if (updatedSecondary.isNotBlank()) rolesSet.add(updatedSecondary)
            val multiArr = JSONArray()
            rolesSet.forEach { multiArr.put(it) }

            castMemberDao.updateCastMember(
                member.copy(
                    secondaryRole = updatedSecondary,
                    multiRoleJson = multiArr.toString(),
                    lastRoleEvaluationIndex = currentMessageCount,
                    lastSceneContext = sceneContext.take(200),
                    baseAffectionDifficultyMultiplier = if (member.baseAffectionDifficultyMultiplier > 0.0) {
                        member.baseAffectionDifficultyMultiplier
                    } else {
                        newMult
                    }
                )
            )
        }
    }

    suspend fun getCastMembersForBot(botId: String): List<CastMemberEntity> = withContext(Dispatchers.IO) {
        castMemberDao.getCastMembersForBot(botId).filter { !it.isBlacklisted }
    }

    suspend fun getFirstPendingCastMember(botId: String): CastMemberEntity? = withContext(Dispatchers.IO) {
        castMemberDao.getPendingCastMembersForBot(botId).firstOrNull { !it.isBlacklisted }
    }

    suspend fun confirmPendingCastMember(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId } ?: return@withContext
        castMemberDao.updateCastMember(
            member.copy(
                pendingUserConfirmation = false,
                isBlacklisted = false,
                detectionConfidence = maxOf(member.detectionConfidence, 90)
            )
        )
    }

    suspend fun dismissPendingCastMember(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId } ?: return@withContext
        castMemberDao.updateCastMember(member.copy(pendingUserConfirmation = false))
    }

    suspend fun blacklistCastMember(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId }
        castMemberDao.blacklistCastMember(castMemberId)
        if (member != null) {
            emotionDao.deleteByCharacterName(botId, member.name)
        }
    }

    suspend fun deleteAutoAddedCastMemberById(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersForBot(botId).firstOrNull { it.id == castMemberId }
        castMemberDao.deleteCastMember(castMemberId)
        if (member != null) {
            emotionDao.deleteByCharacterName(botId, member.name)
        }
    }

    suspend fun deleteCharacterAndBlacklist(botId: String, characterName: String) = withContext(Dispatchers.IO) {
        val cleanName = cleanCharacterNameCandidate(characterName)
        val existing = castMemberDao.findByName(botId, cleanName) ?: castMemberDao.findByName(botId, characterName)
        if (existing != null) {
            castMemberDao.updateCastMember(
                existing.copy(
                    isBlacklisted = true,
                    pendingUserConfirmation = false
                )
            )
        } else if (cleanName.isNotBlank()) {
            castMemberDao.insertCastMember(
                CastMemberEntity(
                    botId = botId,
                    name = cleanName,
                    description = "",
                    isAutoAdded = true,
                    isBlacklisted = true,
                    pendingUserConfirmation = false
                )
            )
        }
        emotionDao.deleteByCharacterName(botId, characterName)
        if (cleanName != characterName) {
            emotionDao.deleteByCharacterName(botId, cleanName)
        }
    }

    suspend fun updateCastMembersAgeForBot(botId: String, currentStoryCalendarDateStr: String) = withContext(Dispatchers.IO) {
        val yearMatch = Regex("""\b(19\d{2}|20\d{2}|21\d{2})\b""").find(currentStoryCalendarDateStr)?.value?.toIntOrNull()
            ?: return@withContext
        val members = castMemberDao.getCastMembersForBot(botId)
        for (member in members) {
            val birthYear = Regex("""\b(19\d{2}|20\d{2}|21\d{2})\b""").find(member.birthDate)?.value?.toIntOrNull()
            if (birthYear != null && yearMatch >= birthYear) {
                val computedAge = (yearMatch - birthYear).coerceIn(1, 150)
                if (computedAge != member.currentAge) {
                    castMemberDao.updateCastMember(member.copy(currentAge = computedAge))
                }
            }
        }
    }
}
