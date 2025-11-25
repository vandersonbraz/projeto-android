package com.optimus.player.ui.config

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.optimus.player.R
import com.optimus.player.databinding.FragmentConfigBinding
import com.optimus.player.ui.activation.ActivationActivity
import com.optimus.player.utils.PreferenceManager

/**
 * ConfigFragment - Configurações do App
 * 
 * Funcionalidades:
 * - Controle parental
 * - Atualizar playlist
 * - Sobre o app
 * - Sair (logout)
 */
class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        prefManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Parental Control
        binding.parentalControlCard.setOnClickListener {
            // TODO: Open Parental Control settings
            showComingSoonDialog("Controle Parental")
        }
        
        // Update Playlist
        binding.updatePlaylistCard.setOnClickListener {
            // TODO: Sync playlist from API
            showComingSoonDialog("Atualizar Playlist")
        }
        
        // About
        binding.aboutCard.setOnClickListener {
            showAboutDialog()
        }
        
        // Logout
        binding.logoutCard.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showComingSoonDialog(feature: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Em Breve")
            .setMessage("A funcionalidade \"$feature\" estará disponível em breve!")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAboutDialog() {
        val version = try {
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Sobre o Optimus Player")
            .setMessage(
                """
                Optimus Player v$version
                
                Premium IPTV Experience
                
                © 2025 Optimus Player
                Todos os direitos reservados.
                
                Desenvolvido com ❤️ para você.
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair? Você precisará ativar novamente com seu código.")
            .setPositiveButton("Sim, Sair") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        // Clear all preferences
        prefManager.clearAll()
        
        // Go back to activation screen
        val intent = Intent(requireContext(), ActivationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
