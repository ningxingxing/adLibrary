package com.star.ad.adlibrary.interfaces

import com.google.android.gms.ads.AdError

interface OnShowAdCompleteListener {
    fun onShowAdComplete()

    fun onShowAdError(adError: AdError)

    fun onAdShowedFullScreenContent()
}