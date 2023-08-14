package com.star.ad.adlibrary.utils

import android.content.Context
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.utils.Utils.getCurrentPackageName

object AdsUtils {


    /**
     * AD_UNIT_ID
     */
    fun getBannerAdId(context: Context): String {
        val resources = context.resources
        when (getCurrentPackageName(context)) {

            //eq
            resources.getString(R.string.eq_package_name) -> {
                return resources.getString(R.string.eq_unit_id)
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
    fun getInterstitialId(context: Context): String {
        val resources = context.resources

        when (getCurrentPackageName(context)) {
            //eq
            resources.getString(R.string.eq_package_name) -> {
                return resources.getString(R.string.eq_interstitial_id)
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
    fun getNativeId(context: Context): String {
        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_native_id)
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
    fun getRewardId(context: Context): String {
        when (getCurrentPackageName(context)) {
            context.getString(R.string.eq_package_name) -> {
                return context.getString(R.string.eq_rewarded_id)
            }

            context.getString(R.string.demo_package_name) -> {
                return context.getString(R.string.demo_rewarded_id)
            }
        }
        return ""
    }

}