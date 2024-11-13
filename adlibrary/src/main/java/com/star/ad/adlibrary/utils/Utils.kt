package com.star.ad.adlibrary.utils

import android.app.ActivityManager
import android.content.Context
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

}