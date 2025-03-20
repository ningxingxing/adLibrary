package com.star.ad.adlibrary.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * 主线程执行
 *
 * @author ningshengcai
 * @date 2024/7/17.
 */
object MainExecutor : Executor {
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mHandler.post(command)
    }
}