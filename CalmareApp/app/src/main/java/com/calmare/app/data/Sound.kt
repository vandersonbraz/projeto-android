package com.calmare.app.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sound(
    val id: Int,
    val title: String,
    val description: String,
    val duration: Int, // em segundos
    val category: String,
    val audioUrl: String,
    val isPremium: Boolean = false
) : Parcelable

object SoundsRepository {
    val sounds = listOf(
        Sound(
            id = 1,
            title = "Chuva Suave",
            description = "Som relaxante de chuva caindo",
            duration = 600, // 10 minutos (ajuste se souber a duração real do seu áudio)
            category = "Natureza",
            audioUrl = "raw/chuva_suave",  // Arquivo local - SEM extensão .mp3
            isPremium = false
        )
        // Adicione mais áudios aqui conforme for baixando
    )
}
