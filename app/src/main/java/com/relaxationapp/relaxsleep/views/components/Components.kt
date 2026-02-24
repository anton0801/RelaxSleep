package com.relaxationapp.relaxsleep.views.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.relaxationapp.relaxsleep.Models.AppTheme
import kotlin.math.*
import kotlin.random.Random

private data class BubbleData(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val color: Color,
    val phase: Float,
)

@Composable
fun NightBackground(
    theme: AppTheme = AppTheme.NIGHT,
    fadeAlpha: Float = 1f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val gradientColors = when (theme) {
        AppTheme.NIGHT  -> listOf(DeepViolet, DeepPurple, DeepNavy)
        AppTheme.SUNSET -> listOf(SunsetPink, SunsetOrange, Color(0xFF3A1500))
        AppTheme.OCEAN  -> listOf(OceanDeep, OceanMid, Color(0xFF001A2E))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(gradientColors))
    ) {
        FloatingOrbs(theme = theme, alpha = fadeAlpha.coerceIn(0.1f, 1f) * 0.45f)
        content()
    }
}

@Composable
fun FloatingOrbs(
    theme: AppTheme = AppTheme.NIGHT,
    alpha: Float = 0.4f,
    count: Int = 8,
) {
    val bubbleColors = when (theme) {
        AppTheme.NIGHT  -> listOf(SkyBlue, LavenderPink, SoftTeal)
        AppTheme.SUNSET -> listOf(SunsetGold, Color(0xFFFF6680), Color(0xFFFFAA55))
        AppTheme.OCEAN  -> listOf(OceanTeal, Color(0xFF4DA6FF), Color(0xFF00E0CC))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation  = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbTime"
    )

    val bubbles = remember(count) {
        List(count) { i ->
            BubbleData(
                x      = Random.nextFloat(),
                y      = Random.nextFloat(),
                radius = Random.nextFloat() * 60f + 30f,
                speed  = Random.nextFloat() * 0.3f + 0.1f,
                color  = bubbleColors[i % bubbleColors.size],
                phase  = Random.nextFloat() * (2 * PI).toFloat(),
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        bubbles.forEach { bubble ->
            val cx = size.width  * bubble.x + sin(time * bubble.speed + bubble.phase) * size.width * 0.08f
            val cy = size.height * bubble.y + cos(time * bubble.speed * 0.7f + bubble.phase) * size.height * 0.06f
            drawBubble(Offset(cx, cy), bubble.radius, bubble.color.copy(alpha = alpha * 0.6f))
        }
    }
}

private fun DrawScope.drawBubble(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
            center = center, radius = radius * 2f
        ),
        radius = radius * 2f, center = center,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.3f), Color.Transparent),
            center = center, radius = radius,
        ),
        radius = radius, center = center,
    )
    drawCircle(
        color  = Color.White.copy(alpha = 0.25f),
        radius = radius * 0.35f,
        center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
    )
}

@Composable
fun BreathBubble(
    scale: Float,
    glowColor: Color = SkyBlue,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(220.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r  = (size.minDimension / 2) * scale

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = 0.15f * scale), Color.Transparent),
                center = Offset(cx, cy), radius = r * 1.6f,
            ),
            radius = r * 1.6f, center = Offset(cx, cy),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.9f),
                    glowColor.copy(alpha = 0.4f),
                    glowColor.copy(alpha = 0.1f),
                ),
                center = Offset(cx - r * 0.2f, cy - r * 0.2f), radius = r,
            ),
            radius = r, center = Offset(cx, cy),
        )
        drawCircle(
            color  = Color.White.copy(alpha = 0.35f),
            radius = r * 0.3f,
            center = Offset(cx - r * 0.28f, cy - r * 0.28f),
        )
    }
}