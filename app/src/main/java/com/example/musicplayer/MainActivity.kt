package com.example.musicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var audioManager: AudioManager

    private val handler = Handler()
    private val songs = listOf(
        R.raw.song1, //  свои файлы из папки raw
    )
    private var currentSongIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // элементы интерфейса
        val playerImage = findViewById<ImageView>(R.id.player_image)
        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val btnNext = findViewById<Button>(R.id.btn_next)
        val btnPrevious = findViewById<Button>(R.id.btn_previous)
        seekBar = findViewById(R.id.seekBar)
        volumeSeekBar = findViewById(R.id.volumeSeekBar)

        // Инициализация MediaPlayer с первым треком
        mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex])

        // Настройка SeekBar для громкости
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Обновление SeekBar для прогресса трека
        seekBar.max = mediaPlayer.duration
        handler.postDelayed(object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Кнопки управления воспроизведением
        btnPlay.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }

        btnPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        btnStop.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex])
            }
        }

        btnNext.setOnClickListener {
            if (currentSongIndex < songs.size - 1) {
                currentSongIndex++
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex])
                mediaPlayer.start()
            }
        }

        btnPrevious.setOnClickListener {
            if (currentSongIndex > 0) {
                currentSongIndex--
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex])
                mediaPlayer.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
}
