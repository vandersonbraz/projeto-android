package com.optimus.player.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.optimus.player.databinding.ActivitySplashBinding
import com.optimus.player.ui.MainActivity
import com.optimus.player.ui.activation.ActivationActivity
import com.optimus.player.utils.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen com animação do logo hexagonal
 * Verifica se o app já está ativado e redireciona apropriadamente
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen API
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        // Keep splash screen visible while loading
        splashScreen.setKeepOnScreenCondition { true }

        // Animate logo
        animateLogo()

        // Check activation status after animation
        lifecycleScope.launch {
            delay(2500) // Wait for animation
            checkActivationAndNavigate()
        }
    }

    private fun animateLogo() {
        // Scale animation
        binding.logoImage.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
        }

        ObjectAnimator.ofFloat(binding.logoImage, View.SCALE_X, 0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(binding.logoImage, View.SCALE_Y, 0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(binding.logoImage, View.ALPHA, 0f, 1f).apply {
            duration = 800
            start()
        }

        // Fade in text
        lifecycleScope.launch {
            delay(400)
            binding.brandName.apply {
                alpha = 0f
                visibility = View.VISIBLE
            }
            binding.tagline.apply {
                alpha = 0f
                visibility = View.VISIBLE
            }

            ObjectAnimator.ofFloat(binding.brandName, View.ALPHA, 0f, 1f).apply {
                duration = 600
                start()
            }

            delay(200)
            ObjectAnimator.ofFloat(binding.tagline, View.ALPHA, 0f, 1f).apply {
                duration = 600
                start()
            }
        }

        // Glow pulse effect
        lifecycleScope.launch {
            delay(1000)
            pulseGlow()
        }
    }

    private fun pulseGlow() {
        ObjectAnimator.ofFloat(binding.logoGlow, View.ALPHA, 0.3f, 1f, 0.3f).apply {
            duration = 1500
            repeatCount = 1
            start()
        }
    }

    private fun checkActivationAndNavigate() {
        val isActivated = prefManager.isActivated()

        val intent = if (isActivated) {
            // Go to main app
            Intent(this, MainActivity::class.java)
        } else {
            // Go to activation
            Intent(this, ActivationActivity::class.java)
        }

        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
