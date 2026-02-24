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
import com.relaxationapp.relaxsleep.views.components.DeepViolet
import com.relaxationapp.relaxsleep.views.components.GlowCard
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.NightBackground
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal

@Composable
fun HomeScreen(
    theme: AppTheme = AppTheme.NIGHT,
    onBreathing: () -> Unit,
    onSounds: () -> Unit,
    onTimer: () -> Unit,
    onSettings: () -> Unit,
) {
    NightBackground(theme = theme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Relax Sleep", style = MaterialTheme.typography.headlineMedium)
                    Text("Good night 🌙", style = MaterialTheme.typography.bodyMedium)
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSettings,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        drawCircle(color = SkyBlue.copy(alpha = 0.15f), radius = size.minDimension / 2)
                    }
                    Text("⚙️", fontSize = 22.sp)
                }
            }

            Spacer(Modifier.weight(0.4f))

            Text(
                "Time to Unwind",
                style     = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Choose a mode or tap Start",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(36.dp))

            PulsatingStartButton(onClick = onBreathing)

            Spacer(Modifier.weight(0.4f))

            Text(
                "Quick Start",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = GoldenAccent),
            )
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuickModeBubble("💨", "Breathing",  SkyBlue,      onClick = onBreathing)
                QuickModeBubble("🎵", "Sleep Music", LavenderPink, onClick = onSounds)
                QuickModeBubble("🌊", "Waves",       SoftTeal,     onClick = onSounds)
            }

            Spacer(Modifier.height(24.dp))

            GlowCard(
                modifier  = Modifier.fillMaxWidth(),
                glowColor = GoldenAccent.copy(alpha = 0.4f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onTimer,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Sleep Timer", style = MaterialTheme.typography.headlineSmall)
                        Text("Set a gentle fade-out", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("⏰", fontSize = 32.sp)
                }
            }
        }
    }
}

@Composable
private fun PulsatingStartButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue   = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessHigh),
        label         = "startPressScale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "startPulse")
    val pulse by infiniteTransition.animateFloat(
        0.96f, 1.0f,
        infiniteRepeatable(tween(2000, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)), RepeatMode.Reverse),
        label = "startPulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        0.3f, 0.6f,
        infiniteRepeatable(tween(2000, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)), RepeatMode.Reverse),
        label = "startGlowA"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .scale(pressScale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Canvas(Modifier.matchParentSize()) {
            val cx = size.width / 2; val cy = size.height / 2; val r = size.minDimension / 2
            drawCircle(
                brush = Brush.radialGradient(listOf(GoldenAccent.copy(alpha = glowAlpha * 0.4f), Color.Transparent), radius = r * 1.5f),
                radius = r * 1.5f, center = Offset(cx, cy),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldenAccent, GoldenAccent.copy(alpha = 0.7f)),
                    center = Offset(cx - r * 0.2f, cy - r * 0.2f), radius = r * pulse
                ),
                radius = r * pulse, center = Offset(cx, cy),
            )
            drawCircle(color = Color.White.copy(alpha = 0.3f), radius = r * 0.3f, center = Offset(cx - r * 0.25f, cy - r * 0.3f))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("▶", fontSize = 24.sp, color = DeepViolet)
            Text("Start", style = MaterialTheme.typography.labelLarge.copy(color = DeepViolet, fontSize = 13.sp))
        }
    }
}

@Composable
private fun QuickModeBubble(emoji: String, label: String, color: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label         = "qb_$label"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "qbf_$label")
    val floatY by infiniteTransition.animateFloat(
        0f, 8f,
        infiniteRepeatable(
            tween((2200 + label.length * 150), easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)),
            RepeatMode.Reverse,
        ),
        label = "qbFloat$label"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = (-floatY).dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        ) {
            Canvas(Modifier.matchParentSize()) {
                val r = size.minDimension / 2; val cx = r; val cy = r
                drawCircle(brush = Brush.radialGradient(listOf(color.copy(alpha = 0.25f), Color.Transparent), radius = r * 1.4f), radius = r * 1.4f, center = Offset(cx, cy))
                drawCircle(brush = Brush.radialGradient(listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0.4f)), center = Offset(cx - r * 0.2f, cy - r * 0.2f), radius = r), radius = r, center = Offset(cx, cy))
                drawCircle(color = Color.White.copy(alpha = 0.3f), radius = r * 0.28f, center = Offset(cx - r * 0.25f, cy - r * 0.3f))
            }
            Text(emoji, fontSize = 26.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}