package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.data.Sound
import com.calmare.app.data.SoundsRepository
import com.calmare.app.managers.AdManager
import com.calmare.app.ui.PlayerActivity
import com.calmare.app.ui.adapters.SoundsAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SoundsFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var soundsAdapter: SoundsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup

    private var allSounds = SoundsRepository.sounds
    private var currentCategory = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sounds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adManager = AdManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_container)
        adManager.loadBannerAd(adContainer)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recycler_sounds)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        soundsAdapter = SoundsAdapter(
            sounds = allSounds,
            onSoundClick = { sound -> openPlayer(sound) },
            onFavoriteClick = { sound -> toggleFavorite(sound) }
        )

        recyclerView.adapter = soundsAdapter

        // Setup category filters
        chipGroup = view.findViewById(R.id.chip_group_categories)
        setupCategoryFilters()

        // Observa mudanças nos favoritos e atualiza o adapter
        lifecycleScope.launch {
            preferencesManager.favoriteSoundIds.collect { favorites ->
                soundsAdapter.updateFavorites(favorites)
            }
        }
    }

    private fun setupCategoryFilters() {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) {
                filterSounds("Todos")
                return@setOnCheckedStateChangeListener
            }

            val selectedChip = chipGroup.findViewById<Chip>(checkedIds.first())
            val category = selectedChip?.text.toString()
            filterSounds(category)
        }
    }

    private fun filterSounds(category: String) {
        currentCategory = category

        val filteredSounds = when (category) {
            "Todos" -> allSounds
            "Natureza" -> allSounds.filter { it.category == "Natureza" }
            "Meditação" -> allSounds.filter { it.category == "Meditação" }
            "Música" -> allSounds.filter { it.category == "Música" }
            "Sono" -> allSounds.filter { it.category == "Sono" }
            "Ambiente" -> allSounds.filter { it.category == "Ambiente" }
            else -> allSounds
        }

        soundsAdapter.updateSounds(filteredSounds)
    }

    private fun openPlayer(sound: Sound) {
        // Pega a lista filtrada atual do adapter
        val currentPlaylist = soundsAdapter.getCurrentSounds()
        val currentIndex = currentPlaylist.indexOf(sound)

        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("SOUND_ID", sound.id)
            putExtra("SOUND_TITLE", sound.title)
            putExtra("SOUND_CATEGORY", sound.category)
            putExtra("SOUND_DURATION", sound.duration)
            putExtra("SOUND_URL", sound.audioUrl)
            putExtra("IS_PREMIUM", sound.isPremium)
            putParcelableArrayListExtra("PLAYLIST", ArrayList(currentPlaylist))
            putExtra("CURRENT_INDEX", currentIndex)
        }
        startActivity(intent)
    }

    private fun toggleFavorite(sound: Sound) {
        lifecycleScope.launch {
            // Usa .first() ao invés de .collect() para pegar apenas o valor atual
            val favorites = preferencesManager.favoriteSoundIds.first()

            if (favorites.contains(sound.id)) {
                preferencesManager.removeFavorite(sound.id)
                Toast.makeText(
                    requireContext(),
                    "Removido dos favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                preferencesManager.addFavorite(sound.id)
                Toast.makeText(
                    requireContext(),
                    "Adicionado aos favoritos ❤️",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
