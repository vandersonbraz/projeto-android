package com.calmare.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.data.PreferencesManager
import com.calmare.app.managers.AdManager
import com.calmare.app.managers.BillingManager
import com.calmare.app.ui.PremiumActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchAutoPlay: SwitchMaterial
    private lateinit var switchDownloadWifi: SwitchMaterial
    private lateinit var btnPremium: Button
    private lateinit var tvPremiumStatus: TextView
    private lateinit var cardPremium: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adManager = AdManager(requireContext())
        billingManager = BillingManager(requireContext(), lifecycleScope)
        preferencesManager = PreferencesManager(requireContext())

        // Carrega banner de anúncio
        val adContainer = view.findViewById<FrameLayout>(R.id.ad_container)
        adManager.loadBannerAd(adContainer)

        // Initialize views
        switchNotifications = view.findViewById(R.id.switch_notifications)
        switchAutoPlay = view.findViewById(R.id.switch_autoplay)
        switchDownloadWifi = view.findViewById(R.id.switch_download_wifi)
        btnPremium = view.findViewById(R.id.btn_premium)
        tvPremiumStatus = view.findViewById(R.id.tv_premium_status)
        cardPremium = view.findViewById(R.id.card_premium)

        setupListeners()
        loadSettings()
    }

    private fun setupListeners() {
        // Notification toggle
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSetting(isChecked)
        }

        // Auto-play toggle
        switchAutoPlay.setOnCheckedChangeListener { _, isChecked ->
            saveAutoPlaySetting(isChecked)
        }

        // Download only on WiFi toggle
        switchDownloadWifi.setOnCheckedChangeListener { _, isChecked ->
            saveDownloadWifiSetting(isChecked)
        }

        // Premium button
        btnPremium.setOnClickListener {
            openPremiumActivity()
        }

        // Check premium status
        billingManager.initialize()
        billingManager.isPremium.observe(viewLifecycleOwner) { isPremium ->
            updatePremiumUI(isPremium)
        }
    }

    private fun loadSettings() {
        // Carrega configurações do DataStore
        lifecycleScope.launch {
            preferencesManager.notificationsEnabled.collect { enabled ->
                switchNotifications.isChecked = enabled
            }
        }
        lifecycleScope.launch {
            preferencesManager.autoPlayEnabled.collect { enabled ->
                switchAutoPlay.isChecked = enabled
            }
        }
        lifecycleScope.launch {
            preferencesManager.downloadWifiOnly.collect { enabled ->
                switchDownloadWifi.isChecked = enabled
            }
        }
    }

    private fun saveNotificationSetting(enabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    private fun saveAutoPlaySetting(enabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.setAutoPlayEnabled(enabled)
        }
    }

    private fun saveDownloadWifiSetting(enabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.setDownloadWifiOnly(enabled)
        }
    }

    private fun updatePremiumUI(isPremium: Boolean) {
        if (isPremium) {
            tvPremiumStatus.text = "✨ Você é Premium!"
            tvPremiumStatus.setTextColor(resources.getColor(R.color.accent, null))
            btnPremium.text = "Gerenciar Assinatura"
            cardPremium.setCardBackgroundColor(resources.getColor(R.color.primary_light, null))
        } else {
            tvPremiumStatus.text = "Desbloqueie recursos premium"
            tvPremiumStatus.setTextColor(resources.getColor(R.color.text_secondary, null))
            btnPremium.text = "Assinar Premium"
            cardPremium.setCardBackgroundColor(resources.getColor(R.color.card_background, null))
        }
    }

    private fun openPremiumActivity() {
        val intent = Intent(requireContext(), PremiumActivity::class.java)
        startActivity(intent)
    }
}
