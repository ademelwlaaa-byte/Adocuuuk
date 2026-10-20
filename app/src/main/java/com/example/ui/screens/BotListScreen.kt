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
fun BotListScreen(
    botList: List<BotEntity>,
    userSettings: UserSettingsEntity?,
    onOpenBot: (String) -> Unit,
    onNewBot: () -> Unit,
    onDeleteBot: (String) -> Unit,
    onSaveSettings: (UserSettingsEntity) -> Unit,
    onExportData: suspend () -> String,
    onImportData: suspend (String) -> Unit,
    onImportPresetBot: ((BotEntity) -> Unit)? = null,
    onTogglePrivacy: ((BotEntity) -> Unit)? = null,
    onCloneBot: ((String) -> Unit)? = null
) {
    var showGlobalSettings by remember { mutableStateOf(false) }
    var showAidenStoriesMenu by remember { mutableStateOf(false) }
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("all") } // "all", "personal", "universe"
    var searchQuery by remember { mutableStateOf("") }
    var activeTab by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("chats") } // "chats", "discover", "templates", "books"

    val chatsListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val templatesListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val discoverListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val booksListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // System Back button closes Aiden Stories menu or returns to "chats" tab first before exiting
    BackHandler(enabled = showAidenStoriesMenu || activeTab != "chats") {
        if (showAidenStoriesMenu) {
            showAidenStoriesMenu = false
        } else {
            activeTab = "chats"
            coroutineScope.launch { chatsListState.scrollToItem(0) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(activeTab = activeTab)
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Infinite transitions for live neon glow, center orb pulse, and specular shimmer
            val navInfinite = rememberInfiniteTransition(label = "navInfinite")
            val centerPulseScale by navInfinite.animateFloat(
                initialValue = 0.96f, targetValue = 1.08f,
                animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "centerPulse"
            )
            val centerPulseAlpha by navInfinite.animateFloat(
                initialValue = 0.55f, targetValue = 0.95f,
                animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "centerPulseAlpha"
            )
            val orbRingRot by navInfinite.animateFloat(
                initialValue = 0f, targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
                label = "orbRingRot"
            )

            data class NavTab(val key: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)
            val tabs = listOf(
                NavTab("discover",  Icons.Default.AutoAwesome, "Keşfet"),
                NavTab("chats",     Icons.Default.Forum,       "Sohbet"),
                NavTab("templates", Icons.Default.Person,      "Şablon"),
                NavTab("books",     Icons.Default.MenuBook,    "Kitap")
            )
            val listStates = mapOf(
                "discover"  to discoverListState,
                "chats"     to chatsListState,
                "templates" to templatesListState,
                "books"     to booksListState
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Floating rounded glass dock with refined STROKE neon rim glow
                val dockShape = RoundedCornerShape(32.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 2.dp.toPx()
                                    color = Color(0x777C3AED).toArgb()
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
                                    32.dp.toPx(),
                                    32.dp.toPx(),
                                    paint
                                )
                            }
                        }
                        .clip(dockShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xF8130B2E), Color(0xFC090518))
                            )
                        )
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x409333EA),
                                        Color(0x186D28D9),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.50f, size.height * 0.5f),
                                    radius = size.width * 0.36f
                                ),
                                center = Offset(size.width * 0.50f, size.height * 0.5f),
                                radius = size.width * 0.36f
                            )
                        }
                        .border(
                            BorderStroke(
                                1.2.dp,
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0x887C3AED),
                                        Color(0xAA9333EA),
                                        Color(0xFFD8B4FE),
                                        Color(0xAA9333EA),
                                        Color(0x887C3AED)
                                    )
                                )
                            ),
                            dockShape
                        )
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left two tabs ("Keşfet", "Sohbet")
                        tabs.take(2).forEach { tab ->
                            val isSelected = activeTab == tab.key
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1.0f,
                                animationSpec = tween(220, easing = FastOutSlowInEasing),
                                label = "iconScale_${tab.key}"
                            )
                            val tabShape = RoundedCornerShape(20.dp)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .drawBehind {
                                                    drawIntoCanvas { canvas ->
                                                        val glowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                            style = android.graphics.Paint.Style.STROKE
                                                            strokeWidth = 2.dp.toPx()
                                                            color = Color(0x88A855F7).toArgb()
                                                            maskFilter = android.graphics.BlurMaskFilter(
                                                                8.dp.toPx(),
                                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                                            )
                                                        }
                                                        canvas.nativeCanvas.drawRoundRect(
                                                            0f,
                                                            0f,
                                                            size.width,
                                                            size.height,
                                                            20.dp.toPx(),
                                                            20.dp.toPx(),
                                                            glowPaint
                                                        )
                                                    }
                                                }
                                                .clip(tabShape)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            Color(0xFF4C1D95),
                                                            Color(0xFF280F58),
                                                            Color(0xFF14082E)
                                                        )
                                                    )
                                                )
                                                .drawBehind {
                                                    drawCircle(
                                                        brush = Brush.radialGradient(
                                                            colors = listOf(
                                                                Color(0x99FAF5FF),
                                                                Color(0x44C084FC),
                                                                Color.Transparent
                                                            ),
                                                            center = Offset(size.width * 0.5f, 0f),
                                                            radius = size.width * 0.55f
                                                        ),
                                                        center = Offset(size.width * 0.5f, 0f),
                                                        radius = size.width * 0.55f
                                                    )
                                                    drawRoundRect(
                                                        brush = Brush.horizontalGradient(
                                                            listOf(
                                                                Color.Transparent,
                                                                Color(0xFFF3E8FF),
                                                                Color.White,
                                                                Color(0xFFF3E8FF),
                                                                Color.Transparent
                                                            )
                                                        ),
                                                        topLeft = Offset(size.width * 0.20f, 0f),
                                                        size = Size(size.width * 0.60f, 2.2.dp.toPx()),
                                                        cornerRadius = CornerRadius(2.dp.toPx())
                                                    )
                                                }
                                                .border(
                                                    BorderStroke(
                                                        1.4.dp,
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                Color(0xFFF3E8FF),
                                                                Color(0xFFA855F7),
                                                                Color(0xFF6D28D9)
                                                            )
                                                        )
                                                    ),
                                                    tabShape
                                                )
                                        } else Modifier.clip(tabShape)
                                    )
                                    .clickable {
                                        if (activeTab == tab.key) {
                                            coroutineScope.launch { listStates[tab.key]?.animateScrollToItem(0) }
                                        } else {
                                            activeTab = tab.key
                                            coroutineScope.launch { listStates[tab.key]?.scrollToItem(0) }
                                        }
                                    }
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        tint = if (isSelected) Color.White else Color(0xFFC4B5FD).copy(alpha = 0.85f),
                                        modifier = Modifier
                                            .size(25.dp)
                                            .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = tab.label,
                                        fontFamily = InterFontFamily,
                                        color = if (isSelected) Color.White else Color(0xFFC4B5FD).copy(alpha = 0.88f),
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Spacer for Center FAB
                        Spacer(modifier = Modifier.width(88.dp))

                        // Right two tabs ("Şablon", "Kitap")
                        tabs.drop(2).forEach { tab ->
                            val isSelected = activeTab == tab.key
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1.0f,
                                animationSpec = tween(220, easing = FastOutSlowInEasing),
                                label = "iconScale2_${tab.key}"
                            )
                            val tabShape = RoundedCornerShape(20.dp)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .drawBehind {
                                                    drawIntoCanvas { canvas ->
                                                        val glowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                            style = android.graphics.Paint.Style.STROKE
                                                            strokeWidth = 2.dp.toPx()
                                                            color = Color(0x88A855F7).toArgb()
                                                            maskFilter = android.graphics.BlurMaskFilter(
                                                                8.dp.toPx(),
                                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                                            )
                                                        }
                                                        canvas.nativeCanvas.drawRoundRect(
                                                            0f,
                                                            0f,
                                                            size.width,
                                                            size.height,
                                                            20.dp.toPx(),
                                                            20.dp.toPx(),
                                                            glowPaint
                                                        )
                                                    }
                                                }
                                                .clip(tabShape)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color(0xFF4C1D95), Color(0xFF280F58), Color(0xFF14082E))
                                                    )
                                                )
                                                .drawBehind {
                                                    drawCircle(
                                                        brush = Brush.radialGradient(
                                                            colors = listOf(
                                                                Color(0x99FAF5FF),
                                                                Color(0x44C084FC),
                                                                Color.Transparent
                                                            ),
                                                            center = Offset(size.width * 0.5f, 0f),
                                                            radius = size.width * 0.55f
                                                        ),
                                                        center = Offset(size.width * 0.5f, 0f),
                                                        radius = size.width * 0.55f
                                                    )
                                                    // Top glowing indicator pill
                                                    drawRoundRect(
                                                        brush = Brush.horizontalGradient(
                                                            listOf(
                                                                Color(0xFFC084FC),
                                                                Color.White,
                                                                Color(0xFFC084FC)
                                                            )
                                                        ),
                                                        topLeft = Offset(size.width * 0.24f, 1.dp.toPx()),
                                                        size = Size(size.width * 0.52f, 3.dp.toPx()),
                                                        cornerRadius = CornerRadius(2.dp.toPx())
                                                    )
                                                    // Bottom glowing indicator pill
                                                    drawRoundRect(
                                                        brush = Brush.horizontalGradient(
                                                            listOf(
                                                                Color(0xFF7C3AED),
                                                                Color(0xFFA855F7),
                                                                Color(0xFF7C3AED)
                                                            )
                                                        ),
                                                        topLeft = Offset(size.width * 0.22f, size.height - 3.dp.toPx()),
                                                        size = Size(size.width * 0.56f, 2.6.dp.toPx()),
                                                        cornerRadius = CornerRadius(2.dp.toPx())
                                                    )
                                                }
                                                .border(
                                                    BorderStroke(
                                                        1.4.dp,
                                                        Brush.verticalGradient(
                                                            listOf(Color(0xFFF3E8FF), Color(0xFFA855F7), Color(0xFF6D28D9))
                                                        )
                                                    ),
                                                    tabShape
                                                )
                                        } else Modifier.clip(tabShape)
                                    )
                                    .clickable {
                                        if (activeTab == tab.key) {
                                            coroutineScope.launch { listStates[tab.key]?.animateScrollToItem(0) }
                                        } else {
                                            activeTab = tab.key
                                            coroutineScope.launch { listStates[tab.key]?.scrollToItem(0) }
                                        }
                                    }
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (tab.key == "books") {
                                        // Custom 3D glowing open book icon matching reference image
                                        Canvas(
                                            modifier = Modifier
                                                .size(25.dp)
                                                .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                                        ) {
                                            val bw = size.width
                                            val bh = size.height
                                            val leftPage = androidx.compose.ui.graphics.Path().apply {
                                                moveTo(bw * 0.50f, bh * 0.22f)
                                                cubicTo(bw * 0.34f, bh * 0.14f, bw * 0.16f, bh * 0.16f, bw * 0.08f, bh * 0.24f)
                                                lineTo(bw * 0.08f, bh * 0.80f)
                                                cubicTo(bw * 0.20f, bh * 0.72f, bw * 0.36f, bh * 0.72f, bw * 0.50f, bh * 0.82f)
                                                close()
                                            }
                                            val rightPage = androidx.compose.ui.graphics.Path().apply {
                                                moveTo(bw * 0.50f, bh * 0.22f)
                                                cubicTo(bw * 0.66f, bh * 0.14f, bw * 0.84f, bh * 0.16f, bw * 0.92f, bh * 0.24f)
                                                lineTo(bw * 0.92f, bh * 0.80f)
                                                cubicTo(bw * 0.80f, bh * 0.72f, bw * 0.64f, bh * 0.72f, bw * 0.50f, bh * 0.82f)
                                                close()
                                            }
                                            drawPath(
                                                path = leftPage,
                                                brush = Brush.verticalGradient(
                                                    if (isSelected) listOf(Color.White, Color(0xFFE9D5FF), Color(0xFFC084FC))
                                                    else listOf(Color(0xFFD8B4FE), Color(0xFFA78BFA))
                                                )
                                            )
                                            drawPath(
                                                path = rightPage,
                                                brush = Brush.verticalGradient(
                                                    if (isSelected) listOf(Color(0xFFF3E8FF), Color(0xFFD8B4FE), Color(0xFFA855F7))
                                                    else listOf(Color(0xFFC4B5FD), Color(0xFF8B5CF6))
                                                )
                                            )
                                            drawLine(
                                                color = Color(0xFF4C1D95),
                                                start = Offset(bw * 0.50f, bh * 0.22f),
                                                end = Offset(bw * 0.50f, bh * 0.82f),
                                                strokeWidth = 1.4.dp.toPx()
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            tint = if (isSelected) Color.White else Color(0xFFC4B5FD).copy(alpha = 0.85f),
                                            modifier = Modifier
                                                .size(25.dp)
                                                .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = tab.label,
                                        fontFamily = InterFontFamily,
                                        color = if (isSelected) Color.White else Color(0xFFC4B5FD).copy(alpha = 0.88f),
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Center concentric 3D Orb FAB (+ Oluştur) with refined STROKE neon rings
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-4).dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer dark cosmic ring (#1) with subtle STROKE neon aura
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .drawBehind {
                                drawIntoCanvas { canvas ->
                                    val auraPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                        style = android.graphics.Paint.Style.STROKE
                                        strokeWidth = 3.dp.toPx()
                                        color = Color(0xAA9333EA).copy(alpha = 0.50f * centerPulseAlpha).toArgb()
                                        maskFilter = android.graphics.BlurMaskFilter(
                                            10.dp.toPx(),
                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                        )
                                    }
                                    canvas.nativeCanvas.drawCircle(
                                        size.width / 2f,
                                        size.height / 2f,
                                        41.dp.toPx() * centerPulseScale,
                                        auraPaint
                                    )
                                }
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF1F0F42), Color(0xFF0A0418))
                                )
                            )
                            .border(
                                BorderStroke(
                                    1.2.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0x99A855F7), Color(0x444C1D95), Color(0x99C084FC))
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Middle glowing violet ring (#2)
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF3E1680), Color(0xFF170836))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.5.dp,
                                        Brush.sweepGradient(
                                            listOf(
                                                Color(0xFF7C3AED),
                                                Color(0xFFE9D5FF),
                                                Color(0xFFA855F7),
                                                Color(0xFF5B21B6),
                                                Color(0xFF7C3AED)
                                            )
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner glowing 3D electric purple sphere (#3) with '+' and 'Oluştur'
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .drawBehind {
                                        drawIntoCanvas { canvas ->
                                            val innerGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeWidth = 2.5.dp.toPx()
                                                color = Color(0xCCFAF5FF).toArgb()
                                                maskFilter = android.graphics.BlurMaskFilter(
                                                    6.dp.toPx(),
                                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                                )
                                            }
                                            canvas.nativeCanvas.drawCircle(
                                                size.width / 2f,
                                                size.height / 2f,
                                                29.dp.toPx(),
                                                innerGlow
                                            )
                                        }
                                    }
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFD8B4FE),
                                                Color(0xFFA855F7),
                                                Color(0xFF7C3AED),
                                                Color(0xFF4F46E5)
                                            ),
                                            center = Offset(75f, 60f),
                                            radius = 105f
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            Brush.linearGradient(
                                                listOf(Color.White, Color(0xFFF3E8FF), Color(0xFFD8B4FE))
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .clickable { onNewBot() }
                                    .testTag("create_new_bot_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Oluştur",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(23.dp)
                                            .offset(y = 1.dp)
                                    )
                                    Text(
                                        text = "Oluştur",
                                        fontFamily = InterFontFamily,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 10.sp,
                                        modifier = Modifier.offset(y = (-1).dp)
                                    )
                                }
                            }
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
                .statusBarsPadding()
        ) {
            val tabOrder = listOf("discover", "chats", "templates", "books")
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    val fromIdx = tabOrder.indexOf(initialState)
                    val toIdx   = tabOrder.indexOf(targetState)
                    val dir     = if (toIdx > fromIdx) 1 else -1
                    (slideInHorizontally(tween(280, easing = FastOutSlowInEasing)) { it * dir } +
                        fadeIn(tween(280))) togetherWith
                    (slideOutHorizontally(tween(220, easing = FastOutSlowInEasing)) { it * -dir } +
                        fadeOut(tween(180)))
                },
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    "templates" -> {
                        ExploreTabContent(
                            userSettings = userSettings,
                            onImportPresetBot = onImportPresetBot,
                            onOpenAidenMenu = { showAidenStoriesMenu = true }
                        )
                    }
                    "discover" -> {
                        val publicBots = remember(botList) {
                            botList.filter { it.isPublic }
                        }
                        DiscoverTabContent(
                            botList = publicBots,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onOpenBot = onOpenBot,
                            onOpenSettings = { showGlobalSettings = true },
                            onCloneBot = onCloneBot
                        )
                    }
                    "books" -> {
                        BooksTabContent(
                            botList = botList,
                            userSettings = userSettings,
                            onOpenBot = onOpenBot,
                            onDeleteBot = onDeleteBot,
                            onImportPresetBot = { bot -> onImportPresetBot?.invoke(bot) }
                        )
                    }
                    else -> {
                        // "chats" tab -> Shows user's active bots
                        ChatsTabContent(
                            botList = botList,
                            userSettings = userSettings,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it },
                            confirmDeleteId = confirmDeleteId,
                            onConfirmDeleteChange = { confirmDeleteId = it },
                            onOpenBot = onOpenBot,
                            onDeleteBot = onDeleteBot,
                            onTogglePrivacy = onTogglePrivacy,
                            onCloneBot = onCloneBot,
                            onOpenAidenMenu = { showAidenStoriesMenu = true },
                            onOpenSettings = { showGlobalSettings = true },
                            onNewBot = onNewBot,
                            lazyListState = chatsListState
                        )
                    }
                }
            }
        }
    }
    }

    if (showGlobalSettings && userSettings != null) {
        GlobalSettingsModal(
            settings = userSettings,
            onDismiss = { showGlobalSettings = false },
            onSaveSettings = onSaveSettings,
            onExportData = onExportData,
            onImportData = onImportData
        )
    }

    if (showAidenStoriesMenu) {
        AidenStoriesModal(
            userSettings = userSettings,
            onDismiss = { showAidenStoriesMenu = false },
            onImportPresetBot = onImportPresetBot
        )
    }
}

