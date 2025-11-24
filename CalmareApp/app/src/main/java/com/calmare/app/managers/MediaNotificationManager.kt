package com.calmare.app.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.calmare.app.R
import com.calmare.app.ui.PlayerActivity

class MediaNotificationManager(
    private val context: Context,
    private val playerActivity: PlayerActivity
) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var mediaSession: MediaSessionCompat? = null
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "calmare_media_playback"

    // ══════════════════════════════════════════════════════════════════════
    // IMPORTANTE: CONTROLE DE ACESSO FREE vs PREMIUM
    // ══════════════════════════════════════════════════════════════════════
    // Por enquanto está LIBERADO PARA TODOS (free e premium) para testes.
    //
    // QUANDO VALIDAR: Para restringir apenas ao premium, mude esta linha:
    //     private fun shouldShowNotification(): Boolean = true
    // Para:
    //     private fun shouldShowNotification(): Boolean = playerActivity.isUserPremium
    // ══════════════════════════════════════════════════════════════════════
    private fun shouldShowNotification(): Boolean = true  // MUDE AQUI DEPOIS!

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reprodução de Mídia",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controles de reprodução do Calmare"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(context, "CalmareMediaSession").apply {
            @Suppress("DEPRECATION")
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Callback para responder aos comandos dos controles
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    playerActivity.handleMediaCommand("PLAY")
                }

                override fun onPause() {
                    playerActivity.handleMediaCommand("PAUSE")
                }

                override fun onStop() {
                    playerActivity.handleMediaCommand("STOP")
                }

                override fun onSkipToNext() {
                    playerActivity.handleMediaCommand("NEXT")
                }

                override fun onSkipToPrevious() {
                    playerActivity.handleMediaCommand("PREVIOUS")
                }
            })

            isActive = true
        }
    }

    fun showNotification(
        title: String,
        artist: String,
        isPlaying: Boolean
    ) {
        if (!shouldShowNotification()) {
            // Se não deve mostrar notificação, cancela qualquer notificação existente
            hideNotification()
            return
        }

        val mediaSession = this.mediaSession ?: return

        // Atualiza estado da MediaSession
        val state = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
            .build()

        mediaSession.setPlaybackState(playbackState)

        // Intent para abrir o PlayerActivity quando clicar na notificação
        val contentIntent = Intent(context, PlayerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Actions para os botões
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pausar",
                createMediaButtonPendingIntent("PAUSE")
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Tocar",
                createMediaButtonPendingIntent("PLAY")
            )
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Parar",
            createMediaButtonPendingIntent("STOP")
        )

        // Cria bitmap do ícone (emoji como imagem)
        val albumArtBitmap = createAlbumArt("🌧️")

        // Constrói a notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_notification)  // Ícone pequeno
            .setLargeIcon(albumArtBitmap)  // Ícone grande (capa do álbum)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(createMediaButtonPendingIntent("STOP"))  // Quando usuário descarta a notificação
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)  // Se está tocando, notificação não pode ser descartada
            .setShowWhen(false)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)  // Mostra play/pause no modo compacto
                    .setShowCancelButton(true)
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createMediaButtonPendingIntent(action: String): PendingIntent {
        val intent = Intent(action).apply {
            setPackage(context.packageName)
            setClass(context, MediaButtonReceiver::class.java)
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createAlbumArt(emoji: String): Bitmap {
        val size = 200
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fundo roxo
        canvas.drawColor(android.graphics.Color.parseColor("#6C63FF"))

        // Emoji no centro
        val paint = Paint().apply {
            textSize = 100f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(emoji, xPos, yPos, paint)

        return bitmap
    }

    fun updateNotification(title: String, artist: String, isPlaying: Boolean) {
        showNotification(title, artist, isPlaying)
    }

    fun hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
        mediaSession?.isActive = false
    }

    fun release() {
        hideNotification()
        mediaSession?.release()
        mediaSession = null
    }
}
