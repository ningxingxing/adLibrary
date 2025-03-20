package com.star.ad.adlibrary.coroutine

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.*

/**
 * 简单程协语法的封装使用
 *
 * @author ningshengcai
 * @date 2024/7/17.
 */
private const val TAG: String = "CoroutineUtils"

fun <T> coroutine(executor: Executor = DispatcherHelper.Main, block: suspend () -> T) {
    block.startCoroutine(object : Continuation<T> {
        override val context: CoroutineContext = DispatcherHelper.DispatchContext() + DispatcherHelper.ExecutorContext(
            executor
        )
        override fun resumeWith(result: Result<T>) {
        }
    })
}

fun launchMain(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch(block = block)
}

/**
 * 处理IO的协程挂起函数
 */
suspend fun <T> launchIO(block: () -> T) = suspendCancellableCoroutine { continuation ->
    DispatcherHelper.IO.execute {
        try {
            val result = block()
            continuation.resume(result)
        } catch (e: Exception) {
            Log.e(TAG, "suspend launchIO command error: $e")
            continuation.resumeWith(Result.failure(e))
        }
    }
}

/**
 * 处理Default的协程挂起函数
 */
suspend fun <T> launchDefault(block: () -> T) = suspendCancellableCoroutine { continuation ->
    DispatcherHelper.Default.execute {
        try {
            val result = block()
            continuation.resume(result)
        } catch (e: Exception) {
            Log.e(TAG, "suspend launchDefault command error: $e")
            continuation.resumeWith(Result.failure(e))
        }
    }
}