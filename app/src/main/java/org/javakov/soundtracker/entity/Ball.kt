package org.javakov.soundtracker.entity

data class Ball(
    var ballX: Float,
    var ballY: Float,
    var radius: Float,
    var ballSpeedX: Float,
    var ballSpeedY: Float,
    var color: androidx.compose.ui.graphics.Color
) {

    fun update(xMin: Float, xMax: Float, yMin: Float, yMax: Float) {
        this.ballX += this.ballSpeedX
        this.ballY += this.ballSpeedY

        if (this.ballX + this.radius > xMax) {
            this.ballSpeedX = -this.ballSpeedX
            this.ballX = xMax - this.radius
        } else if (this.ballX - this.radius < xMin) {
            this.ballSpeedX = -this.ballSpeedX
            this.ballX = xMin + this.radius
        }

        if (this.ballY + this.radius > yMax) {
            this.ballSpeedY = -this.ballSpeedY
            this.ballY = yMax - this.radius
        } else if (this.ballY - this.radius < yMin) {
            this.ballSpeedY = -this.ballSpeedY
            this.ballY = yMin + this.radius
        }
    }
}
