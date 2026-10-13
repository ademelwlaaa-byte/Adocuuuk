package com.example.data.repository

import com.example.data.local.EmotionState
import com.example.data.local.PersonalityProfile
import com.example.data.local.PrimaryEmotions
import com.example.data.local.RelationshipAxes
import com.example.data.local.SensitiveTriggerEntity
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Baseline Emotion & Personality Initialization Engine (Başlangıç Duyguları).
 * Features:
 * - One-time LLM structured analysis at bot creation (0 runtime token cost)
 * - Safe range clamping [0, 100] for Big Five, Plutchik and regulation capacity
 * - Word-boundary Turkish regex matching to eliminate substring bugs ("abi", "eş ", "anne")
 * - 3 SensitiveTriggers generation for dynamic character trauma/taboo modeling
 */
object BaselineEmotionEngine {

    data class BaselineAnalysisResult(
        val relationshipType: String,
        val personalityProfile: PersonalityProfile,
        val regulationCapacity: Int,
        val difficultyMultiplier: Double,
        val baselineEmotionState: EmotionState,
        val sensitiveTriggers: List<SensitiveTriggerEntity>,
        val speechPattern: String,
        val rawJson: String
    )

    fun createAnalysisPrompt(
        aiName: String,
        personality: String,
        scenario: String,
        userCharName: String = "",
        userCharDesc: String = ""
    ): String {
        return """
        Sen karakter psikolojisi uzmanısın. Aşağıdaki karakter kartını analiz et:
        Karakter Adı: $aiName
        Kişilik: $personality
        Senaryo: $scenario
        Kullanıcı: $userCharName ($userCharDesc)

        SADECE şu JSON formatında yanıt ver:
        {
          "relationship_type": "lover|rival|friend|family|stranger",
          "openness": 0-100,
          "conscientiousness": 0-100,
          "extraversion": 0-100,
          "agreeableness": 0-100,
          "neuroticism": 0-100,
          "attachment_style": "secure|anxious|avoidant|fearful_avoidant",
          "regulation_capacity": 0-100,
          "difficulty_multiplier": 0.5-2.0,
          "primary_emotions": {
            "joy": 0-100, "trust": 0-100, "fear": 0-100, "anger": 0-100,
            "sadness": 0-100, "anticipation": 0-100, "surprise": 0-100, "disgust": 0-100
          },
          "relationship_axes": {
            "affectionScore": 0-100, "respectScore": 0-100, "comfortScore": 0-100, "resentmentScore": 0-100
          },
          "dominant_emotion": "kısa duygu tanımı",
          "speech_pattern": "kısa konuşma tarzı tanımı",
          "sensitive_triggers": [
            {"topic": "tetikleyici konu 1", "description": "neden hassas olduğu", "multiplier": 1.5-3.0},
            {"topic": "tetikleyici konu 2", "description": "neden hassas olduğu", "multiplier": 1.5-3.0},
            {"topic": "tetikleyici konu 3", "description": "neden hassas olduğu", "multiplier": 1.5-3.0}
          ]
        }
        """.trimIndent()
    }

