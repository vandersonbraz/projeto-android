package com.calmare.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AudioDurationDetector {

    private const val PREFS_NAME = "audio_durations"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Detecta a duração real de um arquivo de áudio em segundos
     * Usa cache para não detectar toda vez
     */
    suspend fun getRealDuration(context: Context, soundId: Int, audioUrl: String): Int {
        // Verifica se já tem no cache
        val cached = prefs.getInt("duration_$soundId", -1)
        if (cached != -1) {
            return cached
        }

        // Se não tem no cache, detecta a duração
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()

                // Extrai o nome do arquivo de "raw/arquivo"
                val fileName = audioUrl.replace("raw/", "")

                // Pega o resource ID do arquivo
                val resourceId = context.resources.getIdentifier(
                    fileName,
                    "raw",
                    context.packageName
                )

                if (resourceId != 0) {
                    val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
                    retriever.setDataSource(context, uri)

                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val durationMs = durationStr?.toLongOrNull() ?: 0L
                    val durationSeconds = (durationMs / 1000).toInt()

                    retriever.release()

                    // Salva no cache
                    prefs.edit().putInt("duration_$soundId", durationSeconds).apply()

                    durationSeconds
                } else {
                    60 // Valor padrão se não encontrar o arquivo
                }
            } catch (e: Exception) {
                e.printStackTrace()
                60 // Valor padrão em caso de erro
            }
        }
    }

    /**
     * Pega a duração do cache (ou retorna um placeholder)
     */
    fun getCachedDuration(soundId: Int): Int {
        return prefs.getInt("duration_$soundId", 60) // 60s como placeholder
    }

    /**
     * Detecta durações de todos os sons em background
     */
    suspend fun detectAllDurations(context: Context, sounds: List<com.calmare.app.data.Sound>) {
        withContext(Dispatchers.IO) {
            sounds.forEach { sound ->
                getRealDuration(context, sound.id, sound.audioUrl)
            }
        }
    }
}
