package com.start.ad.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.star.ad.adlibrary.AdActivity
import com.star.ad.adlibrary.interfaces.OnShowAdCompleteListener
import com.star.ad.adlibrary.manager.AppOpenAdManager
import com.star.ad.adlibrary.manager.ShowAdsHelper.showBannerAds
import com.star.ad.adlibrary.thread.ScheduledExecutor
import com.star.ad.adlibrary.utils.WindowInsetsUtils
import com.star.ad.adlibrary.utils.WindowInsetsUtils.SET_WINDOW_DEFAULT
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity(), OnClickListener {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var btnOpenAd: AppCompatButton
    private lateinit var mAdView: AdView
    private lateinit var rlAdView: RelativeLayout

    private var mSessionScheduledFuture: ScheduledFuture<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowInsetsUtils.setOnApplyWindowInsetsListener(window.decorView, SET_WINDOW_DEFAULT)

        initView()
    }

    private fun initView() {
        btnOpenAd = findViewById(R.id.btn_open_ad)
        btnOpenAd.setOnClickListener(this)
        rlAdView = findViewById(R.id.rl_ad_view)

        mAdView = findViewById(R.id.adView)

        showBannerAds(mAdView, object : AdListener() {

        })

//        showBannerAds(this, rlAdView, AdSize.BANNER, object : AdListener() {
//            override fun onAdFailedToLoad(p0: LoadAdError) {
//                Log.e(TAG, "onAdFailedToLoad")
//            }
//
//            override fun onAdLoaded() {
//                Log.e(TAG, "onAdLoaded")
//            }
//        })

    }

    override fun onResume() {
        super.onResume()
        start()
    }

    private fun start() {
        mSessionScheduledFuture?.cancel(true)
        mSessionScheduledFuture = ScheduledExecutor.schedule(2000, TimeUnit.MILLISECONDS) {


        }
    }


    private fun startMainActivity() {
        val intent = Intent(this, AdActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_open_ad -> {
                AppOpenAdManager?.showAdIfAvailable(
                    this@MainActivity,
                    object : OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            Log.e(TAG, "onShowAdComplete 1111")
                            startMainActivity()
                        }

                        override fun onShowAdError(adError: AdError) {

                        }

                        override fun onAdShowedFullScreenContent() {

                        }
                    })

            }

        }

    }
}

