
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // ── META SATIRI: puan · bölüm · süre · seçim ──
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Puan rozeti
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1A0F3B))
                                    .padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("⭐", fontSize = 11.sp)
                                Text("4.9", color = Color(0xFFFDE047), fontSize = 11.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Bold)
                            }
                            Text("·", color = Color(0xFF4A3A6A), fontSize = 14.sp)
                            Text(
                                if (isEnglish) "6 Chapters" else "6 Bölüm",
                                color = Color(0xFF9B89CC), fontSize = 11.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium
                            )
                            Text("·", color = Color(0xFF4A3A6A), fontSize = 14.sp)
                            Text(
                                if (isEnglish) "~45 min" else "~45 dk",
                                color = Color(0xFF9B89CC), fontSize = 11.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium
                            )
                            Text("·", color = Color(0xFF4A3A6A), fontSize = 14.sp)
                            Text(
                                if (isEnglish) "180+ Choices" else "180+ Seçim",
                                color = Color(0xFF9B89CC), fontSize = 11.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                text = if (isEnglish) "Chapter $displayChapter / 6" else "Bölüm $displayChapter / 6",
                                fontFamily = InterFontFamily,
                                color = Color(0xFFC084FC),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Continuous progress bar with glowing 1/6 purple fill
                        val progressFraction = (displayChapter.coerceIn(1, 6)) / 6f
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // ── KARAKTER ŞERİDİ ──────────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (isEnglish) "Cast" else "Karakterler",
                                color = Color(0xFF6B5E8A),
                                fontSize = 11.sp,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                val castList = listOf(
                                    Triple("🛡️", "Steve",   Color(0xFF3B82F6)),
                                    Triple("🕷️", "Natasha", Color(0xFFEF4444)),
                                    Triple("🔮", "Wanda",   Color(0xFFEC4899)),
                                    Triple("🦅", "Sam",     Color(0xFF10B981)),
                                    Triple("⚡", "Tony",    Color(0xFFF59E0B))
                                )
                                castList.forEach { (emoji, _, color) ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.18f))
                                            .border(1.5.dp, color.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                    text = when {
                                        displayChapter >= 7 -> if (isEnglish) "Chapter 8 Coming Soon" else "Bölüm 8 Yakında"
                                        displayChapter >= 6 -> if (isEnglish) "Chapter 7: Dark Mirror" else "Bölüm 7: Karanlık Ayna"
                                        displayChapter > 1  -> if (isEnglish) "Continue · Ch. $displayChapter" else "Devam Et · Bölüm $displayChapter"
                                        else                -> if (isEnglish) "Start Book" else "Kitaba Başla"
                                    },
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
            openDesc = if (isEnglish) "30-day trial period in Avengers Tower — training, trust, and the first bond." else "Avengers Kulesi'nde 30 günlük deneme süresi — antrenman, güven ve ilk bağlar.",
            startMsg = "Bölüm 3: Deneme Süresi'ne başla"
        ),
        BookChapterInfo(
            index = 4,
            openTitle = if (isEnglish) "Chapter 4: First Mission" else "Bölüm 4: İlk Görev",
            openDesc = if (isEnglish) "Aiden's first official HYDRA operation — and an unexpected discovery at Manhattan Harbor." else "Aiden'ın ilk resmi HYDRA operasyonu — ve Manhattan Limanı'nda beklenmedik bir keşif.",
            startMsg = "Bölüm 4: İlk Görev'e başla"
        ),
        BookChapterInfo(
            index = 5,
            openTitle = if (isEnglish) "Chapter 5: Red Ice" else "Bölüm 5: Kırmızı Buz",
            openDesc = if (isEnglish) "The Red Guardian — a mirror from the past. Who is Elias Voss and why was he sent to find you?" else "Kırmızı Koruyucu — geçmişten bir ayna. Elias Voss kim ve neden seni bulmak için gönderildi?",
            startMsg = "Bölüm 5: Kırmızı Buz'a başla"
        ),
        BookChapterInfo(
            index = 6,
            openTitle = if (isEnglish) "Chapter 6: Broken Mirror" else "Bölüm 6: Kırık Ayna",
            openDesc = if (isEnglish) "The Istanbul archive reveals: Aiden was never a fugitive — he was a project. And Project 7-A is not over." else "İstanbul arşivi ortaya koyuyor: Aiden hiçbir zaman bir kaçak değildi — o bir projeydi. Ve Proje 7-A bitmedi.",
            startMsg = "Bölüm 6: Kırık Ayna'ya başla"
        ),
        BookChapterInfo(
            index = 7,
            openTitle = if (isEnglish) "Chapter 7: Dark Mirror" else "Bölüm 7: Karanlık Ayna",
            openDesc = if (isEnglish) "Dr. Hale surrenders. Aiden's parents are found. And a Red Eye carrier waits in the Istanbul tunnels." else "Dr. Hale teslim oluyor. Aiden'ın ailesi bulunuyor. Ve İstanbul tünellerinde bir Kırmızı Göz taşıyıcısı bekliyor.",
            startMsg = "Bölüm 7: Karanlık Ayna'ya başla"
        ),
        BookChapterInfo(
            index = 8,
            openTitle = if (isEnglish) "Chapter 8: Coming Soon" else "Bölüm 8: Yakında",
            openDesc = if (isEnglish) "The next chapter is in development. Stay tuned." else "Sonraki bölüm yapım aşamasında. Takipte kal.",
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
                        // Mini progress dots (6 bölüm)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(6) { i ->
                                Box(modifier = Modifier.size(if (i < progress) 8.dp else 6.dp).clip(CircleShape)
                                    .background(if (i < progress) Color(0xFF34D399) else Color(0xFF2D1F50)))
                            }
                        }
                        Text(when {
                            progress >= 6 -> "Bölüm 6 Tamamlandı 🎉"
                            progress >= 5 -> "Bölüm 5 Tamamlandı • Bölüm 6 Açık"
                            progress >= 4 -> "Bölüm 4 Tamamlandı • Bölüm 5 Açık"
                            progress >= 3 -> "Bölüm 3 Tamamlandı • Bölüm 4 Açık"
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
                    // Her bölümün renk bandı
                    val chapterBandColors = listOf(
                        Color(0xFF7C3AED), // 1 — mor
                        Color(0xFF2563EB), // 2 — mavi
                        Color(0xFF0E7490), // 3 — teal
                        Color(0xFFB45309), // 4 — amber
                        Color(0xFFBE123C), // 5 — kırmızı
                        Color(0xFF059669), // 6 — yeşil
                        Color(0xFF6D28D9), // 7 — derin mor
                        Color(0xFF374151)  // 8 — gri (yakında)
                    )
                    val chapterDurations = listOf("~6 dk", "~7 dk", "~8 dk", "~8 dk", "~7 dk", "~9 dk", "~11 dk", "—")

                    val comingSoonInfinite = rememberInfiniteTransition(label = "cs7")
                    val comingSoonGlow by comingSoonInfinite.animateFloat(
                        0.4f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "csg"
                    )

                    chapters.forEach { item ->
                        val isUnlocked = item.index <= (progress + 1) && item.index <= 7
                        val isCompleted = item.index <= progress
                        val isCurrent = (progress + 1) == item.index || (progress >= 6 && item.index == 6)
                        val isComingSoon = item.index == 8
                        val bandColor = chapterBandColors.getOrElse(item.index - 1) { Color(0xFF7C3AED) }
                        val duration = chapterDurations.getOrElse(item.index - 1) { "—" }

                        val itemScale = remember(item.index) { androidx.compose.animation.core.Animatable(1f) }
                        val itemScope = rememberCoroutineScope()

                        Box(modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { scaleX = itemScale.value; scaleY = itemScale.value }
                            .clip(RoundedCornerShape(14.dp))
                            .background(when {
                                isComingSoon -> Brush.linearGradient(listOf(Color(0xFF0A0618), Color(0xFF0D0820)))
                                isCurrent -> Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF120D2A)))
                                isCompleted -> Brush.linearGradient(listOf(Color(0xFF0A1810), Color(0xFF080F0A)))
                                isUnlocked -> Brush.linearGradient(listOf(Color(0xFF120D24), Color(0xFF0A0818)))
                                else -> Brush.linearGradient(listOf(Color(0xFF080612), Color(0xFF080612)))
                            })
                            .border(1.dp, when {
                                isComingSoon -> Brush.linearGradient(listOf(
                                    bandColor.copy(alpha = comingSoonGlow * 0.6f),
                                    bandColor.copy(alpha = comingSoonGlow * 0.3f)
                                ))
                                isCurrent -> Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)))
                                isCompleted -> Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                                isUnlocked -> Brush.linearGradient(listOf(Color(0xFF3D2D60), Color(0xFF2D1F50)))
                                else -> Brush.linearGradient(listOf(Color(0xFF1A1535), Color(0xFF1A1535)))
                            }, RoundedCornerShape(14.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                when {
                                    isComingSoon -> android.widget.Toast.makeText(context,
                                        "Bölüm 8 yakında yayınlanacak! Takipte kal.", android.widget.Toast.LENGTH_SHORT).show()
                                    isUnlocked -> {
                                        itemScope.launch { itemScale.animateTo(0.96f, tween(80)); itemScale.animateTo(1f, tween(120)) }
                                        onSelectChapter(item.index, item.startMsg)
                                    }
                                    else -> android.widget.Toast.makeText(context,
                                        "Önce önceki bölümü tamamla!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Sol renk bandı
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(72.dp)
                                        .background(
                                            if (isUnlocked || isComingSoon)
                                                Brush.verticalGradient(listOf(bandColor, bandColor.copy(alpha = 0.3f)))
                                            else
                                                Brush.verticalGradient(listOf(Color(0xFF2A1D4E), Color(0xFF1A1535)))
                                        )
                                )
                                Row(
                                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Sol ikon
                                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                        .background(when {
                                            isComingSoon -> Brush.radialGradient(listOf(bandColor.copy(alpha = 0.15f), Color(0xFF0A0618)))
                                            isCompleted -> Brush.radialGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                                            isCurrent -> Brush.radialGradient(listOf(bandColor.copy(alpha = 0.8f), bandColor.copy(alpha = 0.4f)))
                                            isUnlocked -> Brush.radialGradient(listOf(bandColor.copy(alpha = 0.4f), Color(0xFF120D24)))
                                            else -> Brush.radialGradient(listOf(Color(0xFF1A1535), Color(0xFF110E24)))
                                        }), contentAlignment = Alignment.Center) {
                                        when {
                                            isComingSoon -> Text("🔒", fontSize = 16.sp)
                                            !isUnlocked  -> Icon(Icons.Default.Lock, null, tint = Color(0xFF3D2D60), modifier = Modifier.size(18.dp))
                                            isCompleted  -> Text("✓", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                            else         -> Text("${item.index}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = when {
                                                    isComingSoon -> item.openTitle
                                                    isUnlocked   -> item.openTitle
                                                    else         -> "Bölüm ${item.index}  🔒"
                                                },
                                                color = when {
                                                    isComingSoon -> Color(0xFF4A3A6A)
                                                    isUnlocked   -> Color.White
                                                    else         -> Color(0xFF3D2D60)
                                                },
                                                fontSize = 12.5.sp, fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f, fill = false),
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            if (isCurrent) Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                                .background(bandColor.copy(alpha = 0.25f))
                                                .border(0.5.dp, bandColor.copy(alpha = 0.6f), RoundedCornerShape(5.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)) {
                                                Text("▶ AKTİF", color = bandColor, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                                            }
                                            if (isCompleted && !isCurrent) Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                                .background(Color(0xFF022C22))
                                                .border(0.5.dp, Color(0xFF059669).copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)) {
                                                Text("✓ BİTİRİLDİ", color = Color(0xFF34D399), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                            }
                                            if (isComingSoon) Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                                .background(bandColor.copy(alpha = comingSoonGlow * 0.2f))
                                                .border(0.5.dp, bandColor.copy(alpha = comingSoonGlow * 0.7f), RoundedCornerShape(5.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)) {
                                                Text("🔥 YAKINDA", color = bandColor.copy(alpha = comingSoonGlow), fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = when {
                                                    isComingSoon -> "Çok yakında yayınlanacak..."
                                                    isUnlocked   -> item.openDesc
                                                    else         -> "Önceki bölümü tamamla."
                                                },
                                                color = when {
                                                    isUnlocked && !isComingSoon -> Color(0xFF8B7CB0)
                                                    else -> Color(0xFF2D2048)
                                                },
                                                fontSize = 10.5.sp, lineHeight = 14.5.sp,
                                                maxLines = 2,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                        }
                                        if (isUnlocked && !isComingSoon) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                                    .background(bandColor.copy(alpha = 0.12f))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)) {
                                                    Text("⏱ $duration", color = bandColor.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                                }
                                                if (isCompleted) Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF1E1535))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)) {
                                                    Text("Tekrar oyna", color = Color(0xFF6B5E8A), fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                    Icon(
                                        if (isUnlocked && !isComingSoon) Icons.Default.ChevronRight else Icons.Default.Lock,
                                        null,
                                        tint = if (isUnlocked && !isComingSoon) bandColor.copy(alpha = 0.7f) else Color(0xFF2D1F50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
