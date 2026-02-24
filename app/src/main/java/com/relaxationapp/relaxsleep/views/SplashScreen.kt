package com.relaxationapp.relaxsleep.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.views.components.FloatingOrbs
import com.relaxationapp.relaxsleep.views.components.NightGradientColors
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var logoVisible     by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var particlesVisible by remember { mutableStateOf(false) }

    val logoAlpha by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0f,
        animationSpec = tween(1200, easing = EaseInOutCubic),
        label         = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow),
        label         = "logoScale"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue   = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(900, easing = EaseInOutCubic),
        label         = "subAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        particlesVisible = true
        delay(400)
        logoVisible = true
        delay(700)
        subtitleVisible = true
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(NightGradientColors)),
        contentAlignment = Alignment.Center,
    ) {
        FloatingOrbs(count = 12, alpha = 0.35f)

        if (particlesVisible) {
            ParticleRing(modifier = Modifier.size(320.dp), alpha = logoAlpha)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .alpha(logoAlpha)
                    .graphicsLayer { scaleX = logoScale; scaleY = logoScale }
            ) {
                Canvas(Modifier.size(160.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(SkyBlue.copy(alpha = 0.25f), Color.Transparent),
                            radius = size.minDimension / 2
                        ),
                        radius = size.minDimension / 2,
                    )
                }
                Text("🌙", fontSize = 72.sp)
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text     = "Relax Sleep",
                style    = MaterialTheme.typography.displayMedium.copy(
                    fontSize      = 38.sp,
                    letterSpacing = 2.sp,
                ),
                modifier = Modifier.alpha(logoAlpha),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text      = "Your gentle path to deep sleep",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(subtitleAlpha),
            )
        }
    }
}

@Composable
fun ParticleRing(modifier: Modifier = Modifier, alpha: Float = 1f) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart),
        label = "particleRotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "particlePulse"
    )

    val particles = remember {
        List(28) { i ->
            val angle = (i.toFloat() / 28f) * (2f * PI.toFloat())
            Triple(angle, Random.nextFloat() * 0.4f + 0.8f, Random.nextFloat() * 4f + 3f)
        }
    }

    Canvas(modifier = modifier.alpha(alpha)) {
        val cx = size.width / 2; val cy = size.height / 2
        val baseR = size.minDimension / 2 * 0.82f * pulse
        particles.forEach { (angle, radiusFactor, dotSize) ->
            val a = angle + Math.toRadians(rotation.toDouble()).toFloat()
            val r = baseR * radiusFactor
            drawCircle(
                color  = SkyBlue.copy(alpha = 0.55f * alpha),
                radius = dotSize,
                center = Offset(cx + cos(a) * r, cy + sin(a) * r),
            )
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1.000f)
private val EaseInOutSine  = CubicBezierEasing(0.45f,  0.05f,  0.55f,  0.95f)