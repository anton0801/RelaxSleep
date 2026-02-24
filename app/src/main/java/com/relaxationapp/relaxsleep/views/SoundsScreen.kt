package com.relaxationapp.relaxsleep.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.Models.SleepSound
import com.relaxationapp.relaxsleep.viewmodel.SoundState
import com.relaxationapp.relaxsleep.views.components.BubbleVolumeSlider
import com.relaxationapp.relaxsleep.views.components.GlowCard
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.LightBlueText
import com.relaxationapp.relaxsleep.views.components.NightBackground
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal
import com.relaxationapp.relaxsleep.views.components.SpringButton

@Composable
fun SoundsScreen(
    state: SoundState,
    theme: AppTheme = AppTheme.NIGHT,
    onSelectSound: (SleepSound) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onStop: () -> Unit,
    onBack: () -> Unit,
) {
    NightBackground(theme = theme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    "← Back",
                    style    = MaterialTheme.typography.bodyLarge.copy(color = LightBlueText),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onBack() }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Choose Your Atmosphere", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)

            if (state.isPlaying && state.selected != null) {
                Spacer(Modifier.height(8.dp))
                Text("▶ ${state.selected.label}", style = MaterialTheme.typography.bodyLarge.copy(color = GoldenAccent))
                RisingBubbles(modifier = Modifier.height(40.dp).fillMaxWidth())
            } else {
                Spacer(Modifier.height(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            val sounds = SleepSound.values()
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                sounds.toList().chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { sound ->
                            SoundBubble(
                                sound    = sound,
                                selected = state.selected == sound && state.isPlaying,
                                onClick  = { onSelectSound(sound) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            GlowCard(modifier = Modifier.fillMaxWidth(), glowColor = SkyBlue.copy(alpha = 0.5f)) {
                BubbleVolumeSlider(value = state.volume, onValueChange = onVolumeChange, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(20.dp))

            if (state.isPlaying) {
                SpringButton("Stop", onClick = onStop, color = LavenderPink)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SoundBubble(sound: SleepSound, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = when { pressed -> 0.88f; selected -> 1.08f; else -> 1.0f },
        animationSpec = spring(dampingRatio = 0.55f),
        label         = "soundBubble_${sound.name}"
    )
    val color = if (selected) SoftTeal else SkyBlue
    val bodyAlpha = if (selected) 0.9f else 0.3f
    val infiniteTransition = rememberInfiniteTransition(label = "sb_${sound.name}")
    val pulse by infiniteTransition.animateFloat(
        0.95f, 1.0f,
        infiniteRepeatable(tween(if (selected) 1200 else 2000, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)), RepeatMode.Reverse),
        label = "sbPulse_${sound.name}"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale).clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            Canvas(Modifier.matchParentSize()) {
                val r = size.minDimension / 2 * pulse; val cx = size.width / 2; val cy = size.height / 2
                if (selected) drawCircle(brush = Brush.radialGradient(listOf(color.copy(alpha = 0.3f), Color.Transparent), radius = r * 1.4f), radius = r * 1.4f, center = Offset(cx, cy))
                drawCircle(brush = Brush.radialGradient(listOf(color.copy(alpha = bodyAlpha), color.copy(alpha = bodyAlpha * 0.4f)), center = Offset(cx - r * 0.18f, cy - r * 0.18f), radius = r), radius = r, center = Offset(cx, cy))
                drawCircle(color = Color.White.copy(alpha = if (selected) 0.35f else 0.15f), radius = r * 0.28f, center = Offset(cx - r * 0.25f, cy - r * 0.3f))
            }
            Text(sound.emoji, fontSize = 28.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(sound.label, style = MaterialTheme.typography.labelMedium.copy(color = if (selected) SoftTeal else LightBlueText))
    }
}

@Composable
private fun RisingBubbles(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "rising")
    val t by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "risingT")
    val particles = remember { List(12) { i -> Triple(i.toFloat() / 12f, (i * 0.27f) % 1f, i * 2.5f + 4f) } }
    Canvas(modifier = modifier) {
        particles.forEach { (xFrac, offsetT, radius) ->
            val relT = (t + offsetT) % 1f
            drawCircle(color = SoftTeal.copy(alpha = 0.5f * (1f - relT)), radius = radius, center = Offset(xFrac * size.width, size.height * (1f - relT)))
        }
    }
}