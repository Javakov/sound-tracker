package org.javakov.soundtracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SoundVisualizer(modifier: Modifier = Modifier) {
    val audioData = remember { mutableStateOf(emptyList<Float>()) }
    val previousAmplitudes = remember { mutableStateOf(emptyList<Float>()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val colors = listOf(
        Color.Red,
        Color(255, 127, 0),
        Color.Yellow,
        Color.Green,
        Color.Cyan,
        Color.Blue,
        Color.Magenta
    )

    val transition = rememberInfiniteTransition(label = "")
    val colorValue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val colorIndex = (colorValue * colors.size).toInt() % colors.size
    val currentColor by animateColorAsState(
        targetValue = colors[colorIndex],
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearEasing
        ), label = ""
    )

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val sampleRate = 48000
                val channelConfig = AudioFormat.CHANNEL_IN_STEREO
                val encoding = AudioFormat.ENCODING_PCM_16BIT
                val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, encoding)

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    encoding,
                    bufferSize
                )

                val buffer = ShortArray(bufferSize)
                audioRecord.startRecording()

                try {
                    while (true) {
                        val read = audioRecord.read(buffer, 0, buffer.size)
                        if (read > 0) {
                            val amplitudes = buffer.map { it.toFloat() / Short.MAX_VALUE }
                            audioData.value = amplitudes
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } finally {
                    audioRecord.release()
                }
            } else {
                audioData.value = emptyList()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val amplitudes = audioData.value
        val isPortrait = size.height > size.width
        val lineCount = if (isPortrait) 100 else 200

        if (amplitudes.isNotEmpty()) {
            val width = size.width
            val height = size.height
            val middle = height / 2

            val step = width / lineCount

            val interpolatedAmplitudes = List(lineCount) { index ->
                val ratio = index.toFloat() / lineCount
                val currentAmplitude = amplitudes.getOrNull((ratio * amplitudes.size).toInt()) ?: 0f
                val previousAmplitude =
                    previousAmplitudes.value.getOrNull(index) ?: currentAmplitude

                val interpolatedAmplitude = leap(previousAmplitude, currentAmplitude, 0.1f)
                interpolatedAmplitude
            }

            previousAmplitudes.value = interpolatedAmplitudes

            interpolatedAmplitudes.forEachIndexed { index, amplitude ->
                val x = index * step
                val y = middle + amplitude * middle

                drawLine(
                    color = currentColor,
                    start = Offset(x, middle),
                    end = Offset(x, y),
                    strokeWidth = 4f
                )
            }
        }
    }
}

fun leap(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}
