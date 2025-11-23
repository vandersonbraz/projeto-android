package com.calmare.app.data

data class Sound(
    val id: Int,
    val title: String,
    val description: String,
    val duration: Int, // em segundos
    val category: String,
    val audioUrl: String,
    val isPremium: Boolean = false
)

object SoundsRepository {
    val sounds = listOf(
        Sound(
            id = 1,
            title = "Chuva Suave",
            description = "Som relaxante de chuva caindo",
            duration = 600,
            category = "Natureza",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            isPremium = false
        ),
        Sound(
            id = 2,
            title = "Ondas do Mar",
            description = "Ondas quebrando na praia",
            duration = 900,
            category = "Natureza",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            isPremium = false
        ),
        Sound(
            id = 3,
            title = "Floresta Tropical",
            description = "Sons da floresta amazônica",
            duration = 1200,
            category = "Natureza",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            isPremium = true
        ),
        Sound(
            id = 4,
            title = "Meditação Guiada",
            description = "Meditação para iniciantes",
            duration = 600,
            category = "Meditação",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            isPremium = false
        ),
        Sound(
            id = 5,
            title = "Respiração Consciente",
            description = "Exercício de respiração",
            duration = 300,
            category = "Meditação",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            isPremium = false
        ),
        Sound(
            id = 6,
            title = "Fogo de Lareira",
            description = "Som crepitante de lareira",
            duration = 1800,
            category = "Ambiente",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            isPremium = true
        ),
        Sound(
            id = 7,
            title = "Piano Relaxante",
            description = "Música suave de piano",
            duration = 900,
            category = "Música",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            isPremium = true
        ),
        Sound(
            id = 8,
            title = "Ruído Branco",
            description = "Som contínuo para dormir",
            duration = 3600,
            category = "Sono",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            isPremium = false
        )
    )
}
