package com.example.util

import com.example.data.local.BotEntity
import com.example.data.local.MessageEntity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Y5 — Dinamik Zorluk Ayarı
 * Kullanıcının konuşma kalıplarını analiz ederek baseAffectionDifficulty'yi
 * kademeli olarak kalibre eder. Anlık skor değiştirilmez — sadece gelecekteki
 * kazanım HIZI güncellenir.
 *
 * Her 20 mesajda bir (veya çağrıldığında) çalıştırılır.
 */
object DynamicDifficultyCalibrator {

    private const val WINDOW = 20          // kaç mesaja bak
    private const val MAX_SHIFT = 0.15f    // tek seferde max ±0.15 kayma
    private const val FLOOR = 0.5f
    private const val CEILING = 2.5f

    data class CalibrationResult(
        val newDifficulty: Float,
        val oldDifficulty: Float,
        val delta: Float,
        val reason: String
    )

    /**
     * Son [WINDOW] kullanıcı mesajını inceleyerek yeni zorluk çarpanı hesaplar.
     * Özellik kapalıysa [bot.baseAffectionDifficulty] olduğu gibi döner.
     */
    fun calibrate(bot: BotEntity, recentMessages: List<MessageEntity>): CalibrationResult {
        val old = bot.baseAffectionDifficulty.toFloat()

        if (!AppFeatureFlags.isDynamicDifficultyEnabled) {
            return CalibrationResult(old, old, 0f, "dinamik zorluk kapalı")
        }

        val userMsgs = recentMessages
            .filter { it.role == "user" }
            .takeLast(WINDOW)

        if (userMsgs.size < 5) {
            return CalibrationResult(old, old, 0f, "yeterli mesaj yok (${userMsgs.size})")
        }

        val scores = userMsgs.map { scoreMessage(it.text) }
        val avgScore = scores.average().toFloat()   // -1.0 (agresif) .. +1.0 (pozitif)

        // Pozitif kullanıcı → daha kolay kazan (zorluk azalır)
        // Agresif/negatif kullanıcı → daha zor (zorluk artar)
        val targetShift = -avgScore * 0.3f          // ters ilişki: pozitif → daha düşük multiplier
        val clampedShift = targetShift.coerceIn(-MAX_SHIFT, MAX_SHIFT)

        val newRaw = old + clampedShift
        val newDiff = newRaw.coerceIn(FLOOR, CEILING)
        val actualDelta = newDiff - old

        val reason = buildString {
            append("avg_sentiment=%.2f".format(avgScore))
            append(", shift=%.3f".format(actualDelta))
            if (abs(actualDelta) < 0.001f) append(" (değişim yok)")
        }

        return CalibrationResult(newDiff, old, actualDelta, reason)
    }

    /** Mesajı -1 (çok negatif) .. +1 (çok pozitif) arası puanlar. */
    private fun scoreMessage(text: String): Float {
        val lower = text.lowercase()
        var score = 0f

        // Pozitif sinyaller
        val positiveKw = listOf("seviyorum", "teşekkür", "harika", "güzel", "süper",
            "mükemmel", "tatlı", "özledim", "mutlu", "sevindim", "love", "thank",
            "great", "wonderful", "miss you", "happy")
        // Negatif sinyaller
        val negativeKw = listOf("siktir", "lanet", "nefret", "berbat", "rezil",
            "aptal", "salak", "ahmak", "saçma", "bıktım", "hate", "stupid",
            "idiot", "trash", "terrible", "awful", "shut up")
        // Agresif kalıplar
        val aggressiveKw = listOf("!!", "???", "!!!", "hadi be", "ne biçim",
            "nasıl böyle", "olmaz", "yeter artık")

        for (kw in positiveKw)  if (lower.contains(kw)) score += 0.2f
        for (kw in negativeKw)  if (lower.contains(kw)) score -= 0.3f
        for (kw in aggressiveKw) if (lower.contains(kw)) score -= 0.15f

        // Kısa + büyük harf → agresiflik sinyali
        if (text.length < 15 && text == text.uppercase() && text.any { it.isLetter() }) {
            score -= 0.1f
        }

        return score.coerceIn(-1f, 1f)
    }

    /** Her 20 mesajda bir tetiklenmesi gerekip gerekmediğini kontrol eder. */
    fun shouldRun(totalMessageCount: Int): Boolean =
        AppFeatureFlags.isDynamicDifficultyEnabled && totalMessageCount > 0 && totalMessageCount % 20 == 0
}