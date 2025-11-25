package com.optimus.player.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.optimus.player.ui.channels.ChannelsFragment
import com.optimus.player.ui.movies.MoviesFragment
import com.optimus.player.ui.series.SeriesFragment

/**
 * Adapter para gerenciar as 3 tabs do HomeFragment:
 * - Tab 0: Canais (ChannelsFragment)
 * - Tab 1: Filmes (MoviesFragment)
 * - Tab 2: Séries (SeriesFragment)
 */
class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChannelsFragment()
            1 -> MoviesFragment()
            2 -> SeriesFragment()
            else -> ChannelsFragment()
        }
    }
}
