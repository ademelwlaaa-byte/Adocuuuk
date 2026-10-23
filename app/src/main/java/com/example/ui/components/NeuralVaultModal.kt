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
fun NeuralVaultModal(
    bot: BotEntity,
    viewModel: com.example.ui.viewmodel.EmochiViewModel? = null,
    onDismiss: () -> Unit,
    onSaveMemory: (BotEntity) -> Unit
) {
    var keyword by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bilgi (Core)") }
    var memoryText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var categoryFilter by remember { mutableStateOf("Tümü") }
    
    var pinnedMemoryText by remember { mutableStateOf(bot.pinnedMemory) }
    var memoryNotesText by remember { mutableStateOf(bot.memoryNotes) }
    var storyNotesText by remember { mutableStateOf(bot.storyNotes) }

    val activeFactsFlow = remember(bot.id) { viewModel?.getActiveFactsFlow(bot.id) }
    val activeFacts by (activeFactsFlow ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsStateWithLifecycle(initialValue = emptyList())

    val activeEventsFlow = remember(bot.id) { viewModel?.getActiveEventsFlow(bot.id) }
    val activeEvents by (activeEventsFlow ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsStateWithLifecycle(initialValue = emptyList())

    var editingFact by remember { mutableStateOf<com.example.data.local.MemoryFactEntity?>(null) }
    var editingEvent by remember { mutableStateOf<com.example.data.local.MemoryEventEntity?>(null) }
    
    val memoriesList = remember(pinnedMemoryText, memoryNotesText, storyNotesText) {
        val list = mutableListOf<Triple<String, String, String>>()

        if (pinnedMemoryText.isNotBlank()) {
            pinnedMemoryText.lines().filter { it.isNotBlank() }.forEach { line ->
                val parts = line.split(":::", limit = 3)
                if (parts.size >= 3) {
                    list.add(Triple(parts[0].trim(), parts[1].trim(), parts[2].trim()))
                } else if (parts.size == 2) {
                    list.add(Triple("BELLEK", "KALICI", parts[1].trim()))
                } else {
                    list.add(Triple("KALICI", "BİLGİ", line.trim()))
                }
            }
        }

        if (memoryNotesText.isNotBlank()) {
            memoryNotesText.lines().filter { it.isNotBlank() }.forEach { line ->
                val clean = line.removePrefix("-").removePrefix("*").trim()
                if (clean.isNotBlank()) {
                    list.add(Triple("ÖZET", "HAFIZA", clean))
                }
            }
        }

        if (storyNotesText.isNotBlank()) {
            storyNotesText.lines().filter { it.isNotBlank() }.forEach { line ->
                val clean = line.removePrefix("-").removePrefix("*").trim()
                if (clean.isNotBlank()) {
                    list.add(Triple("HİKAYE", "DURUM", clean))
                }
            }
        }

        list
    }
    
    val filteredMemories = memoriesList.filter { (key, cat, body) ->
        val matchesCategory = categoryFilter == "Tümü" || cat.contains(categoryFilter, ignoreCase = true) || (categoryFilter == "Bilgi (Core)" && (cat.contains("BİLGİ", ignoreCase = true) || cat.contains("CORE", ignoreCase = true)))
        val matchesSearch = searchQuery.isBlank() || key.contains(searchQuery, ignoreCase = true) ||
                cat.contains(searchQuery, ignoreCase = true) ||
                body.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val filteredFacts = activeFacts.filter { fact ->
        val matchesCategory = categoryFilter == "Tümü" || categoryFilter.contains("Bilgi", ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || fact.subject.contains(searchQuery, ignoreCase = true) ||
                fact.key.contains(searchQuery, ignoreCase = true) ||
                fact.value.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val filteredEvents = activeEvents.filter { event ->
        val matchesCategory = categoryFilter == "Tümü" || categoryFilter.contains("Biyografi", ignoreCase = true) || categoryFilter.contains("İlişki", ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || event.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Dialog(onDismissRequest = onDismiss) {
        GradientCard(
            cornerRadius = 24.dp,
            fillColors = listOf(Color(0xF215082E), Color(0xF50A041A)),
            borderColors = listOf(Color(0xFFE9D5FF), Color(0xFF9333EA), Color(0xFF60A5FA), Color(0xFFC084FC)),
            borderWidth = 1.3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
            ) {
            Box {
                AppBackground()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                // Header (Aetheria Neon & Gothic Glass)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_title_sparkle),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).padding(end = 6.dp)
                        )
                        Text(
                            text = "Neural Vault • Bellek Mahzeni",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(Color(0xD91E0E3A), Color(0xD9120826))))
                            .border(1.dp, Color(0x66C084FC), RoundedCornerShape(10.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFE9D5FF), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_header_divider),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().height(13.dp)
                )

                // Stats Pills Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xCC1B0B3A), RoundedCornerShape(50))
                            .border(0.8.dp, Color(0x66C084FC), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("${memoriesList.size + activeFacts.size + activeEvents.size} Kayıt", color = Color(0xFFE9D5FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xCC1B0B3A), RoundedCornerShape(50))
                            .border(0.8.dp, Color(0x6660A5FA), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("V4.0 Sync", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xCC1B0B3A), RoundedCornerShape(50))
                            .border(0.8.dp, Color(0x66A855F7), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Long-Term Memory", color = Color(0xFFD8B4FE), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Inject Memory Box
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFFD8B4FE), Color(0xFF7C3AED), Color(0xFF60A5FA)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AppColors.SkyBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YENİ BELLEK ENJEKTE ET",
                                color = AppColors.SkyBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = keyword,
                                onValueChange = { keyword = it },
                                placeholder = { Text("Anahtar Kelime", color = Color.Gray, fontSize = 11.5.sp) },
                                modifier = Modifier.weight(1f),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                            
                            // Category Selector Field (Opens Popup Matching Screenshot 1)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.CardElevated)
                                    .border(1.dp, AppColors.Hex_333344, RoundedCornerShape(8.dp))
                                    .clickable { showCategoryPicker = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Kategori Seç",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Hazır Seçenekler / Şablonlar
                        val quickPresets = listOf(
                            Triple("TAKMA İSİM", "Bilgi (Core)", "Kullanıcıya özel samimi bir unvan veya takma isimle hitap eder."),
                            Triple("GİZLİ DUYGU", "İlişki", "Kullanıcıya karşı derin bir çekim ve gizli bir bağlılık hisseder."),
                            Triple("GEÇMİŞ SIR", "Biyografi", "Geçmişte yaşadığı kırılma noktasını sadece kullanıcıya anlatmıştır."),
                            Triple("ÖZEL MEKAN", "Dünya/Mekan", "Yalnızca ikisinin bildiği gizli ve tenha bir buluşma noktaları vardır."),
                            Triple("YETİŞKİN RP", "İlişki", "Yetişkin etkileşimlerde filtresiz, tutkulu ve tabusuz bir yakınlık benimser."),
                            Triple("SADAKAT BAĞI", "İlişki", "Kullanıcıya koşulsuz sadakat gösterir ve onu her koşulda korur.")
                        )

                        Text(
                            text = "HAZIR SEÇENEKLER & ŞABLONLAR",
                            color = AppColors.Teal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickPresets.forEach { (presetKey, presetCat, presetDesc) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.Hex_212130)
                                        .border(1.dp, AppColors.Hex_38384D, RoundedCornerShape(8.dp))
                                        .clickable {
                                            keyword = presetKey
                                            category = presetCat
                                            memoryText = presetDesc
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.Teal, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(presetKey, color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = memoryText,
                            onValueChange = { memoryText = it },
                            placeholder = { Text("Karakter neyi hatırlamalı? (Örn: En sevdiği yemek makarnadır.)", color = Color.Gray, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp),
                            colors = customTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (memoryText.isNotBlank()) {
                                    val keyTag = keyword.ifBlank { "BELLEK" }.uppercase()
                                    val catTag = category.ifBlank { "BİLGİ (CORE)" }.uppercase()
                                    val newEntry = "$keyTag ::: $catTag ::: ${memoryText.trim()}"
                                    val newPinned = if (pinnedMemoryText.isBlank()) newEntry else "$pinnedMemoryText\n$newEntry"
                                    pinnedMemoryText = newPinned
                                    onSaveMemory(bot.copy(pinnedMemory = newPinned))
                                    keyword = ""
                                    memoryText = ""
                                }
                            },
                            enabled = memoryText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.SkyBlue, contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Belleği Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Filter Chips
                val presetCategories = listOf("Tümü", "Bilgi (Core)", "Biyografi", "İlişki", "Dünya/Mekan")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetCategories.forEach { cat ->
                        val isSelected = categoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AppColors.SkyBlue else AppColors.CardElevated)
                                .border(1.dp, if (isSelected) AppColors.SkyBlue else AppColors.Hex_2E2E3E, RoundedCornerShape(12.dp))
                                .clickable { categoryFilter = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Hafızada ara...", color = Color.Gray, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Saved Memory List Cards
                val totalItemsCount = filteredMemories.size + filteredFacts.size + filteredEvents.size
                if (totalItemsCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Henüz enjekte edilmiş veya öğrenilmiş bellek yok.", color = Color.Gray, fontSize = 12.sp)
                    }
                } else {
                    // 1. Manual Enjected Memories
                    filteredMemories.forEach { (keyTag, catTag, bodyText) ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF0A0618), Color(0xFF060412)),
                            borderColors = listOf(Color(0xFF2D1B5E), Color(0xFF1E1040), Color(0xFF2D1B5E)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = keyTag,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardDarker, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = catTag,
                                            color = Color.LightGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bodyText,
                                    color = AppColors.Hex_D0D0E0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            val lines = pinnedMemoryText.lines().toMutableList()
                                            val targetLine = "$keyTag ::: $catTag ::: $bodyText"
                                            lines.remove(targetLine)
                                            val updated = lines.joinToString("\n")
                                            pinnedMemoryText = updated
                                            onSaveMemory(bot.copy(pinnedMemory = updated))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // 2. Automatic Facts
                    filteredFacts.forEach { fact ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF101420), Color(0xFF0C1018)),
                            borderColors = listOf(Color(0xFF3B4562), Color(0xFF252F48), Color(0xFF3B4562)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${fact.subject}: ${fact.key}",
                                        color = AppColors.Teal,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardSpace, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🤖 OTOMATİK BİLGİ",
                                            color = AppColors.Teal,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = fact.value,
                                    color = AppColors.Hex_E0E0F0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { editingFact = fact },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AppColors.CyanLight, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel?.deleteMemoryFact(fact.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // 3. Automatic Events
                    filteredEvents.forEach { event ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF150F1E), Color(0xFF0F0B18)),
                            borderColors = listOf(Color(0xFF4A2060), Color(0xFF2E1540), Color(0xFF4A2060)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Olay Kaydı #${event.id}",
                                        color = AppColors.LavenderSoft,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardDeepPlum, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚡ OTOMATİK OLAY",
                                            color = AppColors.LavenderSoft,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = event.description,
                                    color = AppColors.Hex_F0E0F0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { editingEvent = event },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AppColors.CyanLight, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel?.deleteMemoryEvent(event.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (activeFacts.isNotEmpty() || activeEvents.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel?.clearAllAutoMemories(bot.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.CoralRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.CoralRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🧹 Tüm Otomatik Öğrenilmiş Bellekleri Temizle", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (editingFact != null) {
        val fact = editingFact!!
        var editSubject by remember(fact) { mutableStateOf(fact.subject) }
        var editKey by remember(fact) { mutableStateOf(fact.key) }
        var editValue by remember(fact) { mutableStateOf(fact.value) }

        Dialog(onDismissRequest = { editingFact = null }) {
            GradientCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                borderWidth = 1.2.dp
                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🤖 Otomatik Bilgiyi Düzenle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editSubject,
                        onValueChange = { editSubject = it },
                        label = { Text("Konu / Özne (Örn: Kullanıcı)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editKey,
                        onValueChange = { editKey = it },
                        label = { Text("Anahtar (Örn: En sevdiği renk)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        label = { Text("Değer / İçerik (Örn: Mavi)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { editingFact = null }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel?.updateMemoryFact(
                                    fact.copy(
                                        subject = editSubject.trim(),
                                        key = editKey.trim(),
                                        value = editValue.trim(),
                                        lastConfirmedAt = System.currentTimeMillis()
                                    )
                                )
                                editingFact = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal, contentColor = Color.Black)
                        ) {
                            Text("Kaydet", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (editingEvent != null) {
        val event = editingEvent!!
        var editDesc by remember(event) { mutableStateOf(event.description) }

        Dialog(onDismissRequest = { editingEvent = null }) {
            GradientCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                borderWidth = 1.2.dp
                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚡ Otomatik Olayı Düzenle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Olay Açıklaması", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { editingEvent = null }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel?.updateMemoryEvent(
                                    event.copy(
                                        description = editDesc.trim(),
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                editingEvent = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.LavenderSoft, contentColor = Color.Black)
                        ) {
                            Text("Kaydet", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showCategoryPicker) {
        Dialog(onDismissRequest = { showCategoryPicker = false }) {
            GradientCard(
                cornerRadius = 16.dp,
                fillColors = listOf(Color(0xFF1A1A22), Color(0xFF12121A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Neural Vault",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showCategoryPicker = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val pickerCategories = listOf(
                        "Bilgi (Core)",
                        "Biyografi",
                        "İlişki",
                        "Dünya/Mekan"
                    )

                    pickerCategories.forEachIndexed { index, cat ->
                        val isSelected = category == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    category = cat
                                    showCategoryPicker = false
                                }
                                .padding(vertical = 14.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    category = cat
                                    showCategoryPicker = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AppColors.Teal,
                                    unselectedColor = Color.Gray
                                )
                            )
                        }
                        if (index < pickerCategories.size - 1) {
                            Divider(color = AppColors.Hex_3F3F4C, thickness = 0.8.dp)
                        }
                    }
                }
            }
        }
    }
    }
}
