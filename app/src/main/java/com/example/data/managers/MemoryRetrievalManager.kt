package com.example.data.managers

import com.example.data.api.GeminiContent
import com.example.data.api.GeminiEmbedContentRequest
import com.example.data.api.GeminiPart
import com.example.data.api.RetrofitClient
import com.example.data.local.BotEntity
import com.example.data.local.MemoryCheckpointDao
import com.example.data.local.MemoryEventDao
import com.example.data.local.MemoryEventEntity
import com.example.data.local.MemoryFactDao
import com.example.data.local.MemoryFactEntity
import com.example.data.local.MemoryFragmentDao
import com.example.data.local.MemoryFragmentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.math.sqrt

class MemoryRetrievalManager(
    private val fragmentDao: MemoryFragmentDao,
    private val memoryFactDao: MemoryFactDao,
    private val memoryEventDao: MemoryEventDao,
    private val memoryCheckpointDao: MemoryCheckpointDao
) {
    fun floatArrayToJson(arr: FloatArray): String {
        val jsonArray = JSONArray()
        for (v in arr) {
            jsonArray.put(v.toDouble())
        }
        return jsonArray.toString()
    }

    fun jsonToFloatArray(str: String): FloatArray {
        if (str.isBlank() || str == "[]") return FloatArray(0)
        return try {
            val arr = JSONArray(str)
            FloatArray(arr.length()) { idx -> arr.optDouble(idx, 0.0).toFloat() }
        } catch (_: Exception) {
            FloatArray(0)
        }
    }

    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty() || v1.size != v2.size) return 0f
        var dot = 0f
        var norm1 = 0f
        var norm2 = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            norm1 += v1[i] * v1[i]
            norm2 += v2[i] * v2[i]
        }
        val denom = sqrt(norm1) * sqrt(norm2)
        return if (denom > 1e-6f) (dot / denom).coerceIn(-1f, 1f) else 0f
    }

    suspend fun computeEmbedding(text: String, apiKey: String? = null): FloatArray = withContext(Dispatchers.IO) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return@withContext computeLocalDeterministicEmbedding("")

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val req = GeminiEmbedContentRequest(
                    model = "models/text-embedding-004",
                    content = GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = cleanText.take(1500)))
                    )
                )
                val resp = RetrofitClient.service.embedContent(
                    apiKey = apiKey,
                    request = req
                )
                val values = resp.embedding?.values
                if (values != null && values.isNotEmpty()) {
                    return@withContext values.toFloatArray()
                }
            } catch (_: Exception) {
                // Fall back to deterministic local trigram embedding when offline or key unavailable
            }
        }
        computeLocalDeterministicEmbedding(cleanText)
    }

    private fun computeLocalDeterministicEmbedding(text: String, dims: Int = 64): FloatArray {
        val vec = FloatArray(dims)
        if (text.isBlank()) return vec
        val normalized = text.lowercase()
        val tokens = normalized.split(Regex("""\s+""")).filter { it.isNotBlank() }
        for (token in tokens) {
            val h = (token.hashCode() and Int.MAX_VALUE) % dims
            vec[h] += 1.0f
            for (i in 0 until (token.length - 2).coerceAtLeast(0)) {
                val tri = token.substring(i, i + 3)
                val th = (tri.hashCode() and Int.MAX_VALUE) % dims
                vec[th] += 0.5f
            }
        }
        var norm = 0f
        for (v in vec) norm += v * v
        val scale = sqrt(norm)
        if (scale > 1e-6f) {
            for (i in vec.indices) vec[i] /= scale
        }
        return vec
    }

    suspend fun getRelevantMemoryFragments(
        bot: BotEntity,
        queryText: String,
        apiKey: String? = null,
        limit: Int = 6
    ): List<MemoryFragmentEntity> = withContext(Dispatchers.IO) {
        val recent = fragmentDao.getRecentFragments(bot.id, 30)
        if (recent.isEmpty()) return@withContext emptyList()
        if (queryText.isBlank()) return@withContext recent.take(limit)

        val queryWords = queryText.lowercase()
            .split(Regex("""[^\p{L}\p{N}]+"""))
            .filter { it.length >= 3 }
            .toSet()

        val queryVec = computeLocalDeterministicEmbedding(queryText)

        recent.map { frag ->
            val fragVec = computeLocalDeterministicEmbedding(frag.content)
            val sim = cosineSimilarity(queryVec, fragVec)
            val lowerContent = frag.content.lowercase()
            val keywordHits = queryWords.count { lowerContent.contains(it) }
            val score = sim + (keywordHits * 0.18f)
            Pair(frag, score)
        }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    fun getActiveFactsFlow(botId: String): Flow<List<MemoryFactEntity>> =
        memoryFactDao.getActiveFactsFlow(botId)

    fun getActiveEventsFlow(botId: String): Flow<List<MemoryEventEntity>> =
        memoryEventDao.getActiveEventsFlow(botId)

    suspend fun deleteMemoryFact(id: Long) = withContext(Dispatchers.IO) {
        memoryFactDao.deleteFact(id)
    }

    suspend fun deleteMemoryEvent(id: Long) = withContext(Dispatchers.IO) {
        memoryEventDao.deleteEvent(id)
    }

    suspend fun updateMemoryFact(fact: MemoryFactEntity) = withContext(Dispatchers.IO) {
        memoryFactDao.updateFact(fact.copy(userCorrected = true, lastConfirmedAt = System.currentTimeMillis()))
    }

    suspend fun updateMemoryEvent(event: MemoryEventEntity) = withContext(Dispatchers.IO) {
        memoryEventDao.updateEvent(event)
    }

    suspend fun deleteAllAutoMemories(botId: String) = withContext(Dispatchers.IO) {
        memoryFactDao.deleteAutoFacts(botId)
        memoryEventDao.deleteAllEventsForBot(botId)
    }
}
