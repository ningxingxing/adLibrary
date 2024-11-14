package com.star.ad.adlibrary.helper

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.star.ad.adlibrary.constants.RESULT_SUCCESS
import com.star.ad.adlibrary.interfaces.ICoreListener

class AppCoreHelper(context: Context) {

    companion object {
        private const val TAG = "AppCoreHelper"

    }

    private val manager = ReviewManagerFactory.create(context)
    private var mReviewInfo: ReviewInfo? = null

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