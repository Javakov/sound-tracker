package org.javakov.soundtracker.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GradientBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "")

    val startX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000, // Длительность анимации
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val startY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000, // Длительность анимации
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF345B82),
            Color(0xFF15395E),
            Color(0xFF1D2A38)
        ),
        start = Offset(0f, 0f),  // Начальный угол
        end = Offset(startX * 1000f, startY * 1000f)  // Изменение направления градиента
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = gradient)
    }
}

@Preview(showBackground = true)
@Composable
fun GradientBackgroundPreview() {
    GradientBackground(modifier = Modifier.fillMaxSize())
}
