package com.example.data.managers

import com.example.data.local.UserSettingsEntity
import com.example.data.local.CustomProviderEntity
import com.example.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONObject

/**
 * Bölüm 6 — ProviderFallbackManager
 *
 * Sorumlu olduğu alanlar:
 *   - 5.1: Çoklu hata / çoklu katman fallback koordinasyonu
 *   - 5.2: Enabled toggle izolasyonu — kapalı sağlayıcı hiç denenmez
 *   - 5.4: validateModelsOnStartup — deprecated model uyarısı
 *   - 5.5: RateLimitTracker izolasyonu — sağlayıcı başına izole state
 *   - 5.7: Utility model 3-katmanlı fallback (utility → main → heuristic)
 *   - Hata özeti: tüm katmanlar başarısız → detaylı hata metni
 *
 * NOT: Gerçek HTTP çağrıları (callGeminiApi, callOpenAiCompatibleApi vb.)
 * LLMAdapterFactory + adapter pattern üzerinden EmochiRepository'de kalır.
 * Bu manager, fallback state, toggle kontrolü ve model doğrulama mantığını yönetir.
 */
class ProviderFallbackManager {

    // ─── B5.2: Provider enabled state ────────────────────────────────────────

    fun isProviderEnabled(providerKey: String, settings: UserSettingsEntity): Boolean {
        return when (providerKey.lowercase()) {
            "gemini"     -> settings.customApiKey.isNotBlank()
            "groq"       -> settings.groqApiKey.isNotBlank()
            "claude"     -> settings.claudeApiKey.isNotBlank()
            "openai"     -> settings.openaiApiKey.isNotBlank()
            "openrouter" -> settings.openRouterApiKey.isNotBlank()
            "nvidia"     -> settings.nvidiaApiKey.isNotBlank()
            "mistral"    -> settings.mistralApiKey.isNotBlank()
            "llm7"       -> settings.enableLlm7
            "pollinations" -> settings.enablePollinations
            "ovh"        -> settings.enableOvh
            else         -> providerKey.startsWith("custom_")
        }
    }

    // ─── B5.5: Per-provider rate limit tracker ────────────────────────────────

    data class RateLimitState(
        val cooldownUntilMs: Long = 0L,
        val isExhaustible: Boolean = false,
        val exhausted: Boolean = false
    )

    private val rateLimitMap = java.util.concurrent.ConcurrentHashMap<String, RateLimitState>()

    fun isRateLimited(providerKey: String): Boolean {
        val state = rateLimitMap[providerKey] ?: return false
        if (state.isExhaustible && state.exhausted) return true
        return System.currentTimeMillis() < state.cooldownUntilMs
    }

    fun recordRateLimit(providerKey: String, cooldownMs: Long = 60_000L) {
        val existing = rateLimitMap[providerKey] ?: RateLimitState()
        rateLimitMap[providerKey] = existing.copy(cooldownUntilMs = System.currentTimeMillis() + cooldownMs)
        android.util.Log.w("ProviderFallbackManager", "[B5.5] $providerKey rate-limited for ${cooldownMs/1000}s")
    }

    fun recordExhausted(providerKey: String) {
        rateLimitMap[providerKey] = RateLimitState(isExhaustible = true, exhausted = true)
        android.util.Log.w("ProviderFallbackManager", "[B5.5] $providerKey exhausted (credit/daily limit)")
    }

    fun clearRateLimit(providerKey: String) {
        rateLimitMap.remove(providerKey)
    }

    fun getRateLimitDiagnostics(): Map<String, RateLimitState> = rateLimitMap.toMap()

    // ─── B5.1: Çoklu hata özeti ───────────────────────────────────────────────

    data class ProviderAttempt(
        val providerKey: String,
        val providerLabel: String,
        val errorMessage: String
    )

    fun buildMultiFailureMessage(attempts: List<ProviderAttempt>): String {
        if (attempts.isEmpty()) return "Hiçbir sağlayıcıya ulaşılamadı."
        val lines = attempts.joinToString("\n") { a ->
            val shortErr = when {
                a.errorMessage.contains("429") || a.errorMessage.contains("rate", ignoreCase = true) -> "rate limit"
                a.errorMessage.contains("404") || a.errorMessage.contains("model", ignoreCase = true) -> "model bulunamadı"
                a.errorMessage.contains("401") || a.errorMessage.contains("key", ignoreCase = true) -> "API key geçersiz"
                a.errorMessage.contains("timeout", ignoreCase = true) || a.errorMessage.contains("timed out") -> "zaman aşımı"
                a.errorMessage.contains("exhausted", ignoreCase = true) || a.errorMessage.contains("credit") -> "kredi tükendi"
                else -> a.errorMessage.take(60)
            }
            "• ${a.providerLabel}: $shortErr"
        }
        return "Hiçbir sağlayıcıya ulaşılamadı:\n$lines\n\nLütfen API ayarlarınızı kontrol edin."
    }

