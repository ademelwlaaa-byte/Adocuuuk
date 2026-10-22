
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
import androidx.compose.animation.slideOutHorizontally
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

@Composable
fun BookReaderView(
    bot: com.example.data.local.BotEntity,
    messages: List<com.example.data.local.MessageEntity>,
    isSending: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onEnsureOpeningMessage: () -> Unit,
    onResetChat: (Boolean) -> Unit,
    onClearError: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var fontSizeSp by remember { mutableStateOf(15.5.sp) }
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    var showSummaryModal by remember { mutableStateOf(false) }
    val listState = rememberSaveable(saver = androidx.compose.foundation.lazy.LazyListState.Saver) {
        androidx.compose.foundation.lazy.LazyListState()
    }
    var lastProcessedMsgCount by rememberSaveable { mutableIntStateOf(0) }

    // Exit fullscreen first when pressing system back button
    androidx.activity.compose.BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    // Automatically ensure opening message so Chapter 1 displays immediately
    LaunchedEffect(messages.isEmpty(), isSending) {
        if (messages.isEmpty() && !isSending) {
            onEnsureOpeningMessage()
        }
    }

    // BookAudioController ile müzik yönetimi — race condition yok
    val isAmbientMusicPlaying by remember { derivedStateOf { com.example.util.BookAudioController.isPlaying } }

    fun toggleAmbientMusic() {
        com.example.util.BookAudioController.toggle(context, com.example.R.raw.bolumsonu)
    }

    DisposableEffect(Unit) {
        onDispose {
            // Okuyucudan çıkışta müziği fade-out ile kapat, anında kesme
            com.example.util.BookAudioController.stop(fadeOutMs = 1200)
        }
    }

    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty()) {
            if (messages.size > lastProcessedMsgCount && lastProcessedMsgCount > 0) {
                listState.animateScrollToItem(messages.lastIndex)
            }
            lastProcessedMsgCount = messages.size
        }
    }

    val lastAssistantMsg = remember(messages) {
        messages.lastOrNull { it.role == "assistant" || it.role == "model" }
    }

    val activeChoices = remember(lastAssistantMsg?.text) {
        if (lastAssistantMsg != null) parseBookChoiceOptions(lastAssistantMsg.text) else emptyList()
    }

    val isChapterCompleted = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        txt.contains("BÖLÜM 1 SONU") || txt.contains("BÖLÜM 2 SONU") ||
        txt.contains("BÖLÜM 3 SONU") || txt.contains("BÖLÜM 4 SONU") ||
        txt.contains("BÖLÜM 5 SONU") || txt.contains("BÖLÜM 6 SONU") ||
        txt.contains("BÖLÜM 7 SONU") ||
        txt.contains("BÖLÜM TAMAMLANDI") || txt.contains("Tebrikler! Bölüm") ||
        (txt.contains("BÖLÜM") && txt.contains("SONU"))
    }

    LaunchedEffect(isChapterCompleted) {
        if (isChapterCompleted && !com.example.util.BookAudioController.isPlaying) {
            com.example.util.BookAudioController.play(context, com.example.R.raw.bolumsonu, loop = true, volume = 0.85f)
        }
    }

    val completedChapterTitle = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        when {
            txt.contains("BÖLÜM 7 SONU") -> "📖 BÖLÜM 7 TAMAMLANDI"
            txt.contains("BÖLÜM 6 SONU") -> "📖 BÖLÜM 6 TAMAMLANDI"
            txt.contains("BÖLÜM 5 SONU") -> "📖 BÖLÜM 5 TAMAMLANDI"
            txt.contains("BÖLÜM 4 SONU") -> "📖 BÖLÜM 4 TAMAMLANDI"
            txt.contains("BÖLÜM 3 SONU") -> "📖 BÖLÜM 3 TAMAMLANDI"
            txt.contains("BÖLÜM 2 SONU") -> "📖 BÖLÜM 2 TAMAMLANDI"
            else -> "📖 BÖLÜM 1 TAMAMLANDI"
        }
    }

    val completedChapterDesc = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        when {
            txt.contains("BÖLÜM 7 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 7'nin sonuna ulaştın."
            txt.contains("BÖLÜM 6 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 6'nın sonuna ulaştın."
            txt.contains("BÖLÜM 5 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 5'in sonuna ulaştın."
            txt.contains("BÖLÜM 4 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 4'ün sonuna ulaştın."
            txt.contains("BÖLÜM 3 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 3'ün sonuna ulaştın."
            txt.contains("BÖLÜM 2 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 2'nin sonuna ulaştın."
            else -> "Tebrikler! Verdiğin kararlarla Bölüm 1'in sonuna ulaştın."
        }
    }

    if (showSummaryModal) {
        ChapterSummaryModal(
            messages = messages,
            onDismiss = { showSummaryModal = false },
            onRestartChapter = { onResetChat(true) },
            onNextChapter = {
                showSummaryModal = false
                val lastMsgText = lastAssistantMsg?.text ?: ""
                when {
                    lastMsgText.contains("BÖLÜM 7 SONU") ->
                        android.widget.Toast.makeText(context, "Bölüm 8 yakında yayınlanacak! Takipte kal.", android.widget.Toast.LENGTH_LONG).show()
                    lastMsgText.contains("BÖLÜM 6 SONU") ->
                        onSendMessage("Bölüm 7: Karanlık Ayna'ya başla")
                    lastMsgText.contains("BÖLÜM 5 SONU") ->
                        onSendMessage("Bölüm 6: Kırık Ayna'ya başla")
                    lastMsgText.contains("BÖLÜM 4 SONU") ->
                        onSendMessage("Bölüm 5: Kırmızı Buz'a başla")
                    lastMsgText.contains("BÖLÜM 3 SONU") ->
                        onSendMessage("Bölüm 4: İlk Görev'e başla")
                    lastMsgText.contains("BÖLÜM 2 SONU") ->
                        onSendMessage("Bölüm 3: Deneme Süresi'ne başla")
                    else ->
                        onSendMessage("Bölüm 2: Şartlar'a başla")
                }
            }
        )
    }

    val chapterNum = remember(messages) {
        val txt = messages.lastOrNull { it.role == "assistant" || it.role == "model" }?.text ?: ""
        when {
            txt.contains("BÖLÜM 7") -> 7
            txt.contains("BÖLÜM 6") -> 6
            txt.contains("BÖLÜM 5") -> 5
            txt.contains("BÖLÜM 4") -> 4
            txt.contains("BÖLÜM 3") -> 3
            txt.contains("BÖLÜM 2") -> 2
            else -> 1
        }
    }

    var showChapterEndOverlay by remember { mutableStateOf(false) }
    LaunchedEffect(isChapterCompleted) {
        if (isChapterCompleted) {
            showChapterEndOverlay = true
            // Progress'i kaydet — sonraki bölümün kilidi açılsın
            ChapterProgressManager.saveUnlockedChapter(context, bot.id, chapterNum)
            ChapterProgressManager.saveUnlockedChapter(context, "preset_aiden_mcu_cosmic", chapterNum)
        }
    }

    // Extract the opening hook sentence (e.g., "Gökyüzü yanlıştı.") from the first assistant message
    val firstAssistantMsg = remember(messages) {
        messages.firstOrNull { it.role == "assistant" || it.role == "model" }
    }
    val openingHookLine = remember(firstAssistantMsg?.text, bot.openingMessage) {
        val src = firstAssistantMsg?.text?.ifBlank { bot.openingMessage } ?: bot.openingMessage
        val paragraphs = src.split("\n\n").map { it.trim() }.filter { it.isNotBlank() }
        val nonHeading = paragraphs.firstOrNull { p ->
            !p.startsWith("#") && !(p.startsWith("📖") && p.contains("BÖLÜM"))
        } ?: "Gökyüzü yanlıştı."
        if (nonHeading.length <= 90) nonHeading.removePrefix("*").removeSuffix("*").trim()
        else "Gökyüzü yanlıştı."
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bookNeonGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05030C))
    ) {
        // 1. Top-Right Gothic Castle & Crescent Moon Background Strip (1:1 from reference)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_bg_top_castle),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 56.dp)
                .align(Alignment.TopCenter)
        )

        // 2. Bottom Cosmic Nebula, Shooting Light Ray, 4-Point Star & Glowing Planet Arc (1:1 from reference)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_bg_bottom_nebula),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // Main Foreground Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── TOP NAVIGATION BAR & HERO CHAPTER BANNER (Hidden when Fullscreen is active) ──
            androidx.compose.animation.AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn(tween(240)) + expandVertically(tween(260, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(240, easing = FastOutSlowInEasing))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top Bar Row
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 2.dp)
                    ) {
                        // Left: Circular Back Button + Glowing Book Icon Box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            val backScale = remember { androidx.compose.animation.core.Animatable(1f) }
                            val backScope = rememberCoroutineScope()
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .graphicsLayer { scaleX = backScale.value; scaleY = backScale.value }
                                    .drawBehind {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF7C3AED).copy(alpha = 0.55f * glowPulse),
                                                    Color(0xFF4F46E5).copy(alpha = 0.2f * glowPulse),
                                                    Color.Transparent
                                                ),
                                                radius = size.minDimension * 0.82f
                                            )
                                        )
                                    }
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFF211042), Color(0xFF0C061C))
                                        )
                                    )
                                    .border(
                                        width = 1.3.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFFA855F7), Color(0xFF6366F1), Color(0xFF9333EA))
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        backScope.launch {
                                            backScale.animateTo(0.88f, tween(80))
                                            backScale.animateTo(1f, tween(120))
                                        }
                                        onBack()
                                    }
                                    .testTag("book_back_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Geri",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(11.dp))

                            // Exact Glowing Open Book Icon Box from reference
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_top_icon),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                modifier = Modifier
                                    .size(36.dp)
                                    .drawBehind {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF8B5CF6).copy(alpha = 0.45f * glowPulse),
                                                    Color.Transparent
                                                ),
                                                radius = size.minDimension * 0.85f
                                            )
                                        )
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }

                        // Center: "Kozmik Sürükleniş" + "BÖLÜM 1" + Glowing 4-Point Star Divider
                        val bookTitleText = remember(bot.aiName) {
                            val raw = bot.aiName.ifBlank { "Kozmik Sürükleniş" }
                            if (raw.contains("Kozmik", ignoreCase = true)) "Kozmik Sürükleniş" else raw
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 2.dp, start = 92.dp, end = 112.dp)
                        ) {
                            Text(
                                text = bookTitleText,
                                color = Color.White,
                                fontSize = 15.5.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "BÖLÜM $chapterNum",
                                color = Color(0xFFD8B4FE),
                                fontSize = 10.sp,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.6.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_top_divider),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                                modifier = Modifier
                                    .width(138.dp)
                                    .height(13.dp)
                            )
                        }

                        // Right: [A- | A+] + [Mute] + [Refresh] (1:1 with reference)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 3.dp)
                        ) {
                            // [A- | A+] Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Color(0xFF100824))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF7C3AED), Color(0xFF6366F1))
                                        ),
                                        shape = RoundedCornerShape(9.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            fontSizeSp = if (fontSizeSp.value > 13f) (fontSizeSp.value - 1.5f).sp else 13.sp
                                        }
                                        .padding(horizontal = 7.dp)
                                        .testTag("book_font_decrease_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "A⁻",
                                        color = Color(0xFFE9D5FF),
                                        fontSize = 11.sp,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(16.dp)
                                        .background(Color(0x558B5CF6))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            fontSizeSp = if (fontSizeSp.value < 22f) (fontSizeSp.value + 1.5f).sp else 22.sp
                                        }
                                        .padding(horizontal = 7.dp)
                                        .testTag("book_font_increase_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "A⁺",
                                        color = Color(0xFFE9D5FF),
                                        fontSize = 11.sp,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Mute / Ambient Music Toggle Button
                            val musicScale = remember { androidx.compose.animation.core.Animatable(1f) }
                            val musicScope = rememberCoroutineScope()
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .graphicsLayer { scaleX = musicScale.value; scaleY = musicScale.value }
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Color(0xFF100824))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            if (isAmbientMusicPlaying) {
                                                listOf(Color(0xFFD946EF), Color(0xFF8B5CF6))
                                            } else {
                                                listOf(Color(0xFF7C3AED), Color(0xFF6366F1))
                                            }
                                        ),
                                        shape = RoundedCornerShape(9.dp)
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        musicScope.launch {
                                            musicScale.animateTo(0.86f, tween(80))
                                            musicScale.animateTo(1f, tween(120))
                                        }
                                        toggleAmbientMusic()
                                    }
                                    .testTag("book_music_toggle_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isAmbientMusicPlaying) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_mute_icon),
                                        contentDescription = "Sesi Aç",
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text("🔊", fontSize = 13.sp)
                                }
                            }

                            // Refresh / Reset Chapter Button
                            val resetScale = remember { androidx.compose.animation.core.Animatable(1f) }
                            val resetScope = rememberCoroutineScope()
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .graphicsLayer { scaleX = resetScale.value; scaleY = resetScale.value }
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Color(0xFF100824))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF7C3AED), Color(0xFF6366F1))
                                        ),
                                        shape = RoundedCornerShape(9.dp)
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        resetScope.launch {
                                            resetScale.animateTo(0.86f, tween(80))
                                            resetScale.animateTo(1f, tween(120))
                                        }
                                        onResetChat(true)
                                    }
                                    .testTag("book_reset_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sıfırla",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── HERO CHAPTER BANNER CARD (1:1 with reference) ──
                    val isKozmikBook = remember(bot.aiName, bot.id) {
                        bot.aiName.contains("Kozmik", ignoreCase = true) ||
                        bot.id == "preset_aiden_mcu_cosmic" ||
                        bot.aiName.isBlank()
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .aspectRatio(775f / 308f)
                            .drawBehind {
                                drawRoundRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF7C3AED).copy(alpha = 0.25f * glowPulse),
                                            Color.Transparent
                                        ),
                                        radius = size.maxDimension * 0.65f
                                    ),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx())
                                )
                            }
                            .clip(RoundedCornerShape(22.dp))
                    ) {
                        if (isKozmikBook) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_hero_banner_card),
                                contentDescription = "Kozmik Sürükleniş Bölüm $chapterNum",
                                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Dynamic chapter pill override if user is on Chapter 2 or 3
                            if (chapterNum > 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 74.dp, top = 16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A103C))
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Bölüm $chapterNum",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 11.5.sp,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_hero_banner_generic),
                                contentDescription = bot.aiName,
                                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 76.dp, end = 140.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A103C))
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Bölüm $chapterNum",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 11.sp,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bot.aiName.uppercase(),
                                    color = Color(0xFFEDE9FE),
                                    fontSize = 19.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ROMAN  •  İNTERAKTİF  •  FANTASTİK",
                                    color = Color(0xFFC4B5FD),
                                    fontSize = 8.5.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.8.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(11.dp))
                }
            }

            // ── HATA BANNER — Premium inline kart ──────────────────────
            androidx.compose.animation.AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)),
                exit = fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
            ) {
                errorMessage?.let { err ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF2D0808), Color(0xFF1C0A0A), Color(0xFF2D0808)))
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(Color(0xFF991B1B), Color(0xFF7F1D1D), Color(0xFF991B1B))),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3D1010)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚠️", fontSize = 15.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Bağlantı Hatası",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.5.sp,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                err.take(80),
                                color = Color(0xFFEF9191),
                                fontSize = 10.5.sp,
                                fontFamily = InterFontFamily,
                                lineHeight = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3D1010))
                                .border(1.dp, Color(0xFF7F1D1D), CircleShape)
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClearError() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            // ── STORY READING CARD (1:1 Botanical Neon Frame + Single Extra Fullscreen Button) ──
            val bottomSpaceDp by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (isFullScreen) 8.dp else 112.dp,
                animationSpec = tween(260, easing = FastOutSlowInEasing),
                label = "bottomSpaceDp"
            )
            val topSpaceDp by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (isFullScreen) 8.dp else 0.dp,
                animationSpec = tween(260, easing = FastOutSlowInEasing),
                label = "topSpaceDp"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = topSpaceDp,
                        bottom = bottomSpaceDp
                    )
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // Botanical purple leaves + glowing neon border frame (1:1 from reference)
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_story_card_bg),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 13.dp, bottom = 30.dp)
                ) {
                    // Header Row inside Story Reading Card:
                    // Left: [✦] Star Badge + Italic Hook ("Gökyüzü yanlıştı.")
                    // Right: Single Extra Fullscreen Button ("sadece bir tane tam ekran butonu olsun ekstra olarak")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 13.dp, end = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_star_badge),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(11.dp))

                        Text(
                            text = openingHookLine,
                            color = Color(0xFFC8B6FF),
                            fontSize = (fontSizeSp.value + 0.5f).sp,
                            fontFamily = InterFontFamily,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // THE SINGLE EXTRA FULLSCREEN BUTTON
                        val fsScale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val fsScope = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .size(31.dp)
                                .graphicsLayer { scaleX = fsScale.value; scaleY = fsScale.value }
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF8B5CF6).copy(alpha = 0.42f * glowPulse),
                                                Color.Transparent
                                            ),
                                            radius = size.minDimension * 0.85f
                                        )
                                    )
                                }
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF190C36), Color(0xFF0D0620))
                                    )
                                )
                                .border(
                                    width = 1.1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFFA855F7), Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    ),
                                    shape = RoundedCornerShape(9.dp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    fsScope.launch {
                                        fsScale.animateTo(0.86f, tween(80))
                                        fsScale.animateTo(1f, tween(120))
                                    }
                                    isFullScreen = !isFullScreen
                                }
                                .testTag("book_fullscreen_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Crisp custom Fullscreen / FullscreenExit corner brackets icon
                            Canvas(modifier = Modifier.size(15.dp)) {
                                val stroke = 1.7.dp.toPx()
                                val len = size.width * 0.32f
                                val w = size.width
                                val h = size.height
                                val col = Color(0xFFF3E8FF)
                                val cap = androidx.compose.ui.graphics.StrokeCap.Round

                                if (!isFullScreen) {
                                    // Outward corners (Expand to Fullscreen)
                                    // Top-Left
                                    drawLine(col, Offset(0f, len), Offset(0f, 0f), stroke, cap)
                                    drawLine(col, Offset(0f, 0f), Offset(len, 0f), stroke, cap)
                                    // Top-Right
                                    drawLine(col, Offset(w - len, 0f), Offset(w, 0f), stroke, cap)
                                    drawLine(col, Offset(w, 0f), Offset(w, len), stroke, cap)
                                    // Bottom-Left
                                    drawLine(col, Offset(0f, h - len), Offset(0f, h), stroke, cap)
                                    drawLine(col, Offset(0f, h), Offset(len, h), stroke, cap)
                                    // Bottom-Right
                                    drawLine(col, Offset(w - len, h), Offset(w, h), stroke, cap)
                                    drawLine(col, Offset(w, h - len), Offset(w, h), stroke, cap)
                                } else {
                                    // Inward corners (Exit Fullscreen)
                                    // Top-Left
                                    drawLine(col, Offset(0f, len), Offset(len, len), stroke, cap)
                                    drawLine(col, Offset(len, 0f), Offset(len, len), stroke, cap)
                                    // Top-Right
                                    drawLine(col, Offset(w - len, 0f), Offset(w - len, len), stroke, cap)
                                    drawLine(col, Offset(w, len), Offset(w - len, len), stroke, cap)
                                    // Bottom-Left
                                    drawLine(col, Offset(0f, h - len), Offset(len, h - len), stroke, cap)
                                    drawLine(col, Offset(len, h), Offset(len, h - len), stroke, cap)
                                    // Bottom-Right
                                    drawLine(col, Offset(w - len, h), Offset(w - len, h - len), stroke, cap)
                                    drawLine(col, Offset(w, h - len), Offset(w - len, h - len), stroke, cap)
                                }
                            }
                        }
                    }

                    // ── OKUMA İLERLEME ÇUBUĞU ──────────────────────────────────
                    val readProgress by remember {
                        derivedStateOf {
                            val total = listState.layoutInfo.totalItemsCount
                            if (total > 0)
                                ((listState.firstVisibleItemIndex + 1).toFloat() / total.toFloat()).coerceIn(0f, 1f)
                            else 0f
                        }
                    }
                    val animatedReadProgress by animateFloatAsState(
                        targetValue = readProgress,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label = "readProgress"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1E1535))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedReadProgress)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF7C3AED), Color(0xFFA855F7), Color(0xFFC084FC))
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Story Text Body
                    val displayMessages = remember(messages, bot.openingMessage) {
                        if (messages.isNotEmpty()) messages
                        else if (bot.openingMessage.isNotBlank()) {
                            listOf(
                                com.example.data.local.MessageEntity(
                                    id = "opening_preview",
                                    botId = bot.id,
                                    role = "assistant",
                                    text = bot.openingMessage,
                                    timestamp = 0L
                                )
                            )
                        } else emptyList()
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 54.dp, end = 42.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        itemsIndexed(displayMessages, key = { idx, msg -> "${msg.id}_$idx" }) { idx, msg ->
                            if (msg.role == "user") {
                                // User Choice Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1A1032), Color(0xFF110A22))))
                                            .border(
                                                1.dp,
                                                Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF6366F1))),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 11.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(CircleShape)
                                                    .background(Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF4C1D95)))),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("✦", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "VERİLEN KARAR",
                                                    color = Color(0xFFC4B5FD),
                                                    fontSize = 9.5.sp,
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.8.sp
                                                )
                                                Text(
                                                    text = msg.text.replace(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|\\d+[\\.\\)]|[-•*])\\s*"), ""),
                                                    color = Color(0xFFF3E8FF),
                                                    fontSize = (fontSizeSp.value - 1f).sp,
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = (fontSizeSp.value * 1.4f).sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // AI Story Narrative Paragraphs
                                val msgChoices = parseBookChoiceOptions(msg.text)
                                val rawChoiceLines = msgChoices.map { it.rawLine.trim() }.toSet()
                                val paragraphs = msg.text.split("\n\n")
                                val isLastMsg = msg.id == (lastAssistantMsg?.id ?: "")
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(18.dp)
                                ) {
                                    paragraphs.forEachIndexed { pIdx, paragraph ->
                                        val trimmed = paragraph.trim()
                                        if (trimmed.isBlank()) return@forEachIndexed

                                        // Skip chapter title line in first message because it's in the Hero Banner Card
                                        if (idx == 0 && (trimmed.startsWith("#") || (trimmed.startsWith("📖") && trimmed.contains("BÖLÜM")))) {
                                            return@forEachIndexed
                                        }

                                        // Skip the openingHookLine in first message because it's rendered next to the top-left [✦] star
                                        if (idx == 0 && trimmed.removePrefix("*").removeSuffix("*").trim() == openingHookLine) {
                                            return@forEachIndexed
                                        }

                                        val lines = trimmed.lines().map { it.trim() }
                                        val isChoiceBlock = lines.all { line ->
                                            line.isBlank() || rawChoiceLines.contains(line) || line.startsWith("🔀") ||
                                            line.startsWith("---") || line.matches(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|5️⃣|6️⃣|\\d+[\\.\\)]|[-•*])\\s*.*"))
                                        }
                                        if (isChoiceBlock && msgChoices.isNotEmpty()) return@forEachIndexed

                                        // Paragraph fade-in: son mesajın paragrafları sırayla belirir
                                        val paraVisible = remember(msg.id, pIdx) { androidx.compose.runtime.mutableStateOf(false) }
                                        LaunchedEffect(msg.id, pIdx) {
                                            if (isLastMsg) {
                                                kotlinx.coroutines.delay((pIdx * 120L).coerceAtMost(600L))
                                            }
                                            paraVisible.value = true
                                        }

                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = paraVisible.value,
                                            enter = fadeIn(tween(if (isLastMsg) 500 else 0)) +
                                                    slideInVertically(initialOffsetY = { 20 }, animationSpec = tween(if (isLastMsg) 500 else 0))
                                        ) {

                                        when {
                                            trimmed.contains("BÖLÜM") && (trimmed.contains("TAMAMLANDI") || trimmed.contains("SONU")) -> {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = trimmed.replace("#", "").trim(),
                                                        color = Color(0xFF34D399),
                                                        fontSize = (fontSizeSp.value + 3f).sp,
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        lineHeight = (fontSizeSp.value * 1.45f).sp
                                                    )
                                                }
                                            }
                                            trimmed.startsWith("#") || (trimmed.startsWith("📖") && trimmed.contains("BÖLÜM")) -> {
                                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(1.dp)
                                                            .background(Brush.linearGradient(listOf(Color.Transparent, Color(0x80A78BFA), Color.Transparent)))
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = trimmed.replace("#", "").trim(),
                                                        color = Color(0xFFC4B5FD),
                                                        fontSize = (fontSizeSp.value + 3f).sp,
                                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        lineHeight = (fontSizeSp.value * 1.45f).sp
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(1.dp)
                                                            .background(Brush.linearGradient(listOf(Color.Transparent, Color(0x80A78BFA), Color.Transparent)))
                                                    )
                                                }
                                            }
                                            trimmed == "---" || trimmed == "---\n---" -> {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    repeat(3) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFF8B5CF6).copy(alpha = 0.75f))
                                                        )
                                                        if (it < 2) Spacer(modifier = Modifier.width(8.dp))
                                                    }
                                                }
                                            }
                                            else -> {
                                                Text(
                                                    text = trimmed.removePrefix("*").removeSuffix("*"),
                                                    color = Color(0xFFEDE8F8),
                                                    fontSize = fontSizeSp,
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.Normal,
                                                    lineHeight = (fontSizeSp.value * 1.56f).sp,
                                                    letterSpacing = 0.1.sp
                                                )
                                            }
                                        }
                                        } // AnimatedVisibility kapanış
                                    }
                                }
                            }
                        }

                        // ── SHIMMER SKELETON — AI sahneyi yazıyor ──────────────────
                        if (isSending) {
                            item(key = "sending_book_progress") {
                                val shimmerInfinite = rememberInfiniteTransition(label = "shimmer")
                                val shimmerX by shimmerInfinite.animateFloat(
                                    -1f, 2f,
                                    infiniteRepeatable(tween(1400, easing = LinearEasing)), label = "sx"
                                )
                                val dotAlpha1 by shimmerInfinite.animateFloat(
                                    0.25f, 1f,
                                    infiniteRepeatable(tween(480, 0), RepeatMode.Reverse), label = "d1"
                                )
                                val dotAlpha2 by shimmerInfinite.animateFloat(
                                    0.25f, 1f,
                                    infiniteRepeatable(tween(480, 160), RepeatMode.Reverse), label = "d2"
                                )
                                val dotAlpha3 by shimmerInfinite.animateFloat(
                                    0.25f, 1f,
                                    infiniteRepeatable(tween(480, 320), RepeatMode.Reverse), label = "d3"
                                )

                                val shimmerColors = listOf(
                                    Color(0xFF1A1035),
                                    Color(0xFF2D1F50),
                                    Color(0xFF3B2870),
                                    Color(0xFF2D1F50),
                                    Color(0xFF1A1035)
                                )
                                val shimmerStartX = shimmerX * 600f
                                val shimmerBg = Brush.linearGradient(
                                    colors = shimmerColors,
                                    start = androidx.compose.ui.geometry.Offset(shimmerStartX, 0f),
                                    end = androidx.compose.ui.geometry.Offset(shimmerStartX + 400f, 0f)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0E0920))
                                        .border(1.dp, Color(0xFF2A1D4E), RoundedCornerShape(16.dp))
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Üst satır: ikon + "Sahne yazılıyor" + noktalar
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                                .background(shimmerBg)
                                        )
                                        Text(
                                            "Sahne yazılıyor",
                                            color = Color(0xFFD8B4FE),
                                            fontSize = 12.5.sp,
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf(dotAlpha1, dotAlpha2, dotAlpha3).forEach { alpha ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFA855F7).copy(alpha = alpha))
                                                )
                                            }
                                        }
                                    }
                                    // Shimmer metin satırları
                                    listOf(1f, 0.85f, 0.72f).forEach { widthFraction ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(widthFraction)
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(shimmerBg)
                                        )
                                    }
                                }
                            }
                        }

                        // ── PREMIUM SEÇİM KARTLARI ──────────────────────────────────
                        if (activeChoices.isNotEmpty() && !isSending) {
                            item(key = "interactive_choices_section") {
                                val choiceInfinite = rememberInfiniteTransition(label = "choicePulse")
                                val choiceBorderAlpha by choiceInfinite.animateFloat(
                                    0.5f, 1f,
                                    infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "cb"
                                )
                                // Her seçeneğe farklı renk tonu
                                val choiceAccents = listOf(
                                    listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)), // mor-indigo
                                    listOf(Color(0xFF0E7490), Color(0xFF0891B2)), // teal
                                    listOf(Color(0xFF065F46), Color(0xFF047857)), // yeşil
                                    listOf(Color(0xFF92400E), Color(0xFFB45309))  // amber
                                )
                                val choiceAccentsDim = listOf(
                                    listOf(Color(0xFF3B1C7A), Color(0xFF2D2670)),
                                    listOf(Color(0xFF0A3D4D), Color(0xFF0B4A58)),
                                    listOf(Color(0xFF022C22), Color(0xFF033A2C)),
                                    listOf(Color(0xFF451F06), Color(0xFF5C2A08))
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    // Başlık bandı
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            .background(
                                                Brush.linearGradient(listOf(Color(0xFF0D0820), Color(0xFF150F30)))
                                            )
                                            .border(
                                                width = 1.2.dp,
                                                brush = Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFA855F7).copy(alpha = choiceBorderAlpha),
                                                        Color(0xFF6366F1).copy(alpha = choiceBorderAlpha * 0.7f),
                                                        Color(0xFFA855F7).copy(alpha = choiceBorderAlpha)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFA855F7).copy(alpha = choiceBorderAlpha))
                                            )
                                            Text(
                                                text = "SENİN KARARIN",
                                                color = Color(0xFFD8B4FE),
                                                fontSize = 10.5.sp,
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 2.sp
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "${activeChoices.size} seçenek",
                                                color = Color(0xFF7C3AED).copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Seçim kartları
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                            .background(Color(0xFF0A0618))
                                            .border(
                                                width = 1.2.dp,
                                                brush = Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFA855F7).copy(alpha = choiceBorderAlpha * 0.6f),
                                                        Color(0xFF6366F1).copy(alpha = choiceBorderAlpha * 0.4f)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                                            )
                                            .padding(bottom = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        activeChoices.forEachIndexed { choiceIdx, choice ->
                                            val choiceScale = remember { androidx.compose.animation.core.Animatable(1f) }
                                            val choiceScope = rememberCoroutineScope()
                                            val accentColors = choiceAccents[choiceIdx.coerceAtMost(3)]
                                            val accentDimColors = choiceAccentsDim[choiceIdx.coerceAtMost(3)]
                                            val numLabel = listOf("A", "B", "C", "D")[choiceIdx.coerceAtMost(3)]

                                            if (choiceIdx > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(1.dp)
                                                        .padding(horizontal = 14.dp)
                                                        .background(Color(0xFF1A1035))
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .graphicsLayer { scaleX = choiceScale.value; scaleY = choiceScale.value }
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    ) {
                                                        choiceScope.launch {
                                                            choiceScale.animateTo(0.97f, tween(70))
                                                            choiceScale.animateTo(1f, tween(130))
                                                        }
                                                        onSendMessage(choice.rawLine)
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    // Numara rozeti
                                                    Box(
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(
                                                                Brush.linearGradient(accentDimColors)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                Brush.linearGradient(accentColors),
                                                                RoundedCornerShape(8.dp)
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = numLabel,
                                                            color = Color(accentColors[0].red, accentColors[0].green, accentColors[0].blue, 1f).copy(alpha = 1f),
                                                            fontSize = 12.sp,
                                                            fontFamily = InterFontFamily,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(11.dp))

                                                    // Seçim metni
                                                    Text(
                                                        text = choice.text,
                                                        color = Color(0xFFEDE9FE),
                                                        fontSize = (fontSizeSp.value - 0.5f).sp,
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.weight(1f),
                                                        lineHeight = (fontSizeSp.value * 1.45f).sp
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    // Ok ikonu
                                                    Box(
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.radialGradient(accentDimColors)
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ChevronRight,
                                                            contentDescription = null,
                                                            tint = Color(accentColors[0].red, accentColors[0].green, accentColors[0].blue, 1f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Chapter End Card
                        if (isChapterCompleted && !isSending) {
                            item(key = "chapter_end_card") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF0A2010), Color(0xFF0D1A0D))))
                                        .border(1.5.dp, Color(0xFF34D399), RoundedCornerShape(18.dp))
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = completedChapterTitle,
                                            color = Color(0xFF6EE7B7),
                                            fontSize = 16.sp,
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Text(
                                            text = completedChapterDesc,
                                            color = Color(0xFFD1FAE5),
                                            fontSize = 12.5.sp,
                                            fontFamily = InterFontFamily,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857))))
                                                .clickable { showSummaryModal = true }
                                                .padding(vertical = 11.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "✨ SEÇİM ÖZETİNİ GÖR",
                                                color = Color.White,
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom-Right Page/Chapter Indicator: "✦ 1 / 3" (1:1 from reference)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 11.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_book_page_star),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier
                            .width(23.dp)
                            .height(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$chapterNum / 6",
                        color = Color(0xFFE2D9F3),
                        fontSize = 12.sp,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // ── SİNEMATİK BÖLÜM SONU OVERLAY ────────────────────────────────
        AnimatedVisibility(
            visible = showChapterEndOverlay,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(400))
        ) {
            val confettiColors = listOf(
                Color(0xFF7C3AED), Color(0xFFA78BFA), Color(0xFF34D399),
                Color(0xFFFBBF24), Color(0xFFF472B6), Color(0xFF60A5FA),
                Color(0xFFE879F9), Color(0xFF4ADE80)
            )
            data class ConfettiParticle(
                val x: Float, val y: Float, val vx: Float, val vy: Float,
                val size: Float, val rotation: Float, val vr: Float, val color: Color, val shape: Int
            )

            val overlayInfinite = rememberInfiniteTransition(label = "overlayAnim")
            val overlayTick by overlayInfinite.animateFloat(
                0f, 1f,
                infiniteRepeatable(tween(50, easing = LinearEasing)), label = "ot"
            )

            val particles = remember {
                List(55) {
                    ConfettiParticle(
                        x = (0..100).random() / 100f,
                        y = -(0..60).random() / 100f,
                        vx = ((-3)..3).random() / 300f,
                        vy = (4..10).random() / 250f,
                        size = (6..14).random().toFloat(),
                        rotation = (0..360).random().toFloat(),
                        vr = ((-5)..5).random().toFloat(),
                        color = confettiColors.random(),
                        shape = (0..2).random()
                    )
                }
            }
            val particleStates = remember { particles.map { androidx.compose.animation.core.Animatable(0f) }.toMutableList() }
            LaunchedEffect(Unit) {
                particleStates.forEachIndexed { i, anim ->
                    launch { anim.animateTo(1f, tween(3500 + i * 20, easing = LinearEasing)) }
                }
            }

            val emojiScale = remember { androidx.compose.animation.core.Animatable(0f) }
            val cardSlide = remember { androidx.compose.animation.core.Animatable(120f) }
            LaunchedEffect(Unit) {
                delay(200)
                launch { emojiScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
                launch { cardSlide.animateTo(0f, tween(450, easing = FastOutSlowInEasing)) }
            }

            val overlayGlow by overlayInfinite.animateFloat(
                0.6f, 1f,
                infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "og"
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.radialGradient(listOf(Color(0xCC0A0518), Color(0xEE000005))))
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val t = overlayTick
                    particles.forEachIndexed { i, p ->
                        val progress = particleStates[i].value + (t * 0f)
                        val cx = (p.x + p.vx * progress * 80) * size.width
                        val cy = (p.y + p.vy * progress * 80) * size.height
                        if (cy < 0 || cy > size.height || cx < 0 || cx > size.width) return@forEachIndexed
                        val alpha = if (progress > 0.75f) 1f - (progress - 0.75f) * 4f else 1f
                        val rot = (p.rotation + p.vr * progress * 800) * (Math.PI / 180).toFloat()
                        when (p.shape) {
                            0 -> drawCircle(p.color.copy(alpha = alpha.coerceIn(0f, 1f)), p.size, Offset(cx, cy))
                            1 -> {
                                rotate(degrees = rot * 57.3f, pivot = Offset(cx, cy)) {
                                    drawRect(
                                        p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                        Offset(cx - p.size / 2, cy - p.size / 4),
                                        androidx.compose.ui.geometry.Size(p.size, p.size / 2)
                                    )
                                }
                            }
                            else -> {
                                rotate(degrees = rot * 57.3f, pivot = Offset(cx, cy)) {
                                    drawLine(
                                        p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                        Offset(cx - p.size / 2, cy), Offset(cx + p.size / 2, cy), strokeWidth = 2.5f
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.85f)
                        .graphicsLayer { translationY = cardSlide.value }
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF0F0A20), Color(0xFF1A1035))))
                        .border(
                            2.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7C3AED).copy(alpha = overlayGlow),
                                    Color(0xFF34D399).copy(alpha = overlayGlow),
                                    Color(0xFF7C3AED).copy(alpha = overlayGlow)
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(28.dp)
                ) {
                    // Karar istatistikleri hesapla
                    val userDecisions = remember(messages) { messages.count { it.role == "user" } }
                    val chapterDecisions = when {
                        chapterNum >= 6 -> userDecisions.coerceIn(3, 4)
                        chapterNum >= 4 -> userDecisions.coerceIn(3, 4)
                        chapterNum >= 3 -> userDecisions.coerceIn(4, 8)
                        else -> userDecisions.coerceIn(4, 5)
                    }
                    // İlişki çubuğu animasyonu
                    val steveBar  = remember { androidx.compose.animation.core.Animatable(0f) }
                    val natBar    = remember { androidx.compose.animation.core.Animatable(0f) }
                    val wandaBar  = remember { androidx.compose.animation.core.Animatable(0f) }
                    val samBar    = remember { androidx.compose.animation.core.Animatable(0f) }
                    LaunchedEffect(showChapterEndOverlay) {
                        if (showChapterEndOverlay) {
                            delay(600)
                            launch { steveBar.animateTo(0.72f, tween(900, easing = FastOutSlowInEasing)) }
                            delay(120)
                            launch { natBar.animateTo(0.65f, tween(900, easing = FastOutSlowInEasing)) }
                            delay(120)
                            launch { wandaBar.animateTo(0.58f, tween(900, easing = FastOutSlowInEasing)) }
                            delay(120)
                            launch { samBar.animateTo(0.44f, tween(900, easing = FastOutSlowInEasing)) }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // ── BAŞLIK ALANI ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .graphicsLayer { scaleX = emojiScale.value; scaleY = emojiScale.value }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📖", fontSize = 30.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = completedChapterTitle,
                                    color = Color(0xFF6EE7B7),
                                    fontSize = 17.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Hikayeni şekillendirdin",
                                    color = Color(0xFF9B89CC),
                                    fontSize = 12.sp,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── KARAR İSTATİSTİKLERİ ──
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0D0820))
                                .border(1.dp, Color(0xFF2A1D4E), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Karar
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$chapterDecisions", color = Color(0xFFA855F7), fontSize = 18.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.ExtraBold)
                                Text("Karar", color = Color(0xFF6B5E8A), fontSize = 10.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
                            }
                            Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF2A1D4E)))
                            // Bölüm
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$chapterNum/6", color = Color(0xFF60A5FA), fontSize = 18.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.ExtraBold)
                                Text("Bölüm", color = Color(0xFF6B5E8A), fontSize = 10.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
                            }
                            Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF2A1D4E)))
                            // Sır
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (chapterNum >= 4) "Açık" else "—", color = Color(0xFF34D399), fontSize = 18.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.ExtraBold)
                                Text("Sır", color = Color(0xFF6B5E8A), fontSize = 10.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // ── KARAKTER İLİŞKİ ÇUBUKLARI ──
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0D0820))
                                .border(1.dp, Color(0xFF2A1D4E), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "BAĞLAR",
                                color = Color(0xFF6B5E8A),
                                fontSize = 10.sp,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                            // RelBar inline — 4 karakter
                            listOf(
                                Triple("Steve",   "🛡️", Pair(steveBar.value,  Color(0xFF60A5FA))),
                                Triple("Natasha", "🕷️", Pair(natBar.value,    Color(0xFFFF6B6B))),
                                Triple("Wanda",   "🔮", Pair(wandaBar.value,  Color(0xFFE879F9))),
                                Triple("Sam",     "🦅", Pair(samBar.value,    Color(0xFF34D399)))
                            ).forEach { (name, emoji, pair) ->
                                val (prog, barColor) = pair
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(emoji, fontSize = 14.sp)
                                    Text(
                                        name,
                                        color = Color(0xFFCDB8F5),
                                        fontSize = 11.sp,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(62.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF1E1535))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(prog)
                                                .fillMaxHeight()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(barColor.copy(alpha = 0.7f), barColor)
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── BUTONLAR ──
                        val overlayBtn1Scale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val overlayScope1 = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                                )
                                .graphicsLayer { scaleX = overlayBtn1Scale.value; scaleY = overlayBtn1Scale.value }
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                    overlayScope1.launch {
                                        overlayBtn1Scale.animateTo(0.94f, tween(80))
                                        overlayBtn1Scale.animateTo(1f, tween(120))
                                    }
                                    showChapterEndOverlay = false
                                    showSummaryModal = true
                                }
                                .padding(vertical = 13.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("✨", fontSize = 16.sp)
                                Text(
                                    "Seçim Özetini Gör",
                                    color = Color.White,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val overlayBtn2Scale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val overlayScope2 = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF110C28))
                                .border(1.dp, Color(0xFF2D1F50), RoundedCornerShape(14.dp))
                                .graphicsLayer { scaleX = overlayBtn2Scale.value; scaleY = overlayBtn2Scale.value }
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                    overlayScope2.launch {
                                        overlayBtn2Scale.animateTo(0.94f, tween(80))
                                        overlayBtn2Scale.animateTo(1f, tween(120))
                                        com.example.util.BookAudioController.stop(fadeOutMs = 1500)
                                        showChapterEndOverlay = false
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Şimdi Değil",
                                color = Color(0xFF9F7AEA),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── PAYLAŞIM BUTONU ──────────────────────────────────
                        val shareScope = rememberCoroutineScope()
                        var shareCopied by remember { mutableStateOf(false) }
                        val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0D0820))
                                .border(1.dp, Color(0xFF2A1D4E), RoundedCornerShape(14.dp))
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                    val userChoices = messages.filter { it.role == "user" }
                                    val shareText = buildString {
                                        appendLine("📖 Kozmik Sürükleniş — $completedChapterTitle")
                                        appendLine("Bölüm $chapterNum tamamlandı!")
                                        appendLine()
                                        userChoices.take(4).forEachIndexed { i, msg ->
                                            appendLine("${i + 1}. ${msg.text.take(60).trimEnd()}…")
                                        }
                                        appendLine()
                                        appendLine("#KozmikSuruklenis #InteractiveFiction")
                                    }
                                    clipboard.setText(androidx.compose.ui.text.AnnotatedString(shareText))
                                    shareCopied = true
                                    shareScope.launch {
                                        kotlinx.coroutines.delay(2500)
                                        shareCopied = false
                                    }
                                }
                                .padding(vertical = 11.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (shareCopied) "✓" else "🔗", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (shareCopied) "Panoya kopyalandı!" else "Hikaye yolunu paylaş",
                                color = if (shareCopied) Color(0xFF34D399) else Color(0xFF6B5E8A),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
