package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotEntity
import com.example.data.local.EmotionState
import com.example.data.local.SensitiveTriggerEntity
import com.example.data.local.WorldAtmosphere
import com.example.data.repository.BaselineEmotionEngine
import com.example.data.repository.KeyCharacter
import com.example.ui.theme.AppColors
import com.example.ui.theme.EmochiError
import com.example.ui.theme.InterFontFamily
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CreateBotWizardScreen(
    onBack: () -> Unit,
    onFinish: (BotEntity) -> Unit,
    onGenerateOpening: suspend (BotEntity) -> String,
    onAnalyzeBaseline: (suspend (BotEntity) -> BaselineEmotionEngine.BaselineAnalysisResult)? = null
) {
    var stepIdx by remember { mutableIntStateOf(0) }

    var mode by remember { mutableStateOf("personal") }
    var aiName by remember { mutableStateOf("") }
    var aiPersonality by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var pinnedMemory by remember { mutableStateOf("") }
    var scenario by remember { mutableStateOf("") }
    var universeName by remember { mutableStateOf("") }
    var userCharName by remember { mutableStateOf("") }
    var userCharDesc by remember { mutableStateOf("") }
    var openingMessage by remember { mutableStateOf("") }
    var writingStyle by remember { mutableStateOf("rp") }
    var intensity by remember { mutableStateOf("normal") }
    var isPublic by remember { mutableStateOf(true) }

    var storyStartDate by remember { mutableStateOf("") }

    var keyCharacters by remember { mutableStateOf<List<KeyCharacter>>(emptyList()) }

    // Baseline analysis state (step 4 — review before writing style)
    var analysisResult by remember { mutableStateOf<BaselineEmotionEngine.BaselineAnalysisResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }
    var userEditedFields by remember { mutableStateOf<Set<String>>(emptySet()) }
    var reviewRelType by remember { mutableStateOf("") }
    var reviewAttachment by remember { mutableStateOf("secure") }
    var reviewSpeechStyle by remember { mutableStateOf("") }
    var reviewDominantEmotion by remember { mutableStateOf("") }
    var reviewTriggers by remember { mutableStateOf<List<SensitiveTriggerEntity>>(emptyList()) }
    var reviewBigFive by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var reviewAxes by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var isGeneratingOpening by remember { mutableStateOf(false) }
    var genError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { inputUri ->
            scope.launch {
                val localPath = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                val base64Uri = com.example.util.ImageStorageManager.toBase64DataUri(context, localPath)
                avatarUrl = base64Uri ?: localPath
            }
        }
    }

    val totalSteps = 6

    fun buildCurrentDraft(): BotEntity {
        val serializedCast = if (keyCharacters.isEmpty()) "[]" else {
            val array = org.json.JSONArray()
            for (char in keyCharacters) {
                val obj = org.json.JSONObject()
                obj.put("id", char.id)
                obj.put("name", char.name)
                obj.put("desc", char.desc)
                array.put(obj)
            }
            array.toString()
        }
        val baselineEmotion = EmotionState.calculateBaselineEmotionState(
            aiName = if (mode == "personal") aiName.trim() else universeName.trim(),
            personality = aiPersonality.trim(),
            scenario = scenario.trim(),
            userCharName = userCharName.trim(),
            userCharDesc = userCharDesc.trim()
        ).toJson()

        val initialWorldAtmosphere = if (mode == "universe") {
            val intensityInt = when (intensity) {
                "düşük" -> 3
                "yüksek" -> 8
                else -> 5
            }
            WorldAtmosphere(
                mood = "sakin ve beklentili",
                intensity = intensityInt,
                currentEvent = "Açılış sahnesi ve hikayenin başlangıcı",
                macroAtmosphere = "${universeName.trim()} evreninin genel düzeni ve toplumsal kuralları hakim.",
                microAtmosphere = "Hikayenin başladığı fiziki mekan ve başlangıç ortamı."
            ).toJson()
        } else ""

        return BotEntity(
            id = UUID.randomUUID().toString(),
            mode = mode,
            aiName = aiName.trim(),
            aiPersonality = aiPersonality.trim(),
            avatarUrl = avatarUrl.trim(),
            pinnedMemory = pinnedMemory.trim(),
            scenario = scenario.trim(),
            universeName = universeName.trim(),
            keyCharactersJson = serializedCast,
            userCharName = userCharName.trim().ifBlank { "Kullanıcı" },
            userCharDesc = userCharDesc.trim(),
            openingMessage = openingMessage.trim(),
            writingStyle = writingStyle,
            intensity = intensity,
            customLength = "default",
            isNsfw = true,
            isPublic = isPublic,
            emotionState = baselineEmotion,
            previousEmotionState = baselineEmotion,
            worldAtmosphere = initialWorldAtmosphere,
            storyCalendarDate = storyStartDate.trim(),
            hasExplicitStoryDate = storyStartDate.trim().isNotBlank(),
            updatedAt = System.currentTimeMillis()
        )
    }

    val canNext = when (stepIdx) {
        0 -> true
        1 -> if (mode == "personal") aiName.isNotBlank() && aiPersonality.isNotBlank() else universeName.isNotBlank()
        2 -> scenario.isNotBlank() && userCharName.isNotBlank()
        3 -> openingMessage.isNotBlank()
        4 -> !isAnalyzing
        5 -> true
        else -> true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wizardAnimations")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05020C))
    ) {
        // 1:1 Cosmic Nebula & Starfield Background matching reference screenshot
        WizardCosmicBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                val backInteraction = remember { MutableInteractionSource() }
                val backPressed by backInteraction.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (backPressed) 0.92f else 1f,
                    animationSpec = tween(90),
                    label = "wizardBackScale"
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Rounded Square Back Button (matching reference 1:1)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0C061C))
                                .border(
                                    width = 1.2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6D28D9).copy(alpha = 0.85f),
                                            Color(0xFF3B1068).copy(alpha = 0.65f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(interactionSource = backInteraction, indication = null) {
                                    if (stepIdx > 0) stepIdx-- else onBack()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFD8B4FE),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = "Bot Oluştur",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Pill Step Badge "1 / 6"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF12082A))
                                .border(
                                    width = 1.2.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF7C3AED).copy(alpha = 0.85f),
                                            Color(0xFF5B21B6).copy(alpha = 0.75f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 13.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${stepIdx + 1} / $totalSteps",
                                fontFamily = InterFontFamily,
                                color = Color(0xFFD8B4FE),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6 Equal-Length Horizontal Progress Bars (matching reference 1:1)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(totalSteps) { idx ->
                            val isActive = idx == stepIdx
                            val isDone = idx < stepIdx
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(4.5.dp)
                                    .then(
                                        if (isActive) {
                                            Modifier.drawBehind {
                                                drawIntoCanvas { canvas ->
                                                    val paint = androidx.compose.ui.graphics.Paint().apply {
                                                        style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                                        strokeWidth = 3.dp.toPx()
                                                        color = Color(0xFFC084FC).copy(alpha = 0.65f * pulseGlowAlpha)
                                                    }
                                                    paint.asFrameworkPaint().maskFilter =
                                                        android.graphics.BlurMaskFilter(8f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                                                    canvas.drawRoundRect(
                                                        0f,
                                                        0f,
                                                        size.width,
                                                        size.height,
                                                        50.dp.toPx(),
                                                        50.dp.toPx(),
                                                        paint
                                                    )
                                                }
                                            }
                                        } else Modifier
                                    )
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        when {
                                            isActive -> Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFE9D5FF),
                                                    Color(0xFFC084FC),
                                                    Color(0xFF9333EA)
                                                )
                                            )
                                            isDone -> Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF9333EA),
                                                    Color(0xFF6D28D9)
                                                )
                                            )
                                            else -> Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF1D1830),
                                                    Color(0xFF1D1830)
                                                )
                                            )
                                        }
                                    )
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Bar with Top Subtle Divider & Two Wide Pill Buttons (matching reference 1:1)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xEB070310))
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                ) {
                    // Top horizontal divider line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1E1035).copy(alpha = 0.5f),
                                        Color(0xFF4C1D95).copy(alpha = 0.65f),
                                        Color(0xFF1E1035).copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Wide Pill Button: "←  Geri"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF0A0517))
                                .border(
                                    width = 1.2.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF5B21B6).copy(alpha = 0.85f),
                                            Color(0xFF6D28D9).copy(alpha = 0.85f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                                .clickable {
                                    if (stepIdx > 0) stepIdx-- else onBack()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color(0xFFD8B4FE),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Geri",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Right Wide Pill Button: "İleri  →" / "Tamamla  ✓"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .then(
                                    if (canNext) {
                                        Modifier.drawBehind {
                                            drawIntoCanvas { canvas ->
                                                val paint = androidx.compose.ui.graphics.Paint().apply {
                                                    style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                                    strokeWidth = 3.dp.toPx()
                                                    color = Color(0xFFA855F7).copy(alpha = 0.65f * pulseGlowAlpha)
                                                }
                                                paint.asFrameworkPaint().maskFilter =
                                                    android.graphics.BlurMaskFilter(14f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                                                canvas.drawRoundRect(
                                                    0f,
                                                    0f,
                                                    size.width,
                                                    size.height,
                                                    50.dp.toPx(),
                                                    50.dp.toPx(),
                                                    paint
                                                )
                                            }
                                        }
                                    } else Modifier
                                )
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (canNext) {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF8B3DFF),
                                                Color(0xFFA855F7),
                                                Color(0xFFB967FF)
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF231242),
                                                Color(0xFF1A0D33)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    width = 1.2.dp,
                                    brush = if (canNext) {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFF3E8FF).copy(alpha = 0.9f),
                                                Color(0xFFA855F7).copy(alpha = 0.6f)
                                            )
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF3B1D6A),
                                                Color(0xFF28124C)
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(50)
                                )
                                .clickable(enabled = canNext) {
                                    if (stepIdx == 3) {
                                        stepIdx++
                                        val draft = buildCurrentDraft()
                                        scope.launch {
                                            isAnalyzing = true
                                            analysisError = null
                                            try {
                                                val res = onAnalyzeBaseline?.invoke(draft)
                                                    ?: BaselineEmotionEngine.calculateDeterministicFallback(
                                                        botId = draft.id,
                                                        aiName = draft.aiName,
                                                        personality = draft.aiPersonality,
                                                        scenario = draft.scenario,
                                                        userCharName = draft.userCharName,
                                                        userCharDesc = draft.userCharDesc
                                                    )
                                                analysisResult = res
                                                reviewRelType = res.relationshipType
                                                reviewAttachment = res.personalityProfile.attachmentStyle
                                                reviewSpeechStyle = res.speechPattern
                                                reviewDominantEmotion = res.baselineEmotionState.dominantEmotion
                                                reviewTriggers = res.sensitiveTriggers.toMutableList()
                                                reviewBigFive = mapOf(
                                                    "openness" to res.personalityProfile.openness,
                                                    "conscientiousness" to res.personalityProfile.conscientiousness,
                                                    "extraversion" to res.personalityProfile.extraversion,
                                                    "agreeableness" to res.personalityProfile.agreeableness,
                                                    "neuroticism" to res.personalityProfile.neuroticism
                                                )
                                                reviewAxes = mapOf(
                                                    "affection" to res.baselineEmotionState.affection,
                                                    "respect" to res.baselineEmotionState.relationshipAxes.respectScore,
                                                    "comfort" to res.baselineEmotionState.relationshipAxes.comfortScore,
                                                    "resentment" to res.baselineEmotionState.relationshipAxes.resentmentScore
                                                )
                                            } catch (_: Exception) {
                                                analysisError = "Analiz başarısız. Otomatik tahmin kullanıldı."
                                                val fb = BaselineEmotionEngine.calculateDeterministicFallback(
                                                    botId = draft.id,
                                                    aiName = draft.aiName,
                                                    personality = draft.aiPersonality,
                                                    scenario = draft.scenario,
                                                    userCharName = draft.userCharName,
                                                    userCharDesc = draft.userCharDesc
                                                )
                                                analysisResult = fb
                                                reviewRelType = fb.relationshipType
                                                reviewAttachment = fb.personalityProfile.attachmentStyle
                                                reviewSpeechStyle = fb.speechPattern
                                                reviewDominantEmotion = fb.baselineEmotionState.dominantEmotion
                                                reviewTriggers = fb.sensitiveTriggers.toMutableList()
                                                reviewBigFive = mapOf(
                                                    "openness" to fb.personalityProfile.openness,
                                                    "conscientiousness" to fb.personalityProfile.conscientiousness,
                                                    "extraversion" to fb.personalityProfile.extraversion,
                                                    "agreeableness" to fb.personalityProfile.agreeableness,
                                                    "neuroticism" to fb.personalityProfile.neuroticism
                                                )
                                                reviewAxes = mapOf(
                                                    "affection" to fb.baselineEmotionState.affection,
                                                    "respect" to fb.baselineEmotionState.relationshipAxes.respectScore,
                                                    "comfort" to fb.baselineEmotionState.relationshipAxes.comfortScore,
                                                    "resentment" to fb.baselineEmotionState.relationshipAxes.resentmentScore
                                                )
                                            } finally {
                                                isAnalyzing = false
                                            }
                                        }
                                    } else if (stepIdx < totalSteps - 1) {
                                        stepIdx++
                                    } else {
                                        val base = buildCurrentDraft()
                                        val res = analysisResult
                                        val finalBot = if (res != null) {
                                            val axes = com.example.data.local.RelationshipAxes(
                                                affectionScore = reviewAxes["affection"] ?: res.baselineEmotionState.affection,
                                                respectScore = reviewAxes["respect"] ?: res.baselineEmotionState.relationshipAxes.respectScore,
                                                comfortScore = reviewAxes["comfort"] ?: res.baselineEmotionState.relationshipAxes.comfortScore,
                                                resentmentScore = reviewAxes["resentment"] ?: res.baselineEmotionState.relationshipAxes.resentmentScore
                                            )
                                            val emotions = res.baselineEmotionState.primaryEmotions
                                            val editedState = res.baselineEmotionState.copy(
                                                relationshipAxes = axes,
                                                dominantEmotion = reviewDominantEmotion,
                                                speechPattern = reviewSpeechStyle,
                                                primaryEmotions = emotions
                                            )
                                            val emotionJson = editedState.toJson()
                                            base.copy(
                                                emotionState = emotionJson,
                                                previousEmotionState = emotionJson,
                                                baseAffectionDifficulty = res.difficultyMultiplier,
                                                emotionalRegulationCapacity = res.regulationCapacity,
                                                baselineEmotionAnalysisJson = res.rawJson.ifBlank { "" },
                                                baselineAnalysisSource = if (userEditedFields.isNotEmpty()) "user_edited" else res.analysisSource,
                                                baselineUserEditedFields = userEditedFields.joinToString(",")
                                            )
                                        } else {
                                            base
                                        }
                                        onFinish(finalBot)
                                    }
                                }
                                .testTag("wizard_next_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (stepIdx == totalSteps - 1) "Tamamla" else "İleri",
                                    fontFamily = InterFontFamily,
                                    color = if (canNext) Color.White else Color(0x66C4B5FD),
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = if (stepIdx == totalSteps - 1) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (canNext) Color.White else Color(0x66C4B5FD),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                when (stepIdx) {
                    // ── STEP 1 / 6: INTERACTION MODE (1:1 WITH REFERENCE IMAGE) ──
                    0 -> {
                        WizardStepHeader(
                            title = "Nasıl bir etkileşim\nistersiniz?",
                            subtitle = "AI karakter tipini ve hikaye yapısını seçin.",
                            glowAlpha = pulseGlowAlpha
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        WizardModeCard(
                            title = "Kişisel Karakter",
                            subtitle1 = "Sana özel bire bir sohbet.",
                            subtitle2 = "Tek ve sabit bir karakterle gerçek\nzamanlı bağ.",
                            iconVector = Icons.Default.Person,
                            isSelected = mode == "personal",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { mode = "personal" }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        WizardModeCard(
                            title = "Evren / Hikaye (RP)",
                            subtitle1 = "Kurgusal bir dünyada geçen\nroleplay.",
                            subtitle2 = "AI evrenin anlatıcısı olur ve\nkadroyu yönetir.",
                            iconVector = Icons.Default.MenuBook,
                            isSelected = mode == "universe",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { mode = "universe" }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── STEP 2 / 6: CHARACTER DETAILS OR UNIVERSE CAST ─────────
                    1 -> {
                        if (mode == "personal") {
                            WizardStepHeader(
                                title = "Karakterinizi nasıl\ntanımlarsınız?",
                                subtitle = "Karşınızdaki karakterin resmini, adını ve kişiliğini belirleyin.",
                                glowAlpha = pulseGlowAlpha
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Avatar Upload Card in Step 1's Neon Card Style
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF180A33),
                                                Color(0xFF0E0520)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF7C3AED).copy(alpha = 0.75f),
                                                Color(0xFF3B1068).copy(alpha = 0.55f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable { photoPickerLauncher.launch("image/*") }
                                    .padding(18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Squircle Avatar Box matching Step 1 Icon Box
                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF3B156E),
                                                        Color(0xFF1F0A3D)
                                                    )
                                                )
                                            )
                                            .border(
                                                width = 1.4.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFFC084FC),
                                                        Color(0xFF7C3AED)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        com.example.ui.components.BotAvatarImage(
                                            avatarUrl = avatarUrl.takeIf { it.isNotBlank() },
                                            contentDescription = "Avatar",
                                            modifier = Modifier.fillMaxSize(),
                                            fallback = {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFFDDD6FE),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Bot Profil Fotoğrafı",
                                            fontFamily = InterFontFamily,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = if (avatarUrl.isNotBlank()) "Kapak görseli seçildi • Değiştirmek için dokun" else "Galeriden özel bir portre veya kapak yükle.",
                                            fontFamily = InterFontFamily,
                                            color = Color(0xFFB8A9D9),
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (avatarUrl.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x33EF4444))
                                                .border(1.dp, EmochiError.copy(alpha = 0.7f), CircleShape)
                                                .clickable { avatarUrl = "" },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Sil",
                                                tint = EmochiError,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = Color(0xFFC084FC),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            WizardFieldLabel(title = "Karakter Adı", required = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            WizardNeonTextField(
                                value = aiName,
                                onValueChange = { aiName = it },
                                placeholder = "Ör. Aiden Blackwood",
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_name_input")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            WizardFieldLabel(title = "Kişilik ve Özellikler", required = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            WizardNeonTextField(
                                value = aiPersonality,
                                onValueChange = { aiPersonality = it },
                                placeholder = "Soğukkanlı mı, korumacı mı, konuşma tarzı ve geçmişi nasıl...",
                                singleLine = false,
                                minHeight = 136.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_personality_input")
                            )
                        } else {
                            WizardStepHeader(
                                title = "Evren ve karakter\nkadrosunu kurun",
                                subtitle = "Kurgusal dünyanın adını ve ana karakter kadrosunu belirleyin.",
                                glowAlpha = pulseGlowAlpha
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            WizardFieldLabel(title = "Evren Adı", required = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            WizardNeonTextField(
                                value = universeName,
                                onValueChange = { universeName = it },
                                placeholder = "Ör. Eldoria Krallığı veya Karanlık Akademi",
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            WizardFieldLabel(title = "Ana Karakter Kadrosu (Opsiyonel)", required = false)
                            Spacer(modifier = Modifier.height(8.dp))

                            keyCharacters.forEachIndexed { idx, char ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFF0E0620))
                                        .border(1.dp, Color(0xFF3B1A6A), RoundedCornerShape(18.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        WizardNeonTextField(
                                            value = char.name,
                                            onValueChange = { newName ->
                                                keyCharacters = keyCharacters.toMutableList().apply {
                                                    this[idx] = this[idx].copy(name = newName)
                                                }
                                            },
                                            placeholder = "İsim",
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        WizardNeonTextField(
                                            value = char.desc,
                                            onValueChange = { newDesc ->
                                                keyCharacters = keyCharacters.toMutableList().apply {
                                                    this[idx] = this[idx].copy(desc = newDesc)
                                                }
                                            },
                                            placeholder = "Rol / Tanım",
                                            singleLine = true,
                                            modifier = Modifier.weight(1.6f)
                                        )
                                        IconButton(
                                            onClick = {
                                                keyCharacters = keyCharacters.toMutableList().apply { removeAt(idx) }
                                            },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Sil",
                                                tint = EmochiError,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Add Character Card in Neon Style
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF190A34),
                                                Color(0xFF0E0520)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF7C3AED),
                                                Color(0xFFA855F7)
                                            )
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        keyCharacters = keyCharacters + KeyCharacter(name = "", desc = "")
                                    }
                                    .padding(vertical = 15.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFFD8B4FE),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Karakter Ekle",
                                        fontFamily = InterFontFamily,
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── STEP 3 / 6: SCENARIO & USER CHARACTER ──────────────────
                    2 -> {
                        WizardStepHeader(
                            title = "Senaryo ve kendi\nkarakteriniz",
                            subtitle = "Sahneye giriş bağlamını ve canlandıracağınız rolü tanımlayın.",
                            glowAlpha = pulseGlowAlpha
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        WizardFieldLabel(title = "Senaryo / Bağlam", required = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        WizardNeonTextField(
                            value = scenario,
                            onValueChange = { scenario = it },
                            placeholder = "Neredesiniz, aranızdaki ilişki ne, gerilim veya hikaye nasıl başladı...",
                            singleLine = false,
                            minHeight = 124.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scenario_input")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        WizardFieldLabel(title = "Sizin Canlandırdığınız Karakterin Adı", required = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        WizardNeonTextField(
                            value = userCharName,
                            onValueChange = { userCharName = it },
                            placeholder = "Ör. Zeynep",
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_char_name_input")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        WizardFieldLabel(title = "Karakterinizin Kısa Açıklaması (Opsiyonel)", required = false)
                        Spacer(modifier = Modifier.height(8.dp))
                        WizardNeonTextField(
                            value = userCharDesc,
                            onValueChange = { userCharDesc = it },
                            placeholder = "Yaş, görünüş, kişilik özellikleri veya hikayelerdeki konumunuz...",
                            singleLine = false,
                            minHeight = 104.dp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── STEP 4 / 6: OPENING SCENE ──────────────────────────────
                    3 -> {
                        WizardStepHeader(
                            title = "Başlangıç sahnesi\nnasıl açılsın?",
                            subtitle = "Sohbet bu açılış mesajı ve atmosfer betimlemesiyle başlayacak.",
                            glowAlpha = pulseGlowAlpha
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        WizardFieldLabel(title = "Açılış Mesajı / Sahnesi", required = true)
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            WizardNeonTextField(
                                value = openingMessage,
                                onValueChange = { if (it.length <= 1000) openingMessage = it },
                                placeholder = "Açılış mesajını kendiniz yazın veya aşağıdaki yapay zeka kartını kullanın...",
                                singleLine = false,
                                minHeight = 168.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("opening_message_input")
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 14.dp, bottom = 12.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF190A34))
                                    .border(0.8.dp, Color(0xFF5B21B6), RoundedCornerShape(50))
                                    .padding(horizontal = 9.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${openingMessage.length} / 1000",
                                    fontFamily = InterFontFamily,
                                    color = Color(0xFFC4B5FD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // AI Auto-Generate Card in Step 1's Neon Card Design
                        WizardModeCard(
                            title = if (isGeneratingOpening) "Yapay Zeka Sahneyi Yazıyor..." else "AI Otomatik Yazsın",
                            subtitle1 = "Senaryo ve karakter kişiliğine uygun sinematik açılış.",
                            subtitle2 = "Tek dokunuşla atmosferik ilk mesajı oluştur.",
                            iconVector = Icons.Default.AutoAwesome,
                            isSelected = true,
                            glowAlpha = pulseGlowAlpha,
                            onClick = {
                                if (!isGeneratingOpening) {
                                    scope.launch {
                                        isGeneratingOpening = true
                                        genError = null
                                        try {
                                            val generated = onGenerateOpening(buildCurrentDraft())
                                            openingMessage = generated
                                        } catch (e: Exception) {
                                            genError = e.message ?: "Oluşturulamadı."
                                        } finally {
                                            isGeneratingOpening = false
                                        }
                                    }
                                }
                            }
                        )

                        genError?.let { err ->
                            Text(
                                text = err,
                                fontFamily = InterFontFamily,
                                color = EmochiError,
                                fontSize = 12.5.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── STEP 5 / 6: EMOTIONAL PROFILE & BASELINE REVIEW ────────
                    4 -> {
                        WizardStepHeader(
                            title = "Duygusal profil ve\nilişki analizi",
                            subtitle = "Başlangıç duygu durumunu, bağlanma stilini ve kişilik eksenlerini inceleyin.",
                            glowAlpha = pulseGlowAlpha
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        if (isAnalyzing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color(0xFF100622))
                                    .border(1.2.dp, Color(0xFF6D28D9), RoundedCornerShape(22.dp))
                                    .padding(vertical = 36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    com.example.ui.components.EmochiLoadingSpinner(size = 36.dp)
                                    Spacer(Modifier.height(14.dp))
                                    Text(
                                        text = "Yapay Zeka Karakterinizi Analiz Ediyor…",
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFFD8B4FE),
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            val res = analysisResult
                            if (analysisError != null) {
                                Text(
                                    text = "⚠ $analysisError (Otomatik tahmin uygulandı)",
                                    fontFamily = InterFontFamily,
                                    color = EmochiError,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                            }
                            if (res != null) {
                                val (sourceBadge, sourceColor) = when (res.analysisSource) {
                                    "llm" -> Pair("✦ Yapay Zeka Analizi", Color(0xFFC084FC))
                                    "keyword" -> Pair("✦ Otomatik Tahmin", Color(0xFFA78BFA))
                                    else -> Pair("✦ Kullanıcı Düzenlemesi", Color(0xFFE9D5FF))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF1A0B36))
                                            .border(1.dp, sourceColor.copy(alpha = 0.65f), RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = sourceBadge,
                                            fontFamily = InterFontFamily,
                                            color = sourceColor,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (res.confidence < 60) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(Color(0xFF281510))
                                                .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(50))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "Güven: ${res.confidence}/100",
                                                fontFamily = InterFontFamily,
                                                color = Color(0xFFFBBF24),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Section Card Helper in Step 1 Neon Style
                                @Composable
                                fun WizardSectionCard(
                                    title: String,
                                    subtitle: String,
                                    content: @Composable () -> Unit
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(22.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color(0xFF14082A),
                                                        Color(0xFF0B0418)
                                                    )
                                                )
                                            )
                                            .border(
                                                width = 1.1.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF6D28D9).copy(alpha = 0.7f),
                                                        Color(0xFF2E1250).copy(alpha = 0.5f)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(22.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = title,
                                                fontFamily = InterFontFamily,
                                                color = Color.White,
                                                fontSize = 15.5.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = subtitle,
                                                fontFamily = InterFontFamily,
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 12.sp
                                            )
                                            Spacer(Modifier.height(12.dp))
                                            content()
                                        }
                                    }
                                }

                                // Relationship Type Selector
                                WizardSectionCard(
                                    title = "İlişki Türü",
                                    subtitle = "Karakter ile aranızdaki başlangıç bağı"
                                ) {
                                    val relTypes = BaselineEmotionEngine.VALID_REL_TYPES.toList()
                                    val relLabels = mapOf(
                                        "parent" to "Ebeveyn",
                                        "sibling" to "Kardeş",
                                        "friend" to "Arkadaş",
                                        "romantic" to "Sevgili",
                                        "ex_romantic" to "Eski Sevgili",
                                        "conflicted_romantic" to "Çatışmalı Sevgili",
                                        "rival" to "Rakip",
                                        "enemy" to "Düşman",
                                        "stranger" to "Yabancı",
                                        "authority" to "Otorite",
                                        "mentor" to "Mentor",
                                        "subordinate" to "Ast",
                                        "companion_creature" to "Yaratık/Evcil",
                                        "other" to "Diğer"
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(relTypes) { rt ->
                                            val isSel = reviewRelType == rt
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(
                                                        if (isSel) {
                                                            Brush.horizontalGradient(
                                                                listOf(Color(0xFF8B3DFF), Color(0xFFA855F7))
                                                            )
                                                        } else {
                                                            Brush.horizontalGradient(
                                                                listOf(Color(0xFF170B30), Color(0xFF110724))
                                                            )
                                                        }
                                                    )
                                                    .border(
                                                        width = if (isSel) 1.3.dp else 1.dp,
                                                        color = if (isSel) Color(0xFFE9D5FF) else Color(0xFF4C1D95),
                                                        shape = RoundedCornerShape(50)
                                                    )
                                                    .clickable {
                                                        reviewRelType = rt
                                                        userEditedFields = userEditedFields + "relationshipType"
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                            ) {
                                                Text(
                                                    text = relLabels[rt] ?: rt,
                                                    fontFamily = InterFontFamily,
                                                    color = if (isSel) Color.White else Color(0xFFC4B5FD),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Big Five Sliders
                                WizardSectionCard(
                                    title = "Kişilik Eksenleri (Big Five)",
                                    subtitle = "Karakterin psikolojik eğilim oranları"
                                ) {
                                    val bigFiveLabels = listOf(
                                        "openness" to "Açıklık",
                                        "conscientiousness" to "Sorumluluk",
                                        "extraversion" to "Dışadönüklük",
                                        "agreeableness" to "Uyumluluk",
                                        "neuroticism" to "Nevrotiklik"
                                    )
                                    bigFiveLabels.forEach { (key, label) ->
                                        val value = reviewBigFive[key] ?: 50
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = label,
                                                fontFamily = InterFontFamily,
                                                color = Color(0xFFD8B4FE),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.width(96.dp)
                                            )
                                            Slider(
                                                value = value.toFloat(),
                                                onValueChange = {
                                                    reviewBigFive = reviewBigFive + (key to it.toInt())
                                                    userEditedFields = userEditedFields + "bigFive"
                                                },
                                                valueRange = 0f..100f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFFE9D5FF),
                                                    activeTrackColor = Color(0xFFA855F7),
                                                    inactiveTrackColor = Color(0xFF261447)
                                                )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .width(38.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF1F0D3D))
                                                    .border(0.8.dp, Color(0xFF6D28D9), RoundedCornerShape(8.dp))
                                                    .padding(vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$value",
                                                    fontFamily = InterFontFamily,
                                                    color = Color.White,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Attachment Style
                                WizardSectionCard(
                                    title = "Bağlanma Stili",
                                    subtitle = "Yakınlık ve güven kurma biçimi"
                                ) {
                                    val attachStyles = listOf(
                                        "secure" to "Güvenli",
                                        "anxious" to "Kaygılı",
                                        "avoidant" to "Kaçınan",
                                        "disorganized" to "Düzensiz"
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        attachStyles.forEach { (key, label) ->
                                            val isSel = reviewAttachment == key
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(
                                                        if (isSel) {
                                                            Brush.horizontalGradient(
                                                                listOf(Color(0xFF8B3DFF), Color(0xFFA855F7))
                                                            )
                                                        } else {
                                                            Brush.horizontalGradient(
                                                                listOf(Color(0xFF170B30), Color(0xFF110724))
                                                            )
                                                        }
                                                    )
                                                    .border(
                                                        width = if (isSel) 1.3.dp else 1.dp,
                                                        color = if (isSel) Color(0xFFE9D5FF) else Color(0xFF4C1D95),
                                                        shape = RoundedCornerShape(14.dp)
                                                    )
                                                    .clickable {
                                                        reviewAttachment = key
                                                        userEditedFields = userEditedFields + "attachmentStyle"
                                                    }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontFamily = InterFontFamily,
                                                    color = if (isSel) Color.White else Color(0xFFC4B5FD),
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Dominant Emotion & Speech Style
                                WizardSectionCard(
                                    title = "Baskın Duygu & Konuşma Tarzı",
                                    subtitle = "İlk sahnedeki ton ve hitap biçimi"
                                ) {
                                    WizardFieldLabel(title = "Baskın Duygu", required = false)
                                    Spacer(Modifier.height(6.dp))
                                    WizardNeonTextField(
                                        value = reviewDominantEmotion,
                                        onValueChange = {
                                            reviewDominantEmotion = it
                                            userEditedFields = userEditedFields + "dominantEmotion"
                                        },
                                        placeholder = "Ör. Sakin ve mesafeli",
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    WizardFieldLabel(title = "Konuşma Tarzı", required = false)
                                    Spacer(Modifier.height(6.dp))
                                    WizardNeonTextField(
                                        value = reviewSpeechStyle,
                                        onValueChange = {
                                            reviewSpeechStyle = it
                                            userEditedFields = userEditedFields + "speechStyle"
                                        },
                                        placeholder = "Ör. Kısa, net ve otoriter cümleler",
                                        singleLine = false,
                                        minHeight = 76.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                // Sensitive Triggers
                                WizardSectionCard(
                                    title = "Hassas Konular",
                                    subtitle = "Karakteri duygusal olarak tetikleyen unsurlar"
                                ) {
                                    reviewTriggers.forEachIndexed { idx, trig ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            WizardNeonTextField(
                                                value = trig.triggerTopic,
                                                onValueChange = { v ->
                                                    reviewTriggers = reviewTriggers.toMutableList().also {
                                                        it[idx] = trig.copy(triggerTopic = v)
                                                    }
                                                    userEditedFields = userEditedFields + "sensitiveTriggers"
                                                },
                                                placeholder = "Hassas konu...",
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    if (reviewTriggers.size > 1) {
                                                        reviewTriggers = reviewTriggers.toMutableList().also { it.removeAt(idx) }
                                                        userEditedFields = userEditedFields + "sensitiveTriggers"
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Sil",
                                                    tint = EmochiError,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (reviewTriggers.size < 3) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(Color(0xFF1D0D3A))
                                                .border(1.dp, Color(0xFF7C3AED), RoundedCornerShape(50))
                                                .clickable {
                                                    reviewTriggers = reviewTriggers + SensitiveTriggerEntity(
                                                        botId = "",
                                                        triggerTopic = "",
                                                        sourceDescription = "",
                                                        intensityMultiplier = 1.8
                                                    )
                                                    userEditedFields = userEditedFields + "sensitiveTriggers"
                                                }
                                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                text = "+ Konu Ekle",
                                                fontFamily = InterFontFamily,
                                                color = Color(0xFFD8B4FE),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Reanalyze / Reset Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF1A0B36))
                                            .border(1.dp, Color(0xFF7C3AED), RoundedCornerShape(50))
                                            .clickable {
                                                val draft = buildCurrentDraft()
                                                scope.launch {
                                                    isAnalyzing = true
                                                    analysisError = null
                                                    userEditedFields = emptySet()
                                                    try {
                                                        val r = onAnalyzeBaseline?.invoke(draft)
                                                            ?: BaselineEmotionEngine.calculateDeterministicFallback(
                                                                botId = draft.id,
                                                                aiName = draft.aiName,
                                                                personality = draft.aiPersonality,
                                                                scenario = draft.scenario,
                                                                userCharName = draft.userCharName,
                                                                userCharDesc = draft.userCharDesc
                                                            )
                                                        analysisResult = r
                                                        reviewRelType = r.relationshipType
                                                        reviewAttachment = r.personalityProfile.attachmentStyle
                                                        reviewSpeechStyle = r.speechPattern
                                                        reviewDominantEmotion = r.baselineEmotionState.dominantEmotion
                                                        reviewTriggers = r.sensitiveTriggers.toMutableList()
                                                        reviewBigFive = mapOf(
                                                            "openness" to r.personalityProfile.openness,
                                                            "conscientiousness" to r.personalityProfile.conscientiousness,
                                                            "extraversion" to r.personalityProfile.extraversion,
                                                            "agreeableness" to r.personalityProfile.agreeableness,
                                                            "neuroticism" to r.personalityProfile.neuroticism
                                                        )
                                                        reviewAxes = mapOf(
                                                            "affection" to r.baselineEmotionState.affection,
                                                            "respect" to r.baselineEmotionState.relationshipAxes.respectScore,
                                                            "comfort" to r.baselineEmotionState.relationshipAxes.comfortScore,
                                                            "resentment" to r.baselineEmotionState.relationshipAxes.resentmentScore
                                                        )
                                                    } catch (_: Exception) {
                                                        analysisError = "Yeniden analiz başarısız."
                                                    } finally {
                                                        isAnalyzing = false
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null,
                                                tint = Color(0xFFD8B4FE),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "Yeniden Analiz Et",
                                                fontFamily = InterFontFamily,
                                                color = Color.White,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF0E0620))
                                            .border(1.dp, Color(0xFF3B1A6A), RoundedCornerShape(50))
                                            .clickable {
                                                val fb = BaselineEmotionEngine.calculateDeterministicFallback(
                                                    botId = "",
                                                    aiName = aiName,
                                                    personality = aiPersonality,
                                                    scenario = scenario,
                                                    userCharName = userCharName,
                                                    userCharDesc = userCharDesc
                                                )
                                                analysisResult = fb
                                                reviewRelType = fb.relationshipType
                                                reviewAttachment = fb.personalityProfile.attachmentStyle
                                                reviewSpeechStyle = fb.speechPattern
                                                reviewDominantEmotion = fb.baselineEmotionState.dominantEmotion
                                                reviewTriggers = fb.sensitiveTriggers.toMutableList()
                                                reviewBigFive = mapOf(
                                                    "openness" to fb.personalityProfile.openness,
                                                    "conscientiousness" to fb.personalityProfile.conscientiousness,
                                                    "extraversion" to fb.personalityProfile.extraversion,
                                                    "agreeableness" to fb.personalityProfile.agreeableness,
                                                    "neuroticism" to fb.personalityProfile.neuroticism
                                                )
                                                reviewAxes = mapOf(
                                                    "affection" to fb.baselineEmotionState.affection,
                                                    "respect" to fb.baselineEmotionState.relationshipAxes.respectScore,
                                                    "comfort" to fb.baselineEmotionState.relationshipAxes.comfortScore,
                                                    "resentment" to fb.baselineEmotionState.relationshipAxes.resentmentScore
                                                )
                                                userEditedFields = emptySet()
                                                analysisError = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Varsayılana Dön",
                                            fontFamily = InterFontFamily,
                                            color = Color(0xFFC4B5FD),
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── STEP 6 / 6: WRITING STYLE, TONE & VISIBILITY ───────────
                    5 -> {
                        WizardStepHeader(
                            title = "Yazım tarzı ve\ngörünürlük ayarları",
                            subtitle = "Yanıtların biçimini, tematik yoğunluğunu ve gizliliğini seçin.",
                            glowAlpha = pulseGlowAlpha
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        WizardFieldLabel(title = "Yazım Formatı", required = false)
                        Spacer(modifier = Modifier.height(10.dp))

                        WizardModeCard(
                            title = "RP / Anlatı Tarzı",
                            subtitle1 = "Üçüncü şahıs roman anlatımı.",
                            subtitle2 = "Ortam ve mimik betimlemeleri + diyaloglar.",
                            iconVector = Icons.Default.MenuBook,
                            isSelected = writingStyle == "rp",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { writingStyle = "rp" }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        WizardModeCard(
                            title = "Sade Sohbet",
                            subtitle1 = "Doğal mesajlaşma tarzı.",
                            subtitle2 = "Kısa, samimi ve doğrudan yanıtlar.",
                            iconVector = Icons.Default.Person,
                            isSelected = writingStyle == "chat",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { writingStyle = "chat" }
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        WizardFieldLabel(title = "Tema Yoğunluğu", required = false)
                        Spacer(modifier = Modifier.height(10.dp))

                        WizardModeCard(
                            title = "Normal",
                            subtitle1 = "Dengeli ve yumuşak ton.",
                            subtitle2 = "Günlük hikaye akışı ve doğal dramatik yapı.",
                            iconVector = Icons.Default.Settings,
                            isSelected = intensity == "normal",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { intensity = "normal" }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        WizardModeCard(
                            title = "Yoğun",
                            subtitle1 = "Derin psikolojik gerilim ve karanlık temalar.",
                            subtitle2 = "Duygusal çatışmalar yumuşatılmadan aktarılır.",
                            iconVector = Icons.Default.AutoAwesome,
                            isSelected = intensity == "intense",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { intensity = "intense" }
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        WizardFieldLabel(title = "Hikâye Başlangıç Tarihi (İsteğe Bağlı)", required = false)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Boş bırakırsanız bot yalnızca Gün #N sayacını kullanır.",
                            fontFamily = InterFontFamily,
                            color = Color(0xFF9CA3AF),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        WizardNeonTextField(
                            value = storyStartDate,
                            onValueChange = { storyStartDate = it },
                            placeholder = "Ör. 2045-06-15 (yyyy-AA-gg)",
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        WizardFieldLabel(title = "Görünürlük & Yayınlama", required = false)
                        Spacer(modifier = Modifier.height(10.dp))

                        WizardModeCard(
                            title = "Herkese Açık (Keşfette Yayınla)",
                            subtitle1 = "Toplulukla paylaşılır.",
                            subtitle2 = "Botunuz Keşfet sekmesinde diğer kullanıcılara görünür.",
                            iconVector = Icons.Default.Public,
                            isSelected = isPublic,
                            glowAlpha = pulseGlowAlpha,
                            onClick = { isPublic = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        WizardModeCard(
                            title = "Sadece Bana Özel",
                            subtitle1 = "Tamamen gizli ve kişisel.",
                            subtitle2 = "Bot yalnızca sizin cihazınızda ve sohbetlerinizde gözükür.",
                            iconVector = Icons.Default.Lock,
                            isSelected = !isPublic,
                            glowAlpha = pulseGlowAlpha,
                            onClick = { isPublic = false }
                        )

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

/**
 * 1:1 Cosmic Nebula & Starfield Background matching `Neon_Mor_Temal__Bot_Olu_turma_Ekran__bf9738aa.png`.
 */
@Composable
private fun WizardCosmicBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep obsidian space base
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF06020F),
                    Color(0xFF080316),
                    Color(0xFF05020C)
                )
            )
        )

        // Top-right diagonal purple nebula dust cloud (matching screenshot top-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4C1D95).copy(alpha = 0.34f),
                    Color(0xFF2E1065).copy(alpha = 0.16f),
                    Color.Transparent
                ),
                center = Offset(w * 0.92f, h * 0.11f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w * 0.92f, h * 0.11f)
        )

        // Secondary mid-right soft violet mist
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF6D28D9).copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(w * 0.82f, h * 0.32f),
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.82f, h * 0.32f)
        )

        // Bottom-right diagonal cosmic nebula cloud (matching screenshot bottom-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4C1D95).copy(alpha = 0.36f),
                    Color(0xFF2E1065).copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(w * 0.85f, h * 0.78f),
                radius = w * 0.72f
            ),
            radius = w * 0.72f,
            center = Offset(w * 0.85f, h * 0.78f)
        )

        // Pinpoint cosmic stars scattered across the background
        val starPositions = listOf(
            Triple(0.84f, 0.06f, 1.3f),
            Triple(0.91f, 0.12f, 1.6f),
            Triple(0.76f, 0.15f, 1.1f),
            Triple(0.38f, 0.16f, 1.2f),
            Triple(0.85f, 0.25f, 2.0f),
            Triple(0.18f, 0.63f, 1.2f),
            Triple(0.45f, 0.66f, 1.1f),
            Triple(0.42f, 0.71f, 1.8f),
            Triple(0.75f, 0.69f, 1.3f),
            Triple(0.88f, 0.74f, 1.6f),
            Triple(0.68f, 0.79f, 1.4f),
            Triple(0.24f, 0.82f, 1.1f),
            Triple(0.79f, 0.85f, 1.7f)
        )
        starPositions.forEach { (fx, fy, rDp) ->
            drawCircle(
                color = Color(0xFFE9D5FF).copy(alpha = 0.55f),
                radius = rDp.dp.toPx(),
                center = Offset(w * fx, h * fy)
            )
        }
    }
}

/**
 * Left-aligned step header with the 4-pointed glowing purple/lavender star (`✦`),
 * 2-line bold Inter title, and muted subtitle matching the reference image 1:1.
 */
@Composable
private fun WizardStepHeader(
    title: String,
    subtitle: String,
    glowAlpha: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // 4-point glowing star icon (matching reference screenshot)
        Canvas(
            modifier = Modifier
                .size(26.dp)
                .offset(x = (-2).dp)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = size.width * 0.48f
            val innerR = size.width * 0.11f

            val starPath = Path().apply {
                moveTo(cx, cy - outerR)
                quadraticTo(cx + innerR * 0.5f, cy - innerR * 0.5f, cx + outerR, cy)
                quadraticTo(cx + innerR * 0.5f, cy + innerR * 0.5f, cx, cy + outerR)
                quadraticTo(cx - innerR * 0.5f, cy + innerR * 0.5f, cx - outerR, cy)
                quadraticTo(cx - innerR * 0.5f, cy - innerR * 0.5f, cx, cy - outerR)
                close()
            }

            drawPath(
                path = starPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF0ABFC),
                        Color(0xFFC084FC),
                        Color(0xFF60A5FA)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            // Bright core
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * glowAlpha),
                radius = size.width * 0.08f,
                center = Offset(cx, cy)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontFamily = InterFontFamily,
            color = Color.White,
            fontSize = 27.5.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 33.5.sp,
            letterSpacing = (-0.6).sp,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            fontFamily = InterFontFamily,
            color = Color(0xFF9CA3AF),
            fontSize = 14.2.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 19.sp,
            textAlign = TextAlign.Start
        )
    }
}

/**
 * 1:1 Selection Card matching `Kişisel Karakter` (selected) and `Evren / Hikaye (RP)` (unselected)
 * in `Neon_Mor_Temal__Bot_Olu_turma_Ekran__bf9738aa.png`.
 */
@Composable
private fun WizardModeCard(
    title: String,
    subtitle1: String,
    subtitle2: String,
    iconVector: ImageVector,
    isSelected: Boolean,
    glowAlpha: Float,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "modeCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(
                if (isSelected) {
                    Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                            // Outer neon violet border glow (STROKE only, matching reference 1:1)
                            val paint = androidx.compose.ui.graphics.Paint().apply {
                                style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                strokeWidth = 3.5.dp.toPx()
                                color = Color(0xFFA855F7).copy(alpha = 0.65f * glowAlpha)
                            }
                            paint.asFrameworkPaint().maskFilter =
                                android.graphics.BlurMaskFilter(18f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                            canvas.drawRoundRect(
                                0f,
                                0f,
                                size.width,
                                size.height,
                                22.dp.toPx(),
                                22.dp.toPx(),
                                paint
                            )
                        }
                    }
                } else Modifier
            )
            .clip(cardShape)
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF210D42),
                            Color(0xFF1A0A35),
                            Color(0xFF14072A)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D071E),
                            Color(0xFF090415)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 1.6.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE9D5FF),
                            Color(0xFFC084FC),
                            Color(0xFFA855F7),
                            Color(0xFFD8B4FE)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2F1C52),
                            Color(0xFF21123B)
                        )
                    )
                },
                shape = cardShape
            )
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Squircle Icon Box (58.dp x 58.dp, double-bordered when selected)
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .then(
                        if (isSelected) {
                            Modifier.drawBehind {
                                drawIntoCanvas { canvas ->
                                    val paint = androidx.compose.ui.graphics.Paint().apply {
                                        style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                        strokeWidth = 2.dp.toPx()
                                        color = Color(0xFFA855F7).copy(alpha = 0.5f * glowAlpha)
                                    }
                                    paint.asFrameworkPaint().maskFilter =
                                        android.graphics.BlurMaskFilter(10f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                                    canvas.drawRoundRect(
                                        0f,
                                        0f,
                                        size.width,
                                        size.height,
                                        16.dp.toPx(),
                                        16.dp.toPx(),
                                        paint
                                    )
                                }
                            }
                        } else Modifier
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF39156B),
                                    Color(0xFF240C47)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF170B30),
                                    Color(0xFF100722)
                                )
                            )
                        }
                    )
                    .border(
                        width = if (isSelected) 1.4.dp else 1.dp,
                        brush = if (isSelected) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFC084FC),
                                    Color(0xFF7C3AED)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4C1D95).copy(alpha = 0.7f),
                                    Color(0xFF2E1065).copy(alpha = 0.5f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFDDD6FE) else Color(0xFFA78BFA),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle1,
                    fontFamily = InterFontFamily,
                    color = if (isSelected) Color(0xFFC4B5FD) else Color(0xFF9CA3AF),
                    fontSize = 13.2.sp,
                    lineHeight = 18.sp
                )
                Text(
                    text = subtitle2,
                    fontFamily = InterFontFamily,
                    color = if (isSelected) Color(0xFFC4B5FD) else Color(0xFF9CA3AF),
                    fontSize = 13.2.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Check Circle (when selected) or ChevronRight (when unselected)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFF6D28D9)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun WizardFieldLabel(
    title: String,
    required: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            fontFamily = InterFontFamily,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp
        )
        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "*",
                fontFamily = InterFontFamily,
                color = Color(0xFFC084FC),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun WizardNeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
    minHeight: androidx.compose.ui.unit.Dp = 54.dp
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .height(minHeight)
            .then(
                if (isFocused) {
                    Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = androidx.compose.ui.graphics.Paint().apply {
                                style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                strokeWidth = 2.5.dp.toPx()
                                color = Color(0xFFA855F7).copy(alpha = 0.5f)
                            }
                            paint.asFrameworkPaint().maskFilter =
                                android.graphics.BlurMaskFilter(12f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                            canvas.drawRoundRect(
                                0f,
                                0f,
                                size.width,
                                size.height,
                                18.dp.toPx(),
                                18.dp.toPx(),
                                paint
                            )
                        }
                    }
                } else Modifier
            )
            .clip(shape)
            .background(
                if (isFocused) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B0B36),
                            Color(0xFF110624)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0E0720),
                            Color(0xFF0A0416)
                        )
                    )
                }
            )
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                brush = if (isFocused) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE9D5FF),
                            Color(0xFFC084FC),
                            Color(0xFFA855F7)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B1D68),
                            Color(0xFF251245)
                        )
                    )
                },
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontFamily = InterFontFamily,
                color = Color.White,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(Color(0xFFC084FC)),
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontFamily = InterFontFamily,
                            color = Color(0xFF6B5E87),
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
