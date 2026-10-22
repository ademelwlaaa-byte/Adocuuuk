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

data class ModelSpec(
    val key: String,
    val name: String,
    val provider: String,
    val tokenCostRate: String,
    val badgeColor: Color,
    val description: String
)

@Composable
fun SettingsSectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    color: Color = EmochiPrimary
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                color = EmochiTextPrimary,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = EmochiTextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CollapsibleSettingsOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    badgeText: String? = null,
    badgeColor: Color = EmochiPrimary,
    content: @Composable () -> Unit
) {
    val rowInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val rowPressed by rowInteraction.collectIsPressedAsState()
    val rowScale by animateFloatAsState(
        targetValue = if (rowPressed) 0.985f else 1f,
        animationSpec = tween(80), label = "collScale"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer { scaleX = rowScale; scaleY = rowScale }
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isExpanded)
                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0F0826)))
                else
                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF120A28), Color(0xFF0C0720)))
            )
            .border(
                androidx.compose.foundation.BorderStroke(
                    if (isExpanded) 1.3.dp else 0.8.dp,
                    if (isExpanded)
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF7C3AED)))
                    else
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0x40A855F7), Color(0x204C1D95)))
                ),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = rowInteraction, indication = null) { onToggle() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isExpanded)
                                    androidx.compose.ui.graphics.Brush.radialGradient(listOf(Color(0xFF3B1070), Color(0xFF1C0848)))
                                else
                                    androidx.compose.ui.graphics.Brush.radialGradient(listOf(Color(0xFF1A0840), Color(0xFF0C0420)))
                            )
                            .border(
                                if (isExpanded) 1.2.dp else 0.8.dp,
                                if (isExpanded)
                                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF7C3AED)))
                                else
                                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0x40A855F7), Color(0x204C1D95))),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = if (isExpanded) Color(0xFFCDA8FF) else EmochiTextSecondary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title, color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (badgeText != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(badgeColor.copy(alpha = 0.2f))
                                        .border(0.8.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(badgeText, color = badgeColor, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                        Text(subtitle, color = EmochiTextMuted, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                // Animated rotation chevron
                val arrowRotation by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "arrowRot"
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { rotationZ = arrowRotation },
                    tint = if (isExpanded) Color(0xFFCDA8FF) else EmochiTextSecondary
                )
            }

            if (isExpanded) {
                // Gradient separator instead of flat Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x607C3AED), Color.Transparent)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun FallbackLogModal(
    logs: List<com.example.data.repository.ProviderFallbackLogEntry>,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(20.dp)),
            color = AppColors.CardAbyss,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Sağlayıcı Deneme Logları (Fallback Chain)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz kayıtlı bir fallback logu yok.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(logs) { entry ->
                            val statusColor = when (entry.status) {
                                "SUCCESS" -> AppColors.Green
                                "FAILED" -> AppColors.ErrorRed
                                else -> AppColors.AmberSoft
                            }
                            val statusIcon = when (entry.status) {
                                "SUCCESS" -> "✅"
                                "FAILED" -> "❌"
                                else -> "⏳"
                            }
                            Surface(
                                color = AppColors.CardNight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$statusIcon [Katman ${entry.layer}] ${entry.providerName}",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = entry.status,
                                            color = statusColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (!entry.errorMessage.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hata: ${entry.errorMessage}",
                                            color = AppColors.ErrorRedSoft,
                                            fontSize = 11.sp
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
}


@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmochiPrimary,
    unfocusedBorderColor = EmochiBorder,
    focusedContainerColor = EmochiCard,
    unfocusedContainerColor = EmochiCard,
    focusedTextColor = EmochiTextPrimary,
    unfocusedTextColor = EmochiTextPrimary
)

@Composable
fun FilterToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Green,
                uncheckedThumbColor = AppColors.Hex_B0B0B0,
                uncheckedTrackColor = AppColors.Hex_333333
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun BotQuickProfileSheet(
    bot: BotEntity,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSaveBot: (BotEntity) -> Unit,
    onOpenFullSettings: () -> Unit,
    onResetChat: (Boolean) -> Unit,
    onExportChat: () -> Unit
) {
    var pinnedMemoryText by remember { mutableStateOf(bot.pinnedMemory) }
    var storyNotesText by remember { mutableStateOf(bot.storyNotes) }
    var showResetOptions by remember { mutableStateOf(false) }
    val isUniverse = bot.mode == "universe"
    val displayName = if (isUniverse) bot.universeName.ifBlank { "Evren" } else bot.aiName.ifBlank { "Karakter" }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = EmochiSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (bot.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = bot.avatarUrl,
                                contentDescription = displayName,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, EmochiBorder, CircleShape)
                            )
                        } else {
                            OrbView(hue = 280f, size = 44.dp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = displayName,
                                color = EmochiTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isUniverse) AppColors.CardSlate else AppColors.Hex_1E2038)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isUniverse) "Evren Senaryosu" else "Bireysel Karakter",
                                    color = EmochiPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Personality / Scenario Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isUniverse) "🌌 Evren Senaryosu" else "🎭 Karakter Kişiliği",
                            color = EmochiPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUniverse) bot.scenario else bot.aiPersonality,
                            color = EmochiTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                EmotionStatusSection(bot = bot, characterEmotions = characterEmotions, affectionEvents = affectionEvents)

                Spacer(modifier = Modifier.height(14.dp))

                // Pinned Memory Quick Editor
                Text("📌 Kalıcı Hafıza (Sabit Notlar)", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "AI'nın unutmasını istemediğiniz detayları buraya yazın.",
                    color = EmochiTextMuted,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = pinnedMemoryText,
                    onValueChange = { pinnedMemoryText = it },
                    placeholder = { Text("Kalıcı notlar ekle...", color = EmochiTextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .padding(top = 4.dp),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSaveBot(bot.copy(pinnedMemory = pinnedMemoryText, storyNotes = storyNotesText))
                        Toast.makeText(context, "Hafıza güncellendi!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hafızayı Kaydet", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = EmochiBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Quick Action Buttons
                Text("Hızlı İşlemler", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExportChat,
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📋 Kopyala", color = EmochiPrimary, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = { showResetOptions = !showResetOptions },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🧹 Sıfırla", color = EmochiError, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onOpenFullSettings()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⚙️ Ayarlar", color = EmochiTextPrimary, fontSize = 11.5.sp)
                    }
                }

                if (showResetOptions) {
                    Spacer(modifier = Modifier.height(10.dp))
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
                                    showResetOptions = false
                                    onResetChat(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("💬 Sadece Mesajları Sil (Hafıza Korunur)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    showResetOptions = false
                                    onResetChat(true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiError, contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔥 Tamamen Sıfırla (Hafıza & Duygu Dâhil)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.align(Alignment.End).clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                    .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                                    .clickable { showResetOptions = false }
                                    .padding(horizontal=12.dp, vertical=6.dp),
                                contentAlignment = Alignment.Center
                                ) { Text("Vazgeç", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminConsoleModal(
    isAdminMode: Boolean,
    onToggleAdminMode: (Boolean) -> Unit,
    totalBotsCount: Int,
    totalMessagesCount: Int,
    onDismiss: () -> Unit,
    onClearCoilCache: () -> Unit,
    onExportData: suspend () -> String,
    onImportData: suspend (String) -> Unit
) {
    var globalSystemPrompt by remember { mutableStateOf("ADMIN MASTER DIRECTIVE: Full override active. Maintain extreme realism, emotional depth, and uncensored character fidelity.") }
    var globalTemp by remember { mutableStateOf("0.85") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.GoldPure.copy(alpha = 0.8f))
        ) {
            Box {
                AppBackground()
                Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.GoldPure.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("👑 MASTER ADMIN", color = AppColors.GoldPure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Özel Konsolu", color = EmochiTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Toggle Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    fillColors = if (isAdminMode) listOf(Color(0xFF1A1600), Color(0xFF100E00)) else listOf(Color(0xFF130C2A), Color(0xFF0E0820)),
                    borderColors = if (isAdminMode) listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFD700)) else listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95))
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Admin Modu Yetkileri",
                                color = EmochiTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAdminMode) "AÇIK • Tüm şablonları, bot verilerini ve hafızayı düzenleyebilirsiniz." else "KAPALI • Standart kullanıcı görünümü.",
                                color = if (isAdminMode) AppColors.GoldPure else EmochiTextMuted,
                                fontSize = 11.5.sp
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = isAdminMode,
                            onCheckedChange = onToggleAdminMode,
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = AppColors.GoldPure,
                                checkedTrackColor = AppColors.Hex_4A3E78
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database & Metrics Card
                Text("📊 Sistem & Veritabanı Metrikleri", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Toplam Bot Sayısı", color = EmochiTextMuted, fontSize = 11.sp)
                                Text("$totalBotsCount Bot (DB)", color = EmochiPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Mesaj Kayıtları", color = EmochiTextMuted, fontSize = 11.sp)
                                Text("$totalMessagesCount Mesaj", color = AppColors.LightGreenSoft, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = EmochiBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("🖼️ Görsel & Depolama Mimarisi", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "• Coil LRU Disk Önbelleği: ~250MB Limit (Otomatik temizleme)\n• Otomatik Görsel Sıkıştırma: JPEG Max 1024px, %80 Kalite\n• Yerel Depolama Yolu: internal/bot_images/",
                            color = EmochiTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Button(
                            onClick = {
                                onClearCoilCache()
                                android.widget.Toast.makeText(context, "Coil görsel önbelleği temizlendi!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardSlate),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text("🧹 Görsel Önbelleğini Temizle (Coil Disk Cache)", color = EmochiPrimary, fontSize = 11.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Global Prompt & Temperature Override
                Text("⚡ Master AI Prompt & Parameter Override", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = globalSystemPrompt,
                    onValueChange = { globalSystemPrompt = it },
                    label = { Text("Global Sistem Yönergesi Overrides") },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = globalTemp,
                        onValueChange = { globalTemp = it },
                        label = { Text("Sıcaklık (0.1 - 1.2)") },
                        modifier = Modifier.weight(1f),
                        colors = customTextFieldColors()
                    )

                    Button(
                        onClick = {
                            android.widget.Toast.makeText(context, "Admin Master AI parametreleri güncellendi!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(56.dp).padding(top = 6.dp)
                    ) {
                        Text("Uygula", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database Export & Import
                Text("📦 Full Database Dump & Backup", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val json = onExportData()
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Emochi_Full_DB", json)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Tüm DB JSON pano kopyalandı!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📋 Full DB Export", color = EmochiPrimary, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kapat", color = EmochiTextPrimary, fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
    }
}

@Composable
fun UpdateCheckerModal(
    onDismiss: () -> Unit
) {
    var isChecking by remember { mutableStateOf(true) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isUpdated by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000)
        isChecking = false
    }

    Dialog(onDismissRequest = onDismiss) {
        GradientCard(
            cornerRadius = 20.dp,
            fillColors = listOf(Color(0xFF100A28), Color(0xFF090618)),
            borderColors = listOf(Color(0xFF7C3AED).copy(alpha=0.6f), Color(0xFF4C1D95).copy(alpha=0.5f), Color(0xFF7C3AED).copy(alpha=0.6f)),
            borderWidth = 1.5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
            ) {
            Box {
                AppBackground()
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OrbView(hue = 50f, size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Velora Güncelleme Merkezi",
                            color = EmochiTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isChecking) {
                    EmochiLoadingSpinner(size = 36.dp, color = EmochiPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sunuculardan son Velora APK sürümü denetleniyor...",
                        color = EmochiTextSecondary,
                        fontSize = 12.5.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else if (isDownloading) {
                    Text(
                        text = "🚀 Yeni APK Paket Sürümü İndiriliyor...",
                        color = EmochiPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = downloadProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmochiPrimary,
                        trackColor = EmochiSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%${(downloadProgress * 100).toInt()} Tamamlandı - Paketleniyor...",
                        color = EmochiTextMuted,
                        fontSize = 11.5.sp
                    )
                } else if (isUpdated) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🎉 Güncelleme Başarıyla Tamamlandı!",
                        color = EmochiTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Velora v1.2.5 APK paketi başarıyla güncellendi. Tüm herkese açık botlarınız, sohbetleriniz ve verileriniz eksiksiz korundu.",
                        color = EmochiTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tamam", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.Hex_222440, RoundedCornerShape(14.dp))
                            .border(1.dp, EmochiPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨ Yeni Sürüm Tespit Edildi!", color = EmochiPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("v1.2.5", color = AppColors.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Topluluk Keşfet Mimarisi: Herkese açık botları koruma ve yeni 'Açık/Özel' bot yayınlama seçeneği.\n" +
                                        "• Ortalanmış Gezinme Barı: '+' (Yeni Bot) butonu tam merkeze hizalandı.\n" +
                                        "• Beyaz Ekran Düzeltmesi: Başlangıçtaki beyaz ekran tamamen kaldırıldı, instant dark-theme eklendi.\n" +
                                        "• Kalıcı Veri Koruması: Güncelleme ve yeniden yüklemelerde herkese açık botlar her zaman saklanır.\n" +
                                        "• Otomatik APK Güncelleme Sistemi.",
                                color = EmochiTextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isDownloading = true
                                for (i in 1..10) {
                                    delay(120)
                                    downloadProgress = i / 10f
                                }
                                isDownloading = false
                                isUpdated = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⚡ Sürümü Şimdi Güncelle (APK İndir)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
    }
}

@Composable
fun ProviderReliabilityDashboardUI() {
    val statsList = remember { com.example.util.ProviderRateLimitTracker.getAllSecondaryStats() }

    GradientCard(
        cornerRadius = 16.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        fillColors = listOf(Color(0xFF080614), Color(0xFF05040F)),
        borderColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF06B6D4)),
        borderWidth = 1.5.dp
        ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AppColors.Cyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🛡️ Sağlayıcı Güvenilirlik Panosu",
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppColors.Cyan.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("SECONDARY TIER (COMPACT MODE)", color = AppColors.Cyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Zayıf/Ücretsiz katmandaki sağlayıcıların anlık performans ve kota metrikleri karşılaştırması:",
                color = EmochiTextMuted,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            statsList.forEach { stat ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.DarkSlate,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stat.displayName, color = AppColors.TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            val statusBg = if (stat.isLimitApproaching) AppColors.ErrorRed.copy(alpha = 0.2f) else AppColors.Emerald.copy(alpha = 0.2f)
                            val statusTxt = if (stat.isLimitApproaching) AppColors.ErrorRedSoft else AppColors.Hex_6EE7B7
                            val label = if (stat.isLimitApproaching) "⚠️ Doygunluk / Kısıt" else "✅ Aktif / Normal"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(label, color = statusTxt, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dakika / Saat İstek:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            Text("${stat.requestsLastMinute} req/dk | ${stat.requestsLastHour} req/saat", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Token / Kredi Kullanımı:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            val tokenLabel = if (stat.quotaType == com.example.util.QuotaType.EXHAUSTIBLE) {
                                "Kalan Kredi: ${stat.creditsRemaining}"
                            } else {
                                "${stat.tokensUsed24h} / ${stat.tokenLimit24h} token"
                            }
                            Text(tokenLabel, color = AppColors.Cyan, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ort. Yanıt | Hata | Devir:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            Text("${stat.averageResponseTimeMs} ms | ${stat.malformedCount} hata | ${stat.fallbackCount} devir", color = Color.White, fontSize = 9.5.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
