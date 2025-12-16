//package com.star.ad.adlibrary.helper
//
//import android.app.Activity
//import android.util.Log
//import com.android.billingclient.api.BillingClient
//import com.android.billingclient.api.BillingClient.BillingResponseCode
//import com.android.billingclient.api.BillingClientStateListener
//import com.android.billingclient.api.BillingFlowParams
//import com.android.billingclient.api.BillingResult
//import com.android.billingclient.api.PurchasesUpdatedListener
//import com.android.billingclient.api.QueryProductDetailsParams
//import com.google.common.collect.ImmutableList
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//
//object PayHelper {
//    private const val TAG = "PayHelper"
//
//    private var mBillingClient: BillingClient? = null
//
//    /**
//     * 购买记录的更新
//     */
//    private val purchasesUpdatedListener =
//        PurchasesUpdatedListener { billingResult, purchases ->
//            // To be implemented in a later section.
//            Log.e(TAG,"billingResult =$billingResult purchases=$purchases")
//        }
//
//    /**
//     * ProductType.INAPP（针对一次性商品），也可以是 ProductType.SUBS（针对订阅）
//     */
//    val queryProductDetailsParams =
//        QueryProductDetailsParams.newBuilder()
//            .setProductList(
//                ImmutableList.of(
//                    QueryProductDetailsParams.Product.newBuilder()
//                        .setProductId("product_id_example")
//                        .setProductType(BillingClient.ProductType.SUBS)
//                        .build()))
//            .build()
//
//    fun init(activity: Activity) {
//        mBillingClient = BillingClient.newBuilder(activity)
//            .setListener(purchasesUpdatedListener)
//           // .enablePendingPurchases()
//            .enableAutoServiceReconnection()
//            .build()
//        mBillingClient?.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                }
//            }
//        })
//
//    }
//
//    fun queryProducts(){
//        mBillingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, queryProductDetailsResult  ->
//            if (billingResult.getResponseCode() === BillingResponseCode.OK) {
//                for (productDetails in queryProductDetailsResult.productDetailsList) {
//                    // Process successfully retrieved product details here.
//                }
//
//                for (unfetchedProduct in queryProductDetailsResult .unfetchedProductList) {
//                    // Handle any unfetched products as appropriate.
//                }
//            }
//        }
//    }
//
//    suspend fun processPurchases() {
//        val productList = listOf(
//            QueryProductDetailsParams.Product.newBuilder()
//                .setProductId("product_id_example")
//                .setProductType(BillingClient.ProductType.SUBS)
//                .build()
//        )
//        val params = QueryProductDetailsParams.newBuilder()
//        params.setProductList(productList)
//
//        // leverage queryProductDetails Kotlin extension function
//        val productDetailsResult = withContext(Dispatchers.IO) {
//            mBillingClient?.queryProductDetailsAsync(params.build(), { billingResult, productDetailsList ->
//                if (billingResult.responseCode == BillingResponseCode.OK  ) {
//                    // Process the result.
//                }
//            })
//        }
//
//        // Process the result.
//    }
//
//    fun startPay(activity: Activity){
//        val productDetailsParamsList = listOf(
//                BillingFlowParams.ProductDetailsParams.newBuilder()
//                    // retrieve a value for productDetails by calling queryProductDetailsAsync()
//                    .setProductDetails(productDetails)
//                    // Get the offer token:
//                    // a. For one-time products, call ProductDetails.getOneTimePurchaseOfferDetailsList()
//                    // for a list of offers that are available to the user.
//                    // b. For subscriptions, call ProductDetails.subscriptionOfferDetails()
//                    // for a list of offers that are available to the user.
//                    .setOfferToken(selectedOfferToken)
//                    .build()
//
//            val billingFlowParams = BillingFlowParams.newBuilder()
//            .setProductDetailsParamsList(productDetailsParamsList)
//            .build()
//
//
//        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
//
//    }
//
//}