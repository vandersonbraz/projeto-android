package com.optimus.player.ui.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.optimus.player.databinding.FragmentSeriesBinding

/**
 * SeriesFragment - Catálogo de séries
 * 
 * Funcionalidades:
 * - Grid de séries com posters
 * - Busca de séries
 * - Favoritos
 * - Temporadas e episódios
 * - Continue assistindo
 */
class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadSeries()
    }

    private fun setupViews() {
        // TODO: Setup RecyclerView, search, filters
        binding.emptyText.text = "Catálogo de Séries\n\n📺 Temporadas completas\n🎬 Em breve..."
    }

    private fun loadSeries() {
        // TODO: Load series from API/Repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
