package com.star.ad.adlibrary.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Process


object Utils {

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取当前应用包名
     */
    fun getCurrentPackageName(context: Context): String {
        val processInfos =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses
        val myPid = Process.myPid()
        if (processInfos == null) {
            return ""
        }
        for (info in processInfos) {
            if (info.pid == myPid) {
                return info.processName
                break
            }
        }
        return ""
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }

        val manager = context
            .applicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager?

        if (manager == null) {
            return false
        }
        val networkinfo = manager.getActiveNetworkInfo()

        return !(networkinfo == null || !networkinfo.isAvailable())
    }
}