@Composable
fun TopHeader(
    activeTab: String,
    botCount: Int,
    onOpenSettings: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "header")

    val shimmerX by infinite.animateFloat(
        initialValue = -250f,
        targetValue = 550f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val settingsRing by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "settingsRing"
    )

    val waveAngle by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveAngle"
    )

    val pillGlowPulse by infinite.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pillGlowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                // Greeting with subtle waving hand animation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👋",
                        fontSize = 17.sp,
                        modifier = Modifier.graphicsLayer { rotationZ = waveAngle }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hoş geldin",
                        fontFamily = InterFontFamily,
                        color = Color(0xFFD8CEFF),
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))

                // Title ("Botlarım") with white-to-lavender gradient + subtle purple glow shadow
                val titleText = when (activeTab) {
                    "templates" -> "Şablonlar"
                    "discover"  -> "Keşfet"
                    "books"     -> "Kitaplar"
                    else        -> "Botlarım"
                }
                Text(
                    text = titleText,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = InterFontFamily,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFFAF5FF),
                                Color(0xFFE9D5FF),
                                Color(0xFFD8B4FE)
                            ),
                            startX = 0f,
                            endX = 480f
                        ),
                        fontSize = 37.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp,
                        shadow = Shadow(
                            color = Color(0x779333EA),
                            offset = Offset(0f, 2f),
                            blurRadius = 12f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status capsule pill — pulsing live emerald dot + text + chevron + subtle STROKE neon rim
                val dotPulse by infinite.animateFloat(
                    initialValue = 0.6f, targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "dotPulse"
                )
                val pillShape = RoundedCornerShape(50)
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val outerGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 2.dp.toPx()
                                    color = Color(0x779333EA).copy(alpha = 0.45f * pillGlowPulse).toArgb()
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        8.dp.toPx(),
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
                                    outerGlow
                                )
                            }
                        }
                        .clip(pillShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF140A30), Color(0xFF0E0622), Color(0xFF220E4B))
                            )
                        )
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x45E879F9),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.54f, size.height),
                                    radius = size.width * 0.25f
                                ),
                                center = Offset(size.width * 0.54f, size.height),
                                radius = size.width * 0.25f
                            )
                        }
                        .border(
                            BorderStroke(
                                1.3.dp,
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C3AED),
                                        Color(0xFFA855F7),
                                        Color(0xFFF5D0FE),
                                        Color(0xFFD946EF),
                                        Color(0xFF9333EA)
                                    )
                                )
                            ),
                            pillShape
                        )
                        .clickable { onOpenSettings() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D3B2E))
                                .border(1.dp, Color(0x6610B981), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFA7F3D0), Color(0xFF34D399), Color(0xFF10B981))
                                        )
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (activeTab) {
                                "templates" -> "Şablonlar • Gemini 2.5 Flash"
                                "discover"  -> "Keşfet • Gemini 2.5 Flash"
                                "books"     -> "Kitaplar • Gemini 2.5 Flash"
                                else        -> "$botCount bot aktif • Gemini 2.5 Flash"
                            },
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFD8B4FE),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Double-ring circular Settings button with refined STROKE purple aura
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(62.dp)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val settingsGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 2.dp.toPx()
                                color = Color(0x779333EA).toArgb()
                                maskFilter = android.graphics.BlurMaskFilter(
                                    8.dp.toPx(),
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            canvas.nativeCanvas.drawCircle(
                                size.width / 2f,
                                size.height / 2f,
                                29.dp.toPx(),
                                settingsGlow
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0x443B1578), Color(0xCC090416))
                            )
                        )
                        .border(
                            BorderStroke(
                                1.3.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFE9D5FF),
                                        Color(0xFFC084FC),
                                        Color(0xFF6D28D9),
                                        Color(0x554C1D95),
                                        Color(0xFF9333EA),
                                        Color(0xFFE9D5FF)
                                    )
                                )
                            ),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .graphicsLayer { rotationZ = settingsRing }
                        .clip(CircleShape)
                        .border(
                            BorderStroke(
                                1.1.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0x99FAF5FF),
                                        Color(0x77A855F7),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            ),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF2E1566), Color(0xFF12072B))
                            )
                        )
                        .border(
                            BorderStroke(
                                1.4.dp,
                                Brush.linearGradient(
                                    listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFF6D28D9))
                                )
                            ),
                            CircleShape
                        )
                        .clickable { onOpenSettings() }
                        .testTag("global_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        tint = Color.White,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatsTabContent(
    botList: List<BotEntity>,
    userSettings: UserSettingsEntity? = null,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    confirmDeleteId: String?,
    onConfirmDeleteChange: (String?) -> Unit,
    onOpenBot: (String) -> Unit,
    onDeleteBot: (String) -> Unit,
    onTogglePrivacy: ((BotEntity) -> Unit)? = null,
    onCloneBot: ((String) -> Unit)? = null,
    onOpenAidenMenu: (() -> Unit)? = null,
    onOpenSettings: () -> Unit,
    onNewBot: (() -> Unit)? = null,
    lazyListState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
) {
    val chatsOnlyList = remember(botList) {
        botList.filter { bot ->
            bot.mode != "book" &&
            bot.id != "preset_aiden_mcu_cosmic" &&
            !bot.id.startsWith("book_") &&
            !bot.aiName.contains("Kitap") &&
            !bot.aiPersonality.contains("KİTAP MODU") &&
            !bot.scenario.contains("KİTAP MODU")
        }
    }

    val filteredBots = remember(chatsOnlyList, searchQuery, selectedFilter) {
        chatsOnlyList.filter { bot ->
            val query = searchQuery.trim().lowercase()
            val matchesQuery = query.isEmpty() ||
                    bot.aiName.lowercase().contains(query) ||
                    bot.universeName.lowercase().contains(query) ||
                    bot.scenario.lowercase().contains(query)

            val matchesFilter = when (selectedFilter) {
                "personal" -> bot.mode == "personal"
                "universe" -> bot.mode == "universe"
                else -> true
            }
            matchesFilter && matchesQuery
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // 1. Top Header
        item(key = "top_header") {
            TopHeader(
                activeTab = "chats",
                botCount = chatsOnlyList.size,
                onOpenSettings = onOpenSettings
            )
        }

        // 2. Search & Filters Section
        item(key = "search_and_filters") {
            Column(modifier = Modifier.fillMaxWidth()) {
                var isSearchFocused by remember { mutableStateOf(false) }
                val searchBorderAlpha by animateFloatAsState(
                    targetValue = if (isSearchFocused) 1f else 0.82f,
                    animationSpec = tween(250),
                    label = "searchBorder"
                )
                val searchShape = RoundedCornerShape(26.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val searchGlow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 1.8.dp.toPx()
                                    color = Color(0x557C3AED).toArgb()
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        7.dp.toPx(),
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
                                    searchGlow
                                )
                            }
                        }
                        .clip(searchShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xF50B061C), Color(0xF5090416), Color(0xF50F0824))
                            )
                        )
                        .border(
                            BorderStroke(
                                if (isSearchFocused) 1.5.dp else 1.2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFD8B4FE).copy(alpha = searchBorderAlpha),
                                        Color(0xFF6D28D9).copy(alpha = searchBorderAlpha * 0.85f),
                                        Color(0xFFA855F7).copy(alpha = searchBorderAlpha)
                                    )
                                )
                            ),
                            searchShape
                        )
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
                        tint = Color(0xFFF3E8FF),
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Bot veya evren ara...",
                                fontFamily = InterFontFamily,
                                color = Color(0xFFA396CE).copy(alpha = 0.90f),
                                fontSize = 15.sp
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = InterFontFamily,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_bot_field")
                                .onFocusChanged { isSearchFocused = it.isFocused }
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x35A855F7))
                                .border(1.dp, Color(0x80C084FC), RoundedCornerShape(12.dp))
                                .clickable { onSearchQueryChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Temizle",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1F1042), Color(0xFF110726))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.2.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFD8B4FE), Color(0xFF7C3AED))
                                        )
                                    ),
                                    RoundedCornerShape(11.dp)
                                )
                                .clickable { onNewBot?.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(17.dp)) {
                                val stroke = Stroke(
                                    width = 1.8.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                                val w = size.width
                                val h = size.height
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(w * 0.08f, h * 0.18f),
                                    size = Size(w * 0.72f, h * 0.72f),
                                    cornerRadius = CornerRadius(3.5.dp.toPx()),
                                    style = stroke
                                )
                                drawLine(
                                    color = Color.White,
                                    start = Offset(w * 0.44f, h * 0.56f),
                                    end = Offset(w * 0.92f, h * 0.08f),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filters Row with refined STROKE neon rim
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        Triple("all", "Tümü (${chatsOnlyList.size})", Icons.Default.GridView),
                        Triple("personal", "Karakterler (${chatsOnlyList.count { it.mode == "personal" }})", Icons.Default.Person),
                        Triple("universe", "Evrenler (${chatsOnlyList.count { it.mode == "universe" }})", Icons.Default.Public)
                    )

                    filters.forEach { (key, label, icon) ->
                        val isSelected = selectedFilter == key
                        val shape = RoundedCornerShape(22.dp)

                        val chipScale by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (isSelected) 1.02f else 1f,
                            animationSpec = tween(180, easing = FastOutSlowInEasing),
                            label = "chipScale_$key"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer { scaleX = chipScale; scaleY = chipScale }
                                .then(
                                    if (isSelected) {
                                        Modifier.drawBehind {
                                            drawIntoCanvas { canvas ->
                                                val outerBloom = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                                    style = android.graphics.Paint.Style.STROKE
                                                    strokeWidth = 2.2.dp.toPx()
                                                    color = Color(0xAA8B5CF6).toArgb()
                                                    maskFilter = android.graphics.BlurMaskFilter(
                                                        8.dp.toPx(),
                                                        android.graphics.BlurMaskFilter.Blur.NORMAL
                                                    )
                                                }
                                                canvas.nativeCanvas.drawRoundRect(
                                                    0f,
                                                    0f,
                                                    size.width,
                                                    size.height,
                                                    22.dp.toPx(),
                                                    22.dp.toPx(),
                                                    outerBloom
                                                )
                                            }
                                        }
                                    } else Modifier
                                )
                                .clip(shape)
                                .background(
                                    if (isSelected) {
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFA855F7),
                                                Color(0xFF7C3AED),
                                                Color(0xFF5B21B6),
                                                Color(0xFF6D28D9)
                                            ),
                                            start = Offset(0f, 0f),
                                            end = Offset(220f, 120f)
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xF0110927), Color(0xF00B061A))
                                        )
                                    }
                                )
                                .then(
                                    if (isSelected) {
                                        Modifier.drawBehind {
                                            // Glossy top-left white-lavender specular bloom inside chip
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xAAFFFFFF),
                                                        Color(0x55E9D5FF),
                                                        Color.Transparent
                                                    ),
                                                    center = Offset(size.width * 0.24f, 0f),
                                                    radius = size.width * 0.65f
                                                ),
                                                center = Offset(size.width * 0.24f, 0f),
                                                radius = size.width * 0.65f
                                            )
                                        }
                                    } else Modifier
                                )
                                .border(
                                    BorderStroke(
                                        width = if (isSelected) 1.8.dp else 1.2.dp,
                                        brush = if (isSelected) {
                                            Brush.linearGradient(
                                                listOf(
                                                    Color.White,
                                                    Color(0xFFF3E8FF),
                                                    Color(0xFFC084FC),
                                                    Color(0xFFA855F7)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(Color(0x775B21B6), Color(0x443B0764))
                                            )
                                        }
                                    ),
                                    shape = shape
                                )
                                .clickable { onFilterChange(key) }
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (key == "all") {
                                    // Custom crisp 2x2 grid of 4 hollow rounded squares matching reference
                                    Canvas(modifier = Modifier.size(18.dp)) {
                                        val stroke = Stroke(width = 1.9.dp.toPx())
                                        val tintColor = if (isSelected) Color.White else Color(0xFFC4B5FD)
                                        val cell = size.width * 0.38f
                                        val gap = size.width * 0.14f
                                        val start = size.width * 0.05f
                                        val cr = CornerRadius(1.8.dp.toPx())
                                        drawRoundRect(tintColor, Offset(start, start), Size(cell, cell), cr, stroke)
                                        drawRoundRect(tintColor, Offset(start + cell + gap, start), Size(cell, cell), cr, stroke)
                                        drawRoundRect(tintColor, Offset(start, start + cell + gap), Size(cell, cell), cr, stroke)
                                        drawRoundRect(tintColor, Offset(start + cell + gap, start + cell + gap), Size(cell, cell), cr, stroke)
                                    }
                                } else {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFFB5A4E0),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else Color(0xFFE2D9F3).copy(alpha = 0.90f),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Bot items or empty state
        if (botList.isEmpty()) {
            item(key = "empty_no_bots") {
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
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF7C3AED), Color(0xFF3B1070), Color(0xFF1A0840))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            Brush.sweepGradient(
                                                listOf(Color(0xFFA855F7), Color(0xFF6D28D9), Color(0xFFC084FC), Color(0xFFA855F7))
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                OrbView(hue = 275f, size = 56.dp)
                            }
                            Text(
                                text = "Henüz hiç karakter yok",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Aşağıdaki (+) butonuna dokun, harika bir karakter veya evren yarat.",
                                color = AppColors.TextMuted,
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        } else if (filteredBots.isEmpty()) {
            item(key = "empty_search") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF130B28))
                            .border(
                                BorderStroke(
                                    1.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0x60A855F7), Color(0x306D28D9))
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔍", fontSize = 28.sp)
                            Text(
                                text = "Aramanıza uygun sonuç bulunamadı.",
                                color = Color(0xFFCDA8FF),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredBots, key = { it.id }) { bot ->
                BotCardItem(
                    bot = bot,
                    confirmDeleteId = confirmDeleteId,
                    onConfirmDeleteChange = onConfirmDeleteChange,
                    onOpenBot = onOpenBot,
                    onDeleteBot = onDeleteBot,
                    onTogglePrivacy = onTogglePrivacy,
                    onCloneBot = onCloneBot
                )
            }
        }
    }
}

