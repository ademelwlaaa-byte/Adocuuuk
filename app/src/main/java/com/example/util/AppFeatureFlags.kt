package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Merkezi özellik toggle sistemi.
 * Her özellik SharedPreferences'a kaydedilir; uygulama yeniden başlatılsa bile korunur.
 * UI katmanı StateFlow'ları dinleyerek anlık güncelleme alır.
 */
object AppFeatureFlags {

    private const val PREF_NAME = "app_feature_flags"

    // ─── Bayraklar ────────────────────────────────────────────────────────────
    private const val KEY_SSE_STREAMING        = "sse_streaming"
    private const val KEY_MEMORY_TIMELINE      = "memory_timeline"
    private const val KEY_ROUTINE_NOTIF        = "routine_notifications"
    private const val KEY_SCENE_IMAGE          = "scene_image_gen"
    private const val KEY_DYNAMIC_DIFFICULTY   = "dynamic_difficulty"
    private const val KEY_MULTI_BOT_SCENE      = "multi_bot_scene"
    private const val KEY_TOKEN_COUNTER        = "token_counter"

    // Varsayılanlar: kararlı özellikler açık, deneysel olanlar kapalı
    private val DEFAULTS = mapOf(
        KEY_SSE_STREAMING      to true,
        KEY_MEMORY_TIMELINE    to true,
        KEY_ROUTINE_NOTIF      to true,
        KEY_SCENE_IMAGE        to false,
        KEY_DYNAMIC_DIFFICULTY to false,
        KEY_MULTI_BOT_SCENE    to false,
        KEY_TOKEN_COUNTER      to true,
    )

    private lateinit var prefs: SharedPreferences

    // StateFlow'lar — Compose katmanı bunları collect eder
    private val _sseStreaming        = MutableStateFlow(true)
    private val _memoryTimeline      = MutableStateFlow(true)
    private val _routineNotif        = MutableStateFlow(true)
    private val _sceneImage          = MutableStateFlow(false)
    private val _dynamicDifficulty   = MutableStateFlow(false)
    private val _multiBotScene       = MutableStateFlow(false)
    private val _tokenCounter        = MutableStateFlow(true)

    val sseStreaming:       StateFlow<Boolean> = _sseStreaming.asStateFlow()
    val memoryTimeline:    StateFlow<Boolean> = _memoryTimeline.asStateFlow()
    val routineNotif:      StateFlow<Boolean> = _routineNotif.asStateFlow()
    val sceneImage:        StateFlow<Boolean> = _sceneImage.asStateFlow()
    val dynamicDifficulty: StateFlow<Boolean> = _dynamicDifficulty.asStateFlow()
    val multiBotScene:     StateFlow<Boolean> = _multiBotScene.asStateFlow()
    val tokenCounter:      StateFlow<Boolean> = _tokenCounter.asStateFlow()

    fun init(context: Context) {
        prefs = context.applicationContext
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        syncFromPrefs()
    }

    // ─── Okuma (anlık, StateFlow bağımsız) ───────────────────────────────────
    val isSseStreamingEnabled:      Boolean get() = get(KEY_SSE_STREAMING)
    val isMemoryTimelineEnabled:    Boolean get() = get(KEY_MEMORY_TIMELINE)
    val isRoutineNotifEnabled:      Boolean get() = get(KEY_ROUTINE_NOTIF)
    val isSceneImageEnabled:        Boolean get() = get(KEY_SCENE_IMAGE)
    val isDynamicDifficultyEnabled: Boolean get() = get(KEY_DYNAMIC_DIFFICULTY)
    val isMultiBotSceneEnabled:     Boolean get() = get(KEY_MULTI_BOT_SCENE)
    val isTokenCounterEnabled:      Boolean get() = get(KEY_TOKEN_COUNTER)

