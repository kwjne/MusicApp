package com.github.kwjne.jetpacktest

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class MusicViewModel(application: Application) : AndroidViewModel(application){
    private var _mediaPlayer: MediaPlayer?= null
    private val context = application.applicationContext

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> get() = _isPlaying

    private val _currentTrackIndex = mutableStateOf(0)
    val currentTrackIndex: State<Int> get() = _currentTrackIndex

    val trackList = listOf(R.raw.cupsizeklej, R.raw.bobr, R.raw.deephouse, R.raw.rammsteinengel)

    init {
        createPlayer(trackList[_currentTrackIndex.value])
    }

    private fun createPlayer(resId: Int) {
        _mediaPlayer?.release()
        _mediaPlayer = MediaPlayer.create(context, resId).apply {
            setOnCompletionListener {
                next()
            }
        }
    }

    fun togglePlay() {
        _mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    fun next() {
        _currentTrackIndex.value = (_currentTrackIndex.value + 1) % trackList.size
        createPlayer(trackList[_currentTrackIndex.value])
        _mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun prev(){
        _currentTrackIndex.value = (_currentTrackIndex.value - 1 + trackList.size) % trackList.size
        createPlayer(trackList[_currentTrackIndex.value])
        _mediaPlayer?.start()
        _isPlaying.value = true
    }

    override fun onCleared() {
        _mediaPlayer?.release()
        super.onCleared()
    }
}

// TODO подключить ViewModel, удалить старую логику, передать MusicControls из ViewModel
// блаблабла