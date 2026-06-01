package com.star.ad.adlibrary.helper

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.star.ad.adlibrary.coroutine.launchMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingHelper(private val context: Context, private val externalScope: CoroutineScope) {

    companion object {
        private const val TAG = "BillingHelper"
    }

    private var billingClient: BillingClient? = null

    private var retryCount = 0
    private val MAX_RETRY_COUNT = 3
    private val RETRY_DELAY = 2000L // 2秒

    // 状态追踪
    private val _productDetailsList = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsList = _productDetailsList.asStateFlow()

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed = _isSubscribed.asStateFlow()


    private var mProductDetails: ProductDetails? = null

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        // 1. 确保定义了监听器
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->

            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            when (responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // 支付成功
                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }
                }

                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Log.w(TAG, "User pressed the back button or tapped outside of attempt.")
                }

                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    Log.w(TAG, "User already owns this product.")
                    // 如果用户已拥有，强制同步一次状态
                    queryPurchases()
                }

                BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                    Log.e(TAG, "Developer Error: Check console config. $debugMessage")
                }

                else -> {
                    Log.e(TAG, "Billing Error ($responseCode): $debugMessage")
                }
            }
        }

        // 2. 准备 PendingPurchasesParams (适配 Billing 6.0+)
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        // 3. 构建 Client
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener) // <-- 确保这一行在 .build() 之前
            .enablePendingPurchases(pendingPurchasesParams)
            .enableAutoServiceReconnection()
            .build()


        debugBillingEnvironment(context)

        startConnection()
    }

    private fun debugBillingEnvironment(context: Context) {
        val pm = context.packageManager
        // 1. 检查商店是否存在
        val isStoreInstalled = try {
            pm.getPackageInfo("com.android.vending", 0)
            true
        } catch (e: Exception) {
            false
        }

        // 2. 检查 Google Play 服务状态
        val gmsStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)

        Log.e(TAG, "Store Installed: $isStoreInstalled, GMS Status Code: $gmsStatus")
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.e(TAG, "Billing client connected =${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 连接成功，查询商品和历史订单
                    launchMain {
                        delay(500)
                        querySubscriptionProducts()
                        queryPurchases()
                    }

                }
            }

            override fun onBillingServiceDisconnected() {
                // 断开连接时尝试重连
                // startConnection()
            }
        })
    }

    /**
     * 查询后台配置的所有订阅商品
     */
    fun querySubscriptionProducts() {
        if (billingClient?.isReady != true) {
            Log.w(TAG, "Billing client not ready, skipping product query")
            return
        }

        if (!isBillingSupported()) {
            Log.e(TAG, "Billing not supported on this device")
            return
        }

        if (!checkGooglePlayServices(context)) {
            Log.e(TAG, "Google Play services not available")
            return
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("gallery_star") // 替换为你在 Play Console 设置的 ID
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, result ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            when (responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    val list = result.productDetailsList

                    if (list.isEmpty()) {
                        Log.e(
                            TAG,
                            "Query OK but list is EMPTY. Check if 'gallery_star' is ACTIVE and has an ACTIVE Base Plan."
                        )
                    } else {
                        mProductDetails = list[0]
                        _productDetailsList.value = list
                        Log.d(TAG, "Successfully queried ${list.size} products. ID: ${mProductDetails?.productId}")
                    }
                }

                BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                    // Code 12 核心报错
                    Log.e(
                        TAG,
                        "DEVELOPER_ERROR (12): 1.检查包名(com.zong.sen.photo.photoeditor) 2.检查签名SHA1是否加入许可测试 3.确保BasePlan已点击'激活'"
                    )
                }

                else -> {
                    Log.e(TAG, "Query failed with code $responseCode: $debugMessage")
                }
            }

        }
    }


    suspend fun processPurchases() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("gallery_star")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient?.queryProductDetails(params.build())
        }

        // Process the result.
    }

    private fun handleServiceUnavailable() {
        retryCount++
        Log.w(TAG, "SERVICE_UNAVAILABLE: Attempt $retryCount/$MAX_RETRY_COUNT")

        if (retryCount <= MAX_RETRY_COUNT) {
            // 延迟重试
            externalScope.launch {
                delay(RETRY_DELAY * retryCount) // 递增延迟
                Log.d(TAG, "Retrying querySubscriptionProducts...")
                querySubscriptionProducts()
            }
        } else {
            Log.e(TAG, "Max retry attempts reached. Service unavailable.")
            // 可以在这里通知用户或使用备用方案
        }
    }

    private fun checkBillingEnvironment(context: Context): String {
        val pm = context.packageManager
        val isStoreInstalled = try {
            pm.getPackageInfo("com.android.vending", 0)
            true
        } catch (e: Exception) {
            false
        }

        return if (!isStoreInstalled) "未安装 Play 商店"
        else "Play 商店已安装，请检查网络代理"
    }

    private fun isBillingSupported(): Boolean {
        val result = billingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        return result?.responseCode == BillingClient.BillingResponseCode.OK
    }

    private fun checkGooglePlayServices(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    /**
     * 发起订阅购买流
     */
    fun launchBillingFlow(activity: Activity) {
        val details = mProductDetails ?: run {
            Log.e(TAG, "mProductDetails is null, cannot launch flow")
            return
        }

        // 某些订阅可能没有 OfferDetails (如果未配置基础方案)
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken

        if (offerToken == null) {
            Log.e(TAG, "No offer token found for product: ${details.productId}. Is the base plan active?")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }


    /**
     * 处理订单确认
     */
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _isSubscribed.value = true
                    }
                }
            } else {
                _isSubscribed.value = true
            }
        }
    }

    /**
     * 查询用户当前的有效订单（检查会员状态）
     */
    fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient?.queryPurchasesAsync(params) { _, purchasesList ->
            val hasActiveSub = purchasesList.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            _isSubscribed.value = hasActiveSub
        }
    }
}