    // ─── Yazma ───────────────────────────────────────────────────────────────
    fun setSseStreaming(v: Boolean)      { set(KEY_SSE_STREAMING, v);      _sseStreaming.value = v }
    fun setMemoryTimeline(v: Boolean)   { set(KEY_MEMORY_TIMELINE, v);    _memoryTimeline.value = v }
    fun setRoutineNotif(v: Boolean)     { set(KEY_ROUTINE_NOTIF, v);      _routineNotif.value = v }
    fun setSceneImage(v: Boolean)       { set(KEY_SCENE_IMAGE, v);        _sceneImage.value = v }
    fun setDynamicDifficulty(v: Boolean){ set(KEY_DYNAMIC_DIFFICULTY, v); _dynamicDifficulty.value = v }
    fun setMultiBotScene(v: Boolean)    { set(KEY_MULTI_BOT_SCENE, v);    _multiBotScene.value = v }
    fun setTokenCounter(v: Boolean)     { set(KEY_TOKEN_COUNTER, v);      _tokenCounter.value = v }

    // ─── Meta — tüm toggle listesi (UI için) ─────────────────────────────────
    data class ToggleSpec(
        val key: String,
        val label: String,
        val description: String,
        val isExperimental: Boolean = false,
        val getter: () -> Boolean,
        val setter: (Boolean) -> Unit,
    )

    fun allToggles(): List<ToggleSpec> = listOf(
        ToggleSpec(KEY_SSE_STREAMING, "Gerçek Zamanlı Akış (SSE)",
            "Model yanıtlarını token token göster, daha hızlı hissettir.",
            getter = { isSseStreamingEnabled }, setter = ::setSseStreaming),
        ToggleSpec(KEY_MEMORY_TIMELINE, "Hafıza Zaman Çizelgesi (Y4)",
            "\"Şimdiye kadar neler oldu?\" ekranını etkinleştirir.",
            getter = { isMemoryTimelineEnabled }, setter = ::setMemoryTimeline),
        ToggleSpec(KEY_ROUTINE_NOTIF, "Rutin Hatırlatıcıları",
            "Arka planda çalışan günlük rutin bildirimleri.",
            getter = { isRoutineNotifEnabled }, setter = ::setRoutineNotif),
        ToggleSpec(KEY_TOKEN_COUNTER, "Token Sayacı",
            "Her sohbette harcanan token miktarını göster.",
            getter = { isTokenCounterEnabled }, setter = ::setTokenCounter),
        ToggleSpec(KEY_DYNAMIC_DIFFICULTY, "Dinamik Zorluk (Deneysel)",
            "Konuşma kalıplarına göre sevgi/güven kazanım hızını otomatik kalibre et.",
            isExperimental = true,
            getter = { isDynamicDifficultyEnabled }, setter = ::setDynamicDifficulty),
        ToggleSpec(KEY_SCENE_IMAGE, "Sahne Görseli Üretimi (Deneysel)",
            "Sahne değişimlerinde Pollinations üzerinden arka plan görseli oluştur.",
            isExperimental = true,
            getter = { isSceneImageEnabled }, setter = ::setSceneImage),
        ToggleSpec(KEY_MULTI_BOT_SCENE, "Çoklu Bot Sahnesi (Deneysel)",
            "İki botun birbirleriyle konuştuğu karşılaşma modunu etkinleştirir.",
            isExperimental = true,
            getter = { isMultiBotSceneEnabled }, setter = ::setMultiBotScene),
    )

    // ─── Yardımcılar ─────────────────────────────────────────────────────────
    private fun get(key: String): Boolean {
        if (!::prefs.isInitialized) return DEFAULTS[key] ?: false
        return prefs.getBoolean(key, DEFAULTS[key] ?: false)
    }

    private fun set(key: String, value: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(key, value).apply()
    }

    private fun syncFromPrefs() {
        _sseStreaming.value      = get(KEY_SSE_STREAMING)
        _memoryTimeline.value    = get(KEY_MEMORY_TIMELINE)
        _routineNotif.value      = get(KEY_ROUTINE_NOTIF)
        _sceneImage.value        = get(KEY_SCENE_IMAGE)
        _dynamicDifficulty.value = get(KEY_DYNAMIC_DIFFICULTY)
        _multiBotScene.value     = get(KEY_MULTI_BOT_SCENE)
        _tokenCounter.value      = get(KEY_TOKEN_COUNTER)
    }
}
