package com.star.ad.adlibrary.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.star.ad.adlibrary.R

class RatingHelper(private val context: Context, private val preferences: SharedPreferences) {

    companion object {
        private const val PREF_LAUNCH_COUNT = "launch_count"
        private const val PREF_FIRST_LAUNCH = "first_launch"
        private const val PREF_DONT_SHOW = "dont_show"
        private const val LAUNCHES_UNTIL_PROMPT = 3
    }

    fun appLaunched() {
        if (preferences.getBoolean(PREF_DONT_SHOW, false)) {
            return
        }

        val launchCount = preferences.getInt(PREF_LAUNCH_COUNT, 0) + 1
        preferences.edit { putInt(PREF_LAUNCH_COUNT, launchCount) }

        val firstLaunch = preferences.getLong(PREF_FIRST_LAUNCH, 0)
        if (firstLaunch == 0L) {
            preferences.edit { putLong(PREF_FIRST_LAUNCH, System.currentTimeMillis()) }
        }
    }

    fun shouldShowRatingDialog(): Boolean {
        if (preferences.getBoolean(PREF_DONT_SHOW, false)) {
            return false
        }

        val launchCount = preferences.getInt(PREF_LAUNCH_COUNT, 0)
        val firstLaunch = preferences.getLong(PREF_FIRST_LAUNCH, 0)

        // 至少启动3次且使用超过一周
        return launchCount >= LAUNCHES_UNTIL_PROMPT &&
                System.currentTimeMillis() >= firstLaunch + 7 * 24 * 60 * 60 * 1000
    }

    fun showRatingDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.core_us))
            .setMessage(context.getString(R.string.core_us_content))
            .setPositiveButton(context.getString(R.string.core_evaluate)) { _, _ ->
                // 使用In-App Review或跳转到Play商店
                requestReviewOrOpenStore(activity)
            }
            .setNeutralButton(context.getString(R.string.core_review_later)) { _, _ ->
                preferences.edit {
                    putBoolean(PREF_DONT_SHOW, true) }
            }
            .show()
    }

    private fun requestReviewOrOpenStore(context: Context) {
        AppCoreHelper.openPlayStoreForRating(context)
    }

    private fun isInAppReviewAvailable(): Boolean {
        // 检查设备是否支持In-App Review
        // 实际实现可能需要更复杂的检查
        return true
    }
}