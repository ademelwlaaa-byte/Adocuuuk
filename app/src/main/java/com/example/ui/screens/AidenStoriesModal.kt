
package com.example.ui.screens
import com.example.ui.theme.AppColors
import com.example.ui.theme.EmochiGradients
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import com.example.ui.theme.InterFontFamily
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.BotEntity
import com.example.data.local.UserSettingsEntity
import com.example.ui.components.AppBackground
import com.example.ui.components.GlobalSettingsModal
import com.example.ui.components.MoodColors
import com.example.ui.components.OrbView
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.EmochiBackground
import com.example.ui.theme.EmochiBorder
import com.example.ui.theme.EmochiCard
import com.example.ui.theme.EmochiError
import com.example.ui.theme.EmochiPrimary
import com.example.ui.theme.EmochiSurface
import com.example.ui.theme.EmochiTextMuted
import com.example.ui.theme.EmochiTextPrimary
import com.example.ui.theme.EmochiTextSecondary

@Composable
fun AidenStoriesModal(
    userSettings: UserSettingsEntity?,
    onDismiss: () -> Unit,
    onImportPresetBot: ((BotEntity) -> Unit)?
) {
    val isEnglish = userSettings?.appLanguage == "en"
    val aidenPresets = remember(isEnglish) { getAidenPresetsList(isEnglish) }
    var selectedPresetId by remember { mutableStateOf("preset_aiden_zoktay") }

    val infiniteTransition = rememberInfiniteTransition(label = "aiden_stories_modal_anim")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aiden_modal_glow"
    )
    val boltScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aiden_bolt_scale"
    )

    data class AidenStoryRowSpec(
        val presetId: String,
        val thumbRes: Int,
        val title: String,
        val badgeIcon: String,
        val badgeText: String,
        val badgeBg: Color,
        val badgeBorder: Color,
        val badgeTextColor: Color,
        val desc: String
    )

    val storyRows = remember(isEnglish) {
        listOf(
            AidenStoryRowSpec(
                presetId = "preset_aiden_zoktay",
                thumbRes = R.drawable.img_aiden_row1_gs,
                title = if (isEnglish) "Season 1 • Galatasaray" else "Sezon 1 • Galatasaray",
                badgeIcon = "⚽",
                badgeText = if (isEnglish) "Sports & Romance" else "Spor & Romantizm",
                badgeBg = Color(0xFF381238),
                badgeBorder = Color(0xFFD946EF),
                badgeTextColor = Color(0xFFF5D0FE),
                desc = if (isEnglish)
                    "Crossing paths in Istanbul with Galatasaray's new #9 Aiden Blackwood."
                else
                    "Galatasaray'ın yeni 9 numarası Aiden Blackwood ile İstanbul'da kesişen yollar."
            ),
            AidenStoryRowSpec(
                presetId = "preset_aiden_dispatch",
                thumbRes = R.drawable.img_aiden_row2_dispatch,
                title = if (isEnglish) "Dispatch Universe • Secret Identity" else "Dispatch Evreni • Gizli Kimlik",
                badgeIcon = "🦸",
                badgeText = if (isEnglish) "Superhero & Action" else "Süper Kahraman & Aksiyon",
                badgeBg = Color(0xFF28144A),
                badgeBorder = Color(0xFFA855F7),
                badgeTextColor = Color(0xFFE9D5FF),
                desc = if (isEnglish)
                    "An ordinary clerk at SDN by day, a legendary hero by night..."
                else
                    "Gündüzleri SDN'de sıradan bir memur, geceleri ise efsanevi kahraman..."
            ),
            AidenStoryRowSpec(
                presetId = "preset_aiden_joker_emma",
                thumbRes = R.drawable.img_aiden_row3_joker,
                title = if (isEnglish) "Dark Knight • Joker Obsession" else "Karanlık Şövalye • Joker Takıntısı",
                badgeIcon = "🃏",
                badgeText = if (isEnglish) "Dark Romance & Thriller" else "Karanlık Romantizm & Gerilim",
                badgeBg = Color(0xFF3B1234),
                badgeBorder = Color(0xFFEC4899),
                badgeTextColor = Color(0xFFFBCFE8),
                desc = if (isEnglish)
                    "Gotham's protector Aiden and his most dangerous weakness: the Joker (you)."
                else
                    "Gotham'ın koruyucusu Aiden ve onun en tehlikeli zaafı olan Joker (sen)."
            ),
            AidenStoryRowSpec(
                presetId = "preset_aiden_doctor_jenna",
                thumbRes = R.drawable.img_aiden_row4_hospital,
                title = if (isEnglish) "Special Scenario • Alone Together" else "Özel Senaryo • Başbaşa",
                badgeIcon = "🏥",
                badgeText = if (isEnglish) "Drama & Emotional" else "Dram & Duygusal",
                badgeBg = Color(0xFF2A144A),
                badgeBorder = Color(0xFFA855F7),
                badgeTextColor = Color(0xFFE9D5FF),
                desc = if (isEnglish)
                    "Visiting Aiden in his private hospital room after a training injury."
                else
                    "Antrenmanda sakatlanan Aiden'ı özel hastane odasında ziyaret ediyorsun."
            ),
            AidenStoryRowSpec(
                presetId = "preset_aiden_obsidian_sydney",
                thumbRes = R.drawable.img_aiden_row5_tokyo,
                title = if (isEnglish) "Alternate Universe • Street Race" else "Alternatif Evren • Yasa Dışı Yarış",
                badgeIcon = "🏎️",
                badgeText = if (isEnglish) "Action & Street Racing" else "Aksiyon & Sokak Yarışı",
                badgeBg = Color(0xFF38123A),
                badgeBorder = Color(0xFFD946EF),
                badgeTextColor = Color(0xFFF5D0FE),
                desc = if (isEnglish)
                    "Invincible street racer Aiden under Tokyo's neon lights and his greatest rival..."
                else
                    "Tokyo'nun neon ışıklarında yenilmez sokak yarışçısı Aiden ve en büyük rakibi..."
            )
        )
    }

    fun launchPreset(preset: BotEntity) {
        val newBot = preset.copy(
            id = java.util.UUID.randomUUID().toString(),
            isPublic = false,
            isTemplate = false
        )
        onImportPresetBot?.invoke(newBot)
        onDismiss()
    }

    val featuredPreset = remember(aidenPresets) {
        aidenPresets.find { it.id == "preset_aiden_zoktay" } ?: aidenPresets.firstOrNull()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC04010A))
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Modal Frame with Neon Purple Rim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = androidx.compose.ui.graphics.Paint().apply {
                                style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                strokeWidth = 2.5.dp.toPx()
                                color = Color(0xFFA855F7).copy(alpha = 0.45f * glowPulse)
                            }
                            paint.asFrameworkPaint().maskFilter =
                                android.graphics.BlurMaskFilter(14f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                            canvas.drawRoundRect(
                                0f,
                                0f,
                                size.width,
                                size.height,
                                28.dp.toPx(),
                                28.dp.toPx(),
                                paint
                            )
                        }
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF100520),
                                Color(0xFF0A0314),
                                Color(0xFF090212)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE9D5FF).copy(alpha = 0.85f),
                                Color(0xFFA855F7).copy(alpha = 0.55f),
                                Color(0xFFC084FC).copy(alpha = 0.75f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ── 1. TOP HERO HEADER BANNER ──────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(106.dp)
                            .background(Color(0xFF0D041A))
                    ) {
                        // Right-side Bosphorus & Aiden #9 Scene seamlessly blended
                        Image(
                            painter = painterResource(id = R.drawable.img_aiden_header_bosphorus),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopEnd,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(195.dp)
                                .align(Alignment.TopEnd)
                                .drawWithContent {
                                    drawContent()
                                    // Smooth left fade into header background
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            0.0f to Color(0xFF0D041A),
                                            0.22f to Color(0x990D041A),
                                            0.42f to Color.Transparent
                                        )
                                    )
                                    // Subtle bottom fade
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            0.75f to Color.Transparent,
                                            1.0f to Color(0xFF0D041A)
                                        )
                                    )
                                }
                        )

                        // Left ambient violet glow
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(240.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF6B21A8).copy(alpha = 0.28f),
                                            Color.Transparent
                                        ),
                                        center = Offset(90f, 90f),
                                        radius = 260f
                                    )
                                )
                        )

                        // Left Title + Neon "Stories" Script + Subtitle
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 10.dp, top = 4.dp, end = 64.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_aiden_header_bolt),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .graphicsLayer {
                                            scaleX = boltScale
                                            scaleY = boltScale
                                        }
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Aiden Blackwood",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.5.sp,
                                    letterSpacing = (-0.5).sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.img_aiden_stories_script),
                                    contentDescription = "Stories",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .width(82.dp)
                                        .height(38.dp)
                                        .offset(y = (-2).dp)
                                )
                            }
                            Text(
                                text = if (isEnglish)
                                    "Exclusive character universes and alternate story collection"
                                else
                                    "Özel karakter evrenleri ve alternatif hikaye koleksiyonu",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = Color(0xFFD8B4FE).copy(alpha = 0.9f),
                                modifier = Modifier
                                    .padding(start = 34.dp)
                                    .offset(y = (-4).dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Top-Right Close Button '✕' aligned seamlessly over the circle
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 20.dp, end = 15.dp)
                                .size(37.dp)
                                .clip(CircleShape)
                                .background(Color(0xD9120726))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFC084FC).copy(alpha = 0.7f),
                                            Color(0xFF6B21A8).copy(alpha = 0.5f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clickable { onDismiss() }
                                .testTag("aiden_stories_close_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Bottom Glowing Divider Line of Header
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(1.5.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF581C87).copy(alpha = 0.4f),
                                            Color(0xFFD8B4FE).copy(alpha = 0.95f),
                                            Color(0xFFA855F7).copy(alpha = 0.85f),
                                            Color(0xFF581C87).copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )
                    }

                    // ── 2. MAIN CONTENT BODY ───────────────────────────────────
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 13.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        // ── ÖNE ÇIKAN EVREN HEADER ROW ─────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "✦",
                                    fontSize = 12.sp,
                                    color = Color(0xFFC084FC),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "FEATURED UNIVERSE" else "ÖNE ÇIKAN EVREN",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.0.sp,
                                    color = Color(0xFFE9D5FF)
                                )
                            }
                            Text(
                                text = if (isEnglish) "5 Stories Available" else "5 Hikaye Mevcut",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.5.sp,
                                color = Color(0xFFC4B5FD).copy(alpha = 0.85f)
                            )
                        }

                        // ── FEATURED HERO CARD (ANA EVREN - Sezon 1 • Galatasaray) ──
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(126.dp)
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        val paint = androidx.compose.ui.graphics.Paint().apply {
                                            style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                            strokeWidth = 2.dp.toPx()
                                            color = Color(0xFFC084FC).copy(alpha = 0.5f * glowPulse)
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
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF18082C),
                                            Color(0xFF1C0A35),
                                            Color(0xFF130624)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFE9D5FF),
                                            Color(0xFFA855F7),
                                            Color(0xFFE9D5FF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    featuredPreset?.let { preset ->
                                        launchPreset(preset)
                                    }
                                }
                                .testTag("aiden_featured_universe_card")
                        ) {
                            // Left Stadium Portrait of Aiden (#9) with smooth horizontal blend
                            Image(
                                painter = painterResource(id = R.drawable.img_aiden_hero_stadium),
                                contentDescription = "Sezon 1 Galatasaray",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .width(168.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.CenterStart)
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.horizontalGradient(
                                                0.0f to Color.Transparent,
                                                0.68f to Color.Transparent,
                                                0.90f to Color(0xCC1A0931),
                                                1.0f to Color(0xFF1A0931)
                                            )
                                        )
                                    }
                            )

                            // Right Column Details
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 152.dp, end = 12.dp, top = 9.dp, bottom = 9.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top "✦ ANA EVREN" Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFF2B124C))
                                        .border(
                                            width = 0.9.dp,
                                            color = Color(0xFFC084FC).copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "✦",
                                            fontSize = 9.sp,
                                            color = Color(0xFFFBBF24),
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = if (isEnglish) "MAIN UNIVERSE" else "ANA EVREN",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 8.5.sp,
                                            letterSpacing = 0.6.sp,
                                            color = Color(0xFFF3E8FF)
                                        )
                                    }
                                }

                                // Title
                                Text(
                                    text = if (isEnglish) "Season 1 • Galatasaray" else "Sezon 1 • Galatasaray",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    letterSpacing = (-0.3).sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Description
                                Text(
                                    text = if (isEnglish)
                                        "British-Turkish superstar striker Aiden Blackwood's debut season at Galatasaray and new life in Istanbul."
                                    else
                                        "İngiliz-Türk süperstar forvet Aiden Blackwood'un Galatasaray'daki ilk sezonu ve İstanbul'daki yeni hayatı.",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 9.5.sp,
                                    lineHeight = 12.sp,
                                    color = Color(0xFFE9D5FF).copy(alpha = 0.9f),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Bottom-Right "⚡ Hikayeyi Başlat →" Pill Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .height(26.dp)
                                        .drawBehind {
                                            drawIntoCanvas { canvas ->
                                                val paint = androidx.compose.ui.graphics.Paint().apply {
                                                    style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                                    strokeWidth = 2.dp.toPx()
                                                    color = Color(0xFFD8B4FE).copy(alpha = 0.65f * glowPulse)
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
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFA855F7),
                                                    Color(0xFF9333EA),
                                                    Color(0xFFC084FC)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFF3E8FF).copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 11.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "⚡",
                                            fontSize = 9.5.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isEnglish) "Start Story" else "Hikayeyi Başlat",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "→",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // ── TÜM AIDEN HİKAYELERİ SECTION HEADER ────────────────
                        Text(
                            text = if (isEnglish) "ALL AIDEN STORIES" else "TÜM AIDEN HİKAYELERİ",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.0.sp,
                            color = Color(0xFFD8B4FE).copy(alpha = 0.9f),
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                        )

                        // ── 5 STORY LIST CARDS ─────────────────────────────────
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            storyRows.forEachIndexed { index, rowSpec ->
                                val matchingPreset = aidenPresets.find { it.id == rowSpec.presetId }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = if (index == 0) {
                                                    listOf(
                                                        Color(0xFF1F0C38),
                                                        Color(0xFF17082A),
                                                        Color(0xFF1E0B36)
                                                    )
                                                } else {
                                                    listOf(
                                                        Color(0xFF160828),
                                                        Color(0xFF120621),
                                                        Color(0xFF160828)
                                                    )
                                                }
                                            )
                                        )
                                        .border(
                                            width = if (index == 0) 1.3.dp else 1.dp,
                                            brush = Brush.horizontalGradient(
                                                colors = if (index == 0) {
                                                    listOf(
                                                        Color(0xFFD8B4FE).copy(alpha = 0.85f),
                                                        Color(0xFFA855F7).copy(alpha = 0.65f),
                                                        Color(0xFFD8B4FE).copy(alpha = 0.85f)
                                                    )
                                                } else {
                                                    listOf(
                                                        Color(0xFF9333EA).copy(alpha = 0.55f),
                                                        Color(0xFF6B21A8).copy(alpha = 0.4f),
                                                        Color(0xFF9333EA).copy(alpha = 0.55f)
                                                    )
                                                }
                                            ),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .clickable {
                                            selectedPresetId = rowSpec.presetId
                                            matchingPreset?.let { preset ->
                                                launchPreset(preset)
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                        .testTag("aiden_story_row_${rowSpec.presetId}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left Square Thumbnail
                                        Image(
                                            painter = painterResource(id = rowSpec.thumbRes),
                                            contentDescription = rowSpec.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(11.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFFC084FC).copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(11.dp)
                                                )
                                        )

                                        Spacer(modifier = Modifier.width(9.dp))

                                        // Middle Title + Category Pill + Description
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = rowSpec.title,
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                letterSpacing = (-0.2).sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            // Category Pill Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(rowSpec.badgeBg)
                                                    .border(
                                                        width = 0.8.dp,
                                                        color = rowSpec.badgeBorder.copy(alpha = 0.75f),
                                                        shape = RoundedCornerShape(50)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text(
                                                        text = rowSpec.badgeIcon,
                                                        fontSize = 8.sp
                                                    )
                                                    Text(
                                                        text = rowSpec.badgeText,
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 8.5.sp,
                                                        color = rowSpec.badgeTextColor,
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            // 2-line Description
                                            Text(
                                                text = rowSpec.desc,
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 9.sp,
                                                lineHeight = 11.2.sp,
                                                color = Color(0xFFD8B4FE).copy(alpha = 0.82f),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        // Right Pill Action Button ("⚡ Seç" on Row 1, "✦ Keşfet" on Rows 2..5)
                                        if (index == 0) {
                                            Box(
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .drawBehind {
                                                        drawIntoCanvas { canvas ->
                                                            val paint = androidx.compose.ui.graphics.Paint().apply {
                                                                style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                                                strokeWidth = 1.5.dp.toPx()
                                                                color = Color(0xFFC084FC).copy(alpha = 0.55f * glowPulse)
                                                            }
                                                            paint.asFrameworkPaint().maskFilter =
                                                                android.graphics.BlurMaskFilter(7f, android.graphics.BlurMaskFilter.Blur.NORMAL)
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
                                                    .clip(RoundedCornerShape(50))
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            colors = listOf(
                                                                Color(0xFFA855F7),
                                                                Color(0xFF7E22CE)
                                                            )
                                                        )
                                                    )
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color(0xFFE9D5FF).copy(alpha = 0.85f),
                                                        shape = RoundedCornerShape(50)
                                                    )
                                                    .padding(horizontal = 11.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text(
                                                        text = "⚡",
                                                        fontSize = 9.5.sp,
                                                        color = Color.White
                                                    )
                                                    Text(
                                                        text = if (isEnglish) "Select" else "Seç",
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(Color(0xFF23103C))
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color(0xFFA855F7).copy(alpha = 0.65f),
                                                        shape = RoundedCornerShape(50)
                                                    )
                                                    .padding(horizontal = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text(
                                                        text = "✦",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFD8B4FE)
                                                    )
                                                    Text(
                                                        text = if (isEnglish) "Explore" else "Keşfet",
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFFE9D5FF)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── 3. BOTTOM CHARACTER PROFILE FOOTER CARD ────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF16092A),
                                            Color(0xFF100520)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.2.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7).copy(alpha = 0.7f),
                                            Color(0xFFD8B4FE).copy(alpha = 0.85f),
                                            Color(0xFFA855F7).copy(alpha = 0.7f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 9.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                // Top Row: Circular Avatar with GS Badge + Info Column
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_aiden_profile_badge_avatar),
                                        contentDescription = "Aiden Blackwood GS",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .width(78.dp)
                                            .height(74.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = "Aiden Blackwood",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.5.sp,
                                            letterSpacing = (-0.3).sp,
                                            color = Color.White
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Text(
                                                text = "⚡",
                                                fontSize = 9.5.sp,
                                                color = Color(0xFFC084FC)
                                            )
                                            Text(
                                                text = "GALATASARAY SK #9",
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.5.sp,
                                                letterSpacing = 0.6.sp,
                                                color = Color(0xFFC084FC)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(1.dp))

                                        // Inner Bio Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF1D0D35))
                                                .border(
                                                    width = 0.8.dp,
                                                    color = Color(0xFF6B21A8).copy(alpha = 0.7f),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFFC084FC),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text(
                                                        text = if (isEnglish)
                                                            "Striker (ST) • Age 19 • 1.89m"
                                                        else
                                                            "Forvet (ST) • 19 Yaş • 1.89m",
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 9.5.sp,
                                                        color = Color(0xFFF3E8FF),
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = if (isEnglish)
                                                            "Disciplined, cold-blooded, charismatic leader"
                                                        else
                                                            "Disiplinli, soğukkanlı, karizmatik lider",
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 8.5.sp,
                                                        color = Color(0xFFC4B5FD).copy(alpha = 0.85f),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Bottom Row: 2 Stat Pills + Italic Quote
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Pill 1: Location
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF1F0E38))
                                            .border(
                                                width = 0.8.dp,
                                                color = Color(0xFF7E22CE).copy(alpha = 0.65f),
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(horizontal = 7.dp, vertical = 3.5.dp)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "📍 London → Istanbul" else "📍 Londra → İstanbul",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 8.5.sp,
                                            color = Color(0xFFE9D5FF),
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // Pill 2: Market Value
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFF1F0E38))
                                            .border(
                                                width = 0.8.dp,
                                                color = Color(0xFF7E22CE).copy(alpha = 0.65f),
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(horizontal = 7.dp, vertical = 3.5.dp)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "💎 €80M Market Value" else "💎 €80M Piyasa Değeri",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 8.5.sp,
                                            color = Color(0xFFE9D5FF),
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Quote
                                    Text(
                                        text = if (isEnglish)
                                            "\"There is no room for emotions on the pitch. Only victory.\""
                                        else
                                            "\"Sahada duygulara yer yoktur. Sadece zafere yer vardır.\"",
                                        fontFamily = InterFontFamily,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 8.sp,
                                        lineHeight = 10.sp,
                                        color = Color(0xFFD8B4FE).copy(alpha = 0.85f),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
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

@Composable
fun SafeAppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.Hex_1E1F30)
            .border(1.dp, EmochiBorder, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.ic_vai_logo,
            contentDescription = "Velora Ado AI Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
