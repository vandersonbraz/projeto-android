package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calmare.app.R
import com.calmare.app.data.Sound
import com.calmare.app.managers.AdManager
import com.calmare.app.ui.PlayerActivity
import com.calmare.app.ui.adapters.SoundsAdapter

class FavoritesFragment : Fragment() {

    private lateinit var adManager: AdManager
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
        // TODO: Carregar favoritos do DataStore
        // Por enquanto mostra lista vazia
        favoriteSounds = emptyList()
        updateUI()
    }

    private fun updateUI() {
        if (favoriteSounds.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            soundsAdapter.updateSounds(favoriteSounds)
        }
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

    private fun removeFavorite(sound: Sound) {
        // TODO: Remover dos favoritos no DataStore
        // Por enquanto apenas atualiza a UI
        favoriteSounds = favoriteSounds.filter { it.id != sound.id }
        updateUI()
    }
}
