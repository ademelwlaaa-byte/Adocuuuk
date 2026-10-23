package com.example.data.managers

import com.example.data.local.BotDao
import com.example.data.local.EntityRegistryDao
import com.example.data.local.EntityRelationDao
import com.example.data.local.EntityRelationEntity
import com.example.data.local.RoutineDao
import com.example.data.local.RoutineEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class WorldStateManager(
    private val botDao: BotDao,
    private val routineDao: RoutineDao,
    private val entityRelationDao: EntityRelationDao,
    private val entityRegistryDao: EntityRegistryDao
) {
    private val turkishLocale = Locale("tr", "TR")

    suspend fun extractAndSaveRoutinesFromBotPersonality(
        botId: String,
        personality: String,
        scenario: String
    ): List<RoutineEntity> = withContext(Dispatchers.IO) {
        val combinedText = "$personality\n$scenario".trim()
        if (combinedText.isBlank()) return@withContext emptyList()

        val routinePatterns = listOf(
            Regex("""(?i)([^\n.!?]*?\b(?:her sabah|her akşam|her gece|her gün|her hafta|düzenli olarak|alışkanlık olarak)\b[^\n.!?]*[.!?]?)"""),
            Regex("""(?i)([^\n.!?]*?\b(?:pazartesi|salı|çarşamba|perşembe|cuma|cumartesi|pazar)\s+günleri\b[^\n.!?]*[.!?]?)""")
        )
        val dayNames = listOf("pazartesi", "salı", "çarşamba", "perşembe", "cuma", "cumartesi", "pazar")
        val extracted = mutableListOf<RoutineEntity>()

        for (pattern in routinePatterns) {
            for (match in pattern.findAll(combinedText)) {
                val sentence = match.groupValues[1].trim()
                if (sentence.length < 10) continue

                val lower = sentence.lowercase(turkishLocale)
                val frequency = when {
                    lower.contains("her sabah") -> "morning"
                    lower.contains("her akşam") || lower.contains("her gece") -> "evening"
                    lower.contains("her hafta") || dayNames.any { lower.contains(it) } -> "weekly"
                    else -> "daily"
                }
                val detectedDay = dayNames.firstOrNull { lower.contains(it) }

                val existing = routineDao.findRoutineByDescription(botId, sentence.take(160))
                if (existing == null) {
                    val entity = RoutineEntity(
                        botId = botId,
                        routineDescription = sentence.take(160),
                        frequency = frequency,
                        dayOfWeek = detectedDay,
                        lastMentionedStoryDate = ""
                    )
                    val newId = routineDao.insertRoutine(entity)
                    extracted.add(entity.copy(id = newId))
                } else {
                    extracted.add(existing)
                }
            }
        }
        extracted
    }

    suspend fun extractAndSaveEntityRelations(
        botId: String,
        text: String,
        currentMessageIndex: Int = 0
    ) = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext

        val relationPatterns = listOf(
            Triple(
                Regex("""\b([A-ZÇĞİÖŞÜ][a-zçğıöşü]{2,20})(?:['’][a-zçğıöşü]+)?\s+([A-ZÇĞİÖŞÜ][a-zçğıöşü]{2,20})\s+(?:adlı\s+)?(kardeşi|ablası|abisi|babası|annesi|ortağı|dostu|sevgilisi|düşmanı|patronu|asistanı)\b"""),
                true,
                "HIGH"
            ),
            Triple(
                Regex("""\b([A-ZÇĞİÖŞÜ][a-zçğıöşü]{2,20})\s+ile\s+([A-ZÇĞİÖŞÜ][a-zçğıöşü]{2,20})\s+(kardeş|ortak|dost|evli|sevgili|düşman|rakip)\b"""),
                false,
                "MEDIUM"
            )
        )

        val existingRelations = entityRelationDao.getRelationsForBot(botId)
        for ((regex, _, confidence) in relationPatterns) {
            for (match in regex.findAll(text)) {
                val sourceName = match.groupValues[1].trim()
                val targetName = match.groupValues[2].trim()
                val relType = match.groupValues[3].trim().lowercase(turkishLocale)
                if (sourceName.equals(targetName, ignoreCase = true)) continue

                val alreadyExists = existingRelations.any {
                    it.sourceEntityName.equals(sourceName, ignoreCase = true) &&
                        it.targetEntityName.equals(targetName, ignoreCase = true) &&
                        it.relationType.equals(relType, ignoreCase = true)
                }
                if (!alreadyExists) {
                    val sourceEntity = entityRegistryDao.findByName(botId, sourceName)
                    val targetEntity = entityRegistryDao.findByName(botId, targetName)
                    entityRelationDao.insertRelation(
                        EntityRelationEntity(
                            botId = botId,
                            sourceEntityId = sourceEntity?.id ?: 0L,
                            sourceEntityName = sourceName,
                            relationType = relType,
                            targetEntityId = targetEntity?.id ?: 0L,
                            targetEntityName = targetName,
                            confidence = confidence,
                            createdAtMessageIndex = currentMessageIndex
                        )
                    )
                }
            }
        }
    }
}
