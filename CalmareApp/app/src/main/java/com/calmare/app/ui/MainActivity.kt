package com.calmare.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.calmare.app.utils.NotificationBadgeManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    private lateinit var badgeManager: NotificationBadgeManager
    private lateinit var notificationBadge: TextView

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

        badgeManager = NotificationBadgeManager(this)
        notificationBadge = findViewById(R.id.notification_badge)

        setupNavigation()
        setupPremiumButton()
        setupNotificationBell()
        updateBadge()

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
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun setupPremiumButton() {
        findViewById<android.view.View>(R.id.btn_premium_badge)?.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
    }

    private fun setupNotificationBell() {
        findViewById<android.view.View>(R.id.btn_notifications_container)?.setOnClickListener {
            showMissedMeditations()
        }
    }

    private fun updateBadge() {
        val count = badgeManager.getUnreadCount()
        if (count > 0) {
            notificationBadge.text = if (count > 99) "99+" else count.toString()
            notificationBadge.visibility = View.VISIBLE
        } else {
            notificationBadge.visibility = View.GONE
        }
    }

    private fun showMissedMeditations() {
        val missedList = badgeManager.getMissedMeditations()

        if (missedList.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.missed_meditations))
                .setMessage(getString(R.string.no_missed_meditations))
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val items = missedList.map {
            "⏰ ${it.time} - ${it.title}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("${getString(R.string.missed_meditations)} (${missedList.size})")
            .setItems(items, null)
            .setPositiveButton("Limpar Tudo") { _, _ ->
                badgeManager.clearAllMissedMeditations()
                updateBadge()
            }
            .setNegativeButton("Fechar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
