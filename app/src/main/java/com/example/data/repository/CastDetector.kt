package com.example.data.repository

import android.content.Context
import org.json.JSONArray
import java.util.Locale

/**
 * Cast Member Detection & Alias Resolution Engine (Otomatik Karakter Algılama).
 * Features:
 * - Turkish morphological suffix stemming (Ayla'nın -> Ayla, Kerem'e -> Kerem)
 * - Alias normalization (Doktor Kerem, Kerem, Kerem Bey -> single entity)
 * - JSON-backed blacklist with in-memory caching
 * - Code-based heuristic scoring (appearance count, dialogue share, speech cues)
 * - Active in-scene character filtering for token optimization
 */
object CastDetectorEngine {

    private val DEFAULT_FALLBACK_BLACKLIST = setOf(
        "bugün", "yarın", "dün", "sabah", "akşam", "gece", "gündüz", "şimdi", "sonra", "önce", "burada", "orada", "şurada",
        "çünkü", "sadece", "oysa", "lakin", "fakat", "ayrıca", "özellikle", "nitekim", "ancak", "belki", "böyle", "şöyle",
        "öyle", "peki", "tamam", "pekala", "lütfen", "efendim", "kullanıcı", "karakter", "insan", "adam", "kadın", "çocuk",
        "genç", "yaşlı", "polis", "garson", "hoca", "öğretmen", "müşteri", "sürücü", "asker", "kral", "kraliçe",
        "prens", "prenses", "profesör", "amiral", "komutan", "yüzbaşı", "kaptan", "istanbul", "ankara", "izmir", "türkiye",
        "dünya", "güneş", "ay", "yıldız", "sokak", "cadde", "oda", "masa", "sandalye", "kapı", "pencere", "araba", "telefon",
        "kitap", "kalem", "kılıç", "bıçak", "kalkan", "ateş", "su", "toprak", "hava", "ruh", "ışık", "karanlık", "ağaç",
        "orman", "deniz", "göl", "nehir", "dağ", "taş", "kaya", "ev", "saray", "şato", "kule", "zindan", "okul", "hastane",
        "otel", "meyhane", "restoran", "kafe", "dükkan", "pazar", "şehir", "köy", "ülke", "krallık", "imparatorluk",
        "lonca", "birlik", "takım", "grup", "sürü", "ordu", "donanma", "meclis", "pazartesi", "salı", "çarşamba", "perşembe",
        "cuma", "cumartesi", "pazar", "ocak", "şubat", "mart", "nisan", "mayıs", "haziran", "temmuz", "ağustos", "eylül",
        "ekim", "kasım", "aralık", "evvel", "ahir", "üstelik", "sanki", "hatta", "madem", "meğer", "güya", "zira", "aksi",
        "yine", "yeniden", "hemen", "derhal", "biraz", "çok", "fazla", "az", "hiç", "tüm", "bütün", "her", "kendi", "biri",
        "diğeri", "başka", "hangi", "nasıl", "neden", "niçin", "nere", "nerede", "nereden", "nereye", "kim", "kime", "kimden",
        "kimi", "gözü", "gözleri", "sesi", "yüzü", "adımları", "elleri", "bakışı", "dudakları", "başını", "elini", "arkası",
        "zorba", "soğuk", "sıcak", "büyük", "küçük", "uzun", "kısa", "sessiz", "hızlı", "yavaş", "güçlü", "zayıf",
        "evet", "hayır", "işte", "galiba", "sanırım", "bence", "sence", "zaten", "aslında", "gerçekten", "sonunda"
    )

    private val GENERIC_EXTRAS_BLACKLIST = setOf(
        "garson", "zorba", "zorba çocuk", "çocuk", "adam", "kadın", "polis", "müşteri",
        "yoldan geçen", "şoför", "taksici", "nöbetçi", "biri", "herkes", "hizmetli", "tezgahtar"
    )

    private val HONORIFICS = listOf(
        "Bey", "Hanım", "Efendi", "Ağa", "Usta", "Abi", "Abla", "Komutan",
        "Kaptan", "Doktor", "Hazretleri", "Paşa", "Hoca", "Teyze", "Amca", "Dayı", "Yenge", "Dede", "Peder"
    )

