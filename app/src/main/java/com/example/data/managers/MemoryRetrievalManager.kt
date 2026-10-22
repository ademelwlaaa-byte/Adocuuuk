package com.example.data.managers

import com.example.data.local.ArchivedMemoryDao
import com.example.data.local.EntityRelationDao
import com.example.data.local.ImplicitCorrectionLogDao
import com.example.data.local.MemoryCheckpointDao
import com.example.data.local.MemoryEventDao
import com.example.data.local.MemoryFactDao
import com.example.data.local.MemoryFragmentDao
import com.example.data.local.MemoryFragmentEntity
import com.example.data.local.ArchivedMemoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Bölüm 6 — MemoryRetrievalManager
 *
 * Sorumlu olduğu alanlar:
 *   - Hibrit arama: semanticSimilarity*w1 + keywordScore*w2 + importance*w3 + recency*w4
 *   - Ağırlık config'i: MemoryRankingWeights.activeConfig (hardcode değil)
 *   - Özel isim/tarih sorgularında keywordScore otomatik artırma (2.2)
 *   - Near-duplicate tespiti (similarity >= 0.92 → merge, 2.6)
 *   - Checkpoint özetleme: son 2-3 tam, eskiler embedding-only (2.5)
 *   - retrievalStageUsed (vector_only / query_rewrite / full_rerank) takibi
 *   - İki aşamalı retrieval ucuzluğu: çoğunluk vector_only'de kalsın (2.5)
 *   - Örtük öğrenme: düzeltme → confidence düşme → arşivleme (2.3)
 *   - Graf gezinmesi: kısmi zincir desteği (2.1)
 *   - B4.6: mevsim bazlı event filtresi
 *
 * NOT: Bu manager DB read/write işlemlerini, skorlama ve near-dup mantığını yönetir.
 * LLM çağrısı içeren checkpoint özetleme ve query-rewrite EmochiRepository'de kalır,
 * bu manager'dan coordinated şekilde tetiklenir.
 */
