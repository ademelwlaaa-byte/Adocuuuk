package com.example.data.managers

import com.example.data.local.AppDatabase
import com.example.data.repository.ProviderFallbackLogEntry

class ManagerIndex(
    db: AppDatabase,
    onLogFallback: (ProviderFallbackLogEntry) -> Unit
) {
    val castMemberManager = CastMemberManager(
        castMemberDao = db.castMemberDao(),
        emotionDao = db.characterEmotionDao(),
        botDao = db.botDao()
    )

    val emotionEngineManager = EmotionEngineManager(
        botDao = db.botDao(),
        emotionDao = db.characterEmotionDao(),
        emotionHistoryDao = db.emotionHistoryDao(),
        affectionEventDao = db.affectionEventDao()
    )

    val memoryRetrievalManager = MemoryRetrievalManager(
        fragmentDao = db.memoryFragmentDao(),
        memoryFactDao = db.memoryFactDao(),
        memoryEventDao = db.memoryEventDao(),
        memoryCheckpointDao = db.memoryCheckpointDao()
    )

    val providerFallbackManager = ProviderFallbackManager(
        customProviderDao = db.customProviderDao(),
        onLogFallback = onLogFallback
    )

    val worldStateManager = WorldStateManager(
        botDao = db.botDao(),
        routineDao = db.routineDao(),
        entityRelationDao = db.entityRelationDao(),
        entityRegistryDao = db.entityRegistryDao()
    )
}
