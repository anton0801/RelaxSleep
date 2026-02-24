package com.relaxationapp.relaxsleep.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.Models.BreathPhase
import com.relaxationapp.relaxsleep.viewmodel.BreathingState
import com.relaxationapp.relaxsleep.views.components.BreathBubble
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.LightBlueText
import com.relaxationapp.relaxsleep.views.components.NightBackground
import com.relaxationapp.relaxsleep.views.components.PillButton
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal
import com.relaxationapp.relaxsleep.views.components.SpringButton

@Composable
fun BreathingScreen(
    state: BreathingState,
    theme: AppTheme = AppTheme.NIGHT,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onBack: () -> Unit,
) {
    val targetScale = when (state.phase) {
        BreathPhase.INHALE -> 0.65f + state.phaseProgress * 0.40f
        BreathPhase.HOLD   -> 1.05f
        BreathPhase.EXHALE -> 1.05f - state.phaseProgress * 0.40f
        BreathPhase.REST   -> 0.65f
    }
    val bubbleScale by animateFloatAsState(
        targetValue   = if (state.isRunning) targetScale else 0.75f,
        animationSpec = tween(100, easing = LinearEasing),
        label         = "breathScale"
    )
    val glowColor = when (state.phase) {
        BreathPhase.INHALE -> SkyBlue
        BreathPhase.HOLD   -> LavenderPink
        BreathPhase.EXHALE -> SoftTeal
        BreathPhase.REST   -> SkyBlue.copy(alpha = 0.6f)
    }
    val animatedGlow by animateColorAsState(
        targetValue   = if (state.isRunning) glowColor else SkyBlue.copy(alpha = 0.5f),
        animationSpec = tween(600),
        label         = "glowColor"
    )

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

            Spacer(Modifier.weight(0.3f))

            Text(
                "Breathe With the Light",
                style     = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            if (state.isRunning) {
                Text(
                    "Cycle ${state.cycleCount + 1}  •  ${formatTime(state.sessionSeconds)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.weight(0.4f))

            Box(contentAlignment = Alignment.Center) {
                BreathBubble(scale = bubbleScale, glowColor = animatedGlow, modifier = Modifier.size(220.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (state.isRunning) {
                        Text(
                            text  = state.phase.label,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp, color = Color.White),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        val remaining = state.phase.durationSec - (state.phase.durationSec * state.phaseProgress).toInt()
                        Text(
                            text  = remaining.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp, color = Color.White),
                        )
                    } else {
                        Text(
                            text  = "Tap\nStart",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f)),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.weight(0.4f))

            // Phase guide
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BreathPhase.values().forEach { phase ->
                    PillButton(
                        text     = "${phase.label.take(8)} ${phase.durationSec}s",
                        onClick  = {},
                        selected = state.isRunning && state.phase == phase,
                        color    = when (phase) {
                            BreathPhase.INHALE -> SkyBlue
                            BreathPhase.HOLD   -> LavenderPink
                            BreathPhase.EXHALE -> SoftTeal
                            BreathPhase.REST   -> GoldenAccent
                        }
                    )
                }
            }

            Spacer(Modifier.weight(0.3f))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (!state.isRunning) {
                    SpringButton("Start", onClick = onStart, color = GoldenAccent)
                } else {
                    SpringButton("Pause", onClick = onPause, color = SkyBlue)
                }
                SpringButton("Finish", onClick = { onStop(); onBack() }, color = LavenderPink.copy(alpha = 0.8f))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60; val s = totalSeconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}