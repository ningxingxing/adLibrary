package com.star.ad.adlibrary.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes
import com.star.ad.adlibrary.R
import com.star.ad.adlibrary.coroutine.launchIO
import com.star.ad.adlibrary.coroutine.launchMain
import com.star.ad.adlibrary.helper.RecommendHelper
import com.star.ad.adlibrary.model.ShowAdItem
import com.star.ad.adlibrary.thread.ScheduledExecutor
import com.star.ad.adlibrary.utils.Utils
import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * 广告view
 * @author nsc
 * @date 2026/3/13
 */
class ShowAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ShowAdView"
        private const val DURATION_VALUE = 400L
    }

    private var mValue = 1
    private var mMinValue = 0
    private var mMaxValue = Int.MAX_VALUE

    private lateinit var ivAdIcon: AppCompatImageView
    private var mAdUrl: String? = ""

    private val mList = mutableListOf<ShowAdItem>()
    private var mIndex = 0
    private var mDurationTime  = DURATION_VALUE
    private val mIconList =
        arrayOf(R.drawable.eq, R.drawable.music, R.drawable.photo, R.drawable.wallpaper_live, R.drawable.heart_theme_wallpaper)

    private val mHandler = Handler(Looper.getMainLooper())
    private val mUpdateRunnable = object : Runnable {
        override fun run() {
            updateAdDisplay()
            mHandler.postDelayed(this, 10000)
        }
    }

    private val mPackageNameList = arrayOf(
        R.string.eq_package_name,
        R.string.music_package_name,
        R.string.gallery_package_name,
        R.string.wallpaper_live_package_name,
        R.string.heart_theme_wallpaper_package_name
    )
    private val mTitleList =
        arrayOf(
            R.string.eq_name,
            R.string.music_name,
            R.string.photo_name,
            R.string.wallpaper_live_name,
            R.string.heart_theme_wallpaper_name
        )
    private val mUrlList =
        arrayOf(
            R.string.eq_url,
            R.string.music_url,
            R.string.photo_url,
            R.string.wallpaper_live_url,
            R.string.heart_theme_wallpaper_url
        )

    init {
        context.withStyledAttributes(attrs, R.styleable.ShowAdView) {
            mValue = getInt(R.styleable.ShowAdView_value, mValue)
            mMinValue = getInt(R.styleable.ShowAdView_minValue, mMinValue)
            mMaxValue = getInt(R.styleable.ShowAdView_maxValue, mMaxValue)
        }

        LayoutInflater.from(getContext()).inflate(R.layout.item_show_ad, this, true)
        init()
    }

    private fun init() = launchMain {
        ivAdIcon = findViewById(R.id.iv_ad_icon)
        ivAdIcon.setOnClickListener {
            context?.let { context ->
                if (Utils.isNetworkAvailable(context)) {
                    mAdUrl?.let {
                        RecommendHelper.openApp(context, it)
                    }
                }
            }

        }
        loadData()
        initAds()

        startUpdateTimer()
    }

    private fun startUpdateTimer() {
        mHandler.postDelayed(mUpdateRunnable, 5000)
    }

    private fun updateAdDisplay() {
        if (mList.isEmpty()) {
            return
        }
        mIndex++
        Log.d("ShowAdView", "updateAdDisplay index=$mIndex")
        if (mIndex >= mList.size) {
            mIndex = 0
        }
        val item = mList[mIndex]
        mAdUrl = item.url
        animateFlip(item.icon)
    }

    private fun animateFlip(iconResId: Int) {
        // 创建 Y 轴旋转动画
        val animX = ObjectAnimator.ofFloat(ivAdIcon, "rotationY", 0f, 90f)
        animX.duration = mDurationTime
        animX.interpolator = AccelerateDecelerateInterpolator()

        animX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // 动画中途切换图片
                ivAdIcon.setImageResource(iconResId)

                // 继续完成翻转
                val animBack = ObjectAnimator.ofFloat(ivAdIcon, "rotationY", -90f, 0f)
                animBack.duration = mDurationTime
                animBack.interpolator = AccelerateDecelerateInterpolator()
                animBack.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animX.start()
    }

    fun setDurationTime(durationTime: Long) {
        mDurationTime = durationTime
    }

    private fun initAds() {
        val random = Random()
        val number = random.nextInt(2)
        if (number == 1) {
            mAdUrl = resources.getString(R.string.eq_url)
            ivAdIcon.setImageResource(R.drawable.eq)
        } else {
            mAdUrl = resources.getString(R.string.heart_theme_wallpaper_url)
            ivAdIcon.setImageResource(R.drawable.heart_theme_wallpaper)
        }
    }

    private suspend fun loadData() = launchIO {
        mIconList.forEachIndexed { index, i ->
            val packageName = context.getString(mPackageNameList[index])
            val url = context.getString(mUrlList[index])
            val title = context.getString(mTitleList[index])

            val currentPackageName = context.packageName
            if (currentPackageName != packageName) {
                val item = ShowAdItem(i, title, url, packageName)
                mList.add(item)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 移除定时器，防止内存泄漏
        mHandler.removeCallbacks(mUpdateRunnable)
    }

}