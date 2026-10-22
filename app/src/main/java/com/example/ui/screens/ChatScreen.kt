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
import androidx.compose.ui.graphics.drawscope.rotate
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
    return buildAnnotatedString {
        // Regex for quoted dialogue ("..." or “...”) and asterisk actions (*...*)
        val regex = Regex("(\"[^\"]*\")|(“[^”]*”)|(«[^»]*»)|(\\*[^\\*]*\\*)")
        var lastIdx = 0
        val matches = regex.findAll(clean)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > lastIdx) {
                append(clean.substring(lastIdx, start))
            }
            val matchedStr = clean.substring(start, end)
            if (matchedStr.startsWith("*") && matchedStr.endsWith("*")) {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.TextSecondary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                ) {
                    append(matchedStr)
                }
            } else {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.PrimaryViolet, // Bright neon purple for dialogue quotes
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(matchedStr)
                }
            }
            lastIdx = end
        }
        if (lastIdx < clean.length) {
            append(clean.substring(lastIdx))
        }
    }
}

@Composable
fun RpAtmosphericBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Base dark purple/black canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Hex_07030C)
        )

        // Generated atmospheric gothic wallpaper image (richer visibility)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_bg_gothic),
            contentDescription = "Gothic Chat Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.65f
        )

        // Canvas for subtle floating glowing particles / star dust
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val random = java.util.Random(42)
            for (i in 0..35) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height
                val alpha = random.nextFloat() * 0.35f + 0.1f
                val radius = random.nextFloat() * 1.6f + 0.8f
                drawCircle(
                    color = AppColors.PurpleSoft.copy(alpha = alpha),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
        // Very subtle vignette overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x6005030A),
                            Color(0x1005030A),
                            Color(0x7005030A)
                        )
                    )
                )
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
    Box(modifier = Modifier.fillMaxSize()) {
        val isImeVisible = WindowInsets.isImeVisible

        // Base atmospheric wallpaper (full bleed across entire screen)
        RpAtmosphericBackground()

        // Bot background wallpaper model resolution
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
                        bot.id == "starter_aetheria" || bot.universeName.contains("Aetheria", ignoreCase = true) -> com.example.R.drawable.img_aetheria_universe
                        else -> com.example.R.drawable.img_chat_bg_gothic
                    }
                }
                bot.chatBgUrl.isNotBlank() -> bot.chatBgUrl
                else -> com.example.R.drawable.img_chat_bg_gothic
            }
        }

        coil.compose.AsyncImage(
            model = bgModel,
            contentDescription = "Sohbet Arka Planı",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.65f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x90070512),
                            Color(0x35070512),
                            Color(0x80070512),
                            Color(0xFA070512)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EmochiSurface.copy(alpha = 0.97f),
                                    EmochiSurface.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = if (isImeVisible) 4.dp else 6.dp)
                    ) {
                        // Back button (Left) — premium glass
                        val backInteraction = remember { MutableInteractionSource() }
                        val backPressed by backInteraction.collectIsPressedAsState()
                        val backScale by animateFloatAsState(
                            targetValue = if (backPressed) 0.87f else 1f,
                            animationSpec = tween(80),
                            label = "backScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(if (isImeVisible) 34.dp else 38.dp)
                                .align(Alignment.CenterStart)
                                .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.2.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF6D28D9))
                                        )
                                    ),
                                    RoundedCornerShape(13.dp)
                                )
                                .clickable(interactionSource = backInteraction, indication = null) { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFCDA8FF),
                                modifier = Modifier.size(if (isImeVisible) 17.dp else 19.dp)
                            )
                        }

                        // Truly Centered Stack (Avatar + Bot Name + Mood Emoji) - Hidden when keyboard is open
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !isImeVisible,
                            enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .then(
                                        if (bot.mode == "personal") {
                                            Modifier.clickable { isStatsExpanded = !isStatsExpanded }
                                        } else Modifier
                                    )
                                    .padding(horizontal = 50.dp)
                            ) {
                                // Avatar Box
                                Box(
                                    contentAlignment = Alignment.BottomEnd,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0x30A855F7).copy(alpha = pulseGlowAlpha * 0.5f))
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .align(Alignment.Center)
                                            .clip(CircleShape)
                                            .border(
                                                width = 1.8.dp,
                                                brush = Brush.sweepGradient(
                                                    listOf(AppColors.PurpleLight, AppColors.PrimaryViolet, AppColors.PurpleAccent, AppColors.PurpleLight)
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
                                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_aetheria_universe),
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
                                                fallback = { OrbView(hue = hue, size = 38.dp) }
                                            )
                                        } else {
                                            OrbView(hue = hue, size = 38.dp)
                                        }
                                    }

                                    // Online badge — pulsing glow ring + solid green dot
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.Emerald.copy(alpha = pulseGlowAlpha * 0.55f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        colors = listOf(AppColors.LightGreen, AppColors.Emerald)
                                                    )
                                                )
                                                .border(1.5.dp, EmochiSurface, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Bot Name (+ Mood Emoji on SAME LINE for Personal Mode)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = displayName,
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    if (bot.mode == "personal") {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSending) "✨" else emotionState.getMoodEmoji(),
                                            fontSize = 13.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Right Action Buttons (Wallpaper + Settings)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
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
                                        .size(38.dp)
                                        .graphicsLayer { scaleX = wpScale; scaleY = wpScale }
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.2.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF7C3AED))
                                                )
                                            ),
                                            RoundedCornerShape(13.dp)
                                        )
                                        .clickable(interactionSource = wpInteraction, indication = null) { showBackgroundPicker = true }
                                        .testTag("wallpaper_picker_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Wallpaper,
                                        contentDescription = "Arka Plan",
                                        tint = Color(0xFFCDA8FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Starred messages button
                            AnimatedVisibility(visible = !isImeVisible, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
                                val starBtnInteraction = remember { MutableInteractionSource() }
                                val starBtnPressed by starBtnInteraction.collectIsPressedAsState()
                                val starBtnScale by animateFloatAsState(if (starBtnPressed) 0.88f else 1f, tween(80), label = "starBtnScale")
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .graphicsLayer { scaleX = starBtnScale; scaleY = starBtnScale }
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(if (showStarredSheet) Brush.linearGradient(listOf(Color(0xFF3B2A00), Color(0xFF1F1500))) else Brush.linearGradient(listOf(Color(0xFF1C1040), Color(0xFF0F0828))))
                                        .border(BorderStroke(1.2.dp, Brush.linearGradient(if (showStarredSheet) listOf(Color(0xFFF59E0B), Color(0xFFD97706)) else listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF9333EA)))), RoundedCornerShape(13.dp))
                                        .clickable(interactionSource = starBtnInteraction, indication = null) { showStarredSheet = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⭐", fontSize = 16.sp)
                                }
                            }

                            // Search button
                            AnimatedVisibility(visible = !isImeVisible, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
                                val srInteraction = remember { MutableInteractionSource() }
                                val srPressed by srInteraction.collectIsPressedAsState()
                                val srScale by animateFloatAsState(if (srPressed) 0.88f else 1f, tween(80), label = "srScale")
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .graphicsLayer { scaleX = srScale; scaleY = srScale }
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(if (showSearchBar) Brush.linearGradient(listOf(Color(0xFF3B0F70), Color(0xFF1A0D38))) else Brush.linearGradient(listOf(Color(0xFF1C1040), Color(0xFF0F0828))))
                                        .border(BorderStroke(1.2.dp, Brush.linearGradient(if (showSearchBar) listOf(Color(0xFFA855F7), Color(0xFF7C3AED)) else listOf(Color(0xFF6D28D9), Color(0xFF4C1D95), Color(0xFF7C3AED)))), RoundedCornerShape(13.dp))
                                        .clickable(interactionSource = srInteraction, indication = null) {
                                            showSearchBar = !showSearchBar
                                            if (!showSearchBar) { localSearchQuery = ""; viewModel.clearSearch() }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Search, "Ara", tint = if (showSearchBar) Color(0xFFA855F7) else Color(0xFFCDA8FF), modifier = Modifier.size(18.dp))
                                }
                            }

                            val stInteraction = remember { MutableInteractionSource() }
                            val stPressed by stInteraction.collectIsPressedAsState()
                            val stScale by animateFloatAsState(
                                targetValue = if (stPressed) 0.88f else 1f,
                                animationSpec = tween(80),
                                label = "stScale"
                            )
                            Box(
                                modifier = Modifier
                                    .size(if (isImeVisible) 34.dp else 38.dp)
                                    .graphicsLayer { scaleX = stScale; scaleY = stScale }
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.2.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF9333EA))
                                            )
                                        ),
                                        RoundedCornerShape(13.dp)
                                    )
                                    .clickable(interactionSource = stInteraction, indication = null) { showBotSettings = true }
                                    .testTag("bot_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Ayarlar",
                                    tint = Color(0xFFB99AF8),
                                    modifier = Modifier.size(if (isImeVisible) 17.dp else 18.dp)
                                )
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
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF140D2E))
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
                                // Model pill — left
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

                                // Stats badges — right
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Affection badge — warm pink gradient
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
                                    // Trust badge — cool blue gradient
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
                // ── Bottom bar infinite animations ──────────────────────────
                val inputInfinite = rememberInfiniteTransition(label = "inputBar")
                val rpButtonGlow by inputInfinite.animateFloat(
                    initialValue = 0.55f, targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "rpGlow"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xD0060412),
                                    Color(0xFF060412)
                                )
                            )
                        )
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                ) {
                    // ── Quick RP chips ──────────────────────────────────────
                    val quickPrompts = listOf(
                        "🎭 Sahneyi Derinleştir" to "Lütfen şu anki sahneyi ve karakterin iç dünyasını daha detaylı, atmosferik bir şekilde betimleyerek yanıt ver.",
                        "💡 Ne Yapmalıyım?" to "Karakter bana bakıp şu anda ne yapmam gerektiğiyle ilgili imalı bir öneride bulunsun.",
                        "🔥 Duyguyu Yükselt" to "Aramızdaki duygusal çekimi ve gerilimi hissettirecek şekilde davran.",
                        "🎲 Sürpriz Hamle" to "Karakter beklenmedik, şaşırtıcı bir tepki versin veya yeni bir olay başlatsın."
                    )
                    val chipScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(chipScrollState)
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPrompts.forEachIndexed { idx, (label, promptText) ->
                            val chipInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            val chipPressed by chipInteraction.collectIsPressedAsState()
                            val chipScale by animateFloatAsState(
                                targetValue = if (chipPressed) 0.94f else 1f,
                                animationSpec = tween(90),
                                label = "chipScale_$idx"
                            )
                            val chipAlpha by animateFloatAsState(
                                targetValue = if (isSending) 0.38f else 1f,
                                animationSpec = tween(200),
                                label = "chipAlpha_$idx"
                            )
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { scaleX = chipScale; scaleY = chipScale; alpha = chipAlpha }
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1C1040), Color(0xFF110A2C))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFFA855F7), Color(0xFF7C3AED), Color(0xFF4C1D95))
                                            )
                                        ),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable(
                                        interactionSource = chipInteraction,
                                        indication = null,
                                        enabled = !isSending
                                    ) { onSendMessage(promptText) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = Color(0xFFCDA8FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // ── Thin separator ──────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0x40A855F7), Color(0x20C084FC), Color.Transparent)
                                )
                            )
                    )

                    // ── Input row ───────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // +RP✦ button — glowing when active
                        val isRpMode = inputText.trim().let { it.startsWith("*") && it.endsWith("*") && it.length >= 2 }
                        val rpInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val rpPressed by rpInteraction.collectIsPressedAsState()
                        val rpScale by animateFloatAsState(
                            targetValue = if (rpPressed) 0.91f else 1f,
                            animationSpec = tween(100),
                            label = "rpScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .graphicsLayer { scaleX = rpScale; scaleY = rpScale }
                                .clip(CircleShape)
                                .background(
                                    if (isRpMode) Brush.radialGradient(
                                        listOf(Color(0xFF6D28D9), Color(0xFF3B1070))
                                    ) else Brush.verticalGradient(
                                        listOf(Color(0xFF291060), Color(0xFF150830))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        width = if (isRpMode) 2.dp else 1.5.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                AppColors.PurpleLight.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f),
                                                AppColors.PrimaryViolet.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.5f),
                                                AppColors.PurpleAccent.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f),
                                                AppColors.PurpleLight.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f)
                                            )
                                        )
                                    ),
                                    CircleShape
                                )
                                .clickable(interactionSource = rpInteraction, indication = null) {
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
                                text = if (isRpMode) "✦RP" else "+RP✦",
                                color = if (isRpMode) Color.White else AppColors.PurpleLight,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Input field — custom bordered
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isRpMode) "Eylem veya diyalog...*" else "Bir mesaj yazın...",
                                    color = AppColors.TextSlate.copy(alpha = 0.7f),
                                    fontSize = 13.5.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(22.dp),
                            colors = customTextFieldColors(),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send button — multi-layer premium
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
                                .size(50.dp)
                                .graphicsLayer { scaleX = sendScale; scaleY = sendScale },
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer pulse halo — only when enabled
                            if (isSendEnabled) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale
                                            scaleY = pulseScale
                                            alpha = pulseGlowAlpha * 0.5f
                                        }
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Color(0xFFA855F7), Color(0x007C3AED))
                                            )
                                        )
                                )
                            }
                            // Button core
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSendEnabled) Brush.linearGradient(
                                            listOf(Color(0xFFBB7BFF), Color(0xFF9333EA), Color(0xFF5B21B6))
                                        ) else Brush.linearGradient(
                                            listOf(Color(0x201A0840), Color(0x200C0820))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            if (isSendEnabled) Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                            else Brush.linearGradient(listOf(Color(0x20A855F7), Color(0x10A855F7)))
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
                                    tint = if (isSendEnabled) Color.White else Color(0x35A78BFA),
                                    modifier = Modifier.size(20.dp)
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
                                // AI BOT BUBBLE (Left Side) matching screenshot
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Avatar — sweep border + soft glow halo
                                    Box(
                                        modifier = Modifier.size(42.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Soft glow halo behind avatar
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0x30A855F7), Color.Transparent)
                                                    )
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    BorderStroke(
                                                        1.8.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                )
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            com.example.ui.components.BotAvatarImage(
                                                avatarUrl = bot.avatarUrl.takeIf { it.isNotBlank() },
                                                contentDescription = displayName,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                fallback = {
                                                    androidx.compose.foundation.Image(
                                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_cosmic_orb),
                                                        contentDescription = displayName,
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        // Bot Name Label
                                        Text(
                                            text = displayName,
                                            color = AppColors.PrimaryViolet,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                        )

                                        // AI Bubble Box — soğuk derin gece, subtle border
                                        val aiShape = RoundedCornerShape(topStart = 6.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 22.dp)
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
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    BorderStroke(if (isSearchMatch) 1.5.dp else 1.dp, if (isSearchMatch) Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))) else EmochiGradients.chatBubbleAiBorder),
                                                    aiShape
                                                )
                                                .clip(aiShape)
                                                .background(if (isSearchMatch) Brush.linearGradient(listOf(Color(0xFF1F1408), Color(0xFF0F0B04))) else EmochiGradients.chatBubbleAi)
                                                .combinedClickable(
                                                    onClick = {},
                                                    onLongClick = {
                                                        reactionPickerMessageId = if (reactionPickerMessageId == msg.id) null else msg.id
                                                    }
                                                )
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = formatNarrativeText(displayText + if (isThisStreaming && cursorAlpha > 0.5f) "▌" else ""),
                                                    color = AppColors.TextWhite,
                                                    fontSize = 14.5.sp,
                                                    lineHeight = 22.sp
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.align(Alignment.End),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (timeFormatted.isNotBlank()) {
                                                        Text(
                                                            text = timeFormatted,
                                                            color = AppColors.TextMuted,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                    if (!msg.provider.isNullOrBlank()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        val providerLabel: String
                                                        val providerTextColor: Color
                                                        val providerBg: Color
                                                        val providerBorder: Color
                                                        when {
                                                            msg.provider == "llm7" -> {
                                                                providerLabel = "⚡ LLM7"; providerTextColor = Color(0xFF86EFAC)
                                                                providerBg = Color(0x2022C55E); providerBorder = Color(0x6022C55E)
                                                            }
                                                            msg.provider == "gemini" -> {
                                                                providerLabel = "✨ Gemini"; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x25A855F7); providerBorder = Color(0x60A855F7)
                                                            }
                                                            msg.provider == "groq" -> {
                                                                providerLabel = "🚀 Groq"; providerTextColor = Color(0xFF93C5FD)
                                                                providerBg = Color(0x203B82F6); providerBorder = Color(0x603B82F6)
                                                            }
                                                            msg.provider == "claude" -> {
                                                                providerLabel = "🧠 Claude"; providerTextColor = Color(0xFFFBBF24)
                                                                providerBg = Color(0x20F59E0B); providerBorder = Color(0x60F59E0B)
                                                            }
                                                            msg.provider == "openai" -> {
                                                                providerLabel = "🟢 OpenAI"; providerTextColor = Color(0xFF86EFAC)
                                                                providerBg = Color(0x2022C55E); providerBorder = Color(0x6022C55E)
                                                            }
                                                            msg.provider!!.startsWith("custom_") -> {
                                                                providerLabel = "🌐 Custom"; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x257C3AED); providerBorder = Color(0x607C3AED)
                                                            }
                                                            else -> {
                                                                providerLabel = msg.provider!!; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x25A855F7); providerBorder = Color(0x60A855F7)
                                                            }
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(providerBg)
                                                                .border(0.8.dp, providerBorder, RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = providerLabel,
                                                                color = providerTextColor,
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "||ı||",
                                                        color = AppColors.PrimaryPurple,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
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
                                                                val filterBtnInteraction = remember { MutableInteractionSource() }
                                                                val filterBtnPressed by filterBtnInteraction.collectIsPressedAsState()
                                                                val filterBtnScale by animateFloatAsState(
                                                                    targetValue = if (filterBtnPressed) 0.92f else 1f,
                                                                    animationSpec = tween(80),
                                                                    label = "fbs"
                                                                )
                                                                Box(
                                                                    modifier = Modifier
                                                                        .graphicsLayer { scaleX = filterBtnScale; scaleY = filterBtnScale }
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                                                                        .clickable(
                                                                            interactionSource = filterBtnInteraction,
                                                                            indication = null
                                                                        ) { viewModel.retryWithSoftenedPrompt(bot.id) }
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
                                                                val retryBtnInteraction = remember { MutableInteractionSource() }
                                                                val retryBtnPressed by retryBtnInteraction.collectIsPressedAsState()
                                                                val retryBtnScale by animateFloatAsState(
                                                                    targetValue = if (retryBtnPressed) 0.92f else 1f,
                                                                    animationSpec = tween(80),
                                                                    label = "rbs"
                                                                )
                                                                Box(
                                                                    modifier = Modifier
                                                                        .graphicsLayer { scaleX = retryBtnScale; scaleY = retryBtnScale }
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFF87171), RoundedCornerShape(10.dp))
                                                                        .clickable(
                                                                            interactionSource = retryBtnInteraction,
                                                                            indication = null
                                                                        ) { onRetryMessage?.invoke(msg.id) }
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

                                        Spacer(modifier = Modifier.height(8.dp))

                                         // AI Message Action Pills
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState())
                                                .padding(vertical = 3.dp)
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

                                            // Always visible pills
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
                                            AiActionPill("•••", "") {
                                                expandedMenuMessageId = if (expandedMenuMessageId == msg.id) null else msg.id
                                            }
                                            // Expanded pills (behind •••)
                                            if (expandedMenuMessageId == msg.id) {
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
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CardDarkBlue),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.5.dp, AppColors.PrimaryPurple),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🖼️ Arka Plan Fotoğrafı",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sohbet ekranında görünmesini istediğiniz duvar kağıdı seçeneği:",
                        color = AppColors.IndigoLight,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Button 1: Use Avatar
                    Button(
                        onClick = {
                            onSaveBotProfile(bot.copy(chatBgUrl = "USE_AVATAR"), keyCharacters)
                            showBackgroundPicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardPurpleDark, contentColor = AppColors.PrimaryViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("👤 Bot Avatarını Arka Plan Yap", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 2: Default Theme
                    Button(
                        onClick = {
                            onSaveBotProfile(bot.copy(chatBgUrl = ""), keyCharacters)
                            showBackgroundPicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardMidnight, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("🌌 Varsayılan Temaya Dön", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 3: Custom Gallery Photo / Profile Settings
                    Button(
                        onClick = {
                            showBackgroundPicker = false
                            showBotSettings = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryPurple, contentColor = AppColors.CardDarkBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("📸 Galeriden Fotoğraf Seç", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
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