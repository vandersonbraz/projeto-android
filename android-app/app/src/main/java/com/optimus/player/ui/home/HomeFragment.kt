package com.optimus.player.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.optimus.player.R
import com.optimus.player.databinding.FragmentHomeBinding

/**
 * HomeFragment - Tela principal com Tabs
 * 
 * Contém 3 tabs:
 * - Canais (TV Ao Vivo)
 * - Filmes (Catálogo de filmes)
 * - Séries (Catálogo de séries)
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()
    }

    /**
     * Configura o ViewPager2 com as 3 tabs (Canais, Filmes, Séries)
     */
    private fun setupViewPager() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        // Conectar TabLayout com ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_channels)
                1 -> getString(R.string.tab_movies)
                2 -> getString(R.string.tab_series)
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
