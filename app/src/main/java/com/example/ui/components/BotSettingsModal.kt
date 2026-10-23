@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
package com.example.ui.components

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
fun BotSettingsModal(
    bot: BotEntity,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (BotEntity, List<KeyCharacter>) -> Unit,
    onResetChat: (Boolean) -> Unit,
    onDeleteBot: () -> Unit,
    onCloneBot: (() -> Unit)? = null
) {
    var aiName by remember { mutableStateOf(bot.aiName) }
    var universeName by remember { mutableStateOf(bot.universeName) }
    var personality by remember { mutableStateOf(bot.aiPersonality) }
    var scenario by remember { mutableStateOf(bot.scenario) }
    var userCharName by remember { mutableStateOf(bot.userCharName) }
    var userCharDesc by remember { mutableStateOf(bot.userCharDesc) }
    var writingStyle by remember { mutableStateOf(bot.writingStyle) }
    var intensity by remember { mutableStateOf(bot.intensity) }
    var customLength by remember { mutableStateOf(bot.customLength) }
    var isNsfw by remember { mutableStateOf(bot.isNsfw) }
    var enableOoc by remember { mutableStateOf(bot.enableOoc) }
    var avatarUrl by remember { mutableStateOf(bot.avatarUrl) }
    var chatBgUrl by remember { mutableStateOf(bot.chatBgUrl) }
    var isPublic by remember { mutableStateOf(bot.isPublic) }
    var pinnedMemory by remember { mutableStateOf(bot.pinnedMemory) }
    var storyNotes by remember { mutableStateOf(bot.storyNotes) }
    var memoryNotes by remember { mutableStateOf(bot.memoryNotes) }

    var charList by remember { mutableStateOf(keyCharacters.toMutableList()) }

    var confirmResetChat by remember { mutableStateOf(false) }
    var confirmDeleteBot by remember { mutableStateOf(false) }
    var showNeuralVault by remember { mutableStateOf(false) }
    var showEmotionControlModal by remember { mutableStateOf(false) }
    var showSystemPromptSheet by remember { mutableStateOf(false) }
    var expandedBotSection by remember { mutableStateOf<String?>(null) }

    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val lastSystemPrompt by viewModel.lastSystemPrompt.collectAsState()
    var filterCount by remember { mutableStateOf(0) }

    LaunchedEffect(bot.id) {
        filterCount = viewModel.getContentFilterCountForBot(bot.id)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { inputUri ->
            coroutineScope.launch {
                val localPath = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                val base64Uri = com.example.util.ImageStorageManager.toBase64DataUri(context, localPath)
                avatarUrl = base64Uri ?: localPath
            }
        }
    }

    val bgPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { inputUri ->
            coroutineScope.launch {
                // Chat background stays as file path (large image, not exported)
                val savedUrl = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                chatBgUrl = savedUrl
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xF2160932), Color(0xF50B051D))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.3.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFE9D5FF), Color(0xFF9333EA), Color(0xFF60A5FA), Color(0xFFC084FC))
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            AppBackground()
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Premium Aetheria Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(
                                    1.2.dp,
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFF60A5FA))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_aetheria_orb),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_title_sparkle),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp).padding(end = 4.dp)
                                )
                                Text(
                                    text = "Bot Profil & Özel Ayarlar",
                                    color = Color.White,
                                    fontSize = 16.5.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text("Duygu, bellek ve karakter ayarları", color = Color(0xFFC4B5FD), fontSize = 11.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Brush.linearGradient(listOf(Color(0xD91E0E3A), Color(0xD9120826))))
                            .border(
                                1.1.dp,
                                Brush.linearGradient(listOf(Color(0xFFE9D5FF), Color(0xFF9333EA), Color(0xFF6D28D9))),
                                RoundedCornerShape(11.dp)
                            )
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFF3E8FF), modifier = Modifier.size(17.dp))
                    }
                }
                // Ornamental Aetheria diamond divider
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_header_divider),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().height(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Emotion Bar
                EmotionStatusSection(
                    bot = bot,
                    characterEmotions = characterEmotions,
                    affectionEvents = affectionEvents,
                    filterCount = filterCount,
                    onUpdateCharacterEmotion = { charName, mood, affection, trust, tension ->
                        viewModel.updateCharacterEmotion(bot.id, charName, mood, affection, trust, tension)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // DEDICATED EMOTION & RELATIONSHIP EDITING BUTTON — premium gradient card
                val emotionBtnInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val emotionBtnPressed by emotionBtnInteraction.collectIsPressedAsState()
                val emotionBtnScale by animateFloatAsState(
                    targetValue = if (emotionBtnPressed) 0.97f else 1f,
                    animationSpec = tween(90), label = "emotionBtnScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = emotionBtnScale; scaleY = emotionBtnScale }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color(0xFF200B40), Color(0xFF150828))
                            )
                        )
                        .border(
                            androidx.compose.foundation.BorderStroke(
                                1.3.dp,
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFFE879A0), Color(0xFFBE185D), Color(0xFFDB2777))
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(interactionSource = emotionBtnInteraction, indication = null) { showEmotionControlModal = true }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            listOf(Color(0x60BE185D), Color(0x30831843))
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFFF472B6), Color(0xFFBE185D))
                                        ),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎭", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Duygu & İlişki Düzenleme",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Ruh hali, sevgi, güven, gerginlik ve yan karakterler",
                                    color = Color(0xFFE9A8C8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0xFFBE185D), Color(0xFF831843))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Aç", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                if (showEmotionControlModal) {
                    CharacterEmotionControlModal(
                        bot = bot,
                        characterEmotions = characterEmotions,
                        keyCharacters = keyCharacters,
                        onDismiss = { showEmotionControlModal = false },
                        onSaveBotEmotion = { updatedStateJson ->
                            val updatedBot = bot.copy(
                                emotionState = updatedStateJson,
                                previousEmotionState = updatedStateJson,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.updateBotProfile(updatedBot)
                            showEmotionControlModal = false
                        },
                        onSaveCharacterEmotionState = { charName, json ->
                            viewModel.updateCharacterEmotionState(bot.id, charName, json)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TOP ITEM 1: AVATAR & CHAT BACKGROUND (Directly Visible)
                Text("🖼️ Profil & Arka Plan Görselleri", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                // Avatar Photo Picker UI
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(29.dp))
                                .background(AppColors.CardDarker),
                            contentAlignment = Alignment.Center
                        ) {
                            BotAvatarImage(
                                avatarUrl = avatarUrl.takeIf { it.isNotBlank() },
                                contentDescription = "Bot Avatar",
                                modifier = Modifier.fillMaxSize(),
                                fallback = {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(24.dp))
                                }
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bot Görseli / Avatar", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Galeriden kapak resmi yükle", color = EmochiTextMuted, fontSize = 11.sp)

                            Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Galeri", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (avatarUrl.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(8.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF100000))))
                                            .border(0.8.dp, EmochiError.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                                            .clickable { avatarUrl = "" }
                                            .padding(horizontal=10.dp),
                                        contentAlignment = Alignment.Center
                                        ) { Text("Kaldır", fontSize = 11.sp, color = EmochiError, fontWeight = FontWeight.SemiBold) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Background Wallpaper Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.CardDarker),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chatBgUrl.isNotBlank()) {
                                AsyncImage(
                                    model = chatBgUrl,
                                    contentDescription = "Sohbet Arka Planı",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(24.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sohbet Arka Plan Resmi", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Sohbet içi özel duvar kağıdı belirleyin", color = EmochiTextMuted, fontSize = 11.sp)

                            Row(
                                modifier = Modifier.padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { chatBgUrl = "USE_AVATAR" },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardPurpleDark, contentColor = AppColors.PrimaryViolet),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("👤 Avatar Yap", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { bgPhotoPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Galeri", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                if (chatBgUrl.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(8.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF100000))))
                                            .border(0.8.dp, EmochiError.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                                            .clickable { chatBgUrl = "" }
                                            .padding(horizontal=8.dp),
                                        contentAlignment = Alignment.Center
                                        ) { Text("Sıfırla", fontSize = 10.5.sp, color = EmochiError, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TOP ITEM 2: PER-BOT RESPONSE LENGTH (Directly Visible)
                GradientCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💬 Bu Bota Özel Yanıt Uzunluğu", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        Text("Boş veya varsayılan bırakılırsa Genel Ayarlar'daki yanıt uzunluğu kullanılır.", color = EmochiTextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "default" to "Varsayılan",
                                "short" to "Kısa",
                                "orta" to "Orta",
                                "standard" to "Standart",
                                "long" to "Uzun"
                            ).forEach { (key, label) ->
                                val isSelected = customLength == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.2f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable { customLength = key }
                                        .padding(horizontal = 2.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) EmochiPrimary else EmochiTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = EmochiBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // COLLAPSIBLE OPTION 1: IDENTITY, SCENARIO & CHARACTERS
                CollapsibleSettingsOption(
                    title = "🎭 Hikaye & Senaryo (Kimlik ve Kişilik)",
                    subtitle = "Karakter adı, kişiliği, senaryo bağı ve kullanıcı karakter tanımı",
                    icon = Icons.Default.Person,
                    isExpanded = expandedBotSection == "scenario",
                    onToggle = { expandedBotSection = if (expandedBotSection == "scenario") null else "scenario" }
                ) {
                    if (bot.mode == "personal") {
                        Text("Karakter Adı", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = aiName,
                            onValueChange = { aiName = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            colors = customTextFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Kişilik Detayları", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = personality,
                            onValueChange = { personality = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 2.dp),
                            colors = customTextFieldColors()
                        )
                    } else {
                        Text("Evren / Dünya Adı", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = universeName,
                            onValueChange = { universeName = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            colors = customTextFieldColors(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Senaryo & Bağlam", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = scenario,
                        onValueChange = { scenario = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 2.dp),
                        colors = customTextFieldColors()
                    )

                    if (bot.mode == "universe") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Karakter Kadrosu", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        charList.forEachIndexed { index, char ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = char.name,
                                    onValueChange = { newName ->
                                        charList = charList.toMutableList().apply {
                                            this[index] = this[index].copy(name = newName)
                                        }
                                    },
                                    placeholder = { Text("İsim", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.weight(1f),
                                    colors = customTextFieldColors(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = char.desc,
                                    onValueChange = { newDesc ->
                                        charList = charList.toMutableList().apply {
                                            this[index] = this[index].copy(desc = newDesc)
                                        }
                                    },
                                    placeholder = { Text("Tanım", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.weight(2f),
                                    colors = customTextFieldColors(),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    charList = charList.toMutableList().apply { removeAt(index) }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Sil", tint = EmochiError, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        TextButton(
                            onClick = {
                                charList = charList.toMutableList().apply { add(KeyCharacter(name = "", desc = "")) }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Karakter Ekle", color = EmochiPrimary, fontSize = 12.sp)
                        }
                    }

                    // 3.3: Otomatik tespit edilen karakterler — silme + "bir daha ekleme?" seçeneği
                    val autoDetectedCast = remember { mutableStateOf<List<com.example.data.local.CastMemberEntity>>(emptyList()) }
                    var deleteConfirmCast by remember { mutableStateOf<com.example.data.local.CastMemberEntity?>(null) }
                    LaunchedEffect(bot.id) {
                        autoDetectedCast.value = viewModel.getCastMembersForBot(bot.id)
                            .filter { it.isAutoAdded && !it.isBlacklisted }
                    }
                    if (autoDetectedCast.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Otomatik Tespit Edilen Karakterler", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        autoDetectedCast.value.forEach { cm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cm.name, color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(cm.role + if (cm.detectionConfidence > 0) " — %${cm.detectionConfidence} güven" else "",
                                        color = EmochiTextMuted, fontSize = 11.sp)
                                }
                                IconButton(onClick = { deleteConfirmCast = cm }) {
                                    Icon(Icons.Default.Close, contentDescription = "Sil", tint = EmochiError, modifier = Modifier.size(17.dp))
                                }
                            }
                        }
                    }
                    // 3.3: "Bir daha ekleme?" onay diyaloğu
                    deleteConfirmCast?.let { cm ->
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { deleteConfirmCast = null },
                            containerColor = Color(0xFF150930),
                            title = { Text("\"${cm.name}\" silinsin mi?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                            text = { Text("Bu ismi bir daha otomatik olarak eklemeyeyim mi?", color = EmochiTextSecondary, fontSize = 13.sp) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteAutoAddedCastMember(bot.id, cm.id, cm.name, blacklist = true)
                                        autoDetectedCast.value = autoDetectedCast.value.filterNot { it.id == cm.id }
                                        deleteConfirmCast = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiError)
                                ) { Text("Evet, bir daha ekleme", color = Color.White, fontSize = 12.sp) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    viewModel.deleteAutoAddedCastMember(bot.id, cm.id, cm.name, blacklist = false)
                                    autoDetectedCast.value = autoDetectedCast.value.filterNot { it.id == cm.id }
                                    deleteConfirmCast = null
                                }) { Text("Hayır, sadece bu seferlik sil", color = EmochiTextSecondary, fontSize = 12.sp) }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Senin Karakterin (Kullanıcı Adı)", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = userCharName,
                        onValueChange = { userCharName = it },
                        placeholder = { Text("İsim", fontSize = 12.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        colors = customTextFieldColors(),
                        singleLine = true
                    )
                }

                // COLLAPSIBLE OPTION 2: MEMORY & NEURAL VAULT
                CollapsibleSettingsOption(
                    title = "🧠 Hafıza & Neural Vault",
                    subtitle = "Neural Vault bellek kasası, kalıcı hafıza ve hikaye durumları",
                    icon = Icons.Default.Psychology,
                    isExpanded = expandedBotSection == "memory",
                    onToggle = { expandedBotSection = if (expandedBotSection == "memory") null else "memory" }
                ) {
                    // Neural Vault Button
                    Button(
                        onClick = { showNeuralVault = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Hex_141424, contentColor = AppColors.SkyBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.SkyBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🧠 Neural Vault (Gelişmiş AI Belleği)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    if (showNeuralVault) {
                        NeuralVaultModal(
                            bot = bot.copy(
                                pinnedMemory = pinnedMemory,
                                storyNotes = storyNotes,
                                memoryNotes = memoryNotes
                            ),
                            viewModel = viewModel,
                            onDismiss = { showNeuralVault = false },
                            onSaveMemory = { updatedBot ->
                                pinnedMemory = updatedBot.pinnedMemory
                                storyNotes = updatedBot.storyNotes
                                memoryNotes = updatedBot.memoryNotes
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Kalıcı Hafıza (Asla Silinmez)", color = EmochiPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "AI'nın her zaman bilmesini istediğiniz kuralları, ilişkileri veya gerçekleri buraya yazın.",
                        color = EmochiTextSecondary,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = pinnedMemory,
                        onValueChange = { pinnedMemory = it },
                        modifier = Modifier.fillMaxWidth().height(90.dp).padding(top = 4.dp),
                        colors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Otomatik Hikaye Durumu", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmochiCard)
                            .border(1.dp, EmochiBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = storyNotes.ifBlank { "Henüz kayıtlı hikaye durumu yok." },
                            color = EmochiTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Otomatik Hafıza Özetleri", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmochiCard)
                            .border(1.dp, EmochiBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = memoryNotes.ifBlank { "Henüz kayıtlı hafıza özeti yok." },
                            color = EmochiTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // COLLAPSIBLE OPTION 3: OOC & FILTERS
                CollapsibleSettingsOption(
                    title = "💬 OOC & Filtresiz RP Ayarları",
                    subtitle = "Parantez içi yönlendirme (OOC) ve +18 filtresiz RP izinleri",
                    icon = Icons.Default.Settings,
                    isExpanded = expandedBotSection == "ooc",
                    onToggle = { expandedBotSection = if (expandedBotSection == "ooc") null else "ooc" },
                    badgeText = if (isNsfw) "NSFW" else null,
                    badgeColor = AppColors.ErrorRed
                ) {
                    // OOC Switch Card
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "💬 Parantez İçi Yönlendirme (OOC)",
                                    color = EmochiTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sohbet esnasında parantez içindeki ( ... ) ifadeler AI'a hikaye dışı meta yönlendirme komutu olarak iletilir.",
                                    color = EmochiTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = enableOoc,
                                onCheckedChange = { enableOoc = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.DarkSurface,
                                    checkedTrackColor = EmochiPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Per-Bot +18 NSFW Switch Card
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔞 +18 / Filtresiz RP İzni",
                                    color = EmochiTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Bu karaktere özel sansürsüz içerik izni tanımlar. Boş bırakılırsa genel ayar geçerlidir.",
                                    color = EmochiTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isNsfw,
                                onCheckedChange = { isNsfw = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.DarkSurface,
                                    checkedTrackColor = EmochiPrimary
                                )
                            )
                        }
                    }
                }

                // COLLAPSIBLE OPTION 4: SHARING & VISIBILITY
                if (!bot.isTemplate && !bot.id.startsWith("starter_") && !bot.id.startsWith("preset_")) {
                    CollapsibleSettingsOption(
                        title = "🌐 Paylaşım & Görünürlük",
                        subtitle = "Botun keşfete çıkarılması veya gizli tutulması",
                        icon = Icons.Default.Public,
                        isExpanded = expandedBotSection == "visibility",
                        onToggle = { expandedBotSection = if (expandedBotSection == "visibility") null else "visibility" },
                        badgeText = if (isPublic) "AÇIK" else "GİZLİ"
                    ) {
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                            borderWidth = 1.2.dp
                            ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isPublic) "🌐 Herkese Açık Bot" else "🔒 Sadece Kendine Özel",
                                        color = EmochiTextPrimary,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isPublic) "Diğer kullanıcılar 'Keşfet' bölümünde botunuzu bulabilir." else "Bu bot gizlidir, sadece siz görebilirsiniz.",
                                        color = EmochiTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = isPublic,
                                    onCheckedChange = { isPublic = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AppColors.DarkSurface,
                                        checkedTrackColor = EmochiPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // SAVE BUTTON
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val updated = bot.copy(
                            aiName = aiName,
                            universeName = universeName,
                            aiPersonality = personality,
                            scenario = scenario,
                            userCharName = userCharName,
                            userCharDesc = userCharDesc,
                            writingStyle = writingStyle,
                            intensity = intensity,
                            customLength = customLength,
                            isNsfw = isNsfw,
                            enableOoc = enableOoc,
                            avatarUrl = avatarUrl,
                            chatBgUrl = chatBgUrl,
                            isPublic = isPublic,
                            pinnedMemory = pinnedMemory,
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(updated, charList)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Değişiklikleri Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = EmochiBorder)

                // SECTION 6: DANGEROUS ACTIONS
                SettingsSectionHeader(
                    title = "⚠️ Tehlikeli İşlemler",
                    subtitle = "Sohbet geçmişini sıfırlama veya botu tamamen silme.",
                    icon = Icons.Default.Warning,
                    color = EmochiError
                )

                if (!confirmResetChat) {
                    Button(
                        onClick = { confirmResetChat = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard, contentColor = EmochiTextPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmochiPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sohbet Geçmişini Sıfırla")
                    }
                } else {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sohbeti Sıfırlama Yöntemi Seçin:", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    confirmResetChat = false
                                    onResetChat(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("💬 Sadece Mesajları Sil (Hafıza Korunur)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    confirmResetChat = false
                                    onResetChat(true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiError, contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔥 Tamamen Sıfırla (Hafıza & Duygu Dâhil)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.align(Alignment.End).clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                    .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                                    .clickable { confirmResetChat = false }
                                    .padding(horizontal=12.dp, vertical=6.dp),
                                contentAlignment = Alignment.Center
                                ) { Text("Vazgeç", color = EmochiTextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }

                // 📤 Sohbet Export Butonu
                val context = LocalContext.current
                val activeMessages by viewModel.activeMessages.collectAsState()
                var showExportDialog by remember { mutableStateOf(false) }

                if (showExportDialog) {
                    var selectedFormat by remember { mutableStateOf(com.example.util.ChatExporter.ExportFormat.TEXT) }
                    var selectedRange by remember { mutableStateOf(com.example.util.ChatExporter.DateRange.ALL) }
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showExportDialog = false },
                        containerColor = Color(0xFF0F0826),
                        title = { Text("📤 Sohbeti Dışa Aktar", color = Color(0xFFCDA8FF), fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Format", color = Color(0x80CDA8FF), fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(com.example.util.ChatExporter.ExportFormat.TEXT to "📝 Metin",
                                           com.example.util.ChatExporter.ExportFormat.JSON to "🔧 JSON").forEach { (fmt, label) ->
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                                .background(if (selectedFormat == fmt) Color(0xFF3B1070) else Color(0xFF1A1030))
                                                .border(1.dp, if (selectedFormat == fmt) Color(0xFF7C3AED) else Color(0xFF2D1F50), RoundedCornerShape(20.dp))
                                                .clickable { selectedFormat = fmt }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) { Text(label, color = Color.White, fontSize = 12.sp) }
                                    }
                                }
                                Text("Tarih Aralığı", color = Color(0x80CDA8FF), fontSize = 12.sp)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(com.example.util.ChatExporter.DateRange.ALL to "Tümü",
                                           com.example.util.ChatExporter.DateRange.LAST_7_DAYS to "Son 7 Gün",
                                           com.example.util.ChatExporter.DateRange.LAST_30_DAYS to "Son 30 Gün").forEach { (range, label) ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (selectedRange == range) Color(0xFF1E0D40) else Color.Transparent)
                                                .border(1.dp, if (selectedRange == range) Color(0xFF7C3AED) else Color(0xFF2D1F50), RoundedCornerShape(10.dp))
                                                .clickable { selectedRange = range }
                                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                        ) { Text(label, color = Color.White, fontSize = 13.sp) }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showExportDialog = false
                                    com.example.util.ChatExporter.export(context, bot, activeMessages, selectedFormat, selectedRange)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) { Text("Paylaş", color = Color.White, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { showExportDialog = false }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1820), contentColor = Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF0369A1).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📤", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sohbeti Dışa Aktar", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                }

                // 🔬 Sistem Prompt Debug Butonu
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showSystemPromptSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D0B1E), contentColor = Color(0xFF9D74E8)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF4C1D95).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔬", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sistem Promptunu Görüntüle", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                }

                if (onCloneBot != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onCloneBot()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard, contentColor = EmochiPrimary),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EmochiPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📋", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Botu Kopyala (Yeni Bot Olarak Çoğalt)", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!confirmDeleteBot) {
                    Button(
                        onClick = { confirmDeleteBot = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = EmochiError),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EmochiError.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Botu Tamamen Sil")
                    }
                } else {
                    DangerGradientCard(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Bu bot ve tüm geçmişi kalıcı olarak silinecektir!", color = EmochiError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                                Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { confirmDeleteBot = false }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("Vazgeç", color = EmochiTextSecondary, fontWeight = FontWeight.Medium) }
                                Button(
                                    onClick = {
                                        confirmDeleteBot = false
                                        onDeleteBot()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiError)
                                ) {
                                    Text("Sil", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔬 Sistem Prompt BottomSheet
    if (showSystemPromptSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSystemPromptSheet = false },
            containerColor = Color(0xFF0A0818),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0A0818))))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text("🔬 Sistem Promptu", color = Color(0xFFCDA8FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (lastSystemPrompt != null) "Son mesajdan yakalandı · ${(lastSystemPrompt!!.length / 4)} token (tahmini)"
                            else "Henüz mesaj gönderilmedi",
                            color = Color(0x80CDA8FF), fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x30F87171)).align(Alignment.CenterEnd)
                            .clickable { showSystemPromptSheet = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                    }
                }

                if (lastSystemPrompt == null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔬", fontSize = 32.sp)
                            Text("Görmek için önce bir mesaj gönder", color = Color(0x80CDA8FF), fontSize = 13.sp)
                        }
                    }
                } else {
                    val sections = remember(lastSystemPrompt) {
                        val prompt = lastSystemPrompt!!
                        listOf(
                            Triple("Karakter Tanımı", Color(0xFF7C3AED), prompt.substringBefore("\n\n---\n").take(600)),
                            Triple("Duygu & Bellek", Color(0xFF0891B2), prompt.substringAfter("---").substringBefore("\n\n---\n", "").take(600)),
                            Triple("Tüm Prompt", Color(0xFF374151), prompt)
                        )
                    }
                    var selectedSection by remember { mutableStateOf(0) }

                    // Section tabs
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sections.forEachIndexed { i, (label, color, _) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedSection == i) color.copy(alpha = 0.3f) else Color(0xFF1A1030))
                                    .border(1.dp, if (selectedSection == i) color else Color(0xFF2D1F50), RoundedCornerShape(20.dp))
                                    .clickable { selectedSection = i }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, color = if (selectedSection == i) color else Color(0x80CDA8FF), fontSize = 11.sp, fontWeight = if (selectedSection == i) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    val (_, sectionColor, sectionText) = sections[selectedSection]
                    val clipboardManager = LocalClipboardManager.current
                    val context = LocalContext.current

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.End) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(sectionColor.copy(0.2f)).border(1.dp, sectionColor.copy(0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(sectionText))
                                    android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                }.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) { Text("📋 Kopyala", color = sectionColor, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0D0B1E))
                                    .border(1.dp, sectionColor.copy(0.3f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text(sectionText, color = Color(0xFFD8C8FF), fontSize = 11.5.sp, lineHeight = 18.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
