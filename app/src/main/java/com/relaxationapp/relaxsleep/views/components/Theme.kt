package com.relaxationapp.relaxsleep.views.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Night Glow Palette ───────────────────────────────────────────────────────
val DeepViolet     = Color(0xFF0A0033)
val DeepPurple     = Color(0xFF1A004D)
val DeepNavy       = Color(0xFF002266)
val SkyBlue        = Color(0xFF4DDFFF)
val LavenderPink   = Color(0xFFC266FF)
val SoftTeal       = Color(0xFF00E6C6)
val GoldenAccent   = Color(0xFFFFDDAA)
val WhiteText      = Color(0xFFFFFFFF)
val LightBlueText  = Color(0xFFBFDFFF)
val BubbleActive   = Color(0xFF4DDFFF).copy(alpha = 0.85f)
val BubbleInactive = Color(0xFF4DDFFF).copy(alpha = 0.25f)
val OverlayDark    = Color(0x88000022)

// Sunset theme
val SunsetPink   = Color(0xFF3D0030)
val SunsetOrange = Color(0xFF7A1A00)
val SunsetGold   = Color(0xFFFFAA44)

// Ocean theme
val OceanDeep = Color(0xFF001A33)
val OceanMid  = Color(0xFF003355)
val OceanTeal = Color(0xFF00C9B1)

// ── Gradients ────────────────────────────────────────────────────────────────
val NightGradientColors  = listOf(DeepViolet, DeepPurple, DeepNavy)
val SunsetGradientColors = listOf(SunsetPink, SunsetOrange, SunsetGold.copy(alpha = 0.5f))
val OceanGradientColors  = listOf(OceanDeep,  OceanMid,    OceanTeal.copy(alpha = 0.4f))

// ── Typography ───────────────────────────────────────────────────────────────
val RelaxTypography = Typography(
    displayLarge = TextStyle(
        fontFamily    = FontFamily.Serif,
        fontWeight    = FontWeight.Light,
        fontSize      = 48.sp,
        lineHeight    = 56.sp,
        letterSpacing = (-0.5).sp,
        color         = WhiteText,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        fontSize   = 36.sp,
        lineHeight = 44.sp,
        color      = WhiteText,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        color      = WhiteText,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
        color      = WhiteText,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize   = 18.sp,
        lineHeight = 26.sp,
        color      = WhiteText,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = LightBlueText,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = LightBlueText,
    ),
    labelLarge = TextStyle(
        fontFamily    = FontFamily.SansSerif,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        letterSpacing = 1.sp,
        color         = GoldenAccent,
    ),
    labelMedium = TextStyle(
        fontFamily    = FontFamily.SansSerif,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        letterSpacing = 0.5.sp,
        color         = LightBlueText,
    ),
)

// ── Color Scheme ─────────────────────────────────────────────────────────────
private val NightScheme = darkColorScheme(
    primary      = SkyBlue,
    onPrimary    = DeepViolet,
    secondary    = LavenderPink,
    tertiary     = SoftTeal,
    background   = DeepViolet,
    surface      = DeepPurple,
    onBackground = WhiteText,
    onSurface    = LightBlueText,
)

// ── App Theme ─────────────────────────────────────────────────────────────────
@Composable
fun RelaxSleepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightScheme,
        typography  = RelaxTypography,
        content     = content,
    )
}