    /**
     * Parses the LLM JSON output with strict clamping and validation.
     * Falls back to deterministic heuristic if JSON is malformed.
     */
    fun parseAnalysisJson(
        jsonStr: String,
        botId: String,
        aiName: String,
        personality: String,
        scenario: String,
        userCharName: String = "",
        userCharDesc: String = ""
    ): BaselineAnalysisResult {
        return try {
            val jsonMatch = Regex("""(?is)\{.*?\}""").find(jsonStr)?.value ?: jsonStr
            val root = JSONObject(jsonMatch)

            val relType = root.optString("relationship_type", "friend")
            val open = root.optInt("openness", 50).coerceIn(0, 100)
            val cons = root.optInt("conscientiousness", 50).coerceIn(0, 100)
            val extra = root.optInt("extraversion", 50).coerceIn(0, 100)
            val agree = root.optInt("agreeableness", 50).coerceIn(0, 100)
            val neur = root.optInt("neuroticism", 50).coerceIn(0, 100)
            val attach = root.optString("attachment_style", "secure")

            val profile = PersonalityProfile(
                openness = open,
                conscientiousness = cons,
                extraversion = extra,
                agreeableness = agree,
                neuroticism = neur,
                attachmentStyle = attach
            )

            val regCap = root.optInt("regulation_capacity", 50).coerceIn(10, 100)
            val diffMult = root.optDouble("difficulty_multiplier", 1.0).coerceIn(0.5, 2.5)

            val primObj = root.optJSONObject("primary_emotions")
            val primaryEmotions = PrimaryEmotions(
                joy = primObj?.optInt("joy", 0)?.coerceIn(0, 100) ?: 0,
                trust = primObj?.optInt("trust", 50)?.coerceIn(0, 100) ?: 50,
                fear = primObj?.optInt("fear", 0)?.coerceIn(0, 100) ?: 0,
                anger = primObj?.optInt("anger", 0)?.coerceIn(0, 100) ?: 0,
                sadness = primObj?.optInt("sadness", 0)?.coerceIn(0, 100) ?: 0,
                anticipation = primObj?.optInt("anticipation", 0)?.coerceIn(0, 100) ?: 0,
                surprise = primObj?.optInt("surprise", 0)?.coerceIn(0, 100) ?: 0,
                disgust = primObj?.optInt("disgust", 0)?.coerceIn(0, 100) ?: 0
            )

            val axesObj = root.optJSONObject("relationship_axes")
            val relationshipAxes = RelationshipAxes(
                affectionScore = axesObj?.optInt("affectionScore", 50)?.coerceIn(0, 100) ?: 50,
                respectScore = axesObj?.optInt("respectScore", 50)?.coerceIn(0, 100) ?: 50,
                comfortScore = axesObj?.optInt("comfortScore", 50)?.coerceIn(0, 100) ?: 50,
                resentmentScore = axesObj?.optInt("resentmentScore", 0)?.coerceIn(0, 100) ?: 0
            )

            val domEmotion = root.optString("dominant_emotion", "nötr")
            val speechPattern = root.optString("speech_pattern", "Doğal")

            val triggers = mutableListOf<SensitiveTriggerEntity>()
            val trigArr = root.optJSONArray("sensitive_triggers")
            if (trigArr != null) {
                for (i in 0 until minOf(trigArr.length(), 3)) {
                    val tObj = trigArr.optJSONObject(i) ?: continue
                    val topic = tObj.optString("topic", "").trim()
                    if (topic.isNotBlank()) {
                        triggers.add(
                            SensitiveTriggerEntity(
                                botId = botId,
                                triggerTopic = topic,
                                sourceDescription = tObj.optString("description", "Karakter geçmişi ve kişiliği"),
                                intensityMultiplier = tObj.optDouble("multiplier", 1.5).coerceIn(1.2, 3.0),
                                recentTriggerCount = 0
                            )
                        )
                    }
                }
            }

            val emotionState = EmotionState(
                primaryEmotions = primaryEmotions,
                relationshipAxes = relationshipAxes,
                dominantEmotion = domEmotion,
                speechPattern = speechPattern
            )

            BaselineAnalysisResult(
                relationshipType = relType,
                personalityProfile = profile,
                regulationCapacity = regCap,
                difficultyMultiplier = diffMult,
                baselineEmotionState = emotionState,
                sensitiveTriggers = triggers,
                speechPattern = speechPattern,
                rawJson = root.toString()
            )
        } catch (_: Exception) {
            calculateDeterministicFallback(botId, aiName, personality, scenario, userCharName, userCharDesc)
        }
    }

