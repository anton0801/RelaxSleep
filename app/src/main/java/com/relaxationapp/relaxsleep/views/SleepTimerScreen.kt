package com.relaxationapp.relaxsleep.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.viewmodel.SleepTimerState
import com.relaxationapp.relaxsleep.views.components.BubbleToggle
import com.relaxationapp.relaxsleep.views.components.DeepPurple
import com.relaxationapp.relaxsleep.views.components.GlowCard
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.LightBlueText
import com.relaxationapp.relaxsleep.views.components.NightBackground
import com.relaxationapp.relaxsleep.views.components.PillButton
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal
import com.relaxationapp.relaxsleep.views.components.SpringButton
import com.relaxationapp.relaxsleep.views.components.WhiteText
import kotlin.math.*

@Composable
fun SleepTimerScreen(
    state: SleepTimerState,
    theme: AppTheme = AppTheme.NIGHT,
    onSetDuration: (Int) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onToggleFade: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val fadeAlpha = if (state.isRunning && state.fadeEnabled) state.fadeAlpha else 1f

    NightBackground(theme = theme, fadeAlpha = fadeAlpha) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(fadeAlpha.coerceIn(0.3f, 1f))
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

            Spacer(Modifier.height(16.dp))
            Text("Sleep Timer", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.weight(0.5f))

            TimerCircle(state = state)

            Spacer(Modifier.weight(0.5f))

            Text(
                "Choose Duration",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = GoldenAccent),
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(15, 30, 45, 60).forEach { min ->
                    PillButton(
                        text     = "$min min",
                        onClick  = { onSetDuration(min) },
                        selected = state.durationMinutes == min && !state.isRunning,
                        color    = SkyBlue,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            GlowCard(modifier = Modifier.fillMaxWidth(), glowColor = SoftTeal.copy(alpha = 0.3f)) {
                BubbleToggle(
                    label           = "🌅 Gradual fade-out",
                    checked         = state.fadeEnabled,
                    onCheckedChange = onToggleFade,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Sound and screen gently dim before the timer ends",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                )
            }

            Spacer(Modifier.weight(0.5f))

            if (!state.isRunning) {
                SpringButton("Start Timer", onClick = onStart, color = GoldenAccent, enabled = state.durationMinutes > 0)
            } else {
                SpringButton("Stop", onClick = onStop, color = LavenderPink)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TimerCircle(state: SleepTimerState) {
    val totalSec = state.durationMinutes * 60
    val progress = if (totalSec > 0 && state.isRunning) state.remainingSeconds.toFloat() / totalSec.toFloat() else 1f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(900), label = "timerProgress")

    val infiniteTransition = rememberInfiniteTransition(label = "timerGlow")
    val glowPulse by infiniteTransition.animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(if (state.isRunning) 2000 else 3000, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)), RepeatMode.Reverse),
        label = "timerGlow"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
        Canvas(Modifier.matchParentSize()) {
            val cx = size.width / 2; val cy = size.height / 2; val r = size.minDimension / 2 - 12f
            drawCircle(color = DeepPurple.copy(alpha = 0.7f), radius = r, center = Offset(cx, cy))
            drawCircle(brush = Brush.radialGradient(listOf(SkyBlue.copy(alpha = 0.15f * glowPulse), Color.Transparent), radius = r * 1.3f), radius = r * 1.3f, center = Offset(cx, cy))
            val sweepAngle = 360f * animatedProgress
            drawArc(
                brush = Brush.sweepGradient(listOf(SoftTeal, SkyBlue, LavenderPink, SoftTeal)),
                startAngle = -90f, sweepAngle = sweepAngle, useCenter = false,
                topLeft = Offset(cx - r, cy - r), size = Size(r * 2, r * 2),
                style = Stroke(width = 12f, cap = StrokeCap.Round),
            )
            if (state.isRunning) {
                val angleRad = Math.toRadians((-90 + sweepAngle).toDouble())
                val dotX = cx + (r * cos(angleRad)).toFloat(); val dotY = cy + (r * sin(angleRad)).toFloat()
                drawCircle(color = SkyBlue, radius = 8f, center = Offset(dotX, dotY))
                drawCircle(color = Color.White, radius = 4f, center = Offset(dotX, dotY))
            }
            drawCircle(color = Color.White.copy(alpha = 0.05f), radius = r * 0.85f, center = Offset(cx, cy))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.isRunning) {
                Text(formatTime(state.remainingSeconds), style = MaterialTheme.typography.displayMedium.copy(fontSize = 40.sp, color = WhiteText))
                Text("remaining", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("${state.durationMinutes}", style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp, color = GoldenAccent))
                Text("minutes", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp, color = LightBlueText))
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60; val s = totalSeconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}