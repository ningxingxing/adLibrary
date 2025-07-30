package com.star.ad.adlibrary.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object WindowInsetsUtils {
    const val SET_WINDOW_TOP_ZERO = 0 //设置了沉浸式状态栏
    const val SET_WINDOW_DEFAULT = 1

    /**
     * 适配状态栏 android 15之后必须适配
     * @param view
     * 如果顶部返回标题部分代码使用ConstraintLayout嵌套一层，而且自适应高度就不需要二外设置状态栏高度
     * 否则需要设置top为SET_WINDOW_DEFAULT，即设置systemBarInsets.top，所以写界面的时候，如果不想在activity
     * 的onCreate()方法中传入setOnApplyWindowInsetsListener，需要顶部代码嵌套一层ConstraintLayout
     */
    @JvmStatic
    fun setOnApplyWindowInsetsListener(view: View, top: Int = SET_WINDOW_TOP_ZERO) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            // Get the insets for system bars
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the system window insets as padding to the view
            if (top == SET_WINDOW_TOP_ZERO) {
                v.setPadding(
                    systemBarInsets.left,
                    0,
                    systemBarInsets.right,
                    systemBarInsets.bottom
                )
            } else {
                v.setPadding(
                    systemBarInsets.left,
                    systemBarInsets.top,
                    systemBarInsets.right,
                    systemBarInsets.bottom
                )
            }
            insets
        }
    }
}