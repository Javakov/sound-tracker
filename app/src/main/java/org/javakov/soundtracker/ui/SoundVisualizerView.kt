package org.javakov.soundtracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

            // Количество линий для отображения
            val step = width / lineCount

            // Интерполяция между предыдущими и новыми значениями амплитуды
            val interpolatedAmplitudes = List(lineCount) { index ->
                val ratio = index.toFloat() / lineCount
                val currentAmplitude = amplitudes.getOrNull((ratio * amplitudes.size).toInt()) ?: 0f
                val previousAmplitude =
                    previousAmplitudes.value.getOrNull(index) ?: currentAmplitude

                // Плавное изменение (интерполяция) между предыдущей и текущей амплитудой
                val interpolatedAmplitude = leap(previousAmplitude, currentAmplitude, 0.1f)
                interpolatedAmplitude
            }

            // Обновляем предыдущие амплитуды для следующей итерации
            previousAmplitudes.value = interpolatedAmplitudes

            interpolatedAmplitudes.forEachIndexed { index, amplitude ->
                val x = index * step
                val y = middle + amplitude * middle
                drawLine(
                    color = Color.Green,
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
