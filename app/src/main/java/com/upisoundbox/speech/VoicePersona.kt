package com.upisoundbox.speech

enum class VoicePersona(
    val id: String,
    val title: String,
    val description: String,
    val badge: String,
    val defaultLanguage: String,
    val pitchMultiplier: Float,
    val rateMultiplier: Float,
    val sampleTextEn: String,
    val sampleTextHi: String
) {
    COQUI_WARM_RETAIL_FEMALE(
        id = "coqui_warm_retail_female",
        title = "Coqui Natural Retail (Female)",
        description = "Warm, balanced tone inspired by Coqui XTTS natural vocal models.",
        badge = "Coqui XTTS",
        defaultLanguage = "en",
        pitchMultiplier = 1.05f,
        rateMultiplier = 1.0f,
        sampleTextEn = "Received fifty rupees from Rahul, on Google Pay.",
        sampleTextHi = "गूगल पे पर, राहुल से पचास रुपये प्राप्त हुए।"
    ),
    COQUI_CRISP_SOUNDBOX(
        id = "coqui_crisp_soundbox",
        title = "Coqui Crisp Commercial (Paytm Cadence)",
        description = "Punchy, assertive delivery tuned for busy shop floors and fast verification.",
        badge = "Commercial",
        defaultLanguage = "en",
        pitchMultiplier = 1.0f,
        rateMultiplier = 1.12f,
        sampleTextEn = "Fifty rupees received on Paytm.",
        sampleTextHi = "पेटीएम पर, पचास रुपये प्राप्त हुए।"
    ),
    ECHIDNA_BARITONE_MALE(
        id = "echidna_baritone_male",
        title = "Echidna Deep Studio (Male Baritone)",
        description = "Authoritative, warm resonance inspired by Echidna Piper Neural models.",
        badge = "Echidna Piper",
        defaultLanguage = "en",
        pitchMultiplier = 0.85f,
        rateMultiplier = 0.95f,
        sampleTextEn = "Payment received. One hundred rupees from Afzal Mansuri.",
        sampleTextHi = "भुगतान प्राप्त हुआ। अफजल मंसूरी से एक सौ रुपये।"
    ),
    COQUI_HINDI_VYAPAR(
        id = "coqui_hindi_vyapar",
        title = "Coqui Shuddh Hindi Vyapar (Merchant)",
        description = "100% fluent native Devanagari numerals and natural merchant Hindi phrasing.",
        badge = "Hindi Shuddh",
        defaultLanguage = "hi",
        pitchMultiplier = 1.0f,
        rateMultiplier = 1.0f,
        sampleTextEn = "Received twenty four rupees on PhonePe.",
        sampleTextHi = "फोनपे पर, चौबीस रुपये प्राप्त हुए।"
    ),
    ECHIDNA_HIGH_SPEED_CHECKOUT(
        id = "echidna_high_speed_checkout",
        title = "Echidna Turbo Cashier (1.25x Fast)",
        description = "Ultra-rapid announcement cadence for high-traffic supermarket queues.",
        badge = "Turbo 1.25x",
        defaultLanguage = "en",
        pitchMultiplier = 1.15f,
        rateMultiplier = 1.25f,
        sampleTextEn = "Received one hundred rupees on Google Pay.",
        sampleTextHi = "गूगल पे पर, एक सौ रुपये प्राप्त हुए।"
    ),
    COQUI_CALM_EXECUTIVE(
        id = "coqui_calm_executive",
        title = "Coqui Premium Lounge (Executive)",
        description = "Gentle, non-intrusive delivery for cafes, clinic counters, and boutiques.",
        badge = "Executive",
        defaultLanguage = "en",
        pitchMultiplier = 0.95f,
        rateMultiplier = 0.90f,
        sampleTextEn = "Payment received. Five hundred rupees from Rahul.",
        sampleTextHi = "राहुल से पाँच सौ रुपये प्राप्त हुए।"
    ),
    ECHIDNA_MARKET_LOUDSPEAKER(
        id = "echidna_market_loudspeaker",
        title = "Echidna Outdoor Loudspeaker (High Treble)",
        description = "Enhanced treble presence for noisy outdoor bazaars and street stalls.",
        badge = "Outdoor Boost",
        defaultLanguage = "en",
        pitchMultiplier = 1.20f,
        rateMultiplier = 1.05f,
        sampleTextEn = "Received one thousand rupees on PhonePe.",
        sampleTextHi = "फोनपे पर, एक हजार रुपये प्राप्त हुए।"
    ),
    COQUI_HINGLISH_URBAN(
        id = "coqui_hinglish_urban",
        title = "Coqui Urban Hinglish (Bilingual)",
        description = "Dynamic urban bilingual inflection for metropolitan stores and marts.",
        badge = "Urban Hinglish",
        defaultLanguage = "en",
        pitchMultiplier = 1.05f,
        rateMultiplier = 1.02f,
        sampleTextEn = "Payment received. Fifty rupees from Rahul on Google Pay.",
        sampleTextHi = "गूगल पे पर, पचास रुपये रिसीव हुए।"
    );

    companion object {
        fun fromId(id: String?): VoicePersona {
            return entries.firstOrNull { it.id == id } ?: COQUI_WARM_RETAIL_FEMALE
        }
    }
}
