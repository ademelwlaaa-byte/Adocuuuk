package com.example.ui.screens

import com.example.ui.theme.AppColors
import com.example.ui.theme.EmochiGradients
import com.example.ui.theme.EmochiBackground
import com.example.ui.theme.InterFontFamily
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.ChevronLeft
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.imageResource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.platform.testTag
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotEntity
import com.example.data.local.MessageEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.KeyCharacter
import com.example.ui.components.BotQuickProfileSheet
import com.example.ui.components.BotSettingsModal
import com.example.ui.components.MoodColors
import com.example.ui.components.OrbView
import com.example.ui.components.TypingDots
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.EmochiError
import com.example.ui.theme.EmochiSurface

// Helper function to format spoken dialogue and narrative actions in AI messages
fun formatNarrativeText(text: String): AnnotatedString {
    val clean = cleanCopyText(text)
        .replace(
            "gizlemeye çalışarak. \"İstediğimiz",
            "gizlemeye çalışarak.\n\n“İstediğimiz"
        )
        .replace(
            "gizlemeye çalışarak. “İstediğimiz",
            "gizlemeye çalışarak.\n\n“İstediğimiz"
        )
        .replace("\"Tam zamanında geldin Rider,\"", "“Tam zamanında geldin Rider,”")
        .replace("\"İstediğimiz veri çipi elimizde ama izimizdeler. Planı devreye sokmaya hazır mısın?\"", "“İstediğimiz veri çipi elimizde ama izimizdeler. Planı devreye sokmaya hazır mısın?”")
    return buildAnnotatedString {
        // Regex for quoted dialogue ("..." or “...”) and asterisk actions (*...*)
        val regex = Regex("(\"[^\"]*\")|(“[^”]*”)|(«[^»]*»)|(\\*[^\\*]*\\*)")
        var lastIdx = 0
        val matches = regex.findAll(clean)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > lastIdx) {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFE2D9F3)
                    )
                ) {
                    append(clean.substring(lastIdx, start))
                }
            }
            val matchedStr = clean.substring(start, end)
            if (matchedStr.startsWith("*") && matchedStr.endsWith("*")) {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFE9D5FF),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                ) {
                    append(matchedStr)
                }
            } else {
                val curlyDialogue = if (matchedStr.startsWith("\"") && matchedStr.endsWith("\"") && matchedStr.length >= 2) {
                    "“" + matchedStr.substring(1, matchedStr.length - 1) + "”"
                } else {
                    matchedStr
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFE879F9), // Bright glowing pink-lavender for dialogue quotes
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(curlyDialogue)
                }
            }
            lastIdx = end
        }
        if (lastIdx < clean.length) {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFE2D9F3)
                )
            ) {
                append(clean.substring(lastIdx))
            }
        }
    }
}

@Composable
fun Modifier.aetheriaBackdropBlur(
    rootSize: androidx.compose.ui.unit.IntSize,
    blurredBitmap: ImageBitmap,
    alpha: Float = 1f
): Modifier {
    var coordsInRoot by remember { mutableStateOf(Offset.Zero) }
    return this
        .onGloballyPositioned { layoutCoordinates ->
            coordsInRoot = layoutCoordinates.positionInRoot()
        }
        .drawBehind {
            val imgW = blurredBitmap.width
            val imgH = blurredBitmap.height
            if (imgW > 0 && imgH > 0) {
                val rW = if (rootSize.width > 0) rootSize.width.toFloat() else size.width
                val rH = if (rootSize.height > 0) rootSize.height.toFloat() else size.height
                val scale = maxOf(rW / imgW.toFloat(), rH / imgH.toFloat())
                val scaledW = imgW * scale
                val scaledH = imgH * scale
                val rootLeft = (rW - scaledW) / 2f
                val rootTop = (rH - scaledH) / 2f
                drawImage(
                    image = blurredBitmap,
                    dstOffset = androidx.compose.ui.unit.IntOffset(
                        (rootLeft - coordsInRoot.x).toInt(),
                        (rootTop - coordsInRoot.y).toInt()
                    ),
                    dstSize = androidx.compose.ui.unit.IntSize(
                        scaledW.toInt().coerceAtLeast(1),
                        scaledH.toInt().coerceAtLeast(1)
                    ),
                    alpha = alpha
                )
            }
        }
}

