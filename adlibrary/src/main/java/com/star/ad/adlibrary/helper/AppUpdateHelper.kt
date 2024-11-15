package com.star.ad.adlibrary.helper

import android.content.Context
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.interfaces.IAppUpdateHelper

/**
 * @author nsc
 * 谷歌更新应用
 */
class AppUpdateHelper(
    val context: Context,
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
) {

    companion object {
        private const val TAG = "AppUpdateHelper"
    }

    private var mAppUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var iInstallStateUpdatedListener: InstallStateUpdatedListener? = null

    /**
     * 更新
     */
    fun update(listener: IAppUpdateHelper) {

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = mAppUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                startUpdate(appUpdateInfo)
            }
            listener.onUpdateState(appUpdateInfo.updateAvailability())
        }
    }

    /**
     * 开始更新
     */
    fun startUpdate(appUpdateInfo: AppUpdateInfo) {
        mAppUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // an activity result launcher registered via registerForActivityResult
            activityResultLauncher,
            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
            // flexible updates.
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                .setAllowAssetPackDeletion(true)//指定是否允许更新操作在设备存储空间有限的情况下清除资源包
                .build()
        )
    }

    /**
     * 添加更新进度
     */
    fun listenerProgress(listener: IAppUpdateHelper): InstallStateUpdatedListener {
        iInstallStateUpdatedListener = InstallStateUpdatedListener { state ->
            // (Optional) Provide a download progress bar.
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytesToDownload = state.totalBytesToDownload()
                // Show update progress bar.
                listener.onUpdateProgress(bytesDownloaded, totalBytesToDownload)
                Log.i(
                    TAG,
                    "listenerProgress bytesDownloaded=$bytesDownloaded  totalBytesToDownload=$totalBytesToDownload"
                )
            } else if (state.installStatus == InstallStatus.DOWNLOADED) {//下载完成
                listener.onDownloadFinish()
            }
            // Log state or install the update.
        }
        mAppUpdateManager.registerListener(iInstallStateUpdatedListener!!)
        return iInstallStateUpdatedListener!!
    }

    /**
     * 检查是否有应用下载完成
     * 在onResume中执行
     */
    fun checkAppDownload(listener: IAppUpdateHelper) {
        mAppUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    listener.onCheckDownload(true)
                    return@addOnSuccessListener
                }
                listener.onCheckDownload(false)
            }
    }

    /**
     * 更新完成
     */
    fun updateComplete() {
        mAppUpdateManager.completeUpdate()
    }

    fun getAppUpdateManager(): AppUpdateManager {
        return mAppUpdateManager
    }

    /**
     * 更新停止，执行继续更新
     * 在onResume执行
     */
    fun immediatelyUpdate() {
        mAppUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    startUpdate(appUpdateInfo)
                }
            }
    }

    /**
     * 注册下载进度
     */
    fun registerUpdateListener() {
        iInstallStateUpdatedListener?.let {
            mAppUpdateManager.registerListener(it)
        }
    }

    /**
     * 取消下载进度
     */
    fun unregisterUpdateListener() {
        iInstallStateUpdatedListener?.let {
            mAppUpdateManager.unregisterListener(it)
        }
    }

    /**
     * 显示下载完去重新打开
     */
    fun popupSnackbarForCompleteUpdate(
        view: View,
        text: String,
        iconText: String,
        textColor: Int = R.color.white
    ) {
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(iconText) { mAppUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(context, textColor))
            show()
        }
    }

}