package org.javakov.soundtracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.javakov.soundtracker.ui.BouncingBallView
import org.javakov.soundtracker.ui.SoundVisualizer
import org.javakov.soundtracker.ui.theme.SoundTrackerTheme

class MainActivity : ComponentActivity() {
    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startSoundVisualizer()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startSoundVisualizer()
        } else {
            permissionRequestLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSoundVisualizer() {
        setContent {
            SoundTrackerTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    BouncingBallView(modifier = Modifier.fillMaxSize())

                    Column(modifier = Modifier.padding(16.dp)) {
                        SoundVisualizer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

}

