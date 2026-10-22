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
fun CharacterEmotionControlModal(
    bot: BotEntity,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    keyCharacters: List<KeyCharacter> = emptyList(),
    onDismiss: () -> Unit,
    onSaveBotEmotion: (String) -> Unit,
    onSaveCharacterEmotionState: (String, String) -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────
    var selectedTab   by remember { mutableStateOf("main") }
    var activeSection by remember { mutableStateOf("mood") } // "mood" | "relations" | "primary" | "custom" | "history"
    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var emotionHistory by remember { mutableStateOf<List<com.example.data.local.EmotionHistoryEntity>>(emptyList()) }
    var historyLoaded  by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val characterStatesMap = remember(bot.emotionState, characterEmotions, keyCharacters) {
        val map = mutableMapOf<String, com.example.data.local.EmotionState>()
        map["main"] = com.example.data.local.EmotionState.fromJson(bot.emotionState)
        val existingMap = characterEmotions.associateBy { it.characterName }
        val sideNames = (characterEmotions.map { it.characterName } + keyCharacters.map { it.name })
            .filter { it.isNotBlank() }.distinct()
        sideNames.forEach { name ->
            val entity = existingMap[name]
            if (entity != null) map[name] = com.example.data.local.EmotionState.fromJson(entity.emotionState)
            else map[name] = com.example.data.local.EmotionState.calculateBaselineEmotionState(bot.aiName, name, "")
        }
        mutableStateMapOf<String, com.example.data.local.EmotionState>().apply { putAll(map) }
    }

    val curState = characterStatesMap[selectedTab] ?: com.example.data.local.EmotionState()

    var showAddCustomDialog by remember { mutableStateOf(false) }
    var newCustomName      by remember { mutableStateOf("") }
    var newCustomDifficulty by remember { mutableStateOf("Orta") }
    var newCustomMin       by remember { mutableStateOf("0") }
    var newCustomMax       by remember { mutableStateOf("100") }
    var newCustomCurrent   by remember { mutableStateOf("50") }
    var newCustomPurpose   by remember { mutableStateOf("") }

    // Load history when history tab selected
    LaunchedEffect(activeSection, selectedTab) {
        if (activeSection == "history" && !historyLoaded) {
            emotionHistory = viewModel.getRecentEmotionHistory(bot.id, 30)
            historyLoaded = true
        }
    }

    // ── helpers ────────────────────────────────────────────────────────
    @Composable
    fun SectionCard(
        title: String, emoji: String,
        accentColor: Color,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFF180C35), Color(0xFF0E0820))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.3f))
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(title, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }

    @Composable
    fun EmotionSliderRow(
        label: String, emoji: String, value: Int,
        color: Color, onChange: (Int) -> Unit
    ) {
        val animVal by animateFloatAsState(value.toFloat(), animationSpec = tween(300), label = "sliderAnim")
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.2f))
                        .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("$value%", color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(4.dp))
            // Custom gradient track via Box + Canvas overlay
            Box(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = animVal,
                    onValueChange = { onChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = color,
                        activeTrackColor = color,
                        inactiveTrackColor = Color(0xFF2A1C48)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ── Section nav pills ──────────────────────────────────────────────
    val sections = listOf(
        "mood"      to ("🎭" to "Ruh Hali"),
        "relations" to ("❤️" to "İlişki"),
        "primary"   to ("🌈" to "Duygular"),
        "custom"    to ("✨" to "Özel"),
        "history"   to ("📈" to "Geçmiş")
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF160832), Color(0xFF0B0620))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.3.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFE879A0), Color(0xFF7C3AED), Color(0xFF9333EA))
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            AppBackground()
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {

                // ── HEADER ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0xFF5B1050), Color(0xFF2D0828))
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFFF472B6), Color(0xFFBE185D))
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) { Text("🎭", fontSize = 20.sp) }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Duygu & İlişki Paneli",
                                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Tüm karakterler için duygu yönetimi",
                                color = Color(0xFFCCA8E0), fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x30E879A0))
                            .border(1.dp, Color(0x50E879A0), RoundedCornerShape(10.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFF9A8D4), modifier = Modifier.size(16.dp))
                    }
                }

                // Gradient separator
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(1.dp).background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0x80E879A0), Color(0x807C3AED), Color.Transparent)
                        )
                    )
                )
                Spacer(Modifier.height(12.dp))

                // ── CHARACTER TABS ─────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allCharTabs = listOf("main") + characterStatesMap.keys.filter { it != "main" }
                    allCharTabs.forEach { tabKey ->
                        val isSelected = selectedTab == tabKey
                        val tabInteraction = remember { MutableInteractionSource() }
                        val tabPressed by tabInteraction.collectIsPressedAsState()
                        val tabScale by animateFloatAsState(if (tabPressed) 0.93f else 1f, tween(80), label = "tabScale")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = tabScale; scaleY = tabScale }
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected)
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFFBE185D), Color(0xFF7C3AED))
                                        )
                                    else
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFF1C0D38), Color(0xFF150828))
                                        )
                                )
                                .border(
                                    if (isSelected) 1.dp else 0.8.dp,
                                    if (isSelected) Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                    else Brush.linearGradient(
                                        listOf(Color(0x40A855F7), Color(0x20BE185D))
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable(interactionSource = tabInteraction, indication = null) {
                                    selectedTab = tabKey
                                    historyLoaded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = if (tabKey == "main") "🤖 ${bot.aiName}" else "👤 $tabKey",
                                color = if (isSelected) Color.White else Color(0xFF9E8DC0),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── SECTION NAV ────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sections.forEach { (key, labelPair) ->
                        val (emoji, label) = labelPair
                        val isActive = activeSection == key
                        val secInteraction = remember { MutableInteractionSource() }
                        val secPressed by secInteraction.collectIsPressedAsState()
                        val secScale by animateFloatAsState(if (secPressed) 0.93f else 1f, tween(80), label = "secScale")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = secScale; scaleY = secScale }
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isActive) Color(0xFF3B1070) else Color(0xFF120A26)
                                )
                                .border(
                                    if (isActive) 1.dp else 0.7.dp,
                                    if (isActive) Color(0xFF9333EA) else Color(0x30A855F7),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(interactionSource = secInteraction, indication = null) {
                                    activeSection = key
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(emoji, fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    label,
                                    color = if (isActive) Color(0xFFCDA8FF) else Color(0xFF7A6A9A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── SCROLLABLE CONTENT ─────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeSection) {

                        // ── MOOD SECTION ───────────────────────────────
                        "mood" -> {
                            SectionCard("Baskın Ruh Hali", "🎭", Color(0xFFE879A0)) {
                                val presetMoods = listOf("nötr","mutlu","flörtöz","kıskanç","utangaç","gergin","tutkulu","kırgın","sevecen","soğuk","heyecanlı","sakin")
                                androidx.compose.foundation.layout.FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    presetMoods.forEach { m ->
                                        val moodInteraction = remember { MutableInteractionSource() }
                                        val moodPressed by moodInteraction.collectIsPressedAsState()
                                        val moodScale by animateFloatAsState(if (moodPressed) 0.90f else 1f, tween(80), label = "moodPill")
                                        val isSelected = curState.dominantEmotion.equals(m, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = moodScale; scaleY = moodScale }
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isSelected)
                                                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFBE185D), Color(0xFF9333EA)))
                                                    else
                                                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF1C0D38), Color(0xFF140828)))
                                                )
                                                .border(
                                                    if (isSelected) 1.dp else 0.7.dp,
                                                    if (isSelected) Color(0xFFF472B6) else Color(0x30A855F7),
                                                    RoundedCornerShape(20.dp)
                                                )
                                                .clickable(interactionSource = moodInteraction, indication = null) {
                                                    characterStatesMap[selectedTab] = curState.copy(dominantEmotion = m)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                        ) {
                                            Text(m, color = if (isSelected) Color.White else Color(0xFF9E8DC0), fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = curState.dominantEmotion,
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(dominantEmotion = it) },
                                    placeholder = { Text("Özel ruh hali girin...", fontSize = 12.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("İkincil Duygu", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                        OutlinedTextField(
                                            value = curState.computedSecondaryEmotion ?: "",
                                            onValueChange = { characterStatesMap[selectedTab] = curState.copy(computedSecondaryEmotion = it.ifBlank { null }) },
                                            placeholder = { Text("ör. minnettar", fontSize = 11.sp, color = EmochiTextMuted) },
                                            modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Bastırılmış Duygu", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                        OutlinedTextField(
                                            value = curState.suppressedEmotion,
                                            onValueChange = { characterStatesMap[selectedTab] = curState.copy(suppressedEmotion = it) },
                                            placeholder = { Text("ör. çekingenlik", fontSize = 11.sp, color = EmochiTextMuted) },
                                            modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text("⚡ Gerginlik Düzeyi", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val tensionOptions = listOf("none" to "Sakin 🍃", "mild" to "Hafif ⚡", "conflict" to "Çatışma 🔥", "crisis" to "Kriz ⚠️")
                                    tensionOptions.forEach { (key, label) ->
                                        val tInteraction = remember { MutableInteractionSource() }
                                        val tPressed by tInteraction.collectIsPressedAsState()
                                        val tScale by animateFloatAsState(if (tPressed) 0.93f else 1f, tween(80), label = "tensionScale")
                                        val isSelected = curState.tension.equals(key, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .graphicsLayer { scaleX = tScale; scaleY = tScale }
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Color(0xFFEA580C) else Color(0xFF120A26))
                                                .border(if (isSelected) 1.dp else 0.7.dp, if (isSelected) Color(0xFFFB923C) else Color(0x30F97316), RoundedCornerShape(10.dp))
                                                .clickable(interactionSource = tInteraction, indication = null) { characterStatesMap[selectedTab] = curState.copy(tension = key) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(label, color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text("🗣️ Konuşma Üslubu", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = curState.speechPattern,
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(speechPattern = it) },
                                    placeholder = { Text("ör. utangaç, kısık sesli, hızlı konuşan...", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text("🛡️ Duygusal Direnç: ${curState.resilience}/10", color = Color(0xFF9E8DC0), fontSize = 11.5.sp, modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x20A855F7)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text("${curState.resilience}", color = Color(0xFFCDA8FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Slider(
                                    value = curState.resilience.toFloat(),
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(resilience = it.toInt()) },
                                    valueRange = 1f..10f, steps = 8,
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFFA855F7), inactiveTrackColor = Color(0xFF2A1C48))
                                )
                            }
                        }

                        // ── RELATIONS SECTION ──────────────────────────
                        "relations" -> {
                            SectionCard("İlişki & Duygu Parametreleri", "❤️", Color(0xFFE879A0)) {
                                val aff = curState.affection
                                val tierLabel = when (aff) {
                                    in 0..15  -> "💔 Düşman"
                                    in 16..35 -> "👤 Yabancı"
                                    in 36..55 -> "🙂 Tanıdık"
                                    in 56..70 -> "🤝 Arkadaş"
                                    in 71..85 -> "💖 Flört"
                                    in 86..95 -> "🔥 Sevgili"
                                    else      -> "👑 Ruh Eşi"
                                }
                                // Tier pill header
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                listOf(Color(0xFF5B1040), Color(0xFF2D0820))
                                            )
                                        )
                                        .border(1.dp, Color(0xFFBE185D).copy(0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("İlişki Kademesi", color = Color(0xFFFDA4AF), fontSize = 12.sp)
                                        Text(tierLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                EmotionSliderRow("Sevgi & Yakınlık", "❤️", aff, Color(0xFFE879A0)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(relationshipAxes = curState.relationshipAxes.copy(affectionScore = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Güven Seviyesi", "🛡️", curState.trust, Color(0xFF60A5FA)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(primaryEmotions = curState.primaryEmotions.copy(trust = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Fiziksel Yakınlık", "🤝", curState.physicalComfortScore, Color(0xFFFBBF24)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(physicalComfortScore = v)
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Kırgınlık / Mesafe", "💔", curState.hurt, Color(0xFFA78BFA)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(relationshipAxes = curState.relationshipAxes.copy(resentmentScore = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Takıntı / Bağımlılık", "🖤", curState.obsession, Color(0xFFF472B6)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(obsessionScore = v)
                                }
                            }
                        }

                        // ── PRIMARY EMOTIONS SECTION ───────────────────
                        "primary" -> {
                            SectionCard("8 Temel Duygu (Plutchik)", "🌈", Color(0xFF60A5FA)) {
                                val p = curState.primaryEmotions
                                val emotions = listOf(
                                    Triple("Neşe / Mutluluk",    "😄", p.joy)         to { v: Int -> curState.copy(primaryEmotions = p.copy(joy = v)) },
                                    Triple("Üzüntü / Keder",     "😢", p.sadness)      to { v: Int -> curState.copy(primaryEmotions = p.copy(sadness = v)) },
                                    Triple("Öfke / Kızgınlık",   "😡", p.anger)        to { v: Int -> curState.copy(primaryEmotions = p.copy(anger = v)) },
                                    Triple("Korku / Endişe",     "😨", p.fear)         to { v: Int -> curState.copy(primaryEmotions = p.copy(fear = v)) },
                                    Triple("Tiksinme",           "🤢", p.disgust)       to { v: Int -> curState.copy(primaryEmotions = p.copy(disgust = v)) },
                                    Triple("Şaşkınlık",         "😲", p.surprise)      to { v: Int -> curState.copy(primaryEmotions = p.copy(surprise = v)) },
                                    Triple("Beklenti / Heyecan", "⏳", p.anticipation) to { v: Int -> curState.copy(primaryEmotions = p.copy(anticipation = v)) }
                                )
                                val emotionColors = listOf(
                                    Color(0xFFFBBF24), Color(0xFF60A5FA), Color(0xFFF87171),
                                    Color(0xFFA78BFA), Color(0xFF34D399), Color(0xFFF472B6), Color(0xFFFB923C)
                                )
                                emotions.forEachIndexed { idx, (triple, updater) ->
                                    val (label, emoji, value) = triple
                                    EmotionSliderRow(label, emoji, value, emotionColors[idx % emotionColors.size]) { v ->
                                        characterStatesMap[selectedTab] = updater(v)
                                    }
                                    if (idx < emotions.size - 1) Spacer(Modifier.height(6.dp))
                                }
                            }
                        }

                        // ── CUSTOM EMOTIONS SECTION ────────────────────
                        "custom" -> {
                            SectionCard("Karaktere Özel Duygular", "✨", Color(0xFFA855F7)) {
                                val customList = curState.customEmotions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${customList.size} özel duygu", color = Color(0xFF9E8DC0), fontSize = 12.sp)
                                    val addInteraction = remember { MutableInteractionSource() }
                                    val addPressed by addInteraction.collectIsPressedAsState()
                                    val addScale by animateFloatAsState(if (addPressed) 0.93f else 1f, tween(80), label = "addScale")
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer { scaleX = addScale; scaleY = addScale }
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                androidx.compose.ui.graphics.Brush.linearGradient(
                                                    listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                )
                                            )
                                            .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                                            .clickable(interactionSource = addInteraction, indication = null) { showAddCustomDialog = true }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Yeni Ekle", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (customList.isEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x20A855F7))
                                            .border(1.dp, Color(0x30A855F7), RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Bu karakter için henüz özel duygu eklenmedi. Sadakat, şüphe, saygı gibi benzersiz duygular ekleyebilirsin.",
                                            color = Color(0xFF9E8DC0), fontSize = 11.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.height(10.dp))
                                    customList.forEachIndexed { index, ce ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                                        listOf(Color(0xFF1A0D38), Color(0xFF120826))
                                                    )
                                                )
                                                .border(
                                                    1.dp,
                                                    when (ce.difficulty) {
                                                        "Kolay"      -> Color(0xFF22C55E).copy(0.5f)
                                                        "Orta"       -> Color(0xFF60A5FA).copy(0.5f)
                                                        "Zor"        -> Color(0xFFFBBF24).copy(0.5f)
                                                        else         -> Color(0xFFF87171).copy(0.5f)
                                                    },
                                                    RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Text("✨ ${ce.name}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                        Spacer(Modifier.width(8.dp))
                                                        val diffColor = when (ce.difficulty) {
                                                            "Kolay" -> Color(0xFF22C55E)
                                                            "Orta"  -> Color(0xFF60A5FA)
                                                            "Zor"   -> Color(0xFFFBBF24)
                                                            else    -> Color(0xFFF87171)
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(diffColor.copy(0.15f))
                                                                .border(0.8.dp, diffColor.copy(0.5f), RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(ce.difficulty, color = diffColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                                        }
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0x30F87171))
                                                            .border(0.8.dp, Color(0x50F87171), RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                val updated = customList.toMutableList().apply { removeAt(index) }
                                                                characterStatesMap[selectedTab] = curState.copy(
                                                                    customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updated)
                                                                )
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Delete, null, tint = Color(0xFFF87171), modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                if (ce.purpose.isNotBlank()) {
                                                    Spacer(Modifier.height(4.dp))
                                                    Text("🎯 ${ce.purpose}", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                                }
                                                Spacer(Modifier.height(6.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                    Text("${ce.minValue}–${ce.maxValue}", color = Color(0xFF7A6A9A), fontSize = 10.sp)
                                                    Spacer(Modifier.weight(1f))
                                                    Box(
                                                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x20A855F7)).padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("${ce.currentValue}", color = Color(0xFFCDA8FF), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                                    }
                                                }
                                                Slider(
                                                    value = ce.currentValue.toFloat().coerceIn(ce.minValue.toFloat(), ce.maxValue.toFloat()),
                                                    onValueChange = { v ->
                                                        val updatedItem = ce.copy(currentValue = v.toInt())
                                                        val updated = customList.toMutableList().apply { set(index, updatedItem) }
                                                        characterStatesMap[selectedTab] = curState.copy(
                                                            customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updated)
                                                        )
                                                    },
                                                    valueRange = ce.minValue.toFloat()..ce.maxValue.toFloat().coerceAtLeast(ce.minValue.toFloat() + 1f),
                                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFFA855F7), inactiveTrackColor = Color(0xFF2A1C48))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── HISTORY SECTION ────────────────────────────
                        "history" -> {
                            SectionCard("Duygu & İlişki Geçmişi", "📈", Color(0xFF34D399)) {
                                if (!historyLoaded) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        EmochiLoadingSpinner(size = 28.dp, color = Color(0xFF34D399))
                                    }
                                } else if (emotionHistory.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x1534D399)).border(1.dp, Color(0x3034D399), RoundedCornerShape(12.dp)).padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Henüz duygu geçmişi yok.\nSohbet başladıkça duygu değişimleri burada görünecek.", color = Color(0xFF6EE7B7), fontSize = 11.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                    }
                                } else {
                                    // ─ Line chart ──────────────────────
                                    val mainHistory = emotionHistory.filter { it.characterName == null }
                                    if (mainHistory.size >= 2) {
                                        val recentAff = mainHistory.takeLast(20)
                                        val maxVal = 100f
                                        val chartHeight = 100.dp
                                        // Net change pill
                                        val firstAff = recentAff.first().affectionScore
                                        val lastAff  = recentAff.last().affectionScore
                                        val delta    = lastAff - firstAff
                                        val deltaColor = if (delta >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Son ${recentAff.size} kayıt", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                            Box(
                                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                                    .background(deltaColor.copy(0.15f))
                                                    .border(0.8.dp, deltaColor.copy(0.5f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    "${if (delta >= 0) "+" else ""}$delta sevgi değişimi",
                                                    color = deltaColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        // Canvas chart
                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(chartHeight)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF0C0720))
                                                .border(1.dp, Color(0x2034D399), RoundedCornerShape(12.dp))
                                        ) {
                                            val w = size.width
                                            val h = size.height
                                            val stepX = if (recentAff.size > 1) w / (recentAff.size - 1) else w
                                            val pad   = 16f

                                            // Grid lines
                                            listOf(0.25f, 0.5f, 0.75f).forEach { frac ->
                                                val y = h - frac * (h - pad * 2) - pad
                                                drawLine(Color(0x1534D399), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                                            }

                                            // Affection path (green)
                                            if (recentAff.size >= 2) {
                                                val affPts = recentAff.mapIndexed { i, e ->
                                                    Offset(i * stepX, h - (e.affectionScore / maxVal) * (h - pad * 2) - pad)
                                                }
                                                for (i in 0 until affPts.size - 1) {
                                                    drawLine(Color(0xFF34D399), affPts[i], affPts[i + 1], strokeWidth = 2.5f,
                                                        cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                }
                                                // Gradient fill under line
                                                val fillPath = androidx.compose.ui.graphics.Path().apply {
                                                    moveTo(affPts.first().x, h)
                                                    affPts.forEach { lineTo(it.x, it.y) }
                                                    lineTo(affPts.last().x, h)
                                                    close()
                                                }
                                                drawPath(fillPath, androidx.compose.ui.graphics.Brush.verticalGradient(
                                                    listOf(Color(0x5034D399), Color(0x0034D399)),
                                                    startY = 0f, endY = h
                                                ))
                                                // Trust path (blue)
                                                val trPts = recentAff.mapIndexed { i, e ->
                                                    Offset(i * stepX, h - (e.trustScore / maxVal) * (h - pad * 2) - pad)
                                                }
                                                for (i in 0 until trPts.size - 1) {
                                                    drawLine(Color(0xFF60A5FA), trPts[i], trPts[i + 1], strokeWidth = 2f,
                                                        cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                }
                                                // Dots on affection line
                                                affPts.forEachIndexed { i, pt ->
                                                    val dotColor = if (i > 0 && recentAff[i].affectionScore > recentAff[i - 1].affectionScore) Color(0xFF34D399) else Color(0xFFF87171)
                                                    drawCircle(dotColor, 4f, pt)
                                                    drawCircle(Color.White, 2f, pt)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        // Legend
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF34D399)))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Sevgi", color = Color(0xFF6EE7B7), fontSize = 10.sp)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF60A5FA)))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Güven", color = Color(0xFF93C5FD), fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))
                                    // Last 8 history entries as cards
                                    Text("Son Olaylar", color = Color(0xFF9E8DC0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    emotionHistory.takeLast(8).reversed().forEach { h ->
                                        val dominant = h.dominantEmotion
                                        val moodEmoji = when (dominant.lowercase()) {
                                            "mutlu", "joy"    -> "😄"
                                            "üzgün", "sad"    -> "😢"
                                            "kızgın", "anger" -> "😡"
                                            "flört"           -> "💕"
                                            "sakin", "calm"   -> "😌"
                                            else              -> "🎭"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF0E0820))
                                                .border(0.8.dp, Color(0x2034D399), RoundedCornerShape(10.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(moodEmoji, fontSize = 16.sp)
                                                    Spacer(Modifier.width(8.dp))
                                                    Column {
                                                        Text(dominant.replaceFirstChar { it.uppercase() }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                        Text(
                                                            java.text.SimpleDateFormat("dd MMM HH:mm", java.util.Locale.getDefault()).format(java.util.Date(h.timestamp)),
                                                            color = Color(0xFF7A6A9A), fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("❤️ ${h.affectionScore}", color = Color(0xFFF9A8D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("🛡️ ${h.trustScore}", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                // ── SAVE BUTTON ────────────────────────────────────────
                Spacer(Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0x60E879A0), Color.Transparent)
                    )
                ))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val cancelInteraction = remember { MutableInteractionSource() }
                    val cancelPressed by cancelInteraction.collectIsPressedAsState()
                    val cancelScale by animateFloatAsState(if (cancelPressed) 0.95f else 1f, tween(80), label = "cancelScale")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { scaleX = cancelScale; scaleY = cancelScale }
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20A855F7))
                            .border(1.dp, Color(0x50A855F7), RoundedCornerShape(14.dp))
                            .clickable(interactionSource = cancelInteraction, indication = null) { onDismiss() }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("İptal", color = Color(0xFFCDA8FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }

                    val saveInteraction = remember { MutableInteractionSource() }
                    val savePressed by saveInteraction.collectIsPressedAsState()
                    val saveScale by animateFloatAsState(if (savePressed) 0.95f else 1f, tween(80), label = "saveScale")
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .graphicsLayer { scaleX = saveScale; scaleY = saveScale }
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFFBE185D), Color(0xFF7C3AED))
                                )
                            )
                            .border(1.dp, Color(0xFFF472B6).copy(0.5f), RoundedCornerShape(14.dp))
                            .clickable(interactionSource = saveInteraction, indication = null) {
                                characterStatesMap["main"]?.let { onSaveBotEmotion(it.toJson()) }
                                characterStatesMap.forEach { (key, state) ->
                                    if (key != "main") onSaveCharacterEmotionState(key, state.toJson())
                                }
                                onDismiss()
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Tüm Duyguları Kaydet", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddCustomDialog) {
        Dialog(onDismissRequest = { showAddCustomDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFF1A0D38), Color(0xFF100820))
                        )
                    )
                    .border(
                        1.2.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFA855F7), Color(0xFF6D28D9))
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Özel Duygu Ekle", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Duygu Adı", color = Color(0xFF9E8DC0), fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newCustomName,
                        onValueChange = { newCustomName = it },
                        placeholder = { Text("ör. Sadakat, Şüphe, Saygı...", fontSize = 11.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Kazanım Zorluğu", color = Color(0xFF9E8DC0), fontSize = 11.5.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val difficulties = listOf("Kolay", "Orta", "Zor", "Aşırı Zor")
                        val diffColors = listOf(Color(0xFF22C55E), Color(0xFF60A5FA), Color(0xFFFBBF24), Color(0xFFF87171))
                        difficulties.forEachIndexed { idx, diff ->
                            val isSelected = newCustomDifficulty == diff
                            val dInteraction = remember { MutableInteractionSource() }
                            val dPressed by dInteraction.collectIsPressedAsState()
                            val dScale by animateFloatAsState(if (dPressed) 0.93f else 1f, tween(80), label = "diffScale")
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer { scaleX = dScale; scaleY = dScale }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) diffColors[idx].copy(0.25f) else Color(0xFF120A26))
                                    .border(if (isSelected) 1.dp else 0.7.dp, if (isSelected) diffColors[idx] else Color(0x30A855F7), RoundedCornerShape(8.dp))
                                    .clickable(interactionSource = dInteraction, indication = null) { newCustomDifficulty = diff }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(diff, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Min Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomMin,
                                onValueChange = { newCustomMin = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Max Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomMax,
                                onValueChange = { newCustomMax = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Şu Anki Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomCurrent,
                                onValueChange = { newCustomCurrent = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Duygunun Amacı / Etkisi", color = EmochiTextSecondary, fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newCustomPurpose,
                        onValueChange = { newCustomPurpose = it },
                        placeholder = { Text("ör. Karakterin sadakat seviyesini ve karar mekanizmasını yönlendirir...", fontSize = 11.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )

                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val cancelDInteraction = remember { MutableInteractionSource() }
                        val cDPressed by cancelDInteraction.collectIsPressedAsState()
                        val cDScale by animateFloatAsState(if (cDPressed) 0.94f else 1f, tween(80), label = "cds")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer { scaleX = cDScale; scaleY = cDScale }
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x20A855F7))
                                .border(1.dp, Color(0x50A855F7), RoundedCornerShape(12.dp))
                                .clickable(interactionSource = cancelDInteraction, indication = null) { showAddCustomDialog = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("İptal", color = Color(0xFFCDA8FF), fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

                        val addBtnInteraction = remember { MutableInteractionSource() }
                        val addBtnPressed by addBtnInteraction.collectIsPressedAsState()
                        val addBtnScale by animateFloatAsState(if (addBtnPressed) 0.94f else 1f, tween(80), label = "addBtn")
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .graphicsLayer { scaleX = addBtnScale; scaleY = addBtnScale }
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                    )
                                )
                                .border(1.dp, Color(0xFFA855F7).copy(0.6f), RoundedCornerShape(12.dp))
                                .clickable(interactionSource = addBtnInteraction, indication = null) {
                                    if (newCustomName.isNotBlank()) {
                                        val minVal = newCustomMin.toIntOrNull() ?: 0
                                        val maxVal = newCustomMax.toIntOrNull() ?: 100
                                        val curVal = (newCustomCurrent.toIntOrNull() ?: 0).coerceIn(minVal, maxVal)
                                        val newDef = com.example.data.local.CustomEmotionDefinition(
                                            name = newCustomName,
                                            difficulty = newCustomDifficulty,
                                            minValue = minVal,
                                            maxValue = maxVal,
                                            currentValue = curVal,
                                            purpose = newCustomPurpose
                                        )
                                        val updatedList = curState.customEmotions + newDef
                                        characterStatesMap[selectedTab] = curState.copy(
                                            customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updatedList)
                                        )
                                        showAddCustomDialog = false
                                        newCustomName = ""
                                        newCustomPurpose = ""
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(5.dp))
                                Text("Duygu Ekle", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
