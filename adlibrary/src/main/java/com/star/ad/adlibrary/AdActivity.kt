package com.star.ad.adlibrary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.star.ad.adlibrary.interfaces.OnInterstitialAdListener
import com.star.ad.adlibrary.interfaces.OnRewardListener
import com.star.ad.adlibrary.interfaces.OnShowAdCompleteListener
import com.star.ad.adlibrary.manager.AppOpenAdManager
import com.star.ad.adlibrary.manager.ShowAdsHelper
import com.star.ad.adlibrary.manager.ShowAdsHelper.initRewarded
import com.star.ad.adlibrary.manager.ShowAdsHelper.loadRewardedInterstitialAd
import com.star.ad.adlibrary.manager.ShowAdsHelper.releaseNativeAds
import com.star.ad.adlibrary.manager.ShowAdsHelper.showBannerAds
import com.star.ad.adlibrary.manager.ShowAdsHelper.showInterstitialAds
import com.star.ad.adlibrary.manager.ShowAdsHelper.showNativeAdLoader
import com.star.ad.adlibrary.manager.ShowAdsHelper.showRewardedAd
import com.star.ad.adlibrary.manager.ShowAdsHelper.showRewardedInterstitialAd


class AdActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val TAG = "AdActivity"
    }

    private lateinit var btnLoadRewardAd: Button
    private lateinit var btnShowRewardAd: Button
    private lateinit var btnLoadNativeAd: Button
    private lateinit var tv_load_status: TextView
    private lateinit var btnOpenAd: Button
    private lateinit var rlBannerAds: RelativeLayout
    private lateinit var btnInterstitialAd: Button
    private lateinit var adFrame: FrameLayout
    private lateinit var nativeAdFrame: FrameLayout
    private lateinit var btnLoadRewardInterstitialAd: Button
    private lateinit var btnShowRewardInterstitialAd: Button

    private lateinit var mAdView: AdView

    val REWRDAD_TEST_ID = "ca-app-pub-3940256099942544/5224354917"
    val NATIVEAD_TEST_ID = "ca-app-pub-3940256099942544/2247696110"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        initView()
        initData()


    }


    private fun initView() {

        btnLoadRewardAd = findViewById(R.id.btn_load_reward_ad)
        btnLoadRewardAd.setOnClickListener(this)
        tv_load_status = findViewById(R.id.tv_load_status)
        btnLoadNativeAd = findViewById(R.id.btn_load_native_ad)
        btnLoadNativeAd.setOnClickListener(this)
        btnShowRewardAd = findViewById(R.id.btn_show_reward_ad)
        btnShowRewardAd.setOnClickListener(this)
        btnOpenAd = findViewById(R.id.btn_open_ad)
        btnOpenAd.setOnClickListener(this)
        rlBannerAds = findViewById(R.id.rl_banner_ads)
        rlBannerAds.setOnClickListener(this)
        btnInterstitialAd = findViewById(R.id.btn_interstitial_ad)
        btnInterstitialAd.setOnClickListener(this)
        adFrame = findViewById(R.id.ad_frame)
        nativeAdFrame = findViewById(R.id.native_ad_frame)
        btnLoadRewardInterstitialAd = findViewById(R.id.btn_load_reward_interstitial_ad)
        btnLoadRewardInterstitialAd.setOnClickListener(this)
        btnShowRewardInterstitialAd = findViewById(R.id.btn_show_reward_interstitial_ad)
        btnShowRewardInterstitialAd.setOnClickListener(this)

        mAdView = showBannerAds(this, rlBannerAds, AdSize.BANNER)

    }

    private fun initData() {


    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_load_native_ad -> {
                //原生广告
                showNativeAdLoader(this@AdActivity, adFrame, object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.e(TAG, "showNativeAdLoader onAdFailedToLoad")
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.e(TAG, "showNativeAdLoader onAdLoaded")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Log.e(TAG, "showNativeAdLoader onAdOpened")
                    }
                }, 0)
            }

            R.id.btn_open_ad -> {//开屏广告
                AppOpenAdManager.showAdIfAvailable(
                    this@AdActivity,
                    object : OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            Log.e(TAG, "onShowAdComplete 3243242")
                        }

                        override fun onShowAdError(adError: AdError) {

                        }

                        override fun onAdShowedFullScreenContent() {

                        }
                    })
            }

            R.id.btn_interstitial_ad -> {//插页广告
                showInterstitialAds(this@AdActivity, object : OnInterstitialAdListener {
                    override fun onComplete() {
                        Log.e(TAG, "showInterstitialAds onComplete")
                    }
                })

            }

            R.id.btn_load_reward_ad -> {
                initRewarded(this@AdActivity)

            }

            R.id.btn_show_reward_ad -> {
                showRewardedAd(this@AdActivity, false, object : OnRewardListener {
                    override fun onAdShowed() {

                    }

                    override fun onAdFailedToShow() {

                    }

                    override fun onAdDismissed() {

                    }

                })
            }

            R.id.btn_load_reward_interstitial_ad -> {
                loadRewardedInterstitialAd(this@AdActivity)
            }

            R.id.btn_show_reward_interstitial_ad -> {
                showRewardedInterstitialAd(this@AdActivity,
                    OnUserEarnedRewardListener { rewardItem ->
                        Log.e(TAG, " rewardItem amount=${rewardItem.amount}  type =${rewardItem.type}")
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAdView.resume()
    }

    override fun onPause() {
        super.onPause()
        mAdView.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        mAdView.destroy()

        releaseNativeAds()

    }


}