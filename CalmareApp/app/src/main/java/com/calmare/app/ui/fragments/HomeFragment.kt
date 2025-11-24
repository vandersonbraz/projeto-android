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

        // Setup popular sounds
        setupPopularSounds(view)

        // Setup guided meditations
        setupGuidedMeditations(view)
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

    private fun setupPopularSounds(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_popular_sounds)

        // Get the 4 most popular sounds (non-premium)
        val popularSounds = SoundsRepository.sounds.filter { !it.isPremium }.take(4)

        val adapter = SoundsHorizontalAdapter(popularSounds) { sound ->
            val currentIndex = popularSounds.indexOf(sound)
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putParcelableArrayListExtra("PLAYLIST", ArrayList(popularSounds))
                putExtra("CURRENT_INDEX", currentIndex)
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

    private fun setupGuidedMeditations(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_meditations)

        // Get meditation sounds
        val meditations = SoundsRepository.sounds.filter {
            it.category == "Meditação"
        }

        val adapter = SoundsHorizontalAdapter(meditations) { sound ->
            val currentIndex = meditations.indexOf(sound)
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SOUND_ID", sound.id)
                putExtra("SOUND_TITLE", sound.title)
                putExtra("SOUND_CATEGORY", sound.category)
                putExtra("SOUND_DURATION", sound.duration)
                putExtra("SOUND_URL", sound.audioUrl)
                putExtra("IS_PREMIUM", sound.isPremium)
                putParcelableArrayListExtra("PLAYLIST", ArrayList(meditations))
                putExtra("CURRENT_INDEX", currentIndex)
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
        // Busca o som "Respiração Consciente" no repositório
        val sound = SoundsRepository.sounds.find { it.id == 5 } // ID 5 = Respiração Consciente

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
