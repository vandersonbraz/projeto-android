package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
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
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var soundsAdapter: SoundsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout

    private var favoriteSounds = emptyList<Sound>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adManager = AdManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_container)
        adManager.loadBannerAd(adContainer)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rv_favorites)
        emptyLayout = view.findViewById(R.id.tv_empty_favorites)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        soundsAdapter = SoundsAdapter(
            sounds = favoriteSounds,
            onSoundClick = { sound -> openPlayer(sound) },
            onFavoriteClick = { sound -> removeFavorite(sound) }
        )

        recyclerView.adapter = soundsAdapter

        // Load favorites from DataStore
        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            preferencesManager.favoriteSoundIds.collect { favoriteIds ->
                // Carrega os sons que estão nos favoritos
                favoriteSounds = SoundsRepository.sounds.filter { sound ->
                    favoriteIds.contains(sound.id)
                }
                updateUI(favoriteIds)
            }
        }
    }

    private fun updateUI(favoriteIds: Set<Int>) {
        if (favoriteSounds.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            soundsAdapter.updateSounds(favoriteSounds)
            // Atualiza os favoritos para mostrar corações vermelhos
            soundsAdapter.updateFavorites(favoriteIds)
        }
    }

    private fun openPlayer(sound: Sound) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("SOUND_ID", sound.id)
            putExtra("SOUND_TITLE", sound.title)
            putExtra("SOUND_CATEGORY", sound.category)
            putExtra("SOUND_DURATION", sound.duration)
            putExtra("SOUND_URL", sound.audioUrl)
            putExtra("IS_PREMIUM", sound.isPremium)
        }
        startActivity(intent)
    }

    private fun removeFavorite(sound: Sound) {
        lifecycleScope.launch {
            preferencesManager.removeFavorite(sound.id)
            // A UI será atualizada automaticamente pelo Flow no loadFavorites()
        }
    }
}
