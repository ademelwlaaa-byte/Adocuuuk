package com.example.data.managers

import com.example.data.local.BotDao
import com.example.data.local.CastMemberDao
import com.example.data.local.EmotionHistoryDao
import com.example.data.local.EmotionHistoryEntity
import com.example.data.local.PendingReappraisalDao
import com.example.data.local.SelfCheckFailureLogDao
import com.example.data.local.SensitiveTriggerDao
import com.example.data.local.SensitiveTriggerEntity
import com.example.data.repository.EmotionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Bölüm 6 — EmotionEngineManager
 *
 * Sorumlu olduğu alanlar:
 *   - calculateClampedDelta: asimetrik clamp mantığı (EmochiRepository.Companion delegate)
 *   - calculateCastMemberDifficultyMultiplier: rol bazlı çarpan
 *   - Duygu geçmişi (EmotionHistoryEntity) kaydetme ve okuma
 *   - SensitiveTrigger: intensityMultiplier uygulama, recentTriggerCount güncelleme
 *   - PendingReappraisal: tetikleme, çözüm, son kullanma tarihi
 *   - Self-check failure log yönetimi
 *   - calculateEmotionalRegulationCapacity
 *
 * NOT: LLM çağrısı içeren parseAndApplyEmotionUpdates, buildSystemPrompt,
 * evaluateSensitiveTriggers gibi fonksiyonlar EmochiRepository'de kalır
 * (bunlar birden fazla DAO + LLM adapter + context gerektiriyor).
 * Bu manager saf hesaplama + DB read/write işlemlerini üstlenir.
 */
class EmotionEngineManager(
    private val botDao: BotDao,
    private val castMemberDao: CastMemberDao,
    private val emotionHistoryDao: EmotionHistoryDao,
    private val selfCheckFailureLogDao: SelfCheckFailureLogDao,
    private val sensitiveTriggerDao: SensitiveTriggerDao,
    private val pendingReappraisalDao: PendingReappraisalDao
) {

    // ─── calculateClampedDelta — Asimetrik kısıtlama (2.3, 3.4 ortak şema) ──

    /**
     * Duygu puanı değişimini asimetrik clamp ile uygular.
     * Pozitif delta: baseAffectionDifficulty çarpanıyla yavaşlatılır.
     * Negatif delta: tam uygulanır (düşüş hızlandırılmaz, sadece yavaşlatılmaz).
     */
    fun applyClampedDelta(
        currentScore: Int,
        rawDelta: Int,
        baseAffectionDifficulty: Double,
        emotionalRegulationCapacity: Int = 50
    ): Int {
        val regulationResult = EmotionEngine.calculateEmotionalRegulation(rawDelta, emotionalRegulationCapacity)
        val regulated = regulationResult.regulatedDelta
        val effective = if (regulated > 0) {
            (regulated * baseAffectionDifficulty).toInt()
        } else {
            regulated
        }
        return (currentScore + effective).coerceIn(0, 100)
    }

    // ─── Duygu geçmişi (EmotionHistoryEntity) ────────────────────────────────

    suspend fun saveEmotionSnapshot(entry: EmotionHistoryEntity) = withContext(Dispatchers.IO) {
        emotionHistoryDao.insertEmotionHistory(entry)
    }

    suspend fun getRecentEmotionHistory(botId: String, limit: Int = 30): List<EmotionHistoryEntity> =
        withContext(Dispatchers.IO) {
            emotionHistoryDao.getRecentHistory(botId, limit)
        }

    // ─── SensitiveTrigger ─────────────────────────────────────────────────────

    suspend fun checkAndApplySensitiveTriggers(
        botId: String,
        userText: String
    ): Double = withContext(Dispatchers.IO) {
        val triggers = sensitiveTriggerDao.getTriggersForBot(botId)
        var maxMultiplier = 1.0
        for (trigger in triggers) {
            if (userText.contains(trigger.triggerTopic, ignoreCase = true)) {
                val newCount = trigger.recentTriggerCount + 1
                sensitiveTriggerDao.updateTrigger(trigger.copy(recentTriggerCount = newCount))
                if (trigger.intensityMultiplier > maxMultiplier) {
                    maxMultiplier = trigger.intensityMultiplier
                }
                android.util.Log.d("EmotionEngineManager", "[SensitiveTrigger] '${trigger.triggerTopic}' matched, mult=${trigger.intensityMultiplier}")
            }
        }
        maxMultiplier
    }

    // ─── PendingReappraisal ───────────────────────────────────────────────────

    suspend fun resolvePendingReappraisals(botId: String, currentMessageCount: Int) =
        withContext(Dispatchers.IO) {
            val pending = pendingReappraisalDao.getPendingReappraisals(botId)
            for (r in pending) {
                when {
                    r.resolved || r.expired -> continue
                    currentMessageCount >= r.reappraisalEligibleAtMessageIndex -> {
                        pendingReappraisalDao.updateReappraisal(r.copy(resolved = true))
                        android.util.Log.d("EmotionEngineManager", "[Reappraisal] Resolved reappraisal id=${r.id} for bot $botId")
                    }
                    currentMessageCount >= r.reappraisalEligibleAtMessageIndex + 20 -> {
                        pendingReappraisalDao.updateReappraisal(r.copy(expired = true))
                        android.util.Log.d("EmotionEngineManager", "[Reappraisal] Expired reappraisal id=${r.id} for bot $botId")
                    }
                }
            }
        }

    // ─── Self-check failure log ───────────────────────────────────────────────

    suspend fun getSelfCheckFailureCount(botId: String): Int = withContext(Dispatchers.IO) {
        selfCheckFailureLogDao.getFailureCountForBot(botId)
    }

    // ─── CastMember difficulty multiplier hesaplama ───────────────────────────

    /**
     * Rol ve bağlam cümlesine göre baseAffectionDifficultyMultiplier döndürür.
     * Manuel ve otomatik eklenen karakterler aynı şemayı kullanır (3.4).
     */
    fun calculateDifficultyMultiplier(role: String, characterName: String, cueSentence: String = ""): Double {
        val r = role.lowercase()
        val cue = cueSentence.lowercase()
        return when {
            r.contains("patron") || r.contains("müdür") || r.contains("amir") -> 0.75
            r.contains("doktor") || r.contains("dr.") -> 0.85
            r.contains("öğretmen") || r.contains("hoca") || r.contains("profesör") -> 0.80
            r.contains("komutan") || r.contains("kaptan") || r.contains("general") -> 0.70
            r.contains("rakip") || r.contains("düşman") -> 0.60
            r.contains("aile") || r.contains("anne") || r.contains("baba") || r.contains("kardeş") -> 1.20
            r.contains("sevgili") || r.contains("aşk") || r.contains("romantik") -> 1.40
            r.contains("arkadaş") || r.contains("dost") -> 1.10
            r.contains("yabancı") || r.contains("stranger") -> 0.85
            cue.contains("eski") && (cue.contains("sevgili") || cue.contains("aşk")) -> 1.30
            else -> 1.05
        }
    }
}