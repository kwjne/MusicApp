package com.github.kwjne.jetpacktest

import android.media.MediaPlayer
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.kwjne.jetpacktest.ui.theme.JetpackTestTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    lateinit var mediaPlayer: MediaPlayer
    val trackList = listOf(R.raw.cupsizeklej, R.raw.bobr, R.raw.deephouse, R.raw.rammsteinengel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { // compose interface
            JetpackTestTheme { // custom theme
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "HUESOS",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            MyApp(this, trackList)
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.cupsizeklej).apply {
            isLooping = false // можно залупливать трек
        }
    }

    fun toggleMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        else {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackTestTheme {
        Greeting("HUESOS")
    }
}

@Composable
fun MusicControls(activity: MainActivity, trackList: List<Int>) {
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(activity.mediaPlayer.isPlaying) }

    fun switchTrack(newIndex: Int){
        if (newIndex in trackList.indices) {
            activity.mediaPlayer.release()
            activity.mediaPlayer = MediaPlayer.create(activity, trackList[newIndex])
            activity.mediaPlayer.setOnCompletionListener {
                switchTrack((currentTrackIndex + 1) % trackList.size)
            }
            activity.mediaPlayer.start()
            isPlaying = true
            currentTrackIndex = newIndex
        }
    }

    Row(Modifier.padding(8.dp)) {
        MusicSlider(mediaPlayer = activity.mediaPlayer, trackList)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            switchTrack((currentTrackIndex - 1 + trackList.size) % trackList.size)
        }) {
            Text("⏮")
        }

        Button(onClick = {
            activity.toggleMusic()
            isPlaying = activity.mediaPlayer.isPlaying
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

        Button(onClick = {
            switchTrack((currentTrackIndex + 1) % trackList.size)
        }) {
            Text("⏭")
        }
    }
}


@Composable
fun MyApp(activity: MainActivity, trackList: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("HUESOS")
        Spacer(modifier = Modifier.height(16.dp))
        MusicControls(activity, trackList)
    }
}

@Composable
fun MusicSlider(mediaPlayer: MediaPlayer, trackList: List<Int>) {
    var position by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(1f) } // Избегаем деления на 0
    var currentTrackIndex by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(mediaPlayer) {
        duration = try {
            mediaPlayer.duration.toFloat().coerceAtLeast(1f)
        } catch (e: Exception) {
            1f
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            position = mediaPlayer.currentPosition.toFloat()
            delay(1000) // Обновляем раз в секунду
        }
    }

    Slider(
        value = position,
        onValueChange = { newPosition ->
            position = newPosition // UI обновляется сразу
        },
        onValueChangeFinished = {
            mediaPlayer.seekTo(position.toInt()) // Перематываем трек
        },
        valueRange = 0f..duration
    )
}
