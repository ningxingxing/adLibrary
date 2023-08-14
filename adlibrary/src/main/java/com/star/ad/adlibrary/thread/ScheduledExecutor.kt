package com.star.ad.adlibrary.thread

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 定时任务线程池
 *
 * @author liyizhi
 * @date 2021/6/30.
 */
object ScheduledExecutor : Executor {
    private val mThreadPool: ScheduledThreadPoolExecutor

    init {
        val corePoolSize: Int = Runtime.getRuntime().availableProcessors() + 1
        mThreadPool = ScheduledThreadPoolExecutor(
            corePoolSize,
            object : ThreadFactory {
                private val threadCount: AtomicInteger = AtomicInteger(0)
                override fun newThread(r: Runnable?): Thread {
                    if (threadCount.get() == Integer.MAX_VALUE) {
                        threadCount.set(0)
                    }
                    return Thread(r, "ScheduleTask#${threadCount.getAndIncrement()}")
                }
            }
        )
    }

    override fun execute(command: Runnable) {
        mThreadPool.schedule(command, 0, TimeUnit.NANOSECONDS)
    }

    /**
     * 延迟 delay 后执行一次
     */
    fun schedule(delay: Long, unit: TimeUnit, command: () -> Unit): ScheduledFuture<*> {
        return mThreadPool.schedule(command, delay, unit)
    }

    /**
     * 延迟 initialDelay 后执行一次，然后固定每隔 period 后重复执行（固定周期）
     */
    fun scheduleAtFixedRate(initialDelay: Long, period: Long, unit: TimeUnit, command: () -> Unit): ScheduledFuture<*> {
        return mThreadPool.scheduleAtFixedRate(command, initialDelay, period, unit)
    }

    /**
     * 延迟 initialDelay 后执行一次，然后等上个任务完成后，再每隔 period 后重复执行（每个任务时间不定，固定延迟）
     */
    fun scheduleWithFixedDelay(initialDelay: Long, period: Long, unit: TimeUnit, command: () -> Unit): ScheduledFuture<*> {
        return mThreadPool.scheduleWithFixedDelay(command, initialDelay, period, unit)
    }
}