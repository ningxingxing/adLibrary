package com.star.ad.adlibrary.utils

import android.content.Context
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.utils.Utils.getCurrentPackageName

object AdsUtils {


    /**
     * AD_UNIT_ID
     */
    fun getBannerAdId(context: Context, isDebug: Boolean = false): String {
        val resources = context.resources
        if (isDebug) {
            return resources.getString(R.string.demo_unit_id)
        }

        when (getCurrentPackageName(context)) {

            //eq
            resources.getString(R.string.eq_package_name) -> {
                return resources.getString(R.string.eq_ad_banner_id)
            }
            //gallery
            resources.getString(R.string.gallery_package_name)->{
                return resources.getString(R.string.gallery_ad_banner_id)
            }
            //music
            resources.getString(R.string.music_package_name) ->{
                return resources.getString(R.string.music_ad_banner_id)
            }
            //demo
            resources.getString(R.string.demo_package_name) -> {
                return resources.getString(R.string.demo_unit_id)
            }

        }
        return ""
    }

    /**
     * AD_INTERSTITIAL
     */
    fun getInterstitialId(context: Context, isDebug: Boolean = false): String {
        val resources = context.resources
        if (isDebug) {
            return resources.getString(R.string.demo_interstitial_id)
        }

        when (getCurrentPackageName(context)) {
            //eq
            resources.getString(R.string.eq_package_name) -> {
                return resources.getString(R.string.eq_ad_interstitial_id)
            }
            //gallery
            resources.getString(R.string.gallery_package_name)->{
                return resources.getString(R.string.gallery_ad_interstitial_id)
            }
            //music
            resources.getString(R.string.music_package_name)->{
                return resources.getString(R.string.music_ad_interstitial_id)
            }

            //demo
            resources.getString(R.string.demo_package_name) -> {
                return resources.getString(R.string.demo_interstitial_id)
            }

        }
        return ""
    }

    /**
     * native id
     */
    fun getNativeId(context: Context, isDebug: Boolean = false): String {
        if (isDebug) {
            return context.getString(R.string.demo_native_id)
        }

        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_ad_native_id)
            }

            context.getString(R.string.gallery_package_name)->{
                return context.getString(R.string.gallery_ad_native_id)
            }

            context.getString(R.string.music_package_name)->{
                return context.getString(R.string.music_ad_native_id)
            }

            context.getString(R.string.demo_package_name) -> {
                return context.getString(R.string.demo_native_id)
            }
        }
        return ""
    }

    /**
     * 获取激励广告id
     */
    fun getRewardId(context: Context, isDebug: Boolean = false): String {
        if (isDebug) {
            return context.getString(R.string.demo_rewarded_id)
        }

        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_ad_rewarded_id)
            }

            context.getString(R.string.gallery_package_name)->{
                return context.getString(R.string.gallery_ad_rewarded_id)
            }

            context.getString(R.string.music_package_name)->{
                return context.getString(R.string.music_ad_rewarded_id)
            }

            context.getString(R.string.demo_package_name) -> {
                return context.getString(R.string.demo_rewarded_id)
            }
        }
        return ""
    }

    /**
     * 获取激励广告id
     */
    fun getRewardInterstitialId(context: Context, isDebug: Boolean = false): String {
        if (isDebug) {
            return context.getString(R.string.demo_rewarded_id)
        }

        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_ad_rewarded_interstitial_id)
            }

            context.getString(R.string.gallery_package_name)->{
                return context.getString(R.string.gallery_ad_rewarded_interstitial_id)
            }

            context.getString(R.string.music_package_name)->{
                return context.getString(R.string.music_ad_rewarded_interstitial_id)
            }

            context.getString(R.string.demo_package_name) -> {
                return context.getString(R.string.demo_rewarded_id)
            }
        }
        return ""
    }


    /**
     * 获取开屏id
     */
    fun getAdAppOpenId(context: Context, isDebug: Boolean = false): String {
        if (isDebug) {
            return context.getString(R.string.demo_unit_id)
        }

        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_ad_app_open_id)
            }

            context.getString(R.string.gallery_package_name)->{
                return context.getString(R.string.gallery_ad_app_open_id)
            }

            context.getString(R.string.music_package_name)->{
                return context.getString(R.string.music_ad_app_open_id)
            }

            context.getString(R.string.demo_package_name) -> {
                return context.getString(R.string.demo_unit_id)
            }
        }
        return ""
    }

}