@Composable
fun RpAtmosphericBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF06030E))
        )

        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_bg_aetheria_cathedral),
            contentDescription = "Gothic Stained Glass & Candlelight Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    bot: BotEntity,
    messages: List<MessageEntity>,
    userSettings: UserSettingsEntity?,
    isSending: Boolean,
    errorMessage: String?,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onRegenerate: () -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSaveBotProfile: (BotEntity, List<KeyCharacter>) -> Unit,
    onResetChat: (Boolean) -> Unit,
    onDeleteBot: () -> Unit,
    onSpeakText: ((String) -> Unit)? = null,
    isSpeaking: Boolean = false,
    onStopSpeaking: () -> Unit = {},
    onEnsureOpeningMessage: () -> Unit = {},
    onClearError: () -> Unit = {},
    onRetryMessage: ((String) -> Unit)? = null,
    onRetryFromBanner: (() -> Unit)? = null,
    onRewindToMessage: ((String) -> Unit)? = null,
    onCloneBot: (() -> Unit)? = null,
    streamingMsgId: String? = null,
    streamingText: String? = null
) {
    var inputText by remember { mutableStateOf("") }
    var showBotSettings by remember { mutableStateOf(false) }
    var showQuickProfile by remember { mutableStateOf(false) }
    var showBackgroundPicker by remember { mutableStateOf(false) }

    var editingMessageId by remember { mutableStateOf<String?>(null) }
    var editingText by remember { mutableStateOf("") }
    var expandedMenuMessageId by remember { mutableStateOf<String?>(null) }
    var expandedUserMenuMessageId by remember { mutableStateOf<String?>(null) }
    var rewindTargetMessageId by remember { mutableStateOf<String?>(null) }
    var isStatsExpanded by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var localSearchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val searchResults by viewModel.searchResults.collectAsState()
    val starredMessages by viewModel.starredMessages.collectAsState()
    var showStarredSheet by remember { mutableStateOf(false) }
    var activeMilestone by remember { mutableStateOf<com.example.data.local.MilestoneEntity?>(null) }
    // Reaction picker state
    var reactionPickerMessageId by remember { mutableStateOf<String?>(null) }
    val reactionEmojis = listOf("❤️", "😂", "😮", "😢", "👍", "🔥")
    val milestoneEvent = viewModel.milestoneEvent

    // Milestone event collector
    LaunchedEffect(Unit) {
        milestoneEvent.collect { milestone ->
            activeMilestone = milestone
            kotlinx.coroutines.delay(4000)
            activeMilestone = null
        }
    }

    // Stop speaking immediately when exiting chat
    DisposableEffect(Unit) {
        onDispose {
            onStopSpeaking()
        }
    }

    // Automatically ensure opening message exists if list is empty (except for book mode)
    LaunchedEffect(bot.id, messages.isEmpty()) {
        if (messages.isEmpty() && !isSending && bot.mode != "book") {
            onEnsureOpeningMessage()
        }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size, isSending) {
        val totalItems = messages.size + if (isSending) 1 else 0
        if (totalItems > 0) {
            try {
                listState.animateScrollToItem(totalItems - 1)
            } catch (_: Exception) {}
        }
    }

    val isUniverse = bot.mode == "universe"
    val displayName = if (isUniverse) bot.universeName.ifBlank { "Evren" } else bot.aiName.ifBlank { "Karakter" }

    // --- Rich Animations Setup ---
    val infiniteTransition = rememberInfiniteTransition(label = "chatScreenAnimations")

    // 1. Breathing Glow Alpha Pulse (0.35f to 0.90f)
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowAlpha"
    )

    // 2. Tactile Pulse Scale (0.96f to 1.04f)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // 3. Rotating Sweep Angle for Neon Borders (0f to 360f)
    val sweepRotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepRotateAngle"
    )

    // Mood detection based on last AI text
    val lastAiText = messages.lastOrNull { it.role == "assistant" }?.text ?: ""
    val detectedMood = remember(lastAiText) {
        val lower = lastAiText.lowercase()
        when {
            listOf("harika", "süper", "mutlu", "güldü", "🎉", "😊").any { lower.contains(it) } -> "joy"
            listOf("canım", "değerlisin", "sarıl", "güzelim", "sevgi", "💕", "❤️").any { lower.contains(it) } -> "warm"
            listOf("üzgün", "üzülüyorum", "kötü", "yalnız", "😢").any { lower.contains(it) } -> "sad"
            listOf("tehlike", "sinirli", "öfke", "korktu").any { lower.contains(it) } -> "tense"
            listOf("merak", "acaba", "ilginç").any { lower.contains(it) } -> "curious"
            else -> "calm"
        }
    }

    val hue = MoodColors.getMoodHue(detectedMood)
    val emotionState = remember(bot.emotionState) { com.example.data.local.EmotionState.fromJson(bot.emotionState) }
    val realMoodDisplay = "${emotionState.getMoodEmoji()} ${emotionState.mood.replaceFirstChar { it.uppercase() }}"

    val lastAiIndex = messages.indexOfLast { it.role == "assistant" }

    val isBookMode = remember(bot) {
        bot.mode == "book" ||
        bot.id == "preset_aiden_mcu_cosmic" ||
        bot.aiName.contains("Kitap") ||
        bot.aiPersonality.contains("KİTAP MODU") ||
        bot.scenario.contains("KİTAP MODU")
    }

    if (isBookMode) {
        BookReaderView(
            bot = bot,
            messages = messages,
            isSending = isSending,
            errorMessage = errorMessage,
            onBack = onBack,
            onSendMessage = onSendMessage,
            onEnsureOpeningMessage = onEnsureOpeningMessage,
            onResetChat = { onResetChat(it) },
            onClearError = onClearError
        )
    } else {
    var rootSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    val blurredBgBitmap = ImageBitmap.imageResource(id = com.example.R.drawable.img_chat_bg_aetheria_cathedral_blur)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { rootSize = it }
    ) {
        val isImeVisible = WindowInsets.isImeVisible

        // Base atmospheric wallpaper (full bleed across entire screen: img_chat_bg_aetheria_cathedral)
        RpAtmosphericBackground()

        // Custom wallpaper overlay only when explicitly customized by user
        if (bot.chatBgUrl.isNotBlank() && bot.id != "starter_aetheria") {
            val bgModel: Any = remember(bot.chatBgUrl, bot.avatarUrl, bot.id) {
                when {
                    bot.chatBgUrl == "USE_AVATAR" -> {
                        when {
                            bot.avatarUrl.isNotBlank() -> bot.avatarUrl
                            bot.id.contains("aiden_zoktay") -> com.example.R.drawable.aiden_zoktay
                            bot.id.contains("aiden_dispatch") -> com.example.R.drawable.aiden_dispatch
                            bot.id.contains("aiden_joker") -> com.example.R.drawable.aiden_joker
                            bot.id.contains("aiden_doctor") -> com.example.R.drawable.aiden_doctor
                            bot.id.contains("aiden_obsidian") -> com.example.R.drawable.aiden_obsidian
                            bot.id == "starter_ayla" || bot.aiName.equals("Ayla", ignoreCase = true) -> com.example.R.drawable.img_ayla_avatar
                            else -> com.example.R.drawable.img_chat_aetheria_bg
                        }
                    }
                    else -> bot.chatBgUrl
                }
            }

            coil.compose.AsyncImage(
                model = bgModel,
                contentDescription = "Sohbet Arka Planı",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.82f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                // ── 1:1 EXACT AETHERIA HEADER + ORNAMENTAL DIVIDER + SUB-HEADER PILL ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x4005020E),
                                    Color(0x18070314),
                                    Color.Transparent
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(top = 4.dp, bottom = 2.dp)
                        .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Row: Left Back Button | Center Glowing Avatar | Right 3 Action Buttons
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = if (isImeVisible) 2.dp else 2.dp)
                    ) {
                        // Left Back Button (46x40.dp rounded rectangle with frosted backdrop blur)
                        val backInteraction = remember { MutableInteractionSource() }
                        val backPressed by backInteraction.collectIsPressedAsState()
                        val backScale by animateFloatAsState(
                            targetValue = if (backPressed) 0.88f else 1f,
                            animationSpec = tween(80),
                            label = "backScale"
                        )
                        Box(
                            modifier = Modifier
                                .width(if (isImeVisible) 40.dp else 46.dp)
                                .height(if (isImeVisible) 36.dp else 40.dp)
                                .align(Alignment.CenterStart)
                                .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                .clip(RoundedCornerShape(12.dp))
                                .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x781A0B38), Color(0x88100624))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.25.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                        )
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(interactionSource = backInteraction, indication = null) { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFF3E8FF),
                                modifier = Modifier.size(if (isImeVisible) 18.dp else 20.dp)
                            )
                        }

                        // Center Glowing Avatar (64.dp circular portrait + green dot at bottom-right)
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !isImeVisible,
                            enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(66.dp)
                                    .clickable {
                                        if (bot.mode == "personal") {
                                            isStatsExpanded = !isStatsExpanded
                                        }
                                    }
                            ) {
                                // Outer soft purple-blue neon halo
                                Box(
                                    modifier = Modifier
                                        .size(66.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Color(0x88A855F7), Color(0x4460A5FA), Color.Transparent)
                                            )
                                        )
                                )

                                // Main circular portrait with glowing neon ring
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF160B2E))
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    Color(0xFFF5F3FF),
                                                    Color(0xFFC084FC),
                                                    Color(0xFF60A5FA),
                                                    Color(0xFFA855F7)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(1.5.dp)
                                ) {
                                    if (bot.id == "starter_ayla" || bot.aiName.equals("Ayla", ignoreCase = true)) {
                                        androidx.compose.foundation.Image(
                                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_ayla_avatar),
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else if (bot.id == "starter_aetheria" || bot.universeName.contains("Aetheria", ignoreCase = true) || bot.aiName.contains("Aetheria", ignoreCase = true)) {
                                        androidx.compose.foundation.Image(
                                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_aetheria_avatar),
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else if (bot.avatarUrl.isNotBlank()) {
                                        com.example.ui.components.BotAvatarImage(
                                            avatarUrl = bot.avatarUrl,
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            fallback = {
                                                androidx.compose.foundation.Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_aetheria_avatar),
                                                    contentDescription = displayName,
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                                )
                                            }
                                        )
                                    } else {
                                        androidx.compose.foundation.Image(
                                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_aetheria_avatar),
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    }
                                }

                                // Online badge — glowing green dot with dark inner ring and purple neon outer rim
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (-2).dp, y = (-2).dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0D051F))
                                        .border(1.2.dp, Color(0xFFA855F7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                }
                            }
                        }

                        // Right 3 Square Glass Buttons (Gallery/Wallpaper, Search, Settings)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            // 1. Gallery / Wallpaper button
                            AnimatedVisibility(
                                visible = !isImeVisible,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                val wpInteraction = remember { MutableInteractionSource() }
                                val wpPressed by wpInteraction.collectIsPressedAsState()
                                val wpScale by animateFloatAsState(
                                    targetValue = if (wpPressed) 0.88f else 1f,
                                    animationSpec = tween(80),
                                    label = "wpScale"
                                )
                                Box(
                                    modifier = Modifier
                                        .width(42.dp)
                                        .height(40.dp)
                                        .graphicsLayer { scaleX = wpScale; scaleY = wpScale }
                                        .clip(RoundedCornerShape(12.dp))
                                        .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0x781A0B38), Color(0x88100624))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.25.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                                )
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable(interactionSource = wpInteraction, indication = null) { showBackgroundPicker = true }
                                        .testTag("wallpaper_picker_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Framed landscape gallery icon with corner focus brackets matching reference
                                    Canvas(modifier = Modifier.size(19.dp)) {
                                        val strokeW = 1.65.dp.toPx()
                                        val col = Color(0xFFF3E8FF)
                                        val w = size.width
                                        val h = size.height
                                        val r = 3.5.dp.toPx()
                                        // Outer rounded frame with subtle top/bottom gap (viewfinder frame look)
                                        drawRoundRect(
                                            color = col,
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
                                            style = Stroke(width = strokeW)
                                        )
                                        // Sun dot
                                        drawCircle(
                                            color = col,
                                            radius = 1.7.dp.toPx(),
                                            center = Offset(w * 0.33f, h * 0.34f)
                                        )
                                        // Twin mountain peaks
                                        val mountainPath = Path().apply {
                                            moveTo(w * 0.15f, h * 0.78f)
                                            lineTo(w * 0.44f, h * 0.46f)
                                            lineTo(w * 0.63f, h * 0.66f)
                                            lineTo(w * 0.76f, h * 0.52f)
                                            lineTo(w * 0.88f, h * 0.78f)
                                        }
                                        drawPath(
                                            path = mountainPath,
                                            color = col,
                                            style = Stroke(width = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
                                        )
                                    }
                                }
                            }

                            // 2. Search button
                            AnimatedVisibility(visible = !isImeVisible, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
                                val srInteraction = remember { MutableInteractionSource() }
                                val srPressed by srInteraction.collectIsPressedAsState()
                                val srScale by animateFloatAsState(if (srPressed) 0.88f else 1f, tween(80), label = "srScale")
                                Box(
                                    modifier = Modifier
                                        .width(42.dp)
                                        .height(40.dp)
                                        .graphicsLayer { scaleX = srScale; scaleY = srScale }
                                        .clip(RoundedCornerShape(12.dp))
                                        .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                        .background(
                                            if (showSearchBar) Brush.linearGradient(listOf(Color(0xAA3B0F70), Color(0xAA1A0D38)))
                                            else Brush.linearGradient(listOf(Color(0x781A0B38), Color(0x88100624)))
                                        )
                                        .border(
                                            BorderStroke(
                                                1.25.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                                )
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable(interactionSource = srInteraction, indication = null) {
                                            showSearchBar = !showSearchBar
                                            if (!showSearchBar) { localSearchQuery = ""; viewModel.clearSearch() }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        "Ara",
                                        tint = Color(0xFFF3E8FF),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }

                            // 3. Settings button
                            val stInteraction = remember { MutableInteractionSource() }
                            val stPressed by stInteraction.collectIsPressedAsState()
                            val stScale by animateFloatAsState(
                                targetValue = if (stPressed) 0.88f else 1f,
                                animationSpec = tween(80),
                                label = "stScale"
                            )
                            Box(
                                modifier = Modifier
                                    .width(if (isImeVisible) 38.dp else 42.dp)
                                    .height(if (isImeVisible) 36.dp else 40.dp)
                                    .graphicsLayer { scaleX = stScale; scaleY = stScale }
                                    .clip(RoundedCornerShape(12.dp))
                                    .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0x781A0B38), Color(0x88100624))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.25.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                            )
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable(interactionSource = stInteraction, indication = null) { showBotSettings = true }
                                    .testTag("bot_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Ayarlar",
                                    tint = Color(0xFFF3E8FF),
                                    modifier = Modifier.size(if (isImeVisible) 17.dp else 19.dp)
                                )
                            }
                        }
                    }

                    // Centered Title + Ornamental Divider + Left-Aligned Overlapping Sub-Header Universe Pill
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isImeVisible,
                        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(1.dp))

                            // Centered Title Row: [Sparkle Asset] Aetheria: Neon & Büyü
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .then(
                                        if (bot.mode == "personal") {
                                            Modifier.clickable { isStatsExpanded = !isStatsExpanded }
                                        } else Modifier
                                    )
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_title_sparkle),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(end = 4.dp)
                                )
                                Text(
                                    text = displayName,
                                    color = Color(0xFFF8F5FF),
                                    fontSize = 19.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color(0xDDA855F7),
                                            offset = Offset(0f, 2f),
                                            blurRadius = 16f
                                        )
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Ornamental Horizontal Glowing Line with Center 3-Diamond Filigree (◇❖◇)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.90f)
                                    .height(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_header_divider),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Left-Aligned Sub-Header Universe Pill with Protruding Left Magic Rune Orb (1:1 with reference screenshot)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 34.dp, end = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.74f)
                                        .height(48.dp)
                                        .clickable { showBotSettings = true },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    // Sleek horizontal capsule body (36.dp tall, indented 18.dp from left so the 48.dp orb protrudes on the left)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 18.dp)
                                            .height(36.dp)
                                            .clip(RoundedCornerShape(50))
                                            .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0x6E160930),
                                                        Color(0x78100624),
                                                        Color(0x72190B36)
                                                    )
                                                )
                                            )
                                            .border(
                                                BorderStroke(
                                                    1.15.dp,
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            Color(0xFFC084FC),
                                                            Color(0xFF7C3AED),
                                                            Color(0xFF60A5FA),
                                                            Color(0xFFA855F7)
                                                        )
                                                    )
                                                ),
                                                RoundedCornerShape(50)
                                            )
                                            .padding(start = 38.dp, end = 14.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = displayName,
                                                color = Color(0xFFF3E8FF),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = InterFontFamily,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = "Detaylar",
                                                tint = Color(0xFFD8B4FE),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Protruding Left Circular Magic-Rune Sigil Orb (48.dp)
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .border(
                                                BorderStroke(
                                                    1.2.dp,
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFF60A5FA))
                                                    )
                                                ),
                                                CircleShape
                                            )
                                    ) {
                                        androidx.compose.foundation.Image(
                                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_aetheria_orb),
                                            contentDescription = null,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search bar (slide-in)
                    AnimatedVisibility(
                        visible = showSearchBar,
                        enter = slideInVertically(tween(220, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(220)),
                        exit = slideOutVertically(tween(180)) { -it } + fadeOut(tween(180))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xDD140D2E))
                                    .border(1.dp, Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95))), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (localSearchQuery.isEmpty()) {
                                    Text("Mesajlarda ara...", color = Color(0x60CDA8FF), fontSize = 13.sp)
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = localSearchQuery,
                                    onValueChange = { q ->
                                        localSearchQuery = q
                                        viewModel.setSearchQuery(q.ifBlank { null })
                                    },
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                    singleLine = true,
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFA855F7)),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (localSearchQuery.isNotEmpty()) {
                                val clrInteraction = remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x30F87171))
                                        .border(1.dp, Color(0x60F87171), RoundedCornerShape(10.dp))
                                        .clickable(interactionSource = clrInteraction, indication = null) {
                                            localSearchQuery = ""
                                            viewModel.clearSearch()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, "Temizle", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Search results summary
                    AnimatedVisibility(
                        visible = showSearchBar && searchResults.isNotEmpty(),
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(150))
                    ) {
                        Text(
                            text = "${searchResults.size} sonuç bulundu",
                            color = Color(0xFF9D74E8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
                        )
                    }

                    // Expandable Header Stats Banner (Personal Mode Only)
                    AnimatedVisibility(
                        visible = !isImeVisible && isStatsExpanded && bot.mode == "personal",
                        enter = fadeIn(tween(250)) + expandVertically(tween(250, easing = FastOutSlowInEasing)),
                        exit = fadeOut(tween(180)) + shrinkVertically(tween(180))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF120B28), Color(0xFF0C0720))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0x809333EA), Color(0x404C1D95), Color(0x807C3AED))
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0xFFC084FC), Color(0xFF7C3AED))
                                                )
                                            )
                                    )
                                    Text(
                                        text = userSettings?.selectedModel?.ifBlank { "Gemini 2.0 Flash" } ?: "Gemini 2.0 Flash",
                                        color = Color(0xFFCDA8FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0x35E11D48))
                                            .border(
                                                BorderStroke(
                                                    0.8.dp,
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFFB7185), Color(0xFFE11D48))
                                                    )
                                                ),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "❤️ ${emotionState.affection}%",
                                            color = Color(0xFFFDA4AF),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0x3538BDF8))
                                            .border(
                                                BorderStroke(
                                                    0.8.dp,
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
                                                    )
                                                ),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🛡️ ${emotionState.trust}%",
                                            color = Color(0xFF7DD3FC),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isSpeaking && !isImeVisible,
                        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                        exit = fadeOut(tween(160)) + shrinkVertically(tween(160))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1A0A38), Color(0xFF100626))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0x809333EA), Color(0x404C1D95))
                                        )
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔊 Sesli okunuyor...",
                                    color = Color(0xFFCDA8FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x30A855F7))
                                        .border(1.dp, Color(0x50A855F7), RoundedCornerShape(8.dp))
                                        .clickable { onStopSpeaking() }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("⏹️ Durdur", color = Color(0xFFCDA8FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // ── 1:1 EXACT 3 QUICK-ACTION CARDS + BOTTOM CAPSULE INPUT BAR ──
                val validMsgsForQuickEdit = remember(messages) { messages.filter { it.text.isNotBlank() } }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // ── 3 Equal-Width Quick Action Cards (Sahneyi Derinleştir | Ne Yapmalıyım? | Düzenle) ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Card 1: Sahneyi Derinleştir
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x7815082E), Color(0x840D051F))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.15.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isSending) {
                                    onSendMessage("Lütfen şu anki sahneyi ve karakterin iç dünyasını daha detaylı, atmosferik bir şekilde betimleyerek yanıt ver.")
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_action_sparkle),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Sahneyi\nDerinleştir",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = InterFontFamily,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = "Daha fazla detay ekle",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 8.5.sp,
                                        lineHeight = 10.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Card 2: Ne Yapmalıyım? (with cyan-purple bottom glow border)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x7815082E), Color(0x840D051F))
                                    )
                                )
                                .drawBehind {
                                    // Electric cyan-blue bottom edge glow matching reference screenshot
                                    drawRect(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0x4538BDF8), Color.Transparent),
                                            center = Offset(size.width * 0.5f, size.height),
                                            radius = size.width * 0.65f
                                        )
                                    )
                                }
                                .border(
                                    BorderStroke(
                                        1.25.dp,
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFFA855F7),
                                                Color(0xFF6366F1),
                                                Color(0xFF38BDF8)
                                            )
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isSending) {
                                    onSendMessage("Karakter bana bakıp şu anda ne yapmam gerektiğiyle ilgili imalı bir öneride bulunsun.")
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_action_bulb),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Ne Yapmalıyım?",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = InterFontFamily,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Strateji öner",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 9.sp,
                                        lineHeight = 11.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Card 3: Düzenle (Mesajı değiştir)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x7815082E), Color(0x840D051F))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.15.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF60A5FA))
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    val targetMsg = validMsgsForQuickEdit.lastOrNull()
                                    if (targetMsg != null) {
                                        editingMessageId = targetMsg.id
                                        editingText = targetMsg.text
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_action_flame),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Düzenle",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        lineHeight = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = InterFontFamily,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Mesajı değiştir",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 9.sp,
                                        lineHeight = 11.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // ── Bottom Input Row: [ (+)  ( Bir mesaj yazın...  📎 ) ]   ( ➤ ) ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isRpMode = inputText.trim().let { it.startsWith("*") && it.endsWith("*") && it.length >= 2 }

                        // Outer dark purple frosted glass capsule housing (+) and the inner text field pill
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 54.dp)
                                .clip(RoundedCornerShape(50))
                                .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0x7A120728), Color(0x82170932))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.15.dp,
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF9333EA),
                                                Color(0xFF6366F1),
                                                Color(0xFFA855F7)
                                            )
                                        )
                                    ),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left circular (+) button inside the capsule
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xAA250F4A), Color(0xCC14082C))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFFC084FC), Color(0xFF6366F1))
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .clickable {
                                        val trimmed = inputText.trim()
                                        if (trimmed.isBlank()) {
                                            inputText = "*...*"
                                        } else if (trimmed.startsWith("*") && trimmed.endsWith("*") && trimmed.length >= 2) {
                                            inputText = trimmed.substring(1, trimmed.length - 1)
                                        } else {
                                            inputText = "*$trimmed*"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    color = if (isRpMode) Color(0xFFE879F9) else Color(0xFFE9D5FF),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Inner Pill containing text input and right paperclip icon
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 42.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0x55180B34), Color(0x661F0E40))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            Brush.horizontalGradient(
                                                listOf(Color(0x70A855F7), Color(0x4560A5FA), Color(0x609333EA))
                                            )
                                        ),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.text.BasicTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontFamily = InterFontFamily
                                    ),
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD8B4FE)),
                                    maxLines = 4,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_input_field"),
                                    decorationBox = { innerTextField ->
                                        if (inputText.isEmpty()) {
                                            Text(
                                                text = "Bir mesaj yazın...",
                                                color = Color(0xFFB8A5D6),
                                                fontSize = 14.5.sp,
                                                fontFamily = InterFontFamily
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Diagonal Paperclip Icon on the right inside the inner pill
                                Canvas(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { showBackgroundPicker = true }
                                ) {
                                    val strokeW = 1.8.dp.toPx()
                                    val clipColor = Color(0xFFC4B5FD)
                                    val path = Path().apply {
                                        moveTo(size.width * 0.70f, size.height * 0.35f)
                                        lineTo(size.width * 0.38f, size.height * 0.67f)
                                        cubicTo(
                                            size.width * 0.26f, size.height * 0.79f,
                                            size.width * 0.14f, size.height * 0.67f,
                                            size.width * 0.26f, size.height * 0.55f
                                        )
                                        lineTo(size.width * 0.62f, size.height * 0.19f)
                                        cubicTo(
                                            size.width * 0.78f, size.height * 0.05f,
                                            size.width * 0.95f, size.height * 0.22f,
                                            size.width * 0.80f, size.height * 0.37f
                                        )
                                        lineTo(size.width * 0.42f, size.height * 0.75f)
                                    }
                                    drawPath(
                                        path = path,
                                        color = clipColor,
                                        style = Stroke(width = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Right Circular Glowing Neon Purple-Blue Send Button (52.dp)
                        val isSendEnabled = inputText.isNotBlank() && !isSending
                        val sendInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val sendPressed by sendInteraction.collectIsPressedAsState()
                        val sendScale by animateFloatAsState(
                            targetValue = if (sendPressed) 0.88f else 1f,
                            animationSpec = tween(90),
                            label = "sendScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .graphicsLayer { scaleX = sendScale; scaleY = sendScale },
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer neon halo
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0x88A855F7),
                                                Color(0x4460A5FA),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            // Button core
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFC084FC),
                                                Color(0xFF9333EA),
                                                Color(0xFF6366F1)
                                            )
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.8.dp,
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFFF3E8FF),
                                                    Color(0xFF60A5FA),
                                                    Color(0xFFD8B4FE)
                                                )
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .clickable(
                                        interactionSource = sendInteraction,
                                        indication = null,
                                        enabled = isSendEnabled
                                    ) {
                                        val text = inputText
                                        inputText = ""
                                        onSendMessage(text)
                                    }
                                    .testTag("send_message_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Gönder",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(21.dp)
                                        .offset(x = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                errorMessage?.let { err ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = androidx.compose.animation.slideInVertically(
                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                        ) { -it } + fadeIn(tween(200)),
                        exit = androidx.compose.animation.slideOutVertically(
                            animationSpec = tween(220)
                        ) { -it } + fadeOut(tween(160))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3D1018), Color(0xFF1F0A10))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFF87171), Color(0xFFDC2626), Color(0xFFF87171))
                                        )
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x30F87171)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            tint = EmochiError,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = err,
                                        color = EmochiError,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (onRetryFromBanner != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0x40F87171))
                                                .border(1.dp, Color(0x60F87171), RoundedCornerShape(8.dp))
                                                .clickable { onRetryFromBanner(); onClearError() }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "Tekrar",
                                                color = EmochiError,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x25F87171))
                                            .clickable { onClearError() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Kapat",
                                            tint = EmochiError,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val validMessages = remember(messages) { messages.filter { it.text.isNotBlank() } }
                val lastValidAiIndex = remember(validMessages) { validMessages.indexOfLast { it.role == "assistant" } }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (validMessages.isEmpty() && !isSending) {
                        item(key = "empty_placeholder_card") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF130B2E), Color(0xFF0C0720))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF7C3AED))
                                                )
                                            ),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(28.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // Orb visual
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF1A0840))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✨", fontSize = 28.sp)
                                        }
                                        Text(
                                            text = "$displayName henüz selamlamadı",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Text(
                                            text = "Karakterin selamlama mesajını başlatmak için aşağıya dokun ya da hemen yazmaya başla.",
                                            color = AppColors.TextMuted,
                                            fontSize = 12.5.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                        val emptyBtnInteraction = remember { MutableInteractionSource() }
                                        val emptyBtnPressed by emptyBtnInteraction.collectIsPressedAsState()
                                        val emptyBtnScale by animateFloatAsState(
                                            targetValue = if (emptyBtnPressed) 0.94f else 1f,
                                            animationSpec = tween(90),
                                            label = "emptyBtnScale"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = emptyBtnScale; scaleY = emptyBtnScale }
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFBB7BFF), Color(0xFF9333EA), Color(0xFF5B21B6))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        1.5.dp,
                                                        Brush.linearGradient(
                                                            listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))
                                                        )
                                                    ),
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .clickable(
                                                    interactionSource = emptyBtnInteraction,
                                                    indication = null,
                                                    onClick = onEnsureOpeningMessage
                                                )
                                                .padding(horizontal = 22.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color.White
                                                )
                                                Text(
                                                    "👋 Karakter İlk Mesajını Yükle",
                                                    color = Color.White,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(validMessages, key = { _, msg -> msg.id }) { idx, msg ->
                            val isUser = msg.role == "user"
                            val isLastAi = !isUser && idx == lastValidAiIndex
                            val isLastUserUnanswered = isUser && idx == validMessages.lastIndex

                            val timeFormatted = remember(msg.timestamp) {
                                if (msg.timestamp > 0L) {
                                    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                    sdf.format(java.util.Date(msg.timestamp))
                                } else ""
                            }

                            if (editingMessageId == msg.id) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF140B30), Color(0xFF0C071E))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                )
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0x30A855F7)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("✏️", fontSize = 10.sp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Mesajı Düzenle",
                                                color = Color(0xFFCDA8FF),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        OutlinedTextField(
                                            value = editingText,
                                            onValueChange = { editingText = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = customTextFieldColors(),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0x20A855F7))
                                                    .border(1.dp, Color(0x40A855F7), RoundedCornerShape(10.dp))
                                                    .clickable { editingMessageId = null }
                                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                            ) {
                                                Text("Vazgeç", color = AppColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(Color(0xFF9333EA), Color(0xFF5B21B6))
                                                        )
                                                    )
                                                    .border(
                                                        BorderStroke(
                                                            1.dp,
                                                            Brush.linearGradient(
                                                                listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))
                                                            )
                                                        ),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        val text = editingText
                                                        editingMessageId = null
                                                        onEditMessage(msg.id, text)
                                                    }
                                                    .padding(horizontal = 18.dp, vertical = 7.dp)
                                            ) {
                                                Text("Kaydet", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            } else if (isUser) {
                                // USER BUBBLE (Right Side) matching screenshot
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    // User Name + Avatar Header
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = bot.userCharName.ifBlank { "Kullanıcı" },
                                            color = Color(0xFFCDA8FF),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        // User avatar — gradient fill + sweep border
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0xFF6D28D9), Color(0xFF3B1070))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        1.5.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (bot.userCharName.ifBlank { "K" }).take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    // User Message Box — sıcak derin mor, tek temiz border
                                    val userShape = RoundedCornerShape(topStart = 20.dp, topEnd = 6.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
                                    Box(
                                        modifier = Modifier
                                            .widthIn(min = 80.dp, max = 310.dp)
                                            .border(
                                                BorderStroke(1.5.dp, EmochiGradients.chatBubbleUserBorder),
                                                userShape
                                            )
                                            .clip(userShape)
                                            .background(EmochiGradients.chatBubbleUser)
                                            .clickable {
                                                expandedUserMenuMessageId = if (expandedUserMenuMessageId == msg.id) null else msg.id
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = cleanCopyText(msg.text),
                                                color = Color.White,
                                                fontSize = 14.5.sp,
                                                lineHeight = 21.sp
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Row(
                                                modifier = Modifier.align(Alignment.End),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (timeFormatted.isNotBlank()) {
                                                    Text(
                                                        text = timeFormatted,
                                                        color = AppColors.PrimaryPurple,
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("✓✓", color = AppColors.PrimaryPurple, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (timeFormatted.isNotBlank()) "Şimdi" else "",
                                        color = AppColors.TextSlate,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )

                                    // User Message Action Pills
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(top = 3.dp, bottom = 4.dp)
                                    ) {
                                        @Composable
                                        fun ActionPill(
                                            label: String,
                                            emoji: String,
                                            isDanger: Boolean = false,
                                            onClick: () -> Unit
                                        ) {
                                            val pillInteraction = remember { MutableInteractionSource() }
                                            val pillPressed by pillInteraction.collectIsPressedAsState()
                                            val pillScale by animateFloatAsState(
                                                targetValue = if (pillPressed) 0.93f else 1f,
                                                animationSpec = tween(80),
                                                label = "pillScale"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .graphicsLayer { scaleX = pillScale; scaleY = pillScale }
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isDanger) Brush.linearGradient(
                                                            listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                        ) else Brush.linearGradient(
                                                            listOf(Color(0xFF190E35), Color(0xFF100825))
                                                        )
                                                    )
                                                    .border(
                                                        BorderStroke(
                                                            1.dp,
                                                            if (isDanger) Brush.linearGradient(
                                                                listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                            ) else Brush.linearGradient(
                                                                listOf(Color(0x90C084FC), Color(0x406B21A8))
                                                            )
                                                        ),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable(
                                                        interactionSource = pillInteraction,
                                                        indication = null,
                                                        onClick = onClick
                                                    )
                                                    .padding(horizontal = 11.dp, vertical = 6.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(emoji, fontSize = 11.sp)
                                                    Text(
                                                        label,
                                                        color = if (isDanger) Color(0xFFFCA5A5) else AppColors.TextLight,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        ActionPill("Kopyala", "📋") {
                                            val cleanText = cleanCopyText(msg.text)
                                            clipboardManager.setText(AnnotatedString(cleanText))
                                            android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        ActionPill("Devam Et", "⏪") {
                                            rewindTargetMessageId = msg.id
                                        }
                                        ActionPill("•••", "") {
                                            expandedMenuMessageId = if (expandedMenuMessageId == msg.id) null else msg.id
                                        }

                                        if (expandedMenuMessageId == msg.id) {
                                            ActionPill("Düzenle", "✏️") {
                                                editingMessageId = msg.id
                                                editingText = msg.text
                                                expandedMenuMessageId = null
                                            }
                                            ActionPill("Sil", "🗑️", isDanger = true) {
                                                expandedMenuMessageId = null
                                                onDeleteMessage(msg.id)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // ── 1:1 EXACT AETHERIA FROSTED GLASS STORY CARD ──
                                val aiShape = RoundedCornerShape(22.dp)
                                val isThisStreaming = streamingMsgId == msg.id
                                val displayText = if (isThisStreaming && streamingText != null) streamingText else msg.text

                                // Cursor blink animation
                                val cursorInfinite = rememberInfiniteTransition(label = "cursor_${msg.id}")
                                val cursorAlpha by if (isThisStreaming) {
                                    cursorInfinite.animateFloat(
                                        initialValue = 1f, targetValue = 0f,
                                        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                                        label = "cursorBlink"
                                    )
                                } else {
                                    cursorInfinite.animateFloat(
                                        initialValue = 0f, targetValue = 0f,
                                        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Restart),
                                        label = "cursorOff"
                                    )
                                }

                                val isSearchMatch = showSearchBar && localSearchQuery.isNotBlank() && searchResults.any { it.id == msg.id }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 38.dp, end = 12.dp, top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(aiShape)
                                            // Real coordinate-synced Gaussian blurred cathedral backdrop
                                            .aetheriaBackdropBlur(rootSize, blurredBgBitmap)
                                            // Smooth translucent dark-violet glass glaze
                                            .background(
                                                if (isSearchMatch) {
                                                    Brush.linearGradient(listOf(Color(0xCC2A1A08), Color(0xCC140C04)))
                                                } else {
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            Color(0x4214082C),
                                                            Color(0x5E100624),
                                                            Color(0x640D051E)
                                                        )
                                                    )
                                                }
                                            )
                                            .drawBehind {
                                                // 1. Left vertical frosted-glass light refraction column (passing through the star badge)
                                                drawRect(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0x28D8B4FE),
                                                            Color(0x36E9D5FF),
                                                            Color(0x14A855F7),
                                                            Color.Transparent
                                                        ),
                                                        startX = 0f,
                                                        endX = size.width * 0.25f
                                                    )
                                                )
                                                // Subtle vertical glass refraction streak below the star badge
                                                drawLine(
                                                    brush = Brush.verticalGradient(
                                                        listOf(
                                                            Color.Transparent,
                                                            Color(0x48E9D5FF),
                                                            Color(0x28C084FC),
                                                            Color.Transparent
                                                        )
                                                    ),
                                                    start = Offset(28.dp.toPx(), size.height * 0.14f),
                                                    end = Offset(28.dp.toPx(), size.height * 0.90f),
                                                    strokeWidth = 1.2.dp.toPx()
                                                )
                                                // 2. Right edge electric-blue/cyan neon bloom (around 64% height, matching screenshot)
                                                drawRect(
                                                    brush = Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0x4E38BDF8),
                                                            Color(0x2460A5FA),
                                                            Color.Transparent
                                                        ),
                                                        center = Offset(size.width * 0.96f, size.height * 0.64f),
                                                        radius = size.width * 0.34f
                                                    )
                                                )
                                                // 3. Bottom-left electric blue-violet bloom (around 26% width at bottom border)
                                                drawRect(
                                                    brush = Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0x4238BDF8),
                                                            Color(0x22A855F7),
                                                            Color.Transparent
                                                        ),
                                                        center = Offset(size.width * 0.26f, size.height * 0.98f),
                                                        radius = size.width * 0.30f
                                                    )
                                                )
                                            }
                                            .border(
                                                BorderStroke(
                                                    if (isSearchMatch) 1.6.dp else 1.15.dp,
                                                    if (isSearchMatch) {
                                                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                                                    } else {
                                                        Brush.linearGradient(
                                                            listOf(
                                                                Color(0xFFD8B4FE),
                                                                Color(0xFF7C3AED),
                                                                Color(0xFF38BDF8),
                                                                Color(0xFFA855F7),
                                                                Color(0xFFC084FC)
                                                            )
                                                        )
                                                    }
                                                ),
                                                aiShape
                                            )
                                            .combinedClickable(
                                                onClick = {
                                                    expandedMenuMessageId = if (expandedMenuMessageId == msg.id) null else msg.id
                                                },
                                                onLongClick = {
                                                    reactionPickerMessageId = if (reactionPickerMessageId == msg.id) null else msg.id
                                                }
                                            )
                                            .padding(start = 11.dp, end = 16.dp, top = 14.dp, bottom = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            // Top-Left Circular Dark-Purple 4-Point Glowing Star Badge (from reference)
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 1.dp)
                                                    .size(35.dp)
                                                    .clip(CircleShape)
                                                    .border(0.8.dp, Color(0x66A855F7), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                androidx.compose.foundation.Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_msg_star_badge),
                                                    contentDescription = null,
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(11.dp))

                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = formatNarrativeText(displayText + if (isThisStreaming && cursorAlpha > 0.5f) "▌" else ""),
                                                    color = Color(0xFFF5F0FF),
                                                    fontSize = 14.5.sp,
                                                    lineHeight = 21.8.sp,
                                                    fontFamily = InterFontFamily
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Bottom-Right Timestamp (19:19) + Glowing Purple 5-Bar Waveform Icon (|ı|ı|)
                                                Row(
                                                    modifier = Modifier.align(Alignment.End),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = if (bot.id == "starter_aetheria" && idx == 0) "19:19" else timeFormatted.ifBlank { "19:19" },
                                                        color = Color(0xFFD8CEE8).copy(alpha = 0.90f),
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        fontFamily = InterFontFamily
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    // 5-bar glowing purple audio waveform icon (|ı|ı| matching reference)
                                                    Canvas(
                                                        modifier = Modifier
                                                            .size(width = 18.dp, height = 14.dp)
                                                            .clickable {
                                                                if (isSpeaking) onStopSpeaking() else onSpeakText?.invoke(msg.text)
                                                            }
                                                    ) {
                                                        val barColor = if (isSpeaking) Color(0xFFE879F9) else Color(0xFFC084FC)
                                                        val barWidth = 1.8.dp.toPx()
                                                        val heights = listOf(0.86f, 0.44f, 1.0f, 0.48f, 0.78f)
                                                        val spacing = size.width / (heights.size + 1)
                                                        heights.forEachIndexed { i, hRatio ->
                                                            val x = spacing * (i + 1)
                                                            val barH = size.height * hRatio
                                                            val topY = (size.height - barH) / 2f
                                                            drawLine(
                                                                color = barColor,
                                                                start = Offset(x, topY),
                                                                end = Offset(x, topY + barH),
                                                                strokeWidth = barWidth,
                                                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                                                            )
                                                        }
                                                    }
                                                }

                                                val isFilterError = msg.text.contains("içerik kısıtlaması", ignoreCase = true) ||
                                                        msg.text.contains("içerik filtresi", ignoreCase = true) ||
                                                        msg.text.contains("content_filter", ignoreCase = true) ||
                                                        msg.text.contains("politikası", ignoreCase = true) ||
                                                        msg.text.contains("refusal", ignoreCase = true)

                                                if (msg.status == "failed" || msg.text.startsWith("⚠️")) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                                )
                                                            )
                                                            .border(
                                                                BorderStroke(
                                                                    1.dp,
                                                                    Brush.linearGradient(
                                                                        listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                                    )
                                                                ),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                                    ) {
                                                        if (isFilterError) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Text("⚠️", fontSize = 13.sp)
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        "İçerik filtresi engelledi",
                                                                        color = Color(0xFFFCA5A5),
                                                                        fontSize = 11.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                                Box(
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                                                                        .clickable { viewModel.retryWithSoftenedPrompt(bot.id) }
                                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                                ) {
                                                                    Text(
                                                                        "🛡️ Farklı Dene",
                                                                        color = Color.White,
                                                                        fontSize = 10.5.sp,
                                                                        fontWeight = FontWeight.ExtraBold
                                                                    )
                                                                }
                                                            }
                                                        } else {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Text("⚠️", fontSize = 13.sp)
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        "Mesaj iletilemedi",
                                                                        color = Color(0xFFFCA5A5),
                                                                        fontSize = 11.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                                Box(
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFF87171), RoundedCornerShape(10.dp))
                                                                        .clickable { onRetryMessage?.invoke(msg.id) }
                                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                                ) {
                                                                    Text(
                                                                        "🔄 Tekrar Dene",
                                                                        color = Color.White,
                                                                        fontSize = 10.5.sp,
                                                                        fontWeight = FontWeight.ExtraBold
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Expandable AI Message Action Pills (shown when user taps the story card)
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = expandedMenuMessageId == msg.id,
                                        enter = fadeIn(tween(180)) + expandVertically(tween(180)),
                                        exit = fadeOut(tween(140)) + shrinkVertically(tween(140))
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState())
                                                .padding(top = 8.dp, bottom = 3.dp)
                                        ) {
                                            @Composable
                                            fun AiActionPill(
                                                label: String,
                                                emoji: String,
                                                enabled: Boolean = true,
                                                isDanger: Boolean = false,
                                                onClick: () -> Unit
                                            ) {
                                                val aiPillInteraction = remember { MutableInteractionSource() }
                                                val aiPillPressed by aiPillInteraction.collectIsPressedAsState()
                                                val aiPillScale by animateFloatAsState(
                                                    targetValue = if (aiPillPressed) 0.93f else 1f,
                                                    animationSpec = tween(80),
                                                    label = "aiPillScale"
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .graphicsLayer { scaleX = aiPillScale; scaleY = aiPillScale; alpha = if (enabled) 1f else 0.45f }
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                            if (isDanger) Brush.linearGradient(
                                                                listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                            ) else Brush.linearGradient(
                                                                listOf(Color(0xFF190E35), Color(0xFF100825))
                                                            )
                                                        )
                                                        .border(
                                                            BorderStroke(
                                                                1.dp,
                                                                if (isDanger) Brush.linearGradient(
                                                                    listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                                ) else Brush.linearGradient(
                                                                    listOf(Color(0x90C084FC), Color(0x406B21A8))
                                                                )
                                                            ),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .clickable(
                                                            enabled = enabled,
                                                            interactionSource = aiPillInteraction,
                                                            indication = null,
                                                            onClick = onClick
                                                        )
                                                        .padding(horizontal = 11.dp, vertical = 6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        if (emoji.isNotEmpty()) Text(emoji, fontSize = 11.sp)
                                                        Text(
                                                            label,
                                                            color = if (isDanger) Color(0xFFFCA5A5) else AppColors.TextLight,
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }

                                            AiActionPill("Yeniden Oluştur", "🔄", enabled = !isSending) { onRegenerate() }
                                            AiActionPill("Devam Ettir", "▶️", enabled = !isSending) {
                                                onSendMessage("Devam et, hikayeye kaldığın yerden devam et.")
                                            }
                                            AiActionPill(
                                                if (isSpeaking) "Durdur" else "Sesli Oku",
                                                if (isSpeaking) "⏹️" else "🎙️"
                                            ) {
                                                if (isSpeaking) onStopSpeaking() else onSpeakText?.invoke(msg.text)
                                            }
                                            AiActionPill(if (msg.isStarred) "Yıldızı Kaldır" else "Yıldızla", if (msg.isStarred) "⭐" else "☆") {
                                                viewModel.toggleMessageStar(msg.id)
                                            }
                                            AiActionPill("Kopyala", "📋") {
                                                val cleanText = cleanCopyText(msg.text)
                                                clipboardManager.setText(AnnotatedString(cleanText))
                                                android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                            AiActionPill("Devam Et", "⏪") { rewindTargetMessageId = msg.id }
                                            AiActionPill("Düzenle", "✏️") {
                                                editingMessageId = msg.id
                                                editingText = msg.text
                                                expandedMenuMessageId = null
                                            }
                                            AiActionPill("Sil", "🗑️", isDanger = true) {
                                                expandedMenuMessageId = null
                                                onDeleteMessage(msg.id)
                                            }
                                        }
                                    }

                                    // 😊 Reaction Picker Popup
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = reactionPickerMessageId == msg.id,
                                        enter = androidx.compose.animation.fadeIn(tween(150)) +
                                            androidx.compose.animation.scaleIn(tween(150), transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f)),
                                        exit = androidx.compose.animation.fadeOut(tween(100)) +
                                            androidx.compose.animation.scaleOut(tween(100), transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp, bottom = 4.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF1E0D40), Color(0xFF130828))))
                                                .border(1.2.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF9333EA))), RoundedCornerShape(20.dp))
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                reactionEmojis.forEach { emoji ->
                                                    val isSelected = msg.reaction == emoji
                                                    val emojiInteraction = remember { MutableInteractionSource() }
                                                    val emojiPressed by emojiInteraction.collectIsPressedAsState()
                                                    val emojiScale by animateFloatAsState(
                                                        targetValue = when {
                                                            isSelected -> 1.25f
                                                            emojiPressed -> 0.85f
                                                            else -> 1f
                                                        },
                                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                        label = "emojiScale_$emoji"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .graphicsLayer { scaleX = emojiScale; scaleY = emojiScale }
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected)
                                                                    Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF4C1D95)))
                                                                else
                                                                    Brush.radialGradient(listOf(Color(0x30A855F7), Color(0x10000000)))
                                                            )
                                                            .clickable(interactionSource = emojiInteraction, indication = null) {
                                                                viewModel.setMessageReaction(msg.id, if (isSelected) "" else emoji)
                                                                reactionPickerMessageId = null
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(emoji, fontSize = 18.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isSending) {
                        item(key = "typing_dots_indicator") {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = androidx.compose.animation.fadeIn(tween(300)) +
                                    androidx.compose.animation.slideInVertically(tween(300)) { it / 2 },
                                exit  = androidx.compose.animation.fadeOut(tween(200))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 64.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // AI avatar — small
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(EmochiGradients.chatBubbleAi)
                                            .border(
                                                BorderStroke(1.dp, Brush.sweepGradient(EmochiGradients.avatarBorderSweep)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        com.example.ui.components.BotAvatarImage(
                                            avatarUrl = bot.avatarUrl.takeIf { it.isNotBlank() },
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            fallback = { OrbView(hue = hue, size = 26.dp) }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Bubble
                                    val typingShape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                                    Box(
                                        modifier = Modifier
                                            .border(BorderStroke(1.dp, EmochiGradients.chatBubbleAiBorder), typingShape)
                                            .clip(typingShape)
                                            .background(EmochiGradients.chatBubbleAi)
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TypingDots()
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "$displayName yazıyor",
                                                color = AppColors.TextMuted.copy(alpha = 0.75f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val showScrollToBottom = remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    if (totalItems == 0) return@derivedStateOf false

                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
                    val itemsFromEnd = totalItems - 1 - lastVisibleItem.index

                    if (itemsFromEnd >= 2) {
                        true
                    } else {
                        // On last visible item: check if scrolled up by at least ~400px
                        val viewportEnd = layoutInfo.viewportEndOffset
                        val itemBottom = lastVisibleItem.offset + lastVisibleItem.size
                        (itemBottom - viewportEnd) > 400
                    }
                }
            }

            if (showScrollToBottom.value) {
                val totalCount = messages.size + if (isSending) 1 else 0
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF4C1D95))
                                )
                            )
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFFC084FC))
                                    )
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    if (totalCount > 0) {
                                        listState.animateScrollToItem(totalCount - 1)
                                    }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "En aşağı in",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "En Aşağı İn",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

    if (showBackgroundPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showBackgroundPicker = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xF2170A32), Color(0xF50D0520), Color(0xF214082C))
                        )
                    )
                    .border(
                        BorderStroke(
                            1.3.dp,
                            Brush.linearGradient(
                                listOf(Color(0xFFE9D5FF), Color(0xFF9333EA), Color(0xFF60A5FA), Color(0xFFC084FC))
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_title_sparkle),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).padding(end = 6.dp)
                        )
                        Text(
                            text = "Sahne Atmosferi & Arka Plan",
                            color = Color.White,
                            fontSize = 17.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                shadow = Shadow(color = Color(0xCCA855F7), offset = Offset(0f, 2f), blurRadius = 12f)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_header_divider),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth().height(14.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sohbet ekranında görünmesini istediğiniz atmosfer ve duvar kağıdı seçeneği:",
                        color = Color(0xFFD8B4FE),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 1: Use Avatar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF230E48), Color(0xFF160830))))
                            .border(
                                BorderStroke(1.1.dp, Brush.horizontalGradient(listOf(Color(0xFFD8B4FE), Color(0xFF9333EA)))),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSaveBotProfile(bot.copy(chatBgUrl = "USE_AVATAR"), keyCharacters)
                                showBackgroundPicker = false
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFFE9D5FF), modifier = Modifier.size(19.dp))
                            Text("Karakter Portresini Arka Plan Yap", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Default Aetheria Gothic Theme
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A0D3A), Color(0xFF100724))))
                            .border(
                                BorderStroke(1.1.dp, Brush.horizontalGradient(listOf(Color(0xFF60A5FA), Color(0xFFA855F7)))),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSaveBotProfile(bot.copy(chatBgUrl = ""), keyCharacters)
                                showBackgroundPicker = false
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF93C5FD), modifier = Modifier.size(19.dp))
                            Text("Aetheria Gotik Katedral Teması", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 3: Custom Gallery Photo / Profile Settings
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFF6366F1))))
                            .border(
                                BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFFF3E8FF), Color(0xFFC084FC)))),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                showBackgroundPicker = false
                                showBotSettings = true
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                            Text("Galeriden Özel Fotoğraf Seç", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }

    // --- 3.2: Pending Cast Member Banner (non-blocking, sohbet akışını kesmez) ---
    val pendingCastMember by viewModel.pendingCastMember.collectAsState()
    LaunchedEffect(bot.id, messages.size) {
        viewModel.checkForPendingCastMembers(bot.id, messages.size)
    }
    AnimatedVisibility(
        visible = pendingCastMember != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(250)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(200))
    ) {
        pendingCastMember?.let { pending ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF1A0A3A), Color(0xFF2D1060))
                        )
                    )
                    .border(1.dp, AppColors.PrimaryViolet.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("pending_cast_banner")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("👤", fontSize = 15.sp)
                        Text(
                            text = "Yeni karakter fark ettim: ${pending.name}",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = pending.description.take(90).ifBlank { pending.role },
                        color = AppColors.TextLight,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.confirmPendingCastMember(bot.id, pending.id) },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("pending_cast_confirm_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryViolet),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) { Text("Evet, ekle", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        TextButton(
                            onClick = { viewModel.dismissCastMemberTemporarily(bot.id, pending.id) },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("pending_cast_dismiss_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) { Text("Hayır, önemli değil", color = AppColors.TextSecondary, fontSize = 12.sp) }
                        TextButton(
                            onClick = { viewModel.blacklistAndDismissCastMember(bot.id, pending.id) },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("pending_cast_blacklist_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) { Text("Bir daha sorma", color = AppColors.TextMuted, fontSize = 11.sp) }
                    }
                }
            }
        }
    }

    if (rewindTargetMessageId != null) {
        val targetId = rewindTargetMessageId!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { rewindTargetMessageId = null },
            containerColor = AppColors.CardDarkViolet,
            title = {
                Text(
                    text = "⏪ Buradan Devam Et",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Bu noktadan devam edersen sonraki mesajlar ve onlarla ilgili hafıza/duygu değişimi SİLİNECEK, emin misin?",
                    color = AppColors.TextLight,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idToRewind = targetId
                        rewindTargetMessageId = null
                        onRewindToMessage?.invoke(idToRewind)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.ErrorRedSoft)
                ) {
                    Text("Evet, Devam Et", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                        .clickable { rewindTargetMessageId = null }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("İptal", color = AppColors.TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    if (showBotSettings) {
        BotSettingsModal(
            bot = bot,
            keyCharacters = keyCharacters,
            characterEmotions = characterEmotions,
            affectionEvents = affectionEvents,
            onDismiss = { showBotSettings = false },
            onSave = onSaveBotProfile,
            onResetChat = onResetChat,
            onDeleteBot = onDeleteBot,
            onCloneBot = onCloneBot
        )
    }

    // 🏆 Milestone Overlay
    AnimatedVisibility(
        visible = activeMilestone != null,
        enter = fadeIn(tween(400)) + scaleIn(tween(400, easing = FastOutSlowInEasing), initialScale = 0.7f),
        exit = fadeOut(tween(500)) + scaleOut(tween(300), targetScale = 0.8f)
    ) {
        val milestone = activeMilestone ?: return@AnimatedVisibility
        val infinite = rememberInfiniteTransition(label = "milestoneGlow")
        val glowAlpha by infinite.animateFloat(0.4f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "mg")
        val particleProgress by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "mp")

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Semi-transparent backdrop
            Box(modifier = Modifier.fillMaxSize().background(Color(0x99000000)).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { activeMilestone = null })

            // Confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val colors = listOf(Color(0xFFF59E0B), Color(0xFFA855F7), Color(0xFF34D399), Color(0xFFF472B6), Color(0xFF60A5FA))
                repeat(30) { i ->
                    val hue = (i * 37f) % 360f
                    val x = size.width * ((i * 0.13f + particleProgress * 0.7f) % 1f)
                    val y = size.height * ((i * 0.17f + particleProgress * 0.4f + i * 0.03f) % 1f)
                    val r = 4f + (i % 5) * 2f
                    drawCircle(color = colors[i % colors.size].copy(alpha = 0.7f), radius = r, center = Offset(x, y))
                }
            }

            // Card
            Column(
                modifier = Modifier.padding(40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1F0D4A), Color(0xFF0D0820))))
                    .border(BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))), RoundedCornerShape(24.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(milestone.emoji, fontSize = 52.sp, modifier = Modifier.graphicsLayer { alpha = glowAlpha })
                Text("Yeni Kilometre Taşı!", color = Color(0xFFF59E0B), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(milestone.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("Sevgi: ${milestone.affectionAtTime} · Güven: ${milestone.trustAtTime}", color = Color(0xFFCDA8FF), fontSize = 12.sp)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                        .clickable { activeMilestone = null }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) { Text("Harika! ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            }
        }
    }

    // ⭐ Starred Messages BottomSheet — Premium redesign
    if (showStarredSheet) {
        LaunchedEffect(showStarredSheet) { viewModel.loadStarredMessages() }
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showStarredSheet = false },
            containerColor = Color(0xFF080614),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 0.dp,
            dragHandle = {
                Box(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(40.dp).height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))))
                    )
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                // Premium header
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0D0820))))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.radialGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⭐", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    "Yıldızlanan Mesajlar",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    "${starredMessages.size} mesaj kaydedildi",
                                    color = Color(0x80CDA8FF),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        // Close button
                        val closeInteraction = remember { MutableInteractionSource() }
                        val closePressed by closeInteraction.collectIsPressedAsState()
                        val closeScale by animateFloatAsState(if (closePressed) 0.88f else 1f, tween(80), label = "cs")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = closeScale; scaleY = closeScale }
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))))
                                .border(1.dp, Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFDC2626))), RoundedCornerShape(10.dp))
                                .clickable(interactionSource = closeInteraction, indication = null) { showStarredSheet = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        }
                    }
                }
                // Gradient separator
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(
                    Brush.horizontalGradient(listOf(Color.Transparent, Color(0x60F59E0B), Color(0x60A855F7), Color.Transparent))
                ))

                if (starredMessages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.radialGradient(listOf(Color(0x30F59E0B), Color(0x10D97706)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⭐", fontSize = 32.sp)
                            }
                            Text("Henüz yıldızlanan mesaj yok", color = Color(0x80CDA8FF), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Mesajlara uzun basarak yıldızlayabilirsin", color = Color(0x50CDA8FF), fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(starredMessages, key = { it.id }) { starred ->
                            val isAi = starred.role == "assistant"
                            val cardInteraction = remember { MutableInteractionSource() }
                            val cardPressed by cardInteraction.collectIsPressedAsState()
                            val cardScale by animateFloatAsState(if (cardPressed) 0.97f else 1f, tween(80), label = "cardScale")

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.linearGradient(
                                        if (isAi)
                                            listOf(Color(0xFF140D2E), Color(0xFF0C0820))
                                        else
                                            listOf(Color(0xFF220A45), Color(0xFF150630))
                                    ))
                                    .border(
                                        1.2.dp,
                                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFF59E0B))),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable(interactionSource = cardInteraction, indication = null) {}
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Header row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier.size(22.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isAi)
                                                        Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                                    else
                                                        Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9)))),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(if (isAi) "🤖" else "👤", fontSize = 10.sp)
                                            }
                                            Text(
                                                if (isAi) bot.aiName else (bot.userCharName.ifBlank { "Sen" }),
                                                color = Color(0xFFF59E0B),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text("⭐", fontSize = 12.sp)
                                    }
                                    // Gradient divider
                                    Box(modifier = Modifier.fillMaxWidth().height(0.8.dp).background(
                                        Brush.horizontalGradient(listOf(Color.Transparent, Color(0x40F59E0B), Color.Transparent))
                                    ))
                                    // Message text
                                    Text(
                                        starred.text.take(220) + if (starred.text.length > 220) "…" else "",
                                        color = Color(0xFFE8DEFF),
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                    // Unstar button
                                    val unstarInteraction = remember { MutableInteractionSource() }
                                    val unstarPressed by unstarInteraction.collectIsPressedAsState()
                                    val unstarScale by animateFloatAsState(if (unstarPressed) 0.93f else 1f, tween(80), label = "us")
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = unstarScale; scaleY = unstarScale }
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF2A1500), Color(0xFF1A0E00))))
                                                .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), RoundedCornerShape(8.dp))
                                                .clickable(interactionSource = unstarInteraction, indication = null) {
                                                    viewModel.toggleMessageStar(starred.id)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("☆ Yıldızı Kaldır", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQuickProfile) {
        val context = LocalContext.current
        BotQuickProfileSheet(
            bot = bot,
            keyCharacters = keyCharacters,
            characterEmotions = characterEmotions,
            affectionEvents = affectionEvents,
            onDismiss = { showQuickProfile = false },
            onSaveBot = { updatedBot ->
                onSaveBotProfile(updatedBot, keyCharacters)
            },
            onOpenFullSettings = { showBotSettings = true },
            onResetChat = { fullReset ->
                showQuickProfile = false
                onResetChat(fullReset)
            },
            onExportChat = {
                val fullText = messages.joinToString("\n\n") { "${if (it.role == "user") bot.userCharName.ifBlank { "Kullanıcı" } else displayName}: ${it.text}" }
                clipboardManager.setText(AnnotatedString(fullText))
                android.widget.Toast.makeText(context, "Tüm sohbet metni panoya kopyalandı!", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }
    } // End of else block for non-book mode
}

data class BookChoiceOption(val text: String, val rawLine: String)

fun parseBookChoiceOptions(rawText: String): List<BookChoiceOption> {
    val result = mutableListOf<BookChoiceOption>()
    val lines = rawText.split("\n")
    var inChoiceSection = false

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.startsWith("🔀")) {
            inChoiceSection = true
            continue
        }

        val isChoice = trimmed.startsWith("1️⃣") || trimmed.startsWith("2️⃣") || trimmed.startsWith("3️⃣") ||
                trimmed.startsWith("4️⃣") || trimmed.startsWith("5️⃣") || trimmed.startsWith("6️⃣") ||
                trimmed.matches(Regex("^[1-9][0-9]*[\\.\\)]\\s*.*")) ||
                (inChoiceSection && (trimmed.startsWith("-") || trimmed.startsWith("•") || trimmed.startsWith("*")))

        if (isChoice) {
            val choiceText = trimmed
                .replace(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|5️⃣|6️⃣|\\d+[\\.\\)]|[-•*])\\s*"), "")
                .trim()
            if (choiceText.length >= 2) {
                result.add(BookChoiceOption(choiceText, trimmed))
            }
        }
    }
    return result
}

enum class SummaryViewMode {
    PROMPT,
    SEQUENTIAL,
    FINAL_GRID
}

enum class StepPhase {
    CARD_ONLY,
    RESULT_REVEALED
}

data class ChapterChoiceItem(
    val id: Int,
    val title: String,
    val selectedOption: String,
    val impactText: String,
    val icon: String, // "🔀", "💫", "💭"
    val isBranching: Boolean = false,
    val isRelationship: Boolean = false,
    val impactLevel: String = "orta" // "dusuk", "orta", "kritik"
)

@OptIn(ExperimentalMaterial3Api::class)

private data class CosmicParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speed: Float
)







fun cleanCopyText(rawText: String): String {
    return com.example.data.repository.EmochiRepository.cleanEmotionTags(rawText)
}