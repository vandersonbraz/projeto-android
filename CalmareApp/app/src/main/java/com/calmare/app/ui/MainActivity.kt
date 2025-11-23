package com.calmare.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.managers.AdManager
import com.calmare.app.managers.BillingManager
import com.calmare.app.ui.fragments.FavoritesFragment
import com.calmare.app.ui.fragments.HomeFragment
import com.calmare.app.ui.fragments.SettingsFragment
import com.calmare.app.ui.fragments.SoundsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa managers
        adManager = AdManager(this).apply {
            initialize()
            preloadInterstitialAd()
            preloadRewardedAd()
        }

        billingManager = BillingManager(this, lifecycleScope).apply {
            initialize()
        }

        setupNavigation()
        setupPremiumButton()

        // Carrega o fragment inicial (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Observa status premium para controlar anúncios
        billingManager.isPremium.observe(this) { _ ->
            // Atualiza UI baseado em status premium
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_sounds -> {
                    loadFragment(SoundsFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupPremiumButton() {
        findViewById<android.view.View>(R.id.btn_premium_badge)?.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
