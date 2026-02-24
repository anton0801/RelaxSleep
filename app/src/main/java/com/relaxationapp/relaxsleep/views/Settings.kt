package com.relaxationapp.relaxsleep.views

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.Models.AppSettings
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.views.components.BubbleToggle
import com.relaxationapp.relaxsleep.views.components.GlowCard
import com.relaxationapp.relaxsleep.views.components.GlowDivider
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.LightBlueText
import com.relaxationapp.relaxsleep.views.components.NightBackground
import com.relaxationapp.relaxsleep.views.components.NightGradientColors
import com.relaxationapp.relaxsleep.views.components.OceanGradientColors
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal
import com.relaxationapp.relaxsleep.views.components.SunsetGradientColors

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSleepReminder: (Boolean) -> Unit,
    onVibration: (Boolean) -> Unit,
    onAutoBreathing: (Boolean) -> Unit,
    onTheme: (AppTheme) -> Unit,
    onBack: () -> Unit,
) {
    NightBackground(theme = settings.appTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "← Back",
                    style    = MaterialTheme.typography.bodyLarge.copy(color = LightBlueText),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onBack() }
                )
                Spacer(Modifier.weight(1f))
                Text("Settings", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(Modifier.height(28.dp))

            GlowCard(modifier = Modifier.fillMaxWidth(), glowColor = SkyBlue.copy(alpha = 0.3f)) {
                Text("General", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = GoldenAccent))
                Spacer(Modifier.height(8.dp))
                GlowDivider()
                Spacer(Modifier.height(8.dp))
                BubbleToggle("🔔 Sleep reminder",       settings.sleepReminderEnabled, onSleepReminder)
                GlowDivider()
                BubbleToggle("📳 Vibration",            settings.vibrationEnabled,     onVibration)
                GlowDivider()
                BubbleToggle("🌬️ Auto-start breathing", settings.autoBreathing,        onAutoBreathing)
            }

            Spacer(Modifier.height(20.dp))

            GlowCard(modifier = Modifier.fillMaxWidth(), glowColor = LavenderPink.copy(alpha = 0.3f)) {
                Text("Themes", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = GoldenAccent))
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ThemeBubble(AppTheme.NIGHT,  NightGradientColors,  settings.appTheme == AppTheme.NIGHT,  { onTheme(AppTheme.NIGHT)  })
                    ThemeBubble(AppTheme.SUNSET, SunsetGradientColors, settings.appTheme == AppTheme.SUNSET, { onTheme(AppTheme.SUNSET) })
                    ThemeBubble(AppTheme.OCEAN,  OceanGradientColors,  settings.appTheme == AppTheme.OCEAN,  { onTheme(AppTheme.OCEAN)  })
                }
            }

            Spacer(Modifier.height(20.dp))

            GlowCard(modifier = Modifier.fillMaxWidth(), glowColor = SoftTeal.copy(alpha = 0.2f)) {
                Text("About", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = GoldenAccent))
                Spacer(Modifier.height(8.dp))
                Text("Relax Sleep • v1.0", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Your gentle path to deep sleep.\nRest, relax, drift away.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                )
                Spacer(Modifier.height(8.dp))
                PrivacyPolicyButton()
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeBubble(theme: AppTheme, colors: List<Color>, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = when { pressed -> 0.88f; selected -> 1.12f; else -> 1f },
        animationSpec = spring(dampingRatio = 0.55f),
        label         = "themeBubble_${theme.name}"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "themeBubbleGlow_${theme.name}")
    val glowPulse by infiniteTransition.animateFloat(
        0.5f, 0.9f,
        infiniteRepeatable(tween(if (selected) 1400 else 2800, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)), RepeatMode.Reverse),
        label = "themeGlow_${theme.name}"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .then(if (selected) Modifier.border(2.dp, GoldenAccent, CircleShape) else Modifier)
        ) {
            Canvas(Modifier.matchParentSize()) {
                if (selected) drawCircle(brush = Brush.radialGradient(listOf(colors.first().copy(alpha = 0.4f * glowPulse), Color.Transparent), radius = size.minDimension / 2 * 1.4f), radius = size.minDimension / 2 * 1.4f)
                drawCircle(brush = Brush.verticalGradient(colors.map { it.copy(alpha = 0.95f) }))
                drawCircle(color = Color.White.copy(alpha = 0.25f), radius = size.minDimension * 0.15f, center = Offset(size.width * 0.3f, size.height * 0.28f))
            }
            Text(when (theme) { AppTheme.NIGHT -> "🌙"; AppTheme.SUNSET -> "🌅"; AppTheme.OCEAN -> "🌊" }, fontSize = 24.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(theme.label, style = MaterialTheme.typography.labelMedium.copy(color = if (selected) GoldenAccent else LightBlueText))
    }
}

private const val PRIVACY_POLICY_URL = "https://rellaxsleep.com/privacy-policy.html"

@Composable
private fun PrivacyPolicyButton() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessHigh),
        label         = "ppScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(SkyBlue.copy(alpha = 0.12f), LavenderPink.copy(alpha = 0.10f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(SkyBlue.copy(alpha = 0.35f), LavenderPink.copy(alpha = 0.25f))
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                context.startActivity(intent)
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔒", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                "Privacy Policy",
                style = MaterialTheme.typography.bodyLarge.copy(color = LightBlueText),
            )
        }
        Text(
            "↗",
            style = MaterialTheme.typography.bodyLarge.copy(color = SkyBlue),
        )
    }
}