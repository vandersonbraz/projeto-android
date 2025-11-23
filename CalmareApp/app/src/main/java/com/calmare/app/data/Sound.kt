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

    /*
     * ═══════════════════════════════════════════════════════════════════
     * INSTRUÇÕES: COMO ADICIONAR SEUS PRÓPRIOS ÁUDIOS DE MEDITAÇÃO
     * ═══════════════════════════════════════════════════════════════════
     *
     * OPÇÃO 1 - USAR URLs PÚBLICAS (Mais fácil para testar):
     * -------------------------------------------------------------
     * 1. Faça upload dos seus arquivos .mp3 em:
     *    - Google Drive (compartilhe com "Qualquer pessoa com o link")
     *    - Dropbox (gere link direto)
     *    - Seu próprio servidor/hospedagem
     *
     * 2. Use ferramentas para obter link direto:
     *    - Para Google Drive: Use sites como https://sites.google.com/site/gdocs2direct/
     *    - Para Dropbox: Mude ?dl=0 para ?dl=1 no final do link
     *
     * 3. Substitua audioUrl acima pelos seus links diretos
     *
     *
     * OPÇÃO 2 - BAIXAR SONS GRATUITOS DA INTERNET:
     * -------------------------------------------------------------
     * Sites com áudios de meditação gratuitos:
     *    - Freesound.org - https://freesound.org (precisa criar conta grátis)
     *      Busque por: "rain", "ocean waves", "meditation", "white noise"
     *
     *    - Pixabay Music - https://pixabay.com/music/
     *      Busque por: "meditation", "calm", "relaxing"
     *
     *    - YouTube Audio Library - https://studio.youtube.com/channel/UC.../music
     *      Baixe e hospede você mesmo
     *
     *
     * OPÇÃO 3 - ARMAZENAR LOCALMENTE NO APP (Funciona offline):
     * -------------------------------------------------------------
     * 1. Crie a pasta: app/src/main/res/raw/
     * 2. Coloque seus arquivos .mp3 nessa pasta (nomes sem espaços, só minúsculas)
     *    Exemplo: chuva_suave.mp3, ondas_mar.mp3
     *
     * 3. No código acima, mude audioUrl para formato local:
     *    audioUrl = "raw/chuva_suave"  (sem .mp3 no final)
     *
     * 4. Atualize PlayerActivity.kt para detectar arquivos locais:
     *    if (soundUrl.startsWith("raw/")) {
     *        val resourceId = resources.getIdentifier(
     *            soundUrl.substringAfter("raw/"),
     *            "raw",
     *            packageName
     *        )
     *        mediaPlayer.setDataSource(this, Uri.parse("android.resource://$packageName/$resourceId"))
     *    } else {
     *        mediaPlayer.setDataSource(soundUrl)  // URL da internet
     *    }
     *
     * NOTA: Arquivos locais aumentam o tamanho do APK mas funcionam offline!
     *
     *
     * ATENÇÃO: URLs atuais são de MÚSICA DE EXEMPLO!
     * ═══════════════════════════════════════════════════════════════════
     * Substitua por sons reais de meditação/relaxamento para seu app!
     */
}
