
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

private data class ChatCosmicParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speed: Float
)

@Composable
fun CosmicParticleBackground(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CosmicParticleDrift")

    // Rule 2: Soft radial glow breath loop (scale 0.9 -> 1.05 -> 1.0, 4.5s FastOutSlowIn)
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DriftAnim"
    )

    val particles = remember {
        List(35) {
            ChatCosmicParticle(
                xRatio = (0..100).random() / 100f,
                yRatio = (0..100).random() / 100f,
                radius = (15..45).random() / 10f,
                baseAlpha = (12..28).random() / 100f,
                speed = 0.4f + (0..100).random() / 100f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Broad Center Radial Glow with Character Accent Color & Breathing Scale (Rule 2)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0x208B5CF6),
                            Color(0x080F172A),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Drifting Star Dust / Cosmic Energy Particles Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                val currentY = ((p.yRatio - animProgress * p.speed * 0.35f) % 1f + 1f) % 1f
                val currentX = (p.xRatio + kotlin.math.sin((animProgress * 6.28318f + p.yRatio * 12f).toDouble()).toFloat() * 0.04f) % 1f

                drawCircle(
                    color = accentColor.copy(alpha = p.baseAlpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(currentX * width, currentY * height)
                )
            }
        }

        // 3. Top Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xEE070814), Color.Transparent)
                    )
                )
        )

        // 4. Bottom Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xEE070814))
                    )
                )
        )
    }
}

@Composable
fun PulsingLoadingText(
    modifier: Modifier = Modifier
) {
    // Rule 3: 2px fine progress line filling from left to right over 1.5s + shimmer text
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmerLine")
    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LineFill"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShimmerAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(AppColors.PurpleSoft.copy(alpha = shimmerAlpha))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sonuçlar dokunuyor...",
                color = AppColors.PurpleLight.copy(alpha = shimmerAlpha),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                letterSpacing = 0.9.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2px sleek progress fill bar (Rule 3)
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color(0x308B5CF6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(lineProgress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(AppColors.DeepPurple, AppColors.PurpleSoft)
                        )
                    )
            )
        }
    }
}

