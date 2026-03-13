package com.start.ad.demo

import android.os.Looper
import android.util.Log
import com.star.ad.adlibrary.AdApplication
import com.star.ad.adlibrary.manager.ShowAdsHelper

class MyApplication: AdApplication() {

    override fun onCreate() {
        super.onCreate()
        val time = System.currentTimeMillis()
        Looper.myQueue().addIdleHandler {

            ShowAdsHelper.initAds(this)
            Log.d("MyApplication", "Displayed time11: ${System.currentTimeMillis() - time}")
            false
        }
    }
}