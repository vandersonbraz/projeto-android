package com.calmare.app.ui

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.managers.BillingManager
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private lateinit var billingManager: BillingManager
    private lateinit var preferencesManager: PreferencesManager
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLooping = true
    private var isFavorite = false

    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvAlbumArt: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnFavorite: ImageButton
    private lateinit var btnRewind: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnLoop: ImageButton
    private lateinit var btnTimer: ImageButton
    private lateinit var btnVolume: ImageButton

    private var soundId: Int = 0
    private var soundTitle: String = ""
    private var soundCategory: String = ""
    private var soundDuration: Int = 0
    private var soundUrl: String = ""
    private var isPremium: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        billingManager = BillingManager(this, lifecycleScope)
        preferencesManager = PreferencesManager(this)

        // Get intent extras
        soundId = intent.getIntExtra("SOUND_ID", 0)
        soundTitle = intent.getStringExtra("SOUND_TITLE") ?: "Som Relaxante"
        soundCategory = intent.getStringExtra("SOUND_CATEGORY") ?: "Relaxamento"
        soundDuration = intent.getIntExtra("SOUND_DURATION", 600)
        soundUrl = intent.getStringExtra("SOUND_URL") ?: ""
        isPremium = intent.getBooleanExtra("IS_PREMIUM", false)

        // Initialize views
        tvTitle = findViewById(R.id.track_title)
        tvCategory = findViewById(R.id.track_category)
        tvAlbumArt = findViewById(R.id.album_art)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalTime = findViewById(R.id.tv_total_time)
        seekBar = findViewById(R.id.seekbar_progress)
        btnPlayPause = findViewById(R.id.btn_play_pause)
        btnBack = findViewById(R.id.btn_back)
        btnFavorite = findViewById(R.id.btn_favorite_player)
        btnRewind = findViewById(R.id.btn_rewind)
        btnForward = findViewById(R.id.btn_forward)
        btnLoop = findViewById(R.id.btn_loop)
        btnTimer = findViewById(R.id.btn_timer)
        btnVolume = findViewById(R.id.btn_volume)

        // Setup UI
        tvTitle.text = soundTitle
        tvCategory.text = "$soundCategory • ${formatDuration(soundDuration)}"
        tvTotalTime.text = formatTime(soundDuration)
        updateAlbumArt()
        loadFavoriteState()

        // Setup buttons
        setupClickListeners()

        // Observe premium status
        billingManager.initialize()

        // Prepare media player
        prepareMediaPlayer()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnPlayPause.setOnClickListener { togglePlayPause() }

        btnFavorite.setOnClickListener { toggleFavorite() }

        btnRewind.setOnClickListener { seekBy(-15000) }

        btnForward.setOnClickListener { seekBy(15000) }

        btnLoop.setOnClickListener { toggleLoop() }

        btnTimer.setOnClickListener {
            Toast.makeText(this, "Timer: Em breve!", Toast.LENGTH_SHORT).show()
        }

        btnVolume.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_SAME,
                AudioManager.FLAG_SHOW_UI
            )
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.let {
                        val newPosition = (progress * it.duration) / 100
                        it.seekTo(newPosition)
                        tvCurrentTime.text = formatTime(newPosition / 1000)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun loadFavoriteState() {
        lifecycleScope.launch {
            preferencesManager.favoriteSoundIds.collect { favoriteIds ->
                isFavorite = favoriteIds.contains(soundId)
                updateFavoriteButton()
            }
        }
    }

    private fun toggleFavorite() {
        lifecycleScope.launch {
            if (isFavorite) {
                preferencesManager.removeFavorite(soundId)
                Toast.makeText(this@PlayerActivity, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
            } else {
                preferencesManager.addFavorite(soundId)
                Toast.makeText(this@PlayerActivity, "Adicionado aos favoritos ❤️", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteButton() {
        btnFavorite.alpha = if (isFavorite) 1.0f else 0.5f
    }

    private fun seekBy(milliseconds: Int) {
        mediaPlayer?.let {
            val newPosition = (it.currentPosition + milliseconds).coerceIn(0, it.duration)
            it.seekTo(newPosition)
            updateProgress()
        }
    }

    private fun toggleLoop() {
        isLooping = !isLooping
        mediaPlayer?.isLooping = isLooping
        btnLoop.alpha = if (isLooping) 1.0f else 0.4f
        val message = if (isLooping) "Loop ativado 🔁" else "Loop desativado"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatDuration(seconds: Int): String {
        return "${seconds / 60} min"
    }

    private fun formatTime(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val secs = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%d:%02d", minutes, secs)
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                val current = it.currentPosition / 1000
                val duration = it.duration / 1000
                val progress = ((it.currentPosition.toFloat() / it.duration) * 100).toInt()

                tvCurrentTime.text = formatTime(current)
                seekBar.progress = progress
            }
        }
    }

    private fun updateAlbumArt() {
        val emoji = when {
            soundTitle.contains("Chuva", ignoreCase = true) -> "🌧️"
            soundTitle.contains("Mar", ignoreCase = true) || soundTitle.contains("Onda", ignoreCase = true) -> "🌊"
            soundTitle.contains("Floresta", ignoreCase = true) -> "🌳"
            soundTitle.contains("Meditação", ignoreCase = true) -> "🧘"
            soundTitle.contains("Respiração", ignoreCase = true) -> "🫁"
            soundTitle.contains("Fogo", ignoreCase = true) || soundTitle.contains("Lareira", ignoreCase = true) -> "🔥"
            soundTitle.contains("Piano", ignoreCase = true) -> "🎹"
            soundTitle.contains("Ruído", ignoreCase = true) || soundTitle.contains("Sono", ignoreCase = true) -> "😴"
            else -> "🎵"
        }
        tvAlbumArt.text = emoji
    }

    private fun prepareMediaPlayer() {
        if (soundUrl.isEmpty()) {
            Toast.makeText(this, "URL de áudio não disponível", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(soundUrl)
                isLooping = true
                prepareAsync()

                setOnPreparedListener {
                    tvTotalTime.text = formatTime(duration / 1000)
                    startPlayback()
                }

                setOnErrorListener { _, _, _ ->
                    Toast.makeText(this@PlayerActivity, "Erro ao carregar áudio", Toast.LENGTH_SHORT).show()
                    btnPlayPause.isEnabled = false
                    true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                handler.post(updateProgressRunnable)
            }
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                handler.removeCallbacks(updateProgressRunnable)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        billingManager.destroy()
    }
}
