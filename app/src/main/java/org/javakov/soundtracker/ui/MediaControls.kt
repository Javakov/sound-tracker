package org.javakov.soundtracker.ui

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MusicControls(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    fun isMusicPlaying(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.isMusicActive
    }

    // Функция для отправки медиакнопки
    fun sendMediaButtonClick(context: Context, keyCode: Int) {
        val eventTime = SystemClock.uptimeMillis()

        // Создаем событие нажатия кнопки
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
        val upEvent = KeyEvent(eventTime + 1, eventTime + 1, KeyEvent.ACTION_UP, keyCode, 0)

        val downIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(Intent.EXTRA_KEY_EVENT, downEvent)
        }

        val upIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(Intent.EXTRA_KEY_EVENT, upEvent)
        }

        try {
            // Отправка событий нажатия и отпускания
            context.sendBroadcast(downIntent)  // Используем sendBroadcast вместо sendOrderedBroadcast
            context.sendBroadcast(upIntent)
            Log.d("MusicControl", "Media button click sent: $keyCode")
        } catch (e: Exception) {
            Log.e("MusicControl", "Error sending intent: ${e.message}")
        }

        if (isMusicPlaying(context)) Log.d("MusicControl", "music playing")
        else Log.d("MusicControl", "music NOT playing")
    }

    // UI для кнопок управления музыкой
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Кнопка для предыдущего трека
            Button(
                onClick = { sendMediaButtonClick(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Previous", color = Color.Black)
            }

            // Кнопка для паузы/воспроизведения
            Button(
                onClick = { sendMediaButtonClick(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Play/Pause", color = Color.Black)
            }

            // Кнопка для следующего трека
            Button(
                onClick = { sendMediaButtonClick(context, KeyEvent.KEYCODE_MEDIA_NEXT) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Next", color = Color.Black)
            }
        }
    }
}




