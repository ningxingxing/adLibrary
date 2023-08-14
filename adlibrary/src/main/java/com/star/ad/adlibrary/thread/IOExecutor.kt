package com.star.ad.adlibrary.thread

import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * IO密集型线程池
 *
 * @author liyizhi
 * @date 2021/5/18.
 */
object IOExecutor : Executor {
    private const val TAG = "IOExecutor"
    private const val CORE_POOL_SIZE = 0
    private const val DEFAULT_MAXIMUM_POOL_SIZE: Int = 10
    private const val KEEP_ALIVE_TIME = 30L
    private const val BLOCKING_COEFFICIENT = 0.8
    private val mThreadPool: ThreadPoolExecutor

    init {
        var maximumPoolSize: Int = (Runtime.getRuntime().availableProcessors() / (1 - BLOCKING_COEFFICIENT)).toInt()
        if (maximumPoolSize < 0) {
            maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE
        }

        val threadFactory = object : ThreadFactory {
            private val threadCount: AtomicInteger = AtomicInteger(0)
            override fun newThread(r: Runnable?): Thread {
                if (threadCount.get() == Integer.MAX_VALUE) {
                    threadCount.set(0)
                }
                return Thread(r, "IOTask#${threadCount.getAndIncrement()}")
            }
        }

        val rejectedExecutionHandler = RejectedExecutionHandler { _, _ ->
            Log.e(TAG, "Exceeded ThreadPoolExecutor pool size")
        }

        mThreadPool = ThreadPoolExecutor(CORE_POOL_SIZE, maximumPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, SynchronousQueue(), threadFactory, rejectedExecutionHandler)
    }

    override fun execute(command: Runnable) {
        mThreadPool.execute(command)
    }
}