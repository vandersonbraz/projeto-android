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
import com.calmare.app.data.Sound
import com.calmare.app.managers.BillingManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private lateinit var billingManager: BillingManager
    private lateinit var preferencesManager: PreferencesManager
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLooping = false  // Começa DESATIVADO
    private var isFavorite = false
    private var isAudioPrepared = false
    private var isAutoPlayEnabled = false  // Reprodução automática da próxima faixa
    private var shouldAutoPlayOnPrepared = false  // Flag temporária para autoplay ao preparar
    private var isUserPremium = false  // Status premium do usuário (para áudio em segundo plano)

    // Sistema de anúncios
    private lateinit var adViewBanner: AdView
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    // Contador de ações (a cada 6 ações mostra rewarded)
    private var actionCounter = 0  // Conta ações: pular, retroceder, avançar, completar música
    private val MAX_ACTIONS = 6

    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvAlbumArt: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnFavorite: ImageButton
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

    // Playlist navigation
    private var playlist: List<Sound> = emptyList()
    private var currentIndex: Int = 0

    // Sessão Rápida - Respiração (regras especiais de anúncios)
    private var isQuickBreathingSession = false

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
        isQuickBreathingSession = intent.getBooleanExtra("IS_QUICK_SESSION", false)

        // Get playlist and current index
        playlist = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("PLAYLIST", Sound::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<Sound>("PLAYLIST") ?: emptyList()
        }
        currentIndex = intent.getIntExtra("CURRENT_INDEX", 0)

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
        loadAutoPlayState()

        // Se for sessão rápida (marcada pelo Intent), esconde botões de pular faixa
        if (isQuickBreathingSession) {
            btnPreviousTrack.visibility = android.view.View.GONE
            btnNextTrack.visibility = android.view.View.GONE
        }

        // Botão começa como Play (não Pause)
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play)

        // Botão de loop começa DESATIVADO (branco igual ao volume)
        btnLoop.setColorFilter(getColor(R.color.white))
        btnLoop.alpha = 1.0f

        // Setup buttons
        setupClickListeners()

        // Observe premium status
        billingManager.initialize()
        billingManager.isPremium.observe(this) { premium ->
            isUserPremium = premium
            updateAdsVisibility()  // Atualiza visibilidade dos anúncios
        }

        // Initialize ads
        initializeAds()

        // Prepare media player
        prepareMediaPlayer()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnPlayPause.setOnClickListener { togglePlayPause() }

        btnFavorite.setOnClickListener { toggleFavorite() }

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

    private fun loadAutoPlayState() {
        lifecycleScope.launch {
            preferencesManager.autoPlayEnabled.collect { enabled ->
                isAutoPlayEnabled = enabled
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

    private fun playPreviousTrack() {
        // Sessão rápida respiração: botões de pular não fazem nada
        if (isQuickBreathingSession) {
            return
        }

        // Se não tem playlist ou tem apenas 1 áudio: não faz nada
        if (playlist.isEmpty() || playlist.size <= 1) {
            return
        }

        // Salva estado atual
        val wasPlayingBeforeAd = isPlaying

        // Calcula próximo índice
        val nextIndex = if (currentIndex <= 0) {
            playlist.size - 1
        } else {
            currentIndex - 1
        }

        // PRIMEIRO: Registra ação e mostra anúncio (pode bloquear se for ação 5)
        registerAction {
            // DEPOIS do anúncio: Muda de faixa
            currentIndex = nextIndex
            loadAndPlaySound(playlist[currentIndex], autoPlay = wasPlayingBeforeAd)
        }
    }

    private fun playNextTrack() {
        // Sessão rápida respiração: botões de pular não fazem nada
        if (isQuickBreathingSession) {
            return
        }

        // Se não tem playlist ou tem apenas 1 áudio: não faz nada
        if (playlist.isEmpty() || playlist.size <= 1) {
            return
        }

        // Salva estado atual
        val wasPlayingBeforeAd = isPlaying

        // Calcula próximo índice
        val nextIndex = if (currentIndex >= playlist.size - 1) {
            0
        } else {
            currentIndex + 1
        }

        // PRIMEIRO: Registra ação e mostra anúncio (pode bloquear se for ação 5)
        registerAction {
            // DEPOIS do anúncio: Muda de faixa
            currentIndex = nextIndex
            loadAndPlaySound(playlist[currentIndex], autoPlay = wasPlayingBeforeAd)
        }
    }

    private fun loadAndPlaySound(sound: Sound, autoPlay: Boolean = false) {
        // Para o player atual
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
        handler.removeCallbacks(updateProgressRunnable)

        // Atualiza variáveis do som
        soundId = sound.id
        soundTitle = sound.title
        soundCategory = sound.category
        soundDuration = sound.duration
        soundUrl = sound.audioUrl
        isPremium = sound.isPremium

        // isQuickBreathingSession não muda ao trocar de faixa
        // (só é definido uma vez no onCreate pelo Intent)

        // Atualiza UI
        tvTitle.text = soundTitle
        tvCategory.text = "$soundCategory • ${formatDuration(soundDuration)}"
        tvTotalTime.text = formatTime(soundDuration)
        updateAlbumArt()
        loadFavoriteState()

        // Reseta seekbar e tempo
        seekBar.progress = 0
        tvCurrentTime.text = formatTime(0)

        // Estado inicial
        isPlaying = false
        isAudioPrepared = false
        shouldAutoPlayOnPrepared = autoPlay  // Define se deve tocar automaticamente
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play)

        // Prepara novo áudio
        prepareMediaPlayer()
    }

    private fun toggleLoop() {
        isLooping = !isLooping
        // NÃO aplica loop no MediaPlayer (gerenciamos manualmente para garantir anúncios)
        // mediaPlayer?.isLooping = isLooping

        // Destaque visual quando ativo (verde) ou desativado (branco)
        if (isLooping) {
            btnLoop.setColorFilter(getColor(R.color.success))  // Verde quando ativo
            btnLoop.alpha = 1.0f
        } else {
            btnLoop.setColorFilter(getColor(R.color.white))  // Branco quando desativado (igual ao volume)
            btnLoop.alpha = 1.0f
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
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(soundUrl)
                isLooping = false  // Começa DESATIVADO
                prepareAsync()

                setOnPreparedListener {
                    tvTotalTime.text = formatTime(duration / 1000)
                    isAudioPrepared = true
                    btnPlayPause.isEnabled = true
                    // Se shouldAutoPlayOnPrepared está true, toca automaticamente
                    if (this@PlayerActivity.shouldAutoPlayOnPrepared) {
                        this@PlayerActivity.shouldAutoPlayOnPrepared = false
                        this@PlayerActivity.startPlayback()
                    }
                }

                setOnCompletionListener {
                    // Quando o áudio termina, SEMPRE processa anúncios (mesmo com loop ativo)

                    // Sessão Rápida Respiração: mostra apenas rewarded de 30s
                    if (this@PlayerActivity.isQuickBreathingSession) {
                        // Volta ao início
                        it.seekTo(0)
                        this@PlayerActivity.seekBar.progress = 0
                        this@PlayerActivity.tvCurrentTime.text = this@PlayerActivity.formatTime(0)

                        // Pausa e mostra anúncio de 30s (SEMPRE, mesmo com loop)
                        this@PlayerActivity.pausePlayback()

                        if (!this@PlayerActivity.isUserPremium) {
                            Toast.makeText(
                                this@PlayerActivity,
                                "🎬 Assista ao anúncio para continuar!",
                                Toast.LENGTH_LONG
                            ).show()
                            // Mostra anúncio e DEPOIS decide se retoma playback
                            this@PlayerActivity.showRewardedAd {
                                // Callback executado APÓS o anúncio
                                if (this@PlayerActivity.isLooping) {
                                    this@PlayerActivity.startPlayback()
                                } else {
                                    // Sem loop: garante que está pausado e botão correto
                                    this@PlayerActivity.isPlaying = false
                                    this@PlayerActivity.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                                }
                            }
                        } else {
                            // Premium não vê anúncio
                            if (this@PlayerActivity.isLooping) {
                                this@PlayerActivity.startPlayback()
                            } else {
                                // Sem loop: garante que está pausado e botão correto
                                this@PlayerActivity.isPlaying = false
                                this@PlayerActivity.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                            }
                        }
                    } else {
                        // Resto do app: sistema normal de ações
                        // Salva estado antes de registrar ação (porque pode pausar no anúncio de 30s)
                        val shouldContinuePlaying = true
                        val wasLooping = this@PlayerActivity.isLooping
                        val hasAutoPlay = this@PlayerActivity.isAutoPlayEnabled
                        val hasMultipleTracks = this@PlayerActivity.playlist.size > 1

                        // Registra ação (música terminou) e mostra anúncio (SEMPRE, mesmo com loop)
                        this@PlayerActivity.registerAction {
                            // Callback executado APÓS anúncio (se houver)
                            // Decide o que fazer após mostrar anúncio
                            if (wasLooping) {
                                // Loop da MÚSICA ativo: volta ao início da MESMA música e recomeça
                                it.seekTo(0)
                                this@PlayerActivity.seekBar.progress = 0
                                this@PlayerActivity.tvCurrentTime.text = this@PlayerActivity.formatTime(0)
                                this@PlayerActivity.startPlayback()
                            } else if (hasAutoPlay && hasMultipleTracks) {
                                // Autoplay ativo E tem mais de 1 música: vai pra próxima (loop infinito na playlist)
                                this@PlayerActivity.currentIndex++
                                // Se chegou no fim, volta pra primeira (loop infinito)
                                if (this@PlayerActivity.currentIndex >= this@PlayerActivity.playlist.size) {
                                    this@PlayerActivity.currentIndex = 0
                                }
                                this@PlayerActivity.loadAndPlaySound(this@PlayerActivity.playlist[this@PlayerActivity.currentIndex], autoPlay = shouldContinuePlaying)
                            } else {
                                // Sem loop e sem autoplay (ou só tem 1 música): volta ao início e pausa
                                it.seekTo(0)
                                this@PlayerActivity.isPlaying = false
                                this@PlayerActivity.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                                this@PlayerActivity.handler.removeCallbacks(this@PlayerActivity.updateProgressRunnable)
                                this@PlayerActivity.seekBar.progress = 0
                                this@PlayerActivity.tvCurrentTime.text = this@PlayerActivity.formatTime(0)
                            }
                        }
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

    // ========== SISTEMA DE ANÚNCIOS ==========

    private fun initializeAds() {
        // Inicializa Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Inicializa banner
        adViewBanner = findViewById(R.id.adView_banner)
        if (!isUserPremium) {
            val adRequest = AdRequest.Builder().build()
            adViewBanner.loadAd(adRequest)
        }

        // Carrega intersticial
        loadInterstitialAd()

        // Carrega rewarded ad
        loadRewardedAd()
    }

    private fun updateAdsVisibility() {
        // Premium: oculta banner e não mostra anúncios
        if (isUserPremium) {
            adViewBanner.visibility = android.view.View.GONE
        } else {
            adViewBanner.visibility = android.view.View.VISIBLE
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",  // Test ID do intersticial
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            "ca-app-pub-3940256099942544/5224354917",  // Test ID do rewarded
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    private fun showInterstitialAd() {
        // Só mostra se não for premium
        if (!isUserPremium && interstitialAd != null) {
            interstitialAd?.show(this)
            loadInterstitialAd()  // Carrega o próximo
        }
    }

    private fun showRewardedAd(onAdClosed: (() -> Unit)? = null) {
        if (rewardedAd != null) {
            rewardedAd?.show(this) { _ ->
                // Usuário assistiu ao anúncio completo, reseta contador
                actionCounter = 0
                Toast.makeText(
                    this,
                    "✅ Continue ouvindo suas músicas!",
                    Toast.LENGTH_SHORT
                ).show()
                // Executa callback após anúncio (se fornecido)
                onAdClosed?.invoke()
            }
            loadRewardedAd()  // Carrega o próximo
        } else {
            // Se não tem anúncio carregado, reseta contador e executa callback imediatamente
            actionCounter = 0
            Toast.makeText(
                this,
                "⏳ Carregando anúncio, aguarde...",
                Toast.LENGTH_SHORT
            ).show()
            loadRewardedAd()
            onAdClosed?.invoke()
        }
    }

    private fun registerAction(onComplete: (() -> Unit)? = null) {
        // Premium não vê anúncios
        if (isUserPremium) {
            onComplete?.invoke()
            return
        }

        actionCounter++

        if (actionCounter >= MAX_ACTIONS) {
            // 6ª ação: PAUSA e mostra rewarded de 30s
            if (isPlaying) {
                pausePlayback()
            }
            Toast.makeText(
                this,
                "🎬 Assista ao anúncio para continuar ouvindo!",
                Toast.LENGTH_LONG
            ).show()
            // Mostra rewarded e executa callback APÓS o anúncio
            showRewardedAd {
                onComplete?.invoke()
            }
        } else {
            // Ações 1-5: mostra intersticial de 5s (não bloqueia, callback imediato)
            showInterstitialAd()
            onComplete?.invoke()
        }
    }

    override fun onPause() {
        super.onPause()
        // Áudio em segundo plano é EXCLUSIVO para usuários PREMIUM
        if (!isUserPremium && isPlaying) {
            pausePlayback()
            Toast.makeText(
                this,
                "⭐ PREMIUM: Ouça em segundo plano seus áudios favoritos! Assine por R$ 14,90/mês",
                Toast.LENGTH_LONG
            ).show()
        }
        // Se é premium, o áudio continua tocando em segundo plano
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
