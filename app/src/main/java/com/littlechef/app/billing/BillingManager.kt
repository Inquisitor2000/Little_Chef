package com.littlechef.app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.littlechef.app.data.local.AssetPackManager
import com.littlechef.app.data.preferences.DLCPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dlcPreferences: DLCPreferences,
    private val assetPackManager: AssetPackManager
) : PurchasesUpdatedListener {

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private var billingClient: BillingClient? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected")
                    // Query existing purchases
                    queryPurchases()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected")
                // Try to reconnect
                initializeBillingClient()
            }
        })
    }

    /**
     * Launch the purchase flow for a specific product
     */
    fun launchPurchaseFlow(activity: Activity, productId: String) {
        Log.d(TAG, "launchPurchaseFlow called with productId: $productId")
        
        val client = billingClient
        if (client == null || !client.isReady) {
            Log.e(TAG, "Billing client not ready. Client: $client, isReady: ${client?.isReady}")
            _purchaseState.value = PurchaseState.Error("Billing not ready")
            return
        }

        Log.d(TAG, "Billing client is ready, setting state to Loading")
        _purchaseState.value = PurchaseState.Loading

        scope.launch {
            try {
                Log.d(TAG, "Querying product details for: $productId")
                
                // Query product details
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )

                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()

                val productDetailsResult = client.queryProductDetails(params)
                
                Log.d(TAG, "Product details result: ${productDetailsResult.billingResult.responseCode}")

                if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetails = productDetailsResult.productDetailsList?.firstOrNull()
                    
                    Log.d(TAG, "Product details: $productDetails")
                    
                    if (productDetails != null) {
                        Log.d(TAG, "Launching billing flow...")
                        
                        // Launch billing flow
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .build()
                                )
                            )
                            .build()

                        val billingResult = client.launchBillingFlow(activity, flowParams)
                        
                        Log.d(TAG, "Billing flow launched with result: ${billingResult.responseCode}")
                        
                        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                            Log.e(TAG, "Failed to launch billing flow: ${billingResult.debugMessage}")
                            _purchaseState.value = PurchaseState.Error("Failed to launch billing flow")
                        }
                    } else {
                        Log.e(TAG, "Product not found in Play Console")
                        _purchaseState.value = PurchaseState.Error("Product not found")
                    }
                } else {
                    Log.e(TAG, "Failed to query product details: ${productDetailsResult.billingResult.debugMessage}")
                    _purchaseState.value = PurchaseState.Error("Failed to query product details")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error launching purchase flow", e)
                _purchaseState.value = PurchaseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _purchaseState.value = PurchaseState.Cancelled
        } else {
            _purchaseState.value = PurchaseState.Error(billingResult.debugMessage)
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge the purchase if not already acknowledged
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                // Purchase already acknowledged, mark as purchased
                scope.launch {
                    purchase.products.forEach { productId ->
                        dlcPreferences.markPurchased(productId)
                    }
                    _purchaseState.value = PurchaseState.Success(purchase.products.first())
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return

        scope.launch {
            try {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                val ackResult = client.acknowledgePurchase(acknowledgePurchaseParams)

                if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Mark as purchased in preferences
                    purchase.products.forEach { productId ->
                        dlcPreferences.markPurchased(productId)
                        
                        // Trigger asset pack download
                        Log.d(TAG, "Starting asset pack download for: $productId")
                        _purchaseState.value = PurchaseState.Downloading(productId)
                        
                        // Download the asset pack
                        assetPackManager.requestDownload(productId).collect { downloadState ->
                            when (downloadState) {
                                is com.littlechef.app.data.local.AssetPackDownloadState.Pending -> {
                                    Log.d(TAG, "Download pending...")
                                    _purchaseState.value = PurchaseState.Downloading(productId, 0)
                                }
                                is com.littlechef.app.data.local.AssetPackDownloadState.Downloading -> {
                                    Log.d(TAG, "Download progress: ${downloadState.progress}%")
                                    _purchaseState.value = PurchaseState.Downloading(productId, downloadState.progress)
                                }
                                is com.littlechef.app.data.local.AssetPackDownloadState.Completed -> {
                                    Log.d(TAG, "Asset pack downloaded successfully")
                                    _purchaseState.value = PurchaseState.Success(productId)
                                }
                                is com.littlechef.app.data.local.AssetPackDownloadState.Failed -> {
                                    Log.e(TAG, "Asset pack download failed with error code: ${downloadState.errorCode}")
                                    _purchaseState.value = PurchaseState.Error("Download failed")
                                }
                                is com.littlechef.app.data.local.AssetPackDownloadState.Canceled -> {
                                    Log.d(TAG, "Asset pack download canceled")
                                    _purchaseState.value = PurchaseState.Cancelled
                                }
                            }
                        }
                    }
                } else {
                    _purchaseState.value = PurchaseState.Error("Failed to acknowledge purchase")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error acknowledging purchase", e)
                _purchaseState.value = PurchaseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Query existing purchases to restore them
     */
    private fun queryPurchases() {
        val client = billingClient ?: return

        scope.launch {
            try {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()

                val purchasesResult = client.queryPurchasesAsync(params)

                if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchasesResult.purchasesList.forEach { purchase ->
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            // Restore purchase
                            purchase.products.forEach { productId ->
                                dlcPreferences.markPurchased(productId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying purchases", e)
            }
        }
    }

    fun resetPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
    
    /**
     * Check if a product is purchased
     */
    suspend fun isPurchased(productId: String): Boolean {
        return dlcPreferences.isPurchased(productId)
    }

    fun endConnection() {
        billingClient?.endConnection()
    }

    companion object {
        private const val TAG = "BillingManager"
    }
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Loading : PurchaseState()
    data class Downloading(val productId: String, val progress: Int = 0) : PurchaseState()
    data class Success(val productId: String) : PurchaseState()
    object Cancelled : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
