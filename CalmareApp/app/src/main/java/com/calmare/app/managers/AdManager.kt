package com.calmare.app.managers

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Gerenciador central de todos os anúncios do app (AdMob)
 *
 * IMPORTANTE: Troque os IDs de teste pelos seus IDs reais antes de publicar!
 */
class AdManager(private val context: Context) {

    // ========== IDs DE TESTE - SUBSTITUA PELOS SEUS IDS REAIS ==========
    companion object {
        // Banner Ad Unit ID (TESTE)
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

        // Interstitial Ad Unit ID (TESTE)
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

        // Rewarded Ad Unit ID (TESTE)
        private const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    /**
     * Inicializa o SDK do AdMob (chamar no Application ou MainActivity)
     */
    fun initialize(onComplete: () -> Unit = {}) {
        MobileAds.initialize(context) {
            onComplete()
        }
    }

    // ========== BANNER ADS ==========

    /**
     * Carrega e exibe um banner ad em um container
     */
    fun loadBannerAd(container: FrameLayout) {
        val adView = AdView(context).apply {
            adUnitId = BANNER_AD_UNIT_ID
            setAdSize(AdSize.BANNER)
        }

        container.removeAllViews()
        container.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    // ========== INTERSTITIAL ADS ==========

    /**
     * Pré-carrega um interstitial ad
     */
    fun preloadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    setupInterstitialCallbacks()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun setupInterstitialCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                // Pré-carrega o próximo
                preloadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
            }
        }
    }

    /**
     * Mostra o interstitial ad se estiver carregado
     */
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitialAd()
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onAdClosed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onAdClosed()
            // Tenta carregar para próxima vez
            preloadInterstitialAd()
        }
    }

    // ========== REWARDED ADS ==========

    /**
     * Pré-carrega um rewarded ad
     */
    fun preloadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    /**
     * Mostra rewarded ad se estiver carregado
     */
    fun showRewardedAd(
        activity: Activity,
        onRewarded: (rewardAmount: Int) -> Unit,
        onAdClosed: () -> Unit = {}
    ) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewardedAd()
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    onAdClosed()
                }
            }

            rewardedAd?.show(activity) { rewardItem ->
                // Usuário ganhou a recompensa
                onRewarded(rewardItem.amount)
            }
        } else {
            onAdClosed()
            preloadRewardedAd()
        }
    }

    /**
     * Verifica se rewarded ad está pronto
     */
    fun isRewardedAdReady(): Boolean = rewardedAd != null
}
