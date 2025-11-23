package com.calmare.app.ui

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.calmare.app.R
import com.calmare.app.managers.BillingManager
import java.io.IOException

class PlayerActivity : AppCompatActivity() {

    private lateinit var billingManager: BillingManager
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    private lateinit var tvTitle: TextView
    private lateinit var tvAlbumArt: TextView
    private lateinit var btnPlayPause: Button
    private lateinit var btnSubscribe: Button

    private var soundTitle: String = ""
    private var soundUrl: String = ""
    private var isPremium: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        billingManager = BillingManager(this)

        // Get intent extras
        soundTitle = intent.getStringExtra("SOUND_TITLE") ?: "Som Relaxante"
        soundUrl = intent.getStringExtra("SOUND_URL") ?: ""
        isPremium = intent.getBooleanExtra("IS_PREMIUM", false)

        // Initialize views
        tvTitle = findViewById(R.id.track_title)
        tvAlbumArt = findViewById(R.id.album_art)
        btnPlayPause = findViewById(R.id.btn_play_pause)
        btnSubscribe = findViewById(R.id.btn_subscribe_player)

        // Setup UI
        tvTitle.text = soundTitle
        updateAlbumArt()

        // Setup buttons
        btnPlayPause.setOnClickListener {
            togglePlayPause()
        }

        btnSubscribe.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }

        // Observe premium status
        billingManager.initialize()
        billingManager.isPremium.observe(this) { isPremiumUser ->
            handlePremiumStatus(isPremiumUser)
        }

        // Prepare media player
        prepareMediaPlayer()
    }

    private fun updateAlbumArt() {
        // Set emoji based on sound title
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

    private fun handlePremiumStatus(isPremiumUser: Boolean) {
        if (isPremiumUser) {
            // Premium users don't see the subscribe button
            btnSubscribe.visibility = android.view.View.GONE
        } else if (isPremium && !isPremiumUser) {
            // Premium content for free users - show paywall
            btnSubscribe.visibility = android.view.View.VISIBLE
            btnPlayPause.isEnabled = false
        }
    }

    private fun prepareMediaPlayer() {
        if (soundUrl.isEmpty()) {
            // No URL provided, use placeholder
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
                    // Auto-play when ready
                    startPlayback()
                }

                setOnErrorListener { _, _, _ ->
                    // Handle error
                    btnPlayPause.isEnabled = false
                    true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
                btnPlayPause.text = "⏸"
            }
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                btnPlayPause.text = "▶"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
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
