package com.example.data.managers

import com.example.data.local.AffectionEventDao
import com.example.data.local.AffectionEventEntity
import com.example.data.local.BotDao
import com.example.data.local.CharacterEmotionDao
import com.example.data.local.EmotionHistoryDao
import com.example.data.local.EmotionHistoryEntity
import com.example.data.local.EmotionState
import com.example.data.repository.EmochiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class EmotionEngineManager(
    private val botDao: BotDao,
    private val emotionDao: CharacterEmotionDao,
    private val emotionHistoryDao: EmotionHistoryDao,
    private val affectionEventDao: AffectionEventDao
) {
    suspend fun recordMainEmotionSnapshot(
        botId: String,
        messageIndex: Int,
        state: EmotionState
    ) = withContext(Dispatchers.IO) {
        val vectorJson = try {
            JSONObject().apply {
                put("dominantEmotion", state.dominantEmotion)
                put("suppressedEmotion", state.suppressedEmotion)
                put("affection", state.relationshipAxes.affectionScore)
                put("trust", state.primaryEmotions.trust)
                put("respect", state.relationshipAxes.respectScore)
                put("comfort", state.relationshipAxes.comfortScore)
                put("resentment", state.relationshipAxes.resentmentScore)
                put("physicalComfort", state.physicalComfortScore)
                put("obsession", state.obsessionScore)
            }.toString()
        } catch (_: Exception) {
            "{}"
        }

        emotionHistoryDao.insertHistory(
            EmotionHistoryEntity(
                botId = botId,
                timestamp = System.currentTimeMillis(),
                messageIndex = messageIndex,
                affectionScore = state.relationshipAxes.affectionScore,
                respectScore = state.relationshipAxes.respectScore,
                comfortScore = state.relationshipAxes.comfortScore,
                resentmentScore = state.relationshipAxes.resentmentScore,
                trustScore = state.primaryEmotions.trust,
                physicalComfortScore = state.physicalComfortScore,
                obsessionScore = state.obsessionScore,
                dominantEmotion = state.dominantEmotion,
                fullVectorJson = vectorJson,
                characterName = null
            )
        )
    }

    suspend fun getRecentEmotionHistory(botId: String, limit: Int = 30): List<EmotionHistoryEntity> =
        withContext(Dispatchers.IO) {
            emotionHistoryDao.getRecentHistoryForBot(botId, limit)
        }

    suspend fun getRecentCharacterEmotionHistory(
        botId: String,
        characterName: String,
        limit: Int = 20
    ): List<EmotionHistoryEntity> = withContext(Dispatchers.IO) {
        emotionHistoryDao.getRecentHistoryForCharacter(botId, characterName, limit)
    }

    fun calculateClampedAffectionDelta(
        prevAffection: Int,
        rawDelta: Int,
        totalMsgCount: Int,
        baseMultiplier: Double = 1.0,
        combinedContextMult: Double = 1.0,
        personalityAffectionMult: Double = 1.0,
        recoveryLockUntilMessageCount: Int = 0,
        dailyAffectionGain: Int = 0,
        consecutivePositiveCount: Int = 0,
        regCount: Int = 0,
        isEstablishedRomantic: Boolean = false
    ): Int {
        return EmochiRepository.calculateClampedDelta(
            prevAffection = prevAffection,
            rawDelta = rawDelta,
            totalMsgCount = totalMsgCount,
            baseMultiplier = baseMultiplier,
            combinedContextMult = combinedContextMult,
            personalityAffectionMult = personalityAffectionMult,
            recoveryLockUntilMessageCount = recoveryLockUntilMessageCount,
            dailyAffectionGain = dailyAffectionGain,
            consecutivePositiveCount = consecutivePositiveCount,
            regCount = regCount,
            isEstablishedRomantic = isEstablishedRomantic
        )
    }

    suspend fun logAffectionEvent(
        botId: String,
        delta: Int,
        reason: String
    ) = withContext(Dispatchers.IO) {
        if (delta == 0) return@withContext
        affectionEventDao.insertEvent(
            AffectionEventEntity(
                botId = botId,
                timestamp = System.currentTimeMillis(),
                scoreDelta = delta,
                shortDescription = reason.ifBlank { "Sahne etkileşimi" }
            )
        )
    }
}
