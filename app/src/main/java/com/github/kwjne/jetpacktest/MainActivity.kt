package com.github.kwjne.jetpacktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.github.kwjne.jetpacktest.ui.theme.JetpackTestTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MusicViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[MusicViewModel::class.java]
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { // compose interface
            JetpackTestTheme { // custom theme
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)){
                        MyApp(viewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun MusicControls(viewModel: MusicViewModel) {
    val isPlaying by viewModel.isPlaying

    Row(modifier = Modifier.padding(8.dp)) {
        MusicSlider(viewModel)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { viewModel.prev() }) {
            Text("⏮")
        }

        Button(onClick = { viewModel.togglePlay() }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

        Button(onClick = { viewModel.next() }) {
            Text("⏭")
        }
    }
}



@Composable
fun MyApp(activity: MusicViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        MusicControls(activity)
    }
}

@Composable
fun MusicSlider(viewModel: MusicViewModel) {
    val mediaPlayer by viewModel.mediaPlayer

    var position by remember { mutableFloatStateOf(0f) }
    val duration = remember(mediaPlayer) {
        mediaPlayer?.duration?.toFloat()?.coerceAtLeast(1f) ?: 1f
    }

    LaunchedEffect(mediaPlayer) {
        while (mediaPlayer != null) {
            try {
                position = mediaPlayer?.currentPosition?.toFloat() ?: 0f
            } catch (e: IllegalStateException) {
                // MediaPlayer говно ждем
            }
            delay(1000)
        }
    }

    Slider(
        value = position,
        onValueChange = { position = it },
        onValueChangeFinished = {
            try {
                mediaPlayer?.seekTo(position.toInt())
            } catch (e: IllegalStateException) {
                // игнор если говно в плеере
            }
        },
        valueRange = 0f..duration
    )
}









