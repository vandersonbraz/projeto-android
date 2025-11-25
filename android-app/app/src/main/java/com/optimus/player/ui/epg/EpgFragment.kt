package com.optimus.player.ui.epg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.optimus.player.databinding.FragmentEpgBinding

/**
 * EpgFragment - Guia de Programação (Electronic Program Guide)
 * 
 * Funcionalidades:
 * - Lista de canais com programação atual
 * - Grade de programação por horário
 * - Programação do dia (hoje, amanhã)
 * - Busca de programas
 */
class EpgFragment : Fragment() {

    private var _binding: FragmentEpgBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadEpgData()
    }

    private fun setupViews() {
        // TODO: Setup RecyclerView with EPG grid
        binding.emptyText.text = "Guia de Programação\n\n📺 Grade completa\n🕒 Em breve..."
    }

    private fun loadEpgData() {
        // TODO: Load EPG data from API
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
