package org.javakov.soundtracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import org.javakov.soundtracker.entity.Ball

@Composable
fun BouncingBallView(modifier: Modifier = Modifier) {
    val balls = remember { mutableListOf<Ball>() }
    val ballCount = 30

    LaunchedEffect(true) {
        repeat(ballCount) {
            val radius = 2 + (Math.random() * 50).toFloat()
            val velocityX = 1 + (Math.random() * 10).toFloat()
            val velocityY = 1 + (Math.random() * 10).toFloat()
            val red = (Math.random() * 255).toInt()
            val green = (Math.random() * 255).toInt()
            val blue = (Math.random() * 255).toInt()

            balls.add(
                Ball(
                    ballX = 0f,
                    ballY = 0f,
                    radius = radius,
                    ballSpeedX = velocityX,
                    ballSpeedY = velocityY,
                    color = Color(red, green, blue)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xMin = 0f
            val xMax = size.width
            val yMin = 0f
            val yMax = size.height

            balls.forEach { ball ->
                ball.update(xMin, xMax, yMin, yMax)
                drawCircle(
                    color = ball.color,
                    radius = ball.radius,
                    center = Offset(ball.ballX, ball.ballY)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(20)
        }
    }
}
