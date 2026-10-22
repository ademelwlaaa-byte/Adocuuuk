@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
package com.example.ui.components
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
import com.example.ui.theme.AppColors
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.OutlinedButton
import com.example.data.repository.EmochiRepository
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import com.example.ui.components.BotAvatarImage
import com.example.ui.components.EmochiTextField
import com.example.ui.components.GradientCard
import com.example.ui.components.ActiveGradientCard
import com.example.ui.components.WarningGradientCard
import com.example.ui.components.SuccessGradientCard
import com.example.ui.components.DangerGradientCard
import com.example.ui.components.EmochiLoadingSpinner
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.BotEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.KeyCharacter
import com.example.ui.theme.EmochiBorder
import com.example.ui.theme.EmochiCard
import com.example.ui.theme.EmochiError
import com.example.ui.theme.EmochiErrorContainer
import com.example.ui.theme.EmochiPrimary
import com.example.ui.theme.EmochiSurface
import com.example.ui.theme.EmochiTextMuted
import com.example.ui.theme.EmochiTextPrimary
import com.example.ui.theme.EmochiTextSecondary
import kotlinx.coroutines.launch

@Composable
fun GlobalSettingsModal(
    settings: UserSettingsEntity,
    onDismiss: () -> Unit,
    onSaveSettings: (UserSettingsEntity) -> Unit,
    onExportData: suspend () -> String,
    onImportData: suspend (String) -> Unit,
    viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var geminiApiKey by remember { mutableStateOf(settings.customApiKey) }
    var groqApiKey by remember { mutableStateOf(settings.groqApiKey) }
    var claudeApiKey by remember { mutableStateOf(settings.claudeApiKey) }
    var openaiApiKey by remember { mutableStateOf(settings.openaiApiKey) }
    var openRouterApiKey by remember { mutableStateOf(settings.openRouterApiKey) }
    var openRouterModel by remember { mutableStateOf(settings.openRouterModel.ifBlank { "deepseek/deepseek-chat" }) }
    var nvidiaApiKey by remember { mutableStateOf(settings.nvidiaApiKey) }
    var nvidiaModel by remember { mutableStateOf(settings.nvidiaModel.ifBlank { "" }) }
    var mistralApiKey by remember { mutableStateOf(settings.mistralApiKey) }
    var mistralModel by remember { mutableStateOf(settings.mistralModel.ifBlank { "mistral-large-latest" }) }
    var backupApiKey by remember { mutableStateOf(settings.backupApiKey) }

    var openRouterTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingOpenRouter by remember { mutableStateOf(false) }

    var nvidiaTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingNvidia by remember { mutableStateOf(false) }

    var mistralTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingMistral by remember { mutableStateOf(false) }

    var selectedProvider by remember { mutableStateOf(settings.selectedProvider.ifBlank { "gemini" }) }
    var selectedModel by remember { mutableStateOf(settings.selectedModel) }
    var fallbackModel by remember { mutableStateOf(settings.fallbackModel) }
    var responseLength by remember { mutableStateOf(settings.responseLength) }
    var enableNsfw by remember { mutableStateOf(settings.enableNsfw) }
    var enableFlirty by remember { mutableStateOf(settings.enableFlirty) }
    var enableHardcore by remember { mutableStateOf(settings.enableHardcore) }
    var enableFetish by remember { mutableStateOf(settings.enableFetish) }
    var enableDarkRp by remember { mutableStateOf(settings.enableDarkRp) }
    var enableSweet by remember { mutableStateOf(settings.enableSweet) }
    var enablePrimal by remember { mutableStateOf(settings.enablePrimal) }
    var enableAutoFallback by remember { mutableStateOf(settings.enableAutoFallback) }
    var enableLlm7 by remember { mutableStateOf(settings.enableLlm7 || settings.llm7Enabled) }
    var enablePollinations by remember { mutableStateOf(settings.enablePollinations && settings.pollinationsEnabled) }
    var enableOvh by remember { mutableStateOf(settings.enableOvh && settings.ovhEnabled) }
    var openRouterEnabled by remember { mutableStateOf(settings.openRouterEnabled) }
    var nvidiaEnabled by remember { mutableStateOf(settings.nvidiaEnabled) }
    var mistralEnabled by remember { mutableStateOf(settings.mistralEnabled) }
    var pollinationsModel by remember { mutableStateOf(settings.pollinationsModel.ifBlank { "openai" }) }
    var ovhModel by remember { mutableStateOf(settings.ovhModel.ifBlank { "meta-llama/Meta-Llama-3-70B-Instruct" }) }

    var llm7TestResult by remember { mutableStateOf<String?>(null) }
    var isTestingLlm7 by remember { mutableStateOf(false) }

    var pollinationsTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingPollinations by remember { mutableStateOf(false) }

    var showFallbackLogModal by remember { mutableStateOf(false) }
    var enableTts by remember { mutableStateOf(settings.enableTts) }
    var ttsSpeed by remember { mutableStateOf(settings.ttsSpeed) }
    var ttsPitch by remember { mutableStateOf(settings.ttsPitch) }
    var selectedVoiceName by remember { mutableStateOf(settings.selectedVoiceName) }
    var appLanguage by remember { mutableStateOf(settings.appLanguage) }

    var showKeys by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<String?>("models") }
    var fallbackChainOrder by remember { mutableStateOf(settings.fallbackChainOrder) }
    var showFeatureFlagsPanel by remember { mutableStateOf(false) }

    var exportJson by remember { mutableStateOf("") }
    var importJson by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var showUpdateModal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    isBusy = true
                    val content = context.contentResolver.openInputStream(it)?.use { stream ->
                        stream.bufferedReader().use { reader -> reader.readText() }
                    } ?: ""
                    if (content.isNotBlank()) {
                        onImportData(content)
                        Toast.makeText(context, "Yedek dosyadan yüklendi!", Toast.LENGTH_SHORT).show()
                        statusMessage = "Dosya başarıyla içe aktarıldı."
                    }
                } catch (e: Exception) {
                    statusMessage = "Yükleme hatası: ${e.message}"
                } finally {
                    isBusy = false
                }
            }
        }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    isBusy = true
                    val json = if (exportJson.isNotBlank()) exportJson else onExportData()
                    exportJson = json
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        stream.write(json.toByteArray())
                    }
                    Toast.makeText(context, "Yedek dosyaya kaydedildi!", Toast.LENGTH_SHORT).show()
                    statusMessage = "Yedek dosyaya kaydedildi."
                } catch (e: Exception) {
                    statusMessage = "Kaydetme hatası: ${e.message}"
                } finally {
                    isBusy = false
                }
            }
        }
    }

    val modelsList = listOf(
        ModelSpec(
            key = "default",
            name = "LLM7 Default (Ücretsiz)",
            provider = "LLM7 (api.llm7.io)",
            tokenCostRate = "🆓 ÜCRETSİZ (API Key Gerekmez)",
            badgeColor = AppColors.SuccessGreen,
            description = "API Key gerektirmeyen tamamen ücretsiz OpenAI uyumlu servis. Öncelik modunda varsayılan olarak denenir."
        ),
        ModelSpec(
            key = "gemini-2.0-flash",
            name = "Gemini 2.0 Flash",
            provider = "Google Gemini",
            tokenCostRate = "🟢 Düşük (~0.5x Token)",
            badgeColor = AppColors.Green,
            description = "En gelişmiş, dengeli ve hızlı Gemini modeli. Düşük token harcaması ile yüksek kaliteli rol yapma yanıtları verir."
        ),
        ModelSpec(
            key = "gemini-1.5-pro",
            name = "Gemini 1.5 Pro",
            provider = "Google Gemini",
            tokenCostRate = "🔴 Yüksek (~2.5x Token)",
            badgeColor = AppColors.PinkMuted,
            description = "Üst düzey zeka, derin kurgu ve detaylı roman kalitesinde tutarlı karakter anlatımı."
        ),
        ModelSpec(
            key = "openai/gpt-oss-120b",
            name = "Groq GPT-OSS 120B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.WarningOrange,
            description = "Groq Production katmanında ultra hızlı ve yüksek zekalı açık kaynak model."
        ),
        ModelSpec(
            key = "openai/gpt-oss-20b",
            name = "Groq GPT-OSS 20B",
            provider = "Groq API",
            tokenCostRate = "🟢 Düşük (~0.5x Token)",
            badgeColor = AppColors.Green,
            description = "Groq Production katmanında anlık ultra hızlı ve pratik sohbet yanıtları."
        ),
        ModelSpec(
            key = "qwen/qwen3.6-27b",
            name = "Groq Qwen 3.6 27B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.MagentaPurple,
            description = "Qwen 3.6 27B deneysel model. (Preview katmanında)"
        ),
        ModelSpec(
            key = "qwen/qwen3.8-27b",
            name = "Groq Qwen 3.8 27B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.PinkMuted,
            description = "Qwen 3.8 27B en son yayınlanan deneysel model. (Preview katmanında)"
        ),
        ModelSpec(
            key = "claude-3-5-sonnet-20241022",
            name = "Claude 3.5 Sonnet",
            provider = "Anthropic",
            tokenCostRate = "🔴 Çok Yüksek (~3.0x Token)",
            badgeColor = AppColors.Red,
            description = "Edebi anlatım, yüksek duygusal derinlik ve roman kalitesinde akıcı diyaloglar."
        ),
        ModelSpec(
            key = "claude-3-5-haiku-20241022",
            name = "Claude 3.5 Haiku",
            provider = "Anthropic",
            tokenCostRate = "🟡 Orta (~1.2x Token)",
            badgeColor = AppColors.YellowAmber,
            description = "Hızlı ve seri Claude kalitesi. Kısa ve orta boy diyaloglar için ideal."
        ),
        ModelSpec(
            key = "gpt-4o",
            name = "OpenAI GPT-4o",
            provider = "OpenAI",
            tokenCostRate = "🔴 Yüksek (~2.0x Token)",
            badgeColor = AppColors.MagentaPurple,
            description = "OpenAI amiral gemisi akıllı sohbet modeli."
        ),
        ModelSpec(
            key = "gpt-4o-mini",
            name = "OpenAI GPT-4o Mini",
            provider = "OpenAI",
            tokenCostRate = "🟢 Düşük (~0.8x Token)",
            badgeColor = AppColors.Green,
            description = "Ekonomik, tutarlı ve akıcı OpenAI sohbet altyapısı."
        ),
        ModelSpec(
            key = "deepseek-chat",
            name = "DeepSeek V3",
            provider = "DeepSeek",
            tokenCostRate = "🟢 Düşük (~0.6x Token)",
            badgeColor = AppColors.TealDark,
            description = "Bütçe dostu, geniş bağlamlı yüksek akıl yürütme gücü sunan model."
        )
    )

    var fallbackDropdownExpanded by remember { mutableStateOf(false) }
    val totalTokensUsed = settings.totalPromptTokens + settings.totalCandidateTokens

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(AppColors.CardAbyss, AppColors.CardNight)
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF6D28D9), Color(0xFF3B1070), Color(0xFF7C3AED))
                        )
                    ),
                    androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )
        ) {
            AppBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                // PREMIUM HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF1C0D3C), Color(0xFF130826))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(13.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            listOf(Color(0xFF3B1070), Color(0xFF1C0848))
                                        )
                                    )
                                    .border(
                                        androidx.compose.foundation.BorderStroke(
                                            1.2.dp,
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                listOf(Color(0xFF9333EA), Color(0xFF6D28D9))
                                            )
                                        ),
                                        androidx.compose.foundation.shape.RoundedCornerShape(13.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFFCDA8FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Uygulama & AI Ayarları",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Model, API anahtarları ve filtreler",
                                    color = Color(0xFF9E8DC0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                .background(Color(0x30A855F7))
                                .border(1.dp, Color(0x40A855F7), androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFCDA8FF), modifier = Modifier.size(16.dp))
                        }
                    }
                    // Bottom separator line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0x607C3AED), Color.Transparent)
                                )
                            )
                    )
                }

                    // SCROLLABLE CONTENT BODY
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                // OPTION 1: MODELS & API KEYS
                CollapsibleSettingsOption(
                    title = "🤖 Modeller & API Anahtarları",
                    subtitle = "Gemini, Groq, Claude, OpenAI anahtarları ve aktif model seçimi",
                    icon = Icons.Default.Key,
                    isExpanded = expandedSection == "models",
                    onToggle = { expandedSection = if (expandedSection == "models") null else "models" },
                    badgeText = selectedProvider.uppercase(),
                    badgeColor = EmochiPrimary
                ) {
                    // Eye Icon for Key Visibility
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0F0826))))
                                .border(0.8.dp, EmochiPrimary.copy(alpha=0.5f), RoundedCornerShape(8.dp))
                                .clickable { showKeys = !showKeys },
                            contentAlignment = Alignment.Center
                            ) {
                            Icon(
                                imageVector = if (showKeys) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = EmochiPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showKeys) "Anahtarları Gizle" else "Anahtarları Göster",
                                color = EmochiPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    val customProviders by viewModel.customProviders.collectAsState()
                    var showAddCustomModal by remember { mutableStateOf(false) }
                    var editingCustomProvider by remember { mutableStateOf<com.example.data.local.CustomProviderEntity?>(null) }
                    var quickModelEditProvider by remember { mutableStateOf<com.example.data.local.CustomProviderEntity?>(null) }

                    // Primary Provider Selector Segmented Buttons
                    Text("Ana Sağlayıcı Seçin:", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val providers = listOf(
                            "llm7" to "⚡ LLM7",
                            "gemini" to "Gemini",
                            "groq" to "Groq",
                            "claude" to "Claude",
                            "openai" to "OpenAI",
                            "openrouter" to "OpenRouter",
                            "nvidia" to "NVIDIA NIM",
                            "mistral" to "Mistral AI",
                            "custom" to "🌐 Özel API"
                        )
                        providers.forEach { (pKey, pLabel) ->
                            val isSel = if (pKey == "custom") selectedProvider.startsWith("custom") else selectedProvider == pKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) EmochiPrimary.copy(alpha = 0.25f) else EmochiCard)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) EmochiPrimary else EmochiBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        when (pKey) {
                                            "openrouter" -> {
                                                selectedProvider = "openrouter"
                                                selectedModel = openRouterModel.ifBlank { "deepseek/deepseek-chat" }
                                            }
                                            "nvidia" -> {
                                                selectedProvider = "nvidia"
                                                selectedModel = nvidiaModel.ifBlank { "" }
                                            }
                                            "mistral" -> {
                                                selectedProvider = "mistral"
                                                selectedModel = mistralModel.ifBlank { "mistral-large-latest" }
                                            }
                                            "custom" -> {
                                                if (customProviders.isNotEmpty()) {
                                                    selectedProvider = "custom_${customProviders.first().id}"
                                                    selectedModel = customProviders.first().modelName
                                                } else {
                                                    selectedProvider = "custom"
                                                    showAddCustomModal = true
                                                }
                                            }
                                            else -> {
                                                selectedProvider = pKey
                                                val providerModels = modelsList.filter {
                                                    when (pKey) {
                                                        "llm7" -> it.provider == "LLM7 (api.llm7.io)"
                                                        "gemini" -> it.provider == "Google Gemini"
                                                        "groq" -> it.provider == "Groq API"
                                                        "claude" -> it.provider == "Anthropic"
                                                        "openai" -> it.provider == "OpenAI" || it.provider == "DeepSeek"
                                                        else -> false
                                                    }
                                                }
                                                if (providerModels.none { it.key == selectedModel } && providerModels.isNotEmpty()) {
                                                    selectedModel = providerModels.first().key
                                                }
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pLabel,
                                    color = if (isSel) Color.White else EmochiTextSecondary,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Reliability Dashboard for SECONDARY tier
                    ProviderReliabilityDashboardUI()

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Custom Provider Header Banner (Katman 0 Indicator)
                    val activeCustomProviderAtTop = customProviders.find { "custom_${it.id}" == selectedProvider }
                    if (activeCustomProviderAtTop != null) {
                        ActiveGradientCard(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            accentColor = AppColors.PurpleVibrant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.PurpleVibrant, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Şu an aktif: ${activeCustomProviderAtTop.label} (Özel API)",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.PurpleVibrant.copy(alpha = 0.3f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("KATMAN 0 (İLK SIRADA)", color = AppColors.PurpleLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Model: ${activeCustomProviderAtTop.modelName} | URL: ${activeCustomProviderAtTop.baseUrl}",
                                    color = AppColors.PrimaryViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚡ İstekler önce bu Özel API'ye (Katman 0) gönderilir. Başarısızlık durumunda otomatik Katman 1'e (LLM7/Pollinations/OVH) düşer.",
                                    color = EmochiTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Provider Card 0: LLM7 (Fixed / Free Provider)
                    val isLlm7Active = selectedProvider == "llm7" || enableLlm7
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isLlm7Active)
                            listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isLlm7Active) 1.8.dp else 1.2.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0. LLM7 (Ücretsiz Sabit Servis)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enableLlm7) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ÖNCELİKLİ (STEP 1)", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmeyen ücretsiz servis (https://api.llm7.io/v1). Model: default | Key: unused",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("LLM7'yi Öncelikli Kullan (Fallback Chain)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Açıkken her istek ilk olarak LLM7'ye gönderilir. Başarısız olursa sıradaki sağlayıcı denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enableLlm7,
                                    onCheckedChange = { enableLlm7 = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isTestingLlm7 = true
                                            llm7TestResult = "Bağlantı test ediliyor..."
                                            val res = viewModel.testLlm7Connection()
                                            isTestingLlm7 = false
                                            if (res.isSuccess) {
                                                val fcText = if (res.supportsFunctionCalling) "Function calling (gelişmiş hafıza) destekleniyor." else "Metin-tabanlı hafıza modu ([[MEMORY_SAVE]]) aktif."
                                                llm7TestResult = "✅ Bağlantı başarılı! $fcText"
                                            } else {
                                                llm7TestResult = "❌ Bağlantı hatası: ${res.errorMessage}"
                                            }
                                        }
                                    },
                                    enabled = !isTestingLlm7,
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(if (isTestingLlm7) "Test ediliyor..." else "⚡ LLM7 Test Et", color = AppColors.SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showFallbackLogModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("📋 Fallback Logları", color = AppColors.PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!llm7TestResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = llm7TestResult!!,
                                    color = if (llm7TestResult!!.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            val llm7Stats = remember { com.example.util.Llm7RateLimitTracker.getUsageStats() }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppColors.DarkBg,
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("📊 LLM7 Kota & Performans Göstergesi", color = AppColors.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Dakika / Saat İstek:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.requestsLastMinute}/10 req/dk | ${llm7Stats.requestsLastHour}/60 req/saat", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("24-Saatlik Token Kullanımı:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.tokensUsed24h} / ${llm7Stats.tokenLimit24h} token", color = if (llm7Stats.isTokenLimitReached) AppColors.ErrorRed else AppColors.MintGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Ort. Süre / Hata Düzeltme:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.averageResponseTimeMs}ms | ${llm7Stats.malformedCount} düzelg | ${llm7Stats.fallbackCount} devir", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 0B: Pollinations.ai (Free Provider)
                    val isPollinationsActive = selectedProvider == "pollinations" || enablePollinations
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isPollinationsActive)
                            listOf(Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.4f), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isPollinationsActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0B. Pollinations (Limitsiz Ücretsiz)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enablePollinations) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("STEP 2 FALLBACK", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmez (https://text.pollinations.ai/openai). Key: unused",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aktif Pollinations Modeli: $pollinationsModel", color = EmochiTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("openai", "mistral", "qwen", "llama").forEach { m ->
                                    val isSelected = m == pollinationsModel
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) AppColors.SuccessGreen.copy(alpha = 0.25f) else EmochiCard)
                                            .border(1.dp, if (isSelected) AppColors.SuccessGreen else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { pollinationsModel = m }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(m, color = if (isSelected) Color.White else EmochiTextSecondary, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Pollinations'ı Fallback Chain'e Ekle", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("LLM7 başarısız olursa otomatik olarak Pollinations denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enablePollinations,
                                    onCheckedChange = { enablePollinations = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingPollinations = true
                                        pollinationsTestResult = "Bağlantı test ediliyor..."
                                        val res = viewModel.testPollinationsConnection(pollinationsModel)
                                        isTestingPollinations = false
                                        if (res.isSuccess) {
                                            pollinationsTestResult = "✅ Pollinations bağlantısı başarılı! (Model: $pollinationsModel)"
                                        } else {
                                            pollinationsTestResult = "❌ Bağlantı hatası: ${res.errorMessage}"
                                        }
                                    }
                                },
                                enabled = !isTestingPollinations,
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingPollinations) "Test ediliyor..." else "⚡ Pollinations Test Et", color = AppColors.SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!pollinationsTestResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pollinationsTestResult!!,
                                    color = if (pollinationsTestResult!!.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 0C: OVH AI Free
                    val isOvhActive = selectedProvider == "ovh" || enableOvh
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOvhActive)
                            listOf(Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.4f), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isOvhActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0C. OVH AI (Anahtarsız Ücretsiz)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enableOvh) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("KATMAN 1 FALLBACK", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmez. Model: Meta-Llama-3-70B-Instruct (Varsayılan Kapalı).",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("OVH AI'yi Fallback Chain'e Ekle (Katman 1)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("LLM7 ve Pollinations başarısız olursa otomatik denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enableOvh,
                                    onCheckedChange = { enableOvh = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 1: Google Gemini
                    val isGeminiActive = selectedProvider == "gemini"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isGeminiActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isGeminiActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("1. Google Gemini", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isGeminiActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.Green.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.Green, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = geminiApiKey,
                                onValueChange = { geminiApiKey = it },
                                placeholder = "Gemini API Key...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Google Gemini" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "gemini"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 2: Groq API
                    val isGroqActive = selectedProvider == "groq"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isGroqActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isGroqActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("2. Groq API (Süper Hızlı)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isGroqActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = groqApiKey,
                                onValueChange = { groqApiKey = it },
                                placeholder = "gsk_...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Groq API" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "groq"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 3: Anthropic Claude
                    val isClaudeActive = selectedProvider == "claude"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isClaudeActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isClaudeActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = AppColors.Red, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("3. Anthropic Claude", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isClaudeActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.Red.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.Red, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = claudeApiKey,
                                onValueChange = { claudeApiKey = it },
                                placeholder = "sk-ant-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Anthropic" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "claude"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 4: OpenAI / DeepSeek
                    val isOpenaiActive = selectedProvider == "openai"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOpenaiActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isOpenaiActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = AppColors.TealDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("4. OpenAI & DeepSeek", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isOpenaiActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.TealDark.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.TealDark, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = openaiApiKey,
                                onValueChange = { openaiApiKey = it },
                                placeholder = "sk-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "OpenAI" || it.provider == "DeepSeek" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "openai"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 5: OpenRouter API
                    val isOpenRouterActive = selectedProvider == "openrouter"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOpenRouterActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF7C3AED).copy(alpha = 0.4f), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isOpenRouterActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = AppColors.Cyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("5. OpenRouter API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isOpenRouterActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.Cyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.Cyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = openRouterEnabled,
                                        onCheckedChange = { openRouterEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF0D1F2D), Color(0xFF091520)),
                                borderColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF06B6D4)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 OpenRouter Key Al: openrouter.ai/keys", color = AppColors.Cyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Ücretsiz kullanım için model adının sonuna ':free' ekleyebilir veya 'deepseek/deepseek-chat' kullanabilirsiniz.", color = Color.White, fontSize = 9.5.sp)
                                    Text("• Oran Sınırı: Ücretsiz katmanda günde ~50 istek.", color = EmochiTextSecondary, fontSize = 9.5.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = openRouterApiKey,
                                onValueChange = { openRouterApiKey = it },
                                placeholder = "sk-or-v1-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = openRouterModel,
                                onValueChange = {
                                    openRouterModel = it
                                    if (isOpenRouterActive) selectedModel = it
                                },
                                placeholder = "deepseek/deepseek-chat",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "DeepSeek Chat" to "deepseek/deepseek-chat",
                                    "DeepSeek R1 (Free)" to "deepseek/deepseek-r1:free",
                                    "Llama 3.3 70B (Free)" to "meta-llama/llama-3.3-70b-instruct:free",
                                    "Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            openRouterModel = code
                                            selectedProvider = "openrouter"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingOpenRouter = true
                                        openRouterTestResult = "Test ediliyor..."
                                        val res = viewModel.testOpenRouterConnection(openRouterApiKey, openRouterModel)
                                        isTestingOpenRouter = false
                                        openRouterTestResult = if (res.isSuccess) "✅ OpenRouter bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingOpenRouter && openRouterApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingOpenRouter) "Test ediliyor..." else "⚡ OpenRouter Test Et", color = AppColors.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            openRouterTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 6: NVIDIA NIM API
                    val isNvidiaActive = selectedProvider == "nvidia"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isNvidiaActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF7C3AED).copy(alpha = 0.4f), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isNvidiaActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = AppColors.NvidiaGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("6. NVIDIA NIM API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isNvidiaActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.NvidiaGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.NvidiaGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = nvidiaEnabled,
                                        onCheckedChange = { nvidiaEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF0A1F10), Color(0xFF061408)),
                                borderColors = listOf(Color(0xFF76B900), Color(0xFF4A7500), Color(0xFF76B900)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 NVIDIA NIM Key Al: build.nvidia.com", color = AppColors.NvidiaGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Model formatı: sağlayıcı/model (ör. deepseek-ai/deepseek-v4-flash, mistralai/mistral-nemotron).", color = Color.White, fontSize = 9.5.sp)
                                    Text("⚠️ Limit: Dakikada 40 istek (40 RPM) tüm modeller toplamında geçerlidir. Kredi biterse HTTP 402 verir.", color = AppColors.Hex_FFCC00, fontSize = 9.5.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = nvidiaApiKey,
                                onValueChange = { nvidiaApiKey = it },
                                placeholder = "nvapi-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = nvidiaModel,
                                onValueChange = {
                                    nvidiaModel = it
                                    if (isNvidiaActive) selectedModel = it
                                },
                                placeholder = "deepseek-ai/deepseek-v4-flash",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "DeepSeek V4 Flash" to "deepseek-ai/deepseek-v4-flash",
                                    "Mistral Nemotron" to "mistralai/mistral-nemotron",
                                    "Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            nvidiaModel = code
                                            selectedProvider = "nvidia"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingNvidia = true
                                        nvidiaTestResult = "Test ediliyor..."
                                        val res = viewModel.testNvidiaConnection(nvidiaApiKey, nvidiaModel)
                                        isTestingNvidia = false
                                        nvidiaTestResult = if (res.isSuccess) "✅ NVIDIA NIM bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingNvidia && nvidiaApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingNvidia) "Test ediliyor..." else "⚡ NVIDIA NIM Test Et", color = AppColors.NvidiaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            nvidiaTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 7: Mistral AI API
                    val isMistralActive = selectedProvider == "mistral"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isMistralActive)
                            listOf(Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.4f), Color(0xFFF59E0B))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isMistralActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("8. Mistral AI API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isMistralActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = mistralEnabled,
                                        onCheckedChange = { mistralEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF1A1000), Color(0xFF100A00)),
                                borderColors = listOf(Color(0xFFF59E0B), Color(0xFF92400E), Color(0xFFF59E0B)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 Mistral Key Al: console.mistral.ai", color = AppColors.WarningOrange, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Model adı: mistral-large-latest, open-mistral-nemo, codestral-latest", color = Color.White, fontSize = 9.5.sp)
                                    Text("⚠️ GİZLİLİK UYARISI: Bu ücretsiz katmanda verilerinizin Mistral tarafından model eğitiminde kullanılmasına izin vermiş olursunuz.", color = AppColors.Hex_FFD54F, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = mistralApiKey,
                                onValueChange = { mistralApiKey = it },
                                placeholder = "Mistral API Key...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = mistralModel,
                                onValueChange = {
                                    mistralModel = it
                                    if (isMistralActive) selectedModel = it
                                },
                                placeholder = "mistral-large-latest",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Mistral Large" to "mistral-large-latest",
                                    "Mistral Nemo" to "open-mistral-nemo",
                                    "Codestral" to "codestral-latest"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            mistralModel = code
                                            selectedProvider = "mistral"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingMistral = true
                                        mistralTestResult = "Test ediliyor..."
                                        val res = viewModel.testMistralConnection(mistralApiKey, mistralModel)
                                        isTestingMistral = false
                                        mistralTestResult = if (res.isSuccess) "✅ Mistral AI bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingMistral && mistralApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingMistral) "Test ediliyor..." else "⚡ Mistral AI Test Et", color = AppColors.WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            mistralTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 9: Özel API / Proxy (HER ZAMAN GÖRÜNÜR & BELİRGİN)
                    val isCustomActive = selectedProvider.startsWith("custom")
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isCustomActive)
                            listOf(Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.4f), Color(0xFFF59E0B))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isCustomActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("5. Özel API & OpenRouter Proxy", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isCustomActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "OpenRouter, VLLM, LM Studio, Ollama veya özel bir proxy adresi bağlayarak dilediğiniz yapay zeka modelinin kodunu (örn. Llama 3.3, DeepSeek R1, Qwen 2.5) yazıp kullanabilirsiniz.",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Direct Model Code Editing Field
                            Text("Özel Model Kodu / Adı (Elle Yazabilirsiniz):", color = AppColors.AmberSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            EmochiTextField(
                                value = selectedModel,
                                onValueChange = { newM ->
                                    selectedModel = newM
                                    if (selectedProvider.startsWith("custom_")) {
                                        val cId = selectedProvider.removePrefix("custom_").toLongOrNull()
                                        val activeP = customProviders.find { it.id == cId }
                                        if (activeP != null) {
                                            viewModel.updateCustomProviderModel(activeP.id, newM)
                                        }
                                    }
                                },
                                placeholder = "örn. meta-llama/llama-3.3-70b-instruct",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            // Quick Model Template Chips
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Popüler Model Kısayolları (Dokun & Doldur):", color = EmochiTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val quickChips = listOf(
                                "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                "🤖 GPT-4o Mini" to "openai/gpt-4o-mini"
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickChips.forEach { (lbl, code) ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmochiSurface)
                                            .border(1.dp, AppColors.WarningOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedModel = code
                                                if (selectedProvider.startsWith("custom_")) {
                                                    val cId = selectedProvider.removePrefix("custom_").toLongOrNull()
                                                    val activeP = customProviders.find { it.id == cId }
                                                    if (activeP != null) {
                                                        viewModel.updateCustomProviderModel(activeP.id, code)
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(lbl, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // GÖRÜNÜR & BELİRGİN EKLE BUTONU
                            Button(
                                onClick = { showAddCustomModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Yeni Özel API / Model Ekle", color = Color.Black, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            }

                            if (customProviders.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Kayıtlı Özel API Sağlayıcılarınız:", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))

                                customProviders.forEach { cp ->
                                    val isThisSelected = selectedProvider == "custom_${cp.id}"
                                    GradientCard(
                                        cornerRadius = 10.dp,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                        fillColors = if (isThisSelected) listOf(Color(0xFF1E0B3A), Color(0xFF120820)) else listOf(Color(0xFF130C2A), Color(0xFF0E0820)),
                                        borderColors = if (isThisSelected) listOf(Color(0xFF9333EA), Color(0xFF7C3AED), Color(0xFF9333EA)) else listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                                        borderWidth = if (isThisSelected) 1.8.dp else 1.2.dp
                                        ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(cp.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(EmochiBorder)
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(cp.apiFormat.uppercase(), color = EmochiTextSecondary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text("URL: ${cp.baseUrl}", color = EmochiTextMuted, fontSize = 9.5.sp, maxLines = 1)
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text("Model: ", color = EmochiTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        Text(cp.modelName, color = AppColors.Hex_4ADE80, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                if (isThisSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(AppColors.PurpleVibrant.copy(alpha = 0.3f))
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("KATMAN 0 AKTİF", color = AppColors.PurpleLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isThisSelected) AppColors.PurpleVibrant.copy(alpha = 0.15f) else EmochiCard)
                                                    .clickable {
                                                        selectedProvider = "custom_${cp.id}"
                                                        selectedModel = cp.modelName
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isThisSelected,
                                                    onClick = {
                                                        selectedProvider = "custom_${cp.id}"
                                                        selectedModel = cp.modelName
                                                    },
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = AppColors.PurpleVibrant,
                                                        unselectedColor = EmochiTextMuted
                                                    ),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isThisSelected) "Aktif Sağlayıcı (Katman 0 — Öncelikli)" else "Aktif Sağlayıcı Olarak Kullan",
                                                    color = if (isThisSelected) AppColors.PurpleLight else EmochiTextSecondary,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isThisSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = { quickModelEditProvider = cp },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, tint = AppColors.AmberSoft, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Modeli Yaz / Değiştir", color = AppColors.AmberSoft, fontSize = 9.5.sp)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                OutlinedButton(
                                                    onClick = { editingCustomProvider = cp },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Text("Düzenle", color = EmochiTextPrimary, fontSize = 9.5.sp)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                IconButton(
                                                    onClick = { viewModel.deleteCustomProvider(cp.id) },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Backup Gemini Key Field
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Yedek Gemini API Key:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    EmochiTextField(
                        value = backupApiKey,
                        onValueChange = { backupApiKey = it },
                        placeholder = "Yedek Gemini Key...",
                        visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        singleLine = true
                    )

                    // Active Selected Model Details Card
                    Spacer(modifier = Modifier.height(10.dp))
                    val activeCustomProvider = customProviders.find { "custom_${it.id}" == selectedProvider }
                    if (activeCustomProvider != null) {
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFFF59E0B), Color(0xFF92400E), Color(0xFFF59E0B)),
                            borderWidth = 1.5.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aktif Özel Model: ${activeCustomProvider.modelName}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(activeCustomProvider.label, color = AppColors.WarningOrange, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("URL: ${activeCustomProvider.baseUrl} | Format: ${activeCustomProvider.apiFormat.uppercase()}", color = EmochiTextSecondary, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { quickModelEditProvider = activeCustomProvider },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Farklı Model Kodu Yaz / Değiştir", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        val currentSelectedSpec = modelsList.find { it.key == selectedModel } ?: modelsList.first()
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981)),
                            borderWidth = 1.5.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = AppColors.ErrorRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aktif Seçili Model: ${currentSelectedSpec.name}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(currentSelectedSpec.tokenCostRate, color = AppColors.Emerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(currentSelectedSpec.description, color = EmochiTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                    }

                    // MODAL 1: YENİ ÖZEL SAĞLAYICI & MODEL EKLE
                    if (showAddCustomModal) {
                        var cLabel by remember { mutableStateOf("") }
                        var cBaseUrl by remember { mutableStateOf("https://openrouter.ai/api/v1") }
                        var cApiKey by remember { mutableStateOf("") }
                        var cModelName by remember { mutableStateOf("") }
                        var cApiFormat by remember { mutableStateOf("openai") }
                        var showKeyText by remember { mutableStateOf(false) }

                        var isTesting by remember { mutableStateOf(false) }
                        var testRes by remember { mutableStateOf<EmochiRepository.ProviderTestResult?>(null) }
                        val customModalScope = rememberCoroutineScope()

                        Dialog(onDismissRequest = { showAddCustomModal = false }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                     Text("🌐 Yeni Özel API & Model Ekle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    EmochiTextField(
                                        value = cLabel,
                                        onValueChange = { cLabel = it; testRes = null },
                                        label = "Sağlayıcı Etiketi (ör. OpenRouter, NVIDIA NIM)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("API Formatı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = cApiFormat == "openai",
                                            onClick = { cApiFormat = "openai"; testRes = null },
                                            label = { Text("OpenAI Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = cApiFormat == "anthropic",
                                            onClick = { cApiFormat = "anthropic"; testRes = null },
                                            label = { Text("Anthropic Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    EmochiTextField(
                                        value = cBaseUrl,
                                        onValueChange = { cBaseUrl = it; testRes = null },
                                        label = "Base URL (HTTPS zorunlu)",
                                        placeholder = "https://openrouter.ai/api/v1",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // URL Preset Chips
                                    Text("Hızlı Adres Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "OpenRouter" to "https://openrouter.ai/api/v1",
                                            "Groq Proxy" to "https://api.groq.com/openai/v1",
                                            "DeepSeek" to "https://api.deepseek.com/v1",
                                            "Ollama Local" to "http://localhost:11434/v1"
                                        ).forEach { (presetName, presetUrl) ->
                                            OutlinedButton(
                                                onClick = { cBaseUrl = presetUrl; testRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(presetName, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    EmochiTextField(
                                        value = cApiKey,
                                        onValueChange = { cApiKey = it; testRes = null },
                                        label = "API Key",
                                        visualTransformation = if (showKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            androidx.compose.material3.IconButton(onClick = { showKeyText = !showKeyText }) {
                                                Icon(if (showKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFCDA8FF))
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // MODEL SEÇİMİ VE EL İLE MODEL YAZMA ALANI
                                    EmochiTextField(
                                        value = cModelName,
                                        onValueChange = { cModelName = it; testRes = null },
                                        label = "Model Kodu / Adı (El ile Dilediğinizi Yazın)",
                                        placeholder = "meta-llama/llama-3.3-70b-instruct",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // MODEL HAZIR ŞABLON ŞİPLERİ
                                    Text("Popüler Model Kısayolları (Dokun & Doldur):", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini",
                                            "💨 Mistral Large" to "mistralai/mistral-large"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { cModelName = mCode; testRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    GradientCard(
                                        cornerRadius = 10.dp,
                                        fillColors = listOf(Color(0xFF1A1500), Color(0xFF100E00)),
                                        borderColors = listOf(Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFFFBBF24)),
                                        borderWidth = 1.2.dp
                                        ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Security, contentDescription = null, tint = AppColors.YellowAmber, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Güvenlik Uyarısı: Buraya girdiğin API key, doğrudan girdiğin adrese ($cBaseUrl) gönderilir. Güvendiğin ve HTTPS olan bir kaynak olduğundan emin ol. Key Keystore ile şifrelenir.",
                                                color = AppColors.Hex_FFE082,
                                                fontSize = 10.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }

                                    val currentTestRes = testRes
                                    if (currentTestRes != null) {
                                        GradientCard(
                                            cornerRadius = 10.dp,
                                            fillColors = if (currentTestRes.isSuccess) listOf(Color(0xFF001A0E), Color(0xFF00100A)) else listOf(Color(0xFF1A0000), Color(0xFF100000)),
                                            borderColors = if (currentTestRes.isSuccess) listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981)) else listOf(Color(0xFFEF4444), Color(0xFF7F1D1D), Color(0xFFEF4444))
                                            ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                if (currentTestRes.isSuccess) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Bağlantı Başarılı!", color = AppColors.LightGreenSoft, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        if (currentTestRes.supportsFunctionCalling) "✅ Bu sağlayıcı gelişmiş hafıza (tools/function calling) modunu destekliyor."
                                                        else "⚠️ Bu sağlayıcı gelişmiş hafıza özelliğini desteklemiyor olabilir, metin tabanlı yedek hafıza modu kullanılacak.",
                                                        color = Color.White,
                                                        fontSize = 10.5.sp
                                                    )
                                                } else {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Error, contentDescription = null, tint = AppColors.Red, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Bağlantı Başarısız", color = AppColors.Hex_E57373, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(currentTestRes.errorMessage, color = Color.White, fontSize = 10.5.sp)
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { showAddCustomModal = false },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                customModalScope.launch {
                                                    isTesting = true
                                                    testRes = viewModel.testCustomProviderConnection(cBaseUrl, cApiKey, cModelName, cApiFormat)
                                                    isTesting = false
                                                }
                                            },
                                            enabled = !isTesting && cBaseUrl.isNotBlank() && cApiKey.isNotBlank() && cModelName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange)
                                        ) {
                                            if (isTesting) {
                                                EmochiLoadingSpinner(size = 14.dp, small = true, color = Color.White)
                                            } else {
                                                Text("Bağlantıyı Test Et", fontSize = 10.5.sp, maxLines = 1)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val finalResult = testRes
                                            viewModel.saveCustomProvider(
                                                label = cLabel.ifBlank { "Özel API" },
                                                baseUrl = cBaseUrl,
                                                apiKey = cApiKey,
                                                modelName = cModelName,
                                                apiFormat = cApiFormat,
                                                supportsFunctionCalling = finalResult?.supportsFunctionCalling ?: true
                                            )
                                            selectedModel = cModelName
                                            showAddCustomModal = false
                                        },
                                        enabled = cBaseUrl.isNotBlank() && cApiKey.isNotBlank() && cModelName.isNotBlank(),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                    ) {
                                        Text("Kaydet ve Kullan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // MODAL 2: HIZLI MODEL DEĞİŞTİRME / YAZMA PENCERESİ
                    val currentQuickProvider = quickModelEditProvider
                    if (currentQuickProvider != null) {
                        var newMName by remember { mutableStateOf(currentQuickProvider.modelName) }
                        Dialog(onDismissRequest = { quickModelEditProvider = null }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("✏️ Model Kodunu Güncelle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Sağlayıcı: ${currentQuickProvider.label}", color = EmochiTextSecondary, fontSize = 11.sp)

                                    EmochiTextField(
                                        value = newMName,
                                        onValueChange = { newMName = it },
                                        label = "Model Kodu / Adı (El ile İstediğinizi Yazın)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("Hızlı Model Şablonları:", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini",
                                            "💨 Mistral Large" to "mistralai/mistral-large"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { newMName = mCode },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { quickModelEditProvider = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = {
                                                if (newMName.isNotBlank()) {
                                                    viewModel.updateCustomProviderModel(currentQuickProvider.id, newMName)
                                                    selectedModel = newMName.trim()
                                                    quickModelEditProvider = null
                                                }
                                            },
                                            enabled = newMName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                        ) {
                                            Text("Modeli Güncelle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MODAL 3: TAM SAĞLAYICI DÜZENLEME PENCERESİ
                    val currentEditProvider = editingCustomProvider
                    if (currentEditProvider != null) {
                        var eLabel by remember { mutableStateOf(currentEditProvider.label) }
                        var eBaseUrl by remember { mutableStateOf(currentEditProvider.baseUrl) }
                        var eApiKey by remember { mutableStateOf("") }
                        var eModelName by remember { mutableStateOf(currentEditProvider.modelName) }
                        var eApiFormat by remember { mutableStateOf(currentEditProvider.apiFormat) }
                        var showEKeyText by remember { mutableStateOf(false) }

                        var isETesting by remember { mutableStateOf(false) }
                        var eTestRes by remember { mutableStateOf<EmochiRepository.ProviderTestResult?>(null) }
                        val editModalScope = rememberCoroutineScope()

                        Dialog(onDismissRequest = { editingCustomProvider = null }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🛠️ Özel API Sağlayıcısını Düzenle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    EmochiTextField(
                                        value = eLabel,
                                        onValueChange = { eLabel = it; eTestRes = null },
                                        label = "Sağlayıcı Etiketi",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("API Formatı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = eApiFormat == "openai",
                                            onClick = { eApiFormat = "openai"; eTestRes = null },
                                            label = { Text("OpenAI Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = eApiFormat == "anthropic",
                                            onClick = { eApiFormat = "anthropic"; eTestRes = null },
                                            label = { Text("Anthropic Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    EmochiTextField(
                                        value = eBaseUrl,
                                        onValueChange = { eBaseUrl = it; eTestRes = null },
                                        label = "Base URL",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    EmochiTextField(
                                        value = eApiKey,
                                        onValueChange = { eApiKey = it; eTestRes = null },
                                        label = "Yeni API Key (Boş Bırakırsanız Değişmez)",
                                        visualTransformation = if (showEKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            androidx.compose.material3.IconButton(onClick = { showEKeyText = !showEKeyText }) {
                                                Icon(if (showEKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFCDA8FF))
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    EmochiTextField(
                                        value = eModelName,
                                        onValueChange = { eModelName = it; eTestRes = null },
                                        label = "Model Kodu / Adı (El ile Yazın)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { eModelName = mCode; eTestRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { editingCustomProvider = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                editModalScope.launch {
                                                    isETesting = true
                                                    val testKeyToUse = eApiKey.ifBlank { "existing_key" }
                                                    eTestRes = viewModel.testCustomProviderConnection(eBaseUrl, testKeyToUse, eModelName, eApiFormat)
                                                    isETesting = false
                                                }
                                            },
                                            enabled = !isETesting && eBaseUrl.isNotBlank() && eModelName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange)
                                        ) {
                                            if (isETesting) {
                                                EmochiLoadingSpinner(size = 14.dp, small = true, color = Color.White)
                                            } else {
                                                Text("Bağlantıyı Test Et", fontSize = 10.5.sp, maxLines = 1)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.updateCustomProviderFull(
                                                id = currentEditProvider.id,
                                                label = eLabel,
                                                baseUrl = eBaseUrl,
                                                apiKey = eApiKey.ifBlank { null },
                                                modelName = eModelName,
                                                apiFormat = eApiFormat,
                                                supportsFunctionCalling = eTestRes?.supportsFunctionCalling ?: currentEditProvider.supportsFunctionCalling
                                            )
                                            selectedModel = eModelName
                                            editingCustomProvider = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                    ) {
                                        Text("Değişiklikleri Kaydet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // OPTION 2: RESPONSE SETTINGS & FALLBACK
                CollapsibleSettingsOption(
                    title = "💬 Yanıt Ayarları & Yedek Model",
                    subtitle = "Varsayılan yanıt uzunluğu, yedek model ve otomatik geçiş",
                    icon = Icons.Default.Tune,
                    isExpanded = expandedSection == "length" || expandedSection == "response",
                    onToggle = { expandedSection = if (expandedSection == "length" || expandedSection == "response") null else "response" },
                    badgeText = if (enableAutoFallback) "AÇIK" else "KAPALI",
                    badgeColor = if (enableAutoFallback) AppColors.Emerald else EmochiTextMuted
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text("Varsayılan Yanıt Uzunluğu:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val options = listOf(
                                    Triple("short", "Kısa", Icons.Default.FlashOn),
                                    Triple("orta", "Orta", Icons.Default.AutoAwesome),
                                    Triple("standard", "Standart", Icons.Default.Description),
                                    Triple("long", "Uzun", Icons.Default.List)
                                )
                                options.forEach { (key, label, icon) ->
                                    val isSelected = responseLength == key
                                    val tintColor = if (key == "orta") AppColors.MintGreen else if (key == "long") AppColors.PrimaryPurple else AppColors.Gold
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) AppColors.DeepPurple.copy(alpha = 0.15f) else EmochiCard)
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AppColors.DeepPurple else EmochiBorder,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { responseLength = key }
                                            .padding(horizontal = 6.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = label,
                                                color = if (isSelected) Color.White else EmochiTextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Card for Automatic Fallback & Fallback Model
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                            borderWidth = 1.2.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = EmochiPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                "Otomatik Model/API Geçişi",
                                                color = EmochiTextPrimary,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Modelde hata veya kota aşımı olursa otomatik yedek modele geç.",
                                                color = EmochiTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = enableAutoFallback,
                                        onCheckedChange = { enableAutoFallback = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = AppColors.DarkSurface,
                                            checkedTrackColor = EmochiPrimary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = AppColors.CardPurple)
                                Spacer(modifier = Modifier.height(10.dp))

                                 Text(
                                    "Yedek Model (Hata / Kota Aşımında Kullanılır):",
                                    color = EmochiTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                ExposedDropdownMenuBox(
                                    expanded = fallbackDropdownExpanded,
                                    onExpandedChange = { fallbackDropdownExpanded = !fallbackDropdownExpanded },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    val currentFallbackSpec = modelsList.find { it.key == fallbackModel } ?: modelsList.first()
                                    OutlinedTextField(
                                        value = "${currentFallbackSpec.name} — ${currentFallbackSpec.tokenCostRate}",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fallbackDropdownExpanded) },
                                        colors = customTextFieldColors(),
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = fallbackDropdownExpanded,
                                        onDismissRequest = { fallbackDropdownExpanded = false },
                                        modifier = Modifier.background(EmochiCard)
                                    ) {
                                        modelsList.forEach { spec ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(spec.name, color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        Text(spec.tokenCostRate, color = EmochiPrimary, fontSize = 10.5.sp)
                                                    }
                                                },
                                                onClick = {
                                                    fallbackModel = spec.key
                                                    fallbackDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = AppColors.CardPurple)
                                Spacer(modifier = Modifier.height(8.dp))

                                // FALLBACK CHAIN REORDERING UI
                                Text("🔀 Fallback Sıralaması ve Öncelik Zinciri:", color = EmochiPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("Sağlayıcılar yukarıdan aşağıya doğru sırayla denenir. Ok butonları ile öncelik sırasını değiştirebilirsiniz.", color = EmochiTextMuted, fontSize = 10.sp)

                                val customListForChain by viewModel.customProviders.collectAsState()
                                val customMapForChain = customListForChain.associateBy { "custom_${it.id}" }

                                val currentItems = if (fallbackChainOrder.isNotBlank()) {
                                    fallbackChainOrder.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                } else {
                                    val def = mutableListOf("llm7", "pollinations", "ovh", "main")
                                    customListForChain.forEach { cp -> def.add("custom_${cp.id}") }
                                    def.add("autofallback")
                                    def
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                    currentItems.forEachIndexed { index, itemKey ->
                                        val displayLabel = when {
                                            itemKey == "llm7" -> "1. LLM7 (Ücretsiz Servis)"
                                            itemKey == "pollinations" -> "2. Pollinations (${pollinationsModel.ifBlank { "openai" }})"
                                            itemKey == "ovh" -> "3. OVH AI (${ovhModel})"
                                            itemKey == "main" -> "4. Ana Seçili Sağlayıcı ($selectedProvider)"
                                            itemKey == "autofallback" -> "5. Otomatik Gemini Fallback ($fallbackModel)"
                                            itemKey.startsWith("custom_") -> {
                                                val cp = customMapForChain[itemKey]
                                                if (cp != null) "Özel: ${cp.label} (${cp.modelName})" else "Özel Sağlayıcı ($itemKey)"
                                            }
                                            else -> itemKey
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(AppColors.Hex_1E1638)
                                                .border(1.dp, AppColors.Hex_382C5E, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${index + 1}. $displayLabel",
                                                color = Color.White,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        if (index > 0) {
                                                            val mutable = currentItems.toMutableList()
                                                            val tmp = mutable[index]
                                                            mutable[index] = mutable[index - 1]
                                                            mutable[index - 1] = tmp
                                                            fallbackChainOrder = mutable.joinToString(",")
                                                        }
                                                    },
                                                    enabled = index > 0,
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("▲", color = if (index > 0) EmochiPrimary else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        if (index < currentItems.size - 1) {
                                                            val mutable = currentItems.toMutableList()
                                                            val tmp = mutable[index]
                                                            mutable[index] = mutable[index + 1]
                                                            mutable[index + 1] = tmp
                                                            fallbackChainOrder = mutable.joinToString(",")
                                                        }
                                                    },
                                                    enabled = index < currentItems.size - 1,
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("▼", color = if (index < currentItems.size - 1) EmochiPrimary else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        fallbackChainOrder = ""
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp).align(Alignment.End)
                                ) {
                                    Text("Varsayılan Sıraya Sıfırla", fontSize = 9.5.sp, color = EmochiTextMuted)
                                }
                            }
                        }
                    }
                }

                // OPTION 3: CONTENT FILTERS (NSFW)
                CollapsibleSettingsOption(
                    title = "🔞 İçerik Filtreleri (+18)",
                    subtitle = "Yetişkin içerik (NSFW), Flirty, Hardcore, Fetish, Dark RP filtreleri",
                    icon = Icons.Default.Warning,
                    isExpanded = expandedSection == "nsfw",
                    onToggle = { expandedSection = if (expandedSection == "nsfw") null else "nsfw" },
                    badgeText = if (enableNsfw) "AÇIK" else "KAPALI",
                    badgeColor = if (enableNsfw) AppColors.ErrorRed else EmochiTextMuted
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                            FilterToggleRow(label = "🔓  18+ (NSFW) Genel Kilidini Aç", checked = enableNsfw) { enableNsfw = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "💖  Çapkınlık (Flirty)", checked = enableFlirty) { enableFlirty = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🔥  Sert Mod (Hardcore)", checked = enableHardcore) { enableHardcore = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🎭  Fantezi (Fetish)", checked = enableFetish) { enableFetish = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "👻  Karanlık (Dark RP)", checked = enableDarkRp) { enableDarkRp = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🍬  Romantik (Sweet)", checked = enableSweet) { enableSweet = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🐾  Vahşi (Primal)", checked = enablePrimal) { enablePrimal = it }
                        }
                    }
                }

                // OPTION 4: TEXT-TO-SPEECH (TTS)
                CollapsibleSettingsOption(
                    title = "🔊 Sesli Okuma (TTS)",
                    subtitle = "Sesli okuma aktivasyonu, okuma hızı ve ses tonu",
                    icon = Icons.Default.VolumeUp,
                    isExpanded = expandedSection == "tts",
                    onToggle = { expandedSection = if (expandedSection == "tts") null else "tts" }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sesli Okuma Özelliğini Etkinleştir", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = enableTts,
                            onCheckedChange = { enableTts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AppColors.DarkSurface, checkedTrackColor = EmochiPrimary)
                        )
                    }

                    if (enableTts) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(EmochiCard, RoundedCornerShape(12.dp))
                                .border(1.dp, EmochiBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚡ Okuma Hızı: ${"%.1f".format(ttsSpeed)}x", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0.8f to "0.8x", 1.0f to "1.0x", 1.25f to "1.25x", 1.5f to "1.5x").forEach { (sp, lbl) ->
                                    val sel = ttsSpeed == sp
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) EmochiPrimary.copy(alpha = 0.25f) else EmochiSurface)
                                            .border(1.dp, if (sel) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { ttsSpeed = sp }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(lbl, color = if (sel) EmochiPrimary else EmochiTextSecondary, fontSize = 10.5.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Text("🎵 Ses Tonu (Pitch): ${"%.1f".format(ttsPitch)}x", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0.8f to "Kalın (0.8)", 1.0f to "Normal (1.0)", 1.2f to "İnce (1.2)").forEach { (p, lbl) ->
                                    val sel = ttsPitch == p
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) EmochiPrimary.copy(alpha = 0.25f) else EmochiSurface)
                                            .border(1.dp, if (sel) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { ttsPitch = p }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(lbl, color = if (sel) EmochiPrimary else EmochiTextSecondary, fontSize = 10.5.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }

                // OPTION 5: LANGUAGE
                CollapsibleSettingsOption(
                    title = "🌐 Uygulama & Yanıt Dili",
                    subtitle = "Yapay zeka yanıtları için varsayılan dil tercihi",
                    icon = Icons.Default.Language,
                    isExpanded = expandedSection == "lang",
                    onToggle = { expandedSection = if (expandedSection == "lang") null else "lang" },
                    badgeText = if (appLanguage == "tr") "TÜRKÇE" else "ENGLISH"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        androidx.compose.material3.FilterChip(
                            selected = appLanguage == "tr",
                            onClick = { appLanguage = "tr" },
                            label = { Text("🇹🇷 Türkçe (Varsayılan)", color = if (appLanguage == "tr") AppColors.DarkSurface else EmochiTextPrimary, fontWeight = FontWeight.Bold) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(selectedContainerColor = EmochiPrimary, containerColor = EmochiCard)
                        )
                        androidx.compose.material3.FilterChip(
                            selected = appLanguage == "en",
                            onClick = { appLanguage = "en" },
                            label = { Text("🇬🇧 English", color = if (appLanguage == "en") AppColors.DarkSurface else EmochiTextPrimary, fontWeight = FontWeight.Bold) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(selectedContainerColor = EmochiPrimary, containerColor = EmochiCard)
                        )
                    }
                }

                // OPTION 6: TOKEN USAGE
                CollapsibleSettingsOption(
                    title = "📊 Token Kullanım İstatistikleri",
                    subtitle = "Toplam harcanan girdi ve çıktı token verileri",
                    icon = Icons.Default.Speed,
                    isExpanded = expandedSection == "tokens",
                    onToggle = { expandedSection = if (expandedSection == "tokens") null else "tokens" }
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Girdi Token", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("${settings.totalPromptTokens}", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Çıktı Token", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("${settings.totalCandidateTokens}", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Maliyet", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("Ücretsiz", color = AppColors.PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toplam Harcanan: $totalTokensUsed token",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // OPTION 7: BACKUP & RESTORE
                CollapsibleSettingsOption(
                    title = "💾 Yedekleme & Geri Yükleme",
                    subtitle = "Tüm botlarınızı ve verilerinizi yedekleyin veya içe aktarın",
                    icon = Icons.Default.Save,
                    isExpanded = expandedSection == "backup",
                    onToggle = { expandedSection = if (expandedSection == "backup") null else "backup" }
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        val json = onExportData()
                                        exportJson = json
                                        clipboardManager.setText(AnnotatedString(json))
                                        Toast.makeText(context, "Yedek panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                                        statusMessage = "Yedek kopyalandı."
                                    } catch (e: Exception) {
                                        statusMessage = "Hata: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📋 Kopyala", color = EmochiPrimary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { saveFileLauncher.launch("emochi_backup_${System.currentTimeMillis() / 1000}.json") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💾 Kaydet", color = EmochiPrimary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        val json = if (exportJson.isNotBlank()) exportJson else onExportData()
                                        exportJson = json
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, json)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Yedeği Paylaş"))
                                    } catch (e: Exception) {
                                        statusMessage = "Paylaşım hatası: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📤 Paylaş", color = EmochiPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("application/json") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📂 Dosya Seç (.json)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                clipboardManager.getText()?.let { text ->
                                    importJson = text.text
                                    Toast.makeText(context, "Metin panodan yapıştırıldı!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📋 Yapıştır", color = EmochiPrimary, fontSize = 11.sp)
                        }
                    }

                    if (importJson.isNotBlank()) {
                        OutlinedTextField(
                            value = importJson,
                            onValueChange = { importJson = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp).padding(top = 4.dp),
                            colors = customTextFieldColors()
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        onImportData(importJson)
                                        Toast.makeText(context, "Geri yükleme tamamlandı!", Toast.LENGTH_SHORT).show()
                                        statusMessage = "Veriler yüklendi."
                                        importJson = ""
                                    } catch (e: Exception) {
                                        statusMessage = "Hata: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            enabled = !isBusy,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✅ Yüklemeyi Başlat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    statusMessage?.let { msg ->
                        Text(text = msg, color = EmochiTextSecondary, fontSize = 11.5.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // OPTION 8: VERSION & UPDATES
                CollapsibleSettingsOption(
                    title = "🚀 Sürüm & Güncelleme",
                    subtitle = "Mevcut sürüm v1.4, güncellemeleri kontrol et",
                    icon = Icons.Default.AutoAwesome,
                    isExpanded = expandedSection == "updates",
                    onToggle = { expandedSection = if (expandedSection == "updates") null else "updates" },
                    badgeText = "v1.4"
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        fillColors = listOf(Color(0xFF12083A), Color(0xFF0C0525)),
                        borderColors = listOf(Color(0xFF7C3AED).copy(alpha = 0.5f), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF7C3AED).copy(alpha = 0.5f))
                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sürüm Kontrolü", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                Text("APK güncellemelerini denetleyin.", color = EmochiTextSecondary, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showUpdateModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Güncelle", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                    } // End of scrollable content Column

                    // FIXED FOOTER AT BOTTOM
                    Divider(color = EmochiBorder)
                    // ─── Özellikler butonu ────────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { showFeatureFlagsPanel = true }) {
                            Icon(
                                androidx.compose.material.icons.Icons.Filled.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = EmochiTextMuted
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Özellikler & Deneysel Ayarlar",
                                fontSize = 12.sp,
                                color = EmochiTextMuted
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.CardNight)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
                        ) {
                            Text("Vazgeç", color = EmochiTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(
                                    settings.copy(
                                        customApiKey = geminiApiKey.trim(),
                                        groqApiKey = groqApiKey.trim(),
                                        claudeApiKey = claudeApiKey.trim(),
                                        openaiApiKey = openaiApiKey.trim(),
                                        openRouterApiKey = openRouterApiKey.trim(),
                                        openRouterModel = openRouterModel.trim(),
                                        nvidiaApiKey = nvidiaApiKey.trim(),
                                        nvidiaModel = nvidiaModel.trim(),
                                        mistralApiKey = mistralApiKey.trim(),
                                        mistralModel = mistralModel.trim(),
                                        backupApiKey = backupApiKey.trim(),
                                        selectedProvider = selectedProvider,
                                        selectedModel = selectedModel,
                                        groqModel = if (selectedProvider == "groq") selectedModel else settings.groqModel,
                                        openaiModel = if (selectedProvider == "openai") selectedModel else settings.openaiModel,
                                        geminiModel = if (selectedProvider == "gemini") selectedModel else settings.geminiModel,
                                        claudeModel = if (selectedProvider == "claude") selectedModel else settings.claudeModel,
                                        fallbackModel = fallbackModel,
                                        responseLength = responseLength,
                                        enableNsfw = enableNsfw,
                                        enableFlirty = enableFlirty,
                                        enableHardcore = enableHardcore,
                                        enableFetish = enableFetish,
                                        enableDarkRp = enableDarkRp,
                                        enableSweet = enableSweet,
                                        enablePrimal = enablePrimal,
                                        enableAutoFallback = enableAutoFallback,
                                        fallbackChainOrder = fallbackChainOrder,
                                        enableLlm7 = enableLlm7,
                                        enablePollinations = enablePollinations,
                                        enableOvh = enableOvh,
                                        llm7Enabled = enableLlm7,
                                        pollinationsEnabled = enablePollinations,
                                        ovhEnabled = enableOvh,
                                        openRouterEnabled = openRouterEnabled,
                                        nvidiaEnabled = nvidiaEnabled,
                                        mistralEnabled = mistralEnabled,
                                        pollinationsModel = pollinationsModel,
                                        ovhModel = ovhModel,
                                        enableTts = enableTts,
                                        ttsSpeed = ttsSpeed,
                                        ttsPitch = ttsPitch,
                                        selectedVoiceName = selectedVoiceName,
                                        appLanguage = appLanguage
                                    )
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            modifier = Modifier
                                .weight(2f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ayarları Kaydet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } // End of outer Column
            } // End of Box
    } // End of Dialog

    if (showUpdateModal) {
        UpdateCheckerModal(onDismiss = { showUpdateModal = false })
    }

    if (showFallbackLogModal) {
        val logs = viewModel.providerFallbackLog.collectAsState().value
        FallbackLogModal(logs = logs, onDismiss = { showFallbackLogModal = false })
    }

    if (showFeatureFlagsPanel) {
        FeatureFlagsPanel(onDismiss = { showFeatureFlagsPanel = false })
    }
}