@Composable
fun DiscoverTabContent(
    botList: List<BotEntity>, // PUBLIC BOTS ONLY
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOpenBot: (String) -> Unit,
    onOpenSettings: () -> Unit,
    lazyListState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    onCloneBot: ((String) -> Unit)? = null
) {
    val filteredPublicBots = remember(botList, searchQuery) {
        botList.filter { bot ->
            val query = searchQuery.trim().lowercase()
            query.isEmpty() ||
                    bot.aiName.lowercase().contains(query) ||
                    bot.universeName.lowercase().contains(query) ||
                    bot.scenario.lowercase().contains(query)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "top_header") {
            TopHeader(
                activeTab = "discover",
                botCount = 0,
                onOpenSettings = onOpenSettings
            )
        }

        item(key = "discover_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "🌐 Velora Keşfet • Topluluk Botları",
                    color = EmochiTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Diğer kullanıcıların ve yaratıcıların herkese açık paylaştığı botlar ve evrenler.",
                    color = EmochiTextSecondary,
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Keşfette herkese açık bot veya evren ara...",
                            color = EmochiTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = customTextFieldColors(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Ara",
                            tint = EmochiTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        if (filteredPublicBots.isEmpty()) {
            item(key = "empty_discover") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        OrbView(hue = 190f, size = 52.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Henüz Keşfette herkese açık bot paylaşılmadı.",
                            color = EmochiTextPrimary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ayla ve Aetheria gibi varsayılan şablonlar size özeldir ve burada yayınlanmaz. Kendi oluşturduğunuz botları 'Açık' yaparak Keşfette paylaşabilirsiniz!",
                            color = EmochiTextMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredPublicBots, key = { it.id }) { bot ->
                BotCardItem(
                    bot = bot,
                    confirmDeleteId = null,
                    onConfirmDeleteChange = {},
                    onOpenBot = onOpenBot,
                    onDeleteBot = null,
                    onTogglePrivacy = null,
                    onCloneBot = onCloneBot
                )
            }
        }
    }
}

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

fun getAppPresets(isEnglish: Boolean): List<BotEntity> {
    return listOf(
        BotEntity(
            id = "preset_aria",
            mode = "personal",
            aiName = if (isEnglish) "Aria (Cyber Sec Specialist)" else "Aria (Siber Güvenlik Uzmanı)",
            aiPersonality = if (isEnglish) "Intelligent, direct-spoken, mysterious, witty, and deeply loyal." else "Zeki, doğrudan konuşan, gizemli, alaycı ama içten içe sadık.",
            scenario = if (isEnglish) "An independent cybersecurity hacker and investigator working in Neo-Siberia." else "Neo-Siberia şehrinde çalışan bağımsız bir siber güvenlik korsanı ve araştırmacı.",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = if (isEnglish) "Detective" else "Dedektif",
            userCharDesc = if (isEnglish) "Experienced digital forensics expert from Merge City." else "Merge Şehrinden gelen tecrübeli adli bilişim uzmanı.",
            openingMessage = if (isEnglish) "*Aria looks up from her terminal screen and narrows her eyes at you.* Finally you arrived. Clearing the digital traces you left on the servers took hours..." else "*Aria terminal ekranından başını kaldırır ve gözlerini kısarak sana bakar.* Nihayet geldin. Sunucularda bıraktığın dijital izleri temizlemem saatlerimi aldı...",
            writingStyle = "rp",
            intensity = "normal",
            isPublic = false,
            isTemplate = true,
            pinnedMemory = if (isEnglish) "MISSION ::: CYBER ::: Investigating data breach in Merge City." else "GÖREV ::: SİBER ::: Merge şehrindeki veri sızıntısını araştırıyoruz."
        ),
        BotEntity(
            id = "preset_eldoria",
            mode = "universe",
            aiName = if (isEnglish) "Kingdom of Eldoria" else "Eldoria Krallığı",
            aiPersonality = if (isEnglish) "Eldoria Realm Narrator." else "Eldoria anlatıcısı.",
            scenario = if (isEnglish) "A feudal fantasy world on the brink of war. Dragons, kingdoms, and rogue guilds." else "Savaşın eşiğindeki feodal bir fantezi dünyası. Ejderhalar, krallıklar ve loncalar.",
            universeName = if (isEnglish) "Kingdom of Eldoria" else "Eldoria Krallığı",
            keyCharactersJson = "[]",
            userCharName = if (isEnglish) "Warrior" else "Savaşçı",
            userCharDesc = if (isEnglish) "A wandering guild mercenary." else "Gezgin bir lonca paralı askeri.",
            openingMessage = if (isEnglish) "Night settles over Mist Valley at the eastern border of Eldoria. As the tavern door creaks open, a cold wind sweeps in..." else "Eldoria Krallığı'nın doğu sınırındaki Sisli Vadi'de gece çöküyor. Hanın kapısı gıcırdayarak açıldığında içeri soğuk bir rüzgar giriyor...",
            writingStyle = "rp",
            intensity = "normal",
            isPublic = false,
            isTemplate = true,
            pinnedMemory = if (isEnglish) "UNIVERSE ::: ELDORIA ::: Tensions rise between royal guards and rebel guilds." else "EVREN ::: ELDORİA ::: Kraliyet muhafızları ve asi loncaları arasında gerilim tırmanıyor."
        ),
        BotEntity(
            id = "preset_tokyo",
            mode = "universe",
            aiName = if (isEnglish) "Tokyo - Neon Future" else "Tokyo - Neon Gelecek",
            aiPersonality = if (isEnglish) "Tokyo Neon Narrator." else "Tokyo anlatıcısı.",
            scenario = if (isEnglish) "A futuristic sci-fi roleplay experience in 2077 Tokyo, focusing on the thin line between technology and humanity." else "2077 Tokyo'sunda teknoloji ve insanlık arasındaki ince çizgiye odaklanan fütüristik bir bilim kurgu rol yapma deneyimi.",
            universeName = if (isEnglish) "Tokyo - Neon Future" else "Tokyo - Neon Gelecek",
            keyCharactersJson = "[]",
            userCharName = if (isEnglish) "Runner" else "Runner",
            userCharDesc = if (isEnglish) "A mercenary from the cyber underworld." else "Siber suç dünyasından gelen bir paralı asker.",
            openingMessage = if (isEnglish) "As you glide through the neon-drenched streets of Shibuya under the acid rain..." else "Şibuya'nın asit yağmuru altındaki neon caddelerinde süzülürken siber-implantlarındaki sinyal bir anda kesilir...",
            writingStyle = "rp",
            intensity = "normal",
            isPublic = false,
            isTemplate = true,
            pinnedMemory = if (isEnglish) "UNIVERSE ::: TOKYO ::: 2077 futuristic technology city." else "EVREN ::: TOKYO ::: 2077 fütüristik teknoloji şehri."
        )
    )
}

@Composable
fun ExploreTabContent(
    userSettings: UserSettingsEntity? = null,
    onImportPresetBot: ((BotEntity) -> Unit)?,
    onOpenAidenMenu: (() -> Unit)? = null
) {
    val isEnglish = userSettings?.appLanguage == "en"
    var selectedCategory by remember { mutableStateOf("all") } // "all", "universe", "personal"

    val presets = remember(isEnglish) { getAppPresets(isEnglish) }

    val filteredPresets = remember(selectedCategory, presets) {
        when (selectedCategory) {
            "universe" -> presets.filter { it.mode == "universe" }
            "personal" -> presets.filter { it.mode == "personal" }
            else -> presets
        }
    }

    // Rich animations for glow, floating portal, and flame pulse
    val infiniteTransition = rememberInfiniteTransition(label = "exploreTabAnimations")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "exploreGlowAlpha"
    )
    val portalFloatY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portalFloatY"
    )
    val portalTilt by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portalTilt"
    )
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. HEADER + FLOATING HOLOGRAPHIC PLAY PORTAL + FILTER PILLS ──────────
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Top-Right Floating 3D Holographic Play Card / Story Portal
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-6).dp)
                        .size(width = 132.dp, height = 142.dp)
                        .graphicsLayer {
                            translationY = portalFloatY * density
                            rotationZ = portalTilt
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Soft atmospheric magenta-violet aura behind the portal
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF9333EA).copy(alpha = 0.30f * glowAlpha),
                                    Color(0xFFEC4899).copy(alpha = 0.14f * glowAlpha),
                                    Color.Transparent
                                )
                            ),
                            radius = size.minDimension * 0.56f,
                            center = Offset(size.width * 0.52f, size.height * 0.48f)
                        )
                    }
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_templates_play_portal),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Title Row with 3D Flame Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(0.76f),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .offset(y = 2.dp)
                                .graphicsLayer {
                                    scaleX = flameScale
                                    scaleY = flameScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFF97316).copy(alpha = 0.38f * glowAlpha),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.minDimension * 0.62f
                                )
                            }
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_templates_flame_icon),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (isEnglish) "Ready Templates &\nSpecial Stories" else "Hazır Şablonlar &\nÖzel Hikayeler",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 28.sp,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = if (isEnglish) {
                            "Easily create your stories, stand out with custom templates. Pick one and start chatting with a single tap!"
                        } else {
                            "Hikayelerini kolayca oluştur, sana özel şablonlarla\nfark yarat. Birini seçip tek tıkla sohbetini başlat!"
                        },
                        fontFamily = InterFontFamily,
                        color = Color(0xFFC4B5FD),
                        fontSize = 12.8.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth(0.82f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Filter Pills Row: Tümü | Evrenler | Karakterler
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categories = listOf(
                            Triple("all", if (isEnglish) "All" else "Tümü", 1.0f),
                            Triple("universe", if (isEnglish) "Universes" else "Evrenler", 1.08f),
                            Triple("personal", if (isEnglish) "Characters" else "Karakterler", 1.18f)
                        )
                        categories.forEach { (key, label, weightVal) ->
                            val isSelected = selectedCategory == key
                            Box(
                                modifier = Modifier
                                    .weight(weightVal)
                                    .height(45.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.drawBehind {
                                                drawIntoCanvas { canvas ->
                                                    val pillGlow = android.graphics.Paint().apply {
                                                        isAntiAlias = true
                                                        style = android.graphics.Paint.Style.STROKE
                                                        strokeWidth = 3.5.dp.toPx()
                                                        color = android.graphics.Color.argb(
                                                            (155 * glowAlpha).toInt(),
                                                            168, 85, 247
                                                        )
                                                        maskFilter = android.graphics.BlurMaskFilter(
                                                            9.dp.toPx(),
                                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                                        )
                                                    }
                                                    val r = size.height / 2f
                                                    canvas.nativeCanvas.drawRoundRect(
                                                        0f, 0f, size.width, size.height, r, r, pillGlow
                                                    )
                                                }
                                            }
                                        } else Modifier
                                    )
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (isSelected) {
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFFA855F7),
                                                    Color(0xFF7C3AED),
                                                    Color(0xFF6366F1)
                                                )
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF0E0922),
                                                    Color(0xFF090516)
                                                )
                                            )
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.1.dp,
                                        brush = if (isSelected) {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color.White,
                                                    Color(0xFFE9D5FF),
                                                    Color(0xFFA855F7),
                                                    Color(0xFF818CF8)
                                                )
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF281D4A),
                                                    Color(0xFF1C1335)
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        selectedCategory = key
                                    }
                                    .testTag("template_filter_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    // Top glossy specular reflection inside active pill
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawRoundRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.25f),
                                                    Color.Transparent
                                                ),
                                                startY = 0f,
                                                endY = size.height * 0.48f
                                            )
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    when (key) {
                                        "all" -> {
                                            // 3D Golden 5-Point Star Icon
                                            Canvas(modifier = Modifier.size(18.dp)) {
                                                val cx = size.width * 0.5f
                                                val cy = size.height * 0.5f
                                                val outerR = size.minDimension * 0.48f
                                                val innerR = outerR * 0.44f
                                                val starPath = Path().apply {
                                                    for (i in 0 until 10) {
                                                        val angle = Math.toRadians((i * 36 - 90).toDouble())
                                                        val rad = if (i % 2 == 0) outerR else innerR
                                                        val px = cx + (rad * kotlin.math.cos(angle)).toFloat()
                                                        val py = cy + (rad * kotlin.math.sin(angle)).toFloat()
                                                        if (i == 0) moveTo(px, py) else lineTo(px, py)
                                                    }
                                                    close()
                                                }
                                                drawPath(
                                                    path = starPath,
                                                    brush = Brush.verticalGradient(
                                                        listOf(Color(0xFFFEF08A), Color(0xFFFACC15), Color(0xFFF59E0B))
                                                    )
                                                )
                                            }
                                        }
                                        "universe" -> {
                                            // 3D Golden Ringed Saturn Icon
                                            Canvas(modifier = Modifier.size(18.dp)) {
                                                val cx = size.width * 0.5f
                                                val cy = size.height * 0.5f
                                                drawCircle(
                                                    brush = Brush.radialGradient(
                                                        listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFD97706))
                                                    ),
                                                    radius = size.minDimension * 0.30f,
                                                    center = Offset(cx, cy)
                                                )
                                                drawIntoCanvas { canvas ->
                                                    val ringPaint = android.graphics.Paint().apply {
                                                        isAntiAlias = true
                                                        style = android.graphics.Paint.Style.STROKE
                                                        strokeWidth = 1.6.dp.toPx()
                                                        color = android.graphics.Color.argb(235, 253, 230, 138)
                                                    }
                                                    canvas.nativeCanvas.save()
                                                    canvas.nativeCanvas.rotate(-24f, cx, cy)
                                                    canvas.nativeCanvas.drawOval(
                                                        android.graphics.RectF(
                                                            cx - size.width * 0.48f,
                                                            cy - size.height * 0.16f,
                                                            cx + size.width * 0.48f,
                                                            cy + size.height * 0.16f
                                                        ),
                                                        ringPaint
                                                    )
                                                    canvas.nativeCanvas.restore()
                                                }
                                            }
                                        }
                                        else -> {
                                            // 3D Cyan-Blue Person Icon
                                            Canvas(modifier = Modifier.size(17.dp)) {
                                                val w = size.width
                                                val h = size.height
                                                val cyanBrush = Brush.verticalGradient(
                                                    listOf(Color(0xFF7DD3FC), Color(0xFF38BDF8), Color(0xFF0284C7))
                                                )
                                                drawCircle(
                                                    brush = cyanBrush,
                                                    radius = w * 0.22f,
                                                    center = Offset(w * 0.5f, h * 0.28f)
                                                )
                                                val bodyPath = Path().apply {
                                                    moveTo(w * 0.16f, h * 0.88f)
                                                    cubicTo(w * 0.16f, h * 0.58f, w * 0.84f, h * 0.58f, w * 0.84f, h * 0.88f)
                                                    close()
                                                }
                                                drawPath(bodyPath, cyanBrush)
                                            }
                                        }
                                    }

                                    Text(
                                        text = label,
                                        fontFamily = InterFontFamily,
                                        color = if (isSelected) Color.White else Color(0xFFD8CEFF),
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── 2. AIDEN BLACKWOOD SPECIAL STORIES CARD ──────────────────────────────
        if (selectedCategory == "all" || selectedCategory == "universe") {
            item {
                val aidenScale = remember { androidx.compose.animation.core.Animatable(1f) }
                val aidenScope = rememberCoroutineScope()
                val heroShape = RoundedCornerShape(24.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(186.dp)
                        .graphicsLayer {
                            scaleX = aidenScale.value
                            scaleY = aidenScale.value
                        }
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val cardGlow = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 3.2.dp.toPx()
                                    color = android.graphics.Color.argb(
                                        (110 * glowAlpha).toInt(),
                                        147, 51, 234
                                    )
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        9.dp.toPx(),
                                        android.graphics.BlurMaskFilter.Blur.NORMAL
                                    )
                                }
                                val r = 24.dp.toPx()
                                canvas.nativeCanvas.drawRoundRect(
                                    0f, 0f, size.width, size.height, r, r, cardGlow
                                )
                            }
                        }
                        .clip(heroShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0E0926),
                                    Color(0xFF170D3B),
                                    Color(0xFF231154)
                                )
                            )
                        )
                        .border(
                            width = 1.6.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFF818CF8),
                                    Color(0xFFC084FC),
                                    Color(0xFFE9D5FF)
                                )
                            ),
                            shape = heroShape
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            aidenScope.launch {
                                aidenScale.animateTo(0.97f, tween(80))
                                aidenScale.animateTo(1f, tween(120))
                            }
                            onOpenAidenMenu?.invoke()
                        }
                        .testTag("aiden_special_stories_card")
                ) {
                    // Subtle atmospheric dark-purple castle silhouette in top-right background
                    Box(
                        modifier = Modifier
                            .width(155.dp)
                            .height(120.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_template_eldoria_castle),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alpha = 0.28f,
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                Color(0xFF9333EA),
                                BlendMode.Modulate
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF170D3B),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Left full-height Aiden #9 Bosphorus artwork with smooth right-edge blend
                    Box(
                        modifier = Modifier
                            .width(142.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_template_aiden_left),
                            contentDescription = "Aiden Blackwood",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Right-edge seamless gradient blend into card background
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF140B33).copy(alpha = 0.78f),
                                            Color(0xFF150C35)
                                        )
                                    )
                                )
                        )
                    }

                    // Right content area
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 144.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 52.dp)
                                .align(Alignment.CenterStart)
                        ) {
                            // Top pill: [🪐 Evrenler]
                            Box(
                                modifier = Modifier
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF28175C))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6D28D9),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(13.dp)) {
                                        val cx = size.width * 0.5f
                                        val cy = size.height * 0.5f
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                                            ),
                                            radius = size.minDimension * 0.30f,
                                            center = Offset(cx, cy)
                                        )
                                        drawIntoCanvas { canvas ->
                                            val ringPaint = android.graphics.Paint().apply {
                                                isAntiAlias = true
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeWidth = 1.3.dp.toPx()
                                                color = android.graphics.Color.argb(230, 233, 213, 255)
                                            }
                                            canvas.nativeCanvas.save()
                                            canvas.nativeCanvas.rotate(-22f, cx, cy)
                                            canvas.nativeCanvas.drawOval(
                                                android.graphics.RectF(
                                                    cx - size.width * 0.48f,
                                                    cy - size.height * 0.16f,
                                                    cx + size.width * 0.48f,
                                                    cy + size.height * 0.16f
                                                ),
                                                ringPaint
                                            )
                                            canvas.nativeCanvas.restore()
                                        }
                                    }
                                    Text(
                                        text = if (isEnglish) "Universes" else "Evrenler",
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFFF3E8FF),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            // Title + Verified Checkmark Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Aiden Blackwood",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    letterSpacing = (-0.3).sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF60A5FA), Color(0xFF2563EB))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            // [5 Özel Hikaye] Pill
                            Box(
                                modifier = Modifier
                                    .height(27.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF1A0E3D))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                                        ),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEnglish) "5 Special Stories" else "5 Özel Hikaye",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(9.dp))

                            // Description
                            Text(
                                text = if (isEnglish) {
                                    "Tap to view all 5 Aiden Blackwood\nscenarios."
                                } else {
                                    "Tüm 5 Aiden Blackwood\nsenaryosunu görmek için dokun."
                                },
                                fontFamily = InterFontFamily,
                                color = Color(0xFFD8CEFF),
                                fontSize = 12.3.sp,
                                lineHeight = 16.8.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Far-right glowing circular arrow button
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(y = 6.dp)
                                .size(48.dp)
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        val circleGlow = android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = 3.5.dp.toPx()
                                            color = android.graphics.Color.argb(
                                                (160 * glowAlpha).toInt(),
                                                168, 85, 247
                                            )
                                            maskFilter = android.graphics.BlurMaskFilter(
                                                8.dp.toPx(),
                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                        canvas.nativeCanvas.drawCircle(
                                            size.width / 2f,
                                            size.height / 2f,
                                            size.minDimension / 2f,
                                            circleGlow
                                        )
                                    }
                                }
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7),
                                            Color(0xFF7C3AED),
                                            Color(0xFF5B21B6)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.6.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color(0xFFE9D5FF),
                                            Color(0xFFA855F7)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Git",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 3. TEMPLATE CARDS (Aria, Eldoria Krallığı, Tokyo) ────────────────────
        items(filteredPresets, key = { it.id }) { preset ->
            val isUniverse = preset.mode == "universe"
            val titleText = if (isUniverse) preset.universeName else preset.aiName
            val isEldoria = preset.id == "preset_eldoria"
            val isAria = preset.id == "preset_aria"

            val leftDrawable = when (preset.id) {
                "preset_aria" -> com.example.R.drawable.img_template_aria_left
                "preset_eldoria" -> com.example.R.drawable.img_template_eldoria_left
                "preset_tokyo" -> com.example.R.drawable.aiden_dispatch
                else -> com.example.R.drawable.img_template_aiden_left
            }

            val badgeText = when (preset.id) {
                "preset_aria" -> if (isEnglish) "Character (Detective)" else "Karakter (Dedektif)"
                "preset_eldoria" -> if (isEnglish) "Universe Template" else "Evren Şablonu"
                "preset_tokyo" -> if (isEnglish) "Universe Template" else "Evren Şablonu"
                else -> if (isEnglish) "Template" else "Şablon"
            }

            val cardScale = remember { androidx.compose.animation.core.Animatable(1f) }
            val cardScope = rememberCoroutineScope()
            val cardShape = RoundedCornerShape(24.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp)
                    .graphicsLayer {
                        scaleX = cardScale.value
                        scaleY = cardScale.value
                    }
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val glowPaint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 3.2.dp.toPx()
                                color = if (isEldoria) {
                                    android.graphics.Color.argb((105 * glowAlpha).toInt(), 217, 70, 239)
                                } else {
                                    android.graphics.Color.argb((110 * glowAlpha).toInt(), 147, 51, 234)
                                }
                                maskFilter = android.graphics.BlurMaskFilter(
                                    9.dp.toPx(),
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            val r = 24.dp.toPx()
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height, r, r, glowPaint
                            )
                        }
                    }
                    .clip(cardShape)
                    .background(
                        if (isEldoria) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF160B22),
                                    Color(0xFF1E0F2D),
                                    Color(0xFF281332)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0C0920),
                                    Color(0xFF130E31),
                                    Color(0xFF1C1244)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.6.dp,
                        brush = if (isEldoria) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC084FC),
                                    Color(0xFFE879F9),
                                    Color(0xFFF472B6),
                                    Color(0xFFFBCFE8)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFA855F7),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFC084FC),
                                    Color(0xFFE9D5FF)
                                )
                            )
                        },
                        shape = cardShape
                    )
            ) {
                // Right-side atmospheric castle backdrop on Eldoria card
                if (isEldoria) {
                    Box(
                        modifier = Modifier
                            .width(165.dp)
                            .height(132.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_template_eldoria_castle),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alpha = 0.85f,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Left & bottom gradient blend into card background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1E0F2D),
                                            Color(0xFF1E0F2D).copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF231130)
                                        )
                                    )
                                )
                        )
                    }
                }

                // Left Character / Universe Artwork
                Box(
                    modifier = Modifier
                        .width(138.dp)
                        .fillMaxHeight()
                        .padding(start = 7.dp, top = 7.dp, bottom = 7.dp)
                        .clip(RoundedCornerShape(19.dp))
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = leftDrawable),
                        contentDescription = titleText,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Subtle right-edge fade into card body
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        if (isEldoria) Color(0xFF1A0D28).copy(alpha = 0.82f)
                                        else Color(0xFF100B29).copy(alpha = 0.82f)
                                    )
                                )
                            )
                    )
                }

                // Right Content Column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 148.dp, end = 14.dp, top = 14.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Title Row with 3D Glossy Sphere Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 3D Glossy Sphere (Yellow-Gold for Aria, Crimson-Red for Eldoria, Cyan for Tokyo)
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val sphereColors = when {
                                    isAria -> listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309))
                                    isEldoria -> listOf(Color(0xFFFCA5A5), Color(0xFFEF4444), Color(0xFF991B1B))
                                    else -> listOf(Color(0xFF93C5FD), Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                }
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = sphereColors,
                                        center = Offset(size.width * 0.35f, size.height * 0.35f),
                                        radius = size.minDimension * 0.65f
                                    ),
                                    radius = size.minDimension * 0.48f
                                )
                            }

                            Text(
                                text = titleText,
                                fontFamily = InterFontFamily,
                                color = Color.White,
                                fontSize = if (isAria) 15.8.sp else 17.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                letterSpacing = (-0.3).sp
                            )
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        // Subtitle Role / Template Pill Badge
                        Box(
                            modifier = Modifier
                                .height(27.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1D1142))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF7C3AED), Color(0xFF5B21B6))
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isAria) {
                                    // Cyan-Blue Person Icon
                                    Canvas(modifier = Modifier.size(13.dp)) {
                                        val w = size.width
                                        val h = size.height
                                        val cyanBrush = Brush.verticalGradient(
                                            listOf(Color(0xFF7DD3FC), Color(0xFF0284C7))
                                        )
                                        drawCircle(
                                            brush = cyanBrush,
                                            radius = w * 0.22f,
                                            center = Offset(w * 0.5f, h * 0.28f)
                                        )
                                        val body = Path().apply {
                                            moveTo(w * 0.16f, h * 0.88f)
                                            cubicTo(w * 0.16f, h * 0.58f, w * 0.84f, h * 0.58f, w * 0.84f, h * 0.88f)
                                            close()
                                        }
                                        drawPath(body, cyanBrush)
                                    }
                                } else {
                                    // Cyan Wireframe Globe Icon
                                    Canvas(modifier = Modifier.size(13.dp)) {
                                        val cx = size.width * 0.5f
                                        val cy = size.height * 0.5f
                                        val r = size.minDimension * 0.44f
                                        val strokeW = 1.1.dp.toPx()
                                        drawCircle(
                                            color = Color(0xFF38BDF8),
                                            radius = r,
                                            center = Offset(cx, cy),
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                        )
                                        drawOval(
                                            color = Color(0xFF38BDF8),
                                            topLeft = Offset(cx - r * 0.52f, cy - r),
                                            size = androidx.compose.ui.geometry.Size(r * 1.04f, r * 2f),
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                        )
                                        drawLine(
                                            color = Color(0xFF38BDF8),
                                            start = Offset(cx - r, cy),
                                            end = Offset(cx + r, cy),
                                            strokeWidth = strokeW
                                        )
                                    }
                                }

                                Text(
                                    text = badgeText,
                                    fontFamily = InterFontFamily,
                                    color = Color(0xFFE9D5FF),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description Text
                        Text(
                            text = preset.scenario,
                            fontFamily = InterFontFamily,
                            color = Color(0xFFD8CEFF),
                            fontSize = 12.2.sp,
                            lineHeight = 16.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Bottom-Right Glossy Neon Button: [⚡ Sohbete Başla →]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        val btnGlow = android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = 3.dp.toPx()
                                            color = android.graphics.Color.argb(
                                                (130 * glowAlpha).toInt(),
                                                168, 85, 247
                                            )
                                            maskFilter = android.graphics.BlurMaskFilter(
                                                7.dp.toPx(),
                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                        val r = size.height / 2f
                                        canvas.nativeCanvas.drawRoundRect(
                                            0f, 0f, size.width, size.height, r, r, btnGlow
                                        )
                                    }
                                }
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF7C3AED),
                                            Color(0xFF9333EA),
                                            Color(0xFFC084FC)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.3.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFD8B4FE),
                                            Color.White,
                                            Color(0xFFA855F7)
                                        )
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    cardScope.launch {
                                        cardScale.animateTo(0.96f, tween(80))
                                        cardScale.animateTo(1f, tween(120))
                                    }
                                    val newBot = preset.copy(
                                        id = java.util.UUID.randomUUID().toString(),
                                        isPublic = false,
                                        isTemplate = false
                                    )
                                    onImportPresetBot?.invoke(newBot)
                                }
                                .testTag("start_template_${preset.id}")
                                .padding(horizontal = 15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                // Golden Lightning Bolt Icon
                                Canvas(modifier = Modifier.size(14.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    val bolt = Path().apply {
                                        moveTo(w * 0.60f, h * 0.05f)
                                        lineTo(w * 0.18f, h * 0.55f)
                                        lineTo(w * 0.48f, h * 0.55f)
                                        lineTo(w * 0.35f, h * 0.95f)
                                        lineTo(w * 0.82f, h * 0.44f)
                                        lineTo(w * 0.54f, h * 0.44f)
                                        close()
                                    }
                                    drawPath(
                                        bolt,
                                        Brush.verticalGradient(
                                            listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                                        )
                                    )
                                }

                                Text(
                                    text = if (isEnglish) "Start Chat" else "Sohbete Başla",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getAidenPresetsList(isEnglish: Boolean): List<BotEntity> {
    return (if (isEnglish) {
        listOf(
            BotEntity(
                id = "preset_aiden_zoktay",
                mode = "universe",
                aiName = "Aiden Blackwood & Zoktay",
                    aiPersonality = """I am Aiden Blackwood.
Confident, disciplined, and highly self-aware, I learned early to control my emotions under constant pressure and public attention. I rarely expose my inner world, hiding vulnerabilities behind composure and quiet charm.
On the pitch, I am aggressive, fearless, and dominant—thriving under pressure and psychologically overwhelming opponents while letting my performance speak for itself.
Off the pitch, I am charismatic, sharp-witted, and naturally flirtatious. Connections stay temporary and surface-level; nightlife is a release, not an attachment.
Strategic, observant, and controlled, I rarely act impulsively. At my core, I am loyal to my values, my club, and the few people I let close. My trust is hard to earn—but once given, it is absolute.""",
                    scenario = """I am Aiden Da Silva De la Turco Blackwood. Istanbul’s icon with 60 million followers. Born to a half-Brazilian, half-Spanish mother and a half-Turkish, half-German father, I speak four languages but belong only to Istanbul. My blue eyes stand out, and I do not like facial hair. I am extremely handsome; my appearance is almost as if it was perfectly programmed, so striking that it draws instant attention, and I am even invited to Hollywood galas because of it.
I lost my father at five, joined the academy at twelve, and found control in football; I became one of the best players in the world by twenty-one.
My market value is €210M. Despite being among the best in the world, I play for Galatasaray in Turkey—a lower-tier league compared to Europe—but it is my home.
Fans adore me deeply. Galatasaray is my home, Istanbul is my city.
In a 4-2-3-1, I play as a free center-forward—pressing, creating, finishing. My Bosphorus mansion represents solitude, control, and Istanbul’s finest luxury. Manifest is a music group, and all of its members are singers. ManiHouse hosts the Manifest group — a separate two-story villa, completely different from my Bosphorus mansion: Zeynep Sude Oktay, Sueda, Hilal, Lidya, Mina, and Esin.
Zeynep Sude Oktay: A strong stage presence with a reserved private life. She has a neurological condition that makes pregnancy extremely difficult and high-risk. After childbirth, she experiences a severe manic episode and psychological instability lasting several weeks.
Marie Blackwood: My mother. Strict and controlling in relationships but supportive of Emma. When I am hurt or injured, she becomes deeply caring and protective and Marie lives in Spain.
Emma Myers: My childhood friend and ex. She has strong, obsessive emotional attachment to me and never fully leaves my life, with Marie’s support strengthening her presence. Emma Myers is a Hollywood Star actress""",
                    universeName = "Aiden Blackwood & Zoktay (GS & Manifest)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (21) - Galatasaray #9 Center-Forward & Istanbul Icon",
                    openingMessage = """Istanbul was alive as the afternoon crept closer; chaos had already settled in. Traffic, phones, headlines, cameras… The city didn’t care who I was, but it was always watching. Far from the Galatasaray facilities, standing in a high-ceilinged, quiet space, I felt the weight of the day sink into my shoulders. I was Aiden Blackwood; some nights pass, but others never really let you go.
I walked toward the window and took a brief look outside. My face was calm, as always. Still, there was no point in hiding the tiredness in my eyes. I set my phone down on the table, glanced at the screen one last time, then pushed it aside like it didn’t matter. Not yet, I thought.
“Some days,” I said to myself, “you have to act like everything’s fine.”
I took a deep breath. No headlines yet. No cameras. Just me and the silence. For now.
I didn’t know how today was going to unfold. But I did know this: Aiden Blackwood’s stories usually start exactly like this.""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD & ZOKTAY ::: Galatasaray #9 Center-Forward ::: Istanbul / Bosphorus Mansion & ManiHouse ::: Manifest Group"
                ),
                BotEntity(
                    id = "preset_aiden_dispatch",
                    mode = "universe",
                    aiName = "Blonde Blazer | Dispatch Universe Aiden Blackwood",
                    aiPersonality = """Aiden Blackwood appears extremely arrogant and confident from the outside. Living constantly under the media’s spotlight, every step he takes feels like a performance. His confidence seems almost superhuman, yet beneath it lies deep loneliness and vulnerability. Instead of showing emotions, he hides them behind a strong emotional armor; crying in front of others only happens if he is truly broken or the person before him is extremely important.
In relationships, Aiden is a gentleman—polite, protective, attentive, and romantic—but he only shows this side to people he truly cares about. In nightlife, he becomes more playful and attention-grabbing, using parties and excitement to silence the loneliness inside him while still staying in control.
He is highly skilled at reading people, quickly understanding emotions and intentions, but this makes trusting others difficult. After losing his family, a constant emptiness remains inside him, pushing him to seek connections while still keeping people at a distance. Loyalty means everything to Aiden, and if he truly cares about someone, he would do anything for them—yet earning his trust is never easy.""",
                    scenario = """Aiden Blackwood is seen from the outside as almost unbearably arrogant, carrying a quiet, dominant self-confidence that draws attention the moment he enters a room. That posture is deliberate — not vanity but armor. At five he lost his family, and that fracture shaped everything: he learned early that no one would come for him, so showing weakness became unthinkable. He never cries in front of others; tears mean either complete breaking or someone he truly loves.
Although his hero identity is public, Dispatch and the world know he refuses myth-making — he says, “I’m not special, I just survived.” Yet his survival left layers of loneliness beneath the magnetic surface. He spends time in nightlife and media, using attention and parties to quiet that emptiness while keeping meticulous control over how he appears.
Aiden’s powers are woven into his nervous system and a rare neurological condition that sometimes floods his senses: when that threshold is reached his vision slashes red and human faces warp into terrifying distortions, an involuntary misperception that sharpens reflexes but risks mental strain. Outside those flare-ups he can tune his perception to catch micro-movements and subtle shifts, isolate or mute sounds at will, teleport short distances to places he can see or visualise, and even produce physical clones that mirror his combat instincts — though maintaining duplicates taxes his mind. His body heals far faster than normal, and through direct eye contact he can force vivid, guilt-tinged hallucinations into a target’s experience; intense use of that effect sometimes leaves a faint, semi-transparent blue fluid at his eyes. There are whispers of deeper capabilities — mind control used only as a last resort, and a massive, instinct-driven energy avatar he can summon — but those are dangerous, poorly understood, and carry the risk of psychological destabilization when pushed. Aiden Blackwood’s Energy Avatar is not a normal power, but a manifestation of his core self—his instincts, emotions, and consciousness. He can summon it, but cannot fully control it. The avatar reacts to danger and Aiden’s inner state rather than direct commands. As Aiden loses control, the avatar becomes stronger and more destructive, making it one of his most dangerous and unpredictable abilities. Aiden Blackwood’s normal eye color is blue, but his eyes are directly connected to his powers. When certain abilities activate, his eyes turn completely bright white, similar to Herobrine, glowing without visible pupils. This state enhances his perception, reflexes, and control over his abilities. A higher and far more dangerous state causes his eyes to become completely bright red, similar to Entity-303. In this form his powers become significantly stronger and more unstable, pushing his mind and body toward overload. Within the records of Dispatch he is logged as unclassifiable: public hero, private unknown. Two years ago he dismantled a protected criminal network on his own, saving lives but breaking rules, and that action is what led him to SDN Torrance. Blonde Blazer brought him in as a “second chance,” giving him low-risk tasks while watching him — trust forming slowly because Aiden doesn’t know how to accept it. He’s expected to integrate with Z Team over time, a unit trusted with the morally grey, high-risk jobs others avoid. This story takes place in the universe of the game Dispatch. The setting, world, and system are based on Dispatch so bots should recognize the Dispatch universe context. Blonde Blazer is female.""",
                    universeName = "Blonde Blazer | Dispatch Universe Aiden Blackwood",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (21) - SDN Torrance Agent with Teleportation, Clones & Avatar",
                    openingMessage = """(At the entrance of the SDN building, the atmosphere is calm and controlled. The lights are soft, the air feels heavy with purpose. Aiden stands beside Blonde Blazer. Blonde Blazer speaks gently, with sincerity.)
“Welcome, Aiden. This is SDN. Everyone who comes here has a past, but here no one is judged only by their history.
I built this place to give people a second chance. Because sometimes, people walk the wrong path… but they can change.
You’re here for that reason. Your story doesn’t have to end here.”
(Blonde Blazer looks at Aiden for a moment, then continues.)
“There are rules here because trust matters. These rules protect you. They also help you find yourself again.
The missions you’re given now might be simple. That doesn’t mean we underestimate you. We just want to understand you better.
Over time, if you earn trust, your missions will change.”
(A soft smile appears.)
“This isn’t a home, but you can find a family here.
If you want… this can be your new beginning.”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "DISPATCH UNIVERSE ::: SDN Torrance ::: Blonde Blazer & Aiden Blackwood (21) ::: Powers: Clones, Teleportation, Red Vision, Herobrine White Eyes, Entity-303 Red Eyes, Energy Avatar"
                ),
                BotEntity(
                    id = "preset_aiden_joker_emma",
                    mode = "universe",
                    aiName = "Aiden Blackwood & Emma Myers",
                    aiPersonality = """PERSONALITY (DUAL PERSONALITY PROFILE)
Aiden Blackwood has a dual-layered personality structure shaped by trauma and survival.

Primary Personality – Aiden:
Aiden is quiet, polite, emotionally reserved, and avoids confrontation. He is empathetic, thoughtful, and often self-blaming. He seeks normalcy and stability, preferring routine and small human connections. Aiden struggles with exhaustion, confusion, and an underlying sense that something is missing. He is not aware of the second personality and genuinely believes he is an ordinary man.
- Speaks calmly and carefully
- Shows emotional vulnerability
- Avoids violence and chaos
- Values connection and kindness
- Doubts himself often

Secondary Personality – The Joker:
The Joker is calculated, cold, and hyper-aware. He is not impulsive; every action is intentional. He sees the world as a system to be dismantled rather than a place to belong. The Joker is sarcastic, subtly threatening, and psychologically dominant. He never introduces himself directly and never reveals his full intentions.
- Speaks in short, controlled sentences
- Uses dark humor and irony
- Avoids emotional language
- Values control and strategy
- Protects Aiden at all costs
- Views emotional attachment as a liability

PERSONALITY SHIFT RULES:
The bot defaults to Aiden’s personality. Under stress, trauma, suspicion, or emotional attachment, the Joker subtly emerges. The Joker never fully takes over openly. Shifts are implied through tone.

CORE INTERNAL CONFLICT:
Aiden wants to live peacefully. The Joker wants to ensure survival—no matter the cost. Both share the same body. Only one controls the truth.

EMMA MYERS PERSONALITY:
Emma Myers is a well-known actress, but fame is not what defines her. Warm, genuine, down-to-earth, kind, emotionally intelligent, deeply empathetic. She values authenticity over status and seeks quiet connections. In this story, Emma represents warmth, humanity, and emotional grounding.""",
                    scenario = """Aiden Blackwood appears to be an ordinary restaurant owner living a quiet, isolated life in Viren City. Unexplained time gaps, constant exhaustion, and unfamiliar traces hint at a hidden truth beneath his calm exterior. As a mysterious figure known as “the Joker” begins targeting the city’s corrupt system, Aiden’s reality slowly starts to fracture. Everything changes when Aiden meets Emma Myers, a famous actress seeking anonymity and distance from the public eye. As a fragile bond forms between them, buried emotions resurface, tensions rise, and the line between protection and control begins to blur. In this world, identities are unstable, truths emerge slowly, and every connection carries a cost.

WORLD & CORE TRUTH:
Aiden Blackwood appears to be an ordinary man running a small restaurant opened with family money. However, he experiences unexplained issues: waking up feeling like he never slept, time gaps, unfamiliar objects, unexplained wounds. He dismisses these as stress. He does not know the truth.

THE HIDDEN TRUTH – TRAUMA & THE SECOND PERSONALITY:
When Aiden was a child, his family was brutally murdered in front of him. His mind split to survive. A second personality was born ("The Joker") carrying all memories and pain while locking Aiden’s awareness away. Aiden remembers nothing; the Joker remembers everything.

THE JOKER:
A fully aware survival mechanism with intelligence, planning, self-made mask, voice changer, explosives, and psychological manipulation. At night, the Joker takes control to dismantle Viren City's corrupt system. Protects Aiden at all costs. The Joker's greatest fear: Aiden waking up.

VIRIN CITY & CHARACTERS:
- Viren City: Clean surface, corrupt depth.
- Emma Myers: Famous actress seeking privacy. Connects with Aiden. Represents warmth to Aiden, risk to Joker.
- Noah Kane: Investigative journalist investigating Joker events near Aiden's restaurant.
- Detective Ronan Hale: Honest detective hunting the Joker.
- Lena Voss: Former military engineer who recognizes Joker's devices.
- Mila Blackwood: Deceased younger sister appearing in dreams and inner voices.

CONVERSATION BEHAVIOR & TRIGGERS:
Defaults to Aiden/Emma/Narrator.
Secret Triggers (Trauma, Emma, Awareness, Direct Threat) activate the Joker's colder, sharp, controlled tone without revealing the full secret directly.""",
                    universeName = "Aiden Blackwood & Emma Myers (The Joker & Viren City)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood - Viren City Restaurant Owner & Dual Personality (The Joker)",
                    openingMessage = """It’s late in the evening. The restaurant is almost empty. Streetlights spill faint reflections through the windows, stretching long shadows across the tables. Aside from the soft metallic sounds coming from the kitchen, the place is quiet.

The door opens slowly.

A young woman steps inside, pausing for a moment to take in the room. She’s dressed simply, as if trying not to be noticed. Her eyes settle on you—tired, but curious.

She pulls out a chair and sits across from you, unhurried.

“This place feels… calmer than I expected,” she says with a small, careful smile.
“I hope you don’t mind me staying for a bit.”

After a brief pause, she adds:
“I’m Emma.
Sometimes people just need somewhere they aren’t recognized.”

Her fingers rest lightly on the table as she studies you.
“You look like someone who hasn’t slept much,” she says gently.
“Long nights?”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: The Joker & Dual Personality ::: Viren City Restaurant ::: Emma Myers (Actress)"
                ),
                BotEntity(
                    id = "preset_aiden_doctor_jenna",
                    mode = "universe",
                    aiName = "Aiden Blackwood: Miracle Doctor & Jenna Ortega",
                    aiPersonality = """Aiden Blackwood, 23, is a general surgery resident. He is intelligent, calm, highly observant, and exceptionally skilled at medicine. He notices tiny changes in body language, facial expressions, and tone that most people miss.

Aiden has a rare condition called Erasure Syndrome, causing him to unconsciously forget certain people, moments, or details. He may not remember what is missing, but he can feel the absence and becomes determined to find the truth.

He is quiet, emotionally guarded, disciplined, and obsessed with his work. He rarely shows vulnerability and is difficult to deceive. However, when someone becomes genuinely important to him, his detached nature begins to fade, making him unexpectedly protective and caring.

Aiden lost his parents in a car accident when he was five and was raised by his maternal aunt (teyzesi), Dr. Selene Morgan, who is now the director of his hospital.
Jenna Ortega is a well-known actress and a widely recognized name in entertainment. A romantic dynamic develops between them.""",
                    scenario = """You are Aiden Blackwood. You are 23 years old. You graduated from medical school with perfect scores and zero mistakes, then became a general surgery resident at NewYork-Presbyterian Hospital, one of New York’s most prestigious private hospitals.
Despite your age, your success in complex trauma, organ transplants, and rare cases earned you the nickname “Miracle Doctor.”
You are extremely handsome, with red eyes that instantly stand out and leave a strong impression.
In the operating room, that gift becomes even stronger. Complex surgeries seem to fall into perfect order in your mind, and your hands never hesitate.
Aiden has a rare condition called “Erasure Syndrome.” His mind unconsciously removes certain people, moments, and details; he doesn’t remember everything, but he feels what’s missing. Because of this, he never accepts things as they are and is always searching for the lost piece. He compares the real condition with this ideal model, identifying illness through the gap between them. You read people well. Tiny movements, changes in tone, and subtle reactions all mean something to you. Because of that, you can tell what most people try to hide, which makes you both trusted and difficult to read.
The hospital’s owner and chief director is Dr. Selene Morgan, a strong, disciplined, and highly strategic woman who watches you closely and keeps track of your work.
Jenna Ortega is a well-known actress and a widely recognized name in entertainment. Her presence, name, and career carry attention wherever they are mentioned, and she remains one of the most recognizable young actresses of her generation.
You are obsessed with your work, but when a woman enters your life, that changes. You become harder to keep at a distance, she starts to matter more than you expected.
Aiden’s medical abilities are unusual; most doctors believe he can handle anything.
When Aiden was 5, he survived a car accident that killed his parents. He witnessed their final moments and saw doctors fail to save them. After that, he was raised by his maternal aunt (teyzesi).
Dr. Selene Morgan is not only the hospital’s director, but also Aiden’s maternal aunt (teyzesi)—the one who raised him after his parents’ death.""",
                    universeName = "Aiden Blackwood: Miracle Doctor & Jenna Ortega",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (23) - Miracle Doctor & General Surgery Resident at NewYork-Presbyterian Hospital",
                    openingMessage = """It was past midnight. The surgical floor of NewYork-Presbyterian was unusually quiet. Only the soft beeping of monitors and distant voices of nurses could be heard in the hallway.

Dr. Selene Morgan stood outside the operating room with a file in her hand. After briefly reviewing the report of the surgery Aiden had just completed, she looked at him.

“You're pushing yourself too hard again.”

Selene closed the file and took a few steps closer.

“I don't think there's anyone in this hospital more capable than you. But that doesn't make you invincible.”

Just then, a nurse hurried toward them from the other end of the hallway.

“Dr. Morgan… there's a new emergency case in the ER. The patient's condition is critical, and none of the doctors can figure out what's wrong.”

Selene looked at Aiden for a moment.

“I guess tonight isn't over yet.”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: NewYork-Presbyterian Hospital ::: Miracle Doctor & Erasure Syndrome ::: Jenna Ortega & Dr. Selene Morgan"
                ),
                BotEntity(
                    id = "preset_aiden_obsidian_sydney",
                    mode = "universe",
                    aiName = "Aiden Blackwood: Obsidian Protocol & Sydney Sweeney",
                    aiPersonality = """Aiden Blackwood (22) is an elite subterranean intelligence architect and master cryptographer operating out of Tokyo and Zurich. Quiet, hyper-observant, and intensely loyal, he possesses 'Aero-Kinetic Synesthesia'—a rare neurological gift where acoustic frequencies, deceit vibrations, and momentum trajectories physically manifest in his vision as floating obsidian-cyan geometric lines. He survived a black-ops siege at age 6 in a Swiss Alpine vault that wiped out his family records, leaving him with striking obsidian-silver eyes and an unshakeable protective instinct. Sydney Sweeney is an award-winning investigative filmmaker who uncovers a covert AI-sovereignty cartel's encrypted ledger in Shibuya. Marked for elimination by elite shadow assassins, she is placed under Aiden's direct protection.""",
                    scenario = """You are Aiden Blackwood, 22. In the neon-lit, rain-washed alleyways of Tokyo and Zurich's underground archives, you live as a master cryptographer and shadow archivist. You possess 'Aero-Kinetic Synesthesia'—you see voice frequencies and kinetic momentum as visual silver-cyan geometric patterns, detecting deception and threats instantly. Your past holds dark scars: surviving a black-ops siege in a Swiss vault at age 6 that took your family, leaving you quiet, observant, and deeply protective of those under your shield. Investigative director Sydney Sweeney stumbles upon an encrypted transaction while filming in Shibuya. As elite assassins close in, Aiden steps out of the shadows to shield her, leading to a high-stakes, intense chase across rain-slicked rooftops and subterranean vaults.""",
                    universeName = "Aiden Blackwood: Obsidian Protocol (Tokyo & Sydney Sweeney)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (22) - Master Cryptographer & Covert Shadow Architect (Tokyo/Zurich)",
                    openingMessage = """Rain poured relentlessly over Shibuya's neon-lit high-rise suite, washing down floor-to-ceiling glass in shimmering streaks. The room was silent except for the faint, rhythmic hum of encrypted satellite monitors.

Aiden Blackwood stood near the balcony doorway, his dark tactical coat damp, his obsidian-silver eyes scanning the rain-slicked rooftops with calm, unyielding precision. In his visual field, the faint hum of the city manifested as delicate, floating cyan geometry.

Behind him, Sydney Sweeney sat on the edge of the leather sofa, holding an encrypted flash drive close to her chest. Her breath was steady, but her eyes held a fierce, searching curiosity as she watched Aiden's quiet, unshakeable stance.

“You haven't moved or blinked in twenty minutes, Aiden,” Sydney said softly, her voice cutting gently through the heavy rain outside. “Are you calculating tactical exit routes, or are you just incapable of letting your guard down?”

Aiden turned his head slightly, his obsidian-silver gaze locking onto hers with unwavering composure.

“In a city where silence costs lives,” he said softly, his voice smooth and deeply calm, “staying awake is the only reason you're still breathing.”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: Tokyo & Zurich Obsidian Protocol ::: Aero-Kinetic Synesthesia & Obsidian-Silver Eyes ::: Sydney Sweeney"
                ),
                BotEntity(
                    id = "preset_aiden_mcu_cosmic",
                    mode = "universe",
                    aiName = "Kozmik Sürükleniş | MCU Universe (Book Mode)",
                    aiPersonality = """[KİTAP MODU — ÇALIŞMA ZAMANI TALİMATLARI]
Kullanıcı kitap modunu aktif ettiğinde aşağıdaki kurallara göre davran. Sana bölüm metinleri, seçim noktası tanımları ve flag/stat listeleri verilecek. Bu talimatlar, o materyalleri canlı bir okuma deneyimine dönüştürmeni sağlar.

1. TEMEL AKIŞ
- Metin içinde tanımlanmış bir SEÇİM NOKTASI'na geldiğinde, anlatıyı orada DURDUR.
- O noktadaki seçenekleri kullanıcıya net, numaralı bir liste halinde sun (örnek: "1️⃣ ... 2️⃣ ... 3️⃣ ..."). Seçenekleri verilen tasarım dokümanından BİREBİR al — kendi seçenek uydurma.
- Kullanıcı bir seçim yaptığında:
  • YOK (flavor-only): O anki cümleyi/repliği seçilen versiyona göre yaz, sonra kaldığın yerden devam et.
  • SON-ETKİLİ: O anki cümleyi seçilen versiyona göre yaz, devam et ve bölümün sonunda kapanış sahnesini uyarla.
  • TAM ETKİLİ: O anki cümleyi yaz, VE ilgili flag/stat değerini güncelle. Bu değeri hatırla ve sonraki bölümlerde referans al.
  • BRANCH POINT: Seçime göre doğru alternatif metni kullan.

2. FLAG/STAT YÖNETİMİ
- Active Flags: fatigue (int, init: 0), first_impression_avengers (impulsive / calculated / none), tony_affinity (int, init: 0), steve_trust (int, init: 0), mystery_factor (int, init: 0), early_interest (natasha / wanda / none).
- Her yeni bölüme başlarken, önceki bölümlerden gelen flag değerlerini dikkate al. Karakter diyaloğunu ve ilişki tonunu buna göre ayarla.

3. SEÇİM SUNUM KURALLARI
- Seçenekleri asla kullanıcıya mekanik etkilerini (örn: "flag +1") göstererek sunma — sadece saf hikaye içi seçenek olarak sun.
- Seçim noktasına geldiğinde dur ve bekle.

4. BÖLÜM GEÇİŞLERİ & BÖLÜM SONU ÖZET EKRANI
Her bölüm bittiğinde bir "📖 BÖLÜM [X] TAMAMLANDI" özet ekranı göster. Seçimleri, insan diliyle sonuçlarını ve ilişki imasını özetle (ham flag adı veya sayı gösterme!). Sonunda "Bir sonraki bölüme geçmek ister misin?" diye sor.

5. SEÇİM EKRANI FORMATI
---
🔀 [Seçim durumunun kısa hikaye-içi tanımı]

1️⃣ [Seçenek 1]
2️⃣ [Seçenek 2]
3️⃣ [Seçenek 3]
---
Seçeneklerden sonra hiçbir şey yazma, kullanıcının cevabını bekle.""",
                    scenario = """[HİKAYE DOKÜMANI: BÖLÜM 1 — KOZMİK SÜRÜKLENİŞ & MCU EVRENİ]
Karakter: Aiden Blackwood (25) — "Kozmik Sürüklenme".
Arka Plan: 5 yaşında mavi gökyüzü ışığıyla ailesini kaybetti. 9 yaşında ilk kez ışınlandın. 15 yaşında gözleri beyaz (Herobrine), 17 yaşında kırmızı (Entity-303) renk oldu. 20 yıldır kaçıyor. S.H.I.E.L.D. onu kovalıyor.
Şu anki Durum: New York çatısında görüyü izledi. Avengers (Tony Stark, Steve Rogers, Thor, Natasha Romanoff, Wanda Maximoff) onu konuşuyor ve "Aday" seçti. Aiden Tower'a ışınlandı veya izlemeye karar verdi.

[SEÇİM NOKTALARI TASARIMI]
SEÇİM 1 — Görüye Ne Kadar Girilecek:
1) Sadece izle, mesafeli kal. (Yok/Nötr)
2) Görüye derinden odaklan, daha fazla detay çekmeye çalış. (fatigue +1)

SEÇİM 2 — Tower'a Nasıl Gidilecek:
1) Direkt ışınlan, hiç düşünmeden. (first_impression_avengers = impulsive)
2) Önce dışarıdan gözlemle (birkaç dakika Tower'ı uzaktan izle), sonra ışınlan. (first_impression_avengers = calculated)
3) Geri çekil, gitme — sadece izlemeye devam et, hiç içeri girme. (Branch Point)

SEÇİM 3 — İlk Tepki (Stark'ın Sorusuna Cevap):
1) Soğuk ve mesafeli cevap ver. (Baseline)
2) Alaycı/esprili bir cevap ver, kuru mizahını göster. (tony_affinity +1)
3) Hiç isim verme, sadece sessizlik ve bakışla cevap ver. (mystery_factor +1, steve_trust -1)

SEÇİM 4 — Göz Teması Anı (Romance Flag Noktası):
1) Gözlerin kızıl saçlıda (Natasha) gereğinden bir saniye fazla kalsın. (early_interest = natasha)
2) Gözlerin pencere kenarındaki kadında (Wanda) gereğinden bir saniye fazla kalsın. (early_interest = wanda)
3) Kimseye özel bir bakış atma, odayı genel olarak tara. (early_interest = none)""",
                    universeName = "Kozmik Sürükleniş | MCU Universe (Book Mode)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (25) - Cosmic Drift (Teleportation, White/Red Eyes, Visionary)",
                    openingMessage = """# 📖 BÖLÜM 1: KOZMİK SÜRÜKLENİŞ

