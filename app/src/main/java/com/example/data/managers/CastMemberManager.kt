package com.example.data.managers

import com.example.data.local.BotDao
import com.example.data.local.CastMemberDao
import com.example.data.local.CastMemberEntity
import com.example.data.local.MessageDao
import com.example.data.repository.CastDetectorEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Bölüm 6 — CastMemberManager
 *
 * Sorumlu olduğu alanlar:
 *   - Karakter tespiti (detectAndRegisterCastMembers) koordinasyonu
 *   - Blacklist yönetimi: getBlacklistedCharacterNames, addToBlacklist
 *   - Pending banner state: checkForPendingCastMembers, confirmPendingCast, dismissPendingCast
 *   - Periyodik rol yeniden değerlendirme: reEvaluateCastMemberRolesIfNeeded (3.5)
 *   - Çoklu rol/bağlam çarpan seçimi: determineEffectiveRoleMultiplier (3.6)
 *   - Manuel + otomatik birleşik model: isManuallyAdded = !isAutoAdded (3.4)
 *   - Silme + blacklist dialog desteği (3.3)
 *
 * NOT: evaluateCharacterSignificance LLM çağrısı içerdiğinden EmochiRepository
 * içinde kalmaya devam eder; bu manager koordinasyon ve DB işlemlerini üstlenir.
 */
