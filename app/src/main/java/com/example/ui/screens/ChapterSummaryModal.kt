
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

@Composable
fun ChapterSummaryModal(
    messages: List<com.example.data.local.MessageEntity>,
    onDismiss: () -> Unit,
    onRestartChapter: () -> Unit,
    onNextChapter: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var viewMode by remember { mutableStateOf(SummaryViewMode.PROMPT) }
    var currentIndex by remember { mutableStateOf(0) }
    var stepPhase by remember { mutableStateOf(StepPhase.CARD_ONLY) }
    var isClosing by remember { mutableStateOf(false) }

    val completedChapterNumber = remember(messages) {
        val lastAssistant = messages.lastOrNull { it.role == "assistant" || it.role == "model" }
        val txt = lastAssistant?.text ?: ""
        when {
            txt.contains("BÖLÜM 6 SONU") -> 6
            txt.contains("BÖLÜM 5 SONU") -> 5
            txt.contains("BÖLÜM 4 SONU") -> 4
            txt.contains("BÖLÜM 3 SONU") -> 3
            txt.contains("BÖLÜM 2 SONU") -> 2
            else -> 1
        }
    }

    // BookAudioController — modal açılırken zaten çalıyor olabilir, çift başlatma yok
    LaunchedEffect(Unit) {
        if (!com.example.util.BookAudioController.isPlaying) {
            com.example.util.BookAudioController.play(context, com.example.R.raw.bolumsonu, loop = true, volume = 0.85f)
        }
    }

    // Fade-out helper — BookAudioController stop() fadeOut içeriyor
    val safeExitWithFade = remember {
        { action: () -> Unit ->
            if (!isClosing) {
                isClosing = true
                coroutineScope.launch {
                    com.example.util.BookAudioController.stop(fadeOutMs = 1500)
                    delay(1600)
                    action()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { /* BookAudioController global — burada release etme */ }
    }

    val userMsgs = remember(messages) { messages.filter { it.role == "user" } }
    val lastAssistantMsg = remember(messages) {
        messages.lastOrNull { it.role == "assistant" || it.role == "model" }
    }
    val isChapter7 = remember(messages, lastAssistantMsg) {
        userMsgs.size >= 34 || (lastAssistantMsg?.text?.contains("BÖLÜM 7") == true)
    }
    val isChapter6 = remember(messages, lastAssistantMsg) {
        !isChapter7 && (userMsgs.size >= 29 || (lastAssistantMsg?.text?.contains("BÖLÜM 6") == true))
    }
    val isChapter5 = remember(messages, lastAssistantMsg) {
        !isChapter7 && !isChapter6 && (userMsgs.size >= 25 || (lastAssistantMsg?.text?.contains("BÖLÜM 5") == true))
    }
    val isChapter4 = remember(messages, lastAssistantMsg) {
        !isChapter7 && !isChapter6 && !isChapter5 && (userMsgs.size >= 20 || (lastAssistantMsg?.text?.contains("BÖLÜM 4") == true))
    }
    val isChapter3 = remember(messages, lastAssistantMsg) {
        !isChapter7 && !isChapter6 && !isChapter5 && !isChapter4 && (userMsgs.size >= 11 || (lastAssistantMsg?.text?.contains("BÖLÜM 3") == true))
    }
    val isChapter2 = remember(messages, lastAssistantMsg) {
        !isChapter7 && !isChapter6 && !isChapter5 && !isChapter4 && !isChapter3 &&
        (userMsgs.size >= 5 || (lastAssistantMsg?.text?.contains("BÖLÜM 2") == true))
    }

    val choices = remember(messages, isChapter2, isChapter3, isChapter4, isChapter5, isChapter6, isChapter7) {
        val items = mutableListOf<ChapterChoiceItem>()

        if (isChapter7) {
            // ── BÖLÜM 7 SEÇİMLERİ ──
            if (userMsgs.size >= 35) {
                val c1Msg = userMsgs.getOrNull(34)?.text ?: ""
                val isAsk   = c1Msg.contains("1") || c1Msg.contains("sor") || c1Msg.contains("ne istiyorsun") || c1Msg.contains("direkt")
                val isLeave = c1Msg.contains("2") || c1Msg.contains("çık") || c1Msg.contains("dışarı") || c1Msg.contains("zaman")
                val isWanda = c1Msg.contains("3") || c1Msg.contains("Wanda") || c1Msg.contains("zihin") || c1Msg.contains("oku")
                items.add(ChapterChoiceItem(
                    id = 1,
                    title = "1. DR. HALE'E YAKLAŞIM",
                    selectedOption = when {
                        isAsk   -> "Doğrudan sordun: 'Benden ne istiyorsun?'"
                        isLeave -> "Odadan çıktın — hazmetmek için zaman lazımdı, sonra döndün."
                        else    -> "Wanda'ya baktın: önce zihnini oku, sonra konuş."
                    },
                    impactText = when {
                        isAsk   -> "Hale: 'Seni tamamlamak için geldim.' Kelime ağzında yanlış hissettirdi."
                        isLeave -> "Üç dakika sonra geri döndün. Kaçmak yerine dönmek seçimdi."
                        else    -> "Wanda: 'Gerçekten pişman. Ve korkuyor — senden değil, başkalarından.'"
                    },
                    icon = if (isWanda) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isWanda,
                    impactLevel = "kritik"
                ))
            }
            if (userMsgs.size >= 36) {
                val c2Msg = userMsgs.getOrNull(35)?.text ?: ""
                val isCold    = c2Msg.contains("1") || c2Msg.contains("yetmez") || c2Msg.contains("anlat") || c2Msg.contains("soğuk")
                val isParents = c2Msg.contains("2") || c2Msg.contains("annem") || c2Msg.contains("babam") || c2Msg.contains("aile")
                val isLeave2  = c2Msg.contains("3") || c2Msg.contains("çık") || c2Msg.contains("yeterli") || c2Msg.contains("istemiyorum")
                items.add(ChapterChoiceItem(
                    id = 2,
                    title = "2. 'PİŞMANIM' SÖZÜNE TEPKİ",
                    selectedOption = when {
                        isCold    -> "Soğuk: 'Pişmanlık yetmez. Ne istediklerini anlat.'"
                        isParents -> "Beklenmedik: 'Annem ve babam hâlâ hayatta mı?'"
                        else      -> "Ayağa kalktın — bu kadar bilgi yeterliydi."
                    },
                    impactText = when {
                        isCold    -> "Aktivasyon protokolü, tetikleyici frekans, uzaktan kumanda. Hale her şeyi anlattı."
                        isParents -> "Prag'da. Natasha hemen: 'Konum?' Ekip onlar için de harekete geçti."
                        else      -> "Altı gün sonra Kör Nokta gelecekti. Hazır olduğunda döndin."
                    },
                    icon = if (isParents) "💫" else "💭",
                    isBranching = isParents,
                    isRelationship = isParents || isCold,
                    impactLevel = "kritik"
                ))
            }
            if (userMsgs.size >= 37) {
                val c3Msg = userMsgs.getOrNull(36)?.text ?: ""
                val isSolo = c3Msg.contains("1") || c3Msg.contains("tek başıma") || c3Msg.contains("yalnız") || c3Msg.contains("solo")
                val isTeam = c3Msg.contains("2") || c3Msg.contains("ekip") || c3Msg.contains("birlikte") || c3Msg.contains("plan")
                val isBait = c3Msg.contains("3") || c3Msg.contains("tuzak") || c3Msg.contains("yem") || c3Msg.contains("kullanın")
                items.add(ChapterChoiceItem(
                    id = 3,
                    title = "3. CIHAZ & OPERASYON KARARI",
                    selectedOption = when {
                        isSolo -> "Tek başına girdin — cihazı sen takacaktın."
                        isTeam -> "Ekiple gidildi — plan masada şekillendi."
                        else   -> "Tuzak oldun — beni yem olarak kullanın."
                    },
                    impactText = when {
                        isSolo -> "Sam: 'Sırtın kollanıyor. Vaat.' Steve: 'Emin misin?'"
                        isTeam -> "İlk kez masanın etrafında, onlardan biri gibi oturdun."
                        else   -> "'Bu sefer kaçmak için değil.' Steve uzun süre sana baktı."
                    },
                    icon = if (isBait) "💫" else "🔀",
                    isBranching = isBait,
                    isRelationship = isTeam,
                    impactLevel = "kritik"
                ))
            }
            if (userMsgs.size >= 38) {
                val c4Msg = userMsgs.getOrNull(37)?.text ?: ""
                val isShare  = c4Msg.contains("1") || c4Msg.contains("merak") || c4Msg.contains("lüks") || c4Msg.contains("yanılıyormuşum")
                val isSilent = c4Msg.contains("2") || c4Msg.contains("sessiz") || c4Msg.contains("çık") || c4Msg.contains("hiçbir şey")
                val isAskW   = c4Msg.contains("3") || c4Msg.contains("sen") || c4Msg.contains("hissediyordun") || c4Msg.contains("girerken")
                items.add(ChapterChoiceItem(
                    id = 4,
                    title = "4. WANDA'YA ASANSÖR CEVABI",
                    selectedOption = when {
                        isShare  -> "'Merakı bir lüks sayıyordum. Belki yanılıyormuşum.'"
                        isSilent -> "Hiçbir şey söylemedin — asansörden çıktın."
                        else     -> "'Sen ne hissediyordun? Benim zihnime girerken.'"
                    },
                    impactText = when {
                        isShare  -> "Wanda: 'Ve şimdi?' — 'Belki gitmemeye çalışıyorum.' — 'Bu yeterli.'"
                        isSilent -> "Natasha koridorda bekliyordu. Omuz omuza yürüdünüz, kelime gerekmeden."
                        else     -> "Wanda: 'Zihnin çok yer kaplıyor. Sanki açılmayı bekleyen bir şey var.'"
                    },
                    icon = if (isAskW) "💫" else "💭",
                    isBranching = false,
                    isRelationship = true,
                    impactLevel = "orta"
                ))
            }
            if (userMsgs.size >= 39) {
                val c5Msg = userMsgs.getOrNull(38)?.text ?: ""
                val isConfirm = c5Msg.contains("1") || c5Msg.contains("haklısın") || c5Msg.contains("artık") || c5Msg.contains("kalmak")
                val isHonest  = c5Msg.contains("2") || c5Msg.contains("düşünüyorum") || c5Msg.contains("ama") || c5Msg.contains("gitmek istemiyorum")
                items.add(ChapterChoiceItem(
                    id = 5,
                    title = "5. ÇATIDA NATASHA İLE GECE",
                    selectedOption = when {
                        isConfirm -> "'Haklısın. Artık düşünmüyorum.'"
                        isHonest  -> "'Düşünüyorum. Ama bu sefer gitmek istemiyorum.'"
                        else      -> "Sessiz kaldın — sadece New York'a baktınız."
                    },
                    impactText = when {
                        isConfirm -> "Natasha'nın omuzu senin omunla temas etti. Saat dörde kadar konuşmadınız ama gitmediniz de."
                        isHonest  -> "Natasha: 'Bu fark her şeydir.' Sesin o gece farklıydı — daha az hesaplı."
                        else      -> "Sabahın altısında güneş doğdu. Natasha kalktı ve gitti. Sen kaldın."
                    },
                    icon = "💫",
                    isBranching = false,
                    isRelationship = true,
                    impactLevel = "orta"
                ))
            }
            if (userMsgs.size >= 40) {
                val c6Msg = userMsgs.getOrNull(39)?.text ?: ""
                val isPrimary = c6Msg.contains("1") || c6Msg.contains("birincil") || c6Msg.contains("ben takacağım") || c6Msg.contains("cihaz")
                val isScout   = c6Msg.contains("2") || c6Msg.contains("keşif") || c6Msg.contains("önce göreyim") || c6Msg.contains("tara")
                val isBait2   = c6Msg.contains("3") || c6Msg.contains("tuzak") || c6Msg.contains("yem") || c6Msg.contains("kullan")
                items.add(ChapterChoiceItem(
                    id = 6,
                    title = "6. OPERASYON ROLÜ",
                    selectedOption = when {
                        isPrimary -> "Birincil saldırı — cihazı ben takacağım."
                        isScout   -> "Keşif — önce içeriyi göreyim, sonra karar."
                        else      -> "Tuzak ol — beni yem olarak kullanalım."
                    },
                    impactText = when {
                        isPrimary -> "Sam: 'Sırtın kollanıyor. Vaat.' Plan şekillendi."
                        isScout   -> "Sen ve Natasha öne, ekip desteğe. En sessiz plan."
                        else      -> "'Bu sefer kaçmak için değil.' En cesur seçim."
                    },
                    icon = if (isBait2) "💫" else "🔀",
                    isBranching = isBait2,
                    isRelationship = isPrimary,
                    impactLevel = "kritik"
                ))
            }
            if (userMsgs.size >= 41) {
                val c7Msg = userMsgs.getOrNull(40)?.text ?: ""
                val isTrust  = c7Msg.contains("1") || c7Msg.contains("inan") || c7Msg.contains("yanına al") || c7Msg.contains("benim gibiyim")
                val isVerify = c7Msg.contains("2") || c7Msg.contains("güvenme") || c7Msg.contains("Wanda") || c7Msg.contains("oku")
                val isLeaveM = c7Msg.contains("3") || c7Msg.contains("planı bozma") || c7Msg.contains("geride bırak") || c7Msg.contains("çık")
                items.add(ChapterChoiceItem(
                    id = 7,
                    title = "7. KIRMIZI GÖZ TAŞIYICISI — SON KARAR",
                    selectedOption = when {
                        isTrust  -> "İnandın ve yanına aldın — o da senin gibiydi."
                        isVerify -> "Güvenmedin ama bırakmadın — Wanda önce okudu."
                        else     -> "Planı bozmadın — operasyon bitti, sonra döndün."
                    },
                    impactText = when {
                        isTrust  -> "Beyaz Göz kızıl frekansa dokundu. İki güç, ilk kez serbest birbirini tanıdı."
                        isVerify -> "Wanda: 'Gerçek. Korkuyor — tuzak değil.' Natasha onu aldı."
                        else     -> "Döndüğünde Mira hâlâ oradaydı. Beklemişti."
                    },
                    icon = "💫",
                    isBranching = false,
                    isRelationship = true,
                    impactLevel = "kritik"
                ))
            }
        } else if (isChapter6) {
            // ── BÖLÜM 6 SEÇİMLERİ ──
            if (userMsgs.size >= 30) {
                val c1Msg = userMsgs.getOrNull(29)?.text ?: ""
                val isAsk   = c1Msg.contains("1") || c1Msg.contains("sor") || c1Msg.contains("kim") || c1Msg.contains("sakin")
                val isLeave = c1Msg.contains("2") || c1Msg.contains("dışarı") || c1Msg.contains("yalnız") || c1Msg.contains("sindir")
                val isGather = c1Msg.contains("3") || c1Msg.contains("ekibi topla") || c1Msg.contains("herkes") || c1Msg.contains("toplantı")
                items.add(ChapterChoiceItem(
                    id = 1,
                    title = "1. WANDA'NIN AÇIKLAMASINA TEPKİ",
                    selectedOption = if (isAsk) "Sakin kaldın ve sordun: 'Kim? Kim arıyor beni?'"
                                    else if (isLeave) "Dışarı çıktın — beş dakika yalnız kaldın, sonra döndün: 'Anlat, tamamını.'"
                                    else "Ekibi topladın: 'Bunu herkesin duyması gerekiyor.'",
                    impactText = if (isAsk) "HYDRA öncesi, daha köklü bir örgüt. 'Seni yirmi yıl önce bırakmadılar. Sadece bekledi.'"
                                else if (isLeave) "Wanda bekliyordu — sanki geri döneceğini zaten biliyormuş gibi."
                                else "Steve: 'Bu senin sorunun değil artık. Bizim sorunumuz.' İlk kez bu hafif geldi.",
                    icon = if (isGather) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isGather
                ))
            }

            if (userMsgs.size >= 31) {
                val c2Msg = userMsgs.getOrNull(30)?.text ?: ""
                val isLead    = c2Msg.contains("1") || c2Msg.contains("liderlik") || c2Msg.contains("hikayem")
                val isSupport = c2Msg.contains("2") || c2Msg.contains("destek") || c2Msg.contains("arka planda")
                val isNo      = c2Msg.contains("3") || c2Msg.contains("hayır") || c2Msg.contains("gidemem")
                items.add(ChapterChoiceItem(
                    id = 2,
                    title = "2. İSTANBUL OPERASYONUNDA ROL",
                    selectedOption = if (isLead) "Liderlik ettim — bu benim hikayem."
                                    else if (isSupport) "Destek olarak gittim, duygusal bulanıklık riski vardı."
                                    else "Gitmedim — objektif kalamam, birini tehlikeye atabilirim.",
                    impactText = if (isLead) "Natasha saygıyla başını eğdi: 'Tamam. Ama duygusal boyutun seni etkiler — bilerek git.'"
                                else if (isSupport) "Natasha gözlerinde sıcak bir onay parıldadı. Sınırını bilmek ve söylemek yirmi yılda öğrenilmemiş bir şeydi."
                                else "Wanda: 'Gitmemek de bir seçim. Ve bazen en zor olanı bu.' Tower'da Voss'un önünde beklerken cevabı zaten bulmuştun.",
                    icon = if (isLead || isSupport) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isLead || isSupport
                ))
            }

            if (userMsgs.size >= 32) {
                val c3Msg = userMsgs.getOrNull(31)?.text ?: ""
                val isAnger   = c3Msg.contains("1") || c3Msg.contains("öfke") || c3Msg.contains("hesaplı")
                val isNumb    = c3Msg.contains("2") || c3Msg.contains("uyuşma") || c3Msg.contains("dondum")
                items.add(ChapterChoiceItem(
                    id = 3,
                    title = "3. 'DENEK 7-A' GERÇEĞİNE YANIT",
                    selectedOption = if (isAnger) "Soğuk ve hesaplı bir öfke — bir hedefi olan öfke."
                                    else if (isNumb) "Hiçbir şey hissetmedin. Büyük şeyler böyle işlenir, katman katman."
                                    else "Paradoks ama doğru: rahatlama. Sonunda bir cevap vardı.",
                    impactText = if (isAnger) "Yirmi yıldır neden kaçtığını biliyordun artık. Bu bilmek öfkeyi bir silaha dönüştürdü. Steve yanında sessizce durdu."
                                else if (isNumb) "Natasha geç saatte geldi, kahve koydu. 'Büyük şeyler böyle işlenir. Hemen değil — katman katman.'"
                                else "Wanda: 'İyi misin?' — 'Hayır. Ama ilk kez ne olduğunu biliyorum. Bu fark ediyor.'",
                    icon = "💭",
                    isBranching = false,
                    isRelationship = !isAnger
                ))
            }

            if (userMsgs.size >= 33) {
                val c4Msg = userMsgs.getOrNull(32)?.text ?: ""
                val isHunt = c4Msg.contains("1") || c4Msg.contains("bulmak") || c4Msg.contains("bitirmek")
                val isStay = c4Msg.contains("2") || c4Msg.contains("kalmak") || c4Msg.contains("burada")
                val isVoss = c4Msg.contains("3") || c4Msg.contains("Voss") || c4Msg.contains("kurtarmak")
                items.add(ChapterChoiceItem(
                    id = 4,
                    title = "4. 'BUNDAN SONRA NE İSTİYORSUN?'",
                    selectedOption = if (isHunt) "Onları bulmak istiyorum. Ve bitirmek."
                                    else if (isStay) "Bilmiyorum henüz. Ama burada kalmak istiyorum."
                                    else "Voss'u kurtarmak istiyorum. Sonra bakarız.",
                    impactText = if (isHunt) "Sam: 'Hoş geldin Blackwood — gerçekten bu sefer.' Ekibin gözlerinde tam bir kabullenme."
                                else if (isStay) "Steve elini uzattı: 'Yeter bu.' İlk kez yirmi yılda — elini tutan birinin elini geri bırakmak istemedin."
                                else "Wanda'nın gözleri doldu. Birisini kurtarmak için bir şey istiyordun — kaçmak için değil. Bu fark her şeydi.",
                    icon = "💫",
                    isBranching = false,
                    isRelationship = true
                ))
            }

        } else if (isChapter5) {
            // ── BÖLÜM 5 SEÇİMLERİ ──
            if (userMsgs.size >= 26) {
                val c1Msg = userMsgs.getOrNull(25)?.text ?: ""
                val isTalk   = c1Msg.contains("1") || c1Msg.contains("dur") || c1Msg.contains("bak") || c1Msg.contains("tanı") || c1Msg.contains("konuş")
                val isAttack = c1Msg.contains("2") || c1Msg.contains("saldır") || c1Msg.contains("operasyon") || c1Msg.contains("diyalog yok")
                val isMind   = c1Msg.contains("3") || c1Msg.contains("Beyaz Göz") || c1Msg.contains("zihin") || c1Msg.contains("oku")
                items.add(ChapterChoiceItem(
                    id = 1,
                    title = "1. VOSS'UN 'KAÇ' UYARISINA TEPKİ",
                    selectedOption = if (isTalk) "Durdun ve Türkçe sordun: 'Kim söyledi sana bunu?'"
                                    else if (isAttack) "Diyalog yok — üzerine atladın. Sonunda: 'Söylemem gereken bir şey var.'"
                                    else "Beyaz Göz açıldı — zihnine dokundun. Altında: senin adın, yıllarca taşınmış.",
                    impactText = if (isTalk) "'Seni bulmak için kullandılar beni. Ama ben seni uyarmak istedim.'"
                                else if (isAttack) "Elini çektin. Steve: 'Ne oldu?' — 'Bilmiyorum.' Doğruydu ve yalancı hissettirdi."
                                else "Voss'un zihninde acı, katmanlar halinde. En altında seni bulmak değil — uyarmak için taşınan bir fener.",
                    icon = if (isMind) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isTalk || isMind
                ))
            }

            if (userMsgs.size >= 27) {
                val c2Msg = userMsgs.getOrNull(26)?.text ?: ""
                val isEnter   = c2Msg.contains("1") || c2Msg.contains("ben") || c2Msg.contains("gireceğim") || c2Msg.contains("konuşmam")
                val isNatasha = c2Msg.contains("2") || c2Msg.contains("Natasha") || c2Msg.contains("mesafe") || c2Msg.contains("o girsin")
                val isWanda   = c2Msg.contains("3") || c2Msg.contains("Wanda") || c2Msg.contains("zihinsel") || c2Msg.contains("tarama")
                items.add(ChapterChoiceItem(
                    id = 2,
                    title = "2. VOSS SORGU ODASI SEÇİMİ",
                    selectedOption = if (isEnter) "Sen girdin — onunla konuşman gerekiyordu."
                                    else if (isNatasha) "Natasha girdi, sen cam arkasından izledin."
                                    else "Wanda girdi — zihinsel tarama daha kesin olurdu.",
                    impactText = if (isEnter) "'İkimiz de aynı şeyiz Blackwood. Sadece farklı ellerde.' — Voss"
                                else if (isNatasha) "Natasha: 'Seni arıyordu. Ama bir noktada karar değiştirdi. Belki sen biliyorsundur.'"
                                else "Wanda solgunlaşmış çıktı: 'Yıllar. Acı. En altında senin adın — seni uyarmak için taşınan bir fener gibi.'",
                    icon = if (isEnter || isWanda) "💫" else "💭",
                    isBranching = false,
                    isRelationship = !isNatasha
                ))
            }

            if (userMsgs.size >= 28) {
                val c3Msg = userMsgs.getOrNull(27)?.text ?: ""
                val isAgree    = c3Msg.contains("1") || c3Msg.contains("katılıyorum") || c3Msg.contains("korkutuyor") || c3Msg.contains("olabilirdi")
                val isDisagree = c3Msg.contains("2") || c3Msg.contains("katılmıyorum") || c3Msg.contains("seçimlerimi") || c3Msg.contains("fark bu")
                items.add(ChapterChoiceItem(
                    id = 3,
                    title = "3. 'İKİMİZ DE AYNIYIZ' SÖZÜNE YORUM",
                    selectedOption = if (isAgree) "Katıldın — ve bu seni korkuttu. Farklı bir elde sen de Voss olabilirdin."
                                    else if (isDisagree) "Katılmadın — benzer başlangıçlar benzer sonuçlar yaratmak zorunda değil."
                                    else "Ne kabul ne ret — henüz karar yok, sadece dinledin.",
                    impactText = if (isAgree) "Wanda'nın sözünü hatırladın: 'Seni uyarmak için taşıdı.' Voss o zırhın içinde bir şeyleri korumuştu."
                                else if (isDisagree) "Sen kaçmıştın — kendi kararlarınla, kendi hızınla. Bu fark küçük görünür, ama her şeydi."
                                else "Tamamını bilmeden hüküm vermek, yirmi yıl boyunca seni şekillendiren aceleci yargıların aynısıydı.",
                    icon = "💭",
                    isBranching = false,
                    isRelationship = false
                ))
            }

        } else if (isChapter4) {
            // ── BÖLÜM 4 SEÇİMLERİ ──
            if (userMsgs.size >= 21) {
                val c1Msg = userMsgs.getOrNull(20)?.text ?: ""
                val isImmediate   = c1Msg.contains("1") || c1Msg.contains("Varım") || c1Msg.contains("hemen") || c1Msg.contains("gidiyorum")
                val isConditional = c1Msg.contains("2") || c1Msg.contains("şartlı") || c1Msg.contains("kurallarımla")
                items.add(ChapterChoiceItem(
                    id = 1,
                    title = "1. FURY'NİN GÖREVE ÇAĞRISINA CEVAP",
                    selectedOption = if (isImmediate) "Tek kelime: 'Varım.'"
                                    else if (isConditional) "Şartlı kabul: 'Giderim ama kendi kurallarımla.'"
                                    else "Önce sorular sordun: depo, kaç kişi, alıcı.",
                    impactText = if (isImmediate) "Steve şaşırdı — beklemiyordu bu kadar hızlı. Natasha fısıldadı: 'İlk kez varım diyorsun.'"
                                else if (isConditional) "Fury hologramda omuz silkti — bu onun onayıydı. Sam: 'Bu adam müzakereci olabilirdi.'"
                                else "Natasha başını eğdi. Steve'in yüzünde 'haklı' ifadesi. Fury: 'Bu yüzden sizi gönderiyorum.'",
                    icon = if (isImmediate) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isImmediate
                ))
            }

            if (userMsgs.size >= 22) {
                val c2Msg = userMsgs.getOrNull(21)?.text ?: ""
                val isScout = c2Msg.contains("1") || c2Msg.contains("Keşif") || c2Msg.contains("önden") || c2Msg.contains("ilk ben")
                val isSync  = c2Msg.contains("2") || c2Msg.contains("Natasha") || c2Msg.contains("eş zamanlı") || c2Msg.contains("yan yana")
                items.add(ChapterChoiceItem(
                    id = 2,
                    title = "2. DEPO OPERASYONUNDA ROL",
                    selectedOption = if (isScout) "Keşif önü — ilk sen girdin, koridorları taradın."
                                    else if (isSync) "Natasha ile eş zamanlı giriş — yan yana, birbirinizi kapsayarak."
                                    else "Geride kaldın, tüm takımı izledin — büyük resmi sen gördün.",
                    impactText = if (isScout) "Yirmi yıldır böyle yürümüştün: yalnız, sessiz, önceden. Ama kulaklıktan Natasha'nın nefesini duyunca daha az yalnız hissettirdi."
                                else if (isSync) "Natasha'nın adımları seninle eşleşiyordu — bilinçsiz bir senkronizasyon. Bu gece doğal geldi."
                                else "İki kez erken uyarı verdin. İkisi de hayat kurtardı.",
                    icon = if (isSync) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isSync
                ))
            }

            if (userMsgs.size >= 23) {
                val c3Msg = userMsgs.getOrNull(22)?.text ?: ""
                val isStop     = c3Msg.contains("1") || c3Msg.contains("dur") || c3Msg.contains("araştır") || c3Msg.contains("bekle")
                val isContinue = c3Msg.contains("2") || c3Msg.contains("devam") || c3Msg.contains("sonra") || c3Msg.contains("planı değiştirme")
                items.add(ChapterChoiceItem(
                    id = 3,
                    title = "3. BEKLENMEDİK ENERJİ İMZASI KARARI",
                    selectedOption = if (isStop) "Ekibi durdurdun: 'Üç dakika ver bana.' İmzayı önce araştırdın."
                                    else if (isContinue) "Planı bozmadın, operasyonu tamamladın — imzayı sonra araştırdın."
                                    else "Tek başına imzaya gittin, ekip planı sürdürdü.",
                    impactText = if (isStop) "Kasa odasında criyojenik tüp — üzerinde eski Sovyet askeri damgası. Steve: 'Bu plana dahil değildi.'"
                                else if (isContinue) "Silah değil, şifreli bilgisayar sürücüsü. Stark: 'Sadece bir kişinin biyometrik imzasıyla açılıyor.' Sana baktı."
                                else "İçerideki adam Türkçe söyledi: 'Nihayet. Seni yirmi yıldır bekliyordum.'",
                    icon = "🔀",
                    isBranching = !isStop,
                    isRelationship = isStop
                ))
            }

            if (userMsgs.size >= 24) {
                val c4Msg = userMsgs.getOrNull(23)?.text ?: ""
                val isHonest = c4Msg.contains("1") || c4Msg.contains("Dürüst") || c4Msg.contains("farklıydı") || c4Msg.contains("gerçekti")
                val isShort  = c4Msg.contains("2") || c4Msg.contains("Bitti") || c4Msg.contains("kısa") || c4Msg.contains("yeterli")
                items.add(ChapterChoiceItem(
                    id = 4,
                    title = "4. STEVE'İN 'NASIL GEÇTİ?' SORUSU",
                    selectedOption = if (isHonest) "Dürüst: 'Düşündüğümden farklıydı. Daha gerçekti.'"
                                    else if (isShort) "Kısa: 'Bitti. Bu yeterli.'"
                                    else "Soruyla karşıladın: 'Sovyet dosyasını araştırdınız mı?'",
                    impactText = if (isHonest) "Steve: 'Her zaman öyle olur. Tekrar gelmek ister misin?' — 'Evet.' İlk kez gönüllü olarak."
                                else if (isShort) "Steve çıkarken omzuna dokundu. Natasha: 'İyi iş.' Onun ağzından iki kelime fazlasıydı."
                                else "Steve: 'Blackwood, bu operasyon seni bulmak için kurulmuş olabilir. Birilerinin.' Oda soğudu.",
                    icon = if (isHonest) "💫" else "💭",
                    isBranching = false,
                    isRelationship = isHonest
                ))
            }

        } else if (isChapter3) {
            if (userMsgs.size >= 11) {
                val c1Msg = userMsgs.getOrNull(10)?.text ?: ""
                val isUnconditional = c1Msg.contains("2") || c1Msg.contains("Tereddütsüz")
                val isShort = c1Msg.contains("3") || c1Msg.contains("kısa")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. OTUZ GÜNLÜK TEKLİFE CEVAP",
                        selectedOption = if (isUnconditional) "Tereddütsüz 30 günlük deneme süresini kabul ettin."
                                        else if (isShort) "30 günü çok uzun bulup birkaç günlük kısa süre önerdin."
                                        else "30 günlük süreyi kendi çıkış şartlarınla kabul ettin.",
                        impactText = if (isUnconditional) "Steve Rogers memnun kaldı, sana karşı güveni arttı."
                                    else if (isShort) "Steve anlayış gösterdi ama ekibin temkinli yaklaşımı sürdü."
                                    else "Özgürlüğünden ödün vermeden ilk adımı attın.",
                        icon = if (isUnconditional) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isUnconditional
                    )
                )
            }

            if (userMsgs.size >= 12) {
                val c2Msg = userMsgs.getOrNull(11)?.text ?: ""
                val isEager = c2Msg.contains("2") || c2Msg.contains("İstekli")
                val isRefuse = c2Msg.contains("3") || c2Msg.contains("Reddet")
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. ANTRENMAN SALONU DEĞERLENDİRMESİ",
                        selectedOption = if (isEager) "Steve Rogers ile antrenmana istekli katıldın."
                                        else if (isRefuse) "Fiziksel testi reddedip teorik anlatım istedin."
                                        else "Sınırlarını kendin belirlemek şartıyla temkinli kabul ettin.",
                        impactText = if (isEager) "Ekip üyelerinde büyük merak ve olumlu izlenim uyandırdın."
                                    else if (isRefuse) "Gizemli kaldın ama Steve seni yine de antrenman ortamına davet etti."
                                    else "Kontrolü elinde tutarak antrenman alanına adım attın.",
                        icon = if (isEager) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isEager
                    )
                )
            }

            if (userMsgs.size >= 13) {
                val c3Msg = userMsgs.getOrNull(12)?.text ?: ""
                val isFullPower = c3Msg.contains("3") || c3Msg.contains("Hiç geri tutma")
                val isPushHard = c3Msg.contains("2") || c3Msg.contains("zorla")
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. STEVE'E KARŞI GÜÇ GÖSTERİMİ",
                        selectedOption = if (isFullPower) "Hiç geri tutmadın, Kırmızı Göz ivmesiyle kazandın."
                                        else if (isPushHard) "Steve'in savunmasını zorladın ama sınırda durdun."
                                        else "Kendini tamamen kontrol ettin, Steve'e zarar vermedin.",
                        impactText = if (isFullPower) "Bruce Banner dehşete düştü, Steve gücüne takdirle yaklaştı."
                                    else if (isPushHard) "Natasha ve Steve hızından ve kontrolünden etkilendi."
                                    else "Hızlı iyileşme gücün ve kontrolün Natasha'dan takdir gördü.",
                        icon = if (isFullPower) "🔀" else "💫",
                        isBranching = isFullPower,
                        isRelationship = !isFullPower
                    )
                )
            }

            if (userMsgs.size >= 14) {
                val c4Msg = userMsgs.getOrNull(13)?.text ?: ""
                val isMin = c4Msg.contains("2") || c4Msg.contains("Minimum")
                val isMax = c4Msg.contains("3") || c4Msg.contains("tam kapasite")
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. ENERJİ TESTİ GÖSTERİMİ",
                        selectedOption = if (isMin) "Minimum kıvılcım seviyesinde güç uyguladın."
                                        else if (isMax) "Neredeyse tam kapasitede devasa enerji patlaması yarattın."
                                        else "Orta seviyede kontrollü enerji dalgası fırlattın.",
                        impactText = if (isMin) "Sınırlarını gizli tuttun, merak uyandırdın."
                                    else if (isMax) "Stark sayılara hayran kaldı, Wanda bina seviyesi potansiyelini not etti."
                                    else "Bruce Banner senin kozmik imzanın benzersiz olduğunu belirtti.",
                        icon = if (isMax) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isMax
                    )
                )
            }

            if (userMsgs.size >= 15) {
                val c5Msg = userMsgs.getOrNull(14)?.text ?: ""
                val isSerious = c5Msg.contains("3") || c5Msg.contains("ciddiye")
                val isSilent = c5Msg.contains("2") || c5Msg.contains("Sessiz")
                items.add(
                    ChapterChoiceItem(
                        id = 5,
                        title = "5. SAM WILSON'IN ŞAKASINA TEPKİ",
                        selectedOption = if (isSerious) "Şakayı ciddiye alıp soğuk durdun."
                                        else if (isSilent) "Sessiz kalıp sadece gülümsedin."
                                        else "Kuru bir espriyle masayı kahkahaya boğdun.",
                        impactText = if (isSerious) "Mesafeli tavrını korudun."
                                    else if (isSilent) "Saygıyla karşılanan mesafeli bir gülümseme bıraktın."
                                    else "Yıllar sonra ilk kez bir ekiple masada gülmenin tadını çıkardın.",
                        icon = if (!isSerious && !isSilent) "💫" else "💭",
                        isBranching = false,
                        isRelationship = !isSerious && !isSilent
                    )
                )
            }

            if (userMsgs.size >= 16) {
                val c6Msg = userMsgs.getOrNull(15)?.text ?: ""
                val isShort = c6Msg.contains("2") || c6Msg.contains("çatıya") || c6Msg.contains("Kısa")
                val isBig = c6Msg.contains("3") || c6Msg.contains("uzağa") || c6Msg.contains("Büyük")
                items.add(
                    ChapterChoiceItem(
                        id = 6,
                        title = "6. S.H.I.E.L.D. ALARM ALARMINDA KARAR",
                        selectedOption = if (isBig) "Tümünü geride bırakıp başka bir şehre ışınlandın (Büyük Kaçış)."
                                        else if (isShort) "Refleksle çatıya ışınlandın, sonra geri döndün (Kısa Kaçış)."
                                        else "Kule'de kaldın ve ekibe güvenmeyi seçtin (Normal Akış).",
                        impactText = if (isBig) "Thor seni 3 gün sonra buldu, kaçış ve dönüş deneyimin olgunlaştı."
                                    else if (isShort) "Çatıdan izledikten sonra adımlayarak aşağı indin, Wanda cesaretini övdü."
                                    else "Wanda ve Steve ile güven bağını güçlendirdin, kalmanın cesaret olduğunu hissettin.",
                        icon = "🔀",
                        isBranching = true,
                        isRelationship = !isBig
                    )
                )
            }

            if (userMsgs.size >= 17) {
                val c7Msg = userMsgs.getOrNull(16)?.text ?: ""
                val isSincere = c7Msg.contains("1") || c7Msg.contains("Tam dürüst") || c7Msg.contains("Korkutucu")
                val isJoking = c7Msg.contains("2") || c7Msg.contains("Yarım")
                items.add(
                    ChapterChoiceItem(
                        id = 7,
                        title = "7. NATASHA İLE ÇATI DİYALOGU",
                        selectedOption = if (isSincere) "Kalmanın kaybedecek bir şey biriktirmek olduğunu dürüstçe söyledin."
                                        else if (isJoking) "Manzaradan bahsettiğini söyleyerek durumu şakaya vurdun."
                                        else "Kapanıp sadece hava aldığını söyledin.",
                        impactText = if (isSincere) "Natasha Kızıl Oda geçmişini paylaşarak derin bir bağ kurdu."
                                    else if (isJoking) "Natasha hafifçe gülümsedi ve 30 günlük süreni hatırlattı."
                                    else "Natasha mesafene saygı duyarak iyi geceler diledi.",
                        icon = if (isSincere) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isSincere
                    )
                )
            }

            if (userMsgs.size >= 18) {
                val c8Msg = userMsgs.getOrNull(17)?.text ?: ""
                val isSuppress = c8Msg.contains("2") || c8Msg.contains("cebine") || c8Msg.contains("bastır")
                val isFlashback = c8Msg.contains("3") || c8Msg.contains("flashback") || c8Msg.contains("bak")
                items.add(
                    ChapterChoiceItem(
                        id = 8,
                        title = "8. KOLYE ANI & İÇ SES",
                        selectedOption = if (isSuppress) "Kolyeyi cebine koyup duygusal zayıflığı bastırdın."
                                        else if (isFlashback) "Kolyeye bakıp 20 yıl önceki patlamayı hatırladın."
                                        else "Kolyeyi elinde tutup umutla New York ışıklarını izledin.",
                        impactText = if (isSuppress) "Soğukkanlı zırhını korumaya devam ettin."
                                    else if (isFlashback) "Geçmişin yükünü kabullenerek kulede yeni bir sayfa açtın."
                                    else "Yıllar sonra ilk kez bir şeyleri kaybetmeden tutabileceğine inandın.",
                        icon = if (!isSuppress) "💫" else "💭",
                        isBranching = false,
                        isRelationship = false
                    )
                )
            }
        } else if (isChapter2) {
            // Chapter 2 Choices (Indices 4, 5, 6, 7, 8)
            if (userMsgs.size >= 5) {
                val c1Msg = userMsgs.getOrNull(4)?.text ?: ""
                val isGenerous = c1Msg.contains("3") || c1Msg.contains("fazlası") || c1Msg.contains("Cömert")
                val isRefuse = c1Msg.contains("2") || c1Msg.contains("Reddet") || c1Msg.contains("gösterme")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. GÜÇ GÖSTERİMİ STRATEJİSİ",
                        selectedOption = if (isGenerous) "Güç gösteriminde cömert davrandın ve katmanlı formlarından bahsettin."
                                        else if (isRefuse) "Güç gösterisini tamamen reddettin, sadece sözlerine güvenmelerini istedin."
                                        else "Küçük mavi ışık küresi oluşturdun ve temel sınırlarını açıkladın.",
                        impactText = if (isGenerous) "Tony Stark'ın ilgisini çektin ve bilimsel bir merak uyandırdın."
                                    else if (isRefuse) "Gizemin korundu ancak Steve Rogers ve Tony Stark'ın şüphesi bir nebze arttı."
                                    else "Güç gösterin kontrollü bulundu, Stark ve Rogers güçlerini gerçekçi şekilde değerlendirdi.",
                        icon = if (isGenerous) "💫" else if (isRefuse) "🔀" else "💭",
                        isBranching = isRefuse,
                        isRelationship = isGenerous
                    )
                )
            }

            if (userMsgs.size >= 6) {
                val c2Msg = userMsgs.getOrNull(5)?.text ?: ""
                val isSincere = c2Msg.contains("2") || c2Msg.contains("Ciddi") || c2Msg.contains("zor olduğunu")
                val isSilent = c2Msg.contains("3") || c2Msg.contains("Cevap verme") || c2Msg.contains("değiştir")
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. STEVE'İN GÜVEN SÖZÜNE TEPKİ",
                        selectedOption = if (isSincere) "Güvenin senin için neden bu kadar zor olduğunu dürüstçe açıkladın."
                                        else if (isSilent) "Cevap vermeyip konuyu değiştirdin."
                                        else "Güven kelimesini sorgulayarak alaycı bir üslup takındın.",
                        impactText = if (isSincere) "Steve Rogers'ın saygısını ve güvenini kazandın."
                                    else if (isSilent) "Steve Rogers ayrılırken temkinli ve mesafeli bir tavır sergiledi."
                                    else "Geri çekilmeden sınır çizdin, mesafeli duruşunu korudun.",
                        icon = if (isSincere) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isSincere
                    )
                )
            }

            if (userMsgs.size >= 7) {
                val c3Msg = userMsgs.getOrNull(6)?.text ?: ""
                val isTalk = c3Msg.contains("2") || c3Msg.contains("Sözlü") || c3Msg.contains("sakladığını")
                val isLeave = c3Msg.contains("3") || c3Msg.contains("Mesafe") || c3Msg.contains("çık")
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. NATASHA İLE İLİŞKİ TAVRI",
                        selectedOption = if (isTalk) "Natasha'ya sözlü karşılık verdin, onun geçmişini ima ettin."
                                        else if (isLeave) "Mesafe koyup odadan çıktın."
                                        else "Natasha'ya karşı sessiz kaldın, bakışların onda uzadı.",
                        impactText = if (isTalk) "Natasha ile güçlü ve doğrudan bir diyalog bağı kurdun."
                                    else if (isLeave) "Natasha ile arandaki mesafeyi korudun, gizemini muhafaza ettin."
                                    else "Gölgelerden gelen iki insan arasındaki sessiz anlayış derinleşti.",
                        icon = if (isTalk || !isLeave) "💫" else "💭",
                        isBranching = false,
                        isRelationship = !isLeave
                    )
                )
            }

            if (userMsgs.size >= 8) {
                val c4Msg = userMsgs.getOrNull(7)?.text ?: ""
                val isAccept = c4Msg.contains("2") || c4Msg.contains("Kabul") || c4Msg.contains("açıkça")
                val isReject = c4Msg.contains("3") || c4Msg.contains("Sert") || c4Msg.contains("kapat")
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. WANDA'NIN İÇGÖRÜSÜNE TEPKİ",
                        selectedOption = if (isAccept) "Kayıp hissettiğini Wanda'ya açıkça kabul ettin."
                                        else if (isReject) "Sert bir şekilde konuyu kapatıp geri çekildin."
                                        else "Wanda'ya zihin okuma şüphesiyle karşılık verdin.",
                        impactText = if (isAccept) "Wanda ile aranızda derin bir duygusal empati ve bağ kuruldu."
                                    else if (isReject) "Wanda ile arandaki duygusal teması sınırlandırdın."
                                    else "Wanda anlayışla gülümsedi ve seni içgörüyle dinlemeye devam etti.",
                        icon = if (isAccept) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isAccept || !isReject
                    )
                )
            }

            if (userMsgs.size >= 9) {
                val c5Msg = userMsgs.getOrNull(8)?.text ?: ""
                val isDoubt = c5Msg.contains("2") || c5Msg.contains("şüphe") || c5Msg.contains("kaçmayı")
                val isConflicted = c5Msg.contains("3") || c5Msg.contains("Belirsiz") || c5Msg.contains("karışık") || c5Msg.contains("çatışma")
                items.add(
                    ChapterChoiceItem(
                        id = 5,
                        title = "5. PENCEREDEKİ GECE KARARI",
                        selectedOption = if (isDoubt) "Pencerede kaçış rotalarını düşünmeye devam ettin."
                                        else if (isConflicted) "İç çatışma ve derin kararsızlıkla şehre baktın."
                                        else "Pencerede temkinli bir iyimserlikle New York'u izledin.",
                        impactText = if (isDoubt) "Şüpheci ve kaçış odaklı tutumunu korudun."
                                    else if (isConflicted) "Kararsızlığını korudun, zamanın ne getireceğini beklemeye karar verdin."
                                    else "Avengers'a karşı umut dolu, olumlu bir eğilim kazandın.",
                        icon = if (!isDoubt) "💫" else "🔀",
                        isBranching = isDoubt,
                        isRelationship = false
                    )
                )
            }
        } else {
            // Chapter 1 Choices
            if (userMsgs.isNotEmpty()) {
                val c1Msg = userMsgs.getOrNull(0)?.text ?: ""
                val isC1Deep = c1Msg.contains("odaklan") || c1Msg.contains("derinden") || c1Msg.contains("2")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. GÖRÜYE YAKLAŞIM",
                        selectedOption = if (isC1Deep) "Görüye derinden odaklandın." else "Görüden zihnini geri çektin ve mesafeli kaldın.",
                        impactText = if (isC1Deep) {
                            "Ekstra detaylar elde ettin ancak bu zihinsel yük Aiden'a fiziksel yorgunluk ve burun kanaması verdi."
                        } else {
                            "Bu seçim Aiden'ın gücünü tasarruflu kullanma ve zihnini yıpratmama kararlılığını yansıttı, hikayeyi doğrudan değiştirmedi."
                        },
                        icon = "💭",
                        isBranching = false,
                        isRelationship = false
                    )
                )
            }

            if (userMsgs.size >= 2) {
                val c2Msg = userMsgs.getOrNull(1)?.text ?: ""
                val isC2Avoid = c2Msg.contains("Geri çekil") || c2Msg.contains("gitme") || c2Msg.contains("3")
                val isC2Observe = c2Msg.contains("gözlemle") || c2Msg.contains("uzaktan") || c2Msg.contains("2")
                val c2Selected = if (isC2Avoid) {
                    "Kule'ye girmemeyi ve geri çekilmeyi tercih ettin."
                } else if (isC2Observe) {
                    "Kule'yi önce dışarıdan gözlemledin, sonra ışınlandın."
                } else {
                    "Kule'ye direkt ışınlandın."
                }
                val c2Impact = if (isC2Avoid) {
                    "Avengers Tower'dan uzak durmayı denedin ancak S.H.I.E.L.D. uyduları uzam sapmanı tespit etti ve zorunlu bir portal dalgasıyla seni kuleye getirdi."
                } else if (isC2Observe) {
                    "Hesaplı ve temkinli bir giriş yaptın; bu tavır Natasha Romanoff'un takdirini kazandı."
                } else {
                    "Aniden ortaya çıkman odadakilerin reflekslerini tetikledi ve daha dürtüsel bir izlenim bıraktın."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. KULE'YE GİRİŞ STRATEJİSİ",
                        selectedOption = c2Selected,
                        impactText = c2Impact,
                        icon = if (isC2Avoid) "🔀" else if (isC2Observe) "💫" else "💭",
                        isBranching = isC2Avoid,
                        isRelationship = isC2Observe
                    )
                )
            }

            if (userMsgs.size >= 3) {
                val c3Msg = userMsgs.getOrNull(2)?.text ?: ""
                val isC3Witty = c3Msg.contains("Alaycı") || c3Msg.contains("esprili") || c3Msg.contains("masraflı") || c3Msg.contains("2")
                val isC3Silent = c3Msg.contains("Sessizlik") || c3Msg.contains("Hiç isim") || c3Msg.contains("3")
                val c3Selected = if (isC3Witty) {
                    "Stark'a alaycı ve esprili bir yanıt verdin."
                } else if (isC3Silent) {
                    "Hiç isim vermeyip sessiz kaldın."
                } else {
                    "Soğuk ve mesafeli bir cevap verdin."
                }
                val c3Impact = if (isC3Witty) {
                    "Mizahi yaklaşımın Tony Stark'ın sempatisini kazandı ve ortamdaki gerilimi bir nebze düşürdü."
                } else if (isC3Silent) {
                    "Gizemli ve tekinsiz sessizliğin Steve Rogers'ın şüphesini daha da artırdı."
                } else {
                    "Bu seçim Aiden'ın zırhlarını koruma ve mesafeli durma tavrını yansıttı, hikayeyi doğrudan değiştirmedi."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. TONY STARK İLE DİYALOG",
                        selectedOption = c3Selected,
                        impactText = c3Impact,
                        icon = if (isC3Witty || isC3Silent) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isC3Witty || isC3Silent
                    )
                )
            }

            if (userMsgs.size >= 4) {
                val c4Msg = userMsgs.getOrNull(3)?.text ?: ""
                val isC4Natasha = c4Msg.contains("Natasha") || c4Msg.contains("kızıl") || c4Msg.contains("1")
                val isC4Wanda = c4Msg.contains("Wanda") || c4Msg.contains("pencere") || c4Msg.contains("2")
                val c4Selected = if (isC4Natasha) {
                    "Bakışların Natasha Romanoff'un üzerinde kaldı."
                } else if (isC4Wanda) {
                    "Bakışların Wanda Maximoff'un üzerinde kaldı."
                } else {
                    "Kimseye özel bakış atmayıp odayı genel olarak taradın."
                }
                val c4Impact = if (isC4Natasha) {
                    "Natasha Romanoff ile aranızda sessiz ama güçlü bir diyalog ve görünmez bir bağ kuruldu."
                } else if (isC4Wanda) {
                    "Wanda Maximoff zihinsel dalgalanmanı hissetti ve aranızda ince bir merak filizlendi."
                } else {
                    "Bu seçim Aiden'ın o anki nötr ve profesyonel tavrını yansıttı, hikayeyi doğrudan değiştirmedi."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. BAKIŞ ODAK NOKTASI",
                        selectedOption = c4Selected,
                        impactText = c4Impact,
                        icon = if (isC4Natasha || isC4Wanda) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isC4Natasha || isC4Wanda
                    )
                )
            }
        }

        items
    }

    val relationshipTease = remember(userMsgs, isChapter2, isChapter3, isChapter4, isChapter5, isChapter6, isChapter7) {
        if (isChapter7) {
            val c5Msg = userMsgs.getOrNull(38)?.text ?: ""
            val c7Msg = userMsgs.getOrNull(40)?.text ?: ""
            val natashaBond = c5Msg.contains("1") || c5Msg.contains("haklısın") || c5Msg.contains("düşünüyorum") || c5Msg.contains("ama")
            val wandaBond   = c7Msg.contains("1") || c7Msg.contains("inan") || c7Msg.contains("3") || c7Msg.contains("bekledi")
            when {
                natashaBond && wandaBond -> "Natasha ile çatıda geçirdiğin sessiz saatler ve Mira'ya uzattığın el — ikisi de aynı şeyi gösteriyor: artık gitmiyorsun."
                natashaBond -> "Natasha Romanoff o gece çatıda sana söyledi: 'Bu fark her şeydir.' Ve haklıydı."
                wandaBond   -> "Wanda zihninde merak gördü — korku değil. Ve Mira'ya uzattığın el, başkalarını da kurtarabileceğini gösterdi."
                else        -> "Yirmi yıllık kaçışın ardından, ilk kez bir yerde kalmayı seçtin. Bu, her şeyin başlangıcı."
            }
        } else if (isChapter6) {
            val endMsg = userMsgs.getOrNull(32)?.text ?: ""
            val isStay = endMsg.contains("2") || endMsg.contains("kalmak") || endMsg.contains("burada")
            val isVoss = endMsg.contains("3") || endMsg.contains("Voss") || endMsg.contains("kurtarmak")
            when {
                isStay -> "İlk kez yirmi yılda — elini tutan birinin elini geri bırakmak istemedin. Avengers artık görev değil, ev gibiydi."
                isVoss -> "Birisini kurtarmak için bir şey istedin — kaçmak için değil. Wanda'nın gözleri doldu. Bu fark her şeydi."
                else -> "Örgütü bulmak ve bitirmek için ekibin yanındasın. Sam: 'Hoş geldin Blackwood — gerçekten bu sefer.'"
            }
        } else if (isChapter5) {
            val sorgulama = userMsgs.getOrNull(26)?.text ?: ""
            val isEnter = sorgulama.contains("1") || sorgulama.contains("ben") || sorgulama.contains("gireceğim")
            val isWanda = sorgulama.contains("3") || sorgulama.contains("Wanda")
            when {
                isEnter -> "Voss ile yüzleştin: 'İkimiz de aynı şeyiz. Sadece farklı ellerde.' Geçmişin yansımasında kendi seçimlerinin ağırlığını hissettin."
                isWanda -> "Wanda Voss'un zihninden seni uyarmak için taşıdığı feneri gördü. Aranızdaki empati bağı daha da derinleşti."
                else -> "Natasha cam arkasından seni gözlemledi: 'Bir noktada karar değiştirdi. Belki sen biliyorsundur.'"
            }
        } else if (isChapter4) {
            val roleMsg = userMsgs.getOrNull(21)?.text ?: ""
            val isSync = roleMsg.contains("2") || roleMsg.contains("Natasha") || roleMsg.contains("eş zamanlı")
            when {
                isSync -> "Natasha ile adımların eşleşti — bilinçsiz bir senkronizasyon. İlk gerçek operasyonda yan yana durdunuz."
                else -> "İlk operasyonu tamamladın. Kaçmak yerine kaldın, savaştın ve geri döndün. Sovyet dosyası hâlâ yanıt bekliyordu."
            }
        } else if (isChapter3) {
            "Otuz günlük deneme süresinde Avengers Tower'da yerini almaya başladın. Steve ile karşılıklı saygı, Natasha ile derin geçmiş anlayışı ve Wanda ile duygusal empati geliştirdin."
        } else if (isChapter2) {
            val c3Msg = userMsgs.getOrNull(6)?.text ?: ""
            val c4Msg = userMsgs.getOrNull(7)?.text ?: ""
            val natashaBond = c3Msg.contains("1") || c3Msg.contains("2") || c3Msg.contains("Sözlü") || c3Msg.contains("Sessiz")
            val wandaBond = c4Msg.contains("1") || c4Msg.contains("2") || c4Msg.contains("Kabul") || c4Msg.contains("zihin")
            when {
                natashaBond && wandaBond -> "Natasha ve Wanda ile derin bağlar kurdun. Kule'de hem gölgelerin anlayışını hem de duygusal sığınağı hissettin."
                natashaBond -> "Natasha Romanoff ile aranızdaki gizli bağ güçlendi. Gölgelerden gelen iki ajanın kaderi kesişiyor."
                wandaBond -> "Wanda Maximoff ile aranızda empati filizlendi. Geçmişin acılarını taşıyan iki güç sahibi birbirini anlıyor."
                else -> "Mesafeli duruşunu korudun; Avengers ekibi senin duvarlarını aşmak için zaman kolluyor."
            }
        } else {
            val c4Msg = userMsgs.getOrNull(3)?.text ?: ""
            when {
                c4Msg.contains("Natasha") || c4Msg.contains("kızıl") || c4Msg.contains("1") ->
                    "Natasha Romanoff ile aranızda özel bir bağ kuruldu. İlerleyen bölümlerde sana daha fazla güvenebilir."
                c4Msg.contains("Wanda") || c4Msg.contains("pencere") || c4Msg.contains("2") ->
                    "Wanda Maximoff'un ilgisini çektin. Sonraki bölümlerde zihinsel bağınız derinleşebilir."
                else -> null
            }
        }
    }

    // Auto Step Controller for Sequential Reveal (Life is Strange Pacing: Adım A ~1.8s, Adım B ~7.5s - 2.7s extra reading time)
    LaunchedEffect(viewMode, currentIndex) {
        if (viewMode == SummaryViewMode.SEQUENTIAL && choices.isNotEmpty()) {
            stepPhase = StepPhase.CARD_ONLY
            delay(1800) // ADIM A duration: ~1.8s (Breath & Read Action)
            if (viewMode == SummaryViewMode.SEQUENTIAL) {
                stepPhase = StepPhase.RESULT_REVEALED // ADIM B (Thread & Impact Reveal)
                delay(7500) // ADIM B duration: ~7.5s (2.7s extra time for deep impact reading)
                if (viewMode == SummaryViewMode.SEQUENTIAL) {
                    if (currentIndex < choices.lastIndex) {
                        currentIndex++
                    } else {
                        // Transition to Final Memory Thread after last choice
                        viewMode = SummaryViewMode.FINAL_GRID
                    }
                }
            }
        }
    }

    // FULL-SCREEN DIALOG WITH DARK CINEMATIC ATMOSPHERE & FADING AUDIO
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { safeExitWithFade(onDismiss) },
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        val currentChoice = choices.getOrNull(currentIndex)
        val characterAccentColor = remember(currentChoice) {
            if (currentChoice != null) {
                val text = (currentChoice.selectedOption + " " + currentChoice.impactText + " " + currentChoice.title).lowercase()
                when {
                    text.contains("natasha") || text.contains("romanoff") || text.contains("kızıl") || text.contains("red") -> AppColors.ErrorRed
                    text.contains("wanda") || text.contains("scarlet") || text.contains("cadı") || text.contains("witch") -> AppColors.Rose
                    text.contains("tony") || text.contains("stark") || text.contains("zırh") || text.contains("iron") -> AppColors.WarningAmber
                    text.contains("steve") || text.contains("rogers") || text.contains("kaptan") || text.contains("captain") -> AppColors.Blue
                    else -> AppColors.DeepPurple
                }
            } else {
                AppColors.DeepPurple
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppColors.Hex_070814
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Cosmic Nebula & Character Specific Color Accent & Drifting Star Dust
                CosmicParticleBackground(accentColor = characterAccentColor)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                ) {
                    // TOP BAR: Minimal Dots Indicator & Close Button (Fixed Top Layer)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewMode == SummaryViewMode.SEQUENTIAL && choices.isNotEmpty()) {
                            // Minimal Dots Progress Indicator with Pulse & Smooth Fill (Rule 7)
                            val infiniteTransition = rememberInfiniteTransition(label = "ActiveDotPulse")
                            val activeDotScale by infiniteTransition.animateFloat(
                                initialValue = 1.0f,
                                targetValue = 1.18f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "DotPulse"
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                choices.forEachIndexed { idx, _ ->
                                    val isActive = idx == currentIndex
                                    val isPast = idx < currentIndex
                                    val targetDotSize = if (isActive) 11.dp else 6.dp
                                    val targetDotColor = when {
                                        isActive -> AppColors.PurpleSoft
                                        isPast -> AppColors.DeepPurple
                                        else -> Color(0x3594A3B8)
                                    }
                                    val animSize by androidx.compose.animation.core.animateDpAsState(
                                        targetValue = targetDotSize,
                                        animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                        label = "DotSizeAnim"
                                    )
                                    val animColor by androidx.compose.animation.animateColorAsState(
                                        targetValue = targetDotColor,
                                        animationSpec = tween(300),
                                        label = "DotColorAnim"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(animSize)
                                            .graphicsLayer {
                                                if (isActive) {
                                                    scaleX = activeDotScale
                                                    scaleY = activeDotScale
                                                }
                                            }
                                            .clip(CircleShape)
                                            .background(animColor)
                                    )
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎬", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (viewMode == SummaryViewMode.PROMPT) "BÖLÜM $completedChapterNumber SEÇİM ÖZETİ" else "HAFIZA ŞERİDİ",
                                    color = AppColors.PurpleSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Close (X) Button
                        IconButton(
                            onClick = { safeExitWithFade(onDismiss) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.Hex_231B3D)
                                .border(1.dp, Color(0x60A78BFA), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // MAIN CONTENT AREA (PERFECTLY CENTERED VERTICALLY AND HORIZONTALLY)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        when (viewMode) {
                            // ==================== INITIAL PROMPT SCREEN ====================
                            SummaryViewMode.PROMPT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // ── "SENİN HİKAYEN" KARAKTER KARTI ──────────────────────────
                                    val heroCardInfinite = rememberInfiniteTransition(label = "heroCard")
                                    val heroGlow by heroCardInfinite.animateFloat(
                                        0.5f, 1f,
                                        infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "hg"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF0F0A20), Color(0xFF1A0D35), Color(0xFF0D0A1F))
                                                )
                                            )
                                            .border(
                                                1.5.dp,
                                                Brush.linearGradient(
                                                    listOf(
                                                        AppColors.PrimaryPurple.copy(alpha = heroGlow),
                                                        AppColors.DeepPurple.copy(alpha = heroGlow * 0.6f),
                                                        AppColors.PrimaryPurple.copy(alpha = heroGlow)
                                                    )
                                                ),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            // Karakter avatarı
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(
                                                        Brush.radialGradient(
                                                            listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A))
                                                        )
                                                    )
                                                    .border(1.dp, AppColors.PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("⚡", fontSize = 26.sp)
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        "Aiden Blackwood",
                                                        color = Color.White,
                                                        fontSize = 15.sp,
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(5.dp))
                                                            .background(AppColors.DeepPurple.copy(alpha = 0.4f))
                                                            .border(0.5.dp, AppColors.PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            "Bölüm $completedChapterNumber",
                                                            color = AppColors.PurpleSoft,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = when (completedChapterNumber) {
                                                        6 -> "Geçmişiyle yüzleşti · Seçimi yaptı"
                                                        5 -> "Kırmızı Koruyucu ile karşılaştı · Sorgulama"
                                                        4 -> "İlk operasyon tamamlandı · Takıma katıldı"
                                                        3 -> "Deneme süresi bitti · Güven kazanıldı"
                                                        2 -> "Şartlar müzakere edildi · Bağlar kuruldu"
                                                        else -> "MCU evrenine adım attı · Avengers ile tanıştı"
                                                    },
                                                    color = AppColors.TextMuted,
                                                    fontSize = 11.sp,
                                                    fontFamily = InterFontFamily,
                                                    lineHeight = 15.sp
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                // Mini istatistik satırı
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    listOf(
                                                        Pair("${choices.size}", "Karar"),
                                                        Pair(if (choices.any { it.isBranching }) "Evet" else "—", "Dal"),
                                                        Pair(if (choices.any { it.isRelationship }) "Güçlü" else "Zayıf", "Bağ")
                                                    ).forEach { (v, l) ->
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text(v, color = AppColors.PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                                            Text(l, color = AppColors.TextMuted, fontSize = 9.sp)
                                                        }
                                                        if (l != "Bağ") {
                                                            Box(modifier = Modifier.width(1.dp).height(22.dp).background(Color(0xFF2A1D4E)).align(Alignment.CenterVertically))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text(
                                        text = "Bu bölümdeki seçimlerini görmek ister misin?",
                                        color = Color.White,
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 29.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Verdiğin kararlar ve hikayeye olan etkileri Life is Strange tarzı sinematik reveal akışıyla sunulacak.",
                                        color = AppColors.TextMuted,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        lineHeight = 19.sp
                                    )

                                    Spacer(modifier = Modifier.height(28.dp))

                                    Button(
                                        onClick = {
                                            currentIndex = 0
                                            stepPhase = StepPhase.CARD_ONLY
                                            viewMode = SummaryViewMode.SEQUENTIAL
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepPurple),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("✨ Evet, Sırayla Göster", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    OutlinedButton(
                                        onClick = { viewMode = SummaryViewMode.FINAL_GRID },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextSecondary),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_334155),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    ) {
                                        Text("⚡ Atla (Direkt Hafıza Şeridi)", fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                                    }
                                }
                            }

                            // ==================== SEQUENTIAL CINEMATIC REVEAL ====================
                            SummaryViewMode.SEQUENTIAL -> {
                                if (currentChoice != null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CinematicSequentialReveal(
                                            choice = currentChoice,
                                            phase = stepPhase
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF1E0D3A), Color(0xFF130828))))
                                                .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))), RoundedCornerShape(10.dp))
                                                .clickable { viewMode = SummaryViewMode.FINAL_GRID }
                                                .padding(horizontal = 14.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "⚡ Tümünü Göster / Atla",
                                                color = Color(0xB3A78BFA),
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // ==================== FINAL MEMORY THREAD TIMELINE ====================
                            SummaryViewMode.FINAL_GRID -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // ── FINAL GRID BAŞLIK ALANI ──────────────────────────────────
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A)))),
                                            contentAlignment = Alignment.Center
                                        ) { Text("🎬", fontSize = 20.sp) }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Kaderin Halkaları",
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                "Bölüm $completedChapterNumber · ${choices.size} karar · ${choices.count { it.isBranching }} dal",
                                                color = AppColors.TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                        // Tüm kararlar tamamlandı rozeti
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF022C22))
                                                .border(1.dp, Color(0xFF059669).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text("✓ TAM", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Verdiğin kararların zaman akışında bıraktığı silinmez izler:",
                                        color = AppColors.TextMuted,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    MemoryThreadTimeline(
                                        choices = choices,
                                        relationshipTease = relationshipTease
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            safeExitWithFade {
                                                if (isChapter6) {
                                                    android.widget.Toast.makeText(context, "Bölüm 7 yakında yayınlanacak! Takipte kal.", android.widget.Toast.LENGTH_LONG).show()
                                                } else {
                                                    onDismiss()
                                                    onNextChapter?.invoke()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepPurple),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                when {
                                                isChapter6 -> "✨ Bölüm 6 Tamamlandı (Bölüm 7 Yakında)"
                                                isChapter5 -> "🚀 Bölüm 6'ya Geç"
                                                isChapter4 -> "🚀 Bölüm 5'e Geç"
                                                isChapter3 -> "🚀 Bölüm 4'e Geç"
                                                isChapter2 -> "🚀 Bölüm 3'e Geç"
                                                else -> "🚀 Bölüm 2'ye Geç"
                                            },
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedButton(
                                        onClick = {
                                            safeExitWithFade {
                                                onDismiss()
                                                onRestartChapter()
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.PrimaryPurple),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_4C1D95),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    ) {
                                        Text(
                                            when {
                                                isChapter6 -> "🔄 Bölüm 6'yı Tekrar Oyna"
                                                isChapter5 -> "🔄 Bölüm 5'i Tekrar Oyna"
                                                isChapter4 -> "🔄 Bölüm 4'ü Tekrar Oyna"
                                                isChapter3 -> "🔄 Bölüm 3'ü Tekrar Oyna"
                                                isChapter2 -> "🔄 Bölüm 2'yi Tekrar Oyna"
                                                else -> "🔄 Bölüm 1'i Tekrar Oyna"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            safeExitWithFade { onDismiss() }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Hex_1E1B38),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_382F5E),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = AppColors.TextSecondary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Özeti Kapat",
                                                color = AppColors.TextSecondary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