    private val TITLES = listOf(
        "Bay", "Bayan", "Doktor", "Prof", "Profesör", "Dr.", "Yüzbaşı", "Kaptan", "Amiral",
        "Komutan", "Lord", "Prens", "Prenses", "Kral", "Kraliçe", "Usta", "Hoca", "Savaşçı",
        "Büyücü", "Şövalye", "Mimar", "Aziz", "Üstat", "Gözcü", "Dedektif", "Ajan", "Müfettiş",
        "Savcı", "Hâkim", "Teğmen", "Binbaşı", "Albay", "General", "Sultan", "Paşa", "Dük", "Kont",
        "Baron", "Barones", "Rahip", "Cadı", "Gölge", "Muhafız", "Hizmetkar", "Tüccar", "Eczacı", "Leydi"
    )

    private val DIALOGUE_ACTION_VERBS = listOf(
        "dedi", "sordu", "fısıldadı", "bağırdı", "haykırdı", "mırıldandı",
        "yanıtladı", "ekledi", "güldü", "sırıttı", "iç geçirdi", "kaşlarını çattı",
        "omuz silkti", "seslendi", "atıldı", "cevap verdi"
    )

    @Volatile
    private var cachedBlacklist: Set<String>? = null

    fun getBlacklist(context: Context? = null): Set<String> {
        cachedBlacklist?.let { return it }
        if (context == null) return DEFAULT_FALLBACK_BLACKLIST

        return try {
            val jsonStr = context.assets.open("cast_blacklist.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(jsonStr)
            val set = mutableSetOf<String>()
            for (i in 0 until arr.length()) {
                val item = arr.optString(i)?.trim()?.lowercase(Locale("tr", "TR"))
                if (!item.isNullOrBlank()) set.add(item)
            }
            cachedBlacklist = set
            set
        } catch (_: Exception) {
            DEFAULT_FALLBACK_BLACKLIST
        }
    }

    /**
     * Stems Turkish proper names by removing apostrophes and inflectional suffixes.
     * e.g.:
     *   "Ayla'nın" -> "Ayla"
     *   "Kerem'e" -> "Kerem"
     *   "Deniz'den" -> "Deniz"
     *   "Ahmet'te" -> "Ahmet"
     *   "Mehmet'i" -> "Mehmet"
     */
    fun stemTurkishProperName(rawName: String): String {
        var clean = rawName.trim().removeSurrounding("\"").removeSurrounding("'")
        if (clean.isBlank()) return ""

        // If apostrophe exists (e.g. Ayla'nın, Kerem'e)
        if (clean.contains("'") || clean.contains("’") || clean.contains("`")) {
            val base = clean.split(Regex("""['’`]"""))[0].trim()
            if (base.length >= 2) return base
        }

        // Without apostrophe inflection endings
        val lower = clean.lowercase(Locale("tr", "TR"))
        val suffixes = listOf(
            // Genitive
            "nin", "nın", "nün", "nun", "in", "ın", "ün", "un",
            // Dative
            "ye", "ya", "e", "a",
            // Locative
            "de", "da", "te", "ta",
            // Ablative
            "den", "dan", "ten", "tan",
            // Accusative
            "yi", "yı", "yu", "yü", "i", "ı", "u", "ü",
            // Instrumental
            "yle", "yla", "le", "la"
        )

        for (sfx in suffixes) {
            if (lower.endsWith(sfx) && lower.length - sfx.length >= 3) {
                val potentialRoot = clean.substring(0, clean.length - sfx.length)
                if (potentialRoot.length >= 3) {
                    return potentialRoot
                }
            }
        }

        return clean
    }

    /**
     * Normalizes aliases such as "Doktor Kerem", "Kerem Bey", "Kerem" to root candidate name.
     */
    fun extractCanonicalNameAndRole(rawCandidate: String): Pair<String, String?> {
        val trimmed = rawCandidate.trim()
        var roleFromTitle: String? = null

        // Check Prefix Title: e.g. "Doktor Kerem"
        for (title in TITLES) {
            if (trimmed.startsWith("$title ", ignoreCase = true)) {
                val root = trimmed.substring(title.length).trim()
                roleFromTitle = if (title.contains("Doktor", ignoreCase = true) || title.contains("Dr.", ignoreCase = true)) "Doktor"
                else if (title.contains("Komutan", ignoreCase = true) || title.contains("Kaptan", ignoreCase = true)) "Komutan"
                else title
                return Pair(stemTurkishProperName(root), roleFromTitle)
            }
        }

        // Check Suffix Honorific: e.g. "Kerem Bey"
        for (hon in HONORIFICS) {
            if (trimmed.endsWith(" $hon", ignoreCase = true)) {
                val root = trimmed.substring(0, trimmed.length - hon.length).trim()
                roleFromTitle = if (hon == "Doktor") "Doktor" else if (hon == "Komutan" || hon == "Kaptan") "Komutan" else null
                return Pair(stemTurkishProperName(root), roleFromTitle)
            }
        }

        return Pair(stemTurkishProperName(trimmed), null)
    }

    data class CandidateScore(
        val isSignificant: Boolean,
        val role: String,
        val confidence: Int,
        val difficultyMultiplier: Double,
        val reason: String
    )

    /**
     * Code-based heuristic scoring engine for character significance.
     * Evaluates appearance frequency, dialogue verbs, titles, and speech cues without unnecessary LLM calls.
     */
    fun scoreCandidate(
        candidateName: String,
        sceneContext: String,
        appearanceCount: Int = 1,
        inferredRole: String = "Yan Karakter",
        context: Context? = null
    ): CandidateScore {
        val (canonical, titleRole) = extractCanonicalNameAndRole(candidateName)
        val clean = canonical.ifBlank { candidateName.trim() }
        val lower = clean.lowercase(Locale("tr", "TR"))
        val blacklist = getBlacklist(context)

        // 1. Generic extras and blacklist checks
        if (GENERIC_EXTRAS_BLACKLIST.contains(lower) || blacklist.contains(lower) || clean.length < 2) {
            return CandidateScore(
                isSignificant = false,
                role = "Figüran",
                confidence = 15,
                difficultyMultiplier = 1.0,
                reason = "Genel figüran veya kara liste eşleşmesi."
            )
        }

        var confidence = 40
        var role = titleRole ?: inferredRole

        // 2. Promotion: Second appearance gives immediate high confidence
        if (appearanceCount >= 2) {
            confidence = maxOf(confidence, 85)
        }

        // 3. Dialogue & Action cues: "Kerem dedi", "Kerem sordu"
        val hasSpeechCue = DIALOGUE_ACTION_VERBS.any { verb ->
            sceneContext.contains("$clean $verb", ignoreCase = true) ||
            sceneContext.contains("\" $clean", ignoreCase = true)
        }
        if (hasSpeechCue) {
            confidence += 30
        }

        // 4. Title / Honorific presence
        if (titleRole != null) {
            confidence += 20
        }

        // 5. Named entity capitalization check
        val isProperCase = clean.firstOrNull()?.isUpperCase() == true && clean.length in 3..25
        if (isProperCase) {
            confidence += 15
        }

        confidence = confidence.coerceIn(0, 100)

        // Multiplier evaluation based on role & context
        val mult = when {
            role.contains("Doktor", ignoreCase = true) || role.contains("Komutan", ignoreCase = true) || role.contains("Müdür", ignoreCase = true) -> 0.75
            role.contains("Düşman", ignoreCase = true) || role.contains("Rakip", ignoreCase = true) -> 0.50
            role.contains("Arkadaş", ignoreCase = true) || role.contains("Dost", ignoreCase = true) -> 1.15
            else -> 1.0
        }

        val isSignificant = confidence >= 50
        return CandidateScore(
            isSignificant = isSignificant,
            role = if (role.isNotBlank() && role != "Yan Karakter") role else "Yan Karakter",
            confidence = confidence,
            difficultyMultiplier = mult,
            reason = if (hasSpeechCue) "Aktif diyalog sahibi karakter" else "Sahnede adı geçen karakter"
        )
    }

    /**
     * Filters cast members to only include those active in the current scene/window (last 3 messages),
     * saving precious token budget by omitting off-screen characters.
     */
    fun filterActiveInSceneCastMembers(
        allCastMembers: List<com.example.data.local.CastMemberEntity>,
        recentMessagesText: String
    ): List<com.example.data.local.CastMemberEntity> {
        if (allCastMembers.isEmpty()) return emptyList()
        val lower = recentMessagesText.lowercase(Locale("tr", "TR"))

        return allCastMembers.filter { member ->
            val mName = member.name.lowercase(Locale("tr", "TR"))
            val (canonical, _) = extractCanonicalNameAndRole(member.name)
            val cName = canonical.lowercase(Locale("tr", "TR"))

            lower.contains(mName) || lower.contains(cName) || member.importanceScore >= 85
        }
    }
}
