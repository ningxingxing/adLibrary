package com.star.ad.adlibrary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.material.snackbar.Snackbar
import com.star.ad.adlibrary.constants.KEY_PACKAGE_NAME
import com.star.ad.adlibrary.helper.AppUpdateHelper
import com.star.ad.adlibrary.interfaces.IAppUpdateHelper
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


class AdActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val TAG = "AdActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, AdActivity::class.java))
        }
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
    private lateinit var btnUpdate: Button

    private lateinit var mAdView: AdView

    private lateinit var mLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var mAppUpdateHelper: AppUpdateHelper? = null


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
        btnUpdate = findViewById(R.id.btn_update)
        btnUpdate.setOnClickListener(this)

        mAdView = showBannerAds(this, rlBannerAds, AdSize.BANNER)

    }

    private fun initData() {


        registerForActivityResult()

        mAppUpdateHelper = AppUpdateHelper(this, mLauncher)

    }

    private fun registerForActivityResult() {
        mLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                Log.i(TAG, "mLauncher resultCode: ${it.resultCode}")
                if (it.resultCode != RESULT_OK) {
                    Log.i(TAG, "mLauncher resultCode 11: ${it.resultCode}")
                    // If the update is canceled or fails,
                    // you can request to start the update again.
                }
            }
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
                }, 0, true)
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
                    }, true
                )
            }

            R.id.btn_interstitial_ad -> {//插页广告
                showInterstitialAds(this@AdActivity, object : OnInterstitialAdListener {
                    override fun onComplete() {
                        Log.e(TAG, "showInterstitialAds onComplete")
                    }

                    override fun showAd() {
                        super.showAd()
                    }

                    override fun dismiss() {
                        super.dismiss()

                    }
                }, true)

            }

            R.id.btn_load_reward_ad -> {
                initRewarded(this@AdActivity, true)

            }

            R.id.btn_show_reward_ad -> {
                ShowAdsHelper.showRewardedInterstitialAd(this, false, object : OnRewardListener {
                    override fun onAdShowed() {
                        Log.d(TAG, "onAdShowed")
                    }

                    override fun onAdFailedToShow() {
                        Log.d(TAG, "onAdFailedToShow")
                    }

                    override fun onAdDismissed() {
                        Log.d(TAG, "onAdDismissed")
                    }

                }, true)
            }

            R.id.btn_load_reward_interstitial_ad -> {
                loadRewardedInterstitialAd(this@AdActivity, true)
            }

            R.id.btn_show_reward_interstitial_ad -> {
                ShowAdsHelper.showRewardedInterstitialAd(
                    this,
                    OnUserEarnedRewardListener { rewardItem ->
                        Log.e(
                            TAG,
                            " rewardItem amount=${rewardItem.amount}  type =${rewardItem.type}"
                        )
                    })
            }

            R.id.btn_update -> {
                val intent = Intent(this@AdActivity, RecommendActivity::class.java)
                intent.putExtra(KEY_PACKAGE_NAME, resources.getString(R.string.gallery_package_name))
                startActivity(intent)

//                popupSnackbarForCompleteUpdate()
//                mAppUpdateHelper?.update(object : IAppUpdateHelper {
//                    override fun onUpdateState(updateAvailability: Int) {
//
//                    }
//
//                    override fun onUpdateProgress(progress: Long, maxSize: Long) {
//
//                    }
//
//                    override fun onDownloadFinish() {
//                        super.onDownloadFinish()
//
//                    }
//
//                })


            }
        }
    }


    fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            btnUpdate,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") {
                mAppUpdateHelper?.updateComplete()
            }
            setActionTextColor(context.resources.getColor(R.color.teal_700))
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        mAdView.resume()

        mAppUpdateHelper?.checkAppDownload(object : IAppUpdateHelper {
            override fun onDownloadFinish() {
                super.onDownloadFinish()
                popupSnackbarForCompleteUpdate()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mAdView.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        mAdView.destroy()

        releaseNativeAds()

        mAppUpdateHelper?.unregisterUpdateListener()

    }


}