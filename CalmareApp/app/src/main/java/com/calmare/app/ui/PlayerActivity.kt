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
    private var isAudioPrepared = false

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
    private lateinit var btnPreviousTrack: ImageButton
    private lateinit var btnNextTrack: ImageButton
    private lateinit var btnLoop: ImageButton
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
        btnPreviousTrack = findViewById(R.id.btn_previous_track)
        btnNextTrack = findViewById(R.id.btn_next_track)
        btnLoop = findViewById(R.id.btn_loop)
        btnVolume = findViewById(R.id.btn_volume)

        // Setup UI
        tvTitle.text = soundTitle
        tvCategory.text = "$soundCategory • ${formatDuration(soundDuration)}"
        tvTotalTime.text = formatTime(soundDuration)
        updateAlbumArt()
        loadFavoriteState()

        // Botão começa como Play (não Pause)
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play)

        // Botão de loop começa verde (ativo por padrão)
        btnLoop.setColorFilter(getColor(R.color.success))
        btnLoop.alpha = 1.0f

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

        btnPreviousTrack.setOnClickListener { playPreviousTrack() }

        btnNextTrack.setOnClickListener { playNextTrack() }

        btnLoop.setOnClickListener { toggleLoop() }

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
        if (isFavorite) {
            // Vermelho quando favoritado
            btnFavorite.setColorFilter(getColor(R.color.error))
            btnFavorite.alpha = 1.0f
        } else {
            // Cinza quando não favoritado
            btnFavorite.setColorFilter(getColor(R.color.text_secondary))
            btnFavorite.alpha = 0.5f
        }
    }

    private fun seekBy(milliseconds: Int) {
        mediaPlayer?.let {
            val newPosition = (it.currentPosition + milliseconds).coerceIn(0, it.duration)
            it.seekTo(newPosition)
            updateProgress()
        }
    }

    private fun playPreviousTrack() {
        // Botão de faixa anterior sempre funciona (independente do loop)
        // TODO: Implementar navegação para faixa anterior quando houver playlist
        Toast.makeText(this, "⏮️ Primeira faixa da playlist", Toast.LENGTH_SHORT).show()
    }

    private fun playNextTrack() {
        // Botão de próxima faixa sempre funciona (independente do loop)
        // TODO: Implementar navegação para próxima faixa quando houver playlist
        Toast.makeText(this, "⏭️ Última faixa da playlist", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLoop() {
        isLooping = !isLooping
        mediaPlayer?.isLooping = isLooping

        // Destaque visual quando ativo (verde) ou desativado (cinza)
        if (isLooping) {
            btnLoop.setColorFilter(getColor(R.color.success))  // Verde quando ativo
            btnLoop.alpha = 1.0f
        } else {
            btnLoop.setColorFilter(getColor(R.color.text_secondary))  // Cinza quando desativado
            btnLoop.alpha = 0.4f
        }

        val message = if (isLooping) "Loop ativado 🔁" else "Loop desativado"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatDuration(seconds: Int): String {
        return "${seconds / 60} min"
    }

    private fun formatTime(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val secs = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, secs)
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                val current = it.currentPosition / 1000
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
            Toast.makeText(this, "⚠️ URL de áudio não disponível", Toast.LENGTH_LONG).show()
            btnPlayPause.isEnabled = false
            return
        }

        try {
            // Mostra mensagem de carregamento
            Toast.makeText(this, "Carregando áudio...", Toast.LENGTH_SHORT).show()

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
                    // Player começa pausado - usuário precisa clicar em Play
                    isAudioPrepared = true
                    btnPlayPause.isEnabled = true
                    Toast.makeText(this@PlayerActivity, "✅ Áudio pronto!", Toast.LENGTH_SHORT).show()
                }

                setOnCompletionListener {
                    // Quando o áudio termina (sem loop)
                    if (!this@PlayerActivity.isLooping) {
                        // Volta ao início
                        it.seekTo(0)
                        // Muda para pausado
                        this@PlayerActivity.isPlaying = false
                        this@PlayerActivity.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                        this@PlayerActivity.handler.removeCallbacks(this@PlayerActivity.updateProgressRunnable)
                        // Atualiza barra para 0
                        this@PlayerActivity.seekBar.progress = 0
                        this@PlayerActivity.tvCurrentTime.text = this@PlayerActivity.formatTime(0)
                    }
                }

                setOnErrorListener { _, what, extra ->
                    Toast.makeText(
                        this@PlayerActivity,
                        "❌ Erro ao carregar áudio (código: $what/$extra)\nVerifique sua conexão",
                        Toast.LENGTH_LONG
                    ).show()
                    btnPlayPause.isEnabled = false
                    true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Erro: ${e.message}", Toast.LENGTH_LONG).show()
            btnPlayPause.isEnabled = false
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
        if (!isAudioPrepared) {
            Toast.makeText(this, "⏳ Aguarde o áudio carregar...", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer?.let {
            try {
                if (!it.isPlaying) {
                    it.start()
                    isPlaying = true
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                    handler.post(updateProgressRunnable)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                Toast.makeText(this, "❌ Erro ao reproduzir. Tente novamente.", Toast.LENGTH_SHORT).show()
                isAudioPrepared = false
                btnPlayPause.isEnabled = false
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
