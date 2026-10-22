package com.example.data.managers

/**
 * Bölüm 6 — Kod Organizasyonu
 *
 * EmochiRepository büyük olduğu için sorumluluklarını 5 Manager sınıfına
 * delege etmek üzere tasarlandı. Her Manager, constructor aracılığıyla
 * yalnızca ihtiyaç duyduğu DAO'ları alır; gizli/global erişim yoktur.
 *
 * Manager'lar EmochiRepository.kt içindeki Kotlin extension fonksiyonları
 * (EmochiRepository_*.kt dosyaları) tarafından çağrılır. Bu yaklaşım:
 *   - Mevcut tüm çağrı noktalarını kırmaz (EmochiRepository API'si aynı kalır)
 *   - Test edilebilirliği artırır (Manager'lar bağımsız test edilebilir)
 *   - IDE'de dosya bazında navigation sağlar
 *
 * Dosya haritası:
 *   WorldStateManager.kt      → Takvim, zaman atlama, mevsim, rutin
 *   CastMemberManager.kt      → Karakter tespiti, blacklist, rol yeniden değerlendirme
 *   MemoryRetrievalManager.kt → Hafıza getirme, FTS+embedding, checkpoint, graf
 *   EmotionEngineManager.kt   → Duygu hesaplama, clamp/delta, reappraisal
 *   ProviderFallbackManager.kt → Fallback zinciri, rate limit, model doğrulama
 */
object ManagerIndex