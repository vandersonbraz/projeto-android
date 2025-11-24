package com.calmare.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inicializa AdMob em background
        lifecycleScope.launch {
            MobileAds.initialize(this@SplashActivity)
            delay(1000) // Aguarda inicialização

            // Verifica se deve mostrar App Open (1x por dia)
            if (shouldShowAppOpen()) {
                loadAppOpenAd()
            } else {
                // Não mostra anúncio hoje, vai direto
                navigateToMain()
            }
        }
    }

    private fun shouldShowAppOpen(): Boolean {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val lastAdTime = prefs.getLong("last_app_open_ad", 0)
        val currentTime = Date().time
        val oneDayInMillis = 24 * 60 * 60 * 1000 // 24 horas

        // Mostra anúncio se passou mais de 24h desde o último
        return (currentTime - lastAdTime) > oneDayInMillis
    }

    private fun saveAppOpenTime() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putLong("last_app_open_ad", Date().time).apply()
    }

    private fun loadAppOpenAd() {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            com.calmare.app.managers.AdManager.APP_OPEN_AD_UNIT_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    showAppOpenAd()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Se falhou, vai direto para o app
                    navigateToMain()
                }
            }
        )
    }

    private fun showAppOpenAd() {
        if (isShowingAd) {
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Anúncio fechado, salva timestamp e navega
                appOpenAd = null
                isShowingAd = false
                saveAppOpenTime()
                navigateToMain()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                // Se falhou ao mostrar, vai direto
                appOpenAd = null
                isShowingAd = false
                navigateToMain()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }

        appOpenAd?.show(this) ?: navigateToMain()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