class MemoryRetrievalManager(
    private val fragmentDao: MemoryFragmentDao,
    private val memoryEventDao: MemoryEventDao,
    private val memoryFactDao: MemoryFactDao,
    private val memoryCheckpointDao: MemoryCheckpointDao,
    private val entityRelationDao: EntityRelationDao,
    private val archivedMemoryDao: ArchivedMemoryDao,
    private val implicitCorrectionLogDao: ImplicitCorrectionLogDao
) {

    // ─── Ağırlık Konfigürasyonu (2.2) ────────────────────────────────────────

    data class RankingWeights(
        val semanticWeight: Float = 0.45f,
        val keywordWeight: Float = 0.25f,
        val importanceWeight: Float = 0.20f,
        val recencyWeight: Float = 0.10f
    ) {
        companion object {
            @Volatile
            var activeConfig = RankingWeights()

            /** B2.2: Özel isim veya tarih içeren sorgularda keyword ağırlığı otomatik artırılır */
            fun forQuery(query: String): RankingWeights {
                val hasProperNameOrDate = query.any { it.isUpperCase() } ||
                    Regex("""\d{4}|\d{1,2}[./]\d{1,2}""").containsMatchIn(query) ||
                    Regex("""[A-ZÇĞİÖŞÜ][a-zçğıöşü]{2,}""").containsMatchIn(query)
                return if (hasProperNameOrDate) {
                    activeConfig.copy(keywordWeight = 0.40f, semanticWeight = 0.30f)
                } else {
                    activeConfig
                }
            }
        }
    }

    // ─── B4.6: Mevsim Bazlı Filtreleme ───────────────────────────────────────

    suspend fun getMemoryEventsBySeason(botId: String, season: String): List<com.example.data.local.MemoryEventEntity> =
        withContext(Dispatchers.IO) {
            val allEvents = memoryEventDao.getActiveEventsForBot(botId)
            allEvents.filter { event ->
                if (event.storyCalendarDateStr.isBlank()) return@filter false
                val eventSeason = getSeasonFromDate(event.storyCalendarDateStr)
                eventSeason.equals(season, ignoreCase = true)
            }
        }

    private fun getSeasonFromDate(dateStr: String): String {
        return try {
            val parts = dateStr.split("-")
            val month = parts[1].toInt()
            when (month) {
                3, 4, 5 -> "ilkbahar"
                6, 7, 8 -> "yaz"
                9, 10, 11 -> "sonbahar"
                12, 1, 2 -> "kış"
                else -> "bilinmiyor"
            }
        } catch (_: Exception) { "bilinmiyor" }
    }

    // ─── Near-Duplicate Merge (2.6) ──────────────────────────────────────────

    suspend fun findNearDuplicateFragment(botId: String, newEmbedding: String, threshold: Float = 0.92f): MemoryFragmentEntity? =
        withContext(Dispatchers.IO) {
            if (newEmbedding.isBlank()) return@withContext null
            val existing = fragmentDao.getFragmentsForBot(botId)
            var bestMatch: MemoryFragmentEntity? = null
            var bestSim = 0f
            for (frag in existing) {
                if (frag.embedding.isBlank()) continue
                val sim = cosineSimilarity(newEmbedding, frag.embedding)
                if (sim >= threshold && sim > bestSim) {
                    bestSim = sim
                    bestMatch = frag
                }
            }
            if (bestMatch != null) {
                android.util.Log.d("MemoryRetrievalManager", "[B2.6] Near-dup found for bot $botId: sim=$bestSim → merge into id=${bestMatch.id}")
            }
            bestMatch
        }

    private fun cosineSimilarity(a: String, b: String): Float {
        return try {
            val va = a.trim().split(",").map { it.trim().toFloat() }
            val vb = b.trim().split(",").map { it.trim().toFloat() }
            if (va.size != vb.size || va.isEmpty()) return 0f
            val dot = va.zip(vb).sumOf { (x, y) -> (x * y).toDouble() }
            val magA = Math.sqrt(va.sumOf { (it * it).toDouble() })
            val magB = Math.sqrt(vb.sumOf { (it * it).toDouble() })
            if (magA == 0.0 || magB == 0.0) 0f else (dot / (magA * magB)).toFloat()
        } catch (_: Exception) { 0f }
    }

    // ─── Örtük Öğrenme / Arşivleme (2.3) ─────────────────────────────────────

    suspend fun archiveMemoryFact(factId: Long, botId: String, reason: String) =
        withContext(Dispatchers.IO) {
            val fact = memoryFactDao.getFactById(factId) ?: return@withContext
            archivedMemoryDao.insertArchive(
                ArchivedMemoryEntity(
                    botId = botId,
                    originalType = "fact",
                    originalId = factId,
                    content = "${fact.subject}::${fact.key}::${fact.value}",
                    archivedReason = reason,
                    archivedAt = System.currentTimeMillis()
                )
            )
            memoryFactDao.deleteFact(factId)
            android.util.Log.d("MemoryRetrievalManager", "[B2.3] Fact $factId archived: $reason")
        }

    // ─── Graf Gezinmesi Kısmi Sonuç (2.1) ────────────────────────────────────

    data class GraphTraversalResult(
        val isComplete: Boolean,
        val path: List<String>,
        val lastConfirmedNode: String?,
        val missingStep: String?,
        val promptExplanation: String
    )

    suspend fun traverseRelationChain(
        botId: String,
        startEntity: String,
        relationSteps: List<String>
    ): GraphTraversalResult = withContext(Dispatchers.IO) {
        val path = mutableListOf<String>()
        var current = startEntity
        path.add(current)

        for ((idx, relationType) in relationSteps.withIndex()) {
            val relations = entityRelationDao.getRelationsForEntity(botId, current)
            val match = relations
                .filter { it.relationType.equals(relationType, ignoreCase = true) }
                .maxByOrNull { it.confidence }

            if (match == null) {
                val explanation = if (path.size > 1) {
                    "Zincirin bir kısmını buldum: ${path.joinToString(" → ")}. " +
                    "Adım ${idx + 1} ('$relationType' ilişkisi $current için) bilgi grafiğinde mevcut değil."
                } else {
                    "'$current' için '$relationType' ilişkisi bilgi grafiğinde bulunamadı."
                }
                return@withContext GraphTraversalResult(
                    isComplete = false,
                    path = path,
                    lastConfirmedNode = path.lastOrNull(),
                    missingStep = "$current --[$relationType]--> ?",
                    promptExplanation = explanation
                )
            }
            current = match.targetEntity
            path.add(current)
        }

        GraphTraversalResult(
            isComplete = true,
            path = path,
            lastConfirmedNode = current,
            missingStep = null,
            promptExplanation = "Zincir tamamlandı: ${path.joinToString(" → ")}"
        )
    }

    // ─── Retrieval Stage Tracking (2.5) ──────────────────────────────────────

    val retrievalStageMap = java.util.concurrent.ConcurrentHashMap<String, String>()

    fun recordRetrievalStage(botId: String, stage: String) {
        retrievalStageMap[botId] = stage
        android.util.Log.d("MemoryRetrievalManager", "[B2.5] retrievalStage bot=$botId stage=$stage")
    }
}