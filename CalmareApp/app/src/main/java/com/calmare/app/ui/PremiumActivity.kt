package com.calmare.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.calmare.app.R
import com.calmare.app.managers.BillingManager

class PremiumActivity : AppCompatActivity() {

    private lateinit var billingManager: BillingManager
    private var selectedPlan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        billingManager = BillingManager(this, lifecycleScope).apply {
            initialize()
        }

        setupPlanSelection()
        setupPurchaseButtons()
    }

    private fun setupPlanSelection() {
        val cardMonthly = findViewById<CardView>(R.id.card_plan_monthly)
        val cardAnnual = findViewById<CardView>(R.id.card_plan_annual)
        val cardLifetime = findViewById<CardView>(R.id.card_plan_lifetime)

        // Plano Mensal
        cardMonthly?.setOnClickListener {
            selectedPlan = "monthly"
            highlightCard(cardMonthly, cardAnnual, cardLifetime)
        }

        // Plano Anual (Recomendado)
        cardAnnual?.setOnClickListener {
            selectedPlan = "annual"
            highlightCard(cardAnnual, cardMonthly, cardLifetime)
        }

        // Plano Vitalício
        cardLifetime?.setOnClickListener {
            selectedPlan = "lifetime"
            highlightCard(cardLifetime, cardMonthly, cardAnnual)
        }

        // Seleciona Anual por padrão (melhor oferta)
        cardAnnual?.performClick()
    }

    private fun highlightCard(selected: CardView?, vararg others: CardView?) {
        selected?.cardElevation = 8f
        selected?.alpha = 1.0f

        others.forEach { card ->
            card?.cardElevation = 2f
            card?.alpha = 0.7f
        }
    }

    private fun setupPurchaseButtons() {
        // Botão principal de assinatura Premium
        findViewById<Button>(R.id.btn_subscribe)?.setOnClickListener {
            when (selectedPlan) {
                "monthly" -> {
                    Toast.makeText(this, "Processando assinatura Mensal...", Toast.LENGTH_SHORT).show()
                    // billingManager.purchaseSubscription(this, "premium_monthly")
                }
                "annual" -> {
                    Toast.makeText(this, "Processando assinatura Anual...", Toast.LENGTH_SHORT).show()
                    // billingManager.purchaseSubscription(this, "premium_annual")
                }
                "lifetime" -> {
                    Toast.makeText(this, "Processando compra Vitalícia...", Toast.LENGTH_SHORT).show()
                    // billingManager.purchaseProduct(this, "premium_lifetime")
                }
            }
        }

        // Card de Remover Anúncios (compra única)
        findViewById<CardView>(R.id.card_remove_ads)?.setOnClickListener {
            Toast.makeText(this, "Processando Remoção de Anúncios...", Toast.LENGTH_SHORT).show()
            // billingManager.purchaseProduct(this, "remove_ads")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