    /**
     * High-precision Turkish regex matching to eliminate substring false positives
     * (e.g. matching "abi" inside "tabi" or "eş" inside "ateş").
     */
    fun calculateDeterministicFallback(
        botId: String,
        aiName: String,
        personality: String,
        scenario: String,
        userCharName: String = "",
        userCharDesc: String = ""
    ): BaselineAnalysisResult {
        val text = "$aiName $personality $scenario $userCharName $userCharDesc".lowercase(Locale("tr", "TR"))

        // Word boundary regex tests
        val isMother = Regex("""\b(anne|annem|annesi|valide|anam)\b""").containsMatchIn(text)
        val isFather = Regex("""\b(baba|babam|babası|peder)\b""").containsMatchIn(text)
        val isSibling = Regex("""\b(kardeş|kardeşim|abla|ablam|abi|ağabey|ağabeyim)\b""").containsMatchIn(text)
        val isLover = Regex("""\b(sevgili|sevgilim|aşık|partner|nişanlı|eşim|karım|kocam)\b""").containsMatchIn(text)
        val isBestFriend = Regex("""\b(yakın dost|sırdaş|çocukluk arkadaşı|kanka|can dostu)\b""").containsMatchIn(text)
        val isRival = Regex("""\b(düşman|rakip|hasım|hasmı|can düşmanı)\b""").containsMatchIn(text)
        val isStranger = Regex("""\b(yabancı|tanımıyor|ilk kez karşılaştı|yeni tanıştı)\b""").containsMatchIn(text)

        val hasConflict = Regex("""\b(kavga|küs|küstü|tartışma|soğukluk|çatışma|kırgın)\b""").containsMatchIn(text)

        val (state, relType, mult) = when {
            (isMother || isFather) && hasConflict -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(trust = 45, sadness = 40),
                    relationshipAxes = RelationshipAxes(affectionScore = 75, respectScore = 60, comfortScore = 40, resentmentScore = 60),
                    dominantEmotion = "kırgın ve mesafeli",
                    suppressedEmotion = "hüzünlü sevgi"
                ),
                "family", 1.2
            )
            (isMother || isFather) -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(joy = 40, trust = 88),
                    relationshipAxes = RelationshipAxes(affectionScore = 92, respectScore = 80, comfortScore = 85, resentmentScore = 0),
                    dominantEmotion = "korumacı ve şefkatli"
                ),
                "family", 1.3
            )
            isLover && hasConflict -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(trust = 50, sadness = 50, anger = 30),
                    relationshipAxes = RelationshipAxes(affectionScore = 80, respectScore = 60, comfortScore = 45, resentmentScore = 55),
                    dominantEmotion = "buruk ve tereddütlü"
                ),
                "lover", 1.4
            )
            isLover -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(joy = 60, trust = 85),
                    relationshipAxes = RelationshipAxes(affectionScore = 90, respectScore = 80, comfortScore = 85, resentmentScore = 0),
                    dominantEmotion = "sevecen ve tutkulu"
                ),
                "lover", 1.3
            )
            isBestFriend -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(joy = 50, trust = 85),
                    relationshipAxes = RelationshipAxes(affectionScore = 85, respectScore = 75, comfortScore = 80, resentmentScore = 0),
                    dominantEmotion = "sıcak ve samimi"
                ),
                "friend", 1.1
            )
            isSibling -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(joy = 40, trust = 80),
                    relationshipAxes = RelationshipAxes(affectionScore = 82, respectScore = 70, comfortScore = 75, resentmentScore = 0),
                    dominantEmotion = "samimi ve alaycı"
                ),
                "family", 1.2
            )
            isRival -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(trust = 10, fear = 30, anger = 50),
                    relationshipAxes = RelationshipAxes(affectionScore = 15, respectScore = 40, comfortScore = 10, resentmentScore = 75),
                    dominantEmotion = "şüpheci ve tetikte"
                ),
                "rival", 0.6
            )
            isStranger -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(trust = 25, anticipation = 40),
                    relationshipAxes = RelationshipAxes(affectionScore = 30, respectScore = 50, comfortScore = 30, resentmentScore = 0),
                    dominantEmotion = "temkinli ve meraklı"
                ),
                "stranger", 0.9
            )
            else -> Triple(
                EmotionState(
                    primaryEmotions = PrimaryEmotions(trust = 50, anticipation = 30),
                    relationshipAxes = RelationshipAxes(affectionScore = 50, respectScore = 50, comfortScore = 50, resentmentScore = 0),
                    dominantEmotion = "nötr ve ilgili"
                ),
                "friend", 1.0
            )
        }

        val profile = PersonalityProfile.deriveFromPersonality(aiName, personality, scenario)
        val regCap = PersonalityProfile.deriveEmotionalRegulationCapacity(aiName, personality, scenario)

        val triggers = listOf(
            SensitiveTriggerEntity(
                botId = botId,
                triggerTopic = if (isRival) "küçümsenme" else if (hasConflict) "yalan" else "terk edilme",
                sourceDescription = "Karakter geçmişi ve ilişki dinamikleri",
                intensityMultiplier = 1.8,
                recentTriggerCount = 0
            )
        )

        return BaselineAnalysisResult(
            relationshipType = relType,
            personalityProfile = profile,
            regulationCapacity = regCap,
            difficultyMultiplier = mult,
            baselineEmotionState = state,
            sensitiveTriggers = triggers,
            speechPattern = if (personality.contains("ciddi", ignoreCase = true)) "Resmi ve net" else "Doğal ve samimi",
            rawJson = ""
        )
    }
}