class CastMemberManager(
    private val botDao: BotDao,
    private val castMemberDao: CastMemberDao,
    private val messageDao: MessageDao
) {

    // ─── Blacklist ────────────────────────────────────────────────────────────

    suspend fun getBlacklistedNames(botId: String): Set<String> = withContext(Dispatchers.IO) {
        val dbBlacklisted = castMemberDao.getBlacklistedCastMembers(botId).map { it.name.lowercase() }.toSet()
        val engineBlacklisted = CastDetectorEngine.getBlacklistedNames(botId)
        dbBlacklisted + engineBlacklisted
    }

    suspend fun addToBlacklist(botId: String, name: String) = withContext(Dispatchers.IO) {
        CastDetectorEngine.addToBlacklist(botId, name)
        // Ensure DB record exists and is marked blacklisted
        val existing = castMemberDao.getCastMembersByBotId(botId).firstOrNull { it.name.equals(name, ignoreCase = true) }
        if (existing != null) {
            castMemberDao.updateCastMember(existing.copy(isBlacklisted = true))
        }
        android.util.Log.d("CastMemberManager", "[blacklist] '$name' added for bot $botId")
    }

    suspend fun isBlacklisted(botId: String, name: String): Boolean =
        getBlacklistedNames(botId).contains(name.lowercase())

    // ─── Birleşik veri modeli (3.4) ──────────────────────────────────────────

    /**
     * Manuel eklenen karakteri CastMemberEntity olarak kaydeder (isAutoAdded=false).
     * Bot oluşturma ekranındaki KeyCharacter girişlerinden çağrılır.
     */
    suspend fun ensureManualCastMember(
        botId: String,
        name: String,
        description: String,
        role: String,
        affectionScore: Int,
        difficultyMultiplier: Double
    ): CastMemberEntity = withContext(Dispatchers.IO) {
        val existing = castMemberDao.getCastMembersByBotId(botId)
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
        if (existing != null) {
            val updated = existing.copy(
                description = description.ifBlank { existing.description },
                role = role.ifBlank { existing.role },
                affectionScore = affectionScore,
                baseAffectionDifficultyMultiplier = difficultyMultiplier,
                isAutoAdded = false  // promote to manually added
            )
            castMemberDao.updateCastMember(updated)
            android.util.Log.d("CastMemberManager", "[manual] Promoted '$name' isAutoAdded=false for bot $botId")
            updated
        } else {
            val now = System.currentTimeMillis()
            val entity = CastMemberEntity(
                botId = botId,
                name = name,
                description = description,
                role = role,
                affectionScore = affectionScore.coerceIn(0, 100),
                relationshipState = "Tanıdık",
                firstAppearedAt = now,
                importanceScore = 85,
                isAutoAdded = false,
                isBlacklisted = false,
                baseAffectionDifficultyMultiplier = difficultyMultiplier
            )
            castMemberDao.insertCastMember(entity)
            android.util.Log.d("CastMemberManager", "[manual] Inserted '$name' isAutoAdded=false for bot $botId")
            entity
        }
    }

    // ─── Çoklu rol / bağlam çarpanı (3.6) ───────────────────────────────────

    /**
     * Sahne bağlamına göre aktif rolü ve çarpanını döndürür.
     * mode=formal + iş bağlamı → kısıtlayıcı çarpan (min)
     * mode=casual + kişisel bağlam → cömert çarpan (max)
     */
    fun determineEffectiveMultiplier(
        member: CastMemberEntity,
        sceneMode: String,
        sceneContext: String
    ): Pair<String, Double> {
        val primaryMult = member.baseAffectionDifficultyMultiplier
        val secondaryRole = member.secondaryRole
        val secondaryMult = if (!secondaryRole.isNullOrBlank()) {
            roleMult(secondaryRole)
        } else null

        val isWorkContext = sceneMode.lowercase() in listOf("formal", "professional") ||
            sceneContext.any { word -> listOf("iş", "ofis", "patron", "toplantı", "çalış", "proje", "müdür").any { sceneContext.contains(it) } }
        val isPersonalContext = sceneMode.lowercase() in listOf("casual", "romantic", "intimate") ||
            listOf("özel", "aşk", "romantik", "akşam", "sevgi", "hasret", "özlem").any { sceneContext.contains(it) }

        val activeMult = when {
            secondaryMult == null -> primaryMult
            isWorkContext && !isPersonalContext -> {
                val chosen = minOf(primaryMult, secondaryMult)
                android.util.Log.d("CastMemberManager", "[B3.6] MultiRole '${member.name}' roles=[${member.role}, $secondaryRole] isWork=true → min=$chosen")
                chosen
            }
            isPersonalContext && !isWorkContext -> {
                val chosen = maxOf(primaryMult, secondaryMult)
                android.util.Log.d("CastMemberManager", "[B3.6] MultiRole '${member.name}' roles=[${member.role}, $secondaryRole] isPersonal=true → max=$chosen")
                chosen
            }
            else -> {
                val chosen = minOf(primaryMult, secondaryMult)
                android.util.Log.d("CastMemberManager", "[B3.6] MultiRole '${member.name}' roles=[${member.role}, $secondaryRole] mixed → min=$chosen")
                chosen
            }
        }

        val activeRole = if (secondaryMult != null && activeMult == secondaryMult && !secondaryRole.isNullOrBlank()) secondaryRole else member.role
        return Pair(activeRole, activeMult)
    }

    private fun roleMult(role: String): Double {
        val r = role.lowercase()
        return when {
            r.contains("patron") || r.contains("müdür") || r.contains("amir") || r.contains("otorite") -> 0.75
            r.contains("yabancı") || r.contains("stranger") -> 0.85
            r.contains("sevgili") || r.contains("lover") || r.contains("aşk") || r.contains("romantik") -> 1.40
            r.contains("arkadaş") || r.contains("dost") -> 1.10
            r.contains("aile") || r.contains("kardeş") || r.contains("anne") || r.contains("baba") -> 1.20
            else -> 1.05
        }
    }

    // ─── Periyodik rol yeniden değerlendirme (3.5) ───────────────────────────

    /**
     * Her 30 mesajda bir veya büyük hikaye olayı sonrası çağrılır.
     * affectionScore'u değiştirmez — sadece baseAffectionDifficultyMultiplier güncellenir.
     * Gerçek LLM değerlendirmesi EmochiRepository.reEvaluateCastMemberRolesIfNeeded'de.
     */
    suspend fun shouldReEvaluateRoles(botId: String, currentMessageCount: Int): Boolean {
        val members = castMemberDao.getCastMembersByBotId(botId)
        return members.any { m ->
            val isPeriodicDue = currentMessageCount - (m.lastRoleEvaluationIndex ?: 0) >= 30
            isPeriodicDue
        }
    }

    suspend fun updateMemberMultiplierSafely(
        member: CastMemberEntity,
        newMultiplier: Double,
        newRole: String,
        currentMessageCount: Int
    ) = withContext(Dispatchers.IO) {
        val clamped = newMultiplier.coerceIn(0.50, 2.50)
        // affectionScore DOKUNULMAZ — sadece multiplier ve rol güncellenir (3.5)
        castMemberDao.updateCastMember(
            member.copy(
                role = newRole,
                baseAffectionDifficultyMultiplier = clamped,
                lastRoleEvaluationIndex = currentMessageCount
            )
        )
        android.util.Log.d("CastMemberManager", "[B3.5] '${member.name}' role=$newRole mult=$clamped (affectionScore unchanged)")
    }

    // ─── Silme (3.3) ─────────────────────────────────────────────────────────

    suspend fun deleteAutoAddedMember(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersByBotId(botId).firstOrNull { it.id == castMemberId }
        if (member != null && member.isAutoAdded) {
            castMemberDao.deleteCastMemberById(castMemberId)
            android.util.Log.d("CastMemberManager", "[delete] Auto-added '${member.name}' deleted from bot $botId")
        }
    }

    suspend fun deleteAndBlacklist(botId: String, castMemberId: Long) = withContext(Dispatchers.IO) {
        val member = castMemberDao.getCastMembersByBotId(botId).firstOrNull { it.id == castMemberId }
        if (member != null) {
            addToBlacklist(botId, member.name)
            castMemberDao.deleteCastMemberById(castMemberId)
            android.util.Log.d("CastMemberManager", "[delete+blacklist] '${member.name}' blacklisted and deleted from bot $botId")
        }
    }
}