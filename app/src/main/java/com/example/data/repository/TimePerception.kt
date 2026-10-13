package com.example.data.repository

import com.example.data.local.BotEntity
import com.example.data.local.TimePerceptionMismatchLogEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Enhanced Time Perception Engine (Zaman Algısı Sistemi).
 * Manages physical time intervals, time-of-day / weekday context,
 * story jumps (Turkish NLP), calendar date calculations, and mismatch resolution.
 */
object TimePerceptionEngine {

    enum class TimeOfDay(val label: String, val startHour: Int, val endHour: Int) {
        NIGHT_SLEEP("Gece / Uyku Vakti", 23, 6),
        MORNING("Sabah", 6, 11),
        AFTERNOON("Öğle / Gün Ortası", 11, 17),
        EVENING("Akşam", 17, 23);

        companion object {
            fun fromHour(hour: Int): TimeOfDay {
                return when (hour) {
                    in 6..10 -> MORNING
                    in 11..16 -> AFTERNOON
                    in 17..22 -> EVENING
                    else -> NIGHT_SLEEP
                }
            }
        }
    }

    enum class DayType(val label: String) {
        WEEKDAY("Hafta İçi"),
        WEEKEND("Hafta Sonu");

        companion object {
            fun fromCalendar(cal: Calendar): DayType {
                val day = cal.get(Calendar.DAY_OF_WEEK)
                return if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) WEEKEND else WEEKDAY
            }
        }
    }

    data class TimeContext(
        val timeOfDay: TimeOfDay,
        val dayType: DayType,
        val hour: Int,
        val isOvernightGap: Boolean
    )

    data class TimePerceptionInfo(
        val formattedTimeString: String,
        val exactFormattedTimeString: String,
        val timeGapSignificant: Boolean,
        val rapidMessagingFlag: Boolean,
        val elapsedMs: Long,
        val categoryLabel: String,
        val guidelineInstruction: String,
        val storyCalendarDate: String,
        val storyDayCounter: Long = 1L,
        val currentAge: Int = 20,
        val shouldInjectIntoPrompt: Boolean = true,
        val timeContext: TimeContext? = null
    )

    fun getDeviceCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date())
    }

    fun formatExactTimePassed(lastMs: Long, nowMs: Long): String {
        if (lastMs <= 0L) return "Sohbet ilk kez başlatılıyor."
        val diffMs = (nowMs - lastMs).coerceAtLeast(0L)
        val totalSecs = diffMs / 1000
        val minutes = (totalSecs / 60) % 60
        val hours = (totalSecs / 3600) % 24
        val days = (totalSecs / (3600 * 24))
        val weeks = days / 7
        val remDays = days % 7

        val timeParts = mutableListOf<String>()
        if (weeks > 0) timeParts.add("$weeks hafta")
        if (remDays > 0) timeParts.add("$remDays gün")
        if (hours > 0) timeParts.add("$hours saat")
        if (minutes > 0 || timeParts.isEmpty()) timeParts.add("$minutes dakika")

        val durationStr = timeParts.joinToString(" ")

        val sdf = SimpleDateFormat("d MMMM EEEE, 'saat' HH:mm", Locale("tr", "TR"))
        sdf.timeZone = TimeZone.getDefault()
        val lastDateStr = try { sdf.format(Date(lastMs)) } catch (_: Exception) { "" }

        return "Son mesajdan bu yana $durationStr geçti ($lastDateStr)."
    }

    data class StoryTimeJumpResult(
        val daysToAdvance: Int = 0,
        val newSceneTime: String? = null // e.g. "sabah", "öğle", "akşam", "gece"
    )

    data class StoryTimeInfo(
        val storyCalendarDate: String,
        val storyDayCounter: Long = 1L,
        val currentAge: Int = 20,
        val sceneTime: String = "",
        val promptBlock: String = ""
    )

    fun parseTurkishNumberWords(text: String): Int? {
        val numMap = mapOf(
            "bir" to 1, "iki" to 2, "üç" to 3, "uc" to 3,
            "dört" to 4, "dort" to 4, "beş" to 5, "bes" to 5,
            "altı" to 6, "alti" to 6, "yedi" to 7, "sekiz" to 8,
            "dokuz" to 9, "on" to 10, "on bir" to 11, "on iki" to 12,
            "yirmi" to 20, "otuz" to 30, "kırk" to 40, "elli" to 50,
            "birkaç" to 3, "birkac" to 3
        )
        val lower = text.lowercase().trim()
        val digitMatch = Regex("""\b(\d+)\b""").find(lower)?.value?.toIntOrNull()
        if (digitMatch != null) return digitMatch

        for ((word, num) in numMap) {
            val wordRegex = Regex("""\b${Regex.escape(word)}\b""")
            if (wordRegex.containsMatchIn(lower)) return num
        }
        return null
    }

    /**
     * Robust story jump parsing:
     * - Word boundary safe (e.g. \bay\b instead of contains("ay"))
     * - Recognizes intra-day jumps ("yarım saat", "birkaç saat", "akşam oldu") -> 0 days + scene_time
     * - Recognizes full day jumps ("ertesi gün", "ertesi sabah", "3 gün", "bir hafta", "iki ay")
     */
    fun parseStoryTimeJump(text: String): StoryTimeJumpResult {
        val lower = text.lowercase().trim()
        if (lower.isBlank()) return StoryTimeJumpResult()

        // 1. Intra-day time changes (days = 0, scene_time updated)
        if (Regex("""\b(öğle|öğlen|oglen|öğle vakti)\b""").containsMatchIn(lower)) {
            if (!lower.contains("gün") && !lower.contains("hafta") && !lower.contains("ay")) {
                return StoryTimeJumpResult(daysToAdvance = 0, newSceneTime = "öğle")
            }
        }
        if (Regex("""\b(akşamüstü|akşama doğru|akşam oldu|aksam oldu|bu akşam|gece oldu|gece yarısı)\b""").containsMatchIn(lower)) {
            if (!lower.contains("gün") && !lower.contains("hafta") && !lower.contains("ay")) {
                val scTime = if (lower.contains("gece")) "gece" else "akşam"
                return StoryTimeJumpResult(daysToAdvance = 0, newSceneTime = scTime)
            }
        }
        if (Regex("""\b(birkaç saat|bir saat|iki saat|üç saat|yarım saat|saatler)\s*(?:sonra|geçti|gecti)\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 0, newSceneTime = null)
        }

        // 2. Next morning / Next day (1 day + specific scene_time)
        if (Regex("""\bertesi\s+sabah\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 1, newSceneTime = "sabah")
        }
        if (Regex("""\b(ertesi\s+gün|ertesi\s+gun|sonraki\s+gün|sonraki\s+gun|yarın|yarin)\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 1, newSceneTime = "sabah")
        }
        if (Regex("""\b(birkaç\s+gün|birkac\s+gun)\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 3, newSceneTime = null)
        }

        // 3. Month jumps (word boundary check on \bay\b)
        val monthRegex = Regex("""([a-zçğıöşü\d\s]+)\s+\bay\b\s*(?:sonra|geçti|gecti)?""")
        val monthMatch = monthRegex.find(lower)
        if (monthMatch != null) {
            val amountStr = monthMatch.groupValues[1]
            val months = parseTurkishNumberWords(amountStr) ?: 1
            return StoryTimeJumpResult(daysToAdvance = months * 30, newSceneTime = null)
        }

        // 4. Week jumps
        val weekRegex = Regex("""([a-zçğıöşü\d\s]+)\s+\bhafta\b\s*(?:sonra|geçti|gecti)?""")
        val weekMatch = weekRegex.find(lower)
        if (weekMatch != null) {
            val amountStr = weekMatch.groupValues[1]
            val weeks = parseTurkishNumberWords(amountStr) ?: 1
            return StoryTimeJumpResult(daysToAdvance = weeks * 7, newSceneTime = null)
        }

        // 5. Day jumps
        val dayRegex = Regex("""([a-zçğıöşü\d\s]+)\s+\bgün\b\s*(?:sonra|geçti|gecti)?""")
        val dayMatch = dayRegex.find(lower)
        if (dayMatch != null) {
            val amountStr = dayMatch.groupValues[1]
            val days = parseTurkishNumberWords(amountStr) ?: 1
            return StoryTimeJumpResult(daysToAdvance = days, newSceneTime = null)
        }

        return StoryTimeJumpResult()
    }

    fun parseStoryTimeJumpDays(text: String): Int {
        return parseStoryTimeJump(text).daysToAdvance
    }

    fun parseDaysToSkip(amountStr: String): Int {
        val jump = parseStoryTimeJump(amountStr)
        if (jump.daysToAdvance > 0) return jump.daysToAdvance
        val lower = amountStr.lowercase().trim()
        val num = Regex("""\b\d+\b""").find(lower)?.value?.toIntOrNull()
        return num ?: 0
    }

    fun advanceCalendarDateAndCheckAge(
        currentCalendarDateStr: String,
        currentDayCounter: Long,
        elapsedDaysToAdd: Int,
        birthDateStr: String,
        initialAge: Int
    ): Triple<String, Long, Int> {
        val newDayCounter = currentDayCounter + elapsedDaysToAdd.coerceAtLeast(0)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        val currentDate = try {
            if (currentCalendarDateStr.isBlank()) null else sdf.parse(currentCalendarDateStr)
        } catch (_: Exception) {
            null
        }

        val newCalendarDateStr = if (currentDate != null) {
            val cal = Calendar.getInstance()
            cal.time = currentDate
            if (elapsedDaysToAdd > 0) {
                cal.add(Calendar.DAY_OF_YEAR, elapsedDaysToAdd)
            }
            sdf.format(cal.time)
        } else ""

        var newAge = initialAge
        if (birthDateStr.isNotBlank() && currentDate != null) {
            try {
                val birthDate = sdf.parse(birthDateStr)
                if (birthDate != null) {
                    val cal = Calendar.getInstance().apply { time = currentDate }
                    if (elapsedDaysToAdd > 0) cal.add(Calendar.DAY_OF_YEAR, elapsedDaysToAdd)
                    val birthCal = Calendar.getInstance().apply { time = birthDate }

                    var age = cal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
                    if (cal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    if (age > 0) newAge = age
                }
            } catch (_: Exception) {}
        } else {
            val extraYears = ((newDayCounter - 1) / 365).toInt()
            newAge = initialAge + extraYears
        }

        return Triple(newCalendarDateStr, newDayCounter, newAge)
    }

    /**
     * Builds single-line compact story time block for LLM prompt.
     * Replaces old 3-block real-time system.
     */
    fun buildStoryTimePromptBlock(
        storyDayCounter: Long,
        storyCalendarDate: String,
        currentAge: Int,
        sceneTime: String = ""
    ): String {
        val parts = mutableListOf<String>()
        parts.add("Gün #$storyDayCounter")
        if (storyCalendarDate.isNotBlank()) {
            parts.add("Tarih: $storyCalendarDate")
        }
        if (currentAge > 0) {
            parts.add("Yaş: $currentAge")
        }
        if (sceneTime.isNotBlank()) {
            parts.add("Sahne: $sceneTime")
        }

        val singleLine = parts.joinToString(" | ")
        val moodHint = when (sceneTime.lowercase()) {
            "sabah" -> " (sabah: sakin/mahmur ton)"
            "öğle" -> " (öğle: canlı/odaklı ton)"
            "akşam" -> " (akşam: dinlenmiş/yumuşak ton)"
            "gece" -> " (gece: uykulu/samimi ton)"
            else -> ""
        }

        return "\n## HİKÂYE ZAMANI: $singleLine$moodHint\n- KURAL: Gerçek dünya saati ve tarihi yoktur. Zaman yalnızca hikâyedeki zaman atlamalarıyla ilerler. Kullanıcı ara verse de sohbet kesintisiz devam eder.\n"
    }
}
