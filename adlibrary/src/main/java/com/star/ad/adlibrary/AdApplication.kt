package com.star.ad.adlibrary

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds
import com.star.ad.adlibrary.coroutine.launchDefault
import com.star.ad.adlibrary.coroutine.launchIO
import com.star.ad.adlibrary.coroutine.launchMain
import com.star.ad.adlibrary.manager.AppOpenAdManager

open class AdApplication : Application(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    companion object {
        const val TAG = "AdApplication"
    }


    private var currentActivity: Activity? = null


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

//        launchMain {
//            initAds()
//        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    private suspend fun initAds() = launchIO {
        MobileAds.initialize(this@AdApplication) {
            Log.i(TAG,"initAds success")
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        Log.e(TAG, "onMoveToForeground ")

        // currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }


}