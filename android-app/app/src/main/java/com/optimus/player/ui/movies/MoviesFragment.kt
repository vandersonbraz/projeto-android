package com.optimus.player.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android:view.ViewGroup
import androidx.fragment.app.Fragment
import com.optimus.player.databinding.FragmentMoviesBinding

/**
 * MoviesFragment - Catálogo de filmes
 * 
 * Funcionalidades:
 * - Grid de filmes com posters
 * - Busca de filmes
 * - Favoritos
 * - Categorias (Ação, Drama, Comédia, etc)
 * - Continue assistindo
 */
class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadMovies()
    }

    private fun setupViews() {
        // TODO: Setup RecyclerView, search, filters
        binding.emptyText.text = "Catálogo de Filmes\n\n🎬 Milhares de títulos\n📺 Em breve..."
    }

    private fun loadMovies() {
        // TODO: Load movies from API/Repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
