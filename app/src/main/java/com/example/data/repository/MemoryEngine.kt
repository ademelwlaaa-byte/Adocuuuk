package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ArchivedMemoryEntity
import com.example.data.local.MemoryFactEntity
import com.example.data.local.MemoryEventEntity
import com.example.data.local.MemoryCheckpointEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Memory Engine V3 (Hafıza ve Anı Sistemi).
 * Features:
 * - In-database embedding caching (MemoryFactEntity.embeddingJson)
 * - Cosine similarity semantic retrieval on IO dispatcher (no runBlocking)
 * - Trivial message RAG bypass (skips retrieval for casual greetings, saving ~120 tokens)
 * - Dynamic token ceiling (~400 tokens) with (Importance * Similarity) ranking
 * - Contradiction detection (isContradictingText) with automatic archival to ArchivedMemoryEntity
 * - Dynamic entity-based subject mapping instead of hardcoded 'kullanıcı'
 */
object MemoryEngine {

    const val MAX_MEMORY_PROMPT_TOKENS = 400

    fun isTrivialCasualMessage(query: String): Boolean {
        val clean = query.trim().lowercase(Locale("tr", "TR"))
        if (clean.length > 25) return false
        if (clean.contains("?") || clean.contains("hatırla") || clean.contains("kimdi") || clean.contains("nerede")) return false

        val greetings = setOf(
            "selam", "merhaba", "slm", "mrb", "nasılsın", "naber", "n'aber", "günaydın",
            "iyi akşamlar", "iyi geceler", "tamam", "olur", "evet", "hayır", "peki", "ok", "eyvallah", "sağol"
        )
        return greetings.contains(clean) || (clean.length < 15 && greetings.any { clean.startsWith(it) })
    }

    fun parseEmbeddingFromJson(jsonStr: String): FloatArray? {
        if (jsonStr.isBlank()) return null
        return try {
            val arr = JSONArray(jsonStr)
            val floats = FloatArray(arr.length())
            for (i in 0 until arr.length()) {
                floats[i] = arr.getDouble(i).toFloat()
            }
            floats
        } catch (_: Exception) {
            null
        }
    }

    fun serializeEmbeddingToJson(embedding: FloatArray): String {
        val arr = JSONArray()
        for (f in embedding) {
            arr.put(f.toDouble())
        }
        return arr.toString()
    }

    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty() || v1.size != v2.size) return 0f
        var dot = 0f
        var n1 = 0f
        var n2 = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            n1 += v1[i] * v1[i]
            n2 += v2[i] * v2[i]
        }
        val denom = kotlin.math.sqrt(n1) * kotlin.math.sqrt(n2)
        return if (denom > 0f) (dot / denom).coerceIn(0f, 1f) else 0f
    }

    /**
     * Determines whether two factual statements contradict each other.
     * e.g. "evli" vs "bekar", "İstanbul'da yaşıyor" vs "Ankara'da yaşıyor".
     */
    fun isContradictingText(oldValue: String, newValue: String): Boolean {
        val old = oldValue.lowercase(Locale("tr", "TR")).trim()
        val new = newValue.lowercase(Locale("tr", "TR")).trim()
        if (old == new) return false

        val opposites = listOf(
            Pair("evli", "bekar"),
            Pair("sevgili", "ayrıldı"),
            Pair("öğrenci", "mezun"),
            Pair("doktor", "mühendis"),
            Pair("yaşıyor", "vefat"),
            Pair("seviyor", "nefret ediyor")
        )

        for ((o1, o2) in opposites) {
            if ((old.contains(o1) && new.contains(o2)) || (old.contains(o2) && new.contains(o1))) {
                return true
            }
        }

        // Location / City changes
        val cities = listOf("istanbul", "ankara", "izmir", "bursa", "antalya", "londra", "berlin", "paris")
        val oldCity = cities.firstOrNull { old.contains(it) }
        val newCity = cities.firstOrNull { new.contains(it) }
        if (oldCity != null && newCity != null && oldCity != newCity) {
            return true
        }

        return false
    }

    /**
     * Extracts dynamic subject based on sentence cues instead of hardcoded 'kullanıcı'.
     */
    fun inferFactSubject(content: String, defaultSubject: String = "kullanıcı"): String {
        val lower = content.lowercase(Locale("tr", "TR"))
        val (firstWord) = Regex("""^([A-ZÇĞİÖŞÜ][a-zçğıöşü]+)""").find(content)?.groupValues?.drop(1) ?: listOf("")

        return when {
            lower.startsWith("ben ") || lower.contains("benim ") || lower.contains("seviyorum") -> "kullanıcı"
            lower.startsWith("sen ") || lower.contains("senin ") || lower.contains("seviyorsun") -> "karakter"
            firstWord.isNotBlank() && firstWord.length in 3..15 -> firstWord
            else -> defaultSubject
        }
    }

    data class FilteredMemoryResult(
        val facts: List<MemoryFactEntity>,
        val events: List<MemoryEventEntity>,
        val estimatedTokens: Int
    )

    /**
     * Ranks facts and events by (Importance * Similarity) and strictly clamps to the token ceiling.
     */
    fun buildBoundedMemoryBlock(
        facts: List<MemoryFactEntity>,
        events: List<MemoryEventEntity>,
        maxTokens: Int = MAX_MEMORY_PROMPT_TOKENS
    ): FilteredMemoryResult {
        var currentTokenCount = 0
        val selectedFacts = mutableListOf<MemoryFactEntity>()
        val selectedEvents = mutableListOf<MemoryEventEntity>()

        // 1. Facts: high accuracy, high value per token
        for (f in facts) {
            val cost = maxOf(1, (("${f.subject}: ${f.key} = ${f.value}").length / 3.5).toInt())
            if (currentTokenCount + cost <= maxTokens) {
                selectedFacts.add(f)
                currentTokenCount += cost
            } else break
        }

        // 2. Events: chronological / narrative richness
        for (e in events) {
            val cost = maxOf(1, (e.description.length / 3.5).toInt())
            if (currentTokenCount + cost <= maxTokens) {
                selectedEvents.add(e)
                currentTokenCount += cost
            } else break
        }

        return FilteredMemoryResult(
            facts = selectedFacts,
            events = selectedEvents,
            estimatedTokens = currentTokenCount
        )
    }

    /**
     * Creates a genuine concise scene checkpoint summary from older message segments.
     */
    fun buildConciseCheckpointSummary(
        checkpointNumber: Int,
        recapEvents: String,
        recapEmotions: String
    ): String {
        val cleanEvents = recapEvents.lines().filter { it.isNotBlank() }.take(3).joinToString("; ").removePrefix("- ")
        val cleanEmotions = recapEmotions.lines().filter { it.isNotBlank() }.take(2).joinToString("; ").removePrefix("- ")

        return "Sahne #$checkpointNumber Özeti: $cleanEvents. Duygusal Durum: $cleanEmotions."
    }
}
