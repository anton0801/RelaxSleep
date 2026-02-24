package com.relaxationapp.relaxsleep.Models

// ── App Navigation Routes ────────────────────────────────────────────────────
object Routes {
    const val SPLASH      = "splash"
    const val ONBOARDING  = "onboarding"
    const val HOME        = "home"
    const val BREATHING   = "breathing"
    const val SOUNDS      = "sounds"
    const val SLEEP_TIMER = "sleep_timer"
    const val SETTINGS    = "settings"
}

// ── Sleep Sound ──────────────────────────────────────────────────────────────
enum class SleepSound(val label: String, val emoji: String) {
    RAIN    ("Rain",     "🌧️"),
    OCEAN   ("Ocean",    "🌊"),
    FIRE    ("Campfire", "🔥"),
    WIND    ("Wind",     "💨"),
    SILENCE ("Silence",  "🌙"),
    LULLABY ("Lullaby",  "🎵"),
}

// ── App Theme ────────────────────────────────────────────────────────────────
enum class AppTheme(val label: String) {
    NIGHT  ("Night"),
    SUNSET ("Sunset"),
    OCEAN  ("Ocean"),
}

// ── Settings Model ───────────────────────────────────────────────────────────
data class AppSettings(
    val sleepReminderEnabled: Boolean = false,
    val vibrationEnabled: Boolean     = true,
    val autoBreathing: Boolean        = false,
    val appTheme: AppTheme            = AppTheme.NIGHT,
    val volume: Float                 = 0.7f,
    val selectedSound: SleepSound?    = null,
    val onboardingDone: Boolean       = false,
)

// ── Breathing Phase ──────────────────────────────────────────────────────────
enum class BreathPhase(val label: String, val durationSec: Int) {
    INHALE ("Breathe in",  4),
    HOLD   ("Hold...",     4),
    EXHALE ("Breathe out", 6),
    REST   ("Rest...",     2),
}

// ── Onboarding Page ──────────────────────────────────────────────────────────
data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val description: String,
)

val onboardingPages = listOf(
    OnboardingPage(
        title       = "Welcome to\nSleep",
        subtitle    = "Your personal sanctuary",
        emoji       = "🌙",
        description = "A carefully designed environment\nfor effortless sleep every night"
    ),
    OnboardingPage(
        title       = "Breathe\nWith the Light",
        subtitle    = "Calm your mind in minutes",
        emoji       = "💫",
        description = "Breathing exercises synchronize\nyour body with the rhythm of sleep"
    ),
    OnboardingPage(
        title       = "Your Night\nAtmosphere",
        subtitle    = "Sounds of nature & silence",
        emoji       = "🌊",
        description = "Rain, ocean, fire — choose the\nsound that drifts to sleep with you"
    ),
    OnboardingPage(
        title       = "Gentle\nSleep Timer",
        subtitle    = "A soft fade into dreams",
        emoji       = "⏰",
        description = "Sounds and light gently fade away\nas you sink into deep, restful sleep"
    ),
)