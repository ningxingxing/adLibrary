package com.star.ad.adlibrary.thread

import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object GlobalExecutor : Executor {

    private const val CORE_POOL_SIZE = 1
    private const val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE
    private const val KEEP_ALIVE_TIME = 0L

    private val mSingleThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue()
    ) { r: Runnable? ->
        Thread(r, "global-pool")
    }

    override fun execute(command: Runnable) {
        mSingleThreadPool.execute(command)
    }
}