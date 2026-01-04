package com.start.ad.demo

import com.star.ad.adlibrary.AdApplication
import com.star.ad.adlibrary.manager.ShowAdsHelper

class MyApplication: AdApplication() {

    override fun onCreate() {
        super.onCreate()
        ShowAdsHelper.initAds(this)
    }
}