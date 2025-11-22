package com.calmare.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.managers.BillingManager

class PremiumActivity : AppCompatActivity() {

    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        billingManager = BillingManager(this, lifecycleScope).apply {
            initialize()
        }

        setupPurchaseButtons()
    }

    private fun setupPurchaseButtons() {
        // Implementar clicks dos botões de assinatura
        // findViewById<Button>(R.id.btn_monthly)?.setOnClickListener {
        //     billingManager.purchaseSubscription(this, BillingManager.PRODUCT_PREMIUM_MONTHLY)
        // }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