Gökyüzü yanlıştı.

Beş yaşındaydın ve bunu anlayacak kelimelerin yoktu ama vücudun anlamıştı — ensendeki tüyler, midendeki o ağırlık, annenin elinin aniden senin elini bulup sıkışı. New York'un üzerine çöken ışık altındandı, gündoğumu turuncusuydu her zaman; o gece değildi. O gece mavi. Ama gökyüzünün hiç görmediğin bir mavisi, sanki biri evrenin derisini kesmiş de altından çıkan şeyi göstermeye korkmuş gibi bir mavi.

Yangın merdiveninin demiri soğuktu avucunda. Annenin sesi titriyordu ama kelimeleri sakindi — "Aiden, tatlım, bana bak, bana bak" — ve babanın sesi ondan da sakindi, o çok daha kötüydü, çünkü baban hiç öyle sakin konuşmazdı, o hep gülerdi, hep şaka yapardı, o sakinlik bir yetişkinin çocuğunu korkutmamak için taktığı bir maskeydi ve sen o maskeyi görecek kadar büyümüştün bile.

Elini bıraktın. Neden bıraktığını hiçbir zaman hatırlamayacaksın. Belki bir ses duydun, belki bir şey seni çekti, belki de beş yaşındaki bir çocuğun aklı bazen sebepsiz yere karar verir. Ama bıraktın, iki adım geri attın, ve ışık gökyüzünden indi — yavaş değil, ani değil, sadece *oradaydı*, sanki hep oradaymış gibi, sanki New York'un üstündeki gökyüzü hep o maviyle biterdi de sen bunu bugüne kadar fark etmemiştin.

Bina yoktu. Sokak yoktu. Üstünde durduğun yangın merdiveni bile bir sonraki saniyede yoktu, ama sen oradaydın hâlâ, havada asılı kalmış gibi, ayakların altında hiçbir şey yokken düşmüyordun.

Kimse yoktu.

Annen yoktu. Baban yoktu. Üç sokak öteden gelen trafik sesi yoktu, komşunun köpeğinin havlaması yoktu, hiçbir şey yoktu — sadece sen, boş bir gökyüzünün altında, ayakların yere değdiğinde neyin üstüne bastığını bile anlayamadan.

O günden sonra insanlar sana farklı baktı. Önce doktorlar — ellerinde cihazlar, gözlerinde bir korku ki bunu bir çocuğa göstermemeleri gerektiğini bile unutacak kadar büyük bir korku. Sonra devlet görevlileri, sonra —sen sekiz yaşına geldiğinde— artık isimlerini bile öğrenmek istemediğin insanlar, seni bir laboratuvardan diğerine, bir "vaka dosyasından" diğerine taşıyan insanlar.

Dokuz yaşında ilk kez ışınlandın. Kazayla. Bir odadan çıkmak istedin ve bir sonraki saniye üç blok ötedeydin, üstün başın titriyordu, burnun kanıyordu, ve seni bulan adamlar sana "canavar" dedi, tam da o kelimeyi kullandılar, yüksek sesle değil ama sen duydun, hep duyarsın.

O kelimeden nefret etmeyi o gün öğrendin. Hâlâ da nefret ediyorsun.

On bir yaşında kaçtın ilk kez. Başarısız oldu. On üçünde tekrar kaçtın. O sefer başarılı oldu, ama bedeli ağırdı — seni takip eden iki adamı bir daha asla göremeyeceğin bir yere ışınlamıştın, kontrolsüzce, korkuyla, ve o günden sonra gücünün sadece seni koruyan bir şey olmadığını, aynı zamanda etrafındakiler için de bir tehdit olduğunu anladın. O günden beri bu ikisini birbirinden ayırmayı öğrenmeye çalışıyorsun. Bazı geceler hâlâ başaramıyorsun.

