package com.calmare.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "PLAY", "PAUSE", "STOP", "NEXT", "PREVIOUS" -> {
                // Envia broadcast para o PlayerActivity
                val playerIntent = Intent("com.calmare.app.MEDIA_CONTROL").apply {
                    putExtra("action", intent.action)
                }
                context.sendBroadcast(playerIntent)
            }
        }
    }
}
