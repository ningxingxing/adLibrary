package com.star.ad.adlibrary.thread

import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * CPU密集型线程池
 *
 * @author liyizhi
 * @date 2021/6/28.
 */
object DefaultExecutor : Executor {
    private const val TAG = "DefaultExecutor"
    private val mThreadPool: ThreadPoolExecutor

    init {
        val corePoolSize: Int = Runtime.getRuntime().availableProcessors() + 1

        val threadFactory = object : ThreadFactory {
            private val threadCount: AtomicInteger = AtomicInteger(0)
            override fun newThread(r: Runnable?): Thread {
                if (threadCount.get() == Integer.MAX_VALUE) {
                    threadCount.set(0)
                }
                return Thread(r, "DefaultTask#${threadCount.getAndIncrement()}")
            }
        }

        val rejectedExecutionHandler = RejectedExecutionHandler { _, _ ->
            Log.e(TAG, "Exceeded ThreadPoolExecutor pool size")
        }

        mThreadPool = ThreadPoolExecutor(corePoolSize, corePoolSize, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue(), threadFactory, rejectedExecutionHandler)
    }

    override fun execute(command: Runnable) {
        mThreadPool.execute(command)
    }
}