    // ─── B5.4: Deprecated model warning state ─────────────────────────────────

    data class DeprecatedModelWarning(
        val providerKey: String,
        val providerLabel: String,
        val modelName: String
    )

    private val _warnings = java.util.concurrent.CopyOnWriteArrayList<DeprecatedModelWarning>()
    private val dismissedProviders = java.util.concurrent.CopyOnWriteArraySet<String>()

    val deprecatedWarnings: List<DeprecatedModelWarning> get() = _warnings.toList()

    fun dismissWarning(providerKey: String) {
        dismissedProviders.add(providerKey)
        _warnings.removeAll { it.providerKey == providerKey }
        android.util.Log.d("ProviderFallbackManager", "[B5.4] Warning dismissed for $providerKey until next launch")
    }

    suspend fun validateModels(settings: UserSettingsEntity, customProviders: List<CustomProviderEntity> = emptyList()) =
        withContext(Dispatchers.IO) {
            _warnings.clear()

            data class Check(val key: String, val label: String, val apiKey: String, val model: String, val modelsUrl: String)

            val checks = listOf(
                Check("gemini",     "Gemini",     settings.customApiKey,    settings.geminiModel,     "https://generativelanguage.googleapis.com/v1beta/models?key=${settings.customApiKey}"),
                Check("groq",       "Groq",       settings.groqApiKey,      settings.groqModel,       "https://api.groq.com/openai/v1/models"),
                Check("claude",     "Claude",     settings.claudeApiKey,    settings.claudeModel,     "https://api.anthropic.com/v1/models"),
                Check("openai",     "OpenAI",     settings.openaiApiKey,    settings.openaiModel,     "https://api.openai.com/v1/models"),
                Check("openrouter", "OpenRouter", settings.openRouterApiKey,settings.openRouterModel, "https://openrouter.ai/api/v1/models"),
                Check("nvidia",     "NVIDIA NIM", settings.nvidiaApiKey,    settings.nvidiaModel,     "https://integrate.api.nvidia.com/v1/models"),
                Check("mistral",    "Mistral",    settings.mistralApiKey,   settings.mistralModel,    "https://api.mistral.ai/v1/models")
            )

            for (check in checks) {
                if (check.apiKey.isBlank() || check.model.isBlank()) continue
                if (dismissedProviders.contains(check.key)) continue
                try {
                    val req = Request.Builder()
                        .url(check.modelsUrl)
                        .addHeader("Authorization", "Bearer ${check.apiKey}")
                        .addHeader("x-api-key", check.apiKey)
                        .get().build()
                    RetrofitClient.okHttpClient.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val body = resp.body?.string() ?: ""
                            val available = mutableListOf<String>()
                            try {
                                val json = JSONObject(body)
                                val data = json.optJSONArray("data") ?: json.optJSONArray("models")
                                if (data != null) for (i in 0 until data.length()) {
                                    val id = data.optJSONObject(i)?.optString("id")
                                        ?: data.optJSONObject(i)?.optString("name") ?: ""
                                    if (id.isNotBlank()) available.add(id)
                                }
                            } catch (_: Exception) {}
                            if (available.isNotEmpty() && !available.any {
                                    it.equals(check.model, ignoreCase = true) ||
                                    it.contains(check.model, ignoreCase = true)
                                }) {
                                _warnings.add(DeprecatedModelWarning(check.key, check.label, check.model))
                                android.util.Log.w("ProviderFallbackManager", "[B5.4] DEPRECATED: ${check.label} model='${check.model}'")
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.d("ProviderFallbackManager", "[B5.4] Skip ${check.label}: ${e.message}")
                }
            }
        }

    // ─── B5.2: Sağlayıcı değiştirme sohbet sırasında güvenli ────────────────
    // Mevcut istek zaten başlamışsa in-flight adapter yanıtı döner,
    // bir sonraki generateAiReply çağrısında yeni toggle state okunur.
    // Bu state geçişi EmochiRepository.getOrCreateSettings() flow'unda otomatik.
    // Bu manager herhangi bir sohbet-içi kesme yapmaz — safe by design.

    fun logProviderToggleChange(providerKey: String, nowEnabled: Boolean) {
        android.util.Log.d("ProviderFallbackManager",
            "[B5.2] Provider '$providerKey' toggled to enabled=$nowEnabled — takes effect on next message")
    }
}