package com.star.ad.adlibrary.helper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.star.ad.adlibrary.constants.RESULT_SUCCESS
import com.star.ad.adlibrary.interfaces.ICoreListener
import kotlinx.coroutines.flow.flow
import androidx.core.net.toUri
import com.google.android.play.core.review.testing.FakeReviewManager

class AppCoreHelper(context: Context) {

    companion object {
        private const val TAG = "AppCoreHelper"

        fun openPlayStoreForRating(context: Context) {
            try {
                // 尝试通过Intent直接打开应用页面
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = "market://details?id=${context.packageName}".toUri()
                    setPackage("com.android.vending")
                })
            } catch (e: ActivityNotFoundException) {
                // 如果Play商店不存在，打开网页版
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data =
                        "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                })
            }
        }

    }

    //private val manager = ReviewManagerFactory.create(context)
   //测试使用
    private val manager = FakeReviewManager(context)
    private var mReviewInfo: ReviewInfo? = null

    private fun requestReview(activity: Activity, listener: ICoreListener) {
        val request = manager.requestReviewFlow();
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.getResult();
                val flow = manager.launchReviewFlow(activity, reviewInfo);
                flow.addOnCompleteListener({
                    mReviewInfo = task.result
                    Log.i(TAG, "requestCore reviewInfo=$mReviewInfo")
                    listener.onCoreRequestResult(true, RESULT_SUCCESS)
                }
                )
            } else {
                // 处理错误
                val reviewErrorCode = (task.exception as ReviewException).errorCode
                listener.onCoreRequestResult(false, reviewErrorCode)
            }
        }
    }

    /**
     * 请求打开评分
     */
    fun requestCore(listener: ICoreListener) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                mReviewInfo = task.result
                Log.i(TAG, "requestCore reviewInfo=$mReviewInfo")
                listener.onCoreRequestResult(true, RESULT_SUCCESS)
            } else {
                // There was some problem, log or handle the error code.
                @ReviewErrorCode
                val reviewErrorCode = (task.exception as ReviewException).errorCode
                Log.i(TAG, "requestCore reviewErrorCode=$reviewErrorCode")
                listener.onCoreRequestResult(false, reviewErrorCode)
            }
        }
    }

    fun launchCore(activity: Activity, listener: ICoreListener) {
        if (mReviewInfo == null) {
            Log.i(TAG, "launchCore mReviewInfo is null")
        }
        val flow = manager.launchReviewFlow(activity, mReviewInfo!!)
        flow.addOnCompleteListener { _ ->
            listener.onCoreRequestResult(true, flow.hashCode())
        }
    }

    /**
     * 检查是否已经评分完成
     */
    fun checkCore(activity: Activity) {
        if (mReviewInfo == null) {
            Log.e(TAG, "checkCore mReviewInfo is null")
            return
        }
        val flow = manager.launchReviewFlow(activity, mReviewInfo!!)
        flow.addOnCompleteListener { _ ->
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }


}