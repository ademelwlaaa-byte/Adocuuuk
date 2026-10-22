
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
fun BotCardItem(
    bot: BotEntity,
    confirmDeleteId: String?,
    onConfirmDeleteChange: (String?) -> Unit,
    onOpenBot: (String) -> Unit,
    onDeleteBot: ((String) -> Unit)? = null,
    onTogglePrivacy: ((BotEntity) -> Unit)? = null,
    onCloneBot: ((String) -> Unit)? = null
) {
    val isUniverse = bot.mode == "universe"
    val displayName = when {
        bot.id == "starter_aetheria" || bot.universeName.startsWith("Aetheria: Neon", ignoreCase = true) -> "Aetheria: Neon ..."
        isUniverse -> bot.universeName.ifBlank { "Evren" }
        else -> bot.aiName.ifBlank { "Karakter" }
    }
    val hue = MoodColors.getMoodHue(if (isUniverse) "curious" else "calm")
    val isTemplate = bot.isTemplate || bot.id.startsWith("starter_") || bot.id.startsWith("preset_")

    var isMenuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Press scale animation
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.974f else 1f,
        animationSpec = tween(110, easing = FastOutSlowInEasing),
        label = "cardPressScale"
    )

    // Per-bot phase offset — each card breathes and shimmers smoothly
    val animOffset = remember(bot.id) { (bot.id.hashCode().and(0xFF)) / 255f }
    val infiniteTransition = rememberInfiniteTransition(label = "cardAnim_${bot.id}")
    val cardGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400 + (animOffset * 600).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardGlowAlpha"
    )
    val avatarRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000 + (animOffset * 2000).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatarRing"
    )
    val btnShimmerX by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 450f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + (animOffset * 500).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "btnShimmerX"
    )

    val cardShape = RoundedCornerShape(26.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val baseGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 2.2.dp.toPx()
                        color = Color(0xFF9333EA).copy(alpha = 0.42f * cardGlowAlpha).toArgb()
                        maskFilter = android.graphics.BlurMaskFilter(
                            9.dp.toPx(),
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                    }
                    canvas.nativeCanvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        26.dp.toPx(),
                        26.dp.toPx(),
                        baseGlow
                    )

                    val hotspotPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 2.dp.toPx()
                        color = Color(0xFFD8B4FE).copy(alpha = 0.50f * cardGlowAlpha).toArgb()
                        maskFilter = android.graphics.BlurMaskFilter(
                            6.dp.toPx(),
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                    }
                    canvas.nativeCanvas.drawLine(
                        24.dp.toPx(),
                        0f,
                        size.width * 0.34f,
                        0f,
                        hotspotPaint
                    )
                    canvas.nativeCanvas.drawLine(
                        size.width * 0.64f,
                        size.height,
                        size.width - 24.dp.toPx(),
                        size.height,
                        hotspotPaint
                    )
                }
            }
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A0D3A),
                        Color(0xFF0E0720),
                        Color(0xFF150A30)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 500f)
                )
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x508B5CF6),
                            Color(0x204C1D95),
                            Color.Transparent
                        ),
                        center = Offset(70.dp.toPx(), 72.dp.toPx()),
                        radius = 115.dp.toPx()
                    ),
                    center = Offset(70.dp.toPx(), 72.dp.toPx()),
                    radius = 115.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x459333EA),
                            Color(0x186D28D9),
                            Color.Transparent
                        ),
                        center = Offset(size.width - 80.dp.toPx(), size.height - 24.dp.toPx()),
                        radius = 115.dp.toPx()
                    ),
                    center = Offset(size.width - 80.dp.toPx(), size.height - 24.dp.toPx()),
                    radius = 115.dp.toPx()
                )
            }
            .border(
                BorderStroke(
                    width = 1.4.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFE9D5FF),
                            Color(0xFFA855F7),
                            Color(0xFF5B21B6),
                            Color(0xFFD8B4FE)
                        )
                    )
                ),
                cardShape
            )
            .clickable(interactionSource = interactionSource, indication = null) { onOpenBot(bot.id) }
            .testTag("bot_card_${bot.id}")
            .padding(start = 15.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(110.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val avatarGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 2.5.dp.toPx()
                                    color = if (isUniverse) {
                                        Color(0xFF7C3AED).copy(alpha = 0.50f * cardGlowAlpha).toArgb()
                                    } else {
                                        Color(0xFFC084FC).copy(alpha = 0.62f * cardGlowAlpha).toArgb()
                                    }
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        8.dp.toPx(),
                                        android.graphics.BlurMaskFilter.Blur.NORMAL
                                    )
                                }
                                canvas.nativeCanvas.drawCircle(
                                    size.width / 2f,
                                    size.height / 2f,
                                    54.dp.toPx(),
                                    avatarGlow
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val avatarShape = CircleShape

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(avatarShape)
                            .border(
                                width = 1.6.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFAF5FF),
                                        Color(0xFFD8B4FE),
                                        Color(0xFF9333EA),
                                        Color(0xFF6D28D9),
                                        Color(0xFFD8B4FE),
                                        Color(0xFFFAF5FF)
                                    )
                                ),
                                shape = avatarShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            bot.id == "starter_ayla" || bot.aiName.equals("Ayla", ignoreCase = true) -> {
                                Image(
                                    painter = painterResource(id = R.drawable.img_ayla_avatar),
                                    contentDescription = displayName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(avatarShape)
                                )
                            }
                            bot.id == "starter_aetheria" || bot.universeName.contains("Aetheria", ignoreCase = true) || bot.aiName.contains("Aetheria", ignoreCase = true) -> {
                                Image(
                                    painter = painterResource(id = R.drawable.img_aetheria_universe),
                                    contentDescription = displayName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(avatarShape)
                                )
                            }
                            bot.avatarUrl.isNotBlank() -> {
                                com.example.ui.components.BotAvatarImage(
                                    avatarUrl = bot.avatarUrl,
                                    contentDescription = displayName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(avatarShape),
                                    fallback = { OrbView(hue = hue, size = 96.dp) }
                                )
                            }
                            else -> {
                                OrbView(hue = hue, size = 96.dp)
                            }
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationZ = avatarRingRotation }
                    ) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x99FAF5FF),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            ),
                            radius = size.minDimension / 2f - 1.dp.toPx(),
                            style = Stroke(width = 1.4.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = displayName,
                                fontFamily = InterFontFamily,
                                color = Color.White,
                                fontSize = if (isUniverse) 20.5.sp else 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            if (!isUniverse) {
                                Spacer(modifier = Modifier.width(10.dp))
                                val badgeShape = RoundedCornerShape(50)
                                Box(
                                    modifier = Modifier
                                        .clip(badgeShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF210E45), Color(0xFF14082E))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.1.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFE9D5FF), Color(0xFF9333EA))
                                                )
                                            ),
                                            badgeShape
                                        )
                                        .padding(horizontal = 11.dp, vertical = 4.5.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Text(
                                            text = "✦",
                                            fontFamily = InterFontFamily,
                                            color = Color(0xFFF3E8FF),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Karakter",
                                            fontFamily = InterFontFamily,
                                            color = Color.White,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2A1559), Color(0xFF150A30))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.1.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFFD8B4FE), Color(0xFF6D28D9))
                                            )
                                        ),
                                        RoundedCornerShape(11.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Box {
                                IconButton(
                                    onClick = { isMenuExpanded = true },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = "Daha Fazla",
                                        tint = Color.White.copy(alpha = 0.92f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { isMenuExpanded = false },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF1A0D3A), Color(0xFF100826))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF6D28D9), Color(0xFF4C1D95), Color(0xFF7C3AED))
                                                )
                                            ),
                                            RoundedCornerShape(16.dp)
                                        )
                                ) {
                                    if (onTogglePrivacy != null && !isTemplate) {
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(if (bot.isPublic) "🔒" else "🌐", fontSize = 14.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = if (bot.isPublic) "Özel Yap" else "Herkese Açık Yap",
                                                        color = Color(0xFFE2D5FF),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                isMenuExpanded = false
                                                onTogglePrivacy.invoke(bot.copy(isPublic = !bot.isPublic))
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(0.8.dp)
                                                .padding(horizontal = 12.dp)
                                                .background(Color(0x30A855F7))
                                        )
                                    }
                                    if (onCloneBot != null) {
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("📋", fontSize = 14.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Botu Kopyala",
                                                        color = Color(0xFFE2D5FF),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                isMenuExpanded = false
                                                onCloneBot.invoke(bot.id)
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(0.8.dp)
                                                .padding(horizontal = 12.dp)
                                                .background(Color(0x30A855F7))
                                        )
                                    }
                                    if (onDeleteBot != null) {
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("🗑️", fontSize = 14.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Sil",
                                                        color = EmochiError,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                isMenuExpanded = false
                                                showDeleteConfirmDialog = true
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isUniverse) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val univBadgeShape = RoundedCornerShape(50)
                        Box(
                            modifier = Modifier
                                .clip(univBadgeShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF211044), Color(0xFF14092C))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFC084FC), Color(0xFF7C3AED))
                                        )
                                    ),
                                    univBadgeShape
                                )
                                .padding(horizontal = 11.dp, vertical = 4.5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Canvas(modifier = Modifier.size(13.dp)) {
                                    drawRoundRect(
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                        ),
                                        cornerRadius = CornerRadius(3.dp.toPx())
                                    )
                                    drawLine(
                                        color = Color(0xFFBAE6FD),
                                        start = Offset(size.width * 0.15f, size.height * 0.82f),
                                        end = Offset(size.width * 0.85f, size.height * 0.18f),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 1.3.dp.toPx(),
                                        center = Offset(size.width * 0.35f, size.height * 0.35f)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 1.1.dp.toPx(),
                                        center = Offset(size.width * 0.72f, size.height * 0.65f)
                                    )
                                }
                                Text(
                                    text = "Evren",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val cardDescription = when {
                        bot.id == "starter_ayla" -> "Lise/üniversiteden beri en yakın\ndostun. Akşam saatlerinde"
                        bot.id == "starter_aetheria" -> "Neon ışıklarıyla aydınlatılmış\nAetheria şehrinin yüksek kuleleri"
                        else -> bot.scenario.ifBlank { bot.openingMessage }
                    }
                    Text(
                        text = cardDescription,
                        fontFamily = InterFontFamily,
                        color = Color(0xFFE5DDF8),
                        fontSize = 14.2.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val stableActiveTime = remember(bot.id) {
                            if (bot.id == "starter_ayla" || bot.id == "starter_aetheria") {
                                "1g önce"
                            } else {
                                val raw = (bot.id.hashCode() % 24).let { if (it < 0) -it else it } + 1
                                if (raw < 5) "$raw sa. önce"
                                else if (raw < 12) "bugün aktif"
                                else "${raw / 12}g önce"
                            }
                        }
                        val timePillShape = RoundedCornerShape(50)
                        Box(
                            modifier = Modifier
                                .clip(timePillShape)
                                .background(Color(0xFF110828))
                                .border(
                                    BorderStroke(
                                        1.1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0x778B5CF6), Color(0x444C1D95))
                                        )
                                    ),
                                    timePillShape
                                )
                                .padding(horizontal = 13.dp, vertical = 7.5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0E3B2E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                }
                                Text(
                                    text = stableActiveTime,
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        val btnInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val btnPressed by btnInteraction.collectIsPressedAsState()
                        val btnScale by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (btnPressed) 0.94f else 1f,
                            animationSpec = tween(90, easing = FastOutSlowInEasing),
                            label = "btnScale"
                        )
                        val actionBtnShape = RoundedCornerShape(50)
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = btnScale; scaleY = btnScale }
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        val btnBloom = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = 2.2.dp.toPx()
                                            color = Color(0xFFA855F7).copy(alpha = 0.55f * cardGlowAlpha).toArgb()
                                            maskFilter = android.graphics.BlurMaskFilter(
                                                9.dp.toPx(),
                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                        canvas.nativeCanvas.drawRoundRect(
                                            0f,
                                            0f,
                                            size.width,
                                            size.height,
                                            50.dp.toPx(),
                                            50.dp.toPx(),
                                            btnBloom
                                        )
                                    }
                                }
                                .clip(actionBtnShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7),
                                            Color(0xFF7C3AED),
                                            Color(0xFF5B21B6),
                                            Color(0xFF7E22CE)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(260f, 120f)
                                    )
                                )
                                .drawBehind {
                                    // Top-left glossy highlight inside button
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0x99FFFFFF),
                                                Color(0x44E9D5FF),
                                                Color.Transparent
                                            ),
                                            center = Offset(size.width * 0.26f, 0f),
                                            radius = size.width * 0.60f
                                        ),
                                        center = Offset(size.width * 0.26f, 0f),
                                        radius = size.width * 0.60f
                                    )
                                    // Animated subtle light sweep across button
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0x28FFFFFF),
                                                Color.Transparent
                                            ),
                                            startX = btnShimmerX,
                                            endX = btnShimmerX + 90f
                                        )
                                    )
                                }
                                .border(
                                    BorderStroke(
                                        1.8.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                Color.White,
                                                Color(0xFFF3E8FF),
                                                Color(0xFFC084FC),
                                                Color(0xFFA855F7)
                                            )
                                        )
                                    ),
                                    actionBtnShape
                                )
                                .clickable(
                                    interactionSource = btnInteraction,
                                    indication = null
                                ) { onOpenBot(bot.id) }
                                .padding(
                                    horizontal = if (isUniverse) 26.dp else 21.dp,
                                    vertical = 10.dp
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(19.dp)
                                )
                                Text(
                                    text = if (isUniverse) "Aç" else "Sohbet Et",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.2.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Beautiful Custom Alert Dialog for delete confirmation
    if (showDeleteConfirmDialog) {
        Dialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Hex_0D0A1C)
                    .border(1.dp, AppColors.Hex_221A4C, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sohbeti Sil",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Bu karakter/evren kaydını silmek istediğinizden emin misiniz? Tüm konuşma geçmişiniz silinecektir.",
                        color = EmochiTextSecondary,
                        fontSize = 13.5.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showDeleteConfirmDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardDeep),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Vazgeç", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                showDeleteConfirmDialog = false
                                onDeleteBot?.invoke(bot.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiError),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Sil", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
