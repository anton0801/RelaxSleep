package com.relaxationapp.relaxsleep.views.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Glowing Card ─────────────────────────────────────────────────────────────
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = SkyBlue,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(glowColor.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(DeepPurple.copy(alpha = 0.65f))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(glowColor.copy(alpha = 0.5f), Color.Transparent, glowColor.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp),
            content = content
        )
    }
}

// ── Bubble Toggle Switch ─────────────────────────────────────────────────────
@Composable
fun BubbleToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyLarge,
            color = LightBlueText,
        )
        Switch(
            checked  = checked,
            onCheckedChange = onCheckedChange,
            colors   = SwitchDefaults.colors(
                checkedThumbColor        = GoldenAccent,
                checkedTrackColor        = GoldenAccent.copy(alpha = 0.3f),
                uncheckedThumbColor      = LightBlueText,
                uncheckedTrackColor      = LightBlueText.copy(alpha = 0.2f),
            )
        )
    }
}

// ── Spring Button ────────────────────────────────────────────────────────────
@Composable
fun SpringButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GoldenAccent,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessHigh),
        label         = "btnScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.7f)))
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 32.dp, vertical = 14.dp)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 16.sp,
                color    = DeepViolet,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Small Pill Button ─────────────────────────────────────────────────────────
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    color: Color = SkyBlue,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessHigh),
        label         = "pillScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) color.copy(alpha = 0.85f)
                else color.copy(alpha = 0.18f)
            )
            .border(
                1.dp,
                if (selected) color else color.copy(alpha = 0.4f),
                RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (selected) DeepViolet else color,
            ),
        )
    }
}

// ── Volume Slider (Bubble Style) ─────────────────────────────────────────────
@Composable
fun BubbleVolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            "Громкость",
            style = MaterialTheme.typography.bodyMedium,
            color = LightBlueText,
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            value       = value,
            onValueChange = onValueChange,
            colors      = SliderDefaults.colors(
                thumbColor        = GoldenAccent,
                activeTrackColor  = SkyBlue,
                inactiveTrackColor= SkyBlue.copy(alpha = 0.2f),
            ),
        )
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}

// ── Separator ────────────────────────────────────────────────────────────────
@Composable
fun GlowDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, SkyBlue.copy(alpha = 0.3f), Color.Transparent)
                )
            )
    )
}