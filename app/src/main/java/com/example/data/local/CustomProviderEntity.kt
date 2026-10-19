package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_providers")
data class CustomProviderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,                             // e.g. "OpenRouter Llama 3.3"
    val baseUrl: String,                           // e.g. "https://openrouter.ai/api/v1"
    val apiKeyEncrypted: String,                  // Keystore encrypted API key
    val modelName: String,                        // e.g. "meta-llama/llama-3.3-70b-instruct"
    val apiFormat: String = "openai",             // "openai" or "anthropic"
    val supportsFunctionCalling: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    // Utility model fields — small/cheap model for background tasks
    val utilityModel: String = "",               // "" means use main model; set by user or auto-detect
    val utilityModelSource: String = "none",     // "manual" | "auto_detected" | "fallback_to_main" | "none"
    val detectedSmallModels: String = "[]"       // JSON array of auto-detected small model candidates
)
