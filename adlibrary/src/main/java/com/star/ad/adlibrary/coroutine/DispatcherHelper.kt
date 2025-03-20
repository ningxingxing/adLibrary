package com.star.ad.adlibrary.coroutine


import com.star.ad.adlibrary.thread.DefaultExecutor
import com.star.ad.adlibrary.thread.MainExecutor
import com.star.ad.adlibrary.thread.IOExecutor
import java.util.concurrent.Executor
import kotlin.coroutines.*

/**
 * 协程的线程调度类
 *
 * @author ningshengcai
 * @date 2024/7/17.
 */
class DispatcherHelper {
    companion object {
        val Unconfined: Executor? = null
        val Main: Executor = MainExecutor
        val IO: Executor = IOExecutor
        val Default: Executor = DefaultExecutor
    }

    /**
     * 参数上下文，用于传递线程类型
     */
    class ExecutorContext(val threadType: Executor?) : AbstractCoroutineContextElement(Key) {
        companion object Key : CoroutineContext.Key<ExecutorContext>
    }

    /**
     * 拦截器上下文，用于线程调度
     */
    class DispatchContext : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
        override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
            return object : Continuation<T> {

                override val context: CoroutineContext = continuation.context
                override fun resumeWith(result: Result<T>) {

                    val executorContext = context[ExecutorContext]
                    if (executorContext == null || executorContext.threadType !is Executor) {
                        continuation.resumeWith(result)
                        return
                    }

                    executorContext.threadType.execute {
                        continuation.resumeWith(result)
                    }
                }
            }
        }
    }
}