@Composable
fun CinematicSequentialReveal(
    choice: ChapterChoiceItem,
    phase: StepPhase
) {
    // Screen Shake effect for Branching choices
    val shakeAnim = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase == StepPhase.RESULT_REVEALED && choice.isBranching) {
            repeat(3) {
                shakeAnim.animateTo(4f, androidx.compose.animation.core.tween(30))
                shakeAnim.animateTo(-4f, androidx.compose.animation.core.tween(30))
            }
            shakeAnim.animateTo(0f, androidx.compose.animation.core.tween(30))
        }
    }

    // Rule 4: Overshoot Elastic Icon Reveal (scale 0 -> 1.35 -> 1.0)
    val iconScaleAnim = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase == StepPhase.RESULT_REVEALED) {
            iconScaleAnim.snapTo(0f)
            iconScaleAnim.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
            iconScaleAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(160, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
        }
    }

    val impactColor = when {
        choice.isBranching -> AppColors.WarningAmber // Gold / Amber
        choice.isRelationship -> AppColors.PinkLight // Pink / Magenta
        else -> AppColors.TextSecondary // Slate / Neutral
    }

    // Rule 5: Mood Shift Smooth Color Accent (800ms transition)
    val animatedImpactColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (phase == StepPhase.RESULT_REVEALED) impactColor else AppColors.DeepPurple,
        animationSpec = tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "MoodShiftColor"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(x = shakeAnim.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. ADIM A: Small, subtle top title (e.g., "4. BAKIŞ ODAK NOKTASI")
        Text(
            text = choice.title.uppercase(),
            color = AppColors.PrimaryPurple,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. ADIM A (Rule 1 & 8): Selected Option (opacity 0->1, y: +25px->0, 600ms ease-out, 150ms delay)
        // Rule 8: Exit with scale 1->0.95 + fade out
        androidx.compose.animation.AnimatedContent(
            targetState = choice.selectedOption,
            transitionSpec = {
                (androidx.compose.animation.fadeIn(animationSpec = tween(600, delayMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                        androidx.compose.animation.slideInVertically(
                            initialOffsetY = { 25 },
                            animationSpec = tween(600, delayMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        ) +
                        androidx.compose.animation.scaleIn(initialScale = 0.96f, animationSpec = tween(600, delayMillis = 150))) togetherWith
                        (androidx.compose.animation.fadeOut(animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)) +
                                androidx.compose.animation.scaleOut(targetScale = 0.95f, animationSpec = tween(300)))
            },
            label = "ActionTextAnim"
        ) { text ->
            Text(
                text = text,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (phase == StepPhase.CARD_ONLY) {
            // 3. ADIM A: Vivid Pulsing Loading Indicator Text
            PulsingLoadingText()
        } else {
            // GEÇİŞ & ADIM B: Light Thread + Impact Reveal
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(600)) +
                        androidx.compose.animation.expandVertically(animationSpec = androidx.compose.animation.core.tween(600))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dikey Işık Huzmesi (Connecting Light Thread)
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(44.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(AppColors.DeepPurple, animatedImpactColor)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pulse Icon & Impact Tag & Consequence Text (Floating freely in space)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = choice.icon,
                                fontSize = 23.sp,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = iconScaleAnim.value
                                    scaleY = iconScaleAnim.value
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    choice.isBranching -> "💥 BÜYÜK DALLANMA ETKİSİ"
                                    choice.isRelationship -> "💕 İLİŞKİ DOKUNUŞU"
                                    else -> "💭 TAVIR ETKİSİ"
                                },
                                color = animatedImpactColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── ETKİ SEVİYESİ ROZETİ ──────────────────────────────
                        val (levelLabel, levelColor, levelBg) = when (choice.impactLevel) {
                            "kritik" -> Triple("⚡ KRİTİK ETKİ", androidx.compose.ui.graphics.Color(0xFFFDE047), androidx.compose.ui.graphics.Color(0xFF451A03))
                            "dusuk"  -> Triple("· DÜŞÜK ETKİ",   androidx.compose.ui.graphics.Color(0xFF94A3B8), androidx.compose.ui.graphics.Color(0xFF1E293B))
                            else     -> Triple("◉ ORTA ETKİ",    androidx.compose.ui.graphics.Color(0xFFC084FC), androidx.compose.ui.graphics.Color(0xFF2E1065))
                        }
                        Box(
                            modifier = androidx.compose.ui.Modifier
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                .background(levelBg)
                                .border(
                                    0.8.dp,
                                    levelColor.copy(alpha = 0.4f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                levelLabel,
                                color = levelColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = choice.impactText,
                            color = AppColors.TextWhite,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 23.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryThreadTimeline(
    choices: List<ChapterChoiceItem>,
    relationshipTease: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        // Continuous Vertical Thread Line
        Box(
            modifier = Modifier
                .padding(start = 11.dp, top = 12.dp, bottom = 12.dp)
                .width(2.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(AppColors.DeepPurple, AppColors.PrimaryViolet, Color(0x308B5CF6))
                    )
                )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            choices.forEachIndexed { idx, choice ->
                // Staggered animated appearance
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(idx * 90L)
                    isVisible = true
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) +
                            androidx.compose.animation.slideInVertically(animationSpec = androidx.compose.animation.core.tween(400)) { 20 }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Node Dot on Timeline
                        val nodeColor = when {
                            choice.isBranching -> AppColors.WarningAmber
                            choice.isRelationship -> AppColors.PinkLight
                            else -> AppColors.PrimaryPurple
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(nodeColor)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Timeline Item Content (Clean, unboxed composition)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = choice.title,
                                color = AppColors.PrimaryPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = choice.selectedOption,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            // Etki seviyesi rozeti
                            val (lvlLabel, lvlColor) = when (choice.impactLevel) {
                                "kritik" -> Pair("⚡ KRİTİK", androidx.compose.ui.graphics.Color(0xFFFDE047))
                                "dusuk"  -> Pair("· DÜŞÜK",  androidx.compose.ui.graphics.Color(0xFF94A3B8))
                                else     -> Pair("◉ ORTA",   androidx.compose.ui.graphics.Color(0xFFC084FC))
                            }
                            Box(
                                modifier = Modifier
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                                    .background(lvlColor.copy(alpha = 0.12f))
                                    .border(0.5.dp, lvlColor.copy(alpha = 0.35f), androidx.compose.foundation.shape.RoundedCornerShape(5.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(lvlLabel, color = lvlColor, fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(choice.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = choice.impactText,
                                    color = AppColors.TextSecondary,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            // Dal spoiler toggle — sadece dallanma noktalarında
                            if (choice.isBranching) {
                                var spoilerOpen by remember(choice.id) { mutableStateOf(false) }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                        .background(androidx.compose.ui.graphics.Color(0xFF1A0D35))
                                        .border(
                                            0.8.dp,
                                            AppColors.WarningAmber.copy(alpha = if (spoilerOpen) 0.5f else 0.2f),
                                            androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        )
                                        .clickable { spoilerOpen = !spoilerOpen }
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(if (spoilerOpen) "▲" else "▼", color = AppColors.WarningAmber, fontSize = 9.sp)
                                            Text(
                                                if (spoilerOpen) "Alternatif yolu gizle" else "🔀 Diğer yolu merak ediyorum...",
                                                color = AppColors.WarningAmber,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = spoilerOpen,
                                            enter = androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300)) +
                                                    androidx.compose.animation.expandVertically(androidx.compose.animation.core.tween(300))
                                        ) {
                                            Column {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Box(modifier = Modifier.fillMaxWidth().height(1.dp)
                                                    .background(AppColors.WarningAmber.copy(alpha = 0.2f)))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "Bu noktada farklı bir seçim yapsaydın hikaye dallanırdı. Her seçim kalıcı — ancak sonraki bölümde farklı yolu deneyebilirsin.",
                                                    color = AppColors.TextMuted,
                                                    fontSize = 11.5.sp,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (relationshipTease != null) {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(choices.size * 90L + 100L)
                    isVisible = true
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) +
                            androidx.compose.animation.slideInVertically(animationSpec = androidx.compose.animation.core.tween(400)) { 20 }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Pink)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔮 İLERLEYEN BÖLÜM ETKİSİ",
                                color = AppColors.PinkLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = relationshipTease,
                                color = AppColors.Hex_FBCFE8,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryChoiceCard(
    title: String,
    choiceText: String,
    impact: String,
    icon: String = "💭",
    isBranching: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isBranching) AppColors.Hex_2C192E else AppColors.Hex_181728),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isBranching) AppColors.RoseSoft else AppColors.Hex_382F58
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = if (isBranching) AppColors.RoseLight else AppColors.PrimaryPurple,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = choiceText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = if (isBranching) AppColors.Hex_3B1220 else AppColors.Hex_121220,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
                Text(
                    text = impact,
                    color = if (isBranching) AppColors.Hex_FECDD3 else AppColors.TextSecondary,
                    fontSize = 12.5.sp,
                    lineHeight = 17.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
