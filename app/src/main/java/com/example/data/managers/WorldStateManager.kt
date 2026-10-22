package com.example.data.managers

import com.example.data.local.BotDao
import com.example.data.local.MemoryEventDao
import com.example.data.local.RoutineDao
import com.example.data.local.TimePerceptionMismatchLogDao
import com.example.data.repository.TimePerceptionEngine
import com.example.data.local.RoutineEntity
import com.example.data.local.TimePerceptionMismatchLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Bölüm 6 — WorldStateManager
 *
 * Sorumlu olduğu alanlar:
 *   - Hikaye takvimi (storyCalendarDate) ilerletme ve geri sarma
 *   - Karakter yaşı hesaplama (currentAge)
 *   - Mevsim hesaplama (getSeasonFromDate)
 *   - formatStoryDuration — merkezi süre formatlama (4.3)
 *   - story_time_skip parse güvenilirliği (4.5)
 *   - Rutin tetikleme (RoutineEntity) ve lastMentionedStoryDate güncelleme
 *   - Mesajlar arası zaman çelişkisi tespiti (4.4)
 *   - Rewind sonrası tam state restore — storyCalendarDate + currentAge + mevsim (4.2)
 *
 * EmochiRepository bu manager'ı constructor'da başlatır ve kendi fonksiyonlarını
 * buradaki implementasyona delege eder. Böylece EmochiRepository orkestrasyon
 * katmanı olarak kalır, business logic burada yaşar.
 */
class WorldStateManager(
    private val botDao: BotDao,
    private val memoryEventDao: MemoryEventDao,
    private val routineDao: RoutineDao,
    private val timePerceptionMismatchLogDao: TimePerceptionMismatchLogDao
) {

    // ─── Takvim & Yaş ────────────────────────────────────────────────────────

    /**
     * Hikaye takvimini `elapsedDaysToAdd` gün ilerletir, karakter yaşını günceller.
     * elapsedDaysToAdd=0 ise sadece mevcut tarihe karşılık gelen yaşı hesaplar (rewind için).
     */
    fun advanceCalendarDate(
        currentCalendarDateStr: String,
        currentDayCounter: Long,
        elapsedDaysToAdd: Int,
        birthDateStr: String,
        initialAge: Int
    ): Triple<String, Long, Int> =
        TimePerceptionEngine.advanceCalendarDateAndCheckAge(
            currentCalendarDateStr = currentCalendarDateStr,
            currentDayCounter = currentDayCounter,
            elapsedDaysToAdd = elapsedDaysToAdd,
            birthDateStr = birthDateStr,
            initialAge = initialAge
        )

    /**
     * B4.3 — Merkezi süre formatlama.
     * 0-1g → "bugün/dün", 2-6g → "birkaç gün", 7-29g → "X hafta Y gün",
     * 30-364g → "X ay [Y gün]" (gerçek takvim ayı), 365+ → "X yıl [Y ay]"
     */
    fun formatStoryDuration(days: Long, fromDateStr: String = ""): String =
        TimePerceptionEngine.formatStoryDuration(days, fromDateStr)

    /**
     * B4.6 — Verilen tarihten mevsim döndürür.
     * "2026-08-15" → "yaz", "2026-01-05" → "kış" vb.
     */
    fun getSeasonFromDate(dateStr: String): String =
        TimePerceptionEngine.getSeasonFromDate(dateStr)

    fun getDayOfWeekTurkish(dateStr: String): String =
        TimePerceptionEngine.getDayOfWeekTurkish(dateStr)

    // ─── Rutin Takibi ─────────────────────────────────────────────────────────

    suspend fun getActiveRoutineReminder(botId: String, storyCalendarDateStr: String): RoutineEntity? =
        withContext(Dispatchers.IO) {
            val currentDay = getDayOfWeekTurkish(storyCalendarDateStr)
            val routines = routineDao.getRoutinesForBot(botId)
            routines.firstOrNull { routine ->
                when (routine.frequency.lowercase()) {
                    "daily" -> routine.lastMentionedStoryDate != storyCalendarDateStr
                    "weekly" -> currentDay.isNotBlank() &&
                            routine.dayOfWeek?.equals(currentDay, ignoreCase = true) == true &&
                            routine.lastMentionedStoryDate != storyCalendarDateStr
                    else -> routine.lastMentionedStoryDate != storyCalendarDateStr
                }
            }
        }

    suspend fun markRoutineMentioned(routineId: Long, storyCalendarDateStr: String, botId: String = "") {
        withContext(Dispatchers.IO) {
            val candidateBots = if (botId.isNotBlank()) listOf(botId) else botDao.getAllBotsList().map { it.id }
            for (bId in candidateBots) {
                val target = routineDao.getRoutinesForBot(bId).firstOrNull { it.id == routineId }
                if (target != null) {
                    routineDao.updateRoutine(target.copy(lastMentionedStoryDate = storyCalendarDateStr))
                    return@withContext
                }
            }
        }
    }

    // ─── B4.4: Mesajlar arası zaman çelişkisi ─────────────────────────────────

    suspend fun detectInterMessageTimeConflict(
        botId: String,
        recentAiMessages: List<String>,
        currentProvider: String,
        rawOutput: String
    ) {
        if (recentAiMessages.size < 2) return
        withContext(Dispatchers.IO) {
            val timeProgression = listOf(
                listOf("sabah", "öğlen", "akşam", "gece"),
                listOf("gece", "sabah"),
                listOf("akşam", "sabah"),
                listOf("öğlen", "sabah"),
                listOf("gece yarısı", "sabah")
            )
            val lastTwo = recentAiMessages.takeLast(2)
            val prev = lastTwo[0].lowercase()
            val curr = lastTwo[1].lowercase()
            for (regression in timeProgression) {
                val prevIdx = regression.indexOfFirst { prev.contains(it) }
                val currIdx = regression.indexOfFirst { curr.contains(it) }
                if (prevIdx > currIdx && currIdx >= 0) {
                    val log = TimePerceptionMismatchLogEntity(
                        botId = botId,
                        userMessageText = "[inter-message check]",
                        modelResponseText = rawOutput.take(300),
                        detectedTimeExpression = "${regression[prevIdx]}→${regression[currIdx]}",
                        codeCalculatedDays = 0,
                        mismatchReason = "Mesajlar arası zaman geriye gitti (${regression[prevIdx]} → ${regression[currIdx]}) geçiş anlatılmadan",
                        timestamp = System.currentTimeMillis()
                    )
                    try { timePerceptionMismatchLogDao.insertLog(log) } catch (_: Exception) {}
                    android.util.Log.w("WorldStateManager", "[B4.4] Inter-message time conflict: ${log.mismatchReason}")
                    break
                }
            }
        }
    }

    // ─── B4.5: story_time_skip parse başarısızlığı logu ─────────────────────

    suspend fun logTimeSkipParseFailure(
        botId: String,
        provider: String,
        rawOutput: String,
        reason: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                timePerceptionMismatchLogDao.insertLog(
                    TimePerceptionMismatchLogEntity(
                        botId = botId,
                        userMessageText = "[time_skip_parse_failure]",
                        modelResponseText = rawOutput.take(400),
                        detectedTimeExpression = "story_time_skip",
                        codeCalculatedDays = 0,
                        mismatchReason = "[$provider] story_time_skip parse başarısız: $reason",
                        timestamp = System.currentTimeMillis()
                    )
                )
                android.util.Log.w("WorldStateManager", "[B4.5] time_skip parse failure logged: provider=$provider reason=$reason")
            } catch (_: Exception) {}
        }
    }
}