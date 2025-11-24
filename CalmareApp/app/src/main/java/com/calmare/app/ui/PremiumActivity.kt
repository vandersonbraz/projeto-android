package com.calmare.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

        setupPurchaseButton()
    }

    private fun setupPurchaseButton() {
        // Botão de Remover Anúncios (R$ 14,90)
        findViewById<Button>(R.id.btn_remove_ads)?.setOnClickListener {
            Toast.makeText(this, "Processando compra...", Toast.LENGTH_SHORT).show()
            billingManager.purchaseInApp(this, BillingManager.PRODUCT_REMOVE_ADS) { error ->
                Toast.makeText(this, "Erro: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
