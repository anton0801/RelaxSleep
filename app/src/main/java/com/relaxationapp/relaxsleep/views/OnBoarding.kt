package com.relaxationapp.relaxsleep.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relaxationapp.relaxsleep.Models.onboardingPages
import com.relaxationapp.relaxsleep.views.components.GoldenAccent
import com.relaxationapp.relaxsleep.views.components.LavenderPink
import com.relaxationapp.relaxsleep.views.components.LightBlueText
import com.relaxationapp.relaxsleep.views.components.NightGradientColors
import com.relaxationapp.relaxsleep.views.components.SkyBlue
import com.relaxationapp.relaxsleep.views.components.SoftTeal
import com.relaxationapp.relaxsleep.views.components.SpringButton
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(NightGradientColors))
    ) {
        OnboardingOrbs(page = pagerState.currentPage)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                OnboardingPageContent(page = page, pagerState = pagerState)
            }

            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                repeat(onboardingPages.size) { i ->
                    val selected = pagerState.currentPage == i
                    val width by animateDpAsState(
                        targetValue   = if (selected) 28.dp else 8.dp,
                        animationSpec = spring(dampingRatio = 0.7f),
                        label         = "dotWidth$i"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (selected) GoldenAccent else SkyBlue.copy(alpha = 0.35f))
                    )
                }
            }

            // Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pagerState.currentPage > 0) {
                    Text(
                        text     = "Back",
                        style    = MaterialTheme.typography.bodyLarge.copy(color = LightBlueText),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
                    )
                } else {
                    Spacer(Modifier.width(48.dp))
                }

                SpringButton(
                    text    = if (pagerState.currentPage < onboardingPages.size - 1) "Next" else "Get Started",
                    onClick = {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPageContent(page: Int, pagerState: PagerState) {
    val data = onboardingPages[page]
    val offset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
    val parallaxOffset = offset * 60f

    val enterAlpha by animateFloatAsState(
        targetValue   = if (pagerState.currentPage == page) 1f else 0.3f,
        animationSpec = tween(500),
        label         = "pageAlpha$page"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pageEmoji$page")
    val emojiFloat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 16f,
        animationSpec = infiniteRepeatable(
            tween(2800, easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)),
            RepeatMode.Reverse
        ),
        label = "emojiFloat$page"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .alpha(enterAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.offset(y = (-emojiFloat).dp)
        ) {
            Canvas(Modifier.size(160.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(SkyBlue.copy(alpha = 0.2f), Color.Transparent)),
                    radius = size.minDimension / 2,
                )
            }
            Text(text = data.emoji, fontSize = 80.sp)
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text      = data.title,
            style     = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
            textAlign = TextAlign.Center,
            modifier  = Modifier.offset(x = (parallaxOffset * 0.3f).dp),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text      = data.subtitle,
            style     = MaterialTheme.typography.headlineSmall.copy(color = GoldenAccent),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text      = data.description,
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier  = Modifier.offset(x = (parallaxOffset * 0.15f).dp),
        )
    }
}

@Composable
private fun OnboardingOrbs(page: Int) {
    val colors = when (page) {
        0    -> listOf(LavenderPink, SkyBlue)
        1    -> listOf(SoftTeal, SkyBlue)
        2    -> listOf(SkyBlue, SoftTeal)
        else -> listOf(GoldenAccent.copy(alpha = 0.7f), LavenderPink)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "onboardOrbs$page")
    val t by infiniteTransition.animateFloat(
        0f, (2f * PI.toFloat()),
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing)),
        label = "orbTime$page"
    )

    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width; val cy = size.height
        drawCircle(
            brush = Brush.radialGradient(
                listOf(colors[0].copy(alpha = 0.3f), Color.Transparent),
                center = Offset(cx * 0.85f + sin(t) * 30f, cy * 0.15f + cos(t * 0.7f) * 20f),
                radius = 180f
            ),
            radius = 180f,
            center = Offset(cx * 0.85f + sin(t) * 30f, cy * 0.15f + cos(t * 0.7f) * 20f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(colors[1].copy(alpha = 0.25f), Color.Transparent),
                center = Offset(cx * 0.2f + cos(t * 0.5f) * 25f, cy * 0.8f + sin(t) * 20f),
                radius = 140f
            ),
            radius = 140f,
            center = Offset(cx * 0.2f + cos(t * 0.5f) * 25f, cy * 0.8f + sin(t) * 20f),
        )
    }
}