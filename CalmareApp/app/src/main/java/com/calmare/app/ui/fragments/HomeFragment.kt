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
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.managers.AdManager
import com.calmare.app.ui.PlayerActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var preferencesManager: PreferencesManager

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

        // Setup quick session button
        view.findViewById<Button>(R.id.btn_start_quick_session)?.setOnClickListener {
            openPlayer("Respiração 5 Minutos", 300)
        }

        // Setup mood trackers
        setupMoodTrackers(view)
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
                // Salva humor no DataStore
                lifecycleScope.launch {
                    preferencesManager.saveMood(moodName)
                    Toast.makeText(
                        requireContext(),
                        "Você está se sentindo: $moodName 😊",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openPlayer(title: String, duration: Int) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("SOUND_TITLE", title)
            putExtra("DURATION", duration)
        }
        startActivity(intent)
    }
}
