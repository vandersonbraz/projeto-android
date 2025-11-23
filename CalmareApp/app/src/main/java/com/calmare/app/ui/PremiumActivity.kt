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
        // Botão de Remover Anúncios
        findViewById<Button>(R.id.btn_remove_ads)?.setOnClickListener {
            Toast.makeText(this, "Processando compra...", Toast.LENGTH_SHORT).show()
            // billingManager.purchaseProduct(this, "remove_ads")

            // Simulação de compra bem-sucedida (remover quando integrar billing real)
            Toast.makeText(this, "✅ Compra realizada! Anúncios removidos!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
