package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.data.SoundsRepository
import com.calmare.app.managers.AdManager
import com.calmare.app.ui.PlayerActivity
import com.calmare.app.ui.adapters.SoundsHorizontalAdapter
import com.calmare.app.utils.AudioDurationDetector
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var preferencesManager: PreferencesManager
    private var currentToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adManager = AdManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        // Inicializa o detector de duração de áudio
        AudioDurationDetector.init(requireContext())

        // Detecta durações reais dos áudios em background
        lifecycleScope.launch {
            AudioDurationDetector.detectAllDurations(requireContext(), SoundsRepository.sounds)
        }

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_banner_container)
        adManager.loadBannerAd(adContainer)
        adContainer.visibility = View.VISIBLE

        // Setup quick session - card inteiro clicável
        view.findViewById<View>(R.id.card_quick_session)?.setOnClickListener {
            openPlayer()
        }

        // Botão também clicável (redundante, mas mantém funcionalidade)
        view.findViewById<Button>(R.id.btn_start_quick_session)?.setOnClickListener {
            openPlayer()
        }

        // Setup mood trackers
        setupMoodTrackers(view)

        // Setup nature sounds (Natureza)
        setupNatureSounds(view)

        // Setup meditations
        setupMeditations(view)

        // Setup music
        setupMusicSounds(view)
    }

    private fun setupMoodTrackers(view: View) {
        val moods = listOf(
            R.id.mood_1 to "Calmo",
            R.id.mood_2 to "Ansioso",
            R.id.mood_3 to "Sonolento",
            R.id.mood_4 to "Feliz",
            R.id.mood_5 to "Triste"
        )

        moods.forEach { (id, moodName) ->
            view.findViewById<View>(id)?.setOnClickListener {
                // Cancela o Toast anterior (se existir)
                currentToast?.cancel()

                // Mostra novo Toast IMEDIATAMENTE
                currentToast = Toast.makeText(
                    requireContext(),
                    "Você está se sentindo: $moodName 😊",
                    Toast.LENGTH_SHORT
                )
                currentToast?.show()

                // Salva humor no DataStore em background
                lifecycleScope.launch {
                    preferencesManager.saveMood(moodName)
                }
            }
        }
    }

    private fun setupNatureSounds(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_popular_sounds)

        // Get nature sounds (3 populares)
        val natureSounds = SoundsRepository.sounds.filter { it.category == "Natureza" }.take(3)

        val adapter = SoundsHorizontalAdapter(natureSounds) { sound ->
            val allNatureSounds = SoundsRepository.sounds.filter { it.category == "Natureza" }
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putParcelableArrayListExtra("PLAYLIST", ArrayList(allNatureSounds))
                putExtra("CURRENT_INDEX", allNatureSounds.indexOf(sound))
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = adapter
    }

    private fun setupMeditations(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_meditations)

        // Get meditation sounds (3 populares)
        val meditationsPreview = SoundsRepository.sounds.filter { it.category == "Meditação" }.take(3)
        val allMeditations = SoundsRepository.sounds.filter { it.category == "Meditação" }

        val adapter = SoundsHorizontalAdapter(meditationsPreview) { sound ->
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putParcelableArrayListExtra("PLAYLIST", ArrayList(allMeditations))
                putExtra("CURRENT_INDEX", allMeditations.indexOf(sound))
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = adapter
    }

    private fun setupMusicSounds(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_music)

        // Get music sounds (3 populares)
        val musicPreview = SoundsRepository.sounds.filter { it.category == "Músicas" }.take(3)
        val allMusic = SoundsRepository.sounds.filter { it.category == "Músicas" }

        val adapter = SoundsHorizontalAdapter(musicPreview) { sound ->
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putParcelableArrayListExtra("PLAYLIST", ArrayList(allMusic))
                putExtra("CURRENT_INDEX", allMusic.indexOf(sound))
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = adapter
    }

    private fun openPlayer() {
        // Busca a "Meditação Guiada" no repositório (Sessão Mindfulness)
        val sound = SoundsRepository.sounds.find { it.id == 1 } // ID 1 = Meditação Guiada

        if (sound != null) {
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putExtra("IS_QUICK_SESSION", true)  // Marca como Sessão Rápida
            }
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Som não encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}
