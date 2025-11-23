package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.Sound
import com.calmare.app.data.SoundsRepository
import com.calmare.app.managers.AdManager
import com.calmare.app.ui.PlayerActivity
import com.calmare.app.ui.adapters.SoundsAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SoundsFragment : Fragment() {

    private lateinit var adManager: AdManager
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

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_container)
        adManager.loadBannerAd(adContainer)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rv_sounds)
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
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("SOUND_ID", sound.id)
            putExtra("SOUND_TITLE", sound.title)
            putExtra("SOUND_URL", sound.audioUrl)
            putExtra("DURATION", sound.duration)
            putExtra("IS_PREMIUM", sound.isPremium)
        }
        startActivity(intent)
    }

    private fun toggleFavorite(sound: Sound) {
        // TODO: Implementar sistema de favoritos com DataStore
        // Por enquanto apenas mostra que foi clicado
    }
}