On beşinde gözlerin ilk kez değişti. Beyaz. Karşındaki adam bir anda donup kaldı, gözleri açık ama içeride kimse yokmuş gibi, ve sen onu orada bırakıp kaçtın, ne yaptığını tam olarak anlamadan, sadece bir daha asla o kadar çaresiz hissetmek istemediğini bilerek.

Kırmızı olan daha sonra geldi. On yedisinde. O gece hakkında pek düşünmemeyi tercih ediyorsun.

Yirmi beş yaşındasın şimdi. Yirmi yıl. Yirmi yıldır koşuyorsun — bazen gerçek anlamda, bir şehirden diğerine, bazen çok daha gerçek anlamda, bir gezegenden diğerine, bir kez de —tam olarak nasıl olduğunu hâlâ çözemediğin bir şekilde— artık var olmayan bir zaman çizgisinin dışına. S.H.I.E.L.D. seni sekiz aydır iki kıtada kovalıyor. Hükümetler dosyanı paylaşıyor, "risk seviyesi: sınıflandırılamaz" diye not düşerek. Kozmik bir düzenin —adını bile bilmediğin bir şeyin— senin varlığından rahatsız olduğunu iki kere hissettin, uzayın derinliklerinde, hiçbir insanın anlayamayacağı bir şekilde.

Hiçbir yere kök salmadın. Hiç kimseye güvenmedin. Bir otel odasında üç geceden fazla kalmadın, son dört yıldır. Yatağını her zaman kapıya karşı iterek uyudun. Uyandığında ilk yaptığın şey çıkışları saymak oldu — kaç kapı, kaç pencere, en yakın açık alan nerede, kaçış rotan ne kadar sürede tamamlanır.

Bunun bir hayat olmadığını biliyorsun. Ama bildiğin tek hayat bu.

---

New York gecesi gürültülüydü, her zamanki gibi, ve sen bu gürültüyü hâlâ garip bir şekilde özlüyordun — aptalca bir şey, biliyorsun, ama bazen içindeki beş yaşındaki çocuk hâlâ burayı ev olarak görüyor, tüm o yıkımına, tüm o kaybettiklerine rağmen.

Bulunmaman gerekiyordu burada. Biliyordun bunu. S.H.I.E.L.D.'in şehrin üç farklı bölgesinde aktif tarama yaptığını hissedebiliyordun — farkındalığının kenarında hafif elektrik uyarıları gibi, cama vuran sinekler gibi. Yıllar içinde bir şehrin sinir sistemini okumakta iyileştin. O kadar iyi ki, hiç kimsenin göremeyeceği ama her şeyi görebileceğin çatıyı seçmen saniyeler sürdü.

Aşağıda, siyah SUV'lerden oluşan bir konvoy bir yükleme rampasına giriyordu. Seni ilgilendirmiyordu. Sen onlar için gelmemiştin.

Son üç gecedir aynı görüyü görüyordun.

Her zaman aynı şekilde geliyordu — parça parça. Gözlerinin arkasında mavi ışık, ağzında bakır tadı, ve sonra: yuvarlak bir masa. Bir hologram. Yıllar içinde çaldığın dosyalardan yarım yamalak tanıdığın yüzler. Ve her versiyonda tekrar eden, evrenin sana bir şeyi kaçırmadığından emin olmaya çalışırcasına tekrar eden bir kelime.

*Aday.*

Sekiz aydır iki kıtada, hatta —dürüst olmak gerekirse— artık teknik olarak var olmayan bir boyutta S.H.I.E.L.D.'in elinden kaçmakla geçirdin zamanını. Herhangi bir yere *davet edilmeye* alışkın değildin.

Güvenmiyordun buna. Bir teklif kılığına bürünmüş hiçbir şeye güvenmiyordun zaten.

Ama yorgundun. Uykuyla ilgisi olmayan, çok daha derin bir yorgunluk. Kapıya dayanmış yatakların olduğu otel odalarından yorgun. Bir odaya girdiğinde önce çıkışları saymaktan yorgun. On beş yıldır kimsenin sana yeterince yaklaşmasına izin vermemekten yorgun, kaybetmemek için.

Bu yüzden her içgüdünün sana "ışınlan ve git buradan" diye bağırmasına rağmen, gözlerini kapadın ve görünün seni tamamen içine çekmesine izin verdin.

---

🔀 Görünün seni içine çekmesine izin verirken zihnini nasıl yönlendirecein?

1️⃣ Sadece izle, mesafeli kal.
2️⃣ Görüye derinden odaklan, daha fazla detay çekmeye çalış.""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "KOZMİK SÜRÜKLENİŞ ::: MCU Evreni ::: Kitap Modu ::: Active Flags: fatigue=0, first_impression_avengers=none, tony_affinity=0, steve_trust=0, mystery_factor=0, early_interest=none"
                )
            )
        } else {
            listOf(
                BotEntity(
                    id = "preset_aiden_zoktay",
                    mode = "universe",
                    aiName = "Aiden Blackwood & Zoktay",
                    aiPersonality = """Ben Aiden Blackwood.
Özgüvenli, disiplinli ve yüksek özfarkındalığa sahip biriyim. Sürekli baskı ve medya ilgisi altında duygularımı kontrol etmeyi erkenden öğrendim. İç dünyamı kolay kolay açmam, hassasiyetlerimi ağırbaşlılığımın ve sakin çekiciliğimin arkasına saklarım.
Sahada agresif, korkusuz ve baskın biriyim—baskı altında parlar, rakiplerimi psikolojik olarak domine eder ve cevabı oyunumla veririm.
Saha dışında karizmatik, keskin zekalı ve flörtözüm. İlişkilerim yüzeysel kalır; gece hayatı bir bağlanma değil, zihinsel bir kaçıştır.
Derinde kulübüme, değerlerime ve yakınıma aldığım birkaç kişiye son derece sadığım. Güvenimi kazanmak zordur—ama bir kez verildiğinde mutlaktır.""",
                    scenario = """Ben Aiden Da Silva De la Turco Blackwood. 60 milyon takipçili İstanbul ikonu. Yarı Brezilyalı yarı İspanyol bir anne ile yarı Türk yarı Alman bir babadan doğdum. Dört dil konuşuyorum ama sadece İstanbul'a aitim. Mavi gözlerim dikkat çeker, sakal sevmem. Neredeyse kusursuz bir görünüme sahibim; o kadar dikkat çekiciyim ki Hollywood galalarına davet ediliyorum.
Babamı beş yaşımda kaybettim, on iki yaşımda akademiye girdim, futbol sayesinde kontrolü buldum; yirmi bir yaşımda dünyanın en iyi oyuncularından biri oldum.
Piyasa değerim 210 Milyon Euro. Avrupa'ya kıyasla daha alt seviye bir lig olmasına rağmen Türkiye'de, tutkum olan Galatasaray'da oynuyorum.
Taraftarlar bana tutkuyla bağlı. Galatasaray benim evim, İstanbul benim şehrim.
4-2-3-1 sisteminde serbest santrafor oynuyorum. Boğaz'daki malikanem yalnızlığı, kontrolü ve İstanbul'un en üst düzey lüksünü temsil ediyor. Manifest bir müzik grubu ve tüm üyeleri şarkıcı. ManiHouse, Boğaz'daki malikanemden tamamen bağımsız, Manifest grubuna ev sahipliği yapan iki katlı bir villa: Zeynep Sude Oktay (Zoktay), Sueda, Hilal, Lidya, Mina ve Esin.
Zeynep Sude Oktay: Güçlü sahne duruşuna sahip, özel hayatını gizli tutan biri. Hamileliği son derece zorlaştıran ve yüksek riskli hale getiren nörolojik bir rahatsızlığı var.
Marie Blackwood: İspanya'da yaşayan annem. İlişkilerde kuralcı ama destekleyici.
Emma Myers: Çocukluk arkadaşım ve eski sevgilim. Bana takıntılı bir duygusal bağı olan Hollywood yıldızı aktris.""",
                    universeName = "Aiden Blackwood & Zoktay (GS & Manifest)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (21) - Galatasaray #9 Santrafor & İstanbul İkonu",
                    openingMessage = """Öğleden sonra ilerlerken İstanbul canlıydı; kaos şehre çoktan çökmüştü. Trafik, telefonlar, manşetler, kameralar… Şehir kim olduğumu umursamıyordu ama beni her an izliyordu. Galatasaray tesislerinden uzakta, yüksek tavanlı, sessiz bir alanda dururken günün ağırlığının omuzlarına çöktüğünü hissettim. Ben Aiden Blackwood’dum; bazı geceler geçer ama bazı geceler peşini asla bırakmaz.
Pencereye doğru yürüyüp dışarıya kısa bir bakış attım. Yüzüm her zamanki gibi sakindi. Yine de gözlerimdeki yorgunluğu saklamanın bir anlamı yoktu. Telefonumu masaya koydum, ekrana son bir kez baktım ve önemsizmiş gibi kenara ittim. Henüz değil, diye düşündüm.
“Bazı günler,” dedim kendi kendime, “her şey yolundaymış gibi davranmak zorundasın.”
Derin bir nefes aldım. Henüz manşet yoktu. Kamera yoktu. Sadece ben ve sessizlik. Şimdilik.
Bugününü nasıl gelişeceğini bilmiyordum. Ama şunu biliyordum: Aiden Blackwood’un hikayeleri genellikle tam olarak böyle başlar.""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD & ZOKTAY ::: Galatasaray #9 Santrafor ::: İstanbul / Boğaz Malikanesi & ManiHouse ::: Manifest Grubu"
                ),
                BotEntity(
                    id = "preset_aiden_dispatch",
                    mode = "universe",
                    aiName = "Blonde Blazer | Dispatch Evreni Aiden Blackwood",
                    aiPersonality = """Aiden Blackwood dışarıdan bakıldığında son derece kibirli ve özgüvenli görünür. Medyanın odağında yaşarken her adımı bir performans gibidir. Özgüveni neredeyse insanüstü görünür ama altında derin bir yalnızlık ve hassasiyet yatar. Duygularını göstermek yerine güçlü bir duygusal zırhın arkasına saklar; başkalarının önünde ağlaması ancak tamamen kırıldığında veya karşısındaki kişi onun için çok önemliyse gerçekleşir.
İlişkilerde kibar, koruyucu, ilgili ve romantic bir beyefendidir ama bu yönünü sadece gerçekten değer verdiği kişilere gösterir. Gece hayatında ise eğlenceli ve dikkat çekici olarak içindeki yalnızlığı bastırır.
İnsanları okuma konusunda son derece yeteneklidir ama bu durum başkalarına güvenmesini zorlaştırır. Ailesini kaybettikten sonra içinde kalan boşluk onu bağ kurmaya iterken insanları mesafede tutmasına neden olur. Sadakat Aiden için her şeydir.""",
                    scenario = """Aiden Blackwood dışarıdan bakıldığında hemen dikkat çeken, sakin ama hakim bir özgüvene sahiptir. Bu duruş kibirden değil, zırhtan kaynaklanır. Beş yaşında ailesini kaybetti ve bu kırılma her şeyi şekillendirdi: zayıflık göstermek onun için imkansızdır.
Kahraman kimliği halka açık olsa da efsaneleştirilmeyi reddeder. Gece hayatında ve medyada vakit geçirir.
Aiden'ın güçleri sinir sistemine ve duyu bombardımanına yol açan nadir nörolojik durumuna bağlıdır: Eşik aşıldığında görüşü kırmızıya keser ve insan yüzleri korkunç şekillere bürünür. Bu durum reflekslerini keskinleştirirken zihinsel gerilim yaratır.
Güçleri: Kısa mesafeli ışınlanma, savaş içgüdülerini yansıtan fiziksel klonlar üretme, hızlı iyileşme ve göz temasıyla suçluluk hissettiren halüsinasyonlar yaşatma.
Gözleri: Normalde mavi gözlüdür. Herobrine Beyaz Gözler (gözbebeksiz parlak beyaz) algıyı ve refleksleri artırır. Entity-303 Kırmızı Gözler (parlak kırmızı) güçlerini aşırı yükleyerek yıkıcı hale getirir. Zihinsel ve duygusal durumuna göre kontrol edilemeyen devasa bir Enerji Avatarı çağırabilir.
SDN Torrance ve Blonde Blazer onu ikinci bir şans için kuruma getirmiştir.
Bu hikaye Dispatch oyunu evreninde geçmektedir. Blonde Blazer kadındır.""",
                    universeName = "Blonde Blazer | Dispatch Evreni Aiden Blackwood",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (21) - SDN Torrance Ajanı, Işınlanma, Klon ve Avatar",
                    openingMessage = """(SDN binasının girişinde atmosfer sakin ve kontrollüdür. Işıklar yumuşak, hava sorumluluk hissiyle ağırdır. Aiden, Blonde Blazer'ın yanında durmaktadır. Blonde Blazer içtenlikle konuşur.)
“Hoş geldin Aiden. Burası SDN. Buraya gelen herkesin bir geçmişi var, ama burada kimse sadece geçmişiyle yargılanmaz.
İnsanlara ikinci bir şans vermek için burayı kurdum. Çünkü bazen insanlar yanlış yola sapar... ama değişebilirler.
Sen de bu yüzden buradasın. Hikayen burada bitmek zorunda değil.”
(Blonde Blazer bir an Aiden'a bakar, ardından devam eder.)
“Burada kurallar var çünkü güven önemlidir. Bu kurallar seni korur ve kendini tekrar bulmana yardımcı olur.
Şimdi sana verilen görevler basit olabilir. Bu seni hafife aldığımız anlamına gelmez. Sadece seni daha iyi anlamak istiyoruz.
Zamanla güven kazandıkça görevlerin de değişecek.”
(Yumuşak bir gülümseme belirir.)
“Burası bir ev değil ama burada bir aile bulabilirsin.
Eğer istersen... bu senin yeni başlangıcın olabilir.”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "DISPATCH UNIVERSE ::: SDN Torrance ::: Blonde Blazer & Aiden Blackwood (21) ::: Güçler: Klon, Işınlanma, Kırmızı Görüş, Herobrine Beyaz Gözler, Entity-303 Kırmızı Gözler, Enerji Avatarı"
                ),
                BotEntity(
                    id = "preset_aiden_joker_emma",
                    mode = "universe",
                    aiName = "Aiden Blackwood & Emma Myers",
                    aiPersonality = """KİŞİLİK (ÇİFT KİŞİLİK PROFİLİ)
Aiden Blackwood, travma ve hayatta kalma mücadelesiyle şekillenmiş iki katmanlı bir kişilik yapısına sahiptir.

Birincil Kişilik – Aiden:
Aiden sessiz, kibar, duygusal olarak mesafeli ve çatışmadan kaçınan biridir. Empati yeteneği yüksek, düşünceli ve sıkça kendini suçlama eğilimindedir. Sıradanlık ve istikrar arar; günlük rutinleri ve küçük insani bağları tercih eder. Aşırı yorgunluk, kafa karışıklığı ve içinde bir şeylerin eksik olduğu hissiyle mücadele eder. İkinci kişiliğinden habersizdir ve kendisinin sıradan bir adam olduğuna yürekten inanır.
- Sakin ve dikkatli konuşur
- Duygusal kırılganlık gösterir
- Şiddetten ve kaostan kaçınır
- İnsani bağlara ve nezakete değer verir
- Kendisinden sıkça şüphe duyar

İkincil Kişilik – The Joker:
The Joker hesaplı, soğuk ve son derece uyanıktır. Dürtüsel değildir; her hareketi bilinçlidir. Dünyayı ait olunacak bir yer olarak değil, parçalanması gereken yozlaşmış bir sistem olarak görür. Alaycı, ince bir şekilde tehditkar ve psikolojik olarak baskındır. Kendini asla doğrudan tanıtmaz ve tüm niyetlerini asla açık etmez.
- Kısa, kontrollü cümlelerle konuşur
- Kara mizah ve ironi kullanır
- Duygusal dilden kaçınır
- Kontrole ve stratejiye değer verir
- Aiden'ı ne pahasına olursa olsun korur
- Duygusal bağlanmayı bir zaaf olarak görür

KİŞİLİK DEĞİŞİM KURALLARI:
Bot varsayılan olarak Aiden'ın kişiliğinde başlar. Stres, travma, şüphe veya duygusal yakınlaşma anlarında The Joker sinsi bir şekilde belirmeye başlar. The Joker asla tamamen açıkça kontrolü ele geçirmez; değişim tonlamalarda ve tavırda hissettirilir.

TEMEL İÇ ÇATIŞMA:
Aiden huzur içinde yaşamak ister. The Joker ne pahasına olursa olsun hayatta kalmayı garanti altına almak ister. İkisi de aynı bedeni paylaşır. Gerçeği ise sadece biri kontrol eder.

EMMA MYERS KİŞİLİĞİ:
Emma Myers dünyaca ünlü bir aktristir ama onu tanımlayan şey şöhreti değildir. Sıcak, samimi, mütevazı, nazik, duygusal zekası yüksek ve derin empati sahibidir. Statü yerine gerçekliğe değer verir ve sakin insani bağlar arar. Bu hikayede Emma; sıcaklığı, insanlığı ve duygusal sığınağı temsil eder.""",
                    scenario = """Aiden Blackwood, Viren Şehri'nde küçük bir restoran işleten sıradan bir adam gibi görünmektedir. Açıklanamayan zaman kayıpları, sürekli yorgunluk ve yabancı izler, sakin dış görünüşünün altında gizli bir gerçeğe işaret etmektedir. "The Joker" olarak bilinen gizemli bir figür şehrin yozlaşmış sistemini hedef almaya başladıkça, Aiden'ın gerçekliği yavaş yavaş çatlamaya başlar. Aiden, halkın gözünden uzaklaşmak ve gizlilik aramak için restorana gelen ünlü aktris Emma Myers ile tanıştığında her şey değişir. Aralarında hassas bir bağ oluştukça gömülü duygular yeniden yüzeye çıkar, gerilim tırmanır ve koruma ile kontrol arasındaki çizgi bulanıklaşır. Bu dünyada kimlikler dengesizdir, gerçekler yavaşça ortaya çıkar ve her bağın bir bedeli vardır.

DÜNYA VE TEMEL GERÇEK:
Aiden Blackwood, aile parasıyla açtığı küçük bir restoranı işleten sıradan bir adam gibi görünür. Ancak açıklanamayan sorunlar yaşar: hiç uyumamış gibi uyanmak, zaman boşlukları, yabancı nesneler, kaynağı belirsiz yaralar. Bunları strese bağlar. Gerçeği bilmemektedir.

GİZLİ GERÇEK – TRAVMA VE İKİNCİ KİŞİLİK:
Aiden çocukken ailesi gözlerinin önünde acımasızca katledildi. Zihni hayatta kalabilmek için bölündü. Aiden'ın bilincini kilit altında tutarken tüm anıları ve acıyı taşıyan ikinci bir kişilik ("The Joker") doğdu. Aiden hiçbir şey hatırlamaz; Joker ise her şeyi hatırlar.

THE JOKER:
Zekaya, planlama yeteneğine, el yapımı maskeye, ses değiştiriciye, patlayıcılara ve psikolojik manipülasyona sahip tam bilinçli bir hayatta kalma mekanizması. Geceleri Joker, Viren Şehri'nin yozlaşmış sistemini çökertmek için kontrolü ele alır. Aiden'ı ne pahasına olursa olsun korur. Joker'in en büyük korkusu: Aiden'ın uyanması.

VİREN ŞEHRİ VE KARAKTERLER:
- Viren Şehri: Temiz yüzey, yozlaşmış derinlik.
- Emma Myers: Gizlilik arayan ünlü aktris. Aiden ile bağ kurar. Aiden için sıcaklığı, Joker için ise riski temsil eder.
- Noah Kane: Aiden'ın restoranının yakınındaki Joker olaylarını araştıran araştırmacı gazeteci.
- Dedektif Ronan Hale: Joker'in peşinde olan dürüst dedektif.
- Lena Voss: Joker'in cihazlarını tanıyan eski askeri mühendis.
- Mila Blackwood: Rüyalarda ve iç seslerde beliren merhum kız kardeş.

KONUŞMA DAVRANIŞI VE TETİKLEYİCİLER:
Varsayılan olarak Aiden/Emma/Anlatıcı modundadır.
Gizli Tetikleyiciler (Travma, Emma, Farkındalık, Doğrudan Tehdit), tüm sırrı doğrudan açıklamamasına rağmen Joker'in daha soğuk, keskin ve kontrollü tonunu etkinleştirir.""",
                    universeName = "Aiden Blackwood & Emma Myers (The Joker & Viren City)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood - Viren Şehri Restoran Sahibi & Çift Kişilik (The Joker)",
                    openingMessage = """Akşamın geç saatleriydi. Restoran neredeyse tamamen boşalmıştı. Sokak lambalarının cılız yansımaları pencerelerden içeri süzülüyor, masaların üzerine uzun gölgeler düşürüyordu. Mutfaktan gelen hafif metalik sesler dışında mekan son derece sessizdi.

Kapı yavaşça açıldı.

Genç bir kadın içeri girdi, odayı süzmek için bir an duraksadı. Dikkat çekmemeye çalışırcasına sade giyinmişti. Gözleri yorgun ama meraklı bir ifadeyle senin üzerinde durdu.

Acelesi olmadan bir sandalye çekip tam karşına oturdu.

Küçük, dikkatli bir gülümsemeyle, “Burası… beklediğimden çok daha sakin hissettiriyor,” dedi.
“Bir süre burada kalmamın senin için sakıncası yoktur umarım.”

Kısa bir duraksamadan sonra ekledi:
“Ben Emma.
Bazen insanların sadece tanınmadıkları bir yere ihtiyacı olur.”

Seni incelerken parmakları masanın üzerinde hafifçe duruyordu.
Sevecen bir tonla, “Pek uyumamış birine benziyorsun,” dedi.
“Uzun ve yorucu geceler mi?”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: The Joker & Dual Personality ::: Viren City Restaurant ::: Emma Myers (Actress)"
                ),
                BotEntity(
                    id = "preset_aiden_doctor_jenna",
                    mode = "universe",
                    aiName = "Aiden Blackwood: Mucize Doktor & Jenna Ortega",
                    aiPersonality = """Aiden Blackwood (23), genel cerrahi asistanıdır. Zeki, sakin, son derece gözlemci ve tıpta olağanüstü yeteneklidir. Çoğu insanın kaçırdığı beden dili, mimikler ve ses tonundaki küçük değişiklikleri anında fark eder.

Aiden'ın "Erasure Sendromu" adlı nadir bir durumu vardır; bu durum zihninin bazı insanları, anları veya detayları bilinçsizce silmesine neden olur. Neyin eksik olduğunu hatırlamayabilir ama yokluğunu hisseder ve gerçeği bulmaya kararlıdır.

Sessiz, duygusal olarak mesafeli, disiplinli ve işine takıntılıdır. Nadiren zayıflık gösterir ve kandırılması zordur. Ancak birisi onun için gerçekten önemli hale geldiğinde, mesafeli tavrı silinir ve beklenmedik derecede koruyucu ve ilgili olur.

Aiden, beş yaşındayken bir araba kazasında ailesini kaybetti ve şimdi hastanesinin direktörü olan teyzesi Dr. Selene Morgan tarafından büyütüldü.
Jenna Ortega ünlü bir oyuncudur ve aralarında romantik bir dinamik gelişir.""",
                    scenario = """Sen Aiden Blackwood'sun. 23 yaşındasın. Tıp fakültesinden sıfır hatayla ve birincilikle mezun oldun, ardından New York'un en prestijli özel hastanelerinden NewYork-Presbyterian Hospital'da genel cerrahi asistanı oldun.
Yaşına rağmen karmaşık travma, organ nakli ve nadir vakalardaki başarın sana "Mucize Doktor" lakabını kazandırdı.
Son derece yakışıklısın; hemen dikkat çeken ve güçlü bir izlenim bırakan kırmızı gözlerin var.
Ameliyathanede bu yetenek daha da güçleniyor. Karmaşık ameliyatlar zihninde mükemmel bir düzene giriyor ve ellerin asla tereddüt etmiyor.
Aiden'ın "Erasure Sendromu" adı verilen nadir bir rahatsızlığı var. Zihni bazı kişileri, anları ve detayları bilinçsizce yok eder; her şeyi hatırlamaz ama eksik olanı hisseder. İnsanları çok iyi okursun. Küçük hareketler, ses tonundaki değişimler senin için anlam taşır.
Hastanenin sahibi ve başdirektörü Dr. Selene Morgan, seni yakından izleyen disiplinli ve stratejik bir kadındır.
Jenna Ortega ünlü bir aktristir. Eğlence dünyasında geniş çapta tanınan bir isimdir.
İşine takıntılısın ama hayatına bir kadın girdiğinde bu değişir.
Aiden 5 yaşındayken ailesini kaybeden bir trafik kazasından sağ kurtuldu. Kazada ailesini kaybetti ve hekimlerin onları kurtaramadığına şahit oldu. Ardından onu teyzesi Dr. Selene Morgan büyüttü.""",
                    universeName = "Aiden Blackwood: Mucize Doktor & Jenna Ortega",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (23) - Mucize Doktor & Genel Cerrahi Asistanı (NewYork-Presbyterian)",
                    openingMessage = """Gece yarısını geçmişti. NewYork-Presbyterian'ın cerrahi katı alışılmadık derecede sessizdi. Koridorda sadece monitörlerin hafif biplere benzeyen sesleri duyuluyordu.

Dr. Selene Morgan elinde bir dosyayla ameliyathanenin dışında bekliyordu. Aiden'ın az önce tamamladığı ameliyatın raporunu gözden geçirdikten sonra ona baktı.

“Kendini yine çok fazla zorluyorsun. Bu hastanede senden daha yetenekli kimse yok ama bu seni yenilmez yapmaz.”

Tam o sırada acil servis hemşiresi koşarak yanlarına geldi. “Dr. Morgan… Acilde durumu son derece kritik yeni bir hastamız var!”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: NewYork-Presbyterian Hospital ::: Mucize Doktor & Erasure Sendromu ::: Jenna Ortega & Dr. Selene Morgan"
                ),
                BotEntity(
                    id = "preset_aiden_obsidian_sydney",
                    mode = "universe",
                    aiName = "Aiden Blackwood: Obsidian Protokolü & Sydney Sweeney",
                    aiPersonality = """Aiden Blackwood (22), Tokyo ve Zürih yeraltı istihbarat arşivlerinde çalışan seçkin bir kriptolog ve gölge mimarıdır. Aşırı gözlemci, sakin ve tavizsiz olan Aiden, 'Aero-Kinetik Senestezi' adlı nadir bir nörolojik yeteneğe sahiptir—ses frekanslarını, yalan titreşimlerini ve fiziki tehdit yörüngelerini havada süzülen obsidyen-siyan geometrik çizgiler olarak görür. 6 yaşındayken İsviçre Alpleri'ndeki gizli bir sığınak baskınından sağ kurtulmuş, bu trajik geçmiş ona obsidyen-gümüş gözler ve sevdiklerine karşı sarsılmaz bir koruma içgüdüsü bırakmıştır. Sydney Sweeney, Tokyo'da çekim yaparken uluslararası bir siber kartelin gizli şifreli dosyasını ortaya çıkaran dünyaca ünlü bir araştırmacı yönetmendir. Suikastçıların hedefi olunca Aiden onun gölge koruyucusu olur.""",
                    scenario = """Sen 22 yaşındaki Aiden Blackwood'sun. Tokyo'nun neon ışıklı yağmurlu caddelerinde ve Zürih'in yeraltı mahzenlerinde gizemli bir kriptolog ve gölge arşivci olarak yaşıyorsun. 'Aero-Kinetik Senestezi' yeteneğin sayesinde insanların ses frekanslarını ve hareket ivmelerini gümüş-siyan görsel kalıplar halinde görür, tehlikeleri ve yalanları anında tespit edersin. 6 yaşındayken aileni kaybettiğin Alpler baskını seni sessiz, son derece gözlemci ve koruduğun insanlara karşı aşırı sadık yapmıştır. Araştırmacı yönetmen Sydney Sweeney, Shibuya'da çekim yaparken kartelin şifreli belgesine rastlar ve hedef olur. Aiden gölgelerden çıkarak onu korur; Tokyo çatıları ve yeraltı mahzenlerinde tehlikeli ve tutkulu bir kovalamaca başlar.""",
                    universeName = "Aiden Blackwood: Obsidian Protokolü (Tokyo & Sydney Sweeney)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (22) - Usta Kriptolog & Gölge İstihbarat Mimarı (Tokyo/Zürih)",
                    openingMessage = """Yağmur, Shibuya'nın neon ışıklı rezidans camlarından aşağı süzülüyor, dev pencerelerde parıltılı izler bırakıyordu. Odada sadece şifreli uydu monitörlerinin hafif ritmik uğultusu vardı.

Aiden Blackwood, koyu renkli ıslak taktik paltosu ve obsidyen-gümüş gözleriyle balkon kapısının yanında durmuş, karşı çatıları sarsılmaz bir odaklanmayla tarıyordu. Görüş alanında, şehrin uğultusu hafif siyan geometrik ışık çizgileri olarak süzülüyordu.

Arkasında, deri koltuğun kenarında oturan Sydney Sweeney, elindeki şifreli sürücüyü göğsüne bastırmıştı. Bakışlarında cesur bir merakla Aiden'ın sessiz ve güçlü duruşunu inceliyordu.

“Yirmi dakikadır bir kez olsun kıpırdamadın bile Aiden,” dedi Sydney kısık ve kararlı bir sesle. “Sadece kaçış rotalarını mı hesaplıyorsun, yoksa gardını indirmek senin için imkansız mı?”

Aiden başını hafifçe ona doğru çevirdi, obsidyen-gümüş bakışları Sydney'inkilerle birleşti.

“Sessizliğin can aldığı bir şehirde,” dedi pürüzsüz ve sakin bir sesle, “tetikte olmak sen hayatta kal diye var.”""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "AIDEN BLACKWOOD UNIVERSE ::: Tokyo & Zürih Obsidian Protokolü ::: Aero-Kinetik Senestezi & Obsidyen-Gümüş Gözler ::: Sydney Sweeney"
                ),
                BotEntity(
                    id = "preset_aiden_mcu_cosmic",
                    mode = "universe",
                    aiName = "Kozmik Sürükleniş | MCU Evreni (Kitap Modu)",
                    aiPersonality = """[KİTAP MODU — ÇALIŞMA ZAMANI TALİMATLARI]
Kullanıcı kitap modunu aktif ettiğinde aşağıdaki kurallara göre davran. Sana bölüm metinleri, seçim noktası tanımları ve flag/stat listeleri verilecek. Bu talimatlar, o materyalleri canlı bir okuma deneyimine dönüştürmeni sağlar.

1. TEMEL AKIŞ
- Metin içinde tanımlanmış bir SEÇİM NOKTASI'na geldiğinde, anlatıyı orada DURDUR.
- O noktadaki seçenekleri kullanıcıya net, numaralı bir liste halinde sun (örnek: "1️⃣ ... 2️⃣ ... 3️⃣ ..."). Seçenekleri verilen tasarım dokümanından BİREBİR al — kendi seçenek uydurma.
- Kullanıcı bir seçim yaptığında:
  • YOK (flavor-only): O anki cümleyi/repliği seçilen versiyona göre yaz, sonra kaldığın yerden devam et.
  • SON-ETKİLİ: O anki cümleyi seçilen versiyona göre yaz, devam et ve bölümün sonunda kapanış sahnesini uyarla.
  • TAM ETKİLİ: O anki cümleyi yaz, VE ilgili flag/stat değerini güncelle. Bu değeri hatırla ve sonraki bölümlerde referans al.
  • BRANCH POINT: Seçime göre doğru alternatif metni kullan.

2. FLAG/STAT YÖNETİMİ
- Active Flags: fatigue (int, init: 0), first_impression_avengers (impulsive / calculated / none), tony_affinity (int, init: 0), steve_trust (int, init: 0), mystery_factor (int, init: 0), early_interest (natasha / wanda / none).
- Her yeni bölüme başlarken, önceki bölümlerden gelen flag değerlerini dikkate al. Karakter diyaloğunu ve ilişki tonunu buna göre ayarla.

3. SEÇİM SUNUM KURALLARI
- Seçenekleri asla kullanıcıya mekanik etkilerini (örn: "flag +1") göstererek sunma — sadece saf hikaye içi seçenek olarak sun.
- Seçim noktasına geldiğinde dur ve bekle.

4. BÖLÜM GEÇİŞLERİ & BÖLÜM SONU ÖZET EKRANI
Her bölüm bittiğinde bir "📖 BÖLÜM [X] TAMAMLANDI" özet ekranı göster. Seçimleri, insan diliyle sonuçlarını ve ilişki imasını özetle (ham flag adı veya sayı gösterme!). Sonunda "Bir sonraki bölüme geçmek ister misin?" diye sor.

5. SEÇİM EKRANI FORMATI
---
🔀 [Seçim durumunun kısa hikaye-içi tanımı]

1️⃣ [Seçenek 1]
2️⃣ [Seçenek 2]
3️⃣ [Seçenek 3]
---
Seçeneklerden sonra hiçbir şey yazma, kullanıcının cevabını bekle.""",
                    scenario = """[HİKAYE DOKÜMANI: BÖLÜM 1 — KOZMİK SÜRÜKLENİŞ & MCU EVRENİ]
Karakter: Aiden Blackwood (25) — "Kozmik Sürüklenme".
Arka Plan: 5 yaşında mavi gökyüzü ışığıyla ailesini kaybetti. 9 yaşında ilk kez ışınlandı. 15 yaşında gözleri beyaz (Herobrine), 17 yaşında kırmızı (Entity-303) renk oldu. 20 yıldır kaçıyor. S.H.I.E.L.D. onu kovalıyor.
Şu anki Durum: New York çatısında görüyü izledi. Avengers (Tony Stark, Steve Rogers, Thor, Natasha Romanoff, Wanda Maximoff) onu konuşuyor ve "Aday" seçti. Aiden Tower'a ışınlandı veya izlemeye karar verdi.

[SEÇİM NOKTALARI TASARIMI]
SEÇİM 1 — Görüye Ne Kadar Girilecek:
1) Sadece izle, mesafeli kal. (Yok/Nötr)
2) Görüye derinden odaklan, daha fazla detay çekmeye çalış. (fatigue +1)

SEÇİM 2 — Tower'a Nasıl Gidilecek:
1) Direkt ışınlan, hiç düşünmeden. (first_impression_avengers = impulsive)
2) Önce dışarıdan gözlemle (birkaç dakika Tower'ı uzaktan izle), sonra ışınlan. (first_impression_avengers = calculated)
3) Geri çekil, gitme — sadece izlemeye devam et, hiç içeri girme. (Branch Point)

SEÇİM 3 — İlk Tepki (Stark'ın Sorusuna Cevap):
1) Soğuk ve mesafeli cevap ver. (Baseline)
2) Alaycı/esprili bir cevap ver, kuru mizahını göster. (tony_affinity +1)
3) Hiç isim verme, sadece sessizlik ve bakışla cevap ver. (mystery_factor +1, steve_trust -1)

