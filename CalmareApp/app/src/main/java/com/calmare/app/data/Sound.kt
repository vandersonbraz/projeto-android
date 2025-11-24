package com.calmare.app.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sound(
    val id: Int,
    val title: String,
    val description: String,
    val duration: Int, // em segundos (será detectado automaticamente pelo player)
    val category: String,
    val audioUrl: String,
    val isPremium: Boolean = false
) : Parcelable

object SoundsRepository {
    val sounds = listOf(
        // ═════════════════════════════════════════════════════════════════
        // MINDFULNESS - Sessão Rápida
        // ═════════════════════════════════════════════════════════════════
        Sound(
            id = 1,
            title = "Meditação Guiada",
            description = "Prática mindfulness para iniciantes",
            duration = 60,
            category = "Mindfulness",
            audioUrl = "raw/meditacao_guiada",
            isPremium = false
        ),

        // ═════════════════════════════════════════════════════════════════
        // NATUREZA - Sons da Natureza (12 sons)
        // ═════════════════════════════════════════════════════════════════
        Sound(
            id = 2,
            title = "Cigarra",
            description = "Canto relaxante de cigarras ao entardecer",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/cigarra",
            isPremium = false
        ),
        Sound(
            id = 3,
            title = "Trovão",
            description = "Trovoada suave e distante",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/trovao",
            isPremium = false
        ),
        Sound(
            id = 4,
            title = "Granizo",
            description = "Som suave de granizo caindo",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/granizo",
            isPremium = false
        ),
        Sound(
            id = 5,
            title = "Sapo",
            description = "Coaxar tranquilo de sapos à noite",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/sapo",
            isPremium = false
        ),
        Sound(
            id = 6,
            title = "Grilo",
            description = "Grilos cantando em noite de verão",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/grilo",
            isPremium = false
        ),
        Sound(
            id = 7,
            title = "Cachoeira",
            description = "Água cristalina caindo em cachoeira",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/cachoeira",
            isPremium = false
        ),
        Sound(
            id = 8,
            title = "Vento",
            description = "Brisa suave balançando as árvores",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/vento",
            isPremium = false
        ),
        Sound(
            id = 9,
            title = "Fogo",
            description = "Crepitar relaxante de lareira",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/fogo",
            isPremium = false
        ),
        Sound(
            id = 10,
            title = "Chuva",
            description = "Som relaxante de chuva caindo",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/chuva",
            isPremium = false
        ),
        Sound(
            id = 11,
            title = "Mar",
            description = "Ondas suaves quebrando na praia",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/mar",
            isPremium = false
        ),
        Sound(
            id = 12,
            title = "Pássaros",
            description = "Canto harmonioso de pássaros ao amanhecer",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/passaros",
            isPremium = false
        ),
        Sound(
            id = 13,
            title = "Riacho",
            description = "Água corrente em riacho sereno",
            duration = 60,
            category = "Natureza",
            audioUrl = "raw/riacho",
            isPremium = false
        ),

        // ═════════════════════════════════════════════════════════════════
        // MEDITAÇÃO - Músicas para Meditação (10 sons)
        // ═════════════════════════════════════════════════════════════════
        Sound(
            id = 14,
            title = "Divine Om",
            description = "Mantra sagrado Om para meditação profunda",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/divine_om",
            isPremium = false
        ),
        Sound(
            id = 15,
            title = "Orion Meditation",
            description = "Viagem sonora pelas estrelas",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/orion_meditation",
            isPremium = false
        ),
        Sound(
            id = 16,
            title = "Rishikesh Meditation",
            description = "Sons inspirados nas montanhas do Himalaia",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/rishikesh_meditation",
            isPremium = false
        ),
        Sound(
            id = 17,
            title = "Meditation Olistik",
            description = "Equilíbrio holístico para corpo e mente",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_olistik",
            isPremium = false
        ),
        Sound(
            id = 18,
            title = "Meditation Origin",
            description = "Retorno às origens da consciência",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_origin",
            isPremium = false
        ),
        Sound(
            id = 19,
            title = "Meditation Yoga",
            description = "Música suave para prática de yoga",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_yoga",
            isPremium = false
        ),
        Sound(
            id = 20,
            title = "Meditation Background",
            description = "Som ambiente para meditação guiada",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_background",
            isPremium = false
        ),
        Sound(
            id = 21,
            title = "Relax Meditation",
            description = "Relaxamento profundo e paz interior",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/relax_meditation",
            isPremium = false
        ),
        Sound(
            id = 22,
            title = "Meditation Amp",
            description = "Amplificação da consciência meditativa",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_amp",
            isPremium = false
        ),
        Sound(
            id = 23,
            title = "Meditation Music",
            description = "Melodia tranquila para meditação",
            duration = 60,
            category = "Meditação",
            audioUrl = "raw/meditation_music",
            isPremium = false
        ),

        // ═════════════════════════════════════════════════════════════════
        // MÚSICAS - Músicas Relaxantes (20 músicas)
        // ═════════════════════════════════════════════════════════════════
        Sound(
            id = 24,
            title = "All Is Calm",
            description = "Serenidade e calma absoluta",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/all_is_calm",
            isPremium = false
        ),
        Sound(
            id = 25,
            title = "Melody Nature",
            description = "Melodia inspirada na natureza",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/melody_nature",
            isPremium = false
        ),
        Sound(
            id = 26,
            title = "Solo Flute",
            description = "Flauta solo tocando suavemente",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/solo_flute",
            isPremium = false
        ),
        Sound(
            id = 27,
            title = "Magic Sun",
            description = "Energia mágica do sol nascente",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/magic_sun",
            isPremium = false
        ),
        Sound(
            id = 28,
            title = "Samurai Flutes",
            description = "Flautas japonesas ancestrais",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/samurai_flutes",
            isPremium = false
        ),
        Sound(
            id = 29,
            title = "Relaxing Flute",
            description = "Flauta relaxante para meditação",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/relaxing_flute",
            isPremium = false
        ),
        Sound(
            id = 30,
            title = "Relaxing Krishna",
            description = "Melodias devocionais relaxantes",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/relaxing_krishna",
            isPremium = false
        ),
        Sound(
            id = 31,
            title = "Sad Relaxing",
            description = "Melancolia suave e relaxante",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/sad_relaxing",
            isPremium = false
        ),
        Sound(
            id = 32,
            title = "Relaxing Piano",
            description = "Piano suave para relaxamento",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/relaxing_piano",
            isPremium = false
        ),
        Sound(
            id = 33,
            title = "Origan Blue",
            description = "Tons azuis de tranquilidade",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/origan_blue",
            isPremium = false
        ),
        Sound(
            id = 34,
            title = "Horizon Flyer",
            description = "Voando pelos horizontes da mente",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/horizon_flyer",
            isPremium = false
        ),
        Sound(
            id = 35,
            title = "Piano Soul",
            description = "Piano que toca a alma",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/piano_soul",
            isPremium = false
        ),
        Sound(
            id = 36,
            title = "After Life",
            description = "Jornada além da vida terrena",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/after_life",
            isPremium = false
        ),
        Sound(
            id = 37,
            title = "Magic Moon",
            description = "Magia sob o luar",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/magic_moon",
            isPremium = false
        ),
        Sound(
            id = 38,
            title = "Please Calm",
            description = "Convite à calma e serenidade",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/please_calm",
            isPremium = false
        ),
        Sound(
            id = 39,
            title = "Just Relax",
            description = "Simplesmente relaxe e deixe fluir",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/just_relax",
            isPremium = false
        ),
        Sound(
            id = 40,
            title = "Sad Violin",
            description = "Violino melancólico e tocante",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/sad_violin",
            isPremium = false
        ),
        Sound(
            id = 41,
            title = "When Time",
            description = "Quando o tempo para de existir",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/when_time",
            isPremium = false
        ),
        Sound(
            id = 42,
            title = "Old Style",
            description = "Estilo clássico atemporal",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/old_style",
            isPremium = false
        ),
        Sound(
            id = 43,
            title = "Violins In",
            description = "Violinos em harmonia perfeita",
            duration = 60,
            category = "Músicas",
            audioUrl = "raw/violins_in",
            isPremium = false
        )
    )

    // Sons populares por categoria (escolhidos para exibição em destaque)
    fun getPopularNature() = sounds.filter { it.category == "Natureza" }.take(3)  // Chuva, Mar, Pássaros
    fun getPopularMeditation() = sounds.filter { it.category == "Meditação" }.take(3)  // Divine Om, Orion, Rishikesh
    fun getPopularMusic() = sounds.filter { it.category == "Músicas" }.take(3)  // All Is Calm, Melody Nature, Solo Flute
}
