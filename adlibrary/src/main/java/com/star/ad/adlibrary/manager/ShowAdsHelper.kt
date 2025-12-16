package com.star.ad.adlibrary.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.constants.AD_BIG
import com.star.ad.adlibrary.constants.AD_NORMAL
import com.star.ad.adlibrary.constants.AD_SMALL
import com.star.ad.adlibrary.dialog.AdDialogFragment
import com.star.ad.adlibrary.helper.NativeAdHelper
import com.star.ad.adlibrary.interfaces.OnInterstitialAdListener
import com.star.ad.adlibrary.interfaces.OnRewardListener
import com.star.ad.adlibrary.utils.AdsUtils.getBannerAdId
import com.star.ad.adlibrary.utils.AdsUtils.getInterstitialId
import com.star.ad.adlibrary.utils.AdsUtils.getNativeId
import com.star.ad.adlibrary.utils.AdsUtils.getRewardInterstitialId

object ShowAdsHelper {
    const val TAG = "ShowAdsHelper"
    private var mAdCount = 0
    private var AD_COUNT = 5
    private var isShowAds = true

    fun initAds(application: Application) {
        MobileAds.initialize(
            application,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? ->
                initializationStatus?.adapterStatusMap?.forEach { t, adapterStatus ->
                    Log.i(
                        TAG, "init complete key: ${t} adapterStatus" +
                                "\ninitializationState: ${adapterStatus.initializationState.name}" +
                                "\ndesc: ${adapterStatus.description}" +
                                "\nlatency: ${adapterStatus.latency}"
                    )
                }
            })
    }


    private fun isCanShowAds(): Boolean {
        if (!isShowAds) {
            return false
        }

        mAdCount++
        Log.i(TAG, "isCanShowAds mAdCount=$mAdCount")
        if (mAdCount >= AD_COUNT) {
            return true
        }
        return false
    }

    fun setCountSize(count: Int) {
        AD_COUNT = count
    }

    /**
     * 是否显示广告
     */
    fun setShowAds(isShowAd: Boolean) {
        this.isShowAds = isShowAd
    }

    /**
     * 显示banner 广告
     * @param context
     * @param viewGroup
     * @param adSize
     * BANNER:320x50
     * LARGE_BANNER:320x100
     * MEDIUM_RECTANGLE: 300x250
     * FULL_BANNER 468x60
     * LEADERBOARD 728x90
     * SMART_BANNER  屏幕宽度 x 32|50|90
     *
     *
     */
    fun showBannerAds(
        context: Context,
        viewGroup: ViewGroup,
        adSize: AdSize,
        listener: AdListener? = null,
        isDebug: Boolean = false
    ): AdView {
        val adView = AdView(context)
        adView.adUnitId = getBannerAdId(context, isDebug)
        adView.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        viewGroup.addView(adView)
        if (listener != null) {
            adView.adListener = listener
        }
        return adView
    }

    /**
     * 显示banner 广告
     * @param adView  AdView
     * @param listener  AdListener
     */
    fun showBannerAds(adView: AdView, listener: AdListener? = null) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        if (listener != null) {
            adView.adListener = listener
        }

    }

    /**
     * 返回adSize
     */
    fun getAdSize(context: Context, width: Int): AdSize {
        Log.d(TAG, "getAdSize =$width")
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
    }


    /**
     * ######################### 插页式广告 start #########################################
     */
    private var mInterstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false

    fun isLoadInterstitialAds(): Boolean {
        if (isCanShowAds() && mInterstitialAd != null) {
            return true
        }
        return false
    }

    /**
     * 加载插页广告
     */
    private fun loadInterstitialAds(context: Activity, isFirst: Boolean, isDebug: Boolean = false) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            getInterstitialId(context, isDebug),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad =${adError.cause}")
                    mInterstitialAd = null
                    adIsLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "onAdLoaded =$interstitialAd")
                    mInterstitialAd = interstitialAd
                    adIsLoading = false
                    if (isFirst) {
                        // listener.onComplete()
//                    adIsLoading = false
//                    mInterstitialAd?.show(context)
//                    mInterstitialAd = null
                    }
                }
            })
    }

    /**
     * 显示插页广告
     */
    fun showInterstitialAds(
        activity: Activity,
        listener: OnInterstitialAdListener,
        isImmediately: Boolean = false,
        isDebug: Boolean = false
    ) {
        if (!isCanShowAds() && !isImmediately) {
            listener.onComplete()
            return
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    listener.dismiss()
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    loadInterstitialAds(activity, false, isDebug)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                }


                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    mAdCount = 0
                    listener.showAd()
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            if (!adIsLoading && mInterstitialAd == null) {
                adIsLoading = true
                loadInterstitialAds(activity, true)
            }
            listener.onComplete()
        }
    }


    /**
     * ######################### 原生广告 end #########################################
     */

    @SuppressLint("StaticFieldLeak")
    private var mAdLoader: AdLoader? = null
    private var isDestroyNativeAd = false
    private var mCurrentNativeAd: NativeAd? = null
    fun showNativeAdLoader(
        activity: Activity,
        viewGroup: ViewGroup,
        listener: AdListener,
        adTypes: Int = 1,
        isDebug: Boolean = false
    ) {
        isDestroyNativeAd = false
        val adOptions = NativeAdOptions.Builder()
            //设置广告方向
            .setMediaAspectRatio(MediaAspectRatio.ANY)
            .build()
        mAdLoader = AdLoader.Builder(activity, getNativeId(activity, isDebug))
            .forNativeAd { ad: NativeAd ->
                if (activity.isDestroyed || activity.isFinishing) {
                    ad.destroy()
                    return@forNativeAd
                }
                mCurrentNativeAd?.destroy()
                mCurrentNativeAd = ad

                val view = loadNativeView(activity, ad, adTypes)
                if (view != null) {
                    viewGroup.removeAllViews()
                    viewGroup.addView(view)
                }
            }
            .withAdListener(listener)
            .withNativeAdOptions(adOptions)
            .build()
        // mAdLoader?.loadAds(AdRequest.Builder().build(), 3)
        mAdLoader?.loadAd(AdRequest.Builder().build())
    }

    private fun loadNativeView(activity: Activity, ad: NativeAd, type: Int = 1): View? {
        var view: View? = null
        when (type) {
            AD_SMALL -> {
                view = View.inflate(activity, R.layout.layout_native_small, null)
                NativeAdHelper.populateNativeAdSmallView(ad, view as NativeAdView)
            }

            AD_NORMAL -> {
                view = View.inflate(activity, R.layout.layout_native, null)
                NativeAdHelper.populateNativeAdView(ad, view as NativeAdView)

            }

            AD_BIG -> {
                view = View.inflate(activity, R.layout.layout_native, null)
                NativeAdHelper.populateNativeAdView(ad, view as NativeAdView)
            }

        }
        return view

    }


    fun releaseNativeAds() {
        isDestroyNativeAd = true
    }

    /**
     * 激励广告
     */
    private var mCoinCount: Int = 0
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoadingAds: Boolean = false

    fun isRewardedInterstitialAd(): Boolean {
        return isLoadingAds
    }

    fun initRewarded(activity: Activity, isDebug: Boolean = false) {
        Log.e(TAG, "initRewarded isLoadingAds= $isLoadingAds")
        if (!isLoadingAds) {
            loadRewardedInterstitialAd(activity, isDebug)
        }
    }

    private fun loadRewardedInterstitialAd(activity: Activity, isDebug: Boolean = false) {
        if (rewardedInterstitialAd == null) {
            isLoadingAds = true
            val adRequest = AdRequest.Builder().build()
            // Use the test ad unit ID to load an ad.
            RewardedInterstitialAd.load(
                activity,
                getRewardInterstitialId(activity, isDebug),
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        Log.d(TAG, "loadRewardedInterstitialAd onAdLoaded")
                        rewardedInterstitialAd = ad
                        isLoadingAds = false
                        mAdCount = 0
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.d(
                            TAG,
                            "loadRewardedInterstitialAd onAdFailedToLoad: " + loadAdError.message
                        )

                        // Handle the error.
                        rewardedInterstitialAd = null
                        isLoadingAds = false
                    }
                })
        }
    }

    private fun introduceVideoAd(
        activity: AppCompatActivity,
        rewardAmount: Int,
        rewardType: String,
        listener: OnRewardListener,
        isImmediately: Boolean = false,
        isDebug: Boolean = false
    ) {
        val dialog: AdDialogFragment = AdDialogFragment.newInstance(rewardAmount, rewardType)
        dialog.setAdDialogInteractionListener(
            object : AdDialogFragment.AdDialogInteractionListener {
                override fun onShowAd() {
                    // Log.d(MainActivity.TAG, "The rewarded interstitial ad is starting.")
                    showRewardedInterstitialVideo(activity, listener, isImmediately, isDebug)
                }

                override fun onCancelAd() {
                    //  Log.d(MainActivity.TAG, "The rewarded interstitial ad was skipped before it starts.")
                }
            })

        dialog.show(activity.supportFragmentManager, "AdDialogFragment")
    }


    fun showRewardedInterstitialAd(
        activity: AppCompatActivity,
        isShowDialog: Boolean,
        listener: OnRewardListener,
        isImmediately: Boolean = false,
        isDebug: Boolean = false
    ): Boolean {

        if (rewardedInterstitialAd == null) return false

        val rewardItem: RewardItem? = rewardedInterstitialAd?.rewardItem
        val rewardAmount = rewardItem?.amount
        val rewardType = rewardItem?.type

        if (rewardAmount == null || rewardType == null) return false
        if (!isCanShowAds() && !isImmediately) {
            return false
        }
        if (isShowDialog) {
            introduceVideoAd(activity, rewardAmount, rewardType, listener, isImmediately, isDebug)
        } else {
            showRewardedInterstitialVideo(activity, listener, isImmediately, isDebug)
        }
        return true
    }

    private fun showRewardedInterstitialVideo(
        activity: Activity,
        listener: OnRewardListener,
        isImmediately: Boolean = false,
        isDebug: Boolean = false
    ) {
        if (!isCanShowAds() && !isImmediately) {
            listener.onAdDismissed()
            return
        }
        if (rewardedInterstitialAd == null) {
            Log.d(TAG, "showRewardedVideo The rewarded interstitial ad wasn't ready yet.")
            return
        }
        rewardedInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when ad showed the full screen content.  */
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "showRewardedVideo onAdShowedFullScreenContent")
                mAdCount = 0
                listener.onAdShowed()
            }

            /** Called when the ad failed to show full screen content.  */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(
                    TAG,
                    "showRewardedVideo onAdFailedToShowFullScreenContent: " + adError.message
                )

                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                rewardedInterstitialAd = null
                loadRewardedInterstitialAd(activity, isDebug)

                listener.onAdFailedToShow()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG, "showRewardedVideo onAdImpression: ")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "showRewardedVideo onAdClicked: ")
            }

            /** Called when full screen content is dismissed.  */
            override fun onAdDismissedFullScreenContent() {
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                listener.onAdDismissed()
                rewardedInterstitialAd = null
                Log.d(TAG, "showRewardedVideo onAdDismissedFullScreenContent")

                // Preload the next rewarded interstitial ad.
                loadRewardedInterstitialAd(activity)
            }
        }
        val activityContext: Activity = activity
        rewardedInterstitialAd!!.show(
            activityContext
        ) { rewardItem -> // Handle the reward.
            Log.d(TAG, "showRewardedVideo The user earned the reward.")
            addCoins(rewardItem.amount)
            listener.onGetReward(rewardItem.amount)
        }
    }

    private fun addCoins(coins: Int) {
        mCoinCount += coins
        Log.d(TAG, "mCoinCount=$mCoinCount")
    }

    fun getCoins(): Int {
        return mCoinCount
    }


    /**
     * ############# 插页式激励 ################
     */

    fun loadRewardedInterstitialAd(context: Context, isDebug: Boolean = false) {
        RewardedInterstitialAd.load(
            context, getRewardInterstitialId(context, isDebug),
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d(TAG, "loadRewardedInterstitialAd Ad was loaded.")
                    rewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "loadRewardedInterstitialAd=" + adError.cause.toString())
                    rewardedInterstitialAd = null
                }
            })
    }

    fun showRewardedInterstitialAd(
        activity: Activity,
        listener: OnUserEarnedRewardListener,
    ) {
        rewardedInterstitialAd?.show(activity, listener)
    }

    fun isLoadRewardedInterstitialAd(): Boolean {
        if (rewardedInterstitialAd != null && isCanShowAds()) {
            return true
        }
        return false
    }


}