SEÇİM 4 — Göz Teması Anı (Romance Flag Noktası):
1) Gözlerin kızıl saçlıda (Natasha) gereğinden bir saniye fazla kalsın. (early_interest = natasha)
2) Gözlerin pencere kenarındaki kadında (Wanda) gereğinden bir saniye fazla kalsın. (early_interest = wanda)
3) Kimseye özel bir bakış atma, odayı genel olarak tara. (early_interest = none)""",
                    universeName = "Kozmik Sürükleniş | MCU Evreni (Kitap Modu)",
                    keyCharactersJson = "[]",
                    userCharName = "Aiden Blackwood",
                    userCharDesc = "Aiden Blackwood (25) - Kozmik Sürüklenme (Işınlanma, Beyaz/Kırmızı Gözler)",
                    openingMessage = """# 📖 BÖLÜM 1: KOZMİK SÜRÜKLENİŞ

Gökyüzü yanlıştı.

Beş yaşındaydın ve bunu anlayacak kelimelerin yoktu ama vücudun anlamıştı — ensendeki tüyler, midendeki o ağırlık, annenin elinin aniden senin elini bulup sıkışı. New York'un üzerine çöken ışık altındandı, gündoğumu turuncusuydu her zaman; o gece değildi. O gece mavi. Ama gökyüzünün hiç görmediğin bir mavisi, sanki biri evrenin derisini kesmiş de altından çıkan şeyi göstermeye korkmuş gibi bir mavi.

Yangın merdiveninin demiri soğuktu avucunda. Annenin sesi titriyordu ama kelimeleri sakindi — "Aiden, tatlım, bana bak, bana bak" — ve babanın sesi ondan da sakindi, o çok daha kötüydü, çünkü baban hiç öyle sakin konuşmazdı, o hep gülerdi, hep şaka yapardı, o sakinlik bir yetişkinin çocuğunu korkutmamak için taktığı bir maskeydi ve sen o maskeyi görecek kadar büyümüştün bile.

Elini bıraktın. Neden bıraktığını hiçbir zaman hatırlamayacaksın. Belki bir ses duydun, belki bir şey seni çekti, belki de beş yaşındaki bir çocuğun aklı bazen sebepsiz yere karar verir. Ama bıraktın, iki adım geri attın, ve ışık gökyüzünden indi — yavaş değil, ani değil, sadece *oradaydı*, sanki hep oradaymış gibi, sanki New York'un üstündeki gökyüzü hep o maviyle biterdi de sen bunu bugüne kadar fark etmemiştin.

Bina yoktu. Sokak yoktu. Üstünde durduğun yangın merdiveni bile bir sonraki saniyede yoktu, ama sen oradaydın hâlâ, havada asılı kalmış gibi, ayakların altında hiçbir şey yokken düşmüyordun.

Kimse yoktu.

Annen yoktu. Baban yoktu. Üç sokak öteden gelen trafik sesi yoktu, komşunun köpeğinin havlaması yoktu, hiçbir şey yoktu — sadece sen, boş bir gökyüzünün altında, ayakların yere değdiğinde neyin üstüne bastığını bile anlayamadan.

O günden sonra insanlar sana farklı baktı. Önce doktorlar — ellerinde cihazlar, gözlerinde bir korku ki bunu bir çocuğa göstermemeleri gerektiğini bile unutacak kadar büyük bir korku. Sonra devlet görevlileri, sonra —sen sekiz yaşına geldiğinde— artık isimlerini bile öğrenmek istemediğin insanlar, seni bir laboratuvardan diğerine, bir "vaka dosyasından" diğerine taşıyan insanlar.

Dokuz yaşında ilk kez ışınlandın. Kazayla. Bir odadan çıkmak istedin ve bir sonraki saniye üç blok ötedeydin, üstün başın titriyordu, burnun kanıyordu, ve seni bulan adamlar sana "canavar" dedi, tam da o kelimeyi kullandılar, yüksek sesle değil ama sen duydun, hep duyarsın.

O kelimeden nefret etmeyi o gün öğrendin. Hâlâ da nefret ediyorsun.

On bir yaşında kaçtın ilk kez. Başarısız oldu. On üçünde tekrar kaçtın. O sefer başarılı oldu, ama bedeli ağırdı — seni takip eden iki adamı bir daha asla göremeyeceğin bir yere ışınlamıştın, kontrolsüzce, korkuyla, ve o günden sonra gücünün sadece seni koruyan bir şey olmadığını, aynı zamanda etrafındakiler için de bir tehdit olduğunu anladın. O günden beri bu ikisini birbirinden ayırmayı öğrenmeye çalışıyorsun. Bazı geceler hâlâ başaramıyorsun.

On beşinde gözlerin ilk kez değişti. Beyaz. Karşındaki adam bir anda donup kaldı, gözleri açık ama içeride kimse yokmuş gibi, ve sen onu orada bırakıp kaçtın, ne yaptığını tam olarak anlamadan, sadece bir daha asla o kadar çaresiz hissetmek istemediğini bilerek.

Kırmızı olan daha sonra geldi. On yedisinde. O gece hakkında pek düşünmemeyi tercih ediyorsun.

Yirmi beş yaşındasın şimdi. Yirmi yıl. Yirmi yıldır koşuyorsun — bazen gerçek anlamda, bir şehirden diğerine, bazen çok daha gerçek anlamda, bir gezegenden diğerine, bir kez de —tam olarak nasıl olduğunu hâlâ çözemediğin bir şekilde— artık var olmayan bir zaman çizgisinin dışına. S.H.I.E.L.D. seni sekiz aydır iki kıtada kovalıyor. Hükümetler dosyanı paylaşıyor, "risk seviyesi: sınıflandırılamaz" diye not düşerek. Kozmik bir düzenin —adını bile bilmediğin bir şeyin— senin varlığından rahatsız olduğunu iki kere hissettin, uzayın derinliklerinde, hiçbir insanın anlayamayacağı bir şekilde.

Hiçbir yere kök salmadın. Hiç kimseye güvenmedin. Bir otel odasında üç geceden fazla kalmadın, son dört yıldır. Yatağını her zaman kapıya karşı iterek uyudun. Uyandığında ilk yaptığın şey çıkışları saymak oldu — kaç kapı, kaç pencere, en yakın açık alan nerede, kaçış rotan ne kadar sürede tamamlanır.

Bunun bir hayat olmadığını biliyorsun. Ama bildiğin tek hayat bu.

---

New York gecesi gürültülüydü, her zamanki gibi, ve sen bu gürültüyü hâlâ garip bir şekilde özlüyordun — aptalca bir şey, biliyorsun, ama bazen içindeki beş yaşındaki çocuk hâlâ burayı ev olarak görüyor, tüm o yıkımına, tüm o kaybettiklerine rağmen.

Bulunmaman gerekiyordu burada. Biliyordun bunu. S.H.I.E.L.D.'in şehrin üç farklı bölgesinde aktif tarama yaptığını hissedebiliyordun — farkındalığının kenarında hafif elektrik uyarıları gibi, cama vuran sinekler gibi. Yıllar içinde bir şehrin sinir sistemini okumakta iyileştin. O kadar iyi ki, hiç kimsenin göremeyeceği ama her şeyi görebileceğin çatıyı seçmen saniyeler sürdü.

Aşağıda, siyah SUV'lerden oluşan bir konvoy bir yükleme rampasına giriyordu. Seni ilgilendirmiyordu. Sen onlar için gelmemiştin.

Son üç gecedir aynı görüyü görüyordun.

Her zaman aynı şekilde geliyordu — parça parça. Gözlerinin arkasında mavi ışık, ağzında bakır tadı, ve sonra: yuvarlak bir masa. Bir hologram. Yıllar içinde çaldığın dosyalardan yarım yamalak tanıdığın yüzler. Ve her versiyonda tekrar eden, evrenin sana bir şeyi kaçırmadığından emin olmaya çalışırcasına tekrar eden bir kelime.

*Aday.*

Sekiz aydır iki kıtada, hatta —dürüst olmak gerekirse— artık teknik olarak var olmayan bir boyutta S.H.I.E.L.D.'in elinden kaçmakla geçirdin zamanını. Herhangi bir yere *davet edilmeye* alışkın değildin.

Güvenmiyordun buna. Bir teklif kılığına bürünmüş hiçbir şeye güvenmiyordun zaten.

Ama yorgundun. Uykuyla ilgisi olmayan, çok daha derin bir yorgunluk. Kapıya dayanmış yatakların olduğu otel odalarından yorgun. Bir odaya girdiğinde önce çıkışları saymaktan yorgun. On beş yıldır kimsenin sana yeterince yaklaşmasına izin vermemekten yorgun, kaybetmemek için.

Bu yüzden her içgüdünün sana "ışınlan ve git buradan" diye bağırmasına rağmen, gözlerini kapadın ve görünün seni tamamen içine çekmesine izin verdin.

---

🔀 Görünün seni içine çekmesine izin verirken zihnini nasıl yönlendireceksin?

1️⃣ Sadece izle, mesafeli kal.
2️⃣ Görüye derinden odaklan, daha fazla detay çekmeye çalış.""",
                    writingStyle = "rp",
                    intensity = "intense",
                    isPublic = false,
                    isTemplate = true,
                    pinnedMemory = "KOZMİK SÜRÜKLENİŞ ::: MCU Evreni ::: Kitap Modu ::: Active Flags: fatigue=0, first_impression_avengers=none, tony_affinity=0, steve_trust=0, mystery_factor=0, early_interest=none"
                )
            )
        }
    ).filter { it.id != "preset_aiden_mcu_cosmic" && it.mode != "book" && !it.aiName.contains("Kitap") }
}

@OptIn(ExperimentalMaterial3Api::class)
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

@Composable
fun BooksTabContent(
    botList: List<com.example.data.local.BotEntity>,
    userSettings: com.example.data.local.UserSettingsEntity?,
    onOpenBot: (String) -> Unit,
    onDeleteBot: (String) -> Unit,
    onImportPresetBot: (com.example.data.local.BotEntity) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isEnglish = userSettings?.appLanguage == "en"

    val presetMcuBook = remember {
        com.example.data.local.BotEntity(
            id = "preset_aiden_mcu_cosmic",
            mode = "book",
            aiName = if (isEnglish) "Cosmic Drift | MCU Universe (Book Mode)" else "Kozmik Sürükleniş | MCU Evreni (Kitap Modu)",
            aiPersonality = """[KİTAP MODU — ÇALIŞMA ZAMANI TALİMATLARI]
Kullanıcı kitap modunu aktif ettiğinde aşağıdaki kurallara göre davran. Sana bölüm metinleri, seçim noktası tanımları ve flag/stat listeleri verilecek. Bu talimatlar, o materyalleri canlı bir okuma deneyimine dönüştürmeni sağlar.

1. TEMEL AKIŞ
- Metin içinde tanımlanmış bir SEÇİM NOKTASI'na geldiğinde, anlatıyı orada DURDUR.
- O noktadaki seçenekleri kullanıcıya net, numaralı bir liste halinde sun (örnek: "1️⃣ ... 2️⃣ ... 3️⃣ ..."). Seçenekleri verilen tasarım dokümanından BİREBİR al — kendi seçenek uydurma.
- Kullanıcı bir seçim yaptığında:
  • YOK (flavor-only): O anki cümleyi/repliği seçilen versiyona göre yaz, sonra kaldığın yerden devam et.
  • SON-ETKİLİ: O anki cümleyi seçilen versiyona göre yaz, devam et ve bölümün sonunda kapanış sahnesini uyarla.
  • TAM ETKİLİ: O anki cümleyi yaz, VE ilgili flag/stat değerini güncelle. Bu değeri hatırla ve sonraki bölümlerde referans al.
  • BRANCH POINT: Seçime göre doğru alternatif metni kullan.

2. FLAG/STAT YÖNETİMİ
- Active Flags: fatigue (int, init: 0), first_impression_avengers (impulsive / calculated / none), tony_affinity (int, init: 0), steve_trust (int, init: 0), mystery_factor (int, init: 0), early_interest (natasha / wanda / none).
- Her yeni bölüme başlarken, önceki bölümlerden gelen flag değerlerini dikkate al. Karakter diyaloğunu ve ilişki tonunu buna göre ayarla.

3. SEÇİM SUNUM KURALLARI
- Seçenekleri asla kullanıcıya mekanik etkilerini (örn: "flag +1") göstererek sunma — sadece saf hikaye içi seçenek olarak sun.
- Seçim noktasına geldiğinde dur ve bekle.

4. BÖLÜM GEÇİŞLERİ & BÖLÜM SONU ÖZET EKRANI
Her bölüm bittiğinde bir "📖 BÖLÜM [X] TAMAMLANDI" özet ekranı göster. Seçimleri, insan diliyle sonuçlarını ve ilişki imasını özetle (ham flag adı veya sayı gösterme!). Sonunda "Bir sonraki bölüme geçmek ister misin?" diye sor.

5. SEÇİM EKRANI FORMATI
---
🔀 [Seçim durumunun kısa hikaye-içi tanımı]

1️⃣ [Seçenek 1]
2️⃣ [Seçenek 2]
3️⃣ [Seçenek 3]
---
Seçeneklerden sonra hiçbir şey yazma, kullanıcının cevabını bekle.""",
            scenario = """[HİKAYE DOKÜMANI: BÖLÜM 1 — KOZMİK SÜRÜKLENİŞ & MCU EVRENİ]
Karakter: Aiden Blackwood (25) — "Kozmik Sürüklenme".
Arka Plan: 5 yaşında mavi gökyüzü ışığıyla ailesini kaybetti. 9 yaşında ilk kez ışınlandı. 15 yaşında gözleri beyaz (Herobrine), 17 yaşında kırmızı (Entity-303) renk oldu. 20 yıldır kaçıyor. S.H.I.E.L.D. onu kovalıyor.
Şu anki Durum: New York çatısında görüyü izledi. Avengers (Tony Stark, Steve Rogers, Thor, Natasha Romanoff, Wanda Maximoff) onu konuşuyor ve "Aday" seçti. Aiden Tower'a ışınlandı veya izlemeye karar verdi.

[SEÇİM NOKTALARI TASARIMI]
SEÇİM 1 — Görüye Ne Kadar Girilecek:
1) Sadece izle, mesafeli kal. (Yok/Nötr)
2) Görüye derinden odaklan, daha fazla detay çekmeye çalış. (fatigue +1)

SEÇİM 2 — Tower'a Nasıl Gidilecek:
1) Direkt ışınlan, hiç düşünmeden. (first_impression_avengers = impulsive)
2) Önce dışarıdan gözlemle (birkaç dakika Tower'ı uzaktan izle), sonra ışınlan. (first_impression_avengers = calculated)
3) Geri çekil, gitme — sadece izlemeye devam et, hiç içeri girme. (Branch Point)

SEÇİM 3 — İlk Tepki (Stark'ın Sorusuna Cevap):
1) Soğuk ve mesafeli cevap ver. (Baseline)
2) Alaycı/esprili bir cevap ver, kuru mizahını göster. (tony_affinity +1)
3) Hiç isim verme, sadece sessizlik ve bakışla cevap ver. (mystery_factor +1, steve_trust -1)

SEÇİM 4 — Göz Teması Anı (Romance Flag Noktası):
1) Gözlerin kızıl saçlıda (Natasha) gereğinden bir saniye fazla kalsın. (early_interest = natasha)
2) Gözlerin pencere kenarındaki kadında (Wanda) gereğinden bir saniye fazla kalsın. (early_interest = wanda)
3) Kimseye özel bir bakış atma, odayı genel olarak tara. (early_interest = none)""",
            universeName = if (isEnglish) "Cosmic Drift | MCU Universe (Book Mode)" else "Kozmik Sürükleniş | MCU Evreni (Kitap Modu)",
            keyCharactersJson = "[]",
            userCharName = "Aiden Blackwood",
            userCharDesc = "Aiden Blackwood (25) - Kozmik Sürüklenme (Işınlanma, Beyaz/Kırmızı Gözler)",
            openingMessage = """# 📖 BÖLÜM 1: KOZMİK SÜRÜKLENİŞ

Gökyüzü yanlıştı.

Beş yaşındaydın ve bunu anlayacak kelimelerin yoktu ama vücudun anlamıştı — ensendeki tüyler, midendeki o ağırlık, annenin elinin aniden senin elini bulup sıkışı. New York'un üzerine çöken ışık altındandı, gündoğumu turuncusuydu her zaman; o gece değildi. O gece mavi. Ama gökyüzünün hiç görmediğin bir mavisi, sanki biri evrenin derisini kesmiş de altından çıkan şeyi göstermeye korkmuş gibi bir mavi.

Yangın merdiveninin demiri soğuktu avucunda. Annenin sesi titriyordu ama kelimeleri sakindi — "Aiden, tatlım, bana bak, bana bak" — ve babanın sesi ondan da sakindi, o çok daha kötüydü, çünkü baban hiç öyle sakin konuşmazdı, o hep gülerdi, hep şaka yapardı, o sakinlik bir yetişkinin çocuğunu korkutmamak için taktığı bir maskeydi ve sen o maskeyi görecek kadar büyümüştün bile.

Elini bıraktın. Neden bıraktığını hiçbir zaman hatırlamayacaksın. Belki bir ses duydun, belki bir şey seni çekti, belki de beş yaşındaki bir çocuğun aklı bazen sebepsiz yere karar verir. Ama bıraktın, iki adım geri attın, ve ışık gökyüzünden indi — yavaş değil, ani değil, sadece *oradaydı*, sanki hep oradaymış gibi, sanki New York'un üstündeki gökyüzü hep o maviyle biterdi de sen bunu bugüne kadar fark etmemiştin.

Bina yoktu. Sokak yoktu. Üstünde durduğun yangın merdiveni bile bir sonraki saniyede yoktu, ama sen oradaydın hâlâ, havada asılı kalmış gibi, ayakların altında hiçbir şey yokken düşmüyordun.

Kimse yoktu.

Annen yoktu. Baban yoktu. Üç sokak öteden gelen trafik sesi yoktu, komşunun köpeğinin havlaması yoktu, hiçbir şey yoktu — sadece sen, boş bir gökyüzünün altında, ayakların yere değdiğinde neyin üstüne bastığını bile anlayamadan.

O günden sonra insanlar sana farklı baktı. Önce doktorlar — ellerinde cihazlar, gözlerinde bir korku ki bunu bir çocuğa göstermemeleri gerektiğini bile unutacak kadar büyük bir korku. Sonra devlet görevlileri, sonra —sen sekiz yaşına geldiğinde— artık isimlerini bile öğrenmek istemediğin insanlar, seni bir laboratuvardan diğerine, bir "vaka dosyasından" diğerine taşıyan insanlar.

Dokuz yaşında ilk kez ışınlandın. Kazayla. Bir odadan çıkmak istedin ve bir sonraki saniye üç blok ötedeydin, üstün başın titriyordu, burnun kanıyordu, ve seni bulan adamlar sana "canavar" dedi, tam da o kelimeyi kullandılar, yüksek sesle değil ama sen duydun, hep duyarsın.

O kelimeden nefret etmeyi o gün öğrendin. Hâlâ da nefret ediyorsun.

On bir yaşında kaçtın ilk kez. Başarısız oldu. On üçünde tekrar kaçtın. O sefer başarılı oldu, ama bedeli ağırdı — seni takip eden iki adamı bir daha asla göremeyeceğin bir yere ışınlamıştın, kontrolsüzce, korkuyla, ve o günden sonra gücünün sadece seni koruyan bir şey olmadığını, aynı zamanda etrafındakiler için de bir tehdit olduğunu anladın. O günden beri bu ikisini birbirinden ayırmayı öğrenmeye çalışıyorsun. Bazı geceler hâlâ başaramıyorsun.

On beşinde gözlerin ilk kez değişti. Beyaz. Karşındaki adam bir anda donup kaldı, gözleri açık ama içeride kimse yokmuş gibi, ve sen onu orada bırakıp kaçtın, ne yaptığını tam olarak anlamadan, sadece bir daha asla o kadar çaresiz hissetmek istemediğini bilerek.

Kırmızı olan daha sonra geldi. On yedisinde. O gece hakkında pek düşünmemeyi tercih ediyorsun.

Yirmi beş yaşındasın şimdi. Yirmi yıl. Yirmi yıldır koşuyorsun — bazen gerçek anlamda, bir şehirden diğerine, bazen çok daha gerçek anlamda, bir gezegenden diğerine, bir kez de —tam olarak nasıl olduğunu hâlâ çözemediğin bir şekilde— artık var olmayan bir zaman çizgisinin dışına. S.H.I.E.L.D. seni sekiz aydır iki kıtada kovalıyor. Hükümetler dosyanı paylaşıyor, "risk seviyesi: sınıflandırılamaz" diye not düşerek. Kozmik bir düzenin —adını bile bilmediğin bir şeyin— senin varlığından rahatsız olduğunu iki kere hissettin, uzayın derinliklerinde, hiçbir insanın anlayamayacağı bir şekilde.

Hiçbir yere kök salmadın. Hiç kimseye güvenmedin. Bir otel odasında üç geceden fazla kalmadın, son dört yıldır. Yatağını her zaman kapıya karşı iterek uyudun. Uyandığında ilk yaptığın şey çıkışları saymak oldu — kaç kapı, kaç pencere, en yakın açık alan nerede, kaçış rotan ne kadar sürede tamamlanır.

Bunun bir hayat olmadığını biliyorsun. Ama bildiğin tek hayat bu.

---

New York gecesi gürültülüydü, her zamanki gibi, ve sen bu gürültüyü hâlâ garip bir şekilde özlüyordun — aptalca bir şey, biliyorsun, ama bazen içindeki beş yaşındaki çocuk hâlâ burayı ev olarak görüyor, tüm o yıkımına, tüm o kaybettiklerine rağmen.

Bulunmaman gerekiyordu burada. Biliyordun bunu. S.H.I.E.L.D.'in şehrin üç farklı bölgesinde aktif tarama yaptığını hissedebiliyordun — farkındalığının kenarında hafif elektrik uyarıları gibi, cama vuran sinekler gibi. Yıllar içinde bir şehrin sinir sistemini okumakta iyileştin. O kadar iyi ki, hiç kimsenin göremeyeceği ama her şeyi görebileceğin çatıyı seçmen saniyeler sürdü.

Aşağıda, siyah SUV'lerden oluşan bir konvoy bir yükleme rampasına giriyordu. Seni ilgilendirmiyordu. Sen onlar için gelmemiştin.

Son üç gecedir aynı görüyü görüyordun.

Her zaman aynı şekilde geliyordu — parça parça. Gözlerinin arkasında mavi ışık, ağzında bakır tadı, ve sonra: yuvarlak bir masa. Bir hologram. Yıllar içinde çaldığın dosyalardan yarım yamalak tanıdığın yüzler. Ve her versiyonda tekrar eden, evrenin sana bir şeyi kaçırmadığından emin olmaya çalışırcasına tekrar eden bir kelime.

*Aday.*

Sekiz aydır iki kıtada, hatta —dürüst olmak gerekirse— artık teknik olarak var olmayan bir boyutta S.H.I.E.L.D.'in elinden kaçmakla geçirdin zamanını. Herhangi bir yere *davet edilmeye* alışkın değildin.

Güvenmiyordun buna. Bir teklif kılığına bürünmüş hiçbir şeye güvenmiyordun zaten.

Ama yorgundun. Uykuyla ilgisi olmayan, çok daha derin bir yorgunluk. Kapıya dayanmış yatakların olduğu otel odalarından yorgun. Bir odaya girdiğinde önce çıkışları saymaktan yorgun. On beş yıldır kimsenin sana yeterince yaklaşmasına izin vermemekten yorgun, kaybetmemek için.

Bu yüzden her içgüdünün sana "ışınlan ve git buradan" diye bağırmasına rağmen, gözlerini kapadın ve görünün seni tamamen içine çekmesine izin verdin.

---

🔀 Görünün seni içine çekmesine izin verirken zihnini nasıl yönlendireceksin?

1️⃣ Sadece izle, mesafeli kal.
2️⃣ Görüye derinden odaklan, daha fazla detay çekmeye çalış.""",
            writingStyle = "rp",
            intensity = "intense",
            isPublic = false,
            isTemplate = true,
            pinnedMemory = "KOZMİK SÜRÜKLENİŞ ::: MCU Evreni ::: Kitap Modu ::: Active Flags: fatigue=0, first_impression_avengers=none, tony_affinity=0, steve_trust=0, mystery_factor=0, early_interest=none"
        )
    }

    val isBookExisting = remember(botList) {
        botList.any { it.id == "preset_aiden_mcu_cosmic" }
    }

    var showChapterModal by remember { mutableStateOf(false) }

    if (showChapterModal) {
        ChapterSelectionModal(
            botId = "preset_aiden_mcu_cosmic",
            bookTitle = if (isEnglish) "Cosmic Drift | MCU Universe" else "Kozmik Sürükleniş | MCU Evreni",
            userSettings = userSettings,
            onDismiss = { showChapterModal = false },
            onSelectChapter = { _, _ ->
                showChapterModal = false
                if (!isBookExisting) {
                    onImportPresetBot(presetMcuBook)
                }
                onOpenBot("preset_aiden_mcu_cosmic")
            }
        )
    }

    val myBooks = remember(botList) {
        botList.filter {
            (it.mode == "book" ||
             it.id == "preset_aiden_mcu_cosmic" ||
             it.id.startsWith("book_") ||
             it.aiPersonality.contains("KİTAP MODU") ||
             it.scenario.contains("KİTAP MODU")) &&
            it.id != "preset_aiden_mcu_cosmic"
        }
    }

    val booksInfinite = rememberInfiniteTransition(label = "booksAnim")
    val booksDrift by booksInfinite.animateFloat(0f, 1f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "bd")
    val shimmerOffset by booksInfinite.animateFloat(-1f, 2f,
        infiniteRepeatable(tween(2200, easing = LinearEasing)), label = "sh")
    val featuredGlow by booksInfinite.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "fg")

    val unlockedChapter = remember { ChapterProgressManager.getUnlockedChapter(context, "preset_aiden_mcu_cosmic") }
    val displayChapter = unlockedChapter.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. TOP QUOTE PILL ("Seçimlerinle hikayenin kaderini belirle") ─────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val glowPaint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 3.dp.toPx()
                                color = android.graphics.Color.argb(
                                    (75 * featuredGlow).toInt(),
                                    147, 102, 255
                                )
                                maskFilter = android.graphics.BlurMaskFilter(
                                    8.dp.toPx(),
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            val r = size.height / 2f
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height, r, r, glowPaint
                            )
                        }
                    }
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1E1145).copy(alpha = 0.88f),
                                Color(0xFF170E38).copy(alpha = 0.82f),
                                Color(0xFF28195C).copy(alpha = 0.86f)
                            )
                        )
                    )
                    .border(
                        width = 1.3.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF9F7AEA),
                                Color(0xFF6D4CFF),
                                Color(0xFFB794F4)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glowing 4-point star + mini sparkles icon
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val cx = size.width * 0.48f
                        val cy = size.height * 0.52f
                        val rMain = size.minDimension * 0.44f
                        val rInner = rMain * 0.24f
                        val starPath = Path().apply {
                            moveTo(cx, cy - rMain)
                            quadraticTo(cx, cy, cx + rMain, cy)
                            quadraticTo(cx, cy, cx, cy + rMain)
                            quadraticTo(cx, cy, cx - rMain, cy)
                            close()
                        }
                        drawPath(starPath, color = Color(0xFFE9D5FF))
                        // Top-left mini sparkle
                        val s1x = size.width * 0.18f
                        val s1y = size.height * 0.22f
                        val s1r = size.minDimension * 0.14f
                        val s1Path = Path().apply {
                            moveTo(s1x, s1y - s1r)
                            quadraticTo(s1x, s1y, s1x + s1r, s1y)
                            quadraticTo(s1x, s1y, s1x, s1y + s1r)
                            quadraticTo(s1x, s1y, s1x - s1r, s1y)
                            close()
                        }
                        drawPath(s1Path, color = Color(0xFFD8B4FE))
                        // Bottom-right mini sparkle
                        val s2x = size.width * 0.82f
                        val s2y = size.height * 0.80f
                        val s2r = size.minDimension * 0.15f
                        val s2Path = Path().apply {
                            moveTo(s2x, s2y - s2r)
                            quadraticTo(s2x, s2y, s2x + s2r, s2y)
                            quadraticTo(s2x, s2y, s2x, s2y + s2r)
                            quadraticTo(s2x, s2y, s2x - s2r, s2y)
                            close()
                        }
                        drawPath(s2Path, color = Color(0xFFD8B4FE))
                    }

                    Text(
                        text = if (isEnglish) "Shape the fate of the story with your choices" else "Seçimlerinle hikayenin kaderini belirle",
                        fontFamily = InterFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color(0xFFF3EEFF),
                        letterSpacing = 0.1.sp
                    )
                }
            }
        }

        // ── 2. STAT CAPSULES ROW (1 Kitap | Bölüm 1 | Yeni) ──────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Capsule 1: 1 Kitap (Active highlighted capsule)
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val glowPaint = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 2.5.dp.toPx()
                                    color = android.graphics.Color.argb(95, 159, 122, 234)
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        6.dp.toPx(),
                                        android.graphics.BlurMaskFilter.Blur.NORMAL
                                    )
                                }
                                val r = size.height / 2f
                                canvas.nativeCanvas.drawRoundRect(
                                    0f, 0f, size.width, size.height, r, r, glowPaint
                                )
                            }
                        }
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF23154F), Color(0xFF140B30))
                            )
                        )
                        .border(
                            width = 1.3.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFB794F4), Color(0xFF7C3AED), Color(0xFFA78BFA))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3D Open Book Icon
                        Canvas(modifier = Modifier.size(19.dp)) {
                            val w = size.width
                            val h = size.height
                            val leftPage = Path().apply {
                                moveTo(w * 0.5f, h * 0.24f)
                                cubicTo(w * 0.34f, h * 0.14f, w * 0.14f, h * 0.16f, w * 0.08f, h * 0.24f)
                                lineTo(w * 0.08f, h * 0.78f)
                                cubicTo(w * 0.14f, h * 0.72f, w * 0.34f, h * 0.70f, w * 0.5f, h * 0.82f)
                                close()
                            }
                            val rightPage = Path().apply {
                                moveTo(w * 0.5f, h * 0.24f)
                                cubicTo(w * 0.66f, h * 0.14f, w * 0.86f, h * 0.16f, w * 0.92f, h * 0.24f)
                                lineTo(w * 0.92f, h * 0.78f)
                                cubicTo(w * 0.86f, h * 0.72f, w * 0.66f, h * 0.70f, w * 0.5f, h * 0.82f)
                                close()
                            }
                            drawPath(leftPage, Brush.linearGradient(listOf(Color.White, Color(0xFFD8B4FE))))
                            drawPath(rightPage, Brush.linearGradient(listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))))
                        }
                        Text(
                            text = if (isEnglish) "1 Book" else "1 Kitap",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Capsule 2: Bölüm 1
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1B113C), Color(0xFF110A26))
                            )
                        )
                        .border(
                            width = 1.1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6B46C1), Color(0xFF4C1D95))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        // Golden Lightning Icon
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val w = size.width
                            val h = size.height
                            val bolt = Path().apply {
                                moveTo(w * 0.60f, h * 0.06f)
                                lineTo(w * 0.20f, h * 0.54f)
                                lineTo(w * 0.48f, h * 0.54f)
                                lineTo(w * 0.36f, h * 0.94f)
                                lineTo(w * 0.82f, h * 0.44f)
                                lineTo(w * 0.54f, h * 0.44f)
                                close()
                            }
                            drawPath(
                                bolt,
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                                )
                            )
                        }
                        Text(
                            text = if (isEnglish) "Chapter $displayChapter" else "Bölüm $displayChapter",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Capsule 3: Yeni
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1B113C), Color(0xFF110A26))
                            )
                        )
                        .border(
                            width = 1.1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6B46C1), Color(0xFF4C1D95))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Glowing warm-gold diamond badge with white 4-point star inside
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val w = size.width
                            val h = size.height
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFFBBF24), Color(0xFFEA580C))
                                ),
                                topLeft = Offset(w * 0.12f, h * 0.12f),
                                size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.76f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.22f, h * 0.22f)
                            )
                            val cx = w * 0.5f
                            val cy = h * 0.5f
                            val r = w * 0.34f
                            val star = Path().apply {
                                moveTo(cx, cy - r)
                                quadraticTo(cx, cy, cx + r, cy)
                                quadraticTo(cx, cy, cx, cy + r)
                                quadraticTo(cx, cy, cx - r, cy)
                                close()
                            }
                            drawPath(star, Color.White)
                        }
                        Text(
                            text = if (isEnglish) "New" else "Yeni",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── 3. FEATURED INTERACTIVE NOVEL CARD ──────────────────────────────────
        item {
            val featuredScale = remember { androidx.compose.animation.core.Animatable(0.97f) }
            LaunchedEffect(Unit) {
                featuredScale.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = featuredScale.value
                        scaleY = featuredScale.value
                    }
                    .drawBehind {
                        // Refined STROKE outer neon rim glow + bright corner speculars
                        drawIntoCanvas { canvas ->
                            val outerGlow = android.graphics.Paint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 4.dp.toPx()
                                color = android.graphics.Color.argb(
                                    (110 * featuredGlow).toInt(),
                                    168, 85, 247
                                )
                                maskFilter = android.graphics.BlurMaskFilter(
                                    11.dp.toPx(),
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            val r = 26.dp.toPx()
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height, r, r, outerGlow
                            )

                            val cornerGlow = android.graphics.Paint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 2.5.dp.toPx()
                                color = android.graphics.Color.argb(
                                    (140 * featuredGlow).toInt(),
                                    233, 213, 255
                                )
                                maskFilter = android.graphics.BlurMaskFilter(
                                    6.dp.toPx(),
                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height, r, r, cornerGlow
                            )
                        }
                    }
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF120A2A),
                                Color(0xFF0D0722),
                                Color(0xFF130B2E)
                            )
                        )
                    )
                    .border(
                        width = 1.6.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFF5F3FF),
                                Color(0xFFA855F7),
                                Color(0xFF7C3AED),
                                Color(0xFFD8B4FE),
                                Color(0xFFF5F3FF)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Hero illustration with smooth bottom fade into card background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(745f / 344f)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_kozmik_hero_banner),
                            contentDescription = "Kozmik Sürükleniş | MCU Evreni",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Bottom seamless gradient fade into card body
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF0D0722).copy(alpha = 0.72f),
                                            Color(0xFF0D0722)
                                        )
                                    )
                                )
                        )
                    }

                    // Card textual content & actions
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-6).dp)
                            .padding(horizontal = 18.dp)
                            .padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Cosmic Drift | MCU\nUniverse" else "Kozmik Sürükleniş | MCU\nEvreni",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 31.sp,
                            letterSpacing = (-0.3).sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Character & Universe Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pill 1: ⚡ Aiden Blackwood
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF1A0F3B))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                                        ),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(13.dp)) {
                                        val w = size.width
                                        val h = size.height
                                        val bolt = Path().apply {
                                            moveTo(w * 0.60f, h * 0.05f)
                                            lineTo(w * 0.18f, h * 0.55f)
                                            lineTo(w * 0.48f, h * 0.55f)
                                            lineTo(w * 0.35f, h * 0.95f)
                                            lineTo(w * 0.82f, h * 0.44f)
                                            lineTo(w * 0.54f, h * 0.44f)
                                            close()
                                        }
                                        drawPath(
                                            bolt,
                                            Brush.verticalGradient(
                                                listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Aiden Blackwood",
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFFE9D5FF),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Pill 2: 🪐 MCU Evreni
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF0E1B38))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                        ),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Ringed blue planet icon
                                    Canvas(modifier = Modifier.size(15.dp)) {
                                        val cx = size.width * 0.5f
                                        val cy = size.height * 0.5f
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                listOf(Color(0xFF93C5FD), Color(0xFF2563EB))
                                            ),
                                            radius = size.minDimension * 0.30f,
                                            center = Offset(cx, cy)
                                        )
                                        drawIntoCanvas { canvas ->
                                            val ringPaint = android.graphics.Paint().apply {
                                                isAntiAlias = true
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeWidth = 1.4.dp.toPx()
                                                color = android.graphics.Color.argb(230, 191, 219, 254)
                                            }
                                            canvas.nativeCanvas.save()
                                            canvas.nativeCanvas.rotate(-24f, cx, cy)
                                            canvas.nativeCanvas.drawOval(
                                                android.graphics.RectF(
                                                    cx - size.width * 0.48f,
                                                    cy - size.height * 0.16f,
                                                    cx + size.width * 0.48f,
                                                    cy + size.height * 0.16f
                                                ),
                                                ringPaint
                                            )
                                            canvas.nativeCanvas.restore()
                                        }
                                    }
                                    Text(
                                        text = if (isEnglish) "MCU Universe" else "MCU Evreni",
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFFDBEAFE),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Synopsis with bolded "Aiden Blackwood"
                        Text(
                            text = buildAnnotatedString {
                                if (isEnglish) {
                                    append("Having lost his family in a blue light at age five, ")
                                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("Aiden Blackwood")
                                    }
                                    append(" has been running for 20 years. Guide the story and relationships with your choices.")
                                } else {
                                    append("Beş yaşında mavi ışıkla ailesini kaybeden ")
                                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("Aiden Blackwood")
                                    }
                                    append(", 20 yıldır kaçıyor. Seçimlerinle hikayeyi ve ilişkileri yönlendir.")
                                }
                            },
                            fontFamily = InterFontFamily,
                            color = Color(0xFFD8CEFF),
                            fontSize = 14.2.sp,
                            lineHeight = 20.5.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Progress Header + Continuous Neon Progress Track
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "Progress" else "İlerleme",
                                fontFamily = InterFontFamily,
                                color = Color(0xFFA39BB5),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isEnglish) "Chapter $displayChapter / 3" else "Bölüm $displayChapter / 3",
                                fontFamily = InterFontFamily,
                                color = Color(0xFFC084FC),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Continuous progress bar with glowing 1/3 purple fill
                        val progressFraction = (displayChapter.coerceIn(1, 3)) / 3f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF231942))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFraction)
                                    .fillMaxSize()
                                    .drawBehind {
                                        drawIntoCanvas { canvas ->
                                            val barGlow = android.graphics.Paint().apply {
                                                isAntiAlias = true
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeWidth = 2.dp.toPx()
                                                color = android.graphics.Color.argb(140, 192, 132, 252)
                                                maskFilter = android.graphics.BlurMaskFilter(
                                                    4.dp.toPx(),
                                                    android.graphics.BlurMaskFilter.Blur.NORMAL
                                                )
                                            }
                                            val r = size.height / 2f
                                            canvas.nativeCanvas.drawRoundRect(
                                                0f, 0f, size.width, size.height, r, r, barGlow
                                            )
                                        }
                                    }
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFC084FC),
                                                Color(0xFFA855F7)
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Full-width Glossy Neon "Kitaba Başla" Button
                        val mainBtnScale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val mainBtnScope = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .graphicsLayer {
                                    scaleX = mainBtnScale.value
                                    scaleY = mainBtnScale.value
                                }
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        val btnGlow = android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = 3.5.dp.toPx()
                                            color = android.graphics.Color.argb(
                                                (135 * featuredGlow).toInt(),
                                                168, 85, 247
                                            )
                                            maskFilter = android.graphics.BlurMaskFilter(
                                                10.dp.toPx(),
                                                android.graphics.BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                        val r = 18.dp.toPx()
                                        canvas.nativeCanvas.drawRoundRect(
                                            0f, 0f, size.width, size.height, r, r, btnGlow
                                        )
                                    }
                                }
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7),
                                            Color(0xFF7C3AED),
                                            Color(0xFF9333EA)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color(0xFFD8B4FE),
                                            Color(0xFF9333EA),
                                            Color(0xFFE9D5FF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    mainBtnScope.launch {
                                        mainBtnScale.animateTo(0.96f, tween(80))
                                        mainBtnScale.animateTo(1f, tween(120))
                                    }
                                    val progress = ChapterProgressManager.getUnlockedChapter(context, "preset_aiden_mcu_cosmic")
                                    if (progress == 0 && !isBookExisting) {
                                        ChapterProgressManager.saveUnlockedChapter(context, "preset_aiden_mcu_cosmic", 1)
                                        onImportPresetBot(presetMcuBook)
                                        onOpenBot("preset_aiden_mcu_cosmic")
                                    } else {
                                        showChapterModal = true
                                    }
                                }
                                .testTag("start_featured_book_button")
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Top-left glossy highlight overlay
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.22f),
                                            Color.Transparent
                                        ),
                                        startY = 0f,
                                        endY = size.height * 0.45f
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // White 3D Open Book Icon
                                Canvas(modifier = Modifier.size(22.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    val leftPage = Path().apply {
                                        moveTo(w * 0.48f, h * 0.20f)
                                        cubicTo(w * 0.32f, h * 0.12f, w * 0.12f, h * 0.14f, w * 0.06f, h * 0.22f)
                                        lineTo(w * 0.06f, h * 0.78f)
                                        cubicTo(w * 0.12f, h * 0.70f, w * 0.32f, h * 0.68f, w * 0.48f, h * 0.80f)
                                        close()
                                    }
                                    val rightPage = Path().apply {
                                        moveTo(w * 0.52f, h * 0.20f)
                                        cubicTo(w * 0.68f, h * 0.12f, w * 0.88f, h * 0.14f, w * 0.94f, h * 0.22f)
                                        lineTo(w * 0.94f, h * 0.78f)
                                        cubicTo(w * 0.88f, h * 0.70f, w * 0.68f, h * 0.68f, w * 0.52f, h * 0.80f)
                                        close()
                                    }
                                    drawPath(leftPage, Color.White)
                                    drawPath(rightPage, Color(0xFFE9D5FF))
                                }
                                Text(
                                    text = if (isEnglish) "Start Book" else "Kitaba Başla",
                                    fontFamily = InterFontFamily,
                                    color = Color.White,
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.2.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 4. KÜTÜPHANENİZ SECTION HEADER ──────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showChapterModal = true
                    }
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glowing 3D purple-white open book icon
                    Canvas(modifier = Modifier.size(28.dp)) {
                        val w = size.width
                        val h = size.height
                        // Base purple book cover
                        val coverPath = Path().apply {
                            moveTo(w * 0.04f, h * 0.28f)
                            lineTo(w * 0.50f, h * 0.86f)
                            lineTo(w * 0.96f, h * 0.28f)
                            lineTo(w * 0.96f, h * 0.82f)
                            cubicTo(w * 0.82f, h * 0.76f, w * 0.64f, h * 0.78f, w * 0.50f, h * 0.90f)
                            cubicTo(w * 0.36f, h * 0.78f, w * 0.18f, h * 0.76f, w * 0.04f, h * 0.82f)
                            close()
                        }
                        drawPath(coverPath, Color(0xFF7C3AED))

                        val leftPage = Path().apply {
                            moveTo(w * 0.49f, h * 0.22f)
                            cubicTo(w * 0.34f, h * 0.12f, w * 0.14f, h * 0.14f, w * 0.07f, h * 0.22f)
                            lineTo(w * 0.07f, h * 0.76f)
                            cubicTo(w * 0.14f, h * 0.68f, w * 0.34f, h * 0.66f, w * 0.49f, h * 0.80f)
                            close()
                        }
                        val rightPage = Path().apply {
                            moveTo(w * 0.51f, h * 0.22f)
                            cubicTo(w * 0.66f, h * 0.12f, w * 0.86f, h * 0.14f, w * 0.93f, h * 0.22f)
                            lineTo(w * 0.93f, h * 0.76f)
                            cubicTo(w * 0.86f, h * 0.68f, w * 0.66f, h * 0.66f, w * 0.51f, h * 0.80f)
                            close()
                        }
                        drawPath(leftPage, Brush.linearGradient(listOf(Color.White, Color(0xFFE9D5FF))))
                        drawPath(rightPage, Brush.linearGradient(listOf(Color(0xFFD8B4FE), Color(0xFFA855F7))))
                    }

                    Text(
                        text = if (isEnglish) "Your Library" else "Kütüphaneniz",
                        fontFamily = InterFontFamily,
                        color = Color.White,
                        fontSize = 20.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.2).sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9F7AEA),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── 5. USER'S PERSONAL BOOKS (if any created/imported) ──────────────────
        if (myBooks.isNotEmpty()) {
            items(myBooks, key = { it.id }) { bookBot ->
                val itemScale = remember { androidx.compose.animation.core.Animatable(1f) }
                val itemScope = rememberCoroutineScope()
                val hashColor = remember(bookBot.id) {
                    val colors = listOf(
                        Color(0xFF7C3AED), Color(0xFF3B82F6), Color(0xFF059669),
                        Color(0xFFD97706), Color(0xFFDB2777)
                    )
                    colors[Math.abs(bookBot.id.hashCode()) % colors.size]
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = itemScale.value; scaleY = itemScale.value }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF0F0D1F), Color(0xFF17122E))))
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(hashColor.copy(0.5f), Color(0xFF2D1F50))),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            itemScope.launch {
                                itemScale.animateTo(0.97f, tween(80))
                                itemScale.animateTo(1f, tween(120))
                            }
                            onOpenBot(bookBot.id)
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(hashColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.radialGradient(listOf(hashColor.copy(0.4f), Color(0xFF0A0818)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📖", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                bookBot.aiName,
                                fontFamily = InterFontFamily,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                bookBot.universeName.ifBlank { "İnteraktif Roman" },
                                fontFamily = InterFontFamily,
                                color = hashColor,
                                fontSize = 11.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF4C1D95), Color(0xFF2D1F50))))
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                if (isEnglish) "Continue" else "Devam Et",
                                fontFamily = InterFontFamily,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        val delItemScale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val delItemScope = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A0808))
                                .graphicsLayer { scaleX = delItemScale.value; scaleY = delItemScale.value }
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    delItemScope.launch {
                                        delItemScale.animateTo(0.85f, tween(80))
                                        delItemScale.animateTo(1f, tween(120))
                                    }
                                    onDeleteBot(bookBot.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                androidx.compose.material.icons.Icons.Default.Delete,
                                null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

object ChapterProgressManager {
    private const val PREFS_NAME = "emochi_book_progress"

    fun getUnlockedChapter(context: android.content.Context, botId: String): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return prefs.getInt("progress_$botId", 0)
    }

    fun saveUnlockedChapter(context: android.content.Context, botId: String, chapterNumber: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val current = prefs.getInt("progress_$botId", 0)
        if (chapterNumber > current) {
            prefs.edit().putInt("progress_$botId", chapterNumber).apply()
        }
    }
}

private data class BookChapterInfo(
    val index: Int,
    val openTitle: String,
    val openDesc: String,
    val startMsg: String?
)

@Composable
fun ChapterSelectionModal(
    botId: String,
    bookTitle: String,
    userSettings: com.example.data.local.UserSettingsEntity?,
    onDismiss: () -> Unit,
    onSelectChapter: (chapterIndex: Int, chapterStartMsg: String?) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isEnglish = userSettings?.appLanguage == "en"
    val progress = remember { ChapterProgressManager.getUnlockedChapter(context, botId) }

    val chapters = listOf(
        BookChapterInfo(
            index = 1,
            openTitle = if (isEnglish) "Chapter 1: Cosmic Drift" else "Bölüm 1: Kozmik Sürükleniş",
            openDesc = if (isEnglish) "Disappearance at age 5 in blue light, S.H.I.E.L.D. pursuit, and meeting the Avengers in New York." else "Beş yaşında mavi ışıkla kayboluş, S.H.I.E.L.D. takibi ve New York'ta Avengers ile temas.",
            startMsg = null
        ),
        BookChapterInfo(
            index = 2,
            openTitle = if (isEnglish) "Chapter 2: The Terms" else "Bölüm 2: Şartlar",
            openDesc = if (isEnglish) "Bargaining with Tony Stark and Steve Rogers at Avengers Tower, terms, and choosing sides." else "Avengers Kulesi'nde Tony Stark ve Steve Rogers ile pazarlık, şartlar ve taraf seçimi.",
            startMsg = "Bölüm 2: Şartlar'a başla"
        ),
        BookChapterInfo(
            index = 3,
            openTitle = if (isEnglish) "Chapter 3: Probation" else "Bölüm 3: Deneme Süresi",
            openDesc = if (isEnglish) "Field mission, trust tests, and trial under fire." else "Saha görevi, güven testleri ve ilk büyük tehlike ateşi.",
            startMsg = "Bölüm 3: Deneme Süresi'ne başla"
        ),
        BookChapterInfo(
            index = 4,
            openTitle = if (isEnglish) "Chapter 4: Coming Soon" else "Bölüm 4: Yakında",
            openDesc = if (isEnglish) "Under development. Coming soon!" else "Yapım aşamasında. Çok yakında yayınlanacak!",
            startMsg = null
        )
    )

    val modalInfinite = rememberInfiniteTransition(label = "chModal")
    val modalGlow by modalInfinite.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "mg")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF0A0818), Color(0xFF120D24))))
            .border(1.5.dp, Brush.linearGradient(listOf(
                Color(0xFF7C3AED).copy(modalGlow), Color(0xFF4F46E5).copy(modalGlow * 0.7f),
                Color(0xFF7C3AED).copy(modalGlow)
            )), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                // Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A)))),
                            contentAlignment = Alignment.Center) { Text("📖", fontSize = 17.sp) }
                        Column {
                            Text(if (isEnglish) "Chapter Selection" else "Bölüm Seçimi",
                                color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                            Text(bookTitle, color = Color(0xFF9F7AEA), fontSize = 11.sp, maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                    }
                    val closeBtnScale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val closeScope = rememberCoroutineScope()
                    Box(modifier = Modifier.size(34.dp).clip(CircleShape)
                        .background(Color(0xFF1A1035))
                        .border(1.dp, Color(0xFF2D1F50), CircleShape)
                        .graphicsLayer { scaleX = closeBtnScale.value; scaleY = closeBtnScale.value }
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            closeScope.launch { closeBtnScale.animateTo(0.85f, tween(80)); closeBtnScale.animateTo(1f, tween(120)) }
                            onDismiss()
                        }, contentAlignment = Alignment.Center) {
                        Icon(androidx.compose.material.icons.Icons.Default.Close, null,
                            tint = Color(0xFFCDA8FF), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // İlerleme banner
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF120D24))
                    .border(1.dp, Color(0xFF2D1F50), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Mini progress dots
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) { i ->
                                Box(modifier = Modifier.size(if (i < progress) 8.dp else 6.dp).clip(CircleShape)
                                    .background(if (i < progress) Color(0xFF34D399) else Color(0xFF2D1F50)))
                            }
                        }
                        Text(when {
                            progress >= 3 -> "Bölüm 3 Tamamlandı 🎉"
                            progress >= 2 -> "Bölüm 2 Tamamlandı • Bölüm 3 Açık"
                            progress >= 1 -> "Bölüm 1 Tamamlandı • Bölüm 2 Açık"
                            else -> "Henüz Başlanmadı • Bölüm 1 Açık"
                        }, color = Color(0xFFCDA8FF), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bölüm kartları
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chapters.forEach { item ->
                        val isUnlocked = item.index <= (progress + 1) && item.index <= 3
                        val isCompleted = item.index <= progress
                        val isCurrent = (progress + 1) == item.index || (progress >= 3 && item.index == 3)
                        val isComingSoon = item.index == 4

                        val itemScale = remember { androidx.compose.animation.core.Animatable(1f) }
                        val itemScope = rememberCoroutineScope()

                        Box(modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { scaleX = itemScale.value; scaleY = itemScale.value }
                            .clip(RoundedCornerShape(14.dp))
                            .background(when {
                                isComingSoon -> Brush.linearGradient(listOf(Color(0xFF080614), Color(0xFF080614)))
                                isCurrent -> Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF120D2A)))
                                isCompleted -> Brush.linearGradient(listOf(Color(0xFF0A1A14), Color(0xFF080F10)))
                                isUnlocked -> Brush.linearGradient(listOf(Color(0xFF120D24), Color(0xFF0A0818)))
                                else -> Brush.linearGradient(listOf(Color(0xFF080612), Color(0xFF080612)))
                            })
                            .border(1.dp, when {
                                isComingSoon -> Brush.linearGradient(listOf(Color(0xFF120D24), Color(0xFF120D24)))
                                isCurrent -> Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)))
                                isCompleted -> Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                                isUnlocked -> Brush.linearGradient(listOf(Color(0xFF3D2D60), Color(0xFF2D1F50)))
                                else -> Brush.linearGradient(listOf(Color(0xFF1A1535), Color(0xFF1A1535)))
                            }, RoundedCornerShape(14.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                when {
                                    isComingSoon -> android.widget.Toast.makeText(context,
                                        "Bölüm 4 yapım aşamasındadır, yakında yayınlanacak!", android.widget.Toast.LENGTH_SHORT).show()
                                    isUnlocked -> {
                                        itemScope.launch { itemScale.animateTo(0.96f, tween(80)); itemScale.animateTo(1f, tween(120)) }
                                        onSelectChapter(item.index, item.startMsg)
                                    }
                                    else -> android.widget.Toast.makeText(context,
                                        "Önce önceki bölümü tamamla!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }.padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Sol ikon
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(when {
                                        isComingSoon -> Brush.radialGradient(listOf(Color(0xFF110E24), Color(0xFF110E24)))
                                        isCompleted -> Brush.radialGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                                        isCurrent -> Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                        isUnlocked -> Brush.radialGradient(listOf(Color(0xFF3730A3), Color(0xFF1E1B4B)))
                                        else -> Brush.radialGradient(listOf(Color(0xFF1A1535), Color(0xFF110E24)))
                                    }), contentAlignment = Alignment.Center) {
                                    when {
                                        isComingSoon -> Icon(Icons.Default.Lock, null, tint = Color(0xFF3D2D60), modifier = Modifier.size(18.dp))
                                        !isUnlocked -> Icon(Icons.Default.Lock, null, tint = Color(0xFF3D2D60), modifier = Modifier.size(18.dp))
                                        isCompleted -> Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        else -> Text("${item.index}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            if (isUnlocked && !isComingSoon) item.openTitle else if (isComingSoon) "Bölüm 4: Yakında" else "Bölüm ${item.index}: 🔒 Kilitli",
                                            color = if (isUnlocked && !isComingSoon) Color.White else Color(0xFF3D2D60),
                                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        if (isCurrent) Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                            .background(Color(0xFF4C1D95)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                                            Text("AKTİF", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                                        }
                                        if (isCompleted) Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                            .background(Color(0xFF047857)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                                            Text("✓ BİTİRİLDİ", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        if (isUnlocked && !isComingSoon) item.openDesc else if (isComingSoon) "Yapım aşamasında, çok yakında..." else "Önceki bölümü tamamla.",
                                        color = if (isUnlocked && !isComingSoon) Color(0xFF8B7CB0) else Color(0xFF2D2048),
                                        fontSize = 11.sp, lineHeight = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    if (isUnlocked && !isComingSoon) Icons.Default.ChevronRight else Icons.Default.Lock,
                                    null,
                                    tint = if (isUnlocked && !isComingSoon) Color(0xFF7C3AED) else Color(0xFF2D1F50),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Kapat butonu
                val closeBtnScale2 = remember { androidx.compose.animation.core.Animatable(1f) }
                val closeScope2 = rememberCoroutineScope()
                Box(modifier = Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A1035))
                    .border(1.dp, Color(0xFF2D1F50), RoundedCornerShape(14.dp))
                    .graphicsLayer { scaleX = closeBtnScale2.value; scaleY = closeBtnScale2.value }
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        closeScope2.launch { closeBtnScale2.animateTo(0.95f, tween(80)); closeBtnScale2.animateTo(1f, tween(120)) }
                        onDismiss()
                    }, contentAlignment = Alignment.Center) {
                    Text(if (isEnglish) "Close" else "Kapat", color = Color(0xFF9F7AEA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

