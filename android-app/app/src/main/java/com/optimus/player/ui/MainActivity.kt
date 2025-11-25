package com.optimus.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.optimus.player.R
import com.optimus.player.databinding.ActivityMainBinding

/**
 * MainActivity - Tela principal do app
 * 
 * Contém:
 * - Bottom Navigation (Início, EPG, MAC, Config)
 * - Navigation Host para gerenciar Fragments
 * - Navegação entre as seções principais
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }

    /**
     * Configura a navegação entre Fragments usando Navigation Component
     */
    private fun setupNavigation() {
        // Pegar o NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // Conectar Bottom Navigation com NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
        
        // Mapear IDs do menu para IDs dos fragments
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_epg -> {
                    navController.navigate(R.id.epgFragment)
                    true
                }
                R.id.navigation_mac -> {
                    navController.navigate(R.id.macFragment)
                    true
                }
                R.id.navigation_config -> {
                    navController.navigate(R.id.configFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
