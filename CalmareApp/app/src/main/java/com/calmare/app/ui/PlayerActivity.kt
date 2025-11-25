package com.calmare.app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
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
import com.calmare.app.managers.MediaNotificationManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    companion object {
        // Instância atual do PlayerActivity (para parar música anterior)
        private var currentInstance: PlayerActivity? = null

        // Indica se havia música tocando antes de abrir novo player
        var wasPlaying = false
    }

    private lateinit var billingManager: BillingManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mediaNotificationManager: MediaNotificationManager
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLooping = false  // Começa DESATIVADO
    private var isFavorite = false
    private var isAudioPrepared = false
    private var isAutoPlayEnabled = false  // Reprodução automática da próxima faixa
    private var shouldAutoPlayOnPrepared = false  // Flag temporária para autoplay ao preparar
    var isUserPremium = false  // Status premium do usuário (para áudio em segundo plano + notificação)

    // BroadcastReceiver para receber comandos dos controles da notificação
    private val mediaControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action") ?: return
            handleMediaCommand(action)
        }
    }

    // Sistema de anúncios
    private lateinit var adViewBanner: AdView
    private var rewardedInterstitialAd: com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    // Contador de ações (a cada 8 PULOS mostra rewarded 30s)
    private var actionCounter = 0  // Conta APENAS quando usuário PULA manualmente
    private val MAX_ACTIONS = 8

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
    private lateinit var btnStop: ImageButton
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

        // Configura botões de volume do dispositivo para controlar o áudio da música
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Para a instância anterior (se existir) e salva se estava tocando
        currentInstance?.let { previousInstance ->
            wasPlaying = previousInstance.isPlaying
            previousInstance.stopAndReleasePlayer()
        }

        // Define esta como a instância atual
        currentInstance = this

        billingManager = BillingManager(this, lifecycleScope)
        preferencesManager = PreferencesManager(this)

        // Initialize Media Notification Manager (controles na notificação)
        mediaNotificationManager = MediaNotificationManager(this, this)
        mediaNotificationManager.initializeMediaSession()

        // Registra BroadcastReceiver para receber comandos da notificação
        val filter = IntentFilter("com.calmare.app.MEDIA_CONTROL")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mediaControlReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(mediaControlReceiver, filter)
        }

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
        btnStop = findViewById(R.id.btn_stop)
        btnVolume = findViewById(R.id.btn_volume)

        // Inicializa o detector de duração
        com.calmare.app.utils.AudioDurationDetector.init(this)

        // Setup UI
        tvTitle.text = soundTitle

        // Mostra apenas a categoria (sem duração)
        tvCategory.text = soundCategory

        // Usa duração do cache se disponível, senão usa placeholder
        val cachedDuration = com.calmare.app.utils.AudioDurationDetector.getCachedDuration(soundId)
        tvTotalTime.text = formatTime(cachedDuration)

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
        // Botão VOLTAR do app: chama finish() para voltar (música continua via notificação)
        btnBack.setOnClickListener { finish() }

        btnPlayPause.setOnClickListener { togglePlayPause() }

        btnFavorite.setOnClickListener { toggleFavorite() }

        btnPreviousTrack.setOnClickListener { playPreviousTrack() }

        btnNextTrack.setOnClickListener { playNextTrack() }

        btnLoop.setOnClickListener { toggleLoop() }

        btnStop.setOnClickListener { stopPlayback() }

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
        // Para COMPLETAMENTE o player atual antes de carregar o novo
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()  // Para a reprodução
                }
                reset()  // Reseta o estado
                release()  // Libera recursos
            } catch (e: IllegalStateException) {
                // MediaPlayer já estava em estado inválido, apenas libera
                release()
            }
        }
        mediaPlayer = null
        isPlaying = false
        isAudioPrepared = false
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

        // Mostra apenas a categoria (sem duração)
        tvCategory.text = soundCategory

        // Usa duração do cache se disponível, senão usa placeholder
        val cachedDuration = com.calmare.app.utils.AudioDurationDetector.getCachedDuration(soundId)
        tvTotalTime.text = formatTime(cachedDuration)

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

    private fun stopAndReleasePlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
            mediaPlayer = null
            isPlaying = false
            handler.removeCallbacks(updateProgressRunnable)
            mediaNotificationManager.hideNotification()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

                // Detecta se é arquivo local (raw/) ou URL da internet
                if (soundUrl.startsWith("raw/")) {
                    // Arquivo local: usa sistema de recursos do Android
                    val resourceName = soundUrl.substringAfter("raw/")
                    val resourceId = resources.getIdentifier(resourceName, "raw", packageName)

                    if (resourceId == 0) {
                        Toast.makeText(
                            this@PlayerActivity,
                            "❌ Arquivo de áudio '$resourceName' não encontrado na pasta raw/",
                            Toast.LENGTH_LONG
                        ).show()
                        btnPlayPause.isEnabled = false
                        return
                    }

                    val uri = android.net.Uri.parse("android.resource://$packageName/$resourceId")
                    setDataSource(this@PlayerActivity, uri)
                } else {
                    // URL da internet
                    setDataSource(soundUrl)
                }

                isLooping = false  // Começa DESATIVADO
                setVolume(1.0f, 1.0f)  // Volume máximo no app (usuário regula pelos botões do celular)
                prepareAsync()

                setOnPreparedListener {
                    // Detecta duração REAL do áudio (em milissegundos)
                    val realDurationSeconds = duration / 1000

                    // Atualiza a duração na variável do Activity
                    this@PlayerActivity.soundDuration = realDurationSeconds

                    // Atualiza UI com tempo real detectado
                    tvTotalTime.text = formatTime(realDurationSeconds)

                    // Categoria já está definida (sem duração)
                    // tvCategory.text já foi definido com apenas soundCategory

                    isAudioPrepared = true
                    btnPlayPause.isEnabled = true

                    // Auto-play se estava tocando antes OU se shouldAutoPlayOnPrepared
                    if (this@PlayerActivity.shouldAutoPlayOnPrepared || wasPlaying) {
                        this@PlayerActivity.shouldAutoPlayOnPrepared = false
                        wasPlaying = false  // Reseta a flag
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
                        // Resto do app: música terminou NATURALMENTE (NÃO conta como ação, NÃO mostra anúncio)
                        val shouldContinuePlaying = true
                        val wasLooping = this@PlayerActivity.isLooping
                        val hasAutoPlay = this@PlayerActivity.isAutoPlayEnabled
                        val hasMultipleTracks = this@PlayerActivity.playlist.size > 1

                        // NÃO registra ação quando música termina naturalmente (apenas quando PULAR manualmente)
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
                    // Atualiza notificação com estado "tocando"
                    updateMediaNotification()
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
                // Atualiza notificação com estado "pausado"
                updateMediaNotification()
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
            it.seekTo(0)
            isPlaying = false
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            handler.removeCallbacks(updateProgressRunnable)
            seekBar.progress = 0
            tvCurrentTime.text = formatTime(0)
            Toast.makeText(this, "⏹️ Reprodução parada", Toast.LENGTH_SHORT).show()
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

        // Carrega rewarded intersticial (ações 1-7)
        loadRewardedInterstitialAd()

        // Carrega rewarded ad (ação 8)
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

    private fun loadRewardedInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            this,
            "ca-app-pub-5255274256204364/7974349040",  // ID de produção - Intersticial premiado
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedInterstitialAd = null
                }
            }
        )
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            "ca-app-pub-5255274256204364/5500213038",  // ID de produção - Premiado
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

    private fun showRewardedInterstitialAd(onAdClosed: (() -> Unit)? = null) {
        // Só mostra se não for premium
        if (!isUserPremium && rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Ad fechado, executa callback
                    onAdClosed?.invoke()
                    loadRewardedInterstitialAd()  // Carrega o próximo
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    // Se falhou, executa callback mesmo assim
                    onAdClosed?.invoke()
                    loadRewardedInterstitialAd()
                }
            }
            rewardedInterstitialAd?.show(this) { _ ->
                // Recompensa concedida (mesmo que mínima)
            }
        } else {
            // Se não tem anúncio ou é premium, executa callback imediatamente
            onAdClosed?.invoke()
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
            // 8ª ação (pulo): PAUSA e mostra rewarded de 30s
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
        } else if (actionCounter % 2 == 0) {
            // Ações PARES (2, 4, 6): mostra rewarded intersticial de 10-15s
            showRewardedInterstitialAd {
                onComplete?.invoke()
            }
        } else {
            // Ações ÍMPARES (1, 3, 5, 7): SEM anúncio, executa callback direto
            onComplete?.invoke()
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // CONTROLES DE MÍDIA NA NOTIFICAÇÃO
    // ══════════════════════════════════════════════════════════════════════

    fun handleMediaCommand(command: String) {
        when (command) {
            "PLAY" -> startPlayback()
            "PAUSE" -> pausePlayback()
            "STOP" -> {
                stopPlayback()
                mediaNotificationManager.hideNotification()
                finish()  // Fecha o PlayerActivity
            }
            "NEXT" -> playNextTrack()
            "PREVIOUS" -> playPreviousTrack()
        }
    }

    private fun updateMediaNotification() {
        // Mostra apenas a categoria (sem duração)
        mediaNotificationManager.updateNotification(
            title = soundTitle,
            artist = soundCategory,
            isPlaying = isPlaying
        )
    }

    // ══════════════════════════════════════════════════════════════════════

    override fun onPause() {
        super.onPause()
        // Quando o app perde foco (não está mais visível)
    }

    override fun onStop() {
        super.onStop()
        // FREE e PREMIUM:
        // - Apertar VOLTAR (isFinishing = true): música CONTINUA (navega pelo app)
        // - Minimizar/Bloquear (isFinishing = false): FREE PARA, PREMIUM CONTINUA

        if (!isUserPremium && isPlaying && !isFinishing) {
            // FREE: Para apenas ao minimizar/bloquear (NÃO ao voltar)
            pausePlayback()
        }
        // PREMIUM: sempre continua (não faz nada)
        // FREE + isFinishing: continua (não faz nada)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)

        // Limpa a instância atual se for esta
        if (currentInstance == this) {
            currentInstance = null
            wasPlaying = false
        }

        // SEMPRE para e libera o MediaPlayer ao destruir a Activity
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null

        billingManager.destroy()

        // Limpa notificação e MediaSession
        mediaNotificationManager.release()

        // Desregistra BroadcastReceiver
        try {
            unregisterReceiver(mediaControlReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver não estava registrado
        }
    }
}
