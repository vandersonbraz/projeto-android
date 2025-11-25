package com.optimus.player.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.optimus.player.databinding.FragmentChannelsBinding

/**
 * ChannelsFragment - Lista de canais de TV ao vivo
 * 
 * Funcionalidades:
 * - Lista de canais com categorias
 * - Busca de canais
 * - Favoritos
 * - Filtro por categoria (Esportes, Filmes, Notícias, etc)
 */
class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadChannels()
    }

    private fun setupViews() {
        // TODO: Setup RecyclerView, search, filters
        binding.emptyText.text = "Lista de Canais\n\n🎬 TV Ao Vivo\n📡 Em breve..."
    }

    private fun loadChannels() {
        // TODO: Load channels from API/Repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
