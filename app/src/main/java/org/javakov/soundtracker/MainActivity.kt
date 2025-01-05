package org.javakov.soundtracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import org.javakov.soundtracker.ui.MusicControls
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
            ) == PackageManager.PERMISSION_GRANTED) {
            startSoundVisualizer()
        } else {
            permissionRequestLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSoundVisualizer() {
        setContent {
            SoundTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SoundVisualizer(modifier = Modifier.padding(innerPadding))
                    MusicControls(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

