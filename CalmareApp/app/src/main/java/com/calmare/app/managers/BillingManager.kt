package com.calmare.app.managers

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Gerenciador central do Google Play Billing (Assinaturas e Compras)
 *
 * IMPORTANTE: Configure seus produtos no Google Play Console primeiro!
 */
class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope
) : PurchasesUpdatedListener {

    // ========== IDs DOS PRODUTOS - CONFIGURE NO PLAY CONSOLE ==========
    companion object {
        // Assinaturas
        const val PRODUCT_PREMIUM_MONTHLY = "premium_monthly"  // Mensal R$ 19,90
        const val PRODUCT_PREMIUM_YEARLY = "premium_yearly"    // Anual R$ 154,80

        // Compras únicas
        const val PRODUCT_REMOVE_ADS = "remove_ads"            // Única R$ 14,90
        const val PRODUCT_LIFETIME = "lifetime_premium"        // Única R$ 89,90 (opcional)
    }

    private var billingClient: BillingClient? = null

    // LiveData para observar status premium
    private val _isPremium = MutableLiveData(false)
    val isPremium: LiveData<Boolean> = _isPremium

    private val _hasRemovedAds = MutableLiveData(false)
    val hasRemovedAds: LiveData<Boolean> = _hasRemovedAds

    // Produtos disponíveis
    private val subscriptionProducts = mutableListOf<ProductDetails>()
    private val inAppProducts = mutableListOf<ProductDetails>()

    /**
     * Inicializa o Billing Client
     */
    fun initialize(onReady: () -> Unit = {}) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Carrega produtos e verifica compras
                    queryProducts()
                    queryPurchases()
                    onReady()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Tentar reconectar
            }
        })
    }

    /**
     * Carrega produtos disponíveis (assinaturas + in-app)
     */
    private fun queryProducts() {
        // Assinaturas
        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM_MONTHLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM_YEARLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(subParams) { _, products ->
            subscriptionProducts.clear()
            subscriptionProducts.addAll(products)
        }

        // In-App (compras únicas)
        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_REMOVE_ADS)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_LIFETIME)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(inAppParams) { _, products ->
            inAppProducts.clear()
            inAppProducts.addAll(products)
        }
    }

    /**
     * Verifica compras existentes
     */
    fun queryPurchases() {
        scope.launch(Dispatchers.IO) {
            // Verifica assinaturas
            val subResult = billingClient?.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            subResult?.purchasesList?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    when {
                        purchase.products.contains(PRODUCT_PREMIUM_MONTHLY) ||
                        purchase.products.contains(PRODUCT_PREMIUM_YEARLY) -> {
                            _isPremium.postValue(true)
                        }
                    }
                    // Acknowledge se necessário
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }

            // Verifica in-app purchases
            val inAppResult = billingClient?.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            inAppResult?.purchasesList?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    when {
                        purchase.products.contains(PRODUCT_REMOVE_ADS) -> {
                            _hasRemovedAds.postValue(true)
                        }
                        purchase.products.contains(PRODUCT_LIFETIME) -> {
                            _isPremium.postValue(true)
                        }
                    }
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }
        }
    }

    /**
     * Inicia fluxo de compra de assinatura
     */
    fun purchaseSubscription(
        activity: Activity,
        productId: String,
        onError: (String) -> Unit = {}
    ) {
        val product = subscriptionProducts.find { it.productId == productId }
        if (product == null) {
            onError("Produto não encontrado")
            return
        }

        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            onError("Oferta não disponível")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .setOfferToken(offerToken)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, flowParams)
    }

    /**
     * Inicia fluxo de compra única
     */
    fun purchaseInApp(
        activity: Activity,
        productId: String,
        onError: (String) -> Unit = {}
    ) {
        val product = inAppProducts.find { it.productId == productId }
        if (product == null) {
            onError("Produto não encontrado")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, flowParams)
    }

    /**
     * Callback quando compra é atualizada
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Atualiza status
            when {
                purchase.products.contains(PRODUCT_PREMIUM_MONTHLY) ||
                purchase.products.contains(PRODUCT_PREMIUM_YEARLY) ||
                purchase.products.contains(PRODUCT_LIFETIME) -> {
                    _isPremium.postValue(true)
                }
                purchase.products.contains(PRODUCT_REMOVE_ADS) -> {
                    _hasRemovedAds.postValue(true)
                }
            }

            // Acknowledge se necessário
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        scope.launch(Dispatchers.IO) {
            billingClient?.acknowledgePurchase(params)
        }
    }

    /**
     * Retorna se deve mostrar anúncios
     */
    fun shouldShowAds(): Boolean {
        return !(_isPremium.value == true || _hasRemovedAds.value == true)
    }

    /**
     * Libera recursos
     */
    fun destroy() {
        billingClient?.endConnection()